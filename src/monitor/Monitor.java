package monitor;

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

    public Monitor() {
        symbolMap = new HashMap<>();
        productionLeft = new ArrayList<>();
        productionRight = new ArrayList<>();

        input = ReadHelper.readInput(INPUT_FILE_PATH);

        String[][] cache;
        cache = ReadHelper.readTable(TABLE_FILE_PATH, productionLeft, productionRight, symbolMap);
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

    public static void main(String[] args) {
        Monitor m = new Monitor();
        System.out.println(m.parse());
    }

    /**
     * 将input解释为规约序列
     */
    public List<String> parse() {
        List<String> result = new LinkedList<>();
        int loc = 0;
        Stack<Integer> states = new Stack<>();
        Stack<String> symbols = new Stack<>();
        states.push(0);
        symbols.push("$");

        while (loc < input.length()) {
            int state = states.peek();
            String cur;

            //记录当前读头位置
            int before = loc;
            if (input.charAt(loc) == '{') {
                int start = loc + 1;
                while (input.charAt(loc) != '}') {
                    loc++;
                }
                cur = input.substring(start, loc);
            } else {
                cur = input.substring(loc, loc + 1);
            }

            if (!symbolMap.containsKey(cur)) {
                handelError(loc, "can't find symbol");
                return null;
            }
            int col = symbolMap.get(cur);
            switch (type[state][col]) {
                case 'e':
                    handelError(loc, "no transition in table");
                    return null;
                case 's':
                    states.push(table[state][col]);
                    symbols.push(cur);
                    loc++;
                    break;
                case 'r':
                    handleReduction(loc, table[state][col], states, symbols, result);
                    loc = before;
                    break;
                case 'a':
                    return result;
                default:
                    System.err.println("error in table!");
                    return null;
            }
        }

        handelError(loc, "can't match (incomplete sentence)");
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
