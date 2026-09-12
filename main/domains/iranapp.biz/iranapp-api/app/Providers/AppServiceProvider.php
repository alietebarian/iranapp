<?php

namespace App\Providers;

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
    }
}
