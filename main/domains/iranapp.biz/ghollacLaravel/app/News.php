<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class News extends Model
{
    protected $table = 'news';
    protected $fillable = [
        'title',
        'passage',
    ];

//    protected $appends = [
//        'created_at_fa',
//        'updated_at_fa',
//        'photos'
//    ];

    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);

    }

    public function photo(){
        return $this->hasMany('App\NewsPhoto');
    }

    public function setCreatedAtFaAttribute($value){
        $this->attributes['created_at_fa'] = $value;
    }
    public function setUpdatedAtFaAttribute($value){
        $this->attributes['updated_at_fa'] = $value;
    }
    public function setPhotosAttribute($value){
        $this->attributes['photos'] = $value;
    }
    public function getCreatedAtFaAttribute(){
        return $this->attributes['created_at_fa'];
    }
    public function getUpdatedAtFaAttribute(){
        return $this->attributes['updated_at_fa'];
    }
    public function getPhotosAttribute(){
        return $this->attributes['photos'];
    }

}
