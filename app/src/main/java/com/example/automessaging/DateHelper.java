package com.example.automessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateHelper {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private Long todaysTimestamp = new Date().getTime();

    public Long daysDifferenceFrom(Long otherDay) {
        return TimeUnit.DAYS.convert(otherDay - todaysTimestamp, TimeUnit.MILLISECONDS);
    }

    public Long toTimestamp(String date) throws ParseException{
        return formatter.parse(date).getTime();
    }

    public String toDate(Long time) {
        return formatter.format(time);
    }
}
