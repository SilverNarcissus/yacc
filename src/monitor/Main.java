package monitor;

/**
 * 语法分析器monitor部分入口
 */
public class Main {
    /**
     * out.txt 文件路径
     */
    private static final String OUTPUT_FILE_PATH = "out.txt";

    public static void main(String[] args) {
        Monitor m = new Monitor();
        IOHelper.writeOutput(OUTPUT_FILE_PATH, m.parse());
    }
}
