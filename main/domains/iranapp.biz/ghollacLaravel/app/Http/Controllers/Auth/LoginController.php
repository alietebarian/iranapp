<?php

namespace App\Http\Controllers\Auth;

use App\Http\Controllers\Controller;
use App\Http\Requests\Admin\LoginRequest;
use Illuminate\Foundation\Auth\AuthenticatesUsers;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

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
    protected $redirectTo = '/admin/dashboard';

    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('guest')->except('logout');
    }

    public function showLoginForm()
    {
        return view('admin.login');
    }

    public function login(LoginRequest $request)
    {
        if(filter_var($request->login , FILTER_VALIDATE_EMAIL)){
            $field = 'email';
        }else{
            $field = 'mobile';
        }
        $remember = $request->has('remember_me') ? true : false;
        if(Auth::guard('admin')->attempt([$field => $request->login , 'password' => $request->password] , $remember)){
            return redirect()->route('showAdminDashboard');
        }else{
            $error = new \stdClass();
            $error->title = 'خطا در ورود به سیستم';
            $error->msg = 'اطلاعات ورود به سیستم نادرست است. لطفا مجددا تلاش کنید.';
            return redirect()->back()->with('error_msg' , $error );
        }
    }
}
