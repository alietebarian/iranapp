<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * An installation of the app, reported once by the app on first launch (POST /api/app-stats/installs).
 * See App\Support\InstallStats for how these are counted.
 */
class AppInstall extends Model
{
    const SOURCE_BAZAAR = 'bazaar';
    const SOURCE_GOOGLE_PLAY = 'google_play';
    const SOURCE_MYKET = 'myket';
    const SOURCE_DIRECT = 'direct';
    const SOURCE_OTHER = 'other';

    const SOURCE_LABELS = [
        self::SOURCE_BAZAAR => 'کافه بازار',
        self::SOURCE_GOOGLE_PLAY => 'گوگل پلی',
        self::SOURCE_MYKET => 'مایکت',
        self::SOURCE_DIRECT => 'فایل APK (نصب مستقیم)',
        self::SOURCE_OTHER => 'سایر فروشگاه ها',
    ];

    /** Installer package name (as Android reports it) → source. */
    const INSTALLERS = [
        'com.farsitel.bazaar' => self::SOURCE_BAZAAR,
        'com.android.vending' => self::SOURCE_GOOGLE_PLAY,
        'ir.mservices.market' => self::SOURCE_MYKET,
    ];

    /**
     * Installers that are just the system's package installer, i.e. the user opened an APK file
     * themselves (downloaded from a website, sent over a messenger, copied from another phone).
     */
    const DIRECT_INSTALLERS = [
        'com.google.android.packageinstaller',
        'com.android.packageinstaller',
        'com.samsung.android.packageinstaller',
        'com.miui.packageinstaller',
        'com.android.shell',
    ];

    protected $table = 'app_installs';

    protected $fillable = [
        'install_id',
        'source',
        'installer_package',
        'is_upgrade',
        'app_version',
        'android_version',
        'device_model',
    ];

    protected function casts(): array
    {
        return [
            'is_upgrade' => 'boolean',
        ];
    }

    public static function sourceFromInstaller(?string $installer): string
    {
        $installer = trim((string) $installer);
        if ($installer === '' || in_array($installer, self::DIRECT_INSTALLERS, true)) {
            return self::SOURCE_DIRECT;
        }

        return self::INSTALLERS[$installer] ?? self::SOURCE_OTHER;
    }

    public static function sourceLabel(string $source): string
    {
        return self::SOURCE_LABELS[$source] ?? $source;
    }
}
