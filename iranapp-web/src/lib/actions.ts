import type { NavigateFunction } from 'react-router';
import { DOWNLOAD_LINK } from '@/config';
import { session } from '@/store/session';
import { toast } from '@/store/ui';
import { blank } from './format';

/** Small things several screens do: sharing, copying, dialling, and the login checks. */

/** The Android share sheet. Falls back to copying where the Web Share API is missing (desktop). */
export async function shareText(text: string): Promise<void> {
  if (navigator.share) {
    try {
      await navigator.share({ text });
    } catch {
      // The user closed the sheet.
    }
    return;
  }
  if (await copyText(text)) toast.success('متن برای اشتراک گذاری کپی شد');
}

export async function copyText(text: string): Promise<boolean> {
  try {
    await navigator.clipboard.writeText(text);
    return true;
  } catch {
    const area = document.createElement('textarea');
    area.value = text;
    area.style.position = 'fixed';
    area.style.opacity = '0';
    document.body.appendChild(area);
    area.select();
    let ok = false;
    try {
      ok = document.execCommand('copy');
    } catch {
      ok = false;
    }
    area.remove();
    return ok;
  }
}

/** The text every ad's share button sends (same wording as the app). */
export function adShareText(title: string, phones: Array<string | null | undefined>, address: string | null | undefined): string {
  const lines = ['ایران آپ : ', title];
  for (const phone of phones) if (!blank(phone)) lines.push(String(phone));
  lines.push(blank(address) ? '' : String(address), 'با ایران آپ در تخفیف ها به روز باشید', '', `لینک دانلود : ${DOWNLOAD_LINK}`);
  return lines.join('\n');
}

export function dial(number: string): void {
  window.location.href = `tel:${number}`;
}

export function openExternal(url: string): void {
  window.open(url, '_blank', 'noopener,noreferrer');
}

/**
 * Screens that need an account: guests go to the login page, users who have not entered their
 * SMS code yet go to the confirmation page. Returns true when the user may continue.
 */
export function requireUser(navigate: NavigateFunction, guestMessage?: string): boolean {
  const { user, token } = session();
  if (!user) {
    if (guestMessage) toast.failure(guestMessage);
    navigate('/login');
    return false;
  }
  if (!user.verified || !token) {
    navigate('/confirm');
    return false;
  }
  return true;
}

/** Asks the browser where the user is. Rejects with a Persian message ready for a toast. */
export function locate(): Promise<{ latitude: number; longitude: number }> {
  return new Promise((resolve, reject) => {
    if (!('geolocation' in navigator)) {
      reject(new Error('مرورگر شما از موقعیت یابی پشتیبانی نمی کند'));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (position) => resolve({ latitude: position.coords.latitude, longitude: position.coords.longitude }),
      (error) =>
        reject(
          new Error(
            error.code === error.PERMISSION_DENIED
              ? 'شما اجازه دسترسی به موقعیت خود را به این برنامه نداده اید'
              : 'موقعیت شما پیدا نشد؛ لطفا موقعیت یاب دستگاه را روشن کنید',
          ),
        ),
      { enableHighAccuracy: true, timeout: 15_000, maximumAge: 60_000 },
    );
  });
}
