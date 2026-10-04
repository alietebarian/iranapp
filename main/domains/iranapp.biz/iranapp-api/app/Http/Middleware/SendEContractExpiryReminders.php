<?php

namespace App\Http\Middleware;

use App\Support\EContractExpiry;
use Closure;
use Illuminate\Database\QueryException;
use Illuminate\Support\Facades\Log;

/**
 * Sends the "your contract ends in 15 days" reminders on requests, since no cron runs on the host
 * (see ReleaseScheduledNews). Checks at most every few minutes; pushes go out after the response.
 */
class SendEContractExpiryReminders
{
    public function handle($request, Closure $next)
    {
        try {
            EContractExpiry::sendDueRemindersThrottled();
        } catch (QueryException $e) {
            // E.g. the billing migration has not been run: the app must keep working.
            Log::error('یادآوری پایان قراردادها انجام نشد: ' . $e->getMessage());
        }

        return $next($request);
    }
}
