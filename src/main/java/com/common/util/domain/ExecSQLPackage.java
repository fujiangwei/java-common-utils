package com.common.util.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.Date;

/**
 * 文件描述
 **/
@XStreamAlias(value = "ExecSQLPackage")
public class ExecSQLPackage {

    @XStreamAlias(value = "QueryMode")
    private Integer queryMode;
    @XStreamAlias(value = "QueryCondition")
    private String queryCondition;
    @XStreamAlias(value = "DataParam")
    private DataParam dataParam;
    @XStreamAlias(value = "Query")
    private Query query;
    @XStreamAlias(value = "BeginDate")
    private Date beginDate;

    public Integer getQueryMode() {
        return queryMode;
    }

    public void setQueryMode(Integer queryMode) {
        this.queryMode = queryMode;
    }

    public String getQueryCondition() {
        return queryCondition;
    }

    public void setQueryCondition(String queryCondition) {
        this.queryCondition = queryCondition;
    }

    public DataParam getDataParam() {
        return dataParam;
    }

    public void setDataParam(DataParam dataParam) {
        this.dataParam = dataParam;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }
}
