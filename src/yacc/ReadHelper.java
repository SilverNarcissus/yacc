package yacc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 辅助读取.y文件的类
 */
class ReadHelper {
    /**
     * 读取 .y文件
     * @param fileName .y文件路径
     * @return 基本产生式列表
     */
    public static List<String> readFile(String fileName) {
        File file = new File(fileName);
        List<String> result = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);

            scanner.next();
            while (scanner.hasNext()) {
                if (scanner.next().startsWith("%")) {
                    break;
                }
            }

            while (scanner.hasNext()) {
                String next = scanner.next();
                if(next.startsWith("#")){
                    break;
                }
                result.add(next);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return result;
    }
}
