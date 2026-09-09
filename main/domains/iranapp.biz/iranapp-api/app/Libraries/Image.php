<?php

namespace App\Libraries;

use Intervention\Image\Drivers\Gd\Driver;
use Intervention\Image\ImageManager;
use Intervention\Image\Interfaces\ImageInterface;

/**
 * Thin compatibility layer over Intervention Image v4.
 *
 * The controllers were written against v2, whose API differs in ways that are easy to
 * get subtly wrong — `insert()` took (source, position, x, y) but v4 takes
 * (source, x, y, position), and `resize()` took a constraint callback that v4 replaced
 * with `scale()`. Adapting here keeps every existing call site correct and unchanged.
 */
class Image
{
    private static ?ImageManager $manager = null;

    public static function manager(): ImageManager
    {
        return self::$manager ??= ImageManager::usingDriver(new Driver());
    }

    /** Loads an image, mirroring the old Image::make(). */
    public static function make(string $path): self
    {
        return new self(self::manager()->decodePath($path), $path);
    }

    public function __construct(
        private ImageInterface $image,
        private ?string $path = null,
    ) {
    }

    /**
     * v2 order: insert($source, $position, $x, $y)
     * v4 order: insert($source, $x, $y, $position)
     */
    public function insert($source, string $position = 'top-left', int $x = 0, int $y = 0): self
    {
        $this->image->insert($source, $x, $y, $position);

        return $this;
    }

    /**
     * v2 allowed resize($w, $h, fn($c) => $c->aspectRatio()); v4 expresses the same
     * intent as scale(), which never distorts.
     */
    public function resize($width = null, $height = null, ?callable $callback = null): self
    {
        if ($callback !== null || $width === null || $height === null) {
            $this->image->scale($width, $height);
        } else {
            $this->image->resize($width, $height);
        }

        return $this;
    }

    /** Saves back over the original path when none is given, as v2 did. */
    public function save(?string $path = null, ...$options): self
    {
        $this->image->save($path ?? $this->path, ...$options);

        return $this;
    }

    public function getImage(): ImageInterface
    {
        return $this->image;
    }

    /** Anything not adapted above passes straight through to the v4 image. */
    public function __call(string $method, array $arguments)
    {
        $result = $this->image->{$method}(...$arguments);

        return $result instanceof ImageInterface ? $this : $result;
    }
}
