<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

/**
 * The mobile client sends its token as a `?token=` query parameter on most calls and
 * as an Authorization header only on the multipart uploads. Sanctum reads the header,
 * so promote the query parameter into one when the header is absent.
 */
class AcceptTokenQueryParameter
{
    public function handle(Request $request, Closure $next)
    {
        if (! $request->bearerToken() && $request->filled('token')) {
            $request->headers->set('Authorization', 'Bearer ' . $request->query('token', $request->input('token')));
        }

        return $next($request);
    }
}
