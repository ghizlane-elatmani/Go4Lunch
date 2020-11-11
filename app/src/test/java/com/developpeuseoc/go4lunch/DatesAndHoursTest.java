package com.developpeuseoc.go4lunch;

import com.developpeuseoc.go4lunch.utils.DatesAndHours;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class DatesAndHoursTest {
    @Test
    public void convertDateToHourTest() {

        DateFormat dfTime = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String actualHour = DatesAndHours.convertDateToHour(date);
        String newHour = dfTime.format(new Date());
        assertEquals(newHour,actualHour );

    }
    @Test
    public void convertStringToHoursTest() {

        String hour1 = "20";
        String hour2 = "00";
        assertEquals("20:00", DatesAndHours.convertStringToHours(hour1 + hour2));

    }
}
