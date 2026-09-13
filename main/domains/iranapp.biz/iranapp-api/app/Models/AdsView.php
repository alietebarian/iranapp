<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class AdsView extends Model
{
    protected $table = 'ads_view';

    // A view is never edited, so the table has no updated_at.
    const UPDATED_AT = null;
}
