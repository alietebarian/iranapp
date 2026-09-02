<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class NewsPhoto extends Model
{
    protected $table = 'news_photo';

    protected $fillable = [
        'news_id',
        'file_name',
    ];
}
