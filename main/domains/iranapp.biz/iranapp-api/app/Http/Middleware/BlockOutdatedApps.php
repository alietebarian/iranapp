<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

/**
 * Forces app versions older than 2.0.2 (versionCode 202) to update by refusing their API calls.
 *
 * Their update dialog has a «بیخیال» button that can't be taken away from installed copies, so
 * the server is the only place left to enforce it. Since 202 the app starts its User-Agent with
 * `IranApp/<versionCode>` (AppUserAgent in the app); older builds send the bare Android
 * defaults, `Dalvik/...` from Volley and `okhttp/...` from the ad upload screens. Those are
 * refused, except /api/app-version: an old app keeps asking it on every launch and keeps
 * showing its update dialog, and if the user skips that, nothing else loads.
 *
 * Browsers, the admin panel and anything else that doesn't look like the Android app pass.
 *
 * Off until BLOCK_OUTDATED_APPS=true: turn it on only once the new version is live on Cafe
 * Bazaar and on iranapp.biz/download, or users are refused with no update to install.
 */
class BlockOutdatedApps
{
    /** Paths an outdated app may still call. */
    private const ALLOWED = [
        'api/app-version',
    ];

    public function handle(Request $request, Closure $next): Response
    {
        if (! config('app.block_outdated_apps') || ! self::isOutdatedApp($request)) {
            return $next($request);
        }

        foreach (self::ALLOWED as $path) {
            if ($request->is($path)) {
                return $next($request);
            }
        }

        return response()->json([
            'status' => 426,
            'error' => 'app_outdated',
            'message' => 'نسخه ایران آپ شما قدیمی است. لطفاً نسخه جدید را از کافه‌بازار یا iranapp.biz/download نصب کنید.',
        ], 426);
    }

    public static function isOutdatedApp(Request $request): bool
    {
        $agent = (string) $request->userAgent();

        if (preg_match('#^IranApp/\d+#', $agent)) {
            return false;
        }

        return str_starts_with($agent, 'Dalvik/') || str_starts_with($agent, 'okhttp/');
    }
}
