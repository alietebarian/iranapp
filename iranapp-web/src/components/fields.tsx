import { useState, type ReactNode } from 'react';
import { mdiCheckboxBlankOutline, mdiCheckboxMarked, mdiEye, mdiEyeOff, mdiMenuDown, mdiRadioboxBlank, mdiRadioboxMarked } from '@mdi/js';
import { cx, digitsOnly, toLatinDigits } from '@/lib/format';
import { Icon } from './Icon';

/** The form controls of the app: light grey boxes with a thin red border (edittext_background). */

const BOX = 'w-full rounded-[3px] border border-[#f44336] bg-[#f4f4f4] text-[#333] placeholder:text-[#9e9e9e]';

interface TextFieldProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  /** "digits" keeps 0-9 only and converts Persian digits typed on a Persian keyboard. */
  mode?: 'text' | 'digits' | 'tel' | 'email' | 'url';
  maxLength?: number;
  height?: number;
  /** Small red note above the box (the app's "…WarnTXT" labels). */
  warn?: string | false;
  disabled?: boolean;
  trailing?: ReactNode;
  type?: string;
  enterKeyHint?: 'search' | 'done' | 'next' | 'go';
  onEnter?: () => void;
  id?: string;
  autoComplete?: string;
  className?: string;
  /** Put thousands separators in while typing (prices). */
  money?: boolean;
}

function Warn({ text }: { text?: string | false }) {
  if (!text) return null;
  return <div className="mb-0.5 px-1 text-left text-[11px] text-warn">{text}</div>;
}

export function TextField({
  value,
  onChange,
  placeholder,
  mode = 'text',
  maxLength,
  height = 46,
  warn,
  disabled,
  trailing,
  type,
  enterKeyHint,
  onEnter,
  id,
  autoComplete,
  className,
  money,
}: TextFieldProps) {
  const numeric = mode === 'digits' || mode === 'tel';
  const shown = money ? value.replace(/\B(?=(\d{3})+(?!\d))/g, ',') : value;
  return (
    <div className={cx('mb-2.5', className)}>
      <Warn text={warn} />
      <div className="relative">
        <input
          id={id}
          type={type ?? (mode === 'email' ? 'email' : mode === 'url' ? 'url' : 'text')}
          inputMode={numeric ? 'numeric' : mode === 'email' ? 'email' : mode === 'url' ? 'url' : undefined}
          dir={numeric || mode === 'email' || mode === 'url' ? 'ltr' : undefined}
          value={shown}
          disabled={disabled}
          placeholder={placeholder}
          aria-label={placeholder}
          autoComplete={autoComplete ?? 'off'}
          enterKeyHint={enterKeyHint}
          className={cx(BOX, 'px-2.5', numeric || mode === 'email' || mode === 'url' ? 'text-right placeholder:text-right' : '', trailing ? 'pl-10' : '', disabled && 'text-[#757575]')}
          style={{ height }}
          onChange={(event) => {
            let next = event.target.value;
            if (numeric) next = digitsOnly(next);
            else next = mode === 'email' || mode === 'url' ? toLatinDigits(next) : next;
            if (maxLength !== undefined) next = next.slice(0, maxLength);
            onChange(next);
          }}
          onKeyDown={(event) => {
            if (event.key === 'Enter' && onEnter) {
              event.preventDefault();
              onEnter();
            }
          }}
        />
        {trailing && <div className="absolute inset-y-0 left-0 flex w-10 items-center justify-center text-[#757575]">{trailing}</div>}
      </div>
    </div>
  );
}

/** Password box with the eye toggle of CustomEditText. */
export function PasswordField(props: Omit<TextFieldProps, 'type' | 'trailing' | 'mode'>) {
  const [visible, setVisible] = useState(false);
  return (
    <TextField
      {...props}
      type={visible ? 'text' : 'password'}
      autoComplete={props.autoComplete ?? 'current-password'}
      trailing={
        <button type="button" aria-label={visible ? 'پنهان کردن رمز' : 'نمایش رمز'} className="flex h-10 w-10 items-center justify-center" onClick={() => setVisible(!visible)}>
          <Icon path={visible ? mdiEyeOff : mdiEye} size={22} />
        </button>
      }
    />
  );
}

