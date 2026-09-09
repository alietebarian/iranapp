<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Laravel\Sanctum\HasApiTokens;


class Visitor extends Authenticatable
{
    use HasApiTokens;
    protected $table = 'visitors';


}
