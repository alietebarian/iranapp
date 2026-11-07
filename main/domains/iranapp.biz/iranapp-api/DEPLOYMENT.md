# استقرار بک‌اند جدید (iranapp-api)

این پروژه جایگزین `ghollacLaravel` است. تا روز جابجایی، پروژه‌ی قدیمی روی سرور دست‌نخورده
می‌ماند و اپ‌های فعلی کاربران (نسخه‌ی ۱۰۶) به آن وصل‌اند.

| | قدیمی (`ghollacLaravel`) | جدید (`iranapp-api`) |
|---|---|---|
| Laravel | 5.4 | 13 |
| PHP | 7.0 (فعلی سرور: 7.0.33) | **8.4 به بالا** (بخش «پیش‌نیاز» را ببین) |
| احراز هویت API | tymon/jwt-auth 0.5 | Laravel Sanctum |
| نوتیفیکیشن | brozot/laravel-fcm (API خاموش‌شده) | FCM HTTP v1 |
| پردازش تصویر | Intervention Image 2 | Intervention Image 4 |
| اکسل | maatwebsite/excel 2 (PHPExcel) | maatwebsite/excel 4 |
| تست | تقریباً هیچ | ۱۸۹ تست |

## مشخصات سرور

- پنل: **DirectAdmin** (نه cPanel) با **CloudLinux** و وب‌سرور **LiteSpeed**
- کاربر: `appbiz` — پوشه‌ی خانه: `/home/appbiz`
- مسیر دامنه: `/home/appbiz/domains/iranapp.biz/`
  - `public_html/` — ریشه‌ی وب؛ عکس‌ها (`ads_photo`, `news_photo`, `vip_ads_photo`) و فایل‌های پنل ادمین (`admin`, `icon`) همین‌جا هستند
  - `ghollacLaravel/` — بک‌اند قدیمی
  - `iranapp-api/` — بک‌اند جدید (اینجا آپلود می‌شود)
- نسخه‌ی PHP در CloudLinux برای **کل اکانت** است، نه یک پوشه. اجرای PHP خط فرمان هر نسخه:
  `/opt/alt/php84/usr/bin/php` (برای 8.3: `/opt/alt/php83/usr/bin/php`)

## ترتیب کلی

| مرحله | کی | سرویس فعلی قطع می‌شود؟ |
|---|---|---|
| ۰. کارهای امنیتی | همین حالا | خیر |
| ۱. پیش‌نیاز نسخه‌ی PHP | قبل از آپلود | خیر |
| ۲. بکاپ | قبل از آپلود | خیر |
| ۳. آپلود کد | هر وقت | خیر |
| ۴. `.env` و کلید Firebase | هر وقت | خیر |
| ۵. migrationهای دیتابیس | هر وقت | خیر — همه‌شان فقط اضافه می‌کنند |
| ۶. جابجایی | ساعت کم‌ترافیک | **بله، چند دقیقه** |
| ۷. تست بعد از جابجایی | بلافاصله | — |
| ۸. Cron | بعد از جابجایی | — |
| ۹. انتشار اپ در بازار | بعد از تست | — |

مراحل ۲ تا ۵ را می‌شود روزها قبل از جابجایی انجام داد. فقط مرحله‌ی ۶ سرویس را عوض می‌کند.

---

## ۰. کارهای امنیتی (همین حالا)

1. **حذف `public_html/i.php`** — یک `phpinfo()` عمومی است و نسخه‌ی PHP، مسیرها و ماژول‌های
   سرور را نشان می‌دهد. هیچ جای اپ یا بک‌اندها از آن استفاده نمی‌کند.
2. **ریپوی GitHub را Private کن** — این ریپو عمومی است و کلید SSH (`java/java/5650011133`)،
   ایمیل‌های `info@iranapp.biz`، کلید API کاوه‌نگار (در `ghollacLaravel/config/app.php`) و
   بکاپ‌های سرور را در خود دارد.
3. **SSL (اختیاری ولی توصیه‌شده)** — بخش «SSL» در انتهای این فایل را ببین.

