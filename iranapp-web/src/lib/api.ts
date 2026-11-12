import { get, send, upload } from './http';
import type {
  Ad,
  AdPlan,
  AdStats,
  ApiUser,
  Category,
  ClassifiedKind,
  EContract,
  EContractState,
  ContractTemplate,
  Estate,
  Id,
  Job,
  LikeType,
  MembershipCard,
  MembershipState,
  Named,
  News,
  Region,
  SubCategory,
  Vehicle,
  VipAd,
} from './types';

/** Every endpoint of routes/api.php the Android app uses, one function each. */

export const PAGE_SIZE = 10;

interface ListResponse<T> {
  status: number;
  list: T[];
}

interface StatusOnly {
  status: number;
  error?: string;
  message?: string;
  errors?: unknown;
}

const list = async <T>(promise: Promise<ListResponse<T>>): Promise<T[]> => (await promise).list ?? [];

// ---------------------------------------------------------------- lookups

export const getProvinces = () => list(get<ListResponse<Named>>('/provinces'));
export const getCities = (provinceId: Id) => list(get<ListResponse<Named>>(`/provinces/${provinceId}/cities`));
export const getRegions = (cityId: Id) => list(get<ListResponse<Region>>(`/provinces/cities/${cityId}/regions`));

export const getCategories = () => list(get<ListResponse<Category>>('/categories'));
export const getAllSubCategories = (categoryId: Id) =>
  list(get<ListResponse<SubCategory>>(`/categories/${categoryId}/subcategories/all`));

/** Categories that have ads in a city, plus (sometimes) a VIP ad to show full screen. */
export async function getCityCategories(cityId: Id) {
  const body = await get<{ status: number; data?: { categories?: Category[]; vip_ad?: VipAd | null } }>(
    `/city/${cityId}/ads/category`,
  );
  return { categories: body.data?.categories ?? [], vipAd: body.data?.vip_ad ?? null };
}

/** Sub categories of a category that have ads in the city. */
export const getCitySubCategories = (categoryId: Id, cityId: Id) =>
  list(get<ListResponse<SubCategory>>(`/categories/${categoryId}/subcategories`, { city_id: cityId }));

export const getPlans = () => list(get<ListResponse<AdPlan>>('/ads/plans'));
export const getBrands = () => list(get<ListResponse<Named>>('/vehicles/brands'));
export const getModels = (brandId: Id) => list(get<ListResponse<Named>>(`/vehicles/brands/${brandId}/models`));
export const getCylinderVolumes = () => list(get<ListResponse<{ id: Id; value: string }>>('/cylinder-volumes'));
export const getSpecialities = () => list(get<ListResponse<Named>>('/employs/specialities'));

// ---------------------------------------------------------------- business ads

/** Ads of a sub category in a city; the first page may carry a VIP ad to show full screen. */
export async function getSubCategoryAds(cityId: Id, subCategoryId: Id, offset: number) {
  const body = await get<ListResponse<Ad> & { vip_ad?: VipAd | null }>(`/cities/${cityId}/subCategories/${subCategoryId}/ads`, {
    offset,
    limit: PAGE_SIZE,
  });
  return { list: body.list ?? [], vipAd: body.vip_ad ?? null };
}

export const getLatestAds = (cityId: Id, offset: number) =>
  list(get<ListResponse<Ad>>('/ads/latest', { city_id: cityId, offset, limit: PAGE_SIZE }));

export interface AdSearch {
  title?: string;
  categoryId?: Id;
  subCategoryId?: Id;
  cityId: Id;
  discountOnly?: boolean;
}

export const searchAds = (search: AdSearch, offset: number) =>
  list(
    send<ListResponse<Ad>>('post', '/ads/search', {
      offset,
      limit: PAGE_SIZE,
      title: search.title,
      subcategory_id: search.subCategoryId,
      category_id: search.subCategoryId ? undefined : search.categoryId,
      city_id: search.cityId,
      type: search.discountOnly ? 'discount' : undefined,
    }),
  );

export async function getAd(id: Id): Promise<Ad | null> {
  const body = await get<{ status: number; ad?: Ad | null }>(`/ads/id/${id}`);
  return body.ad ?? null;
}

