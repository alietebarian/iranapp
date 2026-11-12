import type { CSSProperties } from 'react';
import { cx } from '@/lib/format';

/** The app's own vector drawables (res/drawable/*.xml), converted to SVG files. */
const vectorFiles = import.meta.glob('../assets/icons/*.svg', {
  eager: true,
  query: '?url',
  import: 'default',
}) as Record<string, string>;

export function vectorUrl(name: string): string {
  return vectorFiles[`../assets/icons/${name}.svg`] ?? vectorFiles['../assets/icons/ic_cat_default.svg'];
}

interface IconProps {
  /** An SVG path from @mdi/js (24 x 24 grid), the web twin of the app's Material icons. */
  path: string;
  size?: number | string;
  className?: string;
  style?: CSSProperties;
}

/** A Material icon drawn in the current text colour. */
export function Icon({ path, size = 24, className, style }: IconProps) {
  return (
    <svg
      viewBox="0 0 24 24"
      width={size}
      height={size}
      className={cx('shrink-0 fill-current', className)}
      style={style}
      aria-hidden="true"
    >
      <path d={path} />
    </svg>
  );
}

interface MaskIconProps {
  /** Any image whose alpha channel is the shape: a converted vector or one of the app's PNGs. */
  src: string;
  size?: number | string;
  /** Solid tint; defaults to the current text colour. */
  color?: string;
  /** White at the top left fading into the accent red, like GradientIconView on the dark pages. */
  gradient?: boolean;
  className?: string;
  style?: CSSProperties;
}

/**
 * Android tints icons at runtime (android:tint). The web equivalent: use the image only as a
 * mask and paint the colour (or the landing page's gradient) through it.
 */
export function MaskIcon({ src, size = 24, color, gradient, className, style }: MaskIconProps) {
  const mask = `url("${src}") center / contain no-repeat`;
  return (
    <span
      aria-hidden="true"
      className={cx('inline-block shrink-0', className)}
      style={{
        // size 0 = sized by a class (e.g. the screen-relative "size-sdp-20") or by `style`.
        ...(size ? { width: size, height: size } : null),
        WebkitMask: mask,
        mask,
        background: gradient ? 'linear-gradient(135deg, #ffffff, var(--color-home-accent))' : (color ?? 'currentColor'),
        ...style,
      }}
    />
  );
}

/** One of the app's vector drawables by its resource name, e.g. "ic_cat_health". */
export function VectorIcon({ name, ...rest }: Omit<MaskIconProps, 'src'> & { name: string }) {
  return <MaskIcon src={vectorUrl(name)} {...rest} />;
}
