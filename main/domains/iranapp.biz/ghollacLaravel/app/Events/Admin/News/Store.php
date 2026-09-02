<?php

namespace App\Events\Admin\News;

use App\News;
use Illuminate\Broadcasting\Channel;
use Illuminate\Queue\SerializesModels;
use Illuminate\Broadcasting\PrivateChannel;
use Illuminate\Broadcasting\PresenceChannel;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;

class Store
{
    use Dispatchable, InteractsWithSockets, SerializesModels;

    public $fcmTokens;
    public $news;

    /**
     * Create a new event instance.
     *
     * @param array $tokens
     * @param News $news
     */
    public function __construct(array $tokens , News $news)
    {
        $this->fcmTokens = $tokens;
        $this->news = $news;
    }

}
