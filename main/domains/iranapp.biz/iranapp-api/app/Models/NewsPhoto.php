<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class NewsPhoto extends Model
{
    protected $table = 'news_photo';

    protected $fillable = [
        'news_id',
        'file_name',
    ];
}
