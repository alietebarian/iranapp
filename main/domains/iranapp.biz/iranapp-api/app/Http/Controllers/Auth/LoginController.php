<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Http\Requests\Admin\LoginRequest;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

/**
 * Admin panel login. The legacy version pulled in Illuminate's AuthenticatesUsers
 * trait but overrode every method it used, so the trait is simply dropped here.
 * The `guest:admin` middleware is applied on the route group instead of in the
 * constructor, which modern Laravel no longer supports.
 */
class LoginController extends Controller
{
    protected $redirectTo = '/admin/dashboard';

    public function showLoginForm()
    {
        return view('admin.login');
    }

    public function login(LoginRequest $request)
    {
        $field = filter_var($request->login, FILTER_VALIDATE_EMAIL) ? 'email' : 'mobile';
        $remember = $request->has('remember_me');

        $credentials = [$field => $request->login, 'password' => $request->password];

        if (Auth::guard('admin')->attempt($credentials, $remember)) {
            $request->session()->regenerate();

            return redirect()->route('showAdminDashboard');
        }

        $error = new \stdClass();
        $error->title = 'خطا در ورود به سیستم';
        $error->msg = 'اطلاعات ورود به سیستم نادرست است. لطفا مجددا تلاش کنید.';

        return redirect()->back()->with('error_msg', $error);
    }

    public function logout(Request $request)
    {
        Auth::guard('admin')->logout();
        $request->session()->invalidate();
        $request->session()->regenerateToken();

        return redirect()->route('showAdminLoginForm');
    }
}
