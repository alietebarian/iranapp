import { useEffect, type ButtonHTMLAttributes, type CSSProperties, type ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { useNavigate } from 'react-router';
import { mdiArrowRight, mdiCheck, mdiClose } from '@mdi/js';
import logoUrl from '@/assets/img/iran_app_logo1.png';
import { cx } from '@/lib/format';
import { useUi } from '@/store/ui';
import { Icon, MaskIcon } from './Icon';

/** Indeterminate circular progress (Android's default ProgressBar). */
export function Spinner({ size = 40, color = 'var(--color-accent-gray)', className }: { size?: number; color?: string; className?: string }) {
  return (
    <svg
      viewBox="0 0 50 50"
      width={size}
      height={size}
      className={cx('shrink-0', className)}
      style={{ animation: 'spin-progress 0.9s linear infinite' }}
      role="progressbar"
      aria-label="در حال بارگذاری"
    >
      <circle cx="25" cy="25" r="20" fill="none" stroke={color} strokeWidth="4.5" strokeLinecap="round" strokeDasharray="90 150" />
    </svg>
  );
}

/** Thin indeterminate bar (ProgressBar.Horizontal), used for "loading more" and form lookups. */
export function LinearProgress({ className, color = 'var(--color-accent-gray)' }: { className?: string; color?: string }) {
  return (
    <div className={cx('relative h-1 w-full overflow-hidden', className)} style={{ background: 'rgb(0 0 0 / 0.1)' }} role="progressbar">
      <div className="absolute inset-y-0" style={{ background: color, animation: 'linear-progress 1.4s ease-in-out infinite' }} />
    </div>
  );
}

/** Centred spinner filling the available space. */
export function Loading({ className, color }: { className?: string; color?: string }) {
  return (
    <div className={cx('flex flex-1 items-center justify-center py-10', className)}>
      <Spinner color={color} />
    </div>
  );
}

/** The «ایران آپ» wordmark, tinted like android:tint does. */
export function Logo({ color = 'var(--color-primary)', width = 150, height = 40, className }: { color?: string; width?: number; height?: number; className?: string }) {
  return <MaskIcon src={logoUrl} color={color} className={className} style={{ width, height }} size={0} />;
}

interface AppBarProps {
  /** Shown instead of the logo. */
  title?: string;
  /** false hides the back arrow (the main tabs). */
  back?: boolean | (() => void);
  /** Extra control at the far (left) end, e.g. a search button. */
  action?: ReactNode;
  dark?: boolean;
  /** The red hairline most inner pages draw under the bar. */
  line?: boolean;
}

/** toolbar.xml / toolbar_in_newad.xml / toolbar_home.xml: the logo in the middle, back arrow on the right. */
export function AppBar({ title, back = true, action, dark = false, line = true }: AppBarProps) {
  const navigate = useNavigate();
  const tint = dark ? 'var(--color-home-accent)' : 'var(--color-primary)';
  return (
    <header className="relative z-20 shrink-0 pt-safe" style={{ background: dark ? 'var(--color-home-bar)' : '#fff', boxShadow: 'var(--shadow-bar)' }}>
      <div className="relative flex h-14 items-center justify-between">
        <div className="flex h-14 w-14 items-center justify-center">
          {back && (
            <button
              type="button"
              aria-label="بازگشت"
              className={cx('flex h-12 w-12 items-center justify-center rounded-full', dark ? 'tap-dark' : 'tap-light')}
              style={{ color: dark ? 'var(--color-home-text)' : tint }}
              onClick={() => (typeof back === 'function' ? back() : navigate(-1))}
            >
              <Icon path={mdiArrowRight} />
            </button>
          )}
        </div>
        <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
          {title ? (
            <span className="text-[17px]" style={{ color: dark ? 'var(--color-home-text)' : tint }}>
              {title}
            </span>
          ) : (
            <Logo color={tint} />
          )}
        </div>
        <div className="flex h-14 min-w-14 items-center justify-center" style={{ color: tint }}>
          {action}
        </div>
      </div>
      {line && <div className="h-px" style={{ background: dark ? 'var(--color-home-bar-stroke)' : 'var(--color-primary)' }} />}
    </header>
  );
}

/** A whole screen: a fixed bar on top and a scrolling body, like an Activity. */
export function Screen({ bar, children, className, style }: { bar?: ReactNode; children: ReactNode; className?: string; style?: CSSProperties }) {
  return (
    <div className={cx('animate-page-in flex h-full flex-col', className)} style={style}>
      {bar}
      {children}
    </div>
  );
}

export function Card({ children, className, style, onClick }: { children: ReactNode; className?: string; style?: CSSProperties; onClick?: () => void }) {
  return (
    <div className={cx('rounded-[3px] bg-white', className)} style={{ boxShadow: 'var(--shadow-card)', ...style }} onClick={onClick}>
      {children}
    </div>
  );
}

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  /** red = login_login_btn, teal = login_sign_up_btn. */
  tone?: 'red' | 'teal';
  loading?: boolean;
  height?: number;
}

