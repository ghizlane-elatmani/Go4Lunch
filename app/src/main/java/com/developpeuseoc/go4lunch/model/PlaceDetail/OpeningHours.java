package com.developpeuseoc.go4lunch.model.PlaceDetail;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OpeningHours implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("open_now")
    public Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    /** Opening periods covering seven days, starting from Sunday, in chronological order. */
    @SerializedName("periods")
    private List<Period> mPeriods;
    public List<Period> getPeriods() {
        return mPeriods;
    }
    public void setPeriods(List<Period> periods) {
        mPeriods = periods;
    }

    /**
     * The formatted opening hours for each day of the week, as an array of seven strings; for
     * example, {@code "Monday: 8:30 am – 5:30 pm"}.
     */
    @SerializedName("weekday_text")
    public String[] weekdayText;
    public String []getWeekdayText() {
        return weekdayText;
    }
    /**
     * Indicates that the place has permanently shut down.
     *
     * <p>Note: this field will be null if it isn't present in the response.
     */
    public Boolean permanentlyClosed;
    public Boolean getPermanentlyClosed() {
        return permanentlyClosed;
    }


}
