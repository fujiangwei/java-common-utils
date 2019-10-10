package com.common.util.xml.xstream.convert;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 文件描述 XStream字段时间转换器
 **/
public class LocalDateTimeTConverter implements SingleValueConverter {

    private final static String DATE_FORMAT_PATTERN = "yyyyMMdd";

    @Override
    public String toString(Object obj) {

        Assert.notNull(obj, "入参不能为空");
        Assert.isTrue(obj instanceof LocalDateTime, "入参类型错误");

        LocalDateTime localDateTime = (LocalDateTime) obj;
        try {
            return localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN));
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public Object fromString(String dateStr) {

        Assert.isTrue(StringUtils.isNotEmpty(dateStr), "入参不能为空");
        try {
            return LocalDateTime.parse(dateStr, pattern(DATE_FORMAT_PATTERN));
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @description ：格式化
     * @param format 格式化规则
     * @date  2019/9/30 17:16
     * @modifier
     * @return  java.time.format.DateTimeFormatter
     */
    private static DateTimeFormatter pattern(final String format) {
        return DateTimeFormatter.ofPattern(format);
    }

    @Override
    public boolean canConvert(Class aClass) {
        return LocalDateTime.class == aClass || Date.class == aClass;
    }
}