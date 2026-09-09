<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Province extends Model
{
    protected $table = 'province';
    public $timestamps = false;
    protected $fillable = [
        'name'
    ];
    public function city(){
        return $this->hasMany('App\City');
    }
}
