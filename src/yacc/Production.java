package yacc;

import java.util.HashSet;
import java.util.Set;

/**
 * 表示一个具有预测符号的产生式
 */
class Production {
    /**
     * 产生式右部
     */
    String right;
    /**
     * 产生式左部
     */
    String left;
    /**
     * 点的位置
     */
    int dot;
    /**
     * 预测符号集合
     */
    Set<String> prediction;

    public Production(String right, String left, int dot, Set<String> prediction) {
        this.right = right;
        this.left = left;
        this.dot = dot;
        this.prediction = prediction;
    }

    public static void main(String[] args) {
        Set<String> test = new HashSet<>();
        test.add("INTEGER");
        test.add("+");
        Production production = new Production("E", "{INTEGER}+T", 0, test);
        System.out.println(production.shift().shift());
    }

    @Override
    public int hashCode() {
        return left.hashCode() + dot + prediction.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Production)) {
            return false;
        }

        Production another = (Production) o;
        return left.equals(another.left)
                && dot == another.dot
                && prediction.equals(another.prediction);
    }

    /**
     * 判断该产生式是否可归约
     *
     * @return 该产生式是否可归约
     */
    boolean canReduce() {
        return dot == left.length();
    }

    /**
     * 找到下一个移进的符号
     *
     * @return 下一个移进的符号
     */
    String next() {
        if (canReduce()) {
            throw new IllegalStateException("production can't shift because it's reducible");
        }
        int end = dot;
        int start = dot;

        if (left.charAt(start) == '{') {
            start = dot + 1;
            while (left.charAt(end) != '}') {
                end++;
            }
            end--;
        }

        end++;
        return left.substring(start, end);
    }

    /**
     * 将该产生式移进一个标志，产生一个新的产生式
     *
     * @return 移进产生的新产生式
     */
    Production shift() {
        if (canReduce()) {
            throw new IllegalStateException("production can't shift because it's reducible");
        }
        int newDot = dot;
        if (left.charAt(newDot) == '{') {
            while (left.charAt(newDot) != '}') {
                newDot++;
            }
            newDot++;
        } else {
            newDot++;
        }

        return new Production(right, left, newDot, new HashSet<>(prediction));
    }

    @Override
    public String toString() {
        String result = right + "->" + left.substring(0, dot) + "." + left.substring(dot) + " predictions: ";
        for (String s : prediction) {
            result += s + "|";
        }
        return result;
    }
}