export function TextArea({
  value,
  onChange,
  placeholder,
  height = 120,
  warn,
  maxLength,
}: {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  height?: number;
  warn?: string | false;
  maxLength?: number;
}) {
  return (
    <div className="mb-2.5">
      <Warn text={warn} />
      <textarea
        value={value}
        placeholder={placeholder}
        aria-label={placeholder}
        maxLength={maxLength}
        className={cx(BOX, 'block resize-none px-2.5 py-2.5 leading-7')}
        style={{ height }}
        onChange={(event) => onChange(event.target.value)}
      />
    </div>
  );
}

export interface Option {
  value: string;
  label: string;
}

interface SelectFieldProps {
  /** Caption on the right of the box ("استان"), as in the app's spinners. */
  label?: string;
  value: string;
  onChange: (value: string) => void;
  options: Option[];
  /** First row, selected while nothing is chosen ("لطفا انتخاب کنید."). */
  placeholder?: string;
  disabled?: boolean;
  warn?: string | false;
  /** Plain underline-less look used in filter bars. */
  bare?: boolean;
  className?: string;
}

/** An Android Spinner: the native picker of the device, which is the best choice on phones. */
export function SelectField({ label, value, onChange, options, placeholder = 'لطفا انتخاب کنید.', disabled, warn, bare, className }: SelectFieldProps) {
  return (
    <div className={cx(bare ? '' : 'mb-2.5', className)}>
      <Warn text={warn} />
      <label className={cx('relative flex items-center', bare ? 'h-9' : cx(BOX, 'h-[46px]'), disabled && 'opacity-60')}>
        {label && <span className={cx('shrink-0 text-xs', bare ? 'pl-1' : 'pr-3 text-accent-gray')}>{label}</span>}
        <select
          value={value}
          disabled={disabled}
          aria-label={label ?? placeholder}
          className="h-full min-w-0 flex-1 truncate pr-2 pl-7 text-sm text-[#333]"
          style={{ fontSize: 14 }}
          onChange={(event) => onChange(event.target.value)}
        >
          {placeholder !== '' && <option value="">{placeholder}</option>}
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        <Icon path={mdiMenuDown} size={22} className="pointer-events-none absolute left-1 text-[#757575]" />
      </label>
    </div>
  );
}

/** A group of radio buttons on one line with its caption (teal labels, as in the ad forms). */
export function RadioRow({
  label,
  value,
  onChange,
  options,
  color = '#009688',
}: {
  label?: string;
  value: string;
  onChange: (value: string) => void;
  options: Option[];
  color?: string;
}) {
  return (
    <div className="mb-2.5 flex flex-wrap items-center gap-x-4 gap-y-1 px-1" role="radiogroup" aria-label={label}>
      {label && <span className="text-sm text-[#555]">{label}</span>}
      {options.map((option) => {
        const checked = option.value === value;
        return (
          <button
            key={option.value}
            type="button"
            role="radio"
            aria-checked={checked}
            className="flex items-center gap-1 py-1.5 text-sm"
            style={{ color }}
            onClick={() => onChange(option.value)}
          >
            <Icon path={checked ? mdiRadioboxMarked : mdiRadioboxBlank} size={22} />
            {option.label}
          </button>
        );
      })}
    </div>
  );
}

export function Checkbox({
  checked,
  onChange,
  children,
  className,
  color = 'var(--color-primary)',
}: {
  checked: boolean;
  onChange: (checked: boolean) => void;
  children: ReactNode;
  className?: string;
  color?: string;
}) {
  return (
    <button type="button" role="checkbox" aria-checked={checked} className={cx('flex items-center gap-2 text-right', className)} onClick={() => onChange(!checked)}>
      <Icon path={checked ? mdiCheckboxMarked : mdiCheckboxBlankOutline} size={24} style={{ color: checked ? color : '#757575' }} />
      <span>{children}</span>
    </button>
  );
}

/** A white card with a coloured caption strip, the building block of every ad form. */
export function FormSection({ title, children, color = 'var(--color-primary)', className }: { title?: ReactNode; children: ReactNode; color?: string; className?: string }) {
  return (
    <section className={cx('mx-[5px] mb-4 overflow-hidden rounded-lg bg-white', className)} style={{ boxShadow: 'var(--shadow-card)' }}>
      {title !== undefined && (
        <div className="flex h-[35px] items-center justify-center text-sm text-white" style={{ background: color }}>
          {title}
        </div>
      )}
      <div className="px-[5px] pt-2.5 pb-1">{children}</div>
    </section>
  );
}
