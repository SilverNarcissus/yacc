package monitor;

import java.util.Stack;

/**
 * 这个文件是yacc自动生成的归约动作文件
 * 生成时间为：Thu Nov 09 10:30:33 CST 2017
 */
public class Functions {
    public static void function0(Stack<String> s) {
        System.out.println("The result of this expression is:" + s.peek());
    }

    public static void function1(Stack<String> s) {
    }

    public static void function2(Stack<String> s) {
        int first = Integer.valueOf(s.pop());
        String op = s.pop();
        int second = Integer.valueOf(s.pop());
        s.push(String.valueOf(op.equals("+")?(first + second):(first - second)));
    }

    public static void function3(Stack<String> s) {
    }

    public static void function4(Stack<String> s) {
        int first = Integer.valueOf(s.pop());
        String op = s.pop();
        int second = Integer.valueOf(s.pop());
        s.push(String.valueOf(op.equals("*")?(first * second):(first / second)));
    }

    public static void function5(Stack<String> s) {
        s.pop();
        String cur = s.pop();
        s.pop();
        s.push(cur);
    }

    public static void function6(Stack<String> s) {
    }

    public static void function7(Stack<String> s) {
    }

    public static void function8(Stack<String> s) {
    }

    public static void function9(Stack<String> s) {
    }

    public static void function10(Stack<String> s) {
    }

    public static void function11(Stack<String> s) {
    }

}