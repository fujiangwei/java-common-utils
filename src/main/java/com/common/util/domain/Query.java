package com.common.util.domain;

import com.common.util.xml.annotations.FieldValue;
import com.common.util.xml.xstream.convert.QueryConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

/**
 * 文件描述
 **/
@XStreamAlias(value = "Query")
@XStreamConverter(value = QueryConverter.class)
public class Query {

    @XStreamAsAttribute
    @XStreamAlias(value = "TransactionCode")
    private String transactionCode;

    @FieldValue
    private String querySql;

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getQuerySql() {
        return querySql;
    }

    public void setQuerySql(String querySql) {
        this.querySql = querySql;
    }
}
