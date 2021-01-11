package com.common.util.csv;

import com.common.util.file.ReadRemoteFile;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import jcifs.smb.SmbFile;

import java.io.*;
import java.util.*;

/**
 * 文件描述 .csv文件工具
 **/
public class CSVUtils {
    public static void main(String[] args) {

//        String path = "Z:\\20200326\\ZQXX_20200326.csv";
//        CSVImport(path);
        String path = "C:\\Users\\hspcadmin\\Desktop\\GBXX_20191216.xls";
        CSV2XLS(path);
    }

    public static void CSV2XLS(String path)  {
        try {
            File xlsFile = new File(path);
            CSVReader reader = new CSVReader(new FileReader(xlsFile), '\t');
            ArrayList<String[]> data = new ArrayList<String[]>();
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                final int size = nextLine.length;
                //handle empty lines
                if (size == 0) {
                    continue;
                }

                String debut = nextLine[0].trim();
                if (debut.length() == 0 && size == 1) {
                    continue;
                }
                data.add(nextLine);
            }

            String[] titles = data.get(0);
            data.remove(0);

            ArrayList<Map<String, String>> mappedData = new
                    ArrayList<Map<String, String>>(data.size());

            final int titlesLength = titles.length;

            for (String[] oneData : data) {
                final Map<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < titlesLength; i++) {
                    final String key = titles[i];
                    final String value = oneData[i];
                    map.put(key, value);
                }

                mappedData.add(map);
            }

            System.out.println(" 》》》》》 " + mappedData.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

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
//            SmbFile smbFile = ReadRemoteFile.smbFileGet(path);
//            csvReader = new CSVReaderBuilder(
//                    new BufferedReader(
//                            new InputStreamReader(
//                                    smbFile.getInputStream() , charset))).build();
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
