<?php

namespace App\Support;

use App\Models\EContractTemplateVersion;
use Illuminate\Database\QueryException;

/**
 * The wording of the electronic contract.
 *
 * Admins edit it in the panel (متن قرارداد الکترونیک); every save becomes a new version in
 * e_contract_templates and old versions are kept. The app downloads the current version from
 * /api/e-contracts/current and fills the {placeholders} as the user types, so what the user reads
 * before accepting is exactly what gets stored in e_contracts.contract_text. Each contract records
 * the version it was signed under.
 *
 * Only the fixed wording is editable: every placeholder must stay in the text (they carry what the
 * user entered) and no unknown placeholder may be added.
 */
class EContractTemplate
{
    /** The wording contracts were signed under before it became editable. Do not change it. */
    const DEFAULT_VERSION = '1.0';
    const DEFAULT_TITLE = 'قرارداد همکاری';

    /** Shown in place of a field the user has not filled in yet, like the dotted line on paper. */
    const BLANK = '..............';

    /** Every placeholder the user's data fills in, with the label shown to the admin. */
    const PLACEHOLDERS = [
        'business_type' => 'نوع کسب و کار (شرکت/موسسه/...)',
        'business_name' => 'نام کسب و کار',
        'manager_title' => 'آقای/خانم',
        'manager_name' => 'نام مدیر',
        'national_code' => 'کد ملی',
        'phone' => 'تلفن ثابت',
        'mobile' => 'شماره همراه',
        'address' => 'آدرس',
        'subject' => 'خدمات/محصولات',
        'start_date' => 'تاریخ شروع',
        'end_date' => 'تاریخ پایان',
        'duration' => 'مدت قرارداد',
        'discount_percent' => 'درصد تخفیف',
    ];

    /** Cached for the request: the current version is read on every contract page. */
    private static ?array $current = null;

    /**
     * The version in force: {version, title, sections}. Falls back to the built-in 1.0 wording
     * when the table has no rows (or has not been migrated yet).
     */
    public static function current(): array
    {
        if (self::$current !== null) {
            return self::$current;
        }

        try {
            $row = EContractTemplateVersion::orderByDesc('id')->first();
        } catch (QueryException $e) {
            $row = null;
        }

        return self::$current = $row ? self::fromRow($row) : self::defaultTemplate();
    }

    /** A specific version, as signed by a contract; null if unknown. */
    public static function forVersion(?string $version): ?array
    {
        if ($version === null) {
            return null;
        }
        try {
            $row = EContractTemplateVersion::where('version', $version)->first();
        } catch (QueryException $e) {
            $row = null;
        }
        if ($row) {
            return self::fromRow($row);
        }

        return $version === self::DEFAULT_VERSION ? self::defaultTemplate() : null;
    }

    /**
     * Saves an edit as a new version ("2.0", "3.0", ...), or returns null when nothing changed.
     *
     * @param array<int, array{heading: ?string, text: string}> $sections already checked with problems()
     */
    public static function saveNewVersion(string $title, array $sections, ?int $adminId): ?EContractTemplateVersion
    {
        $current = self::current();
        if ($current['title'] === $title && $current['sections'] === $sections) {
            return null;
        }

        // Keep 1.0 in the history even if its row has gone missing.
        self::ensureDefaultStored();
        $latest = EContractTemplateVersion::orderByDesc('id')->value('version') ?? self::DEFAULT_VERSION;
        $row = EContractTemplateVersion::create([
            'version' => ((int) $latest + 1) . '.0',
            'title' => $title,
            'sections' => $sections,
            'created_by' => $adminId,
        ]);
        self::$current = null;

        return $row;
    }

