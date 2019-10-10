package com.common.util.xml.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * 文件描述
 **/
@XStreamAlias(value = "Instruction")
public class Instruction {

    @XStreamAlias(value = "l_temp_no")
    private String lTempNo;

    @XStreamAlias(value = "l_fund_id")
    private String lFundId;

    @XStreamAlias(value = "l_basecombi_id")
    private String lBasecombiId;

    @XStreamAlias(value = "l_stock_count")
    private String lStockCount;

    @XStreamAlias(value = "Stock")
    private List<String> stocks;

    @XStreamAlias(value = "User")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @XStreamAlias(value = "Users")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getlTempNo() {
        return lTempNo;
    }

    public void setlTempNo(String lTempNo) {
        this.lTempNo = lTempNo;
    }

    public String getlFundId() {
        return lFundId;
    }

    public void setlFundId(String lFundId) {
        this.lFundId = lFundId;
    }

    public String getlBasecombiId() {
        return lBasecombiId;
    }

    public void setlBasecombiId(String lBasecombiId) {
        this.lBasecombiId = lBasecombiId;
    }

    public String getlStockCount() {
        return lStockCount;
    }

    public void setlStockCount(String lStockCount) {
        this.lStockCount = lStockCount;
    }

    public List<String> getStocks() {
        return stocks;
    }

    public void setStocks(List<String> stocks) {
        this.stocks = stocks;
    }
}
