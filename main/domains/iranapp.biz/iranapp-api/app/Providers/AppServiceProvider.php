<?php

namespace App\Providers;

use App\Models\AdminNotification;
use App\Models\EContract;
use Illuminate\Database\Eloquent\Collection;
use Illuminate\Database\QueryException;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\View;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        // Without a timeout, an FCM request Google never answers (as can happen from the Iranian
        // host) hangs until PHP's execution-time limit. FIREBASE_HTTP_CLIENT_TIMEOUT overrides this.
        $firebaseTimeout = 'firebase.projects.' . config('firebase.default') . '.http_client_options.timeout';
        if (config($firebaseTimeout) === null) {
            config([$firebaseTimeout => 10]);
        }

        // The bell in the admin panel's top bar. No cron runs on the host, so ads coming up for
        // expiry are picked up whenever an admin page is rendered.
        View::composer('admin.master', function ($view) {
            $notifications = new Collection();
            if (Auth::guard('admin')->check()) {
                try {
                    AdminNotification::syncExpiringAds();
                    $notifications = AdminNotification::unread();
                } catch (QueryException $e) {
                    // E.g. the admin_notifications migration has not been run: the panel must still open.
                    Log::error('اعلان های پنل مدیریت بارگذاری نشد: ' . $e->getMessage());
                }
            }
            $view->with('adminNotifications', $notifications);

            // Badge next to "قراردادهای الکترونیک" in the sidebar.
            $pendingEContracts = 0;
            if (Auth::guard('admin')->check()) {
                try {
                    $pendingEContracts = EContract::where('status', EContract::STATUS_PENDING)->count();
                } catch (QueryException $e) {
                    Log::error('تعداد قراردادهای الکترونیک بارگذاری نشد: ' . $e->getMessage());
                }
            }
            $view->with('pendingEContracts', $pendingEContracts);
        });
    }
}
