<?php

namespace App\Http\Middleware;

use App\Models\News;
use Closure;
use Illuminate\Database\QueryException;
use Illuminate\Support\Facades\Log;

/**
 * Sends the notifications of scheduled news whose publish time has come.
 *
 * No cron runs on the host, so every app and admin request checks; that keeps a scheduled push
 * close to its time instead of waiting for someone to open the news list. The send itself runs
 * after the response (see SendNewsPushNotification), so the request is not slowed down by it.
 */
class ReleaseScheduledNews
{
    public function handle($request, Closure $next)
    {
        try {
            News::releaseDueNotifications();
        } catch (QueryException $e) {
            // E.g. the publish_at migration has not been run: the app must keep working.
            Log::error('انتشار اخبار زمان بندی شده انجام نشد: ' . $e->getMessage());
        }

        return $next($request);
    }
}
