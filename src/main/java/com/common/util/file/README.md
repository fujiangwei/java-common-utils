# 远程文件读取
1. 如果将资讯文件放在Windows共享文件夹下面，可以使用smb协议进行远程读取。

    SMB（ServerMessage Block）通信协议是微软（Microsoft）和英特尔(Intel)在1987年制定的协议，主要是作为Microsoft网络的通讯协议。
    
    pom依赖如下：
    
        <dependency>
            <groupId>org.samba.jcifs</groupId>
            <artifactId>jcifs</artifactId>
            <version>1.3.18-kohsuke-1</version>
        </dependency>
        
    主要代码如下：

        private final static String url_yl = "ip1//dir//";
        private final static String url_jy = "ip2//dir2//";
        private final static String protocol = "smb://";
        private final static String user = "admin";
        private final static String password = "pwd";
        /**
         * 远程读取windows共享文件夹demo
         */
        public static void remoteReadWindowsShareFiles(){
            SmbFile [] fileList = null;
            FileOutputStream fos = null;
            SmbFileInputStream smbIs = null;
            // 拼接url，smb协议是将用户密码拼接到url中
            String romoteSmbURL = protocol + user + ":" + password + "@" + url_jy;
            try {
                SmbFile remoteFile = new SmbFile(romoteSmbURL);
                System.out.println("开始连接:"+System.currentTimeMillis());
                //尝试连接
                remoteFile.connect(); 
                System.out.println("连接成功:"+System.currentTimeMillis());
                if(remoteFile.isDirectory()){
                    fileList= remoteFile.listFiles();
                    System.out.println(fileList);
                    for (SmbFile sf:fileList) {
                        if(sf.isDirectory() && sf.getName().equals("dir/")){
                            for (SmbFile targetF:sf.listFiles()) {
                              if(targetF.isFile() && targetF.getName().equals("xxx.xlsx")){
                                  System.out.println("开始读取:"+System.currentTimeMillis());
                                  InputStream in = targetF.getInputStream();
                                  System.out.println("读取完成，开始解析:"+System.currentTimeMillis());
                                  xlsxEventReader(in);
                              }
                            }
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

2. 如果将资讯文件放在linux服务器上，可以使用sftp协议进行远程读取。

    使用ChannelSftp或者FTPClient，以下示例代码为ChannelSftp，FTPClient需要linux后台服务器开启FTP协议。

    pom文件依赖如下：
    
        <dependency>
            <groupId>jsch</groupId>
            <artifactId>jsch</artifactId>
            <version>0.1.54</version>
        </dependency>

    主要代码如下：

        public static String linux_url="ip";
        public static int linux_port=111;
        public static String linux_username="user";
        public static String linux_password="pwd";
        public static String rootPath="dir";
        /**
         * 远程读取linux文件
         */
        public static void remoteReadLiunxFiles(){
            System.out.println(System.currentTimeMillis()+"：开始远程读取linux服务器文件");
            JSch jsch = new JSch();
            Channel channel = null;
            ChannelSftp sftp = null;
            Session session = null;
            try {
                session = jsch.getSession(linux_username, linux_url, linux_port);
                session.setPassword(linux_password);
                Properties config = new Properties();
                config.put("StrictHostKeyChecking", "no");
                session.setConfig(config);
                session.connect();
                channel = session.openChannel("sftp");
                channel.connect();
                System.out.println(System.currentTimeMillis()+"：linux连接成功");
                sftp = (ChannelSftp) channel;
                // 获取文件列表
                Vector vector = sftp.ls(rootPath);
                for(Object item:vector){
                    ChannelSftp.LsEntry lsEntry = (com.jcraft.jsch.ChannelSftp.LsEntry) item;
                    String fileName = lsEntry.getFilename();
                    if(fileName.equals("xxx.xlsx")){
                        //获取远程文件
                        InputStream inputStream = sftp.get("/root/test/"+fileName);
                        System.out.println(System.currentTimeMillis()+"：获取到excel文件，开始解析");
                        xlsxEventReader(inputStream);
                    }
                }
            } catch (JSchException e) {
                e.printStackTrace();
            } catch (SftpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭相关连接资源
                if(channel!=null && !channel.isClosed()){
                    channel.disconnect();
                }
                if(sftp!=null&&!sftp.isClosed()){
                    sftp.disconnect();
                }
                if(session!=null){
                    session.disconnect();
                }
            }
        }