import axios, { type AxiosRequestConfig } from 'axios';
import { API_BASE } from '@/config';
import { session } from '@/store/session';

/**
 * The API almost always answers HTTP 200 and puts the real outcome in a "status" field of the
 * body (200, 204, 400, 401...), so callers look at the body, not at the HTTP status.
 */
export const http = axios.create({
  baseURL: API_BASE,
  timeout: 30_000,
  headers: { Accept: 'application/json' },
  // Keep 4xx bodies readable: several endpoints explain themselves there (403, 422, 426).
  validateStatus: (status) => status < 500,
});

export const AUTH_EXPIRED_EVENT = 'iranapp:auth-expired';

http.interceptors.request.use((config) => {
  const { token } = session();
  if (token) config.headers.set('Authorization', `Bearer ${token}`);
  return config;
});

http.interceptors.response.use((response) => {
  const body = response.data as { status?: unknown; error?: unknown } | undefined;
  const error = typeof body?.error === 'string' ? body.error : '';
  const expired =
    (response.status === 401 || String(body?.status) === '401') && (error === 'token_expired' || error === 'token_invalid');
  if (expired && session().token) {
    session().signOut();
    window.dispatchEvent(new Event(AUTH_EXPIRED_EVENT));
  }
  return response;
});

type Params = Record<string, string | number | boolean | null | undefined>;

/** Drops empty values: Laravel's $request->has('x') is true for an empty x, which would filter on it. */
export function clean(params: Params): Record<string, string> {
  const out: Record<string, string> = {};
  for (const [key, value] of Object.entries(params)) {
    if (value === null || value === undefined || value === '') continue;
    out[key] = String(value);
  }
  return out;
}

export async function get<T>(url: string, params: Params = {}, config: AxiosRequestConfig = {}): Promise<T> {
  const response = await http.get<T>(url, { ...config, params: clean(params) });
  return response.data;
}

/** Sends the fields the way the Android app does: as form fields, empty ones left out. */
export async function send<T>(
  method: 'post' | 'put' | 'delete',
  url: string,
  body: Params = {},
  query: Params = {},
): Promise<T> {
  const response = await http.request<T>({
    method,
    url,
    params: clean(query),
    data: new URLSearchParams(clean(body)),
  });
  return response.data;
}

export async function upload<T>(url: string, form: FormData): Promise<T> {
  const response = await http.post<T>(url, form, { timeout: 180_000 });
  return response.data;
}

/** True when the request never got an answer (offline, DNS, timeout). */
export function isNetworkError(error: unknown): boolean {
  return axios.isAxiosError(error) && !error.response;
}
