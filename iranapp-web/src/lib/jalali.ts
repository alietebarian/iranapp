/** Jalali (Persian) calendar maths: the port of the app's Utils/JalaliDate. */

export type JDate = [year: number, month: number, day: number];

export const MONTH_NAMES = [
  'فروردین',
  'اردیبهشت',
  'خرداد',
  'تیر',
  'مرداد',
  'شهریور',
  'مهر',
  'آبان',
  'آذر',
  'دی',
  'بهمن',
  'اسفند',
];

function div(a: number, b: number): number {
  return Math.trunc(a / b);
}

/** Gregorian date -> Jalali (the jalaali algorithm, valid for 1976..2620 AD and more). */
export function toJalali(gy: number, gm: number, gd: number): JDate {
  const gdm = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334];
  const gy2 = gm > 2 ? gy + 1 : gy;
  let days =
    355666 + 365 * gy + div(gy2 + 3, 4) - div(gy2 + 99, 100) + div(gy2 + 399, 400) + gd + gdm[gm - 1];
  let jy = -1595 + 33 * div(days, 12053);
  days %= 12053;
  jy += 4 * div(days, 1461);
  days %= 1461;
  if (days > 365) {
    jy += div(days - 1, 365);
    days = (days - 1) % 365;
  }
  const jm = days < 186 ? 1 + div(days, 31) : 7 + div(days - 186, 30);
  const jd = 1 + (days < 186 ? days % 31 : (days - 186) % 30);
  return [jy, jm, jd];
}

/** The same 33-year-cycle rule as jdf's jcheckdate on the server and JalaliDate.isLeap in the app. */
export function isLeap(jy: number): boolean {
  const r = jy % 33;
  return (r % 4) - 1 === Math.trunc(r * 0.05);
}

export function monthLength(jy: number, jm: number): number {
  if (jm <= 6) return 31;
  if (jm <= 11) return 30;
  return isLeap(jy) ? 30 : 29;
}

/** Today in Tehran, whatever the device's own time zone is. */
export function today(): JDate {
  const parts = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'Asia/Tehran',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
    .format(new Date())
    .split('-')
    .map(Number);
  return toJalali(parts[0], parts[1], parts[2]);
}

/** "1405/07/01" (also with dashes or Persian digits) -> date, or null when it is not a real day. */
export function parse(value: string | null | undefined): JDate | null {
  if (!value) return null;
  const latin = value
    .trim()
    .replace(/[۰-۹]/g, (d) => String(d.charCodeAt(0) - 0x06f0))
    .replace(/[٠-٩]/g, (d) => String(d.charCodeAt(0) - 0x0660));
  const parts = latin.split(/[/-]/);
  if (parts.length !== 3) return null;
  const [y, m, d] = parts.map((p) => Number.parseInt(p, 10));
  if (!Number.isFinite(y) || !Number.isFinite(m) || !Number.isFinite(d)) return null;
  if (y < 1000 || m < 1 || m > 12 || d < 1 || d > monthLength(y, m)) return null;
  return [y, m, d];
}

export function format(date: JDate): string {
  const pad = (n: number, width: number) => String(n).padStart(width, '0');
  return `${pad(date[0], 4)}/${pad(date[1], 2)}/${pad(date[2], 2)}`;
}

/** Adds whole months, clamping the day to the target month's length (6/31 + 1 month -> 7/30). */
export function addMonths(date: JDate, months: number): JDate {
  const index = date[1] - 1 + months;
  const y = date[0] + Math.floor(index / 12);
  const m = (((index % 12) + 12) % 12) + 1;
  return [y, m, Math.min(date[2], monthLength(y, m))];
}

export function compare(a: JDate, b: JDate): number {
  for (let i = 0; i < 3; i++) if (a[i] !== b[i]) return a[i] < b[i] ? -1 : 1;
  return 0;
}

/** "12 مهر 1370", the way the sign-up form shows the picked birth date. */
export function formatLong(date: JDate): string {
  return `${date[2]} ${MONTH_NAMES[date[1] - 1]} ${date[0]}`;
}
