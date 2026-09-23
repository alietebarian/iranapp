<?php

namespace App\Models;

use App\Jobs\SendNewsPushNotification;
use Carbon\Carbon;
use Illuminate\Database\Eloquent\Model;

class News extends Model
{
    protected $table = 'news';
    protected $fillable = [
        'title',
        'passage',
        'publish_at',
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
        return $this->hasMany(NewsPhoto::class);
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

    /** Limits a news query to what the app may show: items whose publish time has arrived. */
    public static function wherePublished($query)
    {
        return $query->where(function ($q) {
            $q->whereNull('publish_at')->orWhere('publish_at', '<=', self::nowInAppTimezone());
        });
    }

    /**
     * Sends the push notification of every scheduled news item whose time has come.
     *
     * No cron runs on the host, so this is called from requests the app and the admin panel make
     * anyway. Each row is claimed with a conditional update first, so two requests arriving at
     * once cannot both send the same notification.
     */
    public static function releaseDueNotifications(): void
    {
        $dueIds = self::query()
            ->where('notify_on_publish', true)
            ->where('publish_at', '<=', self::nowInAppTimezone())
            ->pluck('id');

        foreach ($dueIds as $id) {
            $claimed = self::query()->where('id', $id)->where('notify_on_publish', true)
                ->update(['notify_on_publish' => false]);
            if ($claimed) {
                SendNewsPushNotification::dispatchAfterResponse($id);
            }
        }
    }

    /** jdf::jdate changes PHP's default timezone, so now() alone cannot be trusted here. */
    private static function nowInAppTimezone(): string
    {
        return Carbon::now(config('app.timezone'))->format('Y-m-d H:i:s');
    }
}
