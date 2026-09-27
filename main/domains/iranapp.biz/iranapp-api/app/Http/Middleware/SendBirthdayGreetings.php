<?php

namespace App\Http\Middleware;

use App\Support\Birthdays;
use Closure;
use Illuminate\Database\QueryException;
use Illuminate\Support\Facades\Log;

/**
 * Sends the day's birthday greetings on the first request after they are due.
 *
 * No cron runs on the host (see ReleaseScheduledNews). After the day's greetings are claimed this
 * is a single UPDATE that matches no row, and the send itself runs after the response.
 */
class SendBirthdayGreetings
{
    public function handle($request, Closure $next)
    {
        try {
            Birthdays::sendDueGreetings();
        } catch (QueryException $e) {
            // E.g. the birth_date migration has not been run: the app must keep working.
            Log::error('ارسال تبریک تولد انجام نشد: ' . $e->getMessage());
        }

        return $next($request);
    }
}
