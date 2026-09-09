<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Brand extends Model
{
    public $timestamps = false;
    protected $table = 'brand';

    public function models(){
        return $this->hasMany(\App\CarModel::class);
    }
}
