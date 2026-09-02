<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class CarModel extends Model
{
    protected $table = 'model';
    protected $fillable = [
        'name'
    ];
    public function brand(){
        return $this->belongsTo(Brand::class);
    }
}
