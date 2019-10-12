package com.common.util.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 文件描述
 **/
@XStreamAlias(value = "DataParam")
public class DataParam {
    @XStreamAsAttribute
    @XStreamAlias(value = "UserName")
    private String userName;
    @XStreamAsAttribute
    @XStreamAlias(value = "Password")
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
