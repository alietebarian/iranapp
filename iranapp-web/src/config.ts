/** Build-time settings. Every value has a default that matches the production domain. */

const env = import.meta.env;

/** Sub path the app is served from, without a trailing slash ("/web"). */
export const BASE_PATH = env.BASE_URL.replace(/\/$/, '');

/** Where the JSON API lives. Same origin by default, so no CORS and no mixed content. */
export const API_BASE: string = env.VITE_API_BASE || '/api';

/**
 * The API returns absolute photo links (http://iranapp.biz/ads_photo/...). Links to these hosts
 * are rewritten to same-origin paths, so they follow the page's own scheme (https) and, in
 * development, go through the dev proxy.
 */
export const MEDIA_HOSTS: string[] = (env.VITE_MEDIA_HOSTS || 'iranapp.biz,www.iranapp.biz')
  .split(',')
  .map((h: string) => h.trim().toLowerCase())
  .filter(Boolean);

/** Set to "false" to let guests browse without logging in (the Android app requires login). */
export const REQUIRE_LOGIN: boolean = env.VITE_REQUIRE_LOGIN !== 'false';

/** The city the app starts with before the user picks one (SplashScreenActivity: 57 = اصفهان). */
export const DEFAULT_CITY_ID = '57';

/** Free plan every user ad is saved with (NewAdActivity hard-codes it). */
export const DEFAULT_AD_PLAN_ID = '17';

/** Version of the terms the user accepts when posting an ad (StaticData.TERMS_VERSION). */
export const TERMS_VERSION = '1.0';

/** Sent as app_version so the admin can tell web submissions from the Android app's. */
export const APP_VERSION = `web-${__APP_VERSION__}`;

export const MAP_TILE_URL: string = env.VITE_MAP_TILE_URL || 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
export const MAP_ATTRIBUTION: string =
  env.VITE_MAP_ATTRIBUTION || '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>';
/** Centre of the map before a location is known (اصفهان). */
export const MAP_DEFAULT_CENTER: [number, number] = [32.6546, 51.668];

export const SUPPORT_PHONE = '09131094406';
export const DOWNLOAD_LINK = 'https://cafebazaar.ir/app/com.ideabonyan.iranapp/';
export const BAZAAR_LINK = 'https://cafebazaar.ir/app/com.ideabonyan.iranapp/?l=fa';
export const SMS_NUMBER = '985000538027';

/** Web push (optional). Filled from the Firebase console; push stays off while any is missing. */
export const FIREBASE = {
  apiKey: env.VITE_FIREBASE_API_KEY as string | undefined,
  projectId: env.VITE_FIREBASE_PROJECT_ID as string | undefined,
  messagingSenderId: env.VITE_FIREBASE_SENDER_ID as string | undefined,
  appId: env.VITE_FIREBASE_APP_ID as string | undefined,
  vapidKey: env.VITE_FIREBASE_VAPID_KEY as string | undefined,
};

export const PUSH_CONFIGURED = Boolean(
  FIREBASE.apiKey && FIREBASE.projectId && FIREBASE.messagingSenderId && FIREBASE.appId && FIREBASE.vapidKey,
);