export const getNearestAds = (latitude: number, longitude: number, categoryId?: Id, subCategoryId?: Id) =>
  list(
    get<ListResponse<Ad>>('/nearest-ads', {
      latitude,
      longitude,
      sub_category_id: subCategoryId,
      category_id: subCategoryId ? undefined : categoryId,
    }),
  );

export async function getHomeVipAd(cityId: Id): Promise<VipAd | null> {
  const body = await get<{ status: number; ad?: VipAd | null }>(`/home-page/ads/${cityId}/vip`);
  return body.ad ?? null;
}

export interface AdPrefs {
  like: LikeType | null;
  favorite: boolean;
  likes: number;
  dislikes: number;
}

/** The viewer's own like / favourite on an ad, with the totals. Needs a logged-in user. */
export async function getAdPrefs(id: Id): Promise<AdPrefs> {
  const body = await get<{ status: number; like: LikeType | null; fav: number | string; likes_count: number; dislikes_count: number }>(
    `/users/ads/${id}`,
  );
  if (String(body.status) !== '200') throw new Error('prefs');
  return {
    like: body.like ?? null,
    favorite: String(body.fav) === '1',
    likes: Number(body.likes_count) || 0,
    dislikes: Number(body.dislikes_count) || 0,
  };
}

export async function getAdLikeCounts(id: Id): Promise<AdPrefs> {
  const body = await get<{ likes: number; dislike: number }>(`/ads/${id}/likes-and-dislikes`);
  return { like: null, favorite: false, likes: Number(body.likes) || 0, dislikes: Number(body.dislike) || 0 };
}

export interface LikeResult {
  status: number;
  like_type?: LikeType | null;
  likes_count?: number;
  dislikes_count?: number;
}

export const likeAd = (id: Id, type: LikeType) => send<LikeResult>('post', `/ads/${id}/likes`, {}, { like_type: type });
export const unlikeAd = (id: Id, type: LikeType) => get<LikeResult>(`/ads/${id}/like/off`, { like_type: type });
export const toggleAdFavorite = (id: Id) => send<{ status: number; fav_status?: string }>('post', `/ads/${id}/fav`);
export const recordAdView = (id: Id) => send<StatusOnly>('post', `/ads/${id}/views`);
export const deleteAd = (id: Id) => get<StatusOnly>(`/ads/${id}/delete`);
export const getAdStats = (id: Id) => get<AdStats>(`/ads/${id}/stats`);

/** «برای استفاده از تخفیف این مرکز»: tells the shop the user is at its door (within 250 m). */
export const requestDiscount = (adId: Id, latitude: number, longitude: number) =>
  send<StatusOnly>('post', '/distance', { adsId: adId, latitude, longitude });

export const getMyAds = (offset: number) => list(get<ListResponse<Ad>>('/current-user/ads', { offset, limit: PAGE_SIZE }));
export const getFavoriteAds = (offset: number) =>
  list(get<ListResponse<Ad>>('/users/ads/favorites', { offset, limit: PAGE_SIZE }));

export const saveAd = (form: FormData) => upload<StatusOnly & { ads?: Ad }>('/ads', form);
export const updateAd = (id: Id, form: FormData) => upload<StatusOnly & { ads?: Ad }>(`/ads/${id}/update`, form);

// ---------------------------------------------------------------- vehicles / estates / jobs

export interface VehicleQuery {
  type?: string;
  proOnly?: boolean;
  cityId: Id;
}

export const getVehicles = (query: VehicleQuery, offset: number) =>
  list(
    get<ListResponse<Vehicle>>('/vehicles/ads', {
      type: query.proOnly ? undefined : query.type,
      pro_only: query.proOnly ? 1 : undefined,
      city_id: query.cityId,
      offset,
      limit: PAGE_SIZE,
    }),
  );

export interface EstateQuery {
  categoryId?: Id;
  proOnly?: boolean;
  cityId: Id;
}

export const getEstates = (query: EstateQuery, offset: number) =>
  list(
    get<ListResponse<Estate>>(`/estates/city/${query.cityId}/ads`, {
      offset,
      limit: PAGE_SIZE,
      pro_only: query.proOnly ? 1 : undefined,
      category_id: query.proOnly ? undefined : query.categoryId,
    }),
  );

