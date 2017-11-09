package yacc;

import java.util.*;

/**
 * yacc分析器主类
 */
public class Analyzer {
    /**
     * .y 文件路径
     */
    private static final String DEFINE_FILE_PATH = "my.y";
    /**
     * .t 文件路径
     */
    private static final String TABLE_FILE_PATH = "my.t";
    /**
     * 规约动作文件路径
     */
    private static final String ACTIONS_FILE_PATH = "src/monitor/Functions.java";
    /**
     * 基本产生式列表
     */
    private ArrayList<String> productions;
    /**
     * 基本tokens集合
     */
    private List<String> tokens;

    /**
     * 非终结符序列
     */
    private List<String> nonTerminalSymbols;

    /**
     * 状态列表
     */
    private ArrayList<State> states;

    /**
     * 所有转换边的集合
     */
    private Set<Side> sides;

    /**
     * 所有删除掉的状态编号的集合
     */
    private List<Integer> deletedStates;

    /**
     * 所有归约动作
     */
    private List<String> actions;

    public Analyzer() {
        productions = new ArrayList<>();
        tokens = new ArrayList<>();
        states = new ArrayList<>();
        nonTerminalSymbols = new ArrayList<>();
        sides = new HashSet<>();
        deletedStates = new ArrayList<>();
        actions = new ArrayList<>();

        IOHelper.readFile(DEFINE_FILE_PATH, tokens, productions, actions);
        tokens.add("$");
        //将产生式左部加入token序列
        Set<String> left = new HashSet<>();
        for (String production : productions) {
            left.add(production.split(":")[0]);
        }
        left.remove("S");

        nonTerminalSymbols.addAll(left);
    }

    /**
     * 分析.y文件，生成.t文件
     */
    public void parse() {
        buildDFA();
        optimizeDFA();
        generateTable();
        IOHelper.buildActionsFile(ACTIONS_FILE_PATH, actions);
    }

    /**
     * 最小化生成的DFA的状态
     */
    private void optimizeDFA() {
        //判断是否同心，判断是否有交集规约式，若同心且无交集规约式，则合并状态
        for (int i = 0; i < states.size(); i++) {
            if (deletedStates.contains(i)) {
                continue;
            }

            HashMap<Integer, Side> cols = new HashMap<>();
            for (Side side : sides) {
                if (side.row == i) {
                    cols.put(side.col, side);
                }
            }
            for (int j = i + 1; j < states.size(); j++) {
                if (deletedStates.contains(j) || !states.get(i).hasSameCore(states.get(j))) {
                    continue;
                }

                boolean isSame = true;
                for (Side side : sides) {
                    if (side.row == j && cols.containsKey(side.col)) {
                        Side before = cols.get(side.col);
                        if (before.to != side.to || before.transition != side.transition) {
                            isSame = false;
                            break;
                        }
                    }
                }

                if (isSame) {
                    for (Side side : sides) {
                        //1 将第二个状态中第一个状态没有的符号放入第一个状态
                        if (side.row == j && !cols.containsKey(side.col)) {
                            side.row = i;
                        }
                        //2 将指向第二个状态的边指向第一个状态
                        if (side.to == j) {
                            side.to = i;
                        }
                    }
                    deletedStates.add(j);
                }
            }
        }

        Collections.sort(deletedStates);

        HashMap<Integer, Integer> idMap = new HashMap<>();
        int loc = 0;
        for (int i = 0; i < states.size(); i++) {
            if (i == deletedStates.get(loc)) {
                loc++;
                continue;
            }
            idMap.put(i, i - loc);
        }

        for (Side side : sides) {
            if (deletedStates.contains(side.row)) {
                side.row = -1;
                continue;
            }
            side.row = idMap.get(side.row);
            side.to = idMap.get(side.to);
        }

    }

    /**
     * 生成转换表并产生.t文件
     */
    private void generateTable() {
        int n = states.size() - deletedStates.size();
        int m = tokens.size() + nonTerminalSymbols.size();
        int[][] table = new int[n][m];
        char[][] type = new char[n][m];

        for (Side side : sides) {
            if (side.row == -1) {
                continue;
            }
            table[side.row][side.col] = side.to;
            type[side.row][side.col] = side.transition;
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (type[i][j] == '\0') {
                    type[i][j] = 'e';
                }
            }
        }