/** The app's full-width gradient buttons; the label gives way to a spinner while busy. */
export function Button({ tone = 'red', loading, height = 45, children, className, disabled, ...rest }: ButtonProps) {
  return (
    <button
      type="button"
      {...rest}
      disabled={disabled || loading}
      className={cx('tap relative flex w-full items-center justify-center rounded-[3px] text-base text-white', className)}
      style={{
        height,
        background: tone === 'red' ? 'linear-gradient(to top, #e53935, var(--color-primary))' : 'radial-gradient(circle, #009688, #00796b)',
      }}
    >
      {loading ? <Spinner size={28} color="#fff" /> : children}
    </button>
  );
}

/** Borderless coloured text button (dialog actions). */
export function TextButton({ children, className, color = 'var(--color-hot)', ...rest }: ButtonHTMLAttributes<HTMLButtonElement> & { color?: string }) {
  return (
    <button type="button" {...rest} className={cx('tap-light rounded px-4 py-2.5 text-sm', className)} style={{ color }}>
      {children}
    </button>
  );
}

function overlayRoot(): HTMLElement {
  return document.getElementById('overlay-root') ?? document.body;
}

interface ModalProps {
  onClose?: () => void;
  children: ReactNode;
  /** Card width as a share of the screen; dialogs in the app are narrower than the screen. */
  className?: string;
  full?: boolean;
}

