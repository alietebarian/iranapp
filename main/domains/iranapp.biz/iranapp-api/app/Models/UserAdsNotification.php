<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

class UserAdsNotification extends Model
{
    protected $table = 'user_ads_notification';
    protected $guarded = ['id'];

    private $dbQuery;
    const FIELDS = 'user_ads_notification.id , users.first_name , users.last_name , users.mobile , ads.title as ads_title , user_ads_notification.created_at';
    public function __construct(array $attributes = [])
    {
        parent::__construct($attributes);
        $this->dbQuery = DB::table('user_ads_notification')
            ->join('users' , 'users.id' , 'user_ads_notification.user_id')
            ->join('ads' , 'ads.id' , '=' , 'user_ads_notification.ads_id');
    }

    public function dbSelect($fields){
        $this->dbQuery  = $this->dbQuery->selectRaw($fields);
        return $this->dbQuery;
    }
}
