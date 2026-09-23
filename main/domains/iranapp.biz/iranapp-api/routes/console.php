<?php

use Illuminate\Foundation\Inspiring;
use Illuminate\Support\Facades\Artisan;
use Illuminate\Support\Facades\Schedule;

Artisan::command('inspire', function () {
    $this->comment(Inspiring::quote());
})->purpose('Display an inspiring quote');

// Requests already release scheduled news (see ReleaseScheduledNews); if a cron running
// `php artisan schedule:run` is ever set up, this keeps the pushes on time on quiet nights too.
Schedule::call(fn () => \App\Models\News::releaseDueNotifications())->everyMinute()->name('release-scheduled-news');
