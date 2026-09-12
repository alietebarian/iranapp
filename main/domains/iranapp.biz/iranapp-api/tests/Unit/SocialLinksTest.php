<?php

namespace Tests\Unit;

use App\Support\SocialLinks;
use PHPUnit\Framework\Attributes\DataProvider;
use PHPUnit\Framework\TestCase;

class SocialLinksTest extends TestCase
{
    public static function telegramInputs(): array
    {
        return [
            'bare username' => ['iranapp', 'https://t.me/iranapp'],
            'with @' => ['@iranapp', 'https://t.me/iranapp'],
            'surrounding spaces' => ['  @iranapp  ', 'https://t.me/iranapp'],
            'domain without scheme' => ['t.me/iranapp', 'https://t.me/iranapp'],
            'full https link' => ['https://t.me/iranapp', 'https://t.me/iranapp'],
            'http and telegram.me' => ['http://telegram.me/iranapp/', 'https://t.me/iranapp'],
            'upper-case host' => ['HTTPS://T.ME/IranApp', 'https://t.me/IranApp'],
            'invite link keeps its path' => ['https://t.me/+AbCdEf123', 'https://t.me/+AbCdEf123'],
            'post link drops the query' => ['t.me/iranapp/42?single', 'https://t.me/iranapp/42'],
            'phone number' => ['09121234567', null],
            'persian text' => ['آیدی من', null],
            'other site' => ['https://example.com/iranapp', null],
            'domain only' => ['https://t.me/', null],
        ];
    }

    #[DataProvider('telegramInputs')]
    public function test_telegram(string $input, ?string $expected): void
    {
        $this->assertSame($expected, SocialLinks::telegram($input));
    }

    public static function instagramInputs(): array
    {
        return [
            'bare username' => ['iran.app_1', 'https://instagram.com/iran.app_1'],
            'with @' => ['@iranapp', 'https://instagram.com/iranapp'],
            'domain without scheme' => ['instagram.com/iranapp', 'https://instagram.com/iranapp'],
            'www and share query' => ['https://www.instagram.com/iranapp/?igsh=abc123', 'https://instagram.com/iranapp'],
            'short domain' => ['instagr.am/iranapp', 'https://instagram.com/iranapp'],
            'post link' => ['https://instagram.com/p/Cxyz123/', 'https://instagram.com/p/Cxyz123'],
            'space inside' => ['iran app', null],
            'other site' => ['https://t.me/iranapp', null],
        ];
    }

    #[DataProvider('instagramInputs')]
    public function test_instagram(string $input, ?string $expected): void
    {
        $this->assertSame($expected, SocialLinks::instagram($input));
    }
}
