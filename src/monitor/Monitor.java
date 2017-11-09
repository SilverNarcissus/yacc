package monitor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 由表驱动的syntax parser
 */
public class Monitor {
    /**
     * input 文件路径
     */
    private static final String INPUT_FILE_PATH = "input.txt";
    /**
     * .t 文件路径
     */
    private static final String TABLE_FILE_PATH = "my.t";
    /**
     * 转换表
     */
    private int[][] table;
    /**
     * 转换类型表
     */
    private char[][] type;
    /**
     * 符号表
     * key——符号
     * value——符号对应数组中的位置
     */
    private HashMap<String, Integer> symbolMap;
    /**
     * 产生式左部列表
     */
    private ArrayList<String> productionLeft;
    /**
     * 产生式右部列表
     */
    private ArrayList<String> productionRight;
    /**
     * 要处理的输入
     */
    private String input;

    /**
     * 输入的token序列
     */
    private String[] tokens;

    /**
     * 归约动作类
     */
    private Functions functions;

    /**
     * 归约语法栈
     */
    private Stack<String> semanticStack;

    public Monitor() {
        symbolMap = new HashMap<>();
        productionLeft = new ArrayList<>();
        productionRight = new ArrayList<>();
        functions = new Functions();
        semanticStack = new Stack<>();

        tokens = IOHelper.readInput(INPUT_FILE_PATH).split(">");

        String[][] cache;
        cache = IOHelper.readTable(TABLE_FILE_PATH, productionLeft, productionRight, symbolMap);
        //将开始符放入符号表
        symbolMap.put("S", 0);

        int n = cache.length;
        int m = cache[0].length;
        table = new int[n][m];
        type = new char[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                type[i][j] = cache[i][j].charAt(0);
                table[i][j] = Integer.parseInt(cache[i][j].substring(1));
            }
        }

        //System.out.println(symbolMap);
    }

    /**
     * 将input解释为规约序列
     */
    public List<String> parse() {
        List<String> result = new LinkedList<>();
        Stack<Integer> states = new Stack<>();
        Stack<String> symbols = new Stack<>();
        states.push(0);
        symbols.push("$");

        for (int i = 0; i < tokens.length; i++) {
            int state = states.peek();
            String cur;
            String number;

            cur = tokens[i].split(",")[0].substring(1);
            number = tokens[i].split(",")[1];
            //如果该符号是操作符，则赋予其具体的操作符
            if (cur.equals("OPERATOR")) {
                cur = number;
            }

            if (!symbolMap.containsKey(cur)) {
                handelError(i, "can't find symbol");
                return null;
            }
            int col = symbolMap.get(cur);
            switch (type[state][col]) {
                case 'e':
                    handelError(i, "no transition in table");
                    return null;
                case 's':
                    states.push(table[state][col]);
                    symbols.push(cur);
                    semanticStack.push(number);
                    break;
                case 'r':
                    handleReduction(i, table[state][col], states, symbols, result);
                    i--;
                    break;
                case 'a':
                    handleReduction(i, 0, states, symbols, result);
                    result.add("The result of this expression is :" + semanticStack.pop());
                    return result;
                default:
                    System.err.println("error in table!");
                    return null;
            }
        }

        handelError(tokens.length - 1, "can't match (incomplete sentence)");
        return result;
    }

    /**
     * 处理归约情况
     *
     * @param loc     当前处理位置
     * @param index   规约式位置
     * @param states  状态栈
     * @param symbols 符号栈
     * @param result  规约式的存放列表
     */
    private void handleReduction(int loc, int index, Stack<Integer> states, Stack<String> symbols, List<String> result) {
        String right = productionRight.get(index);
        for (int i = right.length() - 1; i >= 0; i--) {
            String symbol;
            if (right.charAt(i) == '}') {
                int end = i;
                while (right.charAt(i) != '{') {
                    i--;
                }
                symbol = right.substring(i + 1, end);
            } else {
                symbol = right.substring(i, i + 1);
            }

            if (symbols.isEmpty()) {
                handelError(loc, "can't match! 153");
                System.exit(-1);
            }
            //System.out.println(symbol);
            if (!symbols.pop().equals(symbol)) {
                handelError(loc, "can't match! 157");
                System.exit(-1);
            }

            //System.out.println(states.size() + " " + symbols.size());
            states.pop();
        }

        String reductionResult = productionLeft.get(index);
        states.push(table[states.peek()][symbolMap.get(reductionResult)]);
        symbols.push(reductionResult);

        try {
            Functions.class.getDeclaredMethod("function" + index, Stack.class).invoke(functions, semanticStack);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            handelError(loc, "Reflect error");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            handelError(loc, "No such action");
        }
        result.add(right + "->" + reductionResult);
    }

    /**
     * 错误处理程序
     *
     * @param loc     出错的位置
     * @param message 出错信息
     */
    private void handelError(int loc, String message) {
        System.err.println("input is illegal at " + loc + " error : " + message);
    }
}