        IOHelper.writeFile(TABLE_FILE_PATH, table, type, tokens, nonTerminalSymbols, productions);
    }

    /**
     * 生成DFA
     */
    private void buildDFA() {
        //初始化一些变量
        String[] firstPart = productions.get(0).split(":");
        Set<String> fistPredictions = new HashSet<>();
        fistPredictions.add("$");
        Production first = new Production(firstPart[0], firstPart[1], 0, fistPredictions);
        Set<Production> beginSet = new HashSet<>();
        beginSet.add(first);

        //初始化状态0
        State zero = new State(states.size(), beginSet);
        findClosure(zero);
        states.add(zero);
        //

        //开始循环找到发出边
        Queue<State> before = new LinkedList<>();
        before.add(zero);
        while (!before.isEmpty()) {
            State cur = before.poll();
            HashMap<String, State> reachMap = new HashMap<>();
            //对每个产生式进行扫描，寻找出发到达的状态集合及内部可归约产生式
            for (Production production : cur.productionSet) {
                if (production.canReduce()) {
                    if (handleReducible(cur.id, production)) return;
                } else {
                    putReachMap(reachMap, production.next(), production.shift());
                }
            }
            //合并重复状态，产生转换边
            if (generateNewStateAndAddSide(cur.id, reachMap, before)) return;
        }
    }

    /**
     * 合并重复状态，产生新状态，产生转换边
     *
     * @param cur      当前状态号
     * @param reachMap 可达状态集合
     * @return 处理过程中是否出错
     */
    private boolean generateNewStateAndAddSide(int cur, HashMap<String, State> reachMap, Queue<State> before) {
        for (String type : reachMap.keySet()) {
            State candidate = reachMap.get(type);
            findClosure(candidate);
            //确定出发边的类型
            char transition = 's';
            int col = tokens.indexOf(type);
            if (col == -1) {
                col = tokens.size() + nonTerminalSymbols.indexOf(type);
                transition = 'g';
            }
            //

            //查看是否是相同状态
            int stateId = states.indexOf(candidate);
            if (stateId == -1) {
                candidate.id = states.size();
                Side newSide = new Side(cur, col, transition, candidate.id);
                if (!sides.add(newSide)) {
                    System.err.println("has same side!");
                    return true;
                }
                states.add(candidate);
                before.add(candidate);
            } else {
                Side newSide = new Side(cur, col, transition, stateId);
                if (!sides.add(newSide)) {
                    System.err.println("has same side!");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 处理可归约式
     *
     * @param cur        当前状态编号
     * @param production 可归约式
     * @return 处理过程中是否出错
     */
    private boolean handleReducible(int cur, Production production) {
        int idOfProduction = productions.indexOf(production.left + ":" + production.right);
        for (String terminal : production.prediction) {
            Side newSide = new Side(cur, tokens.indexOf(terminal), 'r', idOfProduction);
            if (!sides.add(newSide)) {
                System.err.println("has same side!");
                return true;
            }
        }
        return false;
    }

    /**
     * 向可达图中放入一个新的产生式的辅助方法
     *
     * @param reachMap   可达图
     * @param side       边
     * @param production 新产生式
     */
    private void putReachMap(HashMap<String, State> reachMap, String side, Production production) {
        if (reachMap.containsKey(side)) {
            reachMap.get(side).productionSet.add(production);
        } else {
            Set<Production> productions = new HashSet<>();
            productions.add(production);
            State state = new State(0, productions);
            reachMap.put(side, state);
        }
    }

    /**
     * 寻找闭包
     *
     * @param core 核心状态
     */
    private void findClosure(State core) {
        Set<Production> newElement = new HashSet<>(core.productionSet);
        Set<Production> temp = new HashSet<>();

        boolean haveMore = true;
        while (haveMore) {
            haveMore = false;
            temp.clear();
            for (Production before : newElement) {
                if (before.nextIsNonTerminal()) {
                    String cur = before.next();
                    Set<String> newPrediction = new HashSet<>();
                    //System.out.println(before.after());
                    if (first(before.after(), newPrediction)) {
                        newPrediction.addAll(before.prediction);
                    }

                    for (String production : productions) {
                        String[] part = production.split(":");
                        if (part[0].equals(cur)) {
                            Production newProduction = new Production(part[0], part[1], 0, newPrediction);
                            if (core.productionSet.add(newProduction)) {
                                haveMore = true;
                                temp.add(newProduction);
                            }
                        }
                    }
                }
            }
            newElement.clear();
            newElement.addAll(temp);
        }
    }


    /**
     * 寻找第一个终结符
     *
     * @param sequence 需要寻找的序列
     * @param result   寻找到的终结符集合
     * @return 是否含有空产生式
     */
    private boolean first(String sequence, Set<String> result) {
        int start = 0;
        int end = 0;

        while (start < sequence.length()) {
            end = getEnd(sequence, end);
            String cur = sequence.substring(start, end);
            if (cur.startsWith("{")) {
                cur = cur.substring(1, cur.length() - 1);
            }

            if (tokens.contains(cur)) {
                //处理终结符
                result.add(cur);
                return false;
            } else {
                //处理非终结符
                Set<String> subSet = new HashSet<>();
                boolean hasEmptyProductions = false;
                for (String production : productions) {
                    String[] part = production.split(":");
                    //若是A:->Aα 则不递归
                    if (part[0].equals(cur) && !part[1].substring(0, 1).equals(cur)) {
                        //System.out.println(part[1]);
                        hasEmptyProductions = hasEmptyProductions | first(part[1], subSet);
                    }
                }
                result.addAll(subSet);
                if (!hasEmptyProductions) {
                    return false;
                }
            }
            start = end;
        }

        return true;
    }

    /**
     * 得到一段序列的终止位置（处理{}）
     *
     * @param sequence 序列
     * @param end      起始终止位置
     * @return 最终终止位置
     */
    private int getEnd(String sequence, int end) {
        if (sequence.charAt(end) == '{') {
            while (sequence.charAt(end) != '}') {
                end++;
            }
        }
        end++;
        return end;
    }
}
