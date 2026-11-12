/** Small text helpers shared by the whole app. */

const PERSIAN_ZERO = '۰'.charCodeAt(0);
const ARABIC_ZERO = '٠'.charCodeAt(0);

/** Persian / Arabic digits -> Latin, so what the user types on an iOS Persian keyboard is sent as 0-9. */
export function toLatinDigits(text: string): string {
  let out = '';
  for (const ch of text) {
    const code = ch.charCodeAt(0);
    if (code >= PERSIAN_ZERO && code <= PERSIAN_ZERO + 9) out += String(code - PERSIAN_ZERO);
    else if (code >= ARABIC_ZERO && code <= ARABIC_ZERO + 9) out += String(code - ARABIC_ZERO);
    else out += ch;
  }
  return out;
}

/** Keeps only digits (after normalising them). */
export function digitsOnly(text: string): string {
  return toLatinDigits(text).replace(/\D/g, '');
}

/** 12000000 -> "12,000,000" (the app's DecimalFormat("#,###,###")). */
export function thousands(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') return '';
  const text = String(value);
  if (!/^-?\d+$/.test(text)) return text;
  return text.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

/** "12,000,000 تومان", or the fallback when the price is 0 / empty. */
export function toman(value: string | number | null | undefined): string {
  return `${thousands(value)} تومان`;
}

/** The API mixes real nulls with the strings "null" and "". */
export function blank(value: unknown): boolean {
  return value === null || value === undefined || value === '' || value === 'null';
}

export function text(value: unknown): string {
  return blank(value) ? '' : String(value);
}

/**
 * Turns a mobile number as the owner typed it (09..., 9..., +98..., 0098..., Persian digits) into
 * the digits-only international form messenger links expect, e.g. 989121234567.
 * Returns null when there is no usable number (ShowAdActivity.internationalMobile).
 */
export function internationalMobile(mobile: string | null | undefined): string | null {
  if (!mobile) return null;
  let number = digitsOnly(mobile);
  if (number.startsWith('00')) number = number.slice(2);
  else if (number.startsWith('0')) number = `98${number.slice(1)}`;
  else if (number.length === 10 && number.startsWith('9')) number = `98${number}`;
  return number.length >= 10 ? number : null;
}

/** Iranian national ID checksum, as on the server and in EContractActivity. */
export function isValidNationalCode(code: string): boolean {
  if (!/^\d{10}$/.test(code) || /^(\d)\1{9}$/.test(code)) return false;
  let sum = 0;
  for (let i = 0; i < 9; i++) sum += Number(code[i]) * (10 - i);
  const remainder = sum % 11;
  const check = Number(code[9]);
  return remainder < 2 ? check === remainder : check === 11 - remainder;
}

export function isValidEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
}

/** Collapses runs of whitespace, like the app does for people's names. */
export function squeeze(textValue: string): string {
  return textValue.trim().replace(/\s+/g, ' ');
}

/** A web address typed without a scheme still has to open. */
export function withScheme(link: string): string {
  return /^[a-z][a-z0-9+.-]*:/i.test(link) ? link : `http://${link}`;
}

export function cx(...classes: Array<string | false | null | undefined>): string {
  return classes.filter(Boolean).join(' ');
}
