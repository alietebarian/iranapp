<?php

namespace App\Libraries;

use App\Models\User;
use Kreait\Firebase\Contract\Messaging;
use Kreait\Firebase\Exception\FirebaseException;
use Kreait\Firebase\Messaging\CloudMessage;
use Kreait\Firebase\Messaging\Notification;
use Illuminate\Support\Facades\Log;

/**
 * Push notification sender.
 *
 * Rewritten on top of FCM HTTP v1 (kreait/laravel-firebase). The previous
 * implementation used brozot/laravel-fcm, which talks to the legacy FCM endpoint
 * Google shut down in June 2024 — so pushes were failing in production.
 *
 * The public API (setTitle/setBody/setUser/setUserArray/addData/send and the two
 * counters) is unchanged so the calling controllers did not need to be touched.
 */
class PrNotification
{
    private $data;
    private $body;
    private $user;
    private $title;

    private $numberSuccess = 0;
    private $numberFailures = 0;

    public function __construct()
    {
        $this->data = [];
        $this->body = null;
        $this->user = null;
    }

    public function setTitle($title)
    {
        $this->title = $title;

        return $this;
    }

    public function setUser(User $user)
    {
        $this->user = $user;

        return $this;
    }

    public function setUserArray($userArray)
    {
        $this->user = $userArray;

        return $this;
    }

    public function setBody($body)
    {
        $this->body = $body;

        return $this;
    }

    public function addData($key, $value)
    {
        $this->data[$key] = $value;

        return $this;
    }

    public function send()
    {
        $tokens = $this->collectTokens();

        $this->numberSuccess = 0;
        $this->numberFailures = 0;

        if (empty($tokens)) {
            return;
        }

        // FCM HTTP v1 wants every data value as a string.
        $data = array_map(fn ($v) => is_scalar($v) ? (string) $v : json_encode($v), $this->data);

        $message = CloudMessage::new()
            ->withNotification(Notification::create($this->title, $this->body))
            ->withData($data)
            ->withDefaultSounds();

        try {
            /** @var Messaging $messaging */
            $messaging = app(Messaging::class);
            $report = $messaging->sendMulticast($message, $tokens);

            $this->numberSuccess = $report->successes()->count();
            $this->numberFailures = $report->failures()->count();

            // Tokens the device no longer owns should not be retried forever.
            $stale = $report->unknownTokens() + $report->invalidTokens();
            if (! empty($stale)) {
                User::whereIn('fcm_token', $stale)->update(['fcm_token' => null]);
            }
        } catch (FirebaseException | \Throwable $e) {
            $this->numberFailures = count($tokens);
            Log::error('ارسال نوتیفیکیشن ناموفق بود: ' . $e->getMessage());
        }
    }

    /**
     * Sends straight to a list of device tokens.
     *
     * Several controllers built FCM payloads inline against the old package; this gives
     * them a single entry point and returns a result exposing the same accessors those
     * call sites already use.
     */
    public static function sendToTokens(array $tokens, string $title, ?string $body, array $data = []): PushResult
    {
        $tokens = array_values(array_unique(array_filter($tokens)));

        if (empty($tokens)) {
            return new PushResult(0, 0);
        }

        $data = array_map(fn ($v) => is_scalar($v) ? (string) $v : json_encode($v), $data);

        $message = CloudMessage::new()
            ->withNotification(Notification::create($title, $body))
            ->withData($data)
            ->withDefaultSounds();

        try {
            /** @var Messaging $messaging */
            $messaging = app(Messaging::class);
            $report = $messaging->sendMulticast($message, $tokens);

            $stale = $report->unknownTokens() + $report->invalidTokens();
            if (! empty($stale)) {
                User::whereIn('fcm_token', $stale)->update(['fcm_token' => null]);
            }

            return new PushResult($report->successes()->count(), $report->failures()->count());
        } catch (FirebaseException | \Throwable $e) {
            Log::error('ارسال نوتیفیکیشن ناموفق بود: ' . $e->getMessage());

            return new PushResult(0, count($tokens));
        }
    }

    /** @return string[] */
    private function collectTokens(): array
    {
        if (is_array($this->user)) {
            $tokens = [];
            foreach ($this->user as $user) {
                $token = is_array($user) ? ($user['fcm_token'] ?? null) : ($user->fcm_token ?? null);
                if (! empty($token)) {
                    $tokens[] = $token;
                }
            }

            return array_values(array_unique($tokens));
        }

        $token = $this->user->fcm_token ?? null;

        return empty($token) ? [] : [$token];
    }

    public function numOfFailures()
    {
        return $this->numberFailures;
    }

    public function numOfSuccess()
    {
        return $this->numberSuccess;
    }
}
