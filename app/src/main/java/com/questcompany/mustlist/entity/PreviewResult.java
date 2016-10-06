package com.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 6..
 * preview result object
 */
public class PreviewResult {
    private String startDay;
    private String period;
    private String amount;
    private String timeRange;
    private Integer defaultPoint;
    private Integer successPoint;

    public String getStartDay() {
        return startDay;
    }

    public String getPeriod() {
        return period;
    }

    public String getAmount() {
        return amount;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public void setDefaultPoint(Integer defaultPoint) {
        this.defaultPoint = defaultPoint;
    }

    public void setSuccessPoint(Integer successPoint) {
        this.successPoint = successPoint;
    }

    public Integer getDefaultPoint() {
        return defaultPoint;
    }

    public Integer getSuccessPoint() {
        return successPoint;
    }

    @Override
    public String toString() {
        return "PreviewResult{" +
                "startDay='" + startDay + '\'' +
                ", period='" + period + '\'' +
                ", amount='" + amount + '\'' +
                ", timeRange='" + timeRange + '\'' +
                ", defaultPoint=" + defaultPoint +
                ", successPoint=" + successPoint +
                '}';
    }
}