## ۱. پیش‌نیاز: نسخه‌ی PHP

فایل `composer.lock` روی PHP 8.5 ساخته شده و Symfony 8 را دارد که **حداقل PHP 8.4.1** می‌خواهد.

در DirectAdmin → **Select PHP Version** لیست نسخه‌ها را نگاه کن (**هنوز چیزی را انتخاب نکن**):

- **8.4 یا بالاتر هست:** کاری لازم نیست؛ در مرحله‌ی ۶ همان را انتخاب می‌کنیم.
- **بالاترین نسخه 8.3 است:** قبل از آپلود، روی کامپیوتر خودت پکیج‌ها را برای 8.3 قفل کن و تست بگیر:

  ```bash
  composer config platform.php 8.3.0
  composer update
  php artisan test
  ```

  و در همه‌ی دستورهای این فایل به جای `php84` از `php83` استفاده کن.

> PHP خط فرمان (برای SSH و Cron) مستقل از نسخه‌ی انتخاب‌شده در پنل است؛ با مسیر کامل
> `/opt/alt/php84/usr/bin/php` همین حالا هم می‌شود artisan را روی PHP جدید اجرا کرد.

## ۲. بکاپ

1. **دیتابیس:** DirectAdmin → **MySQL Management** → دیتابیس فعلی → phpMyAdmin → **Export** (حالت Quick، SQL).
   فایل را روی کامپیوترت نگه دار.
2. **فایل‌ها:** از این دو فایل یک کپی با پسوند `.bak` در همان پوشه بساز:
   - `public_html/index.php` → `public_html/index.php.bak`
   - `public_html/.htaccess` → `public_html/.htaccess.bak`
3. **اطلاعات دیتابیس:** فایل `ghollacLaravel/.env` را در File Manager باز کن و مقادیر
   `DB_HOST`، `DB_DATABASE`، `DB_USERNAME` و `DB_PASSWORD` را یادداشت کن. بک‌اند جدید همان
   دیتابیس را استفاده می‌کند.

## ۳. آپلود کد

پوشه‌ی `vendor` را باید ساخت، چون در گیت نیست. یکی از دو راه:

### راه الف: با SSH و composer روی سرور

پوشه را (بدون `vendor`، `.env` و `node_modules`) در `/home/appbiz/domains/iranapp.biz/iranapp-api`
آپلود کن، بعد:

```bash
cd ~/domains/iranapp.biz/iranapp-api
/opt/alt/php84/usr/bin/php $(which composer) install --no-dev --optimize-autoloader
```

اگر `composer` روی سرور نیست، `composer.phar` را از getcomposer.org بگیر و به جای `$(which composer)` بگذار.

### راه ب: بدون SSH (vendor را روی کامپیوتر خودت بساز)

در یک **کپی جداگانه** (پوشه‌ی `vendor` پروژه‌ی اصلی پکیج‌های تست را لازم دارد، دست نزن):

```bash
cp -r iranapp-api ../iranapp-api-deploy
cd ../iranapp-api-deploy
rm -rf vendor node_modules .env .phpunit.result.cache storage/logs/*.log
composer install --no-dev --optimize-autoloader
```

پوشه‌ی `iranapp-api-deploy` را zip کن ← در File Manager دایرکت‌ادمین به `domains/iranapp.biz/`
آپلود و Extract کن ← اسم پوشه را `iranapp-api` بگذار.

### بعد از آپلود

پوشه‌های `storage/` و `bootstrap/cache/` باید برای PHP قابل نوشتن باشند (روی CloudLinux،
PHP با همان کاربر `appbiz` اجرا می‌شود، پس دسترسی 755 کافی است).

## ۴. فایل `.env` و کلید Firebase

### `.env`

در `iranapp-api/` فایل `.env` بساز:

