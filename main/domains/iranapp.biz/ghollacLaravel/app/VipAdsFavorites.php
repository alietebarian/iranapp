<?php

namespace App;

use GuzzleHttp\Psr7\Request;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class VipAdsFavorites extends Model
{
    protected $table = 'vip_ads_favorite';
    private $dbQuery;
    public $timestamps = false;
    const FIELDS = 'users.id as user_id , users.first_name as user_first_name , users.last_name as user_last_name,
    vip_ads_favorite.ads_type , vip_ads_favorite.ads_id as ads_id';
    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->dbQuery = DB::table('vip_ads_favorite')
            ->join('users' , 'users.id' , '=' , 'vip_ads_favorite.user_id');
    }
    public function dbSelect($fields){
        $this->dbQuery = $this->dbQuery->selectRaw($fields);
        return $this->dbQuery;
    }

    protected $fillable = [
        'ads_id',
        'user_id',
        'ads_type'
    ];
}
