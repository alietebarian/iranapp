<?php

namespace App\Http\Middleware;

use Closure;
use DebugBar\DebugBar;
use Illuminate\Support\Facades\Auth;

class DisplayingDebugbar
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        if(Auth::guard('admin')->check() && Auth::guard('admin')->user()->mobile == '09139665376'){
            app('debugbar')->enable();
        }else{
            app('debugbar')->disable();
        }
        return $next($request);
    }
}