    /**
     * What is wrong with an edited wording, in Persian; empty when it can be saved.
     *
     * @param array<int, array{heading: ?string, text: string}> $sections
     */
    public static function problems(string $title, array $sections): array
    {
        $problems = [];
        if (trim($title) === '') {
            $problems[] = 'عنوان قرارداد نمی تواند خالی باشد.';
        }
        if (count($sections) === 0) {
            $problems[] = 'قرارداد باید حداقل یک بند داشته باشد.';
        }

        $used = [];
        foreach ($sections as $i => $section) {
            if (trim($section['text']) === '') {
                $problems[] = 'متن بند ' . ($i + 1) . ' خالی است.';
            }
            if (preg_match('/\{\w+\}/', (string) $section['heading'])) {
                $problems[] = 'عنوان بند ' . ($i + 1) . ' نباید اطلاعات کاربر را داشته باشد؛ آن را در متن بند بگذارید.';
            }
            preg_match_all('/\{(\w+)\}/', $section['text'], $m);
            foreach ($m[1] as $name) {
                $used[$name] = true;
            }
        }

        foreach (array_keys($used) as $name) {
            if (! array_key_exists($name, self::PLACEHOLDERS)) {
                $problems[] = 'عبارت {' . $name . '} شناخته شده نیست؛ فقط اطلاعات کاربر که در راهنما آمده قابل استفاده است.';
            }
        }
        foreach (self::PLACEHOLDERS as $name => $label) {
            if (! isset($used[$name])) {
                $problems[] = '«' . $label . '» ({' . $name . '}) از متن حذف شده است؛ اطلاعاتی که کاربر وارد می کند باید در متن بماند.';
            }
        }

        return $problems;
    }

    /** Stores the built-in 1.0 wording as the first version if there is no version yet. */
    public static function ensureDefaultStored(): void
    {
        if (EContractTemplateVersion::exists()) {
            return;
        }
        EContractTemplateVersion::create([
            'version' => self::DEFAULT_VERSION,
            'title' => self::DEFAULT_TITLE,
            'sections' => self::defaultSections(),
        ]);
        self::$current = null;
    }

    /** The built-in 1.0 wording. */
    public static function defaultTemplate(): array
    {
        return [
            'version' => self::DEFAULT_VERSION,
            'title' => self::DEFAULT_TITLE,
            'sections' => self::defaultSections(),
        ];
    }

    /** Forgets the cached current version (after a save, and between tests). */
    public static function forgetCurrent(): void
    {
        self::$current = null;
    }

    private static function fromRow(EContractTemplateVersion $row): array
    {
        return [
            'version' => $row->version,
            'title' => $row->title,
            'sections' => array_map(fn ($s) => [
                'heading' => isset($s['heading']) && trim((string) $s['heading']) !== '' ? (string) $s['heading'] : null,
                'text' => (string) ($s['text'] ?? ''),
            ], $row->sections ?? []),
        ];
    }

    /** @return array<int, array{heading: ?string, text: string}> */
    public static function defaultSections(): array
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
    public static function fill(array $sections, array $values): array
    {
        return array_map(function (array $section) use ($values) {
            $section['text'] = preg_replace_callback('/\{(\w+)\}/', function ($m) use ($values) {
                $value = trim((string) ($values[$m[1]] ?? ''));

                return $value === '' ? self::BLANK : $value;
            }, $section['text']);

            return $section;
        }, $sections);
    }

    /**
     * Like fill(), but HTML-escaped with each filled-in value wrapped in <b class="$class">, so
     * the admin can tell what the user typed apart from the fixed wording.
     */
    public static function fillHtml(array $sections, array $values, string $class = 'text-primary'): array
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
        }, $sections);
    }

    /**
     * The filled contract as plain text, as stored in e_contracts.contract_text.
     *
     * @param array{title: string, sections: array} $template the version being signed
     */
    public static function render(array $template, array $values, string $contractDate): string
    {
        $parts = [$template['title'], 'تاریخ: ' . $contractDate];
        foreach (self::fill($template['sections'], $values) as $section) {
            $parts[] = $section['heading'] ? $section['heading'] . ":\n" . $section['text'] : $section['text'];
        }

        return implode("\n\n", $parts);
    }
}