```ini
APP_NAME=IranApp
APP_ENV=production
APP_KEY=
APP_DEBUG=false
APP_URL=http://iranapp.biz

APP_LOCALE=en
APP_FALLBACK_LOCALE=en
APP_MAINTENANCE_DRIVER=file

LOG_CHANNEL=stack
LOG_STACK=daily
LOG_DAILY_DAYS=14
LOG_LEVEL=error

# همان مقادیر ghollacLaravel/.env
DB_CONNECTION=mysql
DB_HOST=localhost
DB_PORT=3306
DB_DATABASE=
DB_USERNAME=
DB_PASSWORD=

# دیتابیس جدول‌های sessions/cache/jobs را ندارد؛ این سه باید file/sync بمانند
SESSION_DRIVER=file
SESSION_LIFETIME=120
CACHE_STORE=file
QUEUE_CONNECTION=sync
FILESYSTEM_DISK=local
BROADCAST_CONNECTION=log
MAIL_MAILER=log

FIREBASE_CREDENTIALS=storage/app/firebase/service-account.json

# بدون این، پیامک فعال‌سازی و فراموشی رمز ارسال نمی‌شود و ثبت‌نام خطا می‌دهد
KAVENEGAR_API_KEY=
```

- **`APP_KEY`:** روی کامپیوتر خودت `php artisan key:generate --show` را اجرا کن و خروجی
  (`base64:...`) را بگذار. با SSH: `php artisan key:generate`.
- **`KAVENEGAR_API_KEY`:** کلید فعلی در گیت عمومی لو رفته. در پنل کاوه‌نگار یک کلید جدید بساز و
  آن را اینجا بگذار؛ بعد از جابجایی کلید قدیمی را باطل کن (بک‌اند قدیمی تا روز جابجایی با کلید
  قدیمی کار می‌کند).
- **`APP_URL`:** بعد از فعال شدن SSL به `https://iranapp.biz` تغییر کند.
- **`APP_DEBUG` هرگز `true` نشود** — خطاها را با جزئیات (شامل اطلاعات دیتابیس) به همه نشان می‌دهد.
  خطاها در `storage/logs/laravel-YYYY-MM-DD.log` ثبت می‌شوند.

### کلید Firebase

FCM HTTP v1 با «server key» قدیمی کار نمی‌کند و service account لازم دارد:

Firebase Console → پروژه‌ی `ghollac-b0aa5` → Project settings → Service accounts →
**Generate new private key** → فایل JSON را با نام `service-account.json` در این مسیر بگذار:

```
iranapp-api/storage/app/firebase/service-account.json
```

این فایل کلید خصوصی است و در `.gitignore` هست؛ هرگز commit نشود.

## ۵. migrationهای دیتابیس

دیتابیس فعلی همه‌ی جدول‌های پایه را دارد، اما ۱۸ migration جدید (جدول‌ها و ستون‌های قابلیت‌های
جدید: ویدیو آگهی، قرارداد الکترونیک، کارت عضویت، دفترچه تلفن، تاریخ تولد و...) باید اجرا شوند.
**`php artisan migrate` معمولی را اجرا نکن** — می‌خواهد جدول‌های موجود را دوباره بسازد و خطا می‌دهد.

اسکریپت `scripts/production-migrate.sh` همین ۱۸ فایل را به ترتیب اجرا می‌کند:

- همه فقط جدول یا ستون nullable/پیش‌فرض‌دار **اضافه** می‌کنند؛ بک‌اند قدیمی بعد از آن هم کار می‌کند،
  پس می‌شود قبل از روز جابجایی اجرایش کرد.
- اجرای دوباره بی‌خطر است؛ فایلی که قبلاً اجرا شده رد می‌شود.
- یکی از آن‌ها (`convert_ad_tables_to_utf8mb4`) چهار جدول آگهی را به utf8mb4 تبدیل می‌کند تا
  آگهی‌های دارای ایموجی ذخیره شوند — به همین دلیل بکاپ مرحله‌ی ۲ لازم است.

**با SSH:**

```bash
cd ~/domains/iranapp.biz/iranapp-api
bash scripts/production-migrate.sh
```

