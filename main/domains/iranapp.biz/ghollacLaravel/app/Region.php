<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Region extends Model
{
    protected $table = 'region';
    public $timestamps = false;
    protected $fillable = [
        'name',
        'city_id'
    ];
    public function city(){
        return $this->belongsTo(City::class);
    }
}
