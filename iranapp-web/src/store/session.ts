import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { DEFAULT_CITY_ID } from '@/config';
import type { ApiUser } from '@/lib/types';

/** What the Android app keeps in UserSessionManager (SharedPreferences "user"). */

export interface SessionUser {
  id: string;
  firstName: string;
  lastName: string;
  mobile: string;
  /** False between signing up and entering the SMS code. */
  verified: boolean;
  role: string;
}

interface SessionState {
  user: SessionUser | null;
  token: string | null;

  /** "57" (اصفهان) until the user picks a city; the name stays empty until then. */
  cityId: string;
  cityName: string;
  provinceId: string;
  provinceName: string;

  introSeen: boolean;
  /** The one-time "read the guide first" hint of the main page. */
  guideHintSeen: boolean;
  sendNewsNotifications: boolean;
  sendAdsNotifications: boolean;
  /** FCM registration token of this browser, once push is enabled. */
  pushToken: string | null;

  signIn: (user: ApiUser, mobileFallback?: string) => void;
  /** After /register: logged in but not verified, no API token yet. */
  setUnverifiedUser: (user: { id: string; firstName: string; lastName: string; mobile: string }) => void;
  updateUser: (patch: Partial<SessionUser>) => void;
  signOut: () => void;
  setCity: (city: { id: string; name: string }, province?: { id: string; name: string }) => void;
  setIntroSeen: () => void;
  setGuideHintSeen: () => void;
  setNotificationPrefs: (news: boolean, ads: boolean) => void;
  setPushToken: (token: string | null) => void;
}

const truthy = (value: unknown) => value === 1 || value === '1' || value === true;

export const useSession = create<SessionState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      cityId: DEFAULT_CITY_ID,
      cityName: '',
      provinceId: '',
      provinceName: '',
      introSeen: false,
      guideHintSeen: false,
      sendNewsNotifications: true,
      sendAdsNotifications: true,
      pushToken: null,

      signIn: (apiUser, mobileFallback) =>
        set({
          token: apiUser.jwt_token,
          user: {
            id: String(apiUser.id),
            firstName: apiUser.first_name ?? '',
            lastName: apiUser.last_name ?? '',
            mobile: apiUser.mobile ?? mobileFallback ?? '',
            verified: truthy(apiUser.is_mobile_verified),
            role: apiUser.role ?? 'normal',
          },
          sendNewsNotifications: apiUser.send_news_notifications == null || truthy(apiUser.send_news_notifications),
          sendAdsNotifications: apiUser.send_ads_notifications == null || truthy(apiUser.send_ads_notifications),
        }),

      setUnverifiedUser: (user) => set({ token: null, user: { ...user, verified: false, role: 'normal' } }),

      updateUser: (patch) => set((state) => (state.user ? { user: { ...state.user, ...patch } } : {})),

      signOut: () => set({ user: null, token: null }),

      setCity: (city, province) =>
        set((state) => ({
          cityId: city.id,
          cityName: city.name,
          provinceId: province ? province.id : state.provinceId,
          provinceName: province ? province.name : state.provinceName,
        })),

      setIntroSeen: () => set({ introSeen: true }),
      setGuideHintSeen: () => set({ guideHintSeen: true }),
      setNotificationPrefs: (news, ads) => set({ sendNewsNotifications: news, sendAdsNotifications: ads }),
      setPushToken: (token) => set({ pushToken: token }),
    }),
    { name: 'iranapp-web-session', version: 1 },
  ),
);

/** Non-reactive read for code outside React (the HTTP client). */
export const session = () => useSession.getState();

export function isLoggedIn(): boolean {
  return session().user !== null;
}

export function isVerified(): boolean {
  const { user, token } = session();
  return Boolean(user && user.verified && token);
}

/** True once the user has picked a city themselves (not just the default id). */
export function hasChosenCity(state: Pick<SessionState, 'cityId' | 'cityName'>): boolean {
  return state.cityId !== '0' && state.cityName !== '';
}