**بدون SSH و Cron (روش استفاده‌شده در استقرار اول):** فایل `database/production-migrations.sql`
همین ۱۸ migration به صورت SQL است (از اجرای واقعی آن‌ها روی یک کپی هم‌شکل production ضبط و
مقایسه شده). در phpMyAdmin دیتابیس production را انتخاب کن → **Import** → این فایل → **Go**.
جدول `migrations` را هم به‌روز می‌کند، پس از این به بعد `php artisan migrate` معمولی کار می‌کند.
فقط یک بار اجرا شود؛ اگر وسط کار خطا داد، پیام خطا را نگه دار و فایل را دوباره اجرا نکن.

**بدون SSH، با Cron:** DirectAdmin → **Cron Jobs** → یک کار جدید با زمان «هر دقیقه» (`* * * * *`):

```
cd /home/appbiz/domains/iranapp.biz/iranapp-api && bash scripts/production-migrate.sh >> storage/logs/deploy.log 2>&1
```

یکی-دو دقیقه صبر کن، **Cron را حذف کن**، و `storage/logs/deploy.log` را در File Manager باز کن.
باید با `All 18 migrations done.` تمام شده باشد. (همین روش برای اجرای هر دستور artisan دیگری بدون
SSH هم کار می‌کند.)

## ۶. جابجایی (ساعت کم‌ترافیک)

از اینجا تا پایان مرحله‌ی ۷ اپ کاربران چند دقیقه کار نمی‌کند.

### ۶.۱ نسخه‌ی PHP

DirectAdmin → **Select PHP Version**:

1. نسخه‌ی **8.4** (یا همان نسخه‌ی مرحله‌ی ۱) را انتخاب و **Set as current** کن.
2. تب **Extensions** — این‌ها تیک داشته باشند:
   `bcmath`, `dom`, `fileinfo`, `gd`, `intl`, `mbstring`, `mysqlnd`, `nd_pdo_mysql` (یا `pdo_mysql`),
   `pdo`, `sodium`, `xmlreader`, `xmlwriter`, `zip`
3. تب **Options**:

   ```
   upload_max_filesize = 100M
   post_max_size       = 110M
   max_execution_time  = 300
   max_input_time      = 300
   memory_limit        = 256M
   ```

   (کد تا ۱۰۰ مگابایت ویدیو می‌پذیرد. `max_execution_time` فعلی ۳۰ ثانیه است و برای آپلود ویدیو کم است.
   صفحه‌ی «تصاویر و ویدیو آگهی» در پنل ادمین سقف واقعی را نشان می‌دهد.)

از این لحظه بک‌اند قدیمی کار نمی‌کند.

### ۶.۲ اتصال دامنه به بک‌اند جدید

محتوای `public_html/index.php` را **کامل** با این جایگزین کن (فقط عوض کردن مسیر کافی نیست؛
ساختار Laravel 13 با 5.4 فرق دارد):

```php
<?php

use Illuminate\Foundation\Application;
use Illuminate\Http\Request;

define('LARAVEL_START', microtime(true));

if (file_exists($maintenance = __DIR__.'/../iranapp-api/storage/framework/maintenance.php')) {
    require $maintenance;
}

require __DIR__.'/../iranapp-api/vendor/autoload.php';

/** @var Application $app */
$app = require_once __DIR__.'/../iranapp-api/bootstrap/app.php';

// Photos and videos live in public_html (ads_photo, news_photo, ...), not iranapp-api/public.
$app->usePublicPath(__DIR__);

$app->handleRequest(Request::capture());
```

`public_html/.htaccess` فعلی را **تغییر نده**؛ با بک‌اند جدید سازگار است (هدر Authorization و CORS را دارد).

### ۶.۳ کش کردن تنظیمات

```bash
cd ~/domains/iranapp.biz/iranapp-api
/opt/alt/php84/usr/bin/php artisan optimize
```

(بدون SSH: با روش Cron مرحله‌ی ۵، با دستور
`cd /home/appbiz/domains/iranapp.biz/iranapp-api && /opt/alt/php84/usr/bin/php artisan optimize >> storage/logs/deploy.log 2>&1`)

> **هر بار که `.env` را تغییر دادی، `artisan optimize` را دوباره اجرا کن** — وگرنه تغییر اعمال نمی‌شود.
> برای پاک کردن کش: `artisan optimize:clear`.

