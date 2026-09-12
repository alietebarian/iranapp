package com.ideabonyan.iranapp.Utils;

import com.ideabonyan.iranapp.R;

/**
 * Maps category and sub category names onto the icons of the app.
 *
 * Categories and sub categories are created by an admin on the server, which may attach any
 * thumbnail to them or none at all. To keep the icons consistent, every name that matches a known
 * domain is drawn with the matching line icon (ic_cat_* for categories, ic_sub_* for the finer sub
 * category icons) and the server thumbnail is only a fallback. The keyword tables below were
 * written against the category tree actually served by iranapp.biz.
 */
public final class CategoryIcons {

    /** The icon of one family of names, and the keywords that pick it. */
    public static final class Style {
        public final int icon;
        private final String[] keywords;

        private Style(int icon, String... keywords) {
            this.icon = icon;
            this.keywords = keywords;
        }
    }

    /** Used when a name matches no family at all. */
    public static final Style DEFAULT = new Style(R.drawable.ic_cat_default);

    /**
     * Categories of the landing page. Matched top to bottom against the normalized name, so the
     * narrower families come first: "بیمه" before the banks, "خدمات عروس" / "خدمات شهری" /
     * "خدمات سفر" before the generic services row, "مواد غذایی" before restaurants.
     */
    private static final Style[] STYLES = {
            new Style(R.drawable.ic_sub_shield,
                    "بیمه"),
            new Style(R.drawable.ic_sub_building,
                    "خدماتشهری", "شهرداری", "ادارات"),
            new Style(R.drawable.ic_sub_rings,
                    "تشریفات", "عروس", "تالار", "عقد"),
            new Style(R.drawable.ic_cat_health,
                    "پزشک", "پزشکی", "دندان", "درمان", "کلینیک", "درمانگاه", "بیمارستان", "داروخانه",
                    "سلامت", "آزمایشگاه", "رادیولوژی", "بینایی", "عینک", "فیزیوتراپی", "پرستار",
                    "مامایی", "طبسنتی", "عطاری", "آمبولانس"),
            new Style(R.drawable.ic_cat_estate,
                    "املاک", "مسکن", "آپارتمان", "مستغلات", "رهنواجاره", "ویلا", "زمین",
                    "ساختمانی", "انبوهساز", "اجارهسوئیت"),
            new Style(R.drawable.ic_cat_car,
                    "خودرو", "اتومبیل", "وسایلنقلیه", "موتورسیکلت", "لوازمیدکی", "تعمیرگاه",
                    "اتوسرویس", "کارواش", "باتری", "تایر", "صافکاری", "نقاشیاتومبیل", "اتوگالری",
                    "دوچرخه"),
            new Style(R.drawable.ic_cat_beauty,
                    "آرایش", "آرایشگاه", "زیبایی", "پیرایش", "اپیلاسیون", "کاشتناخن", "سولاریوم"),
            new Style(R.drawable.ic_cat_store,
                    "فروشگاه", "سوپرمارکت", "هایپر", "خواربار", "موادغذایی", "بقالی", "میوه",
                    "پروتئین", "قصابی", "لبنیات", "آجیل", "خشکبار", "عمدهفروشی", "خردهفروشی",
                    "مغازه", "بازار"),
            new Style(R.drawable.ic_cat_restaurant,
                    "رستوران", "غذا", "فستفود", "کافه", "قهوه", "چایخانه", "کافیشاپ", "کبابی",
                    "پیتزا", "ساندویچ", "بیرونبر", "آشپز", "سفرهخانه", "طباخی", "جگرکی",
                    "مرغسوخاری", "قنادی", "شیرینی", "نانوایی", "بستنی"),
            new Style(R.drawable.ic_cat_education,
                    "آموزش", "آموزشگاه", "مدرسه", "دانشگاه", "کلاس", "زبان", "کنکور", "مهدکودک",
                    "تدریس", "دبستان", "دبیرستان", "حوزه", "قرآن"),
            new Style(R.drawable.ic_cat_job,
                    "استخدام", "شغل", "کاریابی", "نیرویکار", "کسبوکار", "اداری", "شرکت", "دفترکار"),
            new Style(R.drawable.ic_sub_laptop,
                    "کامپیوتر", "رایانه", "لپتاپ"),
            new Style(R.drawable.ic_cat_digital,
                    "موبایل", "دیجیتال", "الکترونیک", "تلفن", "سختافزار", "نرمافزار", "شبکه",
                    "مداربسته", "ماهواره", "اینترنت"),
            new Style(R.drawable.ic_cat_finance,
                    "بانک", "مالی", "حسابدار", "حسابداری", "وام", "صرافی", "ارز", "سرمایه",
                    "بورس", "لیزینگ", "قرضالحسنه", "اعتباری"),
            new Style(R.drawable.ic_cat_travel,
                    "هتل", "توریست", "تورمسافرتی", "گردشگری", "سفر", "اقامت", "مسافرتی", "بلیط",
                    "سوئیت", "زیارت", "مهمانپذیر", "اقامتگاه"),
            new Style(R.drawable.ic_cat_transport,
                    "حملونقل", "باربری", "اسبابکشی", "ترابری", "پست", "تاکسی", "آژانس", "پیک", "کامیون"),
            new Style(R.drawable.ic_sub_bag,
                    "مرکزخرید", "مراکزخرید", "پاساژ", "مراکزتجاری"),
            new Style(R.drawable.ic_sub_ferris,
                    "سرگرمی", "فراغت", "تفریح", "شهربازی"),
            new Style(R.drawable.ic_cat_apparel,
                    "پوشاک", "لباس", "بوتیک", "کفش", "کیف", "خیاط", "منسوجات", "پارچه", "چرم",
                    "خرازی", "مزون"),
            new Style(R.drawable.ic_cat_jewelry,
                    "طلا", "جواهر", "نقره", "ساعت", "زیورآلات", "بدلیجات"),
            new Style(R.drawable.ic_cat_media,
                    "چاپ", "تبلیغات", "عکاسی", "فیلمبرداری", "گرافیک", "طراحی", "بنر", "تایپ",
                    "رسانه", "استودیو", "تابلوساز", "آتلیه"),
            new Style(R.drawable.ic_sub_pen,
                    "لوازمالتحریر", "لوازمتحریر", "نوشتافزار"),
            new Style(R.drawable.ic_cat_book,
                    "کتاب", "فرهنگ", "موسیقی", "نشر", "صحافی"),
            new Style(R.drawable.ic_sub_palette,
                    "صنایعدستی", "هنر", "گالری"),
            new Style(R.drawable.ic_cat_sport,
                    "ورزش", "باشگاه", "بدنسازی", "ورزشی", "استخر", "تناسباندام", "رزمی"),
            new Style(R.drawable.ic_cat_furniture,
                    "مبل", "دکور", "دکوراسیون", "لوازمخانگی", "فرش", "آشپزخانه", "پرده", "کابینت",
                    "چوب", "لوستر", "موکت"),
            new Style(R.drawable.ic_cat_eco,
                    "گل", "گیاه", "باغ", "فضایسبز", "کشاورزی", "دام", "طیور", "حیوان", "دامپزشک",
                    "نهال", "سمپاشی"),
            new Style(R.drawable.ic_cat_legal,
                    "وکیل", "وکالت", "حقوق", "قضایی", "ثبت", "دفترخانه", "مشاوره", "ترجمه",
                    "دارالترجمه", "کارشناسرسمی"),
            new Style(R.drawable.ic_cat_industry,
                    "صنعت", "صنعتی", "کارخانه", "تولید", "ماشینآلات", "قطعات", "فلز", "جوش",
                    "پلاستیک", "ریختهگری", "معدن"),
            new Style(R.drawable.ic_cat_service,
                    "خدمات", "تاسیسات", "فنی", "تعمیر", "نصب", "برق", "لوله", "نقاشی", "بنایی",
                    "جوشکاری", "کولر", "آسانسور", "نظافت", "سرویس", "شیشه", "کلیدسازی", "قفلساز"),
    };

