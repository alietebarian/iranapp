package com.ideabonyan.iranapp.Utils;

import java.util.Locale;

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
