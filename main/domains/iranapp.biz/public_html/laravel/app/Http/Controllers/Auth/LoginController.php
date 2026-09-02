<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Libraries\Swal;
use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Http\Request;

class LoginController extends Controller
{
    /*
    |--------------------------------------------------------------------------
    | Login Controller
    |--------------------------------------------------------------------------
    |
    | This controller handles authenticating users for the application and
    | redirecting them to your home screen. The controller uses a trait
    | to conveniently provide its functionality to your applications.
    |
    */

    use AuthenticatesUsers;

    /**
     * Where to redirect users after login.
     *
     * @var string
     */
    protected $redirectTo = '/home';

    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('guest')->except('logout');
    }

    public function login(Request $request)
    {
        $this->validate($request , [
            'email_in_login' => 'required|string|email',
            'password_in_login' => 'required|string'
        ] , [
            'email_in_login.required' => 'وارد کردن ایمیل الزامی است.',
            'email_in_login.string' => 'ایمیل نامعتبر است.',
            'email_in_login.email' => 'ایمیل نامعتبر است.',
            'password_in_login.required' => 'وارد کردن رمز عبور الزامی است.',
            'password_in_login.string' => 'رمز عبور وارد شده نامعتبر است.'
        ]);

        if(auth()->attempt([
            'email' => $request->email_in_login,
            'password' => $request->password_in_login
        ])){
            return redirect()->to('/my-account');
        }else{
            Swal::error('خطا' , 'اطلاعات ورود به سیستم اشتباه است.');
            return redirect()->back();
        }
    }

    public function logout(Request $request)
    {
        auth()->logout();
        return redirect()->back();
    }
}
