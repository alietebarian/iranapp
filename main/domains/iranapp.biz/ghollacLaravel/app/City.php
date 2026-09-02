<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class City extends Model
{
    protected $table = 'city';
    protected $fillable = [
        'province_id',
        'name'
    ];
    public $timestamps = false;

    public function province(){
        return $this->belongsTo('App\Province');
    }

    public function region(){
        return $this->hasMany('App\Region');
    }
}
