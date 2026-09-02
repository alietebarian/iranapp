<?php

/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

use Illuminate\Support\Facades\Route;

Route::get('/' , 'HomeController@index');
Route::group(['middleware' => 'guest'] , function(){
    Route::get('/registration' , 'Auth\RegisterController@showRegistrationForm')->name('register.index');
    Route::post('/register' , 'Auth\RegisterController@store')->name('register.store');
    Route::post('/login' , 'Auth\LoginController@login')->name('user.login');
});
Route::group(['middleware' => 'auth'] , function(){
    Route::get('/product/{product}/purchase' , 'ProductController@purchase')->name('purchase_product');
    Route::post('/orders' , 'OrderController@store')->name('order.store');
    Route::get('/my-account' , 'HomeController@profileShow')->name('user.profile');
    Route::get('/logout' , 'Auth\LoginController@logout')->name('user.logout');
});
