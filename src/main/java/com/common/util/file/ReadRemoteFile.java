package com.common.util.file;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import jcifs.smb.SmbFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件描述
 **/
public class ReadRemoteFile {

    private static Logger log = LoggerFactory.getLogger(ReadRemoteFile.class);

    private static String SMB_PROTOCOL = "smb://";

    private static String USER_DOMAIN = "ip1";
    private static String USER_SHARED_PATH = "jy";
    private static String USER_ACCOUNT = "username";
    private static String USER_PWS = "pwd";
    private static String USER_SHARED_FILE = "xxx.xlsx";

    private static String USER_DOMAIN2 = "ip1";
    private static String USER_SHARED_PATH2 = "usershare";
    private static String USER_ACCOUNT2 = "test01";
    private static String USER_PWS2 = "1111";
    private static String USER_SHARED_FILE2 = "xxx.xlsx";

    public static void main(String[] args) throws Exception {

        //需要认证访问
        String authPath = SMB_PROTOCOL + USER_ACCOUNT + ":" + USER_PWS + "@" + USER_DOMAIN + "/" + USER_SHARED_PATH + "/" + USER_SHARED_FILE;
        String authPath2 = SMB_PROTOCOL + USER_ACCOUNT2 + ":" + USER_PWS2 + "@" + USER_DOMAIN2 + "/" + USER_SHARED_PATH2 + "/" + USER_SHARED_FILE2;
        //无需认证访问
        String noAuthPath = SMB_PROTOCOL + USER_DOMAIN + "/" + USER_SHARED_PATH + "/" + USER_SHARED_FILE;

        smbFileGet(authPath);
        smbFileGet(authPath2);
    }

    public static SmbFile smbFileGet(String remoteUrl) {
        log.info("remoteUrl is {}",remoteUrl);
        try {
            SmbFile smbFile = new SmbFile(remoteUrl);
            if (!smbFile.exists()) {
                log.warn("{} 文件或目录不存在", smbFile.getName());
                throw new IllegalArgumentException(smbFile.getName() + "文件或目录不存在");
            }

            if (smbFile.isDirectory()) {
                log.warn("{} 为目录", smbFile.getName());
                throw new IllegalArgumentException(smbFile.getName() + "为目录");
            }

            if (smbFile.isFile()) {
                log.info("{} 为文件", smbFile.getName());
                return smbFile;
            }
        } catch (Exception e) {
            log.error("smbFileGet 异常，", e);
            throw new IllegalArgumentException("访问目标资源异常");
        }

        return null;
    }

    public static Connection getConnect(String hostName, String username, String password, int port) {
        Connection conn = new Connection(hostName, port);
        try {
            // 连接到主机
            conn.connect();
            // 使用用户名和密码校验
            boolean isconn = conn.authenticateWithPassword(username, password);
            if (!isconn) {
                System.out.println("用户名称或者是密码不正确");
            } else {
                System.out.println("服务器连接成功.");
                return conn;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean fileExist(String path, Connection conn) {
        if (conn != null) {
            Session ss = null;
            try {
                ss = conn.openSession();
                ss.execCommand("ls -l ".concat(path));
                InputStream is = new StreamGobbler(ss.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                String line = "";
                while (true) {
                    String lineInfo = brs.readLine();
                    if (lineInfo != null) {
                        line = line + lineInfo;
                        System.out.println(line);
                    } else {
                        break;
                    }
                }
                brs.close();
                if (line != null && line.length() > 0 && line.startsWith("-")) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 连接的Session和Connection对象都需要关闭
                if (ss != null) {
                    ss.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
        }
        return false;
    }

    public static void readLogFile(String path, Connection conn) {
        if (conn != null) {
            Session ss = null;
            try {
                ss = conn.openSession();
                ss.execCommand("tail -100 ".concat(path));
                InputStream is = new StreamGobbler(ss.getStdout());
                BufferedReader brs = new BufferedReader(new InputStreamReader(is));
                while (true) {
                    String line = brs.readLine();
                    if (line == null) {
                        break;
                    } else {
                        System.out.println(line);
                    }
                }
                brs.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 连接的Session和Connection对象都需要关闭
                if (ss != null) {
                    ss.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }
}