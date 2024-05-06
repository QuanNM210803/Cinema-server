package com.example.cinemaserver.payment.zalopay.config;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ZaloPayConfig {
    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    public static String hmacSha256(String key, String data) {
        return Hex.encodeHexString(HmacUtils.hmacSha256(key.getBytes(), data.getBytes()));
    }
}
