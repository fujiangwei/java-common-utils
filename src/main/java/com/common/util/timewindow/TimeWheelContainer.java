package com.common.util.timewindow;

import java.util.Arrays;
import java.util.List;

/**
 * @author
 * @Description: 文件描述
 * @date
 **/
public class TimeWheelContainer {

    private TimeWheelCalculate calculate;

    /**
     * 历史时间片计数，每个时间片对应其中的一个元素
     */
    private int[] counts;

    /**
     * 实时的时间片计数数量
     */
    private int realTimeCount;

    /**
     * 整个时间轮总数计数数量
     */
    private int timeWheelCount;

    /**
     * 最新一次插入时间
     */
    private Long lastInsertTime;

    public TimeWheelContainer(TimeWheelCalculate calculate) {
        this.counts = new int[calculate.getCellNum()];
        this.calculate = calculate;
        this.realTimeCount = 0;
        this.timeWheelCount = 0;
        this.lastInsertTime = null;
    }

    public void add(long now, int amount) {
        if (lastInsertTime == null) {
            realTimeCount = amount;
            lastInsertTime = now;
            return;
        }

        List<Integer> removeIndex = calculate.getExpireIndexes(lastInsertTime, now);
        if (removeIndex == null) {
            // 两者时间间隔超过一轮，则清空计数
            realTimeCount = amount;
            lastInsertTime = now;
            timeWheelCount = 0;
            clear();
            return;
        }

        if (removeIndex.isEmpty()) {
            // 没有跨过时间片，则只更新实时计数
            realTimeCount += amount;
            lastInsertTime = now;
            return;
        }

        // 跨过了时间片，则需要在总数中删除过期的数据，并追加新的数据
        for (int index : removeIndex) {
            timeWheelCount -= counts[index];
            counts[index] = 0;
        }
        timeWheelCount += realTimeCount;
        counts[calculate.calIndex(lastInsertTime)] = realTimeCount;
        lastInsertTime = now;
        realTimeCount = amount;

        System.out.println(timeWheelCount + " --- " + Arrays.toString(counts));
    }

    private void clear() {
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
    }

    public TimeWheelCalculate getCalculate() {
        return calculate;
    }

    public void setCalculate(TimeWheelCalculate calculate) {
        this.calculate = calculate;
    }

    public int[] getCounts() {
        return counts;
    }

    public void setCounts(int[] counts) {
        this.counts = counts;
    }

    public int getRealTimeCount() {
        return realTimeCount;
    }

    public void setRealTimeCount(int realTimeCount) {
        this.realTimeCount = realTimeCount;
    }

    public int getTimeWheelCount() {
        return timeWheelCount;
    }

    public void setTimeWheelCount(int timeWheelCount) {
        this.timeWheelCount = timeWheelCount;
    }

    public Long getLastInsertTime() {
        return lastInsertTime;
    }

    public void setLastInsertTime(Long lastInsertTime) {
        this.lastInsertTime = lastInsertTime;
    }
}