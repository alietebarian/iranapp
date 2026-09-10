package com.ideabonyan.iranapp.Utils;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.ideabonyan.iranapp.R;

/**
 * Maps a category name onto one of the icons of the app.
 *
 * Categories are created by an admin on the server, which may attach any thumbnail to them or
 * none at all. To keep the landing page consistent, every category whose name matches a known
 * business domain is drawn with the matching ic_cat_* vector tinted with the accent of that
 * domain, and the server thumbnail is only a fallback.
 */
public final class CategoryIcons {

    /** Icon + accent colour shared by one family of categories. */
    public static final class Style {
        public final int icon;
        public final int color;
        private final String[] keywords;

        private Style(int icon, int color, String... keywords) {
            this.icon = icon;
            this.color = color;
            this.keywords = keywords;
        }
    }

    /** Used when a name matches no family at all. */
    public static final Style DEFAULT = new Style(R.drawable.ic_cat_default, R.color.cat_default);

    /**
     * Matched top to bottom against the normalized name, so the narrower families come first:
     * "خدمات خودرو" has to reach the car row before the generic services one, and "آژانس مسافرتی"
     * the travel row before the taxi one.
     */
    private static final Style[] STYLES = {
            new Style(R.drawable.ic_cat_health, R.color.cat_health,
                    "پزشک", "پزشکی", "دندان", "درمان", "کلینیک", "درمانگاه", "بیمارستان", "داروخانه",
                    "سلامت", "آزمایشگاه", "رادیولوژی", "بینایی", "عینک", "فیزیوتراپی", "پرستار",
                    "مامایی", "طبسنتی", "عطاری"),
            new Style(R.drawable.ic_cat_estate, R.color.cat_estate,
                    "املاک", "مسکن", "آپارتمان", "مستغلات", "رهنواجاره", "ویلا", "زمین",
                    "ساختمانی", "انبوهساز", "اجارهسوئیت"),
            new Style(R.drawable.ic_cat_car, R.color.cat_car,
                    "خودرو", "اتومبیل", "موتورسیکلت", "لوازمیدکی", "تعمیرگاه", "اتوسرویس",
                    "کارواش", "باتری", "لاستیک", "صافکاری", "نقاشیاتومبیل", "اتوگالری", "دوچرخه"),
            new Style(R.drawable.ic_cat_beauty, R.color.cat_beauty,
                    "آرایش", "آرایشگاه", "زیبایی", "پیرایش", "اپیلاسیون", "عروس", "کاشتناخن", "سولاریوم"),
            new Style(R.drawable.ic_cat_restaurant, R.color.cat_restaurant,
                    "رستوران", "غذا", "فستفود", "کافه", "قهوه", "چایخانه", "کافیشاپ", "کبابی",
                    "پیتزا", "ساندویچ", "بیرونبر", "آشپز", "تالار", "تشریفات", "سفرهخانه", "طباخی",
                    "جگرکی", "مرغسوخاری", "قنادی", "شیرینی", "نانوایی", "بستنی", "آبمیوه"),
            new Style(R.drawable.ic_cat_store, R.color.cat_store,
                    "فروشگاه", "سوپرمارکت", "هایپر", "خواربار", "موادغذایی", "بقالی", "میوه",
                    "پروتئین", "قصابی", "لبنیات", "آجیل", "خشکبار", "عمدهفروشی", "خردهفروشی",
                    "مغازه", "بازار"),
            new Style(R.drawable.ic_cat_education, R.color.cat_education,
                    "آموزش", "آموزشگاه", "مدرسه", "دانشگاه", "کلاس", "زبان", "کنکور", "مهدکودک",
                    "تدریس", "دبستان", "دبیرستان", "حوزه", "قرآن"),
            new Style(R.drawable.ic_cat_job, R.color.cat_job,
                    "استخدام", "شغل", "کاریابی", "نیرویکار", "کسبوکار", "اداری", "شرکت", "دفترکار"),
            new Style(R.drawable.ic_cat_digital, R.color.cat_digital,
                    "موبایل", "دیجیتال", "کامپیوتر", "رایانه", "لپتاپ", "الکترونیک", "تلفنهمراه",
                    "سختافزار", "نرمافزار", "شبکه", "مداربسته", "ماهواره", "اینترنت", "بازیکنسول"),
            new Style(R.drawable.ic_cat_finance, R.color.cat_finance,
                    "بانک", "بیمه", "مالی", "حسابدار", "حسابداری", "وام", "صرافی", "ارز", "سرمایه",
                    "بورس", "لیزینگ", "قرضالحسنه"),
            new Style(R.drawable.ic_cat_travel, R.color.cat_travel,
                    "هتل", "توریست", "تورمسافرتی", "گردشگری", "سفر", "اقامت", "مسافرتی", "بلیط",
                    "سوئیت", "زیارت", "مهمانپذیر", "اقامتگاه"),
            new Style(R.drawable.ic_cat_transport, R.color.cat_transport,
                    "حملونقل", "باربری", "اسبابکشی", "ترابری", "پست", "تاکسی", "آژانس", "پیک", "کامیون"),
            new Style(R.drawable.ic_cat_apparel, R.color.cat_apparel,
                    "پوشاک", "لباس", "بوتیک", "کفش", "کیف", "خیاط", "منسوجات", "پارچه", "چرم", "خرازی"),
            new Style(R.drawable.ic_cat_jewelry, R.color.cat_jewelry,
                    "طلا", "جواهر", "نقره", "ساعت", "زیورآلات", "بدلیجات"),
            new Style(R.drawable.ic_cat_media, R.color.cat_media,
                    "چاپ", "تبلیغات", "عکاسی", "فیلمبرداری", "گرافیک", "طراحی", "بنر", "تایپ",
                    "رسانه", "استودیو", "تابلوساز", "مزون"),
            new Style(R.drawable.ic_cat_book, R.color.cat_book,
                    "کتاب", "فرهنگ", "هنر", "موسیقی", "نوشتافزار", "لوازمتحریر", "نشر", "صحافی", "گالری"),
            new Style(R.drawable.ic_cat_sport, R.color.cat_sport,
                    "ورزش", "باشگاه", "بدنسازی", "ورزشی", "استخر", "تناسباندام", "رزمی"),
            new Style(R.drawable.ic_cat_furniture, R.color.cat_furniture,
                    "مبل", "دکور", "دکوراسیون", "لوازمخانگی", "فرش", "آشپزخانه", "پرده", "کابینت",
                    "چوب", "صنایعدستی", "لوستر", "موکت"),
            new Style(R.drawable.ic_cat_eco, R.color.cat_eco,
                    "گل", "گیاه", "باغ", "فضایسبز", "کشاورزی", "دام", "طیور", "حیوان", "دامپزشک",
                    "نهال", "سمپاشی"),
            new Style(R.drawable.ic_cat_legal, R.color.cat_legal,
                    "وکیل", "وکالت", "حقوق", "قضایی", "ثبت", "دفترخانه", "مشاوره", "ترجمه",
                    "دارالترجمه", "کارشناسرسمی"),
            new Style(R.drawable.ic_cat_industry, R.color.cat_industry,
                    "صنعت", "صنعتی", "کارخانه", "تولید", "ماشینآلات", "قطعات", "فلز", "جوش",
                    "پلاستیک", "ریختهگری", "معدن"),
            new Style(R.drawable.ic_cat_service, R.color.cat_service,
                    "خدمات", "تاسیسات", "فنی", "تعمیر", "نصب", "برق", "لوله", "نقاشی", "بنایی",
                    "جوشکاری", "کولر", "آسانسور", "نظافت", "سرویس", "شیشه", "کلیدسازی", "قفلساز"),
    };

