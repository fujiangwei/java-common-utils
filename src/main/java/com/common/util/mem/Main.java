package com.common.util.mem;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
public class Main {

    public static void main(String[] args) {
        try {
            OSMemCheck.printOPResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
