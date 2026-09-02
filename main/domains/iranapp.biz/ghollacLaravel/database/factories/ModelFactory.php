<?php

/*
|--------------------------------------------------------------------------
| Model Factories
|--------------------------------------------------------------------------
|
| Here you may define all of your model factories. Model factories give
| you a convenient way to create models for testing and seeding your
| database. Just tell the factory how a default model should look.
|
*/

/** @var \Illuminate\Database\Eloquent\Factory $factory */
$factory->define(App\User::class, function (Faker\Generator $faker) {
    static $password;
    $verifyToken = rand(1111 , 9999);
    $randArr = [$verifyToken , null];
    $rand = $randArr[array_rand($randArr)];
    return [
        'first_name' => $faker->name,
        'last_name' => $faker->name,
        'mobile' => $faker->unique()->safeEmail,
        'email' => $faker->unique()->safeEmail,
        'verify_token' => $rand,
        'password' => bcrypt('secret'),
        'remember_token' => str_random(10),
        'is_mobile_verified' => $rand ? 1 : 0 ,
        'type' => 'user',
        'fcm_token' => null,
        'forget_password_token' => null,
        'send_news_notifications' => rand(0 , 1),
        'send_ads_notifications' => rand(0 , 1),
        'reset_password_token' => null
    ];
});

$factory->define(\App\Province::class , function(\Faker\Generator $faker){
    return [
        'name' => $faker->unique()->city
    ];
});

$factory->define(\App\City::class , function(\Faker\Generator $faker){
    return [
        'name' => $faker->unique()->city,
        'province_id' => function(){
            return factory(\App\Province::class)->create()->id;
        }
    ];
});

$factory->define(\App\Region::class , function(\Faker\Generator $faker){
    return [
        'name' => $faker->unique()->city,
        'city_id' => function(){
            return factory(\App\City::class)->create()->id;
        }
    ];
});

$factory->define(\App\Admin::class ,  function(\Faker\Generator $faker){
    return [
        'first_name' => $faker->firstName,
        'last_name' => $faker->lastName,
        'mobile' => $faker->unique()->phoneNumber,
        'email' => $faker->unique()->email,
        'remember_token' => str_random(10),
        'password' => bcrypt('secret'),
    ];
});

$factory->define(\App\News::class , function (\Faker\Generator $faker){
    return [
        'title' => $faker->text(400),
        'passage' => $faker->sentence(100)
    ];
});

$factory->define(\App\NewsPhoto::class , function(\Faker\Generator $faker){
    return [
        'news_id' => function(){
            return factory(\App\News::class)->create()->id;
        },
        'file_name' => $faker->image(storage_path('/test/images') , 100 , 100 , 'cats' , false)
    ];
});