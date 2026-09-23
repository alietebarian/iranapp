<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;
use Illuminate\Http\Request;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php',
        health: '/up',
    )
    ->withMiddleware(function (Middleware $middleware): void {
        // Persian/Arabic digit and letter normalisation, carried over from the legacy app.
        $middleware->web(append: [
            \App\Http\Middleware\ConvertArabicLettersToPersian::class,
            \App\Http\Middleware\convertFarsiNumbers::class,
            \App\Http\Middleware\ReleaseScheduledNews::class,
        ]);
        $middleware->api(prepend: [
            \App\Http\Middleware\AcceptTokenQueryParameter::class,
        ]);

        // API clients must never be redirected to a login page; returning null here lets
        // the AuthenticationException surface so it can be rendered as JSON below.
        $middleware->redirectGuestsTo(
            fn (Request $request) => $request->is('api/*') || $request->expectsJson()
                ? null
                : route('showAdminLoginForm')
        );
        $middleware->api(append: [
            \App\Http\Middleware\ConvertArabicLettersToPersian::class,
            \App\Http\Middleware\convertFarsiNumbers::class,
            \App\Http\Middleware\ReleaseScheduledNews::class,
        ]);
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        $exceptions->shouldRenderJsonWhen(
            fn (Request $request) => $request->is('api/*') || $request->expectsJson(),
        );

        // The mobile client switches on this exact shape to force a re-login, so an
        // unauthenticated API call must keep answering the way the legacy JWT stack did.
        $exceptions->render(function (\Illuminate\Auth\AuthenticationException $e, Request $request) {
            if ($request->is('api/*') || $request->expectsJson()) {
                return response()->json(['status' => 401, 'error' => 'token_expired'], 401);
            }

            return null;
        });
    })->create();
