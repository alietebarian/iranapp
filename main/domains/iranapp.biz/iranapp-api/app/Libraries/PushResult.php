<?php

namespace App\Libraries;

/**
 * Result of a push send.
 *
 * Mirrors the accessors the old brozot/laravel-fcm "downstream response" exposed, so the
 * controllers that report success/failure counts did not have to change.
 */
class PushResult
{
    public function __construct(
        private int $successes,
        private int $failures,
    ) {
    }

    public function numberSuccess(): int
    {
        return $this->successes;
    }

    public function numberFailure(): int
    {
        return $this->failures;
    }

    public function numberModification(): int
    {
        return $this->successes;
    }
}
