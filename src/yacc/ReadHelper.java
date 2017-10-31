package yacc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

/**
 * 辅助读取.y文件的类
 */
class ReadHelper {
    /**
     * 读取 .y文件
     * @param fileName .y文件路径
     * @param tokens 基本token集合
     * @param productions 产生式列表
     */
    public static void readFile(String fileName, Set<String> tokens, ArrayList<String> productions) {
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


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
