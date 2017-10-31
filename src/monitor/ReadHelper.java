package monitor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * 帮助读取文件的工具类
 */
class ReadHelper {
    /**
     * 读取输入文件的方法
     * @param fileName 输入文件名
     * @return 输入文件的字符串（添加了$符）
     */
    public static String readInput(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            return new String(fileContent, encoding) + "$";
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 读取转换表的方法
     * @param fileName 转换表文件名
     * @param productionsLeft 产生式左部列表
     * @param productionsRight 产生式右部列表
     * @param symbolMap 符号表
     */
    public static String[][] readTable(String fileName, ArrayList<String> productionsLeft
            , ArrayList<String> productionsRight, HashMap<String, Integer> symbolMap) {
        File file = new File(fileName);
        int n, m;
        String[][] result = new String[1][1];
        try {
            Scanner scanner = new Scanner(file);
            n = scanner.nextInt();
            m = scanner.nextInt();
            result = new String[n][m];

            //读转换表
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    result[i][j] = scanner.next();
                }
            }

            scanner.next();
            int count = 0;
            while (scanner.hasNext()) {
                String next = scanner.next();
                if (next.startsWith("#")) {
                    break;
                }
                symbolMap.put(next, count);
                count++;
            }

            while (scanner.hasNext()) {
                String[] next = scanner.next().split(":");
                if(next.length == 1){
                    break;
                }
                productionsLeft.add(next[0]);
                productionsRight.add(next[1]);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return result;
    }
}
