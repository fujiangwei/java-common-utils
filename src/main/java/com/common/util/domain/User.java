package com.common.util.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件描述
 **/
@XStreamAlias(value = "User")
public class User {

    private int id;

    private String name;

    private int status;

    private String extra;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return String.format("%d|%s|%d|%s", this.id, this.name, this.status, StringUtils.isEmpty(this.extra) ? "" : this.extra);
    }
}