    static {
        // Names are normalized before matching, so the keywords have to be normalized too --
        // otherwise "آرایشگاه" could never match a keyword still spelled "آرایش".
        for (Style style : STYLES) {
            for (int i = 0; i < style.keywords.length; i++) {
                style.keywords[i] = normalize(style.keywords[i]);
            }
        }
    }

    /** Opacity of the plate drawn behind each icon, over the white card. */
    private static final int PLATE_ALPHA = 0x1F;

    private CategoryIcons() {
    }

    /** The style matching this name, or null when the name belongs to no known family. */
    public static Style of(String name) {
        if (name == null) return null;
        String normalized = normalize(name);
        if (normalized.isEmpty()) return null;

        for (Style style : STYLES) {
            for (String keyword : style.keywords) {
                if (normalized.contains(keyword)) return style;
            }
        }
        return null;
    }

    public static int accent(Context context, Style style) {
        return ContextCompat.getColor(context, style == null ? DEFAULT.color : style.color);
    }

    /** The accent, faded down so it reads as a soft plate rather than a second icon. */
    public static int plate(int accent) {
        return Color.argb(PLATE_ALPHA, Color.red(accent), Color.green(accent), Color.blue(accent));
    }

    /**
     * Names are typed by hand in the admin panel, so they mix Arabic and Persian letter shapes and
     * stray spaces / half spaces. Fold those away before matching keywords.
     */
    public static String normalize(String name) {
        StringBuilder sb = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            switch (c) {
                case 'ي': // arabic yeh
                    sb.append('ی');
                    break;
                case 'ك': // arabic kaf
                    sb.append('ک');
                    break;
                case 'ة': // teh marbuta
                    sb.append('ه');
                    break;
                case 'أ':
                case 'إ':
                case 'آ':
                    sb.append('ا');
                    break;
                case '‌': // zero width non joiner
                case 'ً': case 'ٌ': case 'ٍ': // tanvin
                case 'َ': case 'ُ': case 'ِ': // harakat
                case 'ّ': case 'ْ':
                    break;
                default:
                    if (!Character.isWhitespace(c)) sb.append(c);
            }
        }
        return sb.toString();
    }
}
