# استقرار بک‌اند جدید (iranapp-api)

این پروژه جایگزین `ghollacLaravel` است. پروژه‌ی قدیمی **دست‌نخورده** باقی مانده تا سرویس فعلی
و دامنه‌ی `asreesfahanapp.com` تا زمان جابجایی کامل قطع نشوند.

| | قدیمی (`ghollacLaravel`) | جدید (`iranapp-api`) |
|---|---|---|
| Laravel | 5.4 | 13 |
| PHP | 5.6+ (سرور: 7.0.33) | 8.3+ |
| احراز هویت API | tymon/jwt-auth 0.5 | Laravel Sanctum |
| نوتیفیکیشن | brozot/laravel-fcm (API خاموش‌شده) | FCM HTTP v1 |
| پردازش تصویر | Intervention Image 2 | Intervention Image 4 |
| اکسل | maatwebsite/excel 2 (PHPExcel) | maatwebsite/excel 4 |
| Migration | ۵ فایل ناقص | ۶۰ فایل، ساخت کامل دیتابیس |
| تست | تقریباً هیچ | ۳۲ تست |

---

## پیش‌نیازهای سرور

### ۱. ارتقای PHP (اجباری)
سرور در حال حاضر **PHP 7.0.33** است که از ژانویه ۲۰۱۹ پشتیبانی نمی‌شود.
در cPanel → **MultiPHP Manager** → دامنه را انتخاب و **PHP 8.3** یا بالاتر را ست کن.

اکستنشن‌های لازم: `pdo_mysql`, `mbstring`, `openssl`, `tokenizer`, `xml`, `ctype`,
`json`, `bcmath`, `fileinfo`, `curl`, `zip`, `gd`

### ۲. فعال‌کردن SSL (اجباری برای اپ اندروید)
گواهی فعلی دامنه self-signed و متعلق به خود هاست است (`CN=parspack`)، نه `iranapp.biz`.
در cPanel → **SSL/TLS Status** → AutoSSL را برای دامنه اجرا کن.

تا وقتی این کار انجام نشود اپ اندروید مجبور است روی HTTP بدون رمزنگاری کار کند،
یعنی رمز عبور و توکن کاربران رمزنگاری‌نشده منتقل می‌شود.

### ۳. حذف فایل افشاکننده (فوری)
فایل `public_html/i.php` یک `phpinfo()` عمومی است و کل پیکربندی سرور را نشان می‌دهد.
همین حالا حذفش کن.

---

## مراحل استقرار

### ۱. آپلود
پوشه‌ی `iranapp-api` را کنار `ghollacLaravel` قرار بده. پوشه‌ی `vendor` را آپلود نکن؛
روی سرور بساز:

```bash
cd ~/domains/iranapp.biz/iranapp-api
composer install --no-dev --optimize-autoloader
```

### ۲. تنظیم `.env`
از `.env.example` کپی بگیر و این‌ها را پر کن:

```
APP_ENV=production
APP_DEBUG=false
APP_URL=https://iranapp.biz
APP_KEY=            # با php artisan key:generate ساخته می‌شود

DB_DATABASE=        # همان دیتابیس فعلی
DB_USERNAME=
DB_PASSWORD=

MAIL_HOST=mail.iranapp.biz
MAIL_PORT=587
MAIL_USERNAME=
MAIL_PASSWORD=

FIREBASE_CREDENTIALS=storage/app/firebase/service-account.json
```

سپس:
```bash
php artisan key:generate
```

### ۳. کلید Firebase
نسخه‌ی جدید FCM دیگر با «server key» کار نمی‌کند و به service account نیاز دارد:

Firebase Console → پروژه‌ی `ghollac-b0aa5` → Project settings → Service accounts →
**Generate new private key** → فایل JSON را در مسیر زیر بگذار:

```
storage/app/firebase/service-account.json
```

این فایل کلید خصوصی است — در گیت قرار نگیرد (در `.gitignore` هست).

### ۴. دیتابیس
دیتابیس فعلی همان ساختار را دارد و migrationها با آن مطابق‌اند. فقط یک جدول جدید لازم است
(برای توکن‌های Sanctum):

```bash
php artisan migrate --path=database/migrations/*_create_personal_access_tokens_table.php
```

> برای نصب روی دیتابیس **خالی**، `php artisan migrate` کل ۴۷ جدول را می‌سازد.
> این قابلیت در پروژه‌ی قدیمی وجود نداشت.

### ۵. اتصال دامنه
فایل `public_html/index.php` فعلی به `ghollacLaravel` اشاره می‌کند. برای جابجایی،
مسیر داخل آن را به `iranapp-api/` تغییر بده (**از فایل فعلی حتماً بک‌آپ بگیر**).

پوشه‌های عکس (`ads_photo`, `news_photo`, `vip_ads_photo`) در `public_html` بمانند —
کد همان مسیرها را استفاده می‌کند.

### ۶. بهینه‌سازی برای production
```bash
php artisan config:cache
php artisan route:cache
php artisan view:cache
```

---

## اپ اندروید

قالب پاسخ‌ها **عمداً دست‌نخورده** مانده: توکن همچنان با کلید `jwt_token` برمی‌گردد،
فیلد `status` همان معناها را دارد، و توکن هم از `?token=` و هم از هدر `Authorization`
پذیرفته می‌شود.

یعنی اپ فعلی فقط با تغییر آدرس در `StaticData.java` به بک‌اند جدید وصل می‌شود:

```java
public static String DOMAIN = "https://iranapp.biz";   // بعد از فعال‌شدن SSL
```

پس از فعال‌شدن SSL، فایل `network_security_config.xml` در اپ اندروید هم باید حذف شود
(آن استثنای موقت برای HTTP بدون رمزنگاری بود).

---

## اجرای تست‌ها

تست‌ها روی MySQL اجرا می‌شوند (نه SQLite — ساختار دیتابیس نام ایندکس‌های تکراری بین
جدول‌ها دارد که SQLite پشتیبانی نمی‌کند). یک دیتابیس خالی به نام `iranapp_test` لازم است:

```bash
php artisan test
```

---

## کارهای باقی‌مانده

- ماژول پرداخت (`app/Webazin`) منتقل شده ولی با درگاه واقعی تست نشده
- ارسال پیامک (`send_sms` در `app/helpers.php`) نیاز به بررسی با سرویس‌دهنده دارد
- قالب‌های پنل ادمین از Bootstrap 3 / Vue 2 استفاده می‌کنند — کار می‌کنند ولی قدیمی‌اند
- کلید `JWT_SECRET` قدیمی روی مقدار پیش‌فرض `changeme` بود؛ با حذف JWT دیگر موضوعیت ندارد
