package yacc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
     * @param action      归约动作列表
     */
    public static void readFile(String fileName, List<String> tokens, ArrayList<String> productions, List<String> action) {
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
                if (scanner.nextLine().startsWith("%")) {
                    break;
                }
            }

            while (scanner.hasNext()) {
                String next = scanner.nextLine();
                if(next.startsWith("#")){
                    continue;
                }
                if (next.startsWith("%")) {
                    break;
                }
                productions.add(next);
                action.add(scanner.nextLine());
            }

            scanner.close();
        } catch (Exception e) {
            System.err.println(".y文件存在格式错误，请检查");
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

    /**
     * 构建归约动作文件
     *
     * @param fileName 文件名
     * @param actions  归约动作列表
     */
    public static void buildActionsFile(String fileName, List<String> actions) {
        FileWriter writer = null;
        BufferedWriter bufferedWriter = null;
        try {
            writer = new FileWriter(new File(fileName));
            bufferedWriter = new BufferedWriter(writer);

            //write head part
            bufferedWriter.write("package monitor;\n\n");
            bufferedWriter.write("import java.util.Stack;\n\n");

            //write comment
            bufferedWriter.write("/**\n * 这个文件是yacc自动生成的归约动作文件\n * 生成时间为：" + new Date() + "\n */\n");
            //
            bufferedWriter.write("public class Functions {\n");
            int count = 0;
            //为了使生成的文件更加整洁，需要加入分隔符
            final String delimiter = "    ";
            for (String function : actions) {
                function = function.substring(1, function.length() - 1);
                bufferedWriter.write(delimiter + "public static void function" + count + "(Stack<String> s) {\n");
                for (String expression : function.split(";")) {
                    if (expression.isEmpty()) {
                        continue;
                    }
                    bufferedWriter.write(delimiter + delimiter + expression.trim() + ";\n");
                }
                bufferedWriter.write(delimiter + "}\n\n");
                count++;
            }

            bufferedWriter.write("}");

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