export interface JobQuery {
  type?: string;
  proOnly?: boolean;
  cityId: Id;
}

export const getJobs = (query: JobQuery, offset: number) =>
  list(
    get<ListResponse<Job>>(`/employs/cities/${query.cityId}/ads`, {
      offset,
      limit: PAGE_SIZE,
      pro_only: query.proOnly ? 1 : undefined,
      type: query.proOnly ? undefined : query.type,
    }),
  );

type SearchParams = Record<string, string | number | undefined>;

export const searchVehicles = (params: SearchParams, offset: number) =>
  list(send<ListResponse<Vehicle>>('post', '/vehicles/search', { ...params, offset, limit: PAGE_SIZE }));
export const searchEstates = (params: SearchParams, offset: number) =>
  list(send<ListResponse<Estate>>('post', '/estates/search', { ...params, offset, limit: PAGE_SIZE }));
export const searchJobs = (params: SearchParams, offset: number) =>
  list(send<ListResponse<Job>>('post', '/employs/search', { ...params, offset, limit: PAGE_SIZE }));

export const getMyVehicles = (offset: number) =>
  list(get<ListResponse<Vehicle>>('/vehicles/users/ads', { offset, limit: PAGE_SIZE }));
export const getMyEstates = (offset: number) => list(get<ListResponse<Estate>>('/estates/ads', { offset, limit: PAGE_SIZE }));
export const getMyJobs = (offset: number) => list(get<ListResponse<Job>>('/employs/users/ads', { offset, limit: PAGE_SIZE }));

export const getFavoriteVehicles = (offset: number) =>
  list(get<ListResponse<Vehicle>>('/users/vehicle-ads/favorites', { offset, limit: PAGE_SIZE }));
export const getFavoriteEstates = (offset: number) =>
  list(get<ListResponse<Estate>>('/users/estate-ads/favorites', { offset, limit: PAGE_SIZE }));
export const getFavoriteJobs = (offset: number) =>
  list(get<ListResponse<Job>>('/users/employs-ads/favorites', { offset, limit: PAGE_SIZE }));

export const deleteVehicle = (id: Id) => send<StatusOnly>('delete', `/vehicles/${id}`);
export const deleteEstate = (id: Id) => send<StatusOnly>('delete', `/estates/ads/${id}`);
export const deleteJob = (id: Id) => send<StatusOnly>('delete', `/employs/${id}`);

export const saveVehicle = (form: FormData) => upload<StatusOnly>('/vehicles', form);
export const updateVehicle = (id: Id, form: FormData) => upload<StatusOnly>(`/vehicles/${id}/update`, form);
export const saveEstate = (form: FormData) => upload<StatusOnly>('/estates/save', form);
export const updateEstate = (id: Id, form: FormData) => upload<StatusOnly>(`/estates/ads/${id}/update`, form);
export const saveJob = (form: FormData) => upload<StatusOnly>('/employs', form);
export const updateJob = (id: Id, form: FormData) => upload<StatusOnly>(`/employs/${id}/update`, form);

export async function toggleClassifiedFavorite(kind: ClassifiedKind, id: Id): Promise<boolean> {
  const body = await send<{ status: number; is_fav?: boolean | string }>('post', '/vip-ads/favorite', {
    type: kind,
    ads_id: id,
  });
  if (String(body.status) !== '200') throw new Error('favorite');
  return String(body.is_fav) === 'true';
}

export async function isClassifiedFavorite(kind: ClassifiedKind, id: Id): Promise<boolean> {
  const body = await get<{ status: number; is_favorite?: boolean | string }>('/vip-ads/favorite/check', {
    adsId: id,
    adType: kind,
  });
  return String(body.is_favorite) === 'true';
}

// ---------------------------------------------------------------- news

export const getNews = (offset: number) => list(get<ListResponse<News>>('/news', { offset, limit: PAGE_SIZE }));

export async function getNewsItem(id: Id): Promise<News | null> {
  const body = await get<{ status: number; news?: News | null }>(`/news/${id}`);
  return body.news ?? null;
}

// ---------------------------------------------------------------- account

