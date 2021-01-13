package com.common.util.timewindow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author
 * @Description: 文件描述
 * @date
 **/
public class TimeWheelCalculate {

    private static final long START = 0;

    /**
     * 时间片
     */
    private int period;

    /**
     * 时间窗口的长度
     */
    private int length;

    /**
     * 划分的时间片个数
     */
    private int cellNum;

    private void check() {
        if (length % period != 0) {
            throw new IllegalArgumentException(
                    "length % period should be zero but not! now length: " + length + " period: " + period);
        }
    }

    public TimeWheelCalculate(int period, int length) {
        this.period = period;
        this.length = length;
        check();
        this.cellNum = length / period;
    }

    /**
     *  根据当前时间，确定过期的数据在数组的索引
     *
     * @param time
     * @return
     */
    public int calIndex(long time) {
        int idx = (int) ((time - START) % length / period);
        System.out.println("cur data is START = " + START + ",time = " + time + ",length = " + length + ",period = " + period + ",idx = " + idx);
        return idx;
    }

    /**
     * 获取所有过期的时间片索引
     *
     * @param lastInsertTime 上次更新时间轮的时间戳
     * @param nowInsertTime  本次更新时间轮的时间戳
     * @return
     */
    public List<Integer> getExpireIndexes(long lastInsertTime, long nowInsertTime) {
        if (nowInsertTime - lastInsertTime >= length) {
            // 已经过了一轮，过去的数据全部丢掉
            return null;
        }

        List<Integer> removeIndexList = new ArrayList<>();
        int lastIndex = calIndex(lastInsertTime);
        int nowIndex = calIndex(nowInsertTime);

        if (lastIndex == nowIndex) {
            // 还没有跨过这个时间片，则不需要删除过期数据
            return Collections.emptyList();
        } else if (lastIndex < nowIndex) {
            for (int tmp = lastIndex; tmp < nowIndex; tmp++) {
                removeIndexList.add(tmp);
            }
        } else {
            for (int tmp = lastIndex; tmp < cellNum; tmp++) {
                removeIndexList.add(tmp);
            }

            for (int tmp = 0; tmp < nowIndex; tmp++) {
                removeIndexList.add(tmp);
            }
        }

        return removeIndexList;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getCellNum() {
        return cellNum;
    }

    public void setCellNum(int cellNum) {
        this.cellNum = cellNum;
    }
}