/** A dimmed backdrop with the dialog in the middle. Tapping outside or pressing Esc closes it. */
export function Modal({ onClose, children, className, full }: ModalProps) {
  useEffect(() => {
    if (!onClose) return;
    const onKey = (event: KeyboardEvent) => {
      if (event.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [onClose]);

  return createPortal(
    <div
      className="animate-fade-in pointer-events-auto absolute inset-0 z-50 flex items-center justify-center bg-black/55"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
    >
      <div
        className={cx(
          'animate-pop-in flex max-h-[88dvh] flex-col overflow-hidden bg-white',
          full ? 'h-full max-h-none w-full' : 'w-[88%] rounded-[3px] shadow-2xl',
          className,
        )}
        onClick={(event) => event.stopPropagation()}
      >
        {children}
      </div>
    </div>,
    overlayRoot(),
  );
}

/**
 * The header every dialog of the app shares: a red strip with the title on the right and a white
 * disc carrying an icon hanging off its bottom left corner.
 */
export function DialogHeader({ title, icon, onBack }: { title: string; icon: ReactNode; onBack?: () => void }) {
  return (
    <div className="relative h-[60px] shrink-0 bg-white">
      <div className="flex h-[45px] items-center gap-2 bg-primary pr-3 text-[15px] text-white">
        {onBack && (
          <button type="button" aria-label="بازگشت" className="tap-dark -mr-1 flex h-8 w-8 items-center justify-center rounded-full" onClick={onBack}>
            <Icon path={mdiArrowRight} size={22} />
          </button>
        )}
        <span>{title}</span>
      </div>
      <div className="absolute bottom-0 left-[15px] flex h-10 w-10 items-center justify-center rounded-full bg-white text-primary" style={{ boxShadow: '0 0 0 1px rgb(0 0 0 / 0.04)' }}>
        {icon}
      </div>
    </div>
  );
}

/** Shows the pending toast (ShowToast.success / failure): a rounded pill above the bottom edge. */
export function Toaster() {
  const toasts = useUi((state) => state.toasts);
  const drop = useUi((state) => state.dropToast);
  return createPortal(
    <div className="pointer-events-none absolute inset-x-0 bottom-24 z-[70] flex flex-col items-center gap-2 px-6">
      {toasts.map((item) => (
        <div
          key={item.id}
          role="status"
          onClick={() => drop(item.id)}
          className="animate-slide-up pointer-events-auto flex max-w-full items-center gap-2 rounded-[15px] py-2 pr-2.5 pl-4 text-center text-sm text-white shadow-lg"
          style={{ background: item.kind === 'success' ? '#33691E' : '#BF360C' }}
        >
          <Icon path={item.kind === 'success' ? mdiCheck : mdiClose} size={26} />
          <span className="leading-6">{item.message}</span>
        </div>
      ))}
    </div>,
    overlayRoot(),
  );
}

/** AlertDialog: a message with one or two buttons. Driven by confirmDialog() / alertDialog(). */
export function ConfirmHost() {
  const request = useUi((state) => state.confirm);
  const answer = useUi((state) => state.answerConfirm);
  if (!request) return null;
  return (
    <Modal onClose={() => answer(false)} className="w-[82%]">
      <div className="px-5 pt-5 pb-2">
        {request.title && <div className="mb-2 text-base text-[#212121]">{request.title}</div>}
        <div className="whitespace-pre-line text-[15px] leading-7 text-[#555]">{request.message}</div>
      </div>
      <div className="flex justify-end gap-1 px-2 pb-2" dir="ltr">
        <TextButton color="var(--color-primary)" className="text-base" onClick={() => answer(true)}>
          {request.positive ?? 'باشه'}
        </TextButton>
        {request.negative && (
          <TextButton color="var(--color-primary)" className="text-base" onClick={() => answer(false)}>
            {request.negative}
          </TextButton>
        )}
      </div>
    </Modal>
  );
}

/** Centred grey note for empty lists and errors, with an optional retry. */
export function EmptyNote({ children, onRetry, dark }: { children: ReactNode; onRetry?: () => void; dark?: boolean }) {
  return (
    <div className={cx('flex flex-col items-center gap-3 px-8 py-10 text-center text-sm', dark ? 'text-home-muted' : 'text-mild')}>
      <div>{children}</div>
      {onRetry && (
        <TextButton color={dark ? 'var(--color-home-accent)' : 'var(--color-primary)'} onClick={onRetry}>
          تلاش مجدد
        </TextButton>
      )}
    </div>
  );
}

/** Horizontal tab strip (TabLayout). Tabs are listed right to left, as they appear on screen. */
export function Tabs<T extends string>({
  tabs,
  value,
  onChange,
  variant,
}: {
  tabs: Array<{ id: T; label: string }>;
  value: T;
  onChange: (id: T) => void;
  variant: 'dark' | 'light' | 'red';
}) {
  const palette = {
    dark: { bar: 'var(--color-home-bar)', on: 'var(--color-home-accent)', off: 'var(--color-home-nav)', line: 'var(--color-home-accent)' },
    light: { bar: '#fff', on: '#212121', off: '#757575', line: 'var(--color-accent-gray)' },
    red: { bar: 'var(--color-primary)', on: '#fff', off: 'rgb(255 255 255 / 0.7)', line: '#fff' },
  }[variant];
  return (
    <div className="flex shrink-0" style={{ background: palette.bar, boxShadow: variant === 'red' ? undefined : '0 -1px 4px rgb(0 0 0 / 0.18)' }} role="tablist">
      {tabs.map((tab) => {
        const active = tab.id === value;
        return (
          <button
            key={tab.id}
            type="button"
            role="tab"
            aria-selected={active}
            className={cx('relative flex-1 text-xs', variant === 'light' ? 'tap-light h-[30px]' : 'tap-dark min-h-sdp-32')}
            style={{ color: active ? palette.on : palette.off }}
            onClick={() => onChange(tab.id)}
          >
            {tab.label}
            {active && <span className="absolute inset-x-0 bottom-0 h-0.5" style={{ background: palette.line }} />}
          </button>
        );
      })}
    </div>
  );
}
