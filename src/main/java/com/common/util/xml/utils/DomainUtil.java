package com.common.util.xml.utils;

import com.common.util.xml.domain.User;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 文件描述
 **/
public class DomainUtil {

    /**
     * 日志记录
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainUtil.class);

    /**
     * 默认时间格式
     */
    public static final String DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd";

    public static void main(String[] args) {
        User user = new User();
        user.setId(1);
//        user.setName("11111");

        List<String> fieldOrderList = Lists.newArrayList("id", "name", "status", "extra");

        System.out.println(user.toString());

        User parse2Bean = parse2Bean(user.toString(), User.class, fieldOrderList);
        System.out.println(parse2Bean.toString());
    }

    /**
     * @description ：解析字符串为对象
     * @param valStr 解析字符串（类似600000SS|1|100.000000000000）
     * @param tClass 映射对象class
     * @param fieldOrderList 映射顺序
     * @date  2019/10/8 16:45
     * @modifier
     * @return  T
     */
    public static <T> T parse2Bean(String valStr, Class<T> tClass, List<String> fieldOrderList) {

        Assert.isTrue(CollectionUtils.isNotEmpty(fieldOrderList), "参数为空");
        Assert.isTrue(StringUtils.isNotEmpty(valStr), "目标字符串为空");

        Splitter splitter = Splitter.on("|").trimResults();
        List<String> valList = splitter.splitToList(valStr);
        Assert.isTrue(fieldOrderList.size() == valList.size(), "字段转换长度不一致");
        Object obj = null;
        try {
            obj = tClass.newInstance();

            for (int i = 0; i < valList.size(); i++) {
                Field curField = tClass.getDeclaredField(fieldOrderList.get(i));
                curField.setAccessible(Boolean.TRUE);
                setFieldValue(curField, valList.get(i), obj, "");
                curField.setAccessible(Boolean.FALSE);
            }
        } catch (Exception e) {
            LOGGER.error("[DomainUtil] parse2Bean 异常，{}", e);
            throw new IllegalArgumentException("解析异常");
        }

        return (T)obj;
    }

    /**
     * @description ：是否是基本类型
     * @param curType 当前类型
     * @date  2019/9/29 18:45
     * @modifier
     * @return  boolean
     */
    public static boolean isBaseType(Class<?> curType) {

        try {
            if (curType.equals(String.class) || curType.equals(Float.class)
                    || curType.equals(Integer.class) || curType.equals(int.class)
                    || curType.equals(Long.class) || curType.equals(long.class)
                    || curType.equals(Double.class) || curType.equals(double.class)
                    || curType.equals(Short.class) || curType.equals(Byte.class))
            {
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            LOGGER.error("[DomainUtil] isBaseType 异常，{}", e);
            throw e;
        }

        return Boolean.FALSE;
    }

    /**
     * @description ：设定对象基本字段值
     * @param curField 字段
     * @param curFieldVal 当前字段值
     * @param obj 映射实体对象
     * @date  2019/9/29 18:19
     * @modifier
     * @return  boolean
     */
    private static void setFieldValue(Field curField, String curFieldVal, Object obj, String dateFormat) {
        // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。
        Class<?> curFieldType = curField.getType();
        try {
            if (curFieldType.equals(String.class)) {
                curField.set(obj, curFieldVal);
            } else if (curFieldType.equals(Long.class) || curFieldType.equals(long.class)) {
                curField.set(obj, Long.parseLong(curFieldVal));
            } else if (curFieldType.equals(Double.class) || curFieldType.equals(double.class)) {
                curField.set(obj, Double.parseDouble(curFieldVal));
            } else if (curFieldType.equals(Integer.class) || curFieldType.equals(int.class)) {
                curField.set(obj, Integer.parseInt(curFieldVal));
            } else if (curFieldType.equals(java.util.Date.class)) {
                SimpleDateFormat format = new SimpleDateFormat(StringUtils.isEmpty(dateFormat)
                        ? DEFAULT_FORMAT_PATTERN : dateFormat);
                curField.set(obj, format.parse(curFieldVal));
            } else {
                LOGGER.warn("Field {} is a not a base type", curField.getName());
            }
        } catch (Exception e) {
            LOGGER.error("[Dom4jUtil] setValue 异常，{}", e);
        }
    }
}
