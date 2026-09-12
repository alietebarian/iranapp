<?php

namespace Tests\Unit;

use Carbon\Carbon;
use PHPUnit\Framework\TestCase;

/**
 * app/helpers.php is loaded through composer.json's autoload "files". It used to be loaded by
 * nothing, so every API list that calls getElapsedTime() died with "Call to undefined function".
 */
class HelpersTest extends TestCase
{
    protected function setUp(): void
    {
        parent::setUp();

        Carbon::setTestNow('2026-09-12 12:00:00');
    }

    protected function tearDown(): void
    {
        Carbon::setTestNow();

        parent::tearDown();
    }

    public function test_helpers_are_autoloaded(): void
    {
        $this->assertTrue(function_exists('getElapsedTime'));
        $this->assertTrue(function_exists('convertDateTimeToJalali'));
    }

    public function test_elapsed_time_is_described_in_persian(): void
    {
        $this->assertSame('3 ساعت قبل', getElapsedTime('2026-09-12 09:00:00'));
        $this->assertSame('2 روز قبل', getElapsedTime('2026-09-10 12:00:00'));
        $this->assertSame('لحظاتی قبل', getElapsedTime('2026-09-12 11:59:59'));
    }
}
