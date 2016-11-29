package io.questcompany.mustlist.entity;

/**
 * Created by kimkkikki on 2016. 10. 6..
 * preview result object
 */
public class Must {
    private Integer index;
    private String name;
    private Integer amount;
    private Integer defaultPoint;
    private Integer successPoint;

    private Integer startDate;
    private Integer endDate;

    private String developerPayload;

    public Must() {
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
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

    public void setAmount(Integer amount) {
        this.amount = amount;
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

    public Integer getStartDate() {
        return startDate;
    }

    public void setStartDate(Integer startDate) {
        this.startDate = startDate;
    }

    public Integer getEndDate() {
        return endDate;
    }

    public void setEndDate(Integer endDate) {
        this.endDate = endDate;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    @Override
    public String toString() {
        return "Must{" +
                "index=" + index +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", defaultPoint=" + defaultPoint +
                ", successPoint=" + successPoint +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", developerPayload='" + developerPayload + '\'' +
                '}';
    }
}