## ۷. تست بعد از جابجایی

**از مرورگر یا curl:**

| آدرس | نتیجه‌ی درست |
|---|---|
| `http://iranapp.biz/up` | صفحه‌ی سبز «Application up» |
| `http://iranapp.biz/api/app-version` | `{"status":200,"app_version":106}` |
| `http://iranapp.biz/api/ads/plans` | لیست پلن‌ها (شامل پلن ۱۷ «رایگان») |
| `http://iranapp.biz/admin/login` | فرم ورود پنل ادمین با استایل کامل |

**با اپ:**

1. **اپ قدیمی (۱۰۶):** باز کن. توکن‌های JWT قدیمی دیگر معتبر نیستند، پس **همه‌ی کاربران یک بار
   از حساب خارج می‌شوند** و باید دوباره وارد شوند (اپ این را خودش مدیریت می‌کند). ورود، لیست
   آگهی‌ها و عکس‌ها را چک کن.
2. **اپ جدید (۲۰۰، نصب دستی APK):**
   - ثبت‌نام با یک شماره‌ی واقعی → پیامک کد فعال‌سازی باید برسد (تست کاوه‌نگار)
   - فراموشی رمز → پیامک
   - ثبت آگهی با عکس → عکس باید واترمارک داشته باشد
   - ثبت آگهی با ویدیو
   - انتشار یک آگهی از پنل ادمین → نوتیفیکیشن باید برسد (تست Firebase)
   - قرارداد الکترونیک، کارت عضویت، دفترچه تلفن

اگر چیزی خطا داد، آخرین فایل `storage/logs/laravel-*.log` را بخوان.

### بازگشت (اگر جابجایی شکست خورد)

1. `public_html/index.php.bak` را به `index.php` برگردان.
2. در **Select PHP Version** نسخه‌ی **7.0** را انتخاب کن.

دیتابیس لازم نیست برگردد — migrationها فقط اضافه کرده‌اند و بک‌اند قدیمی با آن کار می‌کند.
(کسانی که در این فاصله وارد شده‌اند باید دوباره وارد شوند.)

## ۸. Cron (زمان‌بندی)

انتشار خبرهای زمان‌بندی‌شده، تبریک تولد و یادآوری انقضای قرارداد با هر درخواست به سرور هم
بررسی می‌شوند، ولی Cron آن‌ها را در ساعت‌های خلوت هم سر وقت می‌فرستد.

DirectAdmin → **Cron Jobs** → کار جدید، **هر دقیقه** (`* * * * *`):

```
/opt/alt/php84/usr/bin/php /home/appbiz/domains/iranapp.biz/iranapp-api/artisan schedule:run >> /dev/null 2>&1
```

## ۹. انتشار اپ در بازار، صفحه‌ی دانلود و اجبار به آپدیت

1. APK نسخه‌ی ۲۰۲ (`2.0.2`) را در کافه‌بازار منتشر کن.
2. صفحه‌ی دانلود: پوشه‌ی `public_html/download` (`index.html` و `.htaccess`) را روی هاست بگذار و
   همان APK را با نام **`iranapp.apk`** کنارشان آپلود کن. آدرس: `http://iranapp.biz/download/`.
   در هر نسخه‌ی بعدی، `iranapp.apk` را جایگزین کن و عدد نسخه را در `index.html` (`id="ver"`) عوض کن.
   فایل APK در git نیست (`public_html/download/.gitignore`).
