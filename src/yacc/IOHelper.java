package yacc;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 辅助读取.y文件的类
 */
class IOHelper {
    /**
     * 读取 .y文件
     *
     * @param fileName    .y文件路径
     * @param tokens      基本token集合
     * @param productions 产生式列表
     */
    public static void readFile(String fileName, List<String> tokens, ArrayList<String> productions) {
        File file = new File(fileName);
        try {
            Scanner scanner = new Scanner(file);

            scanner.next();
            while (scanner.hasNext()) {
                String next = scanner.next();
                if (next.startsWith("#")) {
                    break;
                }
                tokens.add(next);
            }

            while (scanner.hasNext()) {
                if (scanner.next().startsWith("%")) {
                    break;
                }
            }

            while (scanner.hasNext()) {
                String next = scanner.next();
                if (next.startsWith("#")) {
                    break;
                }
                productions.add(next);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成.t文件
     *
     * @param table              转换表
     * @param type               转换类型
     * @param fileName           .t文件位置
     * @param tokens             token列表
     * @param nonTerminalSymbols 非终结符列表
     * @param productions        产生式列表
     */
    public static void writeFile(String fileName, int[][] table, char[][] type, List<String> tokens, List<String> nonTerminalSymbols, ArrayList<String> productions) {
        int n = table.length;
        int m = table[0].length;

        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(new File(fileName));
            bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(n + " " + m + "\n");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (type[i][j] == 'r' && table[i][j] == 0) {
                        bufferedWriter.write("a0 ");
                        continue;
                    }
                    bufferedWriter.write(type[i][j] + "" + table[i][j] + " ");
                }
                bufferedWriter.write("\n");
            }

            bufferedWriter.write("#symbol_list\n");

            for (String token : tokens) {
                bufferedWriter.write(token + "\n");
            }
            for (String nonTerminalSymbol : nonTerminalSymbols) {
                bufferedWriter.write(nonTerminalSymbol + "\n");
            }

            bufferedWriter.write("#productions\n");

            for (String production : productions) {
                bufferedWriter.write(production + "\n");
            }

            bufferedWriter.write("######end of the table#######\n");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
