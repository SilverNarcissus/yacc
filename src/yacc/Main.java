package yacc;

/**
 * 语法分析器yacc部分的入口
 */
public class Main {
    public static void main(String[] args) {
        Analyzer analyzer = new Analyzer();
        analyzer.parse();
    }
}