    /**
     * Sub categories, shown after tapping a category. Finer than the category families (a tooth
     * for "دندانپزشکی", a key for "اجاره"...); a sub category that matches none of these borrows
     * the icon of its parent category. Ordered so the narrower names win: banks before "مسکن" /
     * "گردشگری" (بانک مسکن، بانک گردشگری), "پارکینگ" before "پارک", "دامپزشکی" and "چشم پزشکی"
     * before the generic doctor row, service before car so repairs get the wrench.
     */
    private static final Style[] SUB_STYLES = {
            new Style(R.drawable.ic_cat_finance,
                    "بانک", "قرضالحسنه", "اعتباری", "صرافی"),
            new Style(R.drawable.ic_sub_shield,
                    "بیمه", "پلیس", "کلانتری", "راهنماییورانندگی", "حفاظتی", "امنیتی"),
            new Style(R.drawable.ic_sub_rings,
                    "عروس", "تالار", "عقد"),
            new Style(R.drawable.ic_sub_perfume,
                    "عطر", "لوازمآرایشی", "شوینده", "شیمیایی", "بهداشتی"),
            new Style(R.drawable.ic_cat_beauty,
                    "آرایشگاه", "آرایش", "پیرایش", "گریم", "زیبایی"),
            new Style(R.drawable.ic_sub_dentist,
                    "دندان"),
            new Style(R.drawable.ic_sub_eye,
                    "چشم", "عینک", "بینایی"),
            new Style(R.drawable.ic_sub_hearing,
                    "شنوایی", "سمعک"),
            new Style(R.drawable.ic_sub_lab,
                    "آزمایشگاه"),
            new Style(R.drawable.ic_sub_pets,
                    "دامپزشک", "حیوانخانگی", "پتشاپ"),
            new Style(R.drawable.ic_sub_body,
                    "فیزیوتراپی", "ماساژ", "یوگا"),
            new Style(R.drawable.ic_sub_chat,
                    "مشاوره", "اعصابوروان", "روانشناس"),
            new Style(R.drawable.ic_sub_gamepad,
                    "بازیهایکامپیوتری", "بازیکامپیوتری", "کنسول"),
            new Style(R.drawable.ic_sub_ferris,
                    "شهربازی", "تفریحی", "سرگرمی"),
            new Style(R.drawable.ic_sub_kids,
                    "کودک", "بچه", "سیسمونی", "نوزاد", "اسباببازی", "خانهبازی"),
            new Style(R.drawable.ic_cat_health,
                    "پزشک", "بیمارستان", "کلینیک", "درمان", "تصویربرداری", "زنانوزایمان", "جراح",
                    "تغذیه", "آمبولانس", "طبسوزنی"),
            new Style(R.drawable.ic_sub_movie,
                    "سینما"),
            new Style(R.drawable.ic_sub_bed,
                    "هتل", "مسافرخانه", "اقامتگاه", "کالایخواب"),
            new Style(R.drawable.ic_sub_mosque,
                    "مسجد", "مساجد", "زیارت"),
            new Style(R.drawable.ic_cat_travel,
                    "هواپیما", "مسافرتی", "گردشگری", "بلیط"),
            new Style(R.drawable.ic_cat_finance,
                    "موزه", "تاریخی"),
            new Style(R.drawable.ic_sub_parking,
                    "پارکینگ"),
            new Style(R.drawable.ic_sub_tree,
                    "پارک", "فضایسبز"),
            new Style(R.drawable.ic_sub_bus,
                    "ترمینال", "اتوبوس"),
            new Style(R.drawable.ic_sub_fuel,
                    "پمپبنزین", "بنزین", "CNG", "سیانجی"),
            new Style(R.drawable.ic_sub_card,
                    "شارژ", "کارت"),
            new Style(R.drawable.ic_sub_mail,
                    "پست"),
            new Style(R.drawable.ic_cat_legal,
                    "دادگاه", "دادگستری", "حلاختلاف", "عریضه", "حقوقی", "حسابداری", "دفترخانه", "ثبت"),
            new Style(R.drawable.ic_sub_bag,
                    "مراکزتجاری", "مرکزخرید", "مراکزخرید", "پاساژ", "کیفوکفش"),
            new Style(R.drawable.ic_sub_print,
                    "تایپ", "تکثیر", "چاپ", "ماشینهایاداری"),
            new Style(R.drawable.ic_sub_building,
                    "شهرداری", "ادارات", "اداره", "اتحادیه", "پیشخوان", "اداری", "تجاری",
                    "مشارکتدرساخت", "پیشفروش"),
            new Style(R.drawable.ic_sub_key,
                    "اجاره"),
            new Style(R.drawable.ic_cat_estate,
                    "مسکونی", "املاک", "آپارتمان", "ویلا", "مسکن"),
            new Style(R.drawable.ic_cat_book,
                    "کتاب", "فرهنگسرا", "صحافی", "نشر"),
            new Style(R.drawable.ic_sub_pen,
                    "نوشتافزار", "لوازمالتحریر", "لوازمتحریر"),
            new Style(R.drawable.ic_sub_palette,
                    "هنری", "هنر", "صنایعدستی"),
            new Style(R.drawable.ic_sub_laptop,
                    "کامپیوتر", "رایانه", "لپتاپ"),
            new Style(R.drawable.ic_cat_digital,
                    "موبایل", "تلفن", "لوازمجانبی"),
            new Style(R.drawable.ic_sub_bolt,
                    "برق", "الکترونیک"),
            new Style(R.drawable.ic_sub_waves,
                    "استخر"),
            new Style(R.drawable.ic_cat_sport,
                    "باشگاه", "بدنسازی", "ورزش"),
            new Style(R.drawable.ic_sub_cafe,
                    "کافیشاپ", "کافه", "قهوه", "چای", "سفرهخانه", "چایخانه"),
            new Style(R.drawable.ic_sub_burger,
                    "فستفود", "ساندویچ", "پیتزا", "برگر"),
            new Style(R.drawable.ic_sub_soup,
                    "آشوحلیم", "حلیم", "کلهپزی", "جگر", "دلوقلوه"),
            new Style(R.drawable.ic_sub_icecream,
                    "بستنی", "آبمیوه"),
            new Style(R.drawable.ic_sub_bakery,
                    "شیرینی", "قنادی", "نانوایی", "نانفانتزی", "کیک"),
            new Style(R.drawable.ic_cat_restaurant,
                    "رستوران", "کباب", "بریان", "طباخی", "بیرونبر", "آشپز", "ظروف"),
            new Style(R.drawable.ic_sub_fish,
                    "ماهی"),
            new Style(R.drawable.ic_sub_apple,
                    "میوه", "سبزی", "صیفی"),
            new Style(R.drawable.ic_sub_cart,
                    "سوپرمارکت", "هایپر", "عمدهفروشی", "خواربار", "فروشگاه", "پروتئین", "گوشت",
                    "لبنیات", "آجیل", "خشکبار", "فرآورده", "عسل"),
            new Style(R.drawable.ic_cat_job,
                    "استخدام"),
            new Style(R.drawable.ic_sub_person,
                    "جویایکار", "کارجو"),
            new Style(R.drawable.ic_sub_flower,
                    "گلفروشی", "گلوگیاه"),
            new Style(R.drawable.ic_cat_eco,
                    "عطاری", "گیاه", "بذر", "کود", "کشاورزی", "دام", "طیور"),
            new Style(R.drawable.ic_sub_drop,
                    "کارواش", "قالیشویی", "تعویضروغن", "شیرآلات", "لوله", "شستشو"),
            new Style(R.drawable.ic_sub_battery,
                    "باطری", "باتری"),
            new Style(R.drawable.ic_sub_tire,
                    "رینگ", "لاستیک", "تایر"),
            new Style(R.drawable.ic_cat_transport,
                    "یدککش", "حملونقل", "باربری", "سنگین", "کامیون", "اسبابکشی"),
            new Style(R.drawable.ic_cat_service,
                    "تعمیر", "تنظیمموتور", "صافکاری", "ابزار", "تراشکاری", "شیشهبری", "خدمات"),
            new Style(R.drawable.ic_cat_industry,
                    "صنعتی", "صنعت", "کارخانه"),
            new Style(R.drawable.ic_cat_car,
                    "خودرو", "اتومبیل", "موتورسیکلت", "لوازمیدکی", "اسپرت", "نمایشگاه"),
            new Style(R.drawable.ic_sub_fridge,
                    "لوازمخانگی", "یخچال", "لباسشویی"),
            new Style(R.drawable.ic_cat_furniture,
                    "مبل", "فرش", "دکوراسیون", "پرده", "کابینت"),
            new Style(R.drawable.ic_cat_apparel,
                    "پوشاک", "لباس", "خیاطی", "مانتو", "شال", "روسری", "کلاه", "چرم", "بوتیک",
                    "خرازی", "حوله", "رومیزی", "مزون"),
            new Style(R.drawable.ic_sub_watch,
                    "ساعت"),
            new Style(R.drawable.ic_cat_jewelry,
                    "طلا", "جواهر", "نقره", "زیورآلات"),
            new Style(R.drawable.ic_cat_media,
                    "آتلیه", "عکاسی", "فیلمبرداری"),
            new Style(R.drawable.ic_cat_education,
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
