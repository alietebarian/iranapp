package com.ideabonyan.iranapp.Utils;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Solar Hijri dates as {year, month, day}, written "1405/07/04".
 *
 * Mirrors App\Support\JalaliDate on the server. The app uses it only to offer a date picker and to
 * preview the contract's end date; the server recomputes and stores the real end date.
 */
public class JalaliDate {

    public static final String[] MONTH_NAMES = {
            "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    };

    /** Parses "1405/7/4" (Latin or Persian digits); null when it is not a real date. */
    public static int[] parse(String text) {
        if (text == null) return null;
        String[] parts = toLatinDigits(text.trim()).split("[/\\-]");
        if (parts.length != 3) return null;
        try {
            int y = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            int d = Integer.parseInt(parts[2]);
            if (y < 1000 || m < 1 || m > 12 || d < 1 || d > monthLength(y, m)) return null;
            return new int[]{y, m, d};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Today in Tehran as {year, month, day}. */
    public static int[] today() {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
        return fromGregorian(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
    }

    /** The same conversion as jdf's gregorian_to_jalali on the server. */
    public static int[] fromGregorian(int gy, int gm, int gd) {
        int[] daysBeforeMonth = {0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
        int gy2 = gm > 2 ? gy + 1 : gy;
        int days = 355666 + 365 * gy + (gy2 + 3) / 4 - (gy2 + 99) / 100 + (gy2 + 399) / 400
                + gd + daysBeforeMonth[gm - 1];
        int jy = -1595 + 33 * (days / 12053);
        days %= 12053;
        jy += 4 * (days / 1461);
        days %= 1461;
        if (days > 365) {
            jy += (days - 1) / 365;
            days = (days - 1) % 365;
        }
        if (days < 186) return new int[]{jy, 1 + days / 31, 1 + days % 31};
        return new int[]{jy, 7 + (days - 186) / 30, 1 + (days - 186) % 30};
    }

    /** Negative, zero or positive as a is before, on or after b. */
    public static int compare(int[] a, int[] b) {
        for (int i = 0; i < 3; i++) if (a[i] != b[i]) return Integer.compare(a[i], b[i]);
        return 0;
    }

    public static String format(int[] date) {
        return String.format(Locale.US, "%04d/%02d/%02d", date[0], date[1], date[2]);
    }

    /** Adds whole months, clamping the day to the target month's length (6/31 + 1 month → 7/30). */
    public static int[] addMonths(int[] date, int months) {
        int index = (date[1] - 1) + months;
        int y = date[0] + index / 12;
        int m = index % 12 + 1;
        return new int[]{y, m, Math.min(date[2], monthLength(y, m))};
    }

    public static int monthLength(int year, int month) {
        if (month <= 6) return 31;
        if (month <= 11) return 30;
        return isLeap(year) ? 30 : 29;
    }

    /** The same 33-year-cycle rule as jdf's jcheckdate on the server. */
    public static boolean isLeap(int year) {
        return (((year % 33) % 4) - 1) == (int) ((year % 33) * 0.05);
    }

    public static String toLatinDigits(String s) {
        StringBuilder out = new StringBuilder(s.length());
        for (char c : s.toCharArray()) {
            if (c >= '۰' && c <= '۹') out.append((char) ('0' + (c - '۰')));
            else if (c >= '٠' && c <= '٩') out.append((char) ('0' + (c - '٠')));
            else out.append(c);
        }
        return out.toString();
    }
}
