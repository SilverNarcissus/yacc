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
     * 基本产生式列表
     */
    private ArrayList<String> productions;
    /**
     * 基本tokens集合
     */
    private Set<String> tokens;
    /**
     * 状态列表
     */
    private ArrayList<State> states;

    public Analyzer() {
        productions = new ArrayList<>();
        tokens = new HashSet<>();
        states = new ArrayList<>();

        ReadHelper.readFile(DEFINE_FILE_PATH, tokens, productions);
    }

    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer();
        analyzer.parse();
    }

    /**
     * 分析.y文件，生成.t文件
     */
    public void parse() {
        buildDFA();
    }

    /**
     * 生成DFA
     */
    private void buildDFA() {
        String[] firstPart = productions.get(0).split(":");
        Set<String> fistPredictions = new HashSet<>();
        fistPredictions.add("$");
        Production first = new Production(firstPart[0], firstPart[1], 0, fistPredictions);
        Set<Production> beginSet = new HashSet<>();
        beginSet.add(first);

        State zero = new State(states.size(), beginSet, new HashMap<>());

        findClosure(zero);

        System.out.println(zero.productionSet);
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
                            if(core.productionSet.add(newProduction)){
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
                    if (part[0].equals(cur)) {
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
