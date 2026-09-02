<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Order extends Model
{
    protected $table = 'orders';

    protected $fillable = [
        'product_id',
        'user_id' ,
        'paid_price',
        'user_name' ,
        'password'
    ];

    public function user(){
        return $this->belongsTo(User::class , 'user_id');
    }
}
