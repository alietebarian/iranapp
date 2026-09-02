<?php

namespace App\Http\Controllers\Auth;

use App\User;
use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Validator;
use Illuminate\Foundation\Auth\RegistersUsers;

class RegisterController extends Controller
{
    /*
    |--------------------------------------------------------------------------
    | Register Controller
    |--------------------------------------------------------------------------
    |
    | This controller handles the registration of new users as well as their
    | validation and creation. By default this controller uses a trait to
    | provide this functionality without requiring any additional code.
    |
    */

    use RegistersUsers;

    /**
     * Where to redirect users after registration.
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
        $this->middleware('guest');
    }

    /**
     * Get a validator for an incoming registration request.
     *
     * @param  array  $data
     * @return \Illuminate\Contracts\Validation\Validator
     */
    protected function validator(array $data)
    {
        return Validator::make($data, [
            'name' => ['required', 'string', 'max:255'],
            'email' => ['required', 'string', 'email', 'max:255', 'unique:users'],
            'password' => ['required', 'string', 'min:6', 'confirmed'],
        ]);
    }

    /**
     * Create a new user instance after a valid registration.
     *
     * @param  array  $data
     * @return \App\User
     */
    protected function create(array $data)
    {
        return User::create([
            'name' => $data['name'],
            'email' => $data['email'],
            'password' => Hash::make($data['password']),
        ]);
    }

    public function store(Request $request){
        $this->validate($request , [
            'name' => 'required|string|min:3|max:300',
            'email' => 'required|string|email|unique:users,email',
            'password' => 'required|confirmed'
        ] , [
            'name.required' => 'نام الزامی است.',
            'name.string' => 'نام نامعتبر است.',
            'name.min' => 'نام حداقل باید 3 کاراکتر باشد.',
            'name.max' => 'نام حداکثر می تواند 300 کاراکتر باشد.',
            'email.required' => 'ایمیل الزامی است.',
            'email.string' => 'ایمیل نامعتبر است.',
            'email.email' => 'اینیل نامعتبر است.',
            'email.unique' => 'ایمیل وارد شده از قبل توسط کاربران دیگر ثبت شده است.',
            'password.required' => 'رمز عبور الزامی است.',
            'password.confirmed' => 'رمز عبور های وارد شده مطابقت ندارند.'
        ]);
        $user = new User();
        $user->name = $request->name;
        $user->email = $request->email;
        $user->password = Hash::make($request->password);
        $user->save();
        auth()->login($user);
        return redirect()->to('/my-account');
    }

    public function showRegistrationForm()
    {
        return view('home.registration');
    }
}
