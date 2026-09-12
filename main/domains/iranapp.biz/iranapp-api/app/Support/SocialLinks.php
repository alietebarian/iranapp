<?php

namespace App\Support;

/**
 * Turns whatever an admin types into a Telegram / Instagram field — a bare username, "@name",
 * "t.me/name", a full https link — into one canonical https URL. The Android app opens the stored
 * value as-is and only prepends "http://" when the scheme is missing, so a bare username used to
 * open the dead address "http://name".
 */
final class SocialLinks
{
    public const TELEGRAM_URL_PATTERN = '#^https://t\.me/\S+$#';

    public const INSTAGRAM_URL_PATTERN = '#^https://instagram\.com/\S+$#';

    /** Returns null when the value is neither a Telegram username nor a Telegram link. */
    public static function telegram(string $value): ?string
    {
        // Telegram usernames start with a letter, so e.g. a phone number is reported as invalid.
        return self::normalize($value, '(?:t\.me|telegram\.me|telegram\.dog)', '[A-Za-z][A-Za-z0-9_]{2,63}', 'https://t.me/');
    }

    /** Returns null when the value is neither an Instagram username nor an Instagram link. */
    public static function instagram(string $value): ?string
    {
        return self::normalize($value, '(?:instagram\.com|instagr\.am)', '[A-Za-z0-9._]{1,30}', 'https://instagram.com/');
    }

    private static function normalize(string $value, string $hosts, string $username, string $base): ?string
    {
        $value = trim($value);

        // A link on the right domain, with or without scheme/www: keep its path (a channel, an
        // invite link, a post) and drop any query string or fragment.
        if (preg_match('#^(?:https?://)?(?:www\.)?' . $hosts . '/([^\s?\#]+)#i', $value, $m)) {
            $path = trim($m[1], '/');

            return $path === '' ? null : $base . $path;
        }

        if (preg_match('#^@?(' . $username . ')/?$#', $value, $m)) {
            return $base . $m[1];
        }

        return null;
    }
}