3. کد سرور (`BlockOutdatedApps`) را می‌شود هر وقت بالا برد؛ تا قدم ۴ خاموش است.
4. **فقط بعد از تأیید بازار و آپلود `iranapp.apk`**، هر دو را با هم انجام بده:

   ```sql
   UPDATE setting SET setting_value = '202' WHERE setting_key = 'app_version';
   ```

   و در `.env` سرور:

   ```
   BLOCK_OUTDATED_APPS=true
   ```

   (از File Manager کافی است؛ کانفیگ روی سرور cache نشده. اگر روزی `config:cache` اجرا شد،
   بعد از هر تغییر `.env` دوباره اجرایش کن.)

   - **نسخه‌ی ۲۰۲ به بعد:** اگر نسخه پایین‌تر از `app_version` باشد، دیالوگ فقط «بروزرسانی» و «خروج»
     دارد. «بروزرسانی» کافه‌بازار را باز می‌کند، و اگر بازار نصب نباشد صفحه‌ی دانلود را.
   - **نسخه‌های قبل از ۲۰۲:** دیالوگ آپدیتشان «بیخیال» دارد و نمی‌شود عوضش کرد. برای همین سرور همه‌ی
     درخواست‌های API آن‌ها را (جز `/api/app-version`) با کد 426 رد می‌کند. اپ قدیمی هر بار دیالوگ
     آپدیت را نشان می‌دهد و اگر رد شود چیزی بارگذاری نمی‌شود. تشخیص از User-Agent است: اپ ۲۰۲ به بعد
     با `IranApp/<versionCode>` شروع می‌شود و نسخه‌های قدیمی با `Dalvik/` یا `okhttp/`.
   - اگر هر کدام زودتر روشن شود، کاربران پشت آپدیتی گیر می‌کنند که هنوز منتشر نشده.
     برای خاموش کردن: `BLOCK_OUTDATED_APPS=false` در `.env`.

---

## SSL

گواهی فعلی دامنه self-signed و متعلق به هاست است (`CN=parspack`). اپ با `http` و استثنای
`network_security_config.xml` کار می‌کند، ولی رمز و توکن کاربران رمزنگاری‌نشده منتقل می‌شود.

**فعال‌سازی:** DirectAdmin → **SSL Certificates** → **Get automatic certificate from ACME Provider
(Let's Encrypt)** → `iranapp.biz` و `www.iranapp.biz` را تیک بزن. اگر نبود، از پشتیبانی پارس‌پک بخواه.

**هشدار:** گزینه‌ی **Force SSL / ریدایرکت http به https** را روشن نکن. اپ‌های نصب‌شده با `http`
وصل می‌شوند و اندروید ریدایرکت http→https را دنبال نمی‌کند — اپ از کار می‌افتد.

**بعد از فعال‌سازی:**
1. در اپ: `StaticData.DOMAIN` → `https://iranapp.biz` و حذف `iranapp.biz` از
   `res/xml/network_security_config.xml`. اگر قبل از انتشار نسخه‌ی ۲۰۰ انجام شود، همین نسخه امن است.
2. در `.env`: `APP_URL=https://iranapp.biz` و دوباره `artisan optimize`.
3. ریدایرکت اجباری فقط وقتی که تقریباً همه‌ی کاربران روی نسخه‌ی https باشند.

---

## سازگاری با اپ اندروید

قالب پاسخ‌ها عمداً مثل بک‌اند قدیمی مانده: توکن با کلید `jwt_token` برمی‌گردد، فیلد `status`
همان معناها را دارد، توکن هم از `?token=` و هم از هدر `Authorization` پذیرفته می‌شود، و درخواست
بدون توکن معتبر `{"status":401,"error":"token_expired"}` می‌گیرد که اپ را به صفحه‌ی ورود می‌برد.

## اجرای تست‌ها (روی کامپیوتر خودت)

تست‌ها روی MySQL اجرا می‌شوند (نه SQLite — نام ایندکس‌ها بین جدول‌ها تکراری است). یک دیتابیس
خالی به نام `iranapp_test` لازم است (تنظیمات در `phpunit.xml`):

```bash
php artisan test
```

## کارهای باقی‌مانده

- ماژول پرداخت (`app/Webazin`، PayPing) منتقل شده ولی با درگاه واقعی تست نشده
- قالب‌های پنل ادمین از Bootstrap 3 / Vue 2 استفاده می‌کنند — کار می‌کنند ولی قدیمی‌اند
- رمز دیتابیس تست در `phpunit.xml` در گیت است؛ اگر جای دیگری هم استفاده شده عوضش کن