export interface AuthResult {
  status: number | string;
  user?: ApiUser;
  user_id?: Id;
  error?: string;
  errors?: Record<string, string[]>;
}

export const login = (mobile: string, password: string, pushToken?: string | null) =>
  send<AuthResult>('post', '/login', { mobile, password, fcm_token: pushToken });

export interface RegisterInput {
  firstName: string;
  lastName: string;
  birthDate: string;
  mobile: string;
  password: string;
  pushToken?: string | null;
}

export const register = (input: RegisterInput) =>
  send<AuthResult>('post', '/register', {
    first_name: input.firstName,
    last_name: input.lastName,
    birth_date: input.birthDate,
    mobile: input.mobile,
    password: input.password,
    fcm_token: input.pushToken,
  });

export const verifyAndLogin = (mobile: string, code: string) =>
  send<AuthResult>('post', '/verify-and-login', { mobile, token: code });

export const resendVerifyCode = (mobile: string) =>
  send<StatusOnly>('put', '/users/verify-token/regenerate', {}, { mobile });

export const changeMobile = (oldMobile: string, newMobile: string) =>
  send<StatusOnly>('put', '/users/mobile/change', {}, { old_mobile: oldMobile, new_mobile: newMobile });

export const sendResetPasswordCode = (mobile: string) =>
  send<StatusOnly>('put', '/users/password/reset-password/token/send', { mobile });
export const verifyResetPasswordCode = (mobile: string, code: string) =>
  get<StatusOnly>('/reset-password/token/verify', { mobile, verifyToken: code });
export const resetPassword = (mobile: string, code: string, newPassword: string) =>
  send<StatusOnly>('put', '/reset-password', { mobile, verifyToken: code, new_password: newPassword });

export const changeAccountInfo = (firstName: string, lastName: string) =>
  send<StatusOnly>('post', '/account-info/change', { first_name: firstName, last_name: lastName });
export const changePassword = (oldPassword: string, newPassword: string) =>
  send<StatusOnly>('post', '/password/change', { old_password: oldPassword, new_password: newPassword });

// ---------------------------------------------------------------- app, notifications

export const getAppVersion = () => get<{ status: number; app_version: number | string }>('/app-version');
export const getInstallStats = () => get<{ status: number; total: number; month: number; today: number }>('/app-stats/installs');

/** Registers this browser's push token (the app's /notification-settings/create). */
export const registerPushToken = (token: string, oldToken?: string | null) =>
  send<StatusOnly>('post', '/notification-settings/create', {}, { token, old_token: oldToken });

export const updateNotificationSettings = (token: string, news: boolean, ads: boolean, cityId: Id) =>
  send<StatusOnly>(
    'put',
    '/notification-settings/update',
    {},
    { send_news_notifications: news ? 1 : 0, send_ads_notifications: ads ? 1 : 0, token, city_id: cityId },
  );

// ---------------------------------------------------------------- electronic contract, membership card

export const getEContract = () => get<EContractState>('/e-contracts/current');

export interface EContractInput {
  business_type: string;
  business_name: string;
  manager_title: string;
  manager_name: string;
  national_code: string;
  phone: string;
  mobile: string;
  address: string;
  subject: string;
  start_date: string;
  duration_months: number;
  discount_percent: string;
  template_version: string;
  app_version: string;
}

export interface EContractSubmitResult extends StatusOnly {
  contract?: EContract;
  errors?: string[];
  template?: ContractTemplate;
}

export const submitEContract = (input: EContractInput) =>
  send<EContractSubmitResult>('post', '/e-contracts', { ...input, terms_accepted: 1 });

export const submitEContractPayment = (reference: string) =>
  send<EContractSubmitResult>('post', '/e-contracts/payment', { payment_reference: reference });

export const getMembershipCard = () => get<MembershipState>('/membership-card');

export interface MembershipSubmitResult extends StatusOnly {
  card?: MembershipCard;
  errors?: string[];
}

export function submitMembershipCard(members: string[], nationalCode: string, appVersion: string) {
  const body: Record<string, string> = { national_code: nationalCode, app_version: appVersion };
  members.forEach((name, index) => {
    body[`members[${index}]`] = name;
  });
  return send<MembershipSubmitResult>('post', '/membership-card', body);
}
