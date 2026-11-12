import { blank, thousands } from './format';
import type { Estate, Vehicle } from './types';

/** Fixed option lists and the Persian labels of the API's enum values, as hard-coded in the app. */

export const VEHICLE_TYPES = [
  { value: 'khodro', label: 'خودرو' },
  { value: 'motorcycle', label: 'موتور سیکلت' },
  { value: 'khodroclasic', label: 'خودرو کلاسیک' },
  { value: 'khordrosorn', label: 'خودرو سنگین و نیمه سنگین' },
  { value: 'lavazem', label: 'لوازم و وسایل نقلیه' },
  { value: 'other', label: 'سایر وسایل نقلیه' },
];

/** What the detail page shows under «گروه بندی» for the non-car types. */
export const VEHICLE_GROUP_LABELS: Record<string, string> = {
  motorcycle: 'موتور سیکلت',
  other: 'سایر وسایل نقلیه',
  khodroclasic: 'خودرو کلاسیک',
  khordrosorn: 'خودرو سنگین',
  lavazem: 'لوازم وسایل نقلیه',
};

export const CHASSIS_TYPES = [
  { value: 'savari', label: 'سواری' },
  { value: 'hachback', label: 'هاچ بک' },
  { value: 'shasiboland', label: 'شاسی بلند' },
  { value: 'vanet', label: 'وانت' },
  { value: 'krook', label: 'کروک' },
  { value: 'van', label: 'ون' },
  { value: 'cupe', label: 'کوپه' },
  { value: 'station', label: 'استیشن' },
  { value: 'other', label: 'دیگر' },
];

export const ESTATE_CATEGORIES = [
  { value: '1', label: 'فروشی مسکونی (آپارتمان،خانه،زمین)' },
  { value: '2', label: 'اجاره مسکونی (آپارتمان،خانه،زمین)' },
  { value: '3', label: 'فروش اداری و تجاری (مغازه،دفتر،صنعتی)' },
  { value: '4', label: 'اجاره اداری و تجاری (مغازه،دفتر،صنعتی)' },
  { value: '5', label: 'خدمات املاک' },
];

export const ESTATE_SUB_CATEGORIES: Record<string, Array<{ value: string; label: string }>> = {
  '1': [
    { value: '6', label: 'آپارتمان' },
    { value: '7', label: 'خانه و ویلا' },
    { value: '8', label: 'زمین و کلنگی' },
  ],
  '2': [
    { value: '9', label: 'آپارتمان' },
    { value: '10', label: 'خانه و ویلا' },
  ],
  '3': [
    { value: '11', label: 'دفترکار،اتاق اداری و مطب' },
    { value: '12', label: 'مغازه و غرفه' },
    { value: '13', label: 'صنعتی،کشاورزی وتجاری' },
  ],
  '4': [
    { value: '14', label: 'دفترکار،اتاق اداری و مطب' },
    { value: '15', label: 'مغازه و غرفه' },
    { value: '16', label: 'صنعتی،کشاورزی وتجاری' },
  ],
  '5': [
    { value: '17', label: 'آژانس املاک' },
    { value: '18', label: 'مشارکت در ساخت' },
    { value: '19', label: 'امور مالی و حقوقی' },
    { value: '20', label: 'پیش فروش' },
  ],
};

/** The top-level estate category (1..5) an ad belongs to, whichever level its own category is. */
export function estateGroup(estate: Pick<Estate, 'category_id' | 'category_parent_id'>): string {
  const parent = blank(estate.category_parent_id) ? '' : String(estate.category_parent_id);
  if (parent && parent !== '0') return parent;
  const own = String(estate.category_id);
  for (const [group, subs] of Object.entries(ESTATE_SUB_CATEGORIES)) {
    if (subs.some((sub) => sub.value === own)) return group;
  }
  return own;
}

export const ROOM_COUNTS = [
  { value: '0', label: 'بدون اتاق' },
  { value: '1', label: 'یک' },
  { value: '2', label: 'دو' },
  { value: '3', label: 'سه' },
  { value: '4', label: 'چهار' },
  { value: '5', label: 'پنج یا بیشتر' },
];

export function roomCountLabel(count: unknown): string {
  return ROOM_COUNTS.find((room) => room.value === String(count))?.label ?? 'بدون اتاق';
}

export const JOB_TYPES = [
  { value: 'forsatshoghli', label: 'استخدام' },
  { value: 'karjoo', label: 'آماده به کار' },
];

export const EDUCATION_LEVELS = [
  { value: 'underdiploma', label: 'زیر دیپلم' },
  { value: 'diploma', label: 'دیپلم' },
  { value: 'tact', label: 'کاردانی' },
  { value: 'expertise', label: 'کارشناسی' },
  { value: 'masterdegree', label: 'کارشناسی ارشد' },
  { value: 'doctoral', label: 'دکترا' },
];

export const AGREEMENT_TYPES = [
  { value: 'tamamvaght', label: 'تمام وقت' },
  { value: 'parevaght', label: 'پاره وقت' },
  { value: 'moshaveri', label: 'مشاوره ای' },
  { value: 'projei', label: 'پروژه ای' },
];

export function labelOf(options: Array<{ value: string; label: string }>, value: unknown): string {
  return options.find((option) => option.value === String(value))?.label ?? '';
}

/** "12,000,000 تومان" or «قیمت توافقی» when the price is 0. */
export function vehiclePrice(vehicle: Pick<Vehicle, 'price'>): string {
  const price = blank(vehicle.price) ? '0' : String(vehicle.price);
  return price === '0' ? 'قیمت توافقی' : `${thousands(price)} تومان`;
}

/**
 * Estate amounts use two sentinels: 0 = negotiable, -1 = the third radio of the form
 * («جهت معاوضه» for a sale price, «مجانی» for rent and deposit).
 */
export function estateAmount(value: unknown, negotiable: string, sentinel: string): string {
  const amount = blank(value) ? '0' : String(value);
  if (amount === '0') return negotiable;
  if (amount === '-1') return sentinel;
  return `${thousands(amount)} تومان`;
}

export const STATUS_LABELS: Record<string, { label: string; color: string }> = {
  pending: { label: 'در انتظار تایید', color: '#FB8C00' },
  rejected: { label: 'رد شده', color: '#D32F2F' },
  approved: { label: 'تایید شد', color: '#388E3C' },
};

export const PENDING_MESSAGE = 'آگهی شما در حال بررسی است و پس از تایید نمایش داده خواهد شد';
