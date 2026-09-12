<?php

namespace App\Http\Requests\Admin\Concerns;

use App\Support\SocialLinks;

/**
 * Lets admins type just a username in the Telegram / Instagram fields of the ad forms; the
 * value is stored as a full link (see SocialLinks).
 */
trait NormalizesSocialLinks
{
    protected function prepareForValidation(): void
    {
        foreach (['telegram', 'instagram'] as $field) {
            $value = $this->input($field);
            if (! is_string($value) || trim($value) === '') {
                continue;
            }

            $link = $field === 'telegram' ? SocialLinks::telegram($value) : SocialLinks::instagram($value);

            // An unrecognisable value is kept as typed so the regex rule below reports it.
            $this->merge([$field => $link ?? $value]);
        }
    }

    protected function socialLinkRules(): array
    {
        return [
            'telegram' => ['nullable', 'string', 'regex:' . SocialLinks::TELEGRAM_URL_PATTERN],
            'instagram' => ['nullable', 'string', 'regex:' . SocialLinks::INSTAGRAM_URL_PATTERN],
        ];
    }

    protected function socialLinkMessages(): array
    {
        return [
            'telegram.regex' => 'آیدی تلگرام نامعتبر است؛ آیدی (مثلاً iranapp یا @iranapp) یا لینک t.me را وارد کنید.',
            'instagram.regex' => 'آیدی اینستاگرام نامعتبر است؛ آیدی (مثلاً iranapp یا @iranapp) یا لینک instagram.com را وارد کنید.',
        ];
    }
}
