import { FIREBASE, PUSH_CONFIGURED } from '@/config';
import { session } from '@/store/session';
import { registerPushToken } from './api';

/**
 * Web push through Firebase Cloud Messaging, the same project the Android app uses.
 *
 * It is optional: until the VITE_FIREBASE_* values are set (see README) everything here reports
 * "unsupported" and the rest of the app works without notifications. On iPhone, web push only
 * exists on iOS 16.4+ and only after the site is added to the home screen.
 */

export type PushSupport = 'ready' | 'not-configured' | 'unsupported' | 'needs-install' | 'denied';

function isStandalone(): boolean {
  return window.matchMedia('(display-mode: standalone)').matches || (navigator as { standalone?: boolean }).standalone === true;
}

function isIos(): boolean {
  return /iphone|ipad|ipod/i.test(navigator.userAgent) || (navigator.platform === 'MacIntel' && navigator.maxTouchPoints > 1);
}

export function pushSupport(): PushSupport {
  if (!PUSH_CONFIGURED) return 'not-configured';
  if (isIos() && !isStandalone()) return 'needs-install';
  if (!('serviceWorker' in navigator) || !('PushManager' in window) || !('Notification' in window)) return 'unsupported';
  if (Notification.permission === 'denied') return 'denied';
  return 'ready';
}

/**
 * Asks for permission if needed, gets this browser's FCM token and registers it with the server.
 * Returns the token, or null when push is unavailable or the user said no.
 */
export async function enablePush(): Promise<string | null> {
  if (pushSupport() !== 'ready') return null;
  const permission = Notification.permission === 'granted' ? 'granted' : await Notification.requestPermission();
  if (permission !== 'granted') return null;

  const registration = await navigator.serviceWorker.ready;
  const [{ initializeApp, getApps }, { getMessaging, getToken }] = await Promise.all([
    import('firebase/app'),
    import('firebase/messaging'),
  ]);
  const app =
    getApps()[0] ??
    initializeApp({
      apiKey: FIREBASE.apiKey,
      projectId: FIREBASE.projectId,
      messagingSenderId: FIREBASE.messagingSenderId,
      appId: FIREBASE.appId,
    });
  const token = await getToken(getMessaging(app), { vapidKey: FIREBASE.vapidKey, serviceWorkerRegistration: registration });
  if (!token) return null;

  const previous = session().pushToken;
  if (previous !== token) {
    await registerPushToken(token, previous);
    session().setPushToken(token);
  }
  return token;
}

/** Refreshes the token quietly on start-up when the user has already allowed notifications. */
export function syncPushToken(): void {
  if (pushSupport() !== 'ready' || Notification.permission !== 'granted') return;
  void enablePush().catch(() => undefined);
}
