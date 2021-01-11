package com.common.util.mem;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
@Component
@Configuration
@EnableScheduling
public class OSMemCheck {
    // 每天17点执行一次
    // @Scheduled(cron="0 0 18 * * ? ")
    // @Scheduled(cron="0/20 * *  * * ? ")
    public void checkTask() {
        try {
            printOPResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String LINE_SPLITER = "\r\n";
    public static final String SPACE_CHAR = " ";
    public static final String[] NEEDED_USER = {"uftdb", "ufrdb", "webclient", "workflow", "webserv", "schedul",
            "ufc", "trade", "nodear", "zookeep", "mysql", "busincomm", "transform", "adapter", "trade", "tradeservice"};

    public static void printOPResource() throws Exception {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;

        System.out.println("Get usage rate of CUP : ");
        // top命令是Linux下常用的性能分析工具，能够实时显示系统中各个进程的资源使用情况。
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("top -b -c  -n 1 -o %MEM");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 可以读取新开启的程序的 System.out.print 输出的内容
        is = process.getInputStream();
        // 将字节流转换为字符流。
        isr = new InputStreamReader(is);
        // BufferedReader 流能够读取文本行
        brStat = new BufferedReader(isr);
        // 解析汇总信息
        String osTotalResource = parseOSTotalResource(brStat);
        // 解析列头
        System.out.println("信息头:" + brStat.readLine());
        // 解析数据内容
        List<OSResource> osResourceList = parseOSResource(brStat);
        // 输出数据到csv文件
        creatNewCSV(osTotalResource, osResourceList);
    }

    private static String parseOSTotalResource(BufferedReader brStat) throws Exception {
        String lineStr = "";
        StringBuilder sb = new StringBuilder();
        while ((lineStr = brStat.readLine()) != null && lineStr.trim().length() != 0) {
            sb.append(lineStr + LINE_SPLITER);
        }

        return sb.toString();
    }

    private static List<OSResource> parseOSResource(BufferedReader brStat) throws Exception {
        List<OSResource> resourceList = new ArrayList<>();
        String lineStr = "";
        // 用来分隔String的应用类
        StringTokenizer tokenStat = null;
        while ((lineStr = brStat.readLine()) != null && !lineStr.equals("\r") && !lineStr.equals("\r\n")) {
            System.out.println("debug:" + lineStr);
            tokenStat = new StringTokenizer(lineStr);
            List<String> mataData = parseMataData(tokenStat);
            if (!needUser(mataData.get(1))) {
                continue;
            }
            OSResource osResource = parseOSResource(mataData);
            resourceList.add(osResource);
        }

        return resourceList;
    }

    private static List<String> parseMataData(StringTokenizer stringTokenizer) {
        List<String> strings = new ArrayList<>();
        while (stringTokenizer.hasMoreTokens()) {
            strings.add(stringTokenizer.nextToken());
        }
        return strings;
    }

    private static OSResource parseOSResource(List<String> mataData) {
        OSResource osResource = new OSResource();
        osResource.setMataData(mataData);
//        private String pid;
////        private String user;
////        private String pr;
////        private String ni;
////        private String virt;
////        private String res;
////        private String shr;
////        private String s;
////        private String cpu;
////        private String men;
////        private String timePlus;
////        private String command;
        osResource.setPid(mataData.get(0));
        osResource.setUser(mataData.get(1));
        osResource.setPr(mataData.get(2));
        osResource.setNi(mataData.get(3));
        osResource.setVirt(mataData.get(4));
        osResource.setRes(mataData.get(5));
        osResource.setShr(mataData.get(6));
        osResource.setS(mataData.get(7));
        osResource.setCpu(mataData.get(8));
        osResource.setMen(mataData.get(9));
        osResource.setTimePlus(mataData.get(10));
        StringBuilder sb = new StringBuilder();
        for (int i = 11; i < mataData.size(); i++) {
            sb.append(mataData.get(i) + SPACE_CHAR);
        }
        osResource.setCommand(sb.toString());
        return osResource;
    }

    public static boolean needUser(String str) {
        System.out.println("判断用户是否需要:" + str);
        if (str.endsWith("+")) {
            str = str.substring(0, str.length() - 1);
        }
        for (String user : NEEDED_USER) {
            if (str.startsWith(user)) {
                return true;
            }
            if (user.startsWith(str)) {
                return true;
            }
        }
        return false;
    }

    //PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
    public static String[] HEADERS_OUT = {"PID", "USER", "PR", "NI", "VIRT", "RES", "SHR", "S", "%CPU", "%MEM", "TIME+", "COMMAND", "Total"};

    public static void creatNewCSV(String totalOSResource, List<OSResource> osResourceList) {
        InetAddress ia = null;
        // 获取当前日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String data = df.format(new Date());
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String localname = addr.getHostName();
            String localip = addr.getHostAddress();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("osresourcecheck_" + localip + "_" + data + ".csv"), "UTF-8"));
            try (CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS_OUT))) {

                try {
                    // 打印各个进程暂用资源的信息
                    for (OSResource osResource : osResourceList) {
                        printer.printRecord(osResource.getPid()
                                , osResource.getUser()
                                , osResource.getPr()
                                , osResource.getNi()
                                , OSMenFormat(osResource.getVirt())
                                , OSMenFormat(osResource.getRes())
                                , OSMenFormat(osResource.getShr())
                                , osResource.getS()
                                , osResource.getCpu()
                                , osResource.getMen()
                                , osResource.getTimePlus()
                                , osResource.getCommand()
                                , "");
                    }
                    // 打印头信息
                    printer.printRecord("", "", "", "", "", "", "", "", "", "", "", "", totalOSResource);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                printer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String OSMenFormat(String originalStr) {
        if (originalStr.endsWith("g") || originalStr.endsWith("m")) {
            return originalStr;
        }
        BigDecimal doubleNum = new BigDecimal(originalStr);

        doubleNum = doubleNum.divide(new BigDecimal(1024));
        return doubleNum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "m";
    }
}
