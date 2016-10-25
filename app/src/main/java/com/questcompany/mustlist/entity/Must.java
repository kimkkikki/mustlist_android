package com.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 6..
 * preview result object
 */
public class Must {
    private String name;
    private Integer amount;
    private String checkTimeRange;
    private Integer defaultPoint;
    private Integer successPoint;

    private String startDate;
    private String endDate;

    public Must() {
    }

    public Must(String name, String startDay, String period, Integer amount, String checkTimeRange) {
        this.name = name;
        this.amount = amount;
        this.checkTimeRange = checkTimeRange;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getCheckTimeRange() {
        return checkTimeRange;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public void setCheckTimeRange(String checkTimeRange) {
        this.checkTimeRange = checkTimeRange;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Must{" +
                "name='" + name + '\'' +
                ", amount='" + amount + '\'' +
                ", checkTimeRange='" + checkTimeRange + '\'' +
                ", defaultPoint=" + defaultPoint +
                ", successPoint=" + successPoint +
                '}';
    }
}
