package com.common.util.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 文件描述 .csv文件工具
 **/
public class CSVUtils {
    public static void main(String[] args) {

        String path = "C:/ZQXX.csv";
        CSVImport(path);
    }

    public static void CSVImport(final String path) {
        long start = System.currentTimeMillis();
        String charset = "GBK";
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReaderBuilder(
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(new File(path)), charset))).build();
//            List<String[]> all = csvReader.readAll();
//            System.out.println("all size " + all.size());
//            StringBuilder headerSb = new StringBuilder();
//            String[] strings = all.get(0);
//            int length = strings.length;
//            for (int i = 0; i < length; i ++) {
//                if (i != length - 1) {
//                    headerSb.append(strings[i]).append(" ---- ");
//                } else {
//                    headerSb.append(strings[i]);
//                }
//            }
//            System.out.println("header is " + headerSb.toString());
//            for(int i = 1; i < all.size(); i ++) {
//                int length1 = all.get(i).length;
//                System.out.println(all.get(i)[0] + " --- " + (all.get(i)[length1 - 1]));
//            }

//            String[] strings1 = csvReader.readNext();
//            StringBuilder headerSb = new StringBuilder();
//            int length = strings1.length;
//            for (int i = 0; i < length; i ++) {
//                if (i != length - 1) {
//                    headerSb.append(strings1[i]).append(" ---- ");
//                } else {
//                    headerSb.append(strings1[i]);
//                }
//            }
//            System.out.println("header is " + headerSb.toString());
//            List<String[]> all = csvReader.readAll();
//            System.out.println("all size " + all.size());
//            for(int i = 0; i < all.size(); i ++) {
//                int length1 = all.get(i).length;
//                System.out.println(all.get(i)[0] + " --- " + (all.get(i)[length1 - 1]));
//            }

            Iterator<String[]> iterator = csvReader.iterator();
            while (iterator.hasNext()) {
                String[] next = iterator.next();
                Arrays.stream(next).forEach(v -> System.out.print(v + "   "));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("total time : " + (System.currentTimeMillis() - start));
        }
    }

    public static boolean isCsv(String fileName) {
        if (fileName.endsWith(".csv")) {
            return true;
        } else {
            return false;
        }
    }
}
