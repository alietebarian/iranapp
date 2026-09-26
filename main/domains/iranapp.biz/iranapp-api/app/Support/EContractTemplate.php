<?php

namespace App\Support;

/**
 * The wording of the electronic contract, kept in one place.
 *
 * The app does not carry its own copy: it downloads these sections from /api/e-contracts/current
 * and fills the {placeholders} as the user types, so what the user reads before accepting is
 * exactly what gets stored in e_contracts.contract_text and shown to the admin.
 *
 * Bump VERSION whenever the wording changes; each contract records the version it was signed under.
 */
class EContractTemplate
{
    const VERSION = '1.0';

    const TITLE = 'قرارداد همکاری';

    /** Shown in place of a field the user has not filled in yet, like the dotted line on paper. */
    const BLANK = '..............';

    /** @return array<int, array{heading: ?string, text: string}> */
    public static function sections(): array
    {
        return [
            [
                'heading' => null,
                'text' => 'شرکت ایران اپ با مجوز رسمی از وزارت ارشاد، اتحادیه کانون آگهی و تبلیغات و مجوز از اتحادیه فناوری اطلاعات رایانه و ثبت به شماره 54916 در نظر دارد مشترکین خود که شامل اصناف و خانواده های محترم آنها می باشد را در امور خدماتی، رفاهی و گردشگری مورد حمایت قرار دهد و در راستای تشویق به مراکز گردشگری و رفاهی، موزه، ورزش، سرگرمی و به خصوص مراکز تفریحی گامی موثر بردارد.',
            ],
            [
                'heading' => null,
                'text' => 'این قرارداد فی مابین تیم توسعه دهنده ایران اپ به شماره مجوز 1-1-718-196-635-1 از وزارت فرهنگ و ارشاد اسلامی که طرف اول نامیده می شود و {business_type} {business_name} به مدیریت {manager_title} {manager_name} به شماره ملی {national_code} و تلفن {phone} و شماره همراه {mobile} و آدرس {address} که در این قرارداد به عنوان طرف دوم نامیده می شود.',
            ],
            [
                'heading' => 'موضوع قرارداد',
                'text' => 'معرفی مشترکین طرف اول جهت دریافت خدمات/محصولات {subject} به طرف دوم.',
            ],
            [
                'heading' => 'مدت قرارداد',
                'text' => 'مدت قرارداد فی مابین از تاریخ {start_date} لغایت {end_date} به مدت {duration} می باشد و در صورت انصراف و عدم همکاری طرف دوم پس از انعقاد قرارداد، وی بدون هیچگونه اعتراضی ملزم به پرداخت کلیه خسارت وارده به طرف اول می باشد.',
            ],
            [
                'heading' => 'تعهدات و الزامات طرفین',
                'text' => implode("\n", [
                    '1- طرف اول ملزم به معرفی طرف دوم به مشترکین خود از طریق اپلیکیشن ایران اپ می باشد.',
                    '2- طرف اول متعهد می گردد میزان تعهدات طرف دوم را طبق قرارداد به مشترکین خود اعلام نموده و از ایجاد تعهدات کذب برای طرف دوم خودداری نماید.',
                    '3- طرف دوم موظف است میزان تخفیف تعیین شده را بدون هیچ عذر و بهانه ای به کاربران ایران اپ ارائه کند.',
                    '4- طرف دوم موظف است کلیه قیمت ها و تعرفه ها را در چهارچوب مقررات صنفی خود ارائه و از هرگونه گران فروشی و ارائه قیمت های خارج از نرخ مصوب خودداری نماید. ضمنا مرجع رسیدگی به اختلافات بر سر کیفیت، اجرت خدمات و قیمت ها، اتحادیه مربوطه است.',
                    '5- طرف دوم موظف است رضایت مشترکین طرف اول قرارداد را حاصل نموده و از هرگونه بی اعتنایی و پاسخ کذب جلوگیری نماید.',
                    '6- طرف دوم موظف است در صورت اختلاف با کاربران "ایران اپ" در همان زمان جهت رفع اختلاف با تماس تلفنی یا درخواست حضور کارشناس، اختلافات و مشکل را حل نموده و حق تصمیم گیری یکطرفه را از خود سلب نماید.',
                    '7- در جهت رسیدگی به شکایات کاربران "ایران اپ" طرف اول در چهارچوب این قرارداد مرتبا از طریق تماس تلفنی و مراجعات کارشناسان خود، موارد مورد توافق با خسارت وارده بنا به تشخیص کارشناسان طرف اول قرارداد صورت می گیرد و ضمنا عدم اطلاع رسانی به موقع در خصوص تغییر آدرس طرف دوم نیز مشمول این جریمه می شود.',
                    '8- بر اساس این قرارداد، طرف دوم موافقت نمود در ازای ارائه کارت "ایران اپ" توسط مشترکین طرف اول، به میزان {discount_percent} درصد در قیمت کالا یا خدمات ارائه شده، تخفیف منظور نماید.',
                    '9- طرف دوم متعهد می گردد با هیچ شرکتی که فعالیت آن مانند طرف اول (اطلاع رسانی نرم افزاری و تخفیف) است انعقاد قرارداد ننماید. در غیر این صورت، این قرارداد فسخ و اطلاعات طرف دوم از روی اپلیکیشن حذف گردیده و طرف دوم حق هیچگونه اعتراضی ندارد.',
                ]),
            ],
        ];
    }

    /**
     * Sections with every {placeholder} replaced; a missing value shows as a dotted blank.
     *
     * @param array<string, string|int|null> $values keyed by placeholder name
     */
    public static function fill(array $values): array
    {
        return array_map(function (array $section) use ($values) {
            $section['text'] = preg_replace_callback('/\{(\w+)\}/', function ($m) use ($values) {
                $value = trim((string) ($values[$m[1]] ?? ''));

                return $value === '' ? self::BLANK : $value;
            }, $section['text']);

            return $section;
        }, self::sections());
    }

    /**
     * Like fill(), but HTML-escaped with each filled-in value wrapped in <b class="$class">, so
     * the admin can tell what the user typed apart from the fixed wording.
     */
    public static function fillHtml(array $values, string $class = 'text-primary'): array
    {
        return array_map(function (array $section) use ($values, $class) {
            $parts = preg_split('/(\{\w+\})/', $section['text'], -1, PREG_SPLIT_DELIM_CAPTURE);
            $html = '';
            foreach ($parts as $part) {
                if (preg_match('/^\{(\w+)\}$/', $part, $m)) {
                    $value = trim((string) ($values[$m[1]] ?? ''));
                    $html .= '<b class="' . e($class) . '">' . e($value === '' ? self::BLANK : $value) . '</b>';
                } else {
                    $html .= e($part);
                }
            }
            $section['html'] = nl2br($html);

            return $section;
        }, self::sections());
    }

    /** The filled contract as plain text, as stored in e_contracts.contract_text. */
    public static function render(array $values, string $contractDate): string
    {
        $parts = [self::TITLE, 'تاریخ: ' . $contractDate];
        foreach (self::fill($values) as $section) {
            $parts[] = $section['heading'] ? $section['heading'] . ":\n" . $section['text'] : $section['text'];
        }

        return implode("\n\n", $parts);
    }
}
