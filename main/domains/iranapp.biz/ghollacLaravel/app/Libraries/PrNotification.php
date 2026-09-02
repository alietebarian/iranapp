<?php
namespace App\Libraries;

use App\User;
use LaravelFCM\Facades\FCM;
use LaravelFCM\Message\OptionsBuilder;
use LaravelFCM\Message\PayloadDataBuilder;
use LaravelFCM\Message\PayloadNotificationBuilder;

class PrNotification{
    private $data;
    private $body;
    private $user;
    private $title;

    private $numberSuccess;
    private $numberFailures;

    public function __construct()
    {
        $this->data = [];
        $this->body = null;
        $this->user = null;
    }
    public function setTitle($title){
        $this->title = $title;
        return $this;
    }

    public function setUser(User $user){
        $this->user = $user;
        return $this;
    }
    public function setUserArray($userArray){
        $this->user = $userArray;
        return $this;
    }
    public function setBody($body){
        $this->body = $body;
        return $this;
    }
    public function addData($key , $value){
        $this->data[$key] = $value;
        return $this;
    }

    public function send(){

//        if(env('APP_DEBUG') != true){
            $optionBuilder = new OptionsBuilder();
            $optionBuilder->setTimeToLive(60 * 20);

            $notificationBuilder = new PayloadNotificationBuilder($this->title);
            $notificationBuilder->setBody($this->body)
                ->setSound('default');

            $dataBuilder = new PayloadDataBuilder();
            $dataBuilder->addData($this->data);
            $data = $dataBuilder->build();
            $option = $optionBuilder->build();
            $notification = $notificationBuilder->build();

            if(is_array($this->user)){
                $tokensArr = [];
                foreach($this->user as $user){
                    $tokensArr[] = $user['fcm_token'];
                }

                $tokens = $tokensArr;
            }else{
                $tokens = [$this->user->fcm_token];
            }
            $downstreamResponse = FCM::sendTo($tokens, $option, $notification , $data);

            $this->numberFailures =  $downstreamResponse->numberFailure();
            $this->numberSuccess =  $downstreamResponse->numberModification();
//        }

    }

    public function numOfFailures(){
        return $this->numberFailures;
    }
    public function numOfSuccess(){
        return $this->numberSuccess;
    }
}