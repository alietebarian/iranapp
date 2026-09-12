package com.ideabonyan.iranapp.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.ideabonyan.iranapp.R;

/**
 * Maps category and sub category names onto the icons of the app, and draws the glossy plate
 * each icon sits on.
 *
 * Categories and sub categories are created by an admin on the server, which may attach any
 * thumbnail to them or none at all. To keep the icons consistent, every name that matches a known
 * domain is drawn with the matching vector (ic_cat_* for categories, ic_sub_* for the finer sub
 * category icons) and the server thumbnail is only a fallback. The keyword tables below were
 * written against the category tree actually served by iranapp.biz.
 */
public final class CategoryIcons {

    /** Icon + accent colour shared by one family of names. */
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
     * Categories of the landing page. Matched top to bottom against the normalized name, so the
     * narrower families come first: "بیمه" before the banks, "خدمات عروس" / "خدمات شهری" /
     * "خدمات سفر" before the generic services row, "مواد غذایی" before restaurants.
     */
    private static final Style[] STYLES = {
            new Style(R.drawable.ic_sub_shield, R.color.cat_insurance,
                    "بیمه"),
            new Style(R.drawable.ic_sub_building, R.color.cat_civic,
                    "خدماتشهری", "شهرداری", "ادارات"),
            new Style(R.drawable.ic_sub_rings, R.color.cat_wedding,
                    "تشریفات", "عروس", "تالار", "عقد"),
            new Style(R.drawable.ic_cat_health, R.color.cat_health,
                    "پزشک", "پزشکی", "دندان", "درمان", "کلینیک", "درمانگاه", "بیمارستان", "داروخانه",
                    "سلامت", "آزمایشگاه", "رادیولوژی", "بینایی", "عینک", "فیزیوتراپی", "پرستار",
                    "مامایی", "طبسنتی", "عطاری", "آمبولانس"),
            new Style(R.drawable.ic_cat_estate, R.color.cat_estate,
                    "املاک", "مسکن", "آپارتمان", "مستغلات", "رهنواجاره", "ویلا", "زمین",
                    "ساختمانی", "انبوهساز", "اجارهسوئیت"),
            new Style(R.drawable.ic_cat_car, R.color.cat_car,
                    "خودرو", "اتومبیل", "وسایلنقلیه", "موتورسیکلت", "لوازمیدکی", "تعمیرگاه",
                    "اتوسرویس", "کارواش", "باتری", "تایر", "صافکاری", "نقاشیاتومبیل", "اتوگالری",
                    "دوچرخه"),
            new Style(R.drawable.ic_cat_beauty, R.color.cat_beauty,
                    "آرایش", "آرایشگاه", "زیبایی", "پیرایش", "اپیلاسیون", "کاشتناخن", "سولاریوم"),
            new Style(R.drawable.ic_cat_store, R.color.cat_store,
                    "فروشگاه", "سوپرمارکت", "هایپر", "خواربار", "موادغذایی", "بقالی", "میوه",
                    "پروتئین", "قصابی", "لبنیات", "آجیل", "خشکبار", "عمدهفروشی", "خردهفروشی",
                    "مغازه", "بازار"),
            new Style(R.drawable.ic_cat_restaurant, R.color.cat_restaurant,
                    "رستوران", "غذا", "فستفود", "کافه", "قهوه", "چایخانه", "کافیشاپ", "کبابی",
                    "پیتزا", "ساندویچ", "بیرونبر", "آشپز", "سفرهخانه", "طباخی", "جگرکی",
                    "مرغسوخاری", "قنادی", "شیرینی", "نانوایی", "بستنی"),
            new Style(R.drawable.ic_cat_education, R.color.cat_education,
                    "آموزش", "آموزشگاه", "مدرسه", "دانشگاه", "کلاس", "زبان", "کنکور", "مهدکودک",
                    "تدریس", "دبستان", "دبیرستان", "حوزه", "قرآن"),
            new Style(R.drawable.ic_cat_job, R.color.cat_job,
                    "استخدام", "شغل", "کاریابی", "نیرویکار", "کسبوکار", "اداری", "شرکت", "دفترکار"),
            new Style(R.drawable.ic_sub_laptop, R.color.cat_computer,
                    "کامپیوتر", "رایانه", "لپتاپ"),
            new Style(R.drawable.ic_cat_digital, R.color.cat_digital,
                    "موبایل", "دیجیتال", "الکترونیک", "تلفن", "سختافزار", "نرمافزار", "شبکه",
                    "مداربسته", "ماهواره", "اینترنت"),
            new Style(R.drawable.ic_cat_finance, R.color.cat_finance,
                    "بانک", "مالی", "حسابدار", "حسابداری", "وام", "صرافی", "ارز", "سرمایه",
                    "بورس", "لیزینگ", "قرضالحسنه", "اعتباری"),
            new Style(R.drawable.ic_cat_travel, R.color.cat_travel,
                    "هتل", "توریست", "تورمسافرتی", "گردشگری", "سفر", "اقامت", "مسافرتی", "بلیط",
                    "سوئیت", "زیارت", "مهمانپذیر", "اقامتگاه"),
            new Style(R.drawable.ic_cat_transport, R.color.cat_transport,
                    "حملونقل", "باربری", "اسبابکشی", "ترابری", "پست", "تاکسی", "آژانس", "پیک", "کامیون"),
            new Style(R.drawable.ic_sub_bag, R.color.cat_mall,
                    "مرکزخرید", "مراکزخرید", "پاساژ", "مراکزتجاری"),
            new Style(R.drawable.ic_sub_ferris, R.color.cat_fun,
                    "سرگرمی", "فراغت", "تفریح", "شهربازی"),
            new Style(R.drawable.ic_cat_apparel, R.color.cat_apparel,
                    "پوشاک", "لباس", "بوتیک", "کفش", "کیف", "خیاط", "منسوجات", "پارچه", "چرم",
                    "خرازی", "مزون"),
            new Style(R.drawable.ic_cat_jewelry, R.color.cat_jewelry,
                    "طلا", "جواهر", "نقره", "ساعت", "زیورآلات", "بدلیجات"),
            new Style(R.drawable.ic_cat_media, R.color.cat_media,
                    "چاپ", "تبلیغات", "عکاسی", "فیلمبرداری", "گرافیک", "طراحی", "بنر", "تایپ",
                    "رسانه", "استودیو", "تابلوساز", "آتلیه"),
            new Style(R.drawable.ic_sub_pen, R.color.cat_stationery,
                    "لوازمالتحریر", "لوازمتحریر", "نوشتافزار"),
            new Style(R.drawable.ic_cat_book, R.color.cat_book,
                    "کتاب", "فرهنگ", "موسیقی", "نشر", "صحافی"),
            new Style(R.drawable.ic_sub_palette, R.color.cat_art,
                    "صنایعدستی", "هنر", "گالری"),
            new Style(R.drawable.ic_cat_sport, R.color.cat_sport,
                    "ورزش", "باشگاه", "بدنسازی", "ورزشی", "استخر", "تناسباندام", "رزمی"),
            new Style(R.drawable.ic_cat_furniture, R.color.cat_furniture,
                    "مبل", "دکور", "دکوراسیون", "لوازمخانگی", "فرش", "آشپزخانه", "پرده", "کابینت",
                    "چوب", "لوستر", "موکت"),
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

    /**
     * Sub categories, shown after tapping a category. Finer than the category families (a tooth
     * for "دندانپزشکی", a key for "اجاره"...); a sub category that matches none of these borrows
     * the style of its parent category. Ordered so the narrower names win: banks before "مسکن" /
     * "گردشگری" (بانک مسکن، بانک گردشگری), "پارکینگ" before "پارک", "دامپزشکی" and "چشم پزشکی"
     * before the generic doctor row, service before car so repairs get the wrench.
     */
    private static final Style[] SUB_STYLES = {
            new Style(R.drawable.ic_cat_finance, R.color.cat_finance,
                    "بانک", "قرضالحسنه", "اعتباری", "صرافی"),
            new Style(R.drawable.ic_sub_shield, R.color.cat_insurance,
                    "بیمه"),
            new Style(R.drawable.ic_sub_shield, R.color.cat_car,
                    "پلیس", "کلانتری", "راهنماییورانندگی", "حفاظتی", "امنیتی"),
            new Style(R.drawable.ic_sub_rings, R.color.cat_wedding,
                    "عروس", "تالار", "عقد"),
            new Style(R.drawable.ic_sub_perfume, R.color.cat_beauty,
                    "عطر", "لوازمآرایشی", "شوینده", "شیمیایی", "بهداشتی"),
            new Style(R.drawable.ic_cat_beauty, R.color.cat_beauty,
                    "آرایشگاه", "آرایش", "پیرایش", "گریم", "زیبایی"),
            new Style(R.drawable.ic_sub_dentist, R.color.cat_health,
                    "دندان"),
            new Style(R.drawable.ic_sub_eye, R.color.cat_health,
                    "چشم", "عینک", "بینایی"),
            new Style(R.drawable.ic_sub_hearing, R.color.cat_health,
                    "شنوایی", "سمعک"),
            new Style(R.drawable.ic_sub_lab, R.color.cat_health,
                    "آزمایشگاه"),
            new Style(R.drawable.ic_sub_pets, R.color.cat_eco,
                    "دامپزشک", "حیوانخانگی", "پتشاپ"),
            new Style(R.drawable.ic_sub_body, R.color.cat_health,
                    "فیزیوتراپی", "ماساژ", "یوگا"),
            new Style(R.drawable.ic_sub_chat, R.color.cat_job,
                    "مشاوره", "اعصابوروان", "روانشناس"),
            new Style(R.drawable.ic_sub_gamepad, R.color.cat_computer,
                    "بازیهایکامپیوتری", "بازیکامپیوتری", "کنسول"),
            new Style(R.drawable.ic_sub_ferris, R.color.cat_fun,
                    "شهربازی", "تفریحی", "سرگرمی"),
            new Style(R.drawable.ic_sub_kids, R.color.cat_fun,
                    "کودک", "بچه", "سیسمونی", "نوزاد", "اسباببازی", "خانهبازی"),
            new Style(R.drawable.ic_cat_health, R.color.cat_health,
                    "پزشک", "بیمارستان", "کلینیک", "درمان", "تصویربرداری", "زنانوزایمان", "جراح",
                    "تغذیه", "آمبولانس", "طبسوزنی"),
            new Style(R.drawable.ic_sub_movie, R.color.cat_media,
                    "سینما"),
            new Style(R.drawable.ic_sub_bed, R.color.cat_travel,
                    "هتل", "مسافرخانه", "اقامتگاه", "کالایخواب"),
            new Style(R.drawable.ic_sub_mosque, R.color.cat_estate,
                    "مسجد", "مساجد", "زیارت"),
            new Style(R.drawable.ic_cat_travel, R.color.cat_travel,
                    "هواپیما", "مسافرتی", "گردشگری", "بلیط"),
            new Style(R.drawable.ic_cat_finance, R.color.cat_legal,
                    "موزه", "تاریخی"),
            new Style(R.drawable.ic_sub_parking, R.color.cat_car,
                    "پارکینگ"),
            new Style(R.drawable.ic_sub_tree, R.color.cat_eco,
                    "پارک", "فضایسبز"),
            new Style(R.drawable.ic_sub_bus, R.color.cat_transport,
                    "ترمینال", "اتوبوس"),
            new Style(R.drawable.ic_sub_fuel, R.color.cat_transport,
                    "پمپبنزین", "بنزین", "CNG", "سیانجی"),
            new Style(R.drawable.ic_sub_card, R.color.cat_digital,
                    "شارژ", "کارت"),
            new Style(R.drawable.ic_sub_mail, R.color.cat_restaurant,
                    "پست"),
            new Style(R.drawable.ic_cat_legal, R.color.cat_legal,
                    "دادگاه", "دادگستری", "حلاختلاف", "عریضه", "حقوقی", "حسابداری", "دفترخانه", "ثبت"),
            new Style(R.drawable.ic_sub_bag, R.color.cat_mall,
                    "مراکزتجاری", "مرکزخرید", "مراکزخرید", "پاساژ", "کیفوکفش"),
            new Style(R.drawable.ic_sub_print, R.color.cat_stationery,
                    "تایپ", "تکثیر", "چاپ", "ماشینهایاداری"),
            new Style(R.drawable.ic_sub_building, R.color.cat_civic,
                    "شهرداری", "ادارات", "اداره", "اتحادیه", "پیشخوان", "اداری", "تجاری",
                    "مشارکتدرساخت", "پیشفروش"),
            new Style(R.drawable.ic_sub_key, R.color.cat_jewelry,
                    "اجاره"),
            new Style(R.drawable.ic_cat_estate, R.color.cat_estate,
                    "مسکونی", "املاک", "آپارتمان", "ویلا", "مسکن"),
            new Style(R.drawable.ic_cat_book, R.color.cat_book,
                    "کتاب", "فرهنگسرا", "صحافی", "نشر"),
            new Style(R.drawable.ic_sub_pen, R.color.cat_stationery,
                    "نوشتافزار", "لوازمالتحریر", "لوازمتحریر"),
            new Style(R.drawable.ic_sub_palette, R.color.cat_art,
                    "هنری", "هنر", "صنایعدستی"),
            new Style(R.drawable.ic_sub_laptop, R.color.cat_computer,
                    "کامپیوتر", "رایانه", "لپتاپ"),
            new Style(R.drawable.ic_cat_digital, R.color.cat_digital,
                    "موبایل", "تلفن", "لوازمجانبی"),
            new Style(R.drawable.ic_sub_bolt, R.color.cat_jewelry,
                    "برق", "الکترونیک"),
            new Style(R.drawable.ic_sub_waves, R.color.cat_travel,
                    "استخر"),
            new Style(R.drawable.ic_cat_sport, R.color.cat_sport,
                    "باشگاه", "بدنسازی", "ورزش"),
            new Style(R.drawable.ic_sub_cafe, R.color.cat_furniture,
                    "کافیشاپ", "کافه", "قهوه", "چای", "سفرهخانه", "چایخانه"),
            new Style(R.drawable.ic_sub_burger, R.color.cat_restaurant,
                    "فستفود", "ساندویچ", "پیتزا", "برگر"),
            new Style(R.drawable.ic_sub_soup, R.color.cat_restaurant,
                    "آشوحلیم", "حلیم", "کلهپزی", "جگر", "دلوقلوه"),
            new Style(R.drawable.ic_sub_icecream, R.color.cat_beauty,
                    "بستنی", "آبمیوه"),
            new Style(R.drawable.ic_sub_bakery, R.color.cat_wedding,
                    "شیرینی", "قنادی", "نانوایی", "نانفانتزی", "کیک"),
            new Style(R.drawable.ic_cat_restaurant, R.color.cat_restaurant,
                    "رستوران", "کباب", "بریان", "طباخی", "بیرونبر", "آشپز", "ظروف"),
            new Style(R.drawable.ic_sub_fish, R.color.cat_estate,
                    "ماهی"),
            new Style(R.drawable.ic_sub_apple, R.color.cat_store,
                    "میوه", "سبزی", "صیفی"),
            new Style(R.drawable.ic_sub_cart, R.color.cat_store,
                    "سوپرمارکت", "هایپر", "عمدهفروشی", "خواربار", "فروشگاه", "پروتئین", "گوشت",
                    "لبنیات", "آجیل", "خشکبار", "فرآورده", "عسل"),
            new Style(R.drawable.ic_cat_job, R.color.cat_job,
                    "استخدام"),
            new Style(R.drawable.ic_sub_person, R.color.cat_job,
                    "جویایکار", "کارجو"),
            new Style(R.drawable.ic_sub_flower, R.color.cat_beauty,
                    "گلفروشی", "گلوگیاه"),
            new Style(R.drawable.ic_cat_eco, R.color.cat_eco,
                    "عطاری", "گیاه", "بذر", "کود", "کشاورزی", "دام", "طیور"),
            new Style(R.drawable.ic_sub_drop, R.color.cat_travel,
                    "کارواش", "قالیشویی", "تعویضروغن", "شیرآلات", "لوله", "شستشو"),
            new Style(R.drawable.ic_sub_battery, R.color.cat_sport,
                    "باطری", "باتری"),
            new Style(R.drawable.ic_sub_tire, R.color.cat_service,
                    "رینگ", "لاستیک", "تایر"),
            new Style(R.drawable.ic_cat_transport, R.color.cat_transport,
                    "یدککش", "حملونقل", "باربری", "سنگین", "کامیون", "اسبابکشی"),
            new Style(R.drawable.ic_cat_service, R.color.cat_service,
                    "تعمیر", "تنظیمموتور", "صافکاری", "ابزار", "تراشکاری", "شیشهبری", "خدمات"),
            new Style(R.drawable.ic_cat_industry, R.color.cat_industry,
                    "صنعتی", "صنعت", "کارخانه"),
            new Style(R.drawable.ic_cat_car, R.color.cat_car,
                    "خودرو", "اتومبیل", "موتورسیکلت", "لوازمیدکی", "اسپرت", "نمایشگاه"),
            new Style(R.drawable.ic_sub_fridge, R.color.cat_car,
                    "لوازمخانگی", "یخچال", "لباسشویی"),
            new Style(R.drawable.ic_cat_furniture, R.color.cat_furniture,
                    "مبل", "فرش", "دکوراسیون", "پرده", "کابینت"),
            new Style(R.drawable.ic_cat_apparel, R.color.cat_apparel,
                    "پوشاک", "لباس", "خیاطی", "مانتو", "شال", "روسری", "کلاه", "چرم", "بوتیک",
                    "خرازی", "حوله", "رومیزی", "مزون"),
            new Style(R.drawable.ic_sub_watch, R.color.cat_digital,
                    "ساعت"),
            new Style(R.drawable.ic_cat_jewelry, R.color.cat_jewelry,
                    "طلا", "جواهر", "نقره", "زیورآلات"),
            new Style(R.drawable.ic_cat_media, R.color.cat_media,
                    "آتلیه", "عکاسی", "فیلمبرداری"),
            new Style(R.drawable.ic_cat_education, R.color.cat_education,
                    "آموزش", "آموزشگاه", "دانشگاه", "مدرسه", "زبان", "دروس"),
    };

    static {
        // Names are normalized before matching, so the keywords have to be normalized too --
        // otherwise "آرایشگاه" could never match a keyword still spelled "آرایش".
        for (Style[] table : new Style[][]{STYLES, SUB_STYLES}) {
            for (Style style : table) {
                for (int i = 0; i < style.keywords.length; i++) {
                    style.keywords[i] = normalize(style.keywords[i]);
                }
            }
        }
    }

    /** How far the plate gradient runs toward white at its top-left and black at its bottom-right. */
    private static final float GRADIENT_LIGHTEN = 0.28f;
    private static final float GRADIENT_DARKEN = 0.12f;
    /** Highlight over the top half of the plate. */
    private static final int GLOSS_COLOR = 0x47FFFFFF;
    /** Corner radius of the plate, as a fraction of its size. */
    private static final float PLATE_ROUNDNESS = 0.3f;

    private CategoryIcons() {
    }

    /** The style of a category, or null when the name belongs to no known family. */
    public static Style of(String name) {
        return match(STYLES, name);
    }

    /**
     * The style of a sub category: its own icon when the name says what it is, otherwise the
     * style of the category it was opened from, otherwise {@link #DEFAULT}.
     */
    public static Style ofSub(String name, Style parent) {
        Style own = match(SUB_STYLES, name);
        if (own != null) return own;
        return parent != null ? parent : DEFAULT;
    }

    private static Style match(Style[] table, String name) {
        if (name == null) return null;
        String normalized = normalize(name);
        if (normalized.isEmpty()) return null;

        for (Style style : table) {
            for (String keyword : style.keywords) {
                if (normalized.contains(keyword)) return style;
            }
        }
        return null;
    }

    public static int accent(Context context, Style style) {
        return ContextCompat.getColor(context, style == null ? DEFAULT.color : style.color);
    }

    /** Diagonal gradient of the accent with a soft highlight over its top half. */
    public static Drawable glossyPlate(int accent, int size) {
        float radius = size * PLATE_ROUNDNESS;

        GradientDrawable fill = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{
                ColorUtils.blendARGB(accent, Color.WHITE, GRADIENT_LIGHTEN),
                ColorUtils.blendARGB(accent, Color.BLACK, GRADIENT_DARKEN)});
        fill.setCornerRadius(radius);

        GradientDrawable gloss = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{GLOSS_COLOR, Color.TRANSPARENT});
        gloss.setCornerRadii(new float[]{radius, radius, radius, radius, 0, 0, 0, 0});

        LayerDrawable plate = new LayerDrawable(new Drawable[]{fill, gloss});
        plate.setLayerInsetBottom(1, size / 2);
        return plate;
    }

    /** A plain plate, for server thumbnails that must not be tinted. */
    public static Drawable flatPlate(int color, int size) {
        GradientDrawable plate = new GradientDrawable();
        plate.setColor(color);
        plate.setCornerRadius(size * PLATE_ROUNDNESS);
        return plate;
    }

    /** Sets the plate behind an icon; on Android 9+ its shadow glows in the given colour. */
    public static void applyPlate(View plate, Drawable background, int glow) {
        plate.setBackground(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            plate.setOutlineSpotShadowColor(glow);
            plate.setOutlineAmbientShadowColor(glow);
        }
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
