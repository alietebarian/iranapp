<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AdsPhoto extends Model
{
    /**
     * Formats GD can decode and every Android version the app supports can display
     * (AVIF and HEIC fail one or the other). Validated against the file's content.
     */
    const FORMATS = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'];
    const MAX_KB = 10240;

    protected $table = 'ads_photo';
}
