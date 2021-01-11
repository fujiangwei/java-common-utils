package com.common.util.mem;

import java.util.List;

/**
 * 文件描述
 *
 * @author
 * @date
 **/
public class OSResource {
    //PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND
    private String pid;
    private String user;
    private String pr;
    private String ni;
    private String virt;
    private String res;
    private String shr;
    private String s;
    private String cpu;
    private String men;
    private String timePlus;
    private String command;
    private List<String> mataData;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getNi() {
        return ni;
    }

    public void setNi(String ni) {
        this.ni = ni;
    }

    public String getVirt() {
        return virt;
    }

    public void setVirt(String virt) {
        this.virt = virt;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }

    public String getShr() {
        return shr;
    }

    public void setShr(String shr) {
        this.shr = shr;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMen() {
        return men;
    }

    public void setMen(String men) {
        this.men = men;
    }

    public String getTimePlus() {
        return timePlus;
    }

    public void setTimePlus(String timePlus) {
        this.timePlus = timePlus;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<String> getMataData() {
        return mataData;
    }

    public void setMataData(List<String> mataData) {
        this.mataData = mataData;
    }
}
