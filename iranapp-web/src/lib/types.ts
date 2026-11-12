/** Shapes of the JSON the API returns. Numbers often arrive as strings and vice versa. */

export type Id = number | string;
export type Maybe<T> = T | null | undefined;

export interface Photo {
  id: Id;
  file_name: string;
}

export interface Named {
  id: Id;
  name: string;
}

export interface Category extends Named {
  icon?: string | null;
}

export interface SubCategory extends Named {
  category_id: Id;
  icon?: string | null;
}

export interface Region extends Named {
  city_id: Id;
}

export type LikeType = 'like' | 'dislike';

/** A business ad (کسب و کار): discount or need. */
export interface Ad {
  id: Id;
  title: string;
  latitude: Maybe<number | string>;
  longitude: Maybe<number | string>;
  city_id: Id;
  province_id?: Id;
  address: Maybe<string>;
  type: 'discount' | 'need';
  category_id?: Id;
  sub_category_id: Id;
  updates_count: Maybe<number | string>;
  max_number_of_update?: Maybe<number | string>;
  email: Maybe<string>;
  mobile: Maybe<string>;
  tel1: Maybe<string>;
  tel2: Maybe<string>;
  link: Maybe<string>;
  discount: Maybe<string>;
  working_time: Maybe<string>;
  telegram: Maybe<string>;
  instagram: Maybe<string>;
  notes: Maybe<string>;
  status: string;
  ads_owner_name: Maybe<string>;
  ads_plan_id: Id;
  user_id: Maybe<Id>;
  category_name?: string;
  sub_category_name?: string;
  city_name?: string;
  province_name?: string;
  num_of_stars?: number | string;
  photos: Photo[];
  likes?: number;
  dislikes?: number;
  video_url?: Maybe<string>;
  user_like_type?: Maybe<LikeType>;
  distance?: number;
}

export interface VipAd extends Ad {
  vip_ads_id?: Id;
  vip_ads_photo?: Maybe<string>;
}

export interface AdPlan {
  id: Id;
  max_number_of_photos: number | string;
  price: number | string;
  plan_title: string;
  num_of_updates: number | string;
  interval_days: number | string;
}

export interface News {
  id: Id;
  title: string;
  passage: string;
  created_at: string;
  created_at_fa?: string;
  photos: Photo[];
}

export type VehicleType = 'khodro' | 'motorcycle' | 'khodroclasic' | 'khordrosorn' | 'lavazem' | 'other';

interface ClassifiedBase {
  id: Id;
  user_id: Maybe<Id>;
  thumbnail_photo: Maybe<string>;
  region_id: Maybe<Id>;
  region_name: Maybe<string>;
  city_id: Maybe<Id>;
  city_name: Maybe<string>;
  province_id: Maybe<Id>;
  province_name: Maybe<string>;
  status: string;
  ads_title: string;
  description: Maybe<string>;
  latitude: Maybe<number | string>;
  longitude: Maybe<number | string>;
  address: Maybe<string>;
  telephone1: Maybe<string>;
  telephone2: Maybe<string>;
  ads_owner_name: Maybe<string>;
  photos: Photo[];
  is_pro?: Maybe<number | string>;
}

export interface Vehicle extends ClassifiedBase {
  type: VehicleType;
  price: Maybe<number | string>;
  kilometre: Maybe<number | string>;
  production_year: Maybe<number | string>;
  chassis_type: Maybe<string>;
  cylinder_volume: Maybe<string>;
  cylinder_volume_id?: Maybe<Id>;
  neworold: Maybe<string>;
  brand_id: Maybe<Id>;
  brand: Maybe<string>;
  model_id: Maybe<Id>;
  model_name: Maybe<string>;
  passed_time?: Maybe<string>;
  person_or_company: Maybe<string>;
}

export interface Estate extends ClassifiedBase {
  category_id: Id;
  category_parent_id: Maybe<Id>;
  category_name?: Maybe<string>;
  user_type: Maybe<string>;
  is_in_hoome: Maybe<number | string>;
  sell_or_buy: Maybe<string>;
  price_kharid: Maybe<number | string>;
  pre_pay_ejare: Maybe<number | string>;
  monthly_price_ejare: Maybe<number | string>;
  rooms_count: Maybe<number | string>;
  meters: Maybe<number | string>;
  sanad_edari: Maybe<number | string>;
  elapsed_time?: Maybe<string>;
}

export interface Job extends ClassifiedBase {
  specialty_id: Maybe<Id>;
  specialty: Maybe<string>;
  education_level: Maybe<string>;
  agremment_type: Maybe<string>;
  type: Maybe<string>;
  elapsed_time?: Maybe<string>;
  person_or_company: Maybe<string>;
}

export type ClassifiedKind = 'vehicles' | 'estates' | 'employs';

export interface ApiUser {
  id: Id;
  first_name: string;
  last_name: string;
  mobile: string;
  jwt_token: string;
  is_mobile_verified: number | string;
  send_news_notifications: Maybe<number | string>;
  send_ads_notifications: Maybe<number | string>;
  role?: string;
}

export interface EContract {
  id: number;
  status: 'pending' | 'awaiting_payment' | 'payment_submitted' | 'approved' | 'rejected';
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
  end_date: string;
  duration_months: number;
  discount_percent: number;
  contract_date: string;
  contract_text: Maybe<string>;
  rejection_reason: Maybe<string>;
  submitted_at: Maybe<string>;
  reviewed_at: Maybe<string>;
  billing: Maybe<string>;
  amount_formatted: Maybe<string>;
  card_number: Maybe<string>;
  card_number_formatted: Maybe<string>;
  card_holder: Maybe<string>;
  payment_message: Maybe<string>;
  payment_submitted_at: Maybe<string>;
  payment_rejection_reason: Maybe<string>;
  days_left: Maybe<number>;
  expiry_reminder_days?: number;
}

export interface ContractTemplate {
  version: string;
  title: string;
  blank?: string;
  sections: Array<{ heading?: string | null; text: string }>;
}

export interface KeyLabel {
  key: string;
  label: string;
}

export interface EContractState {
  status: number;
  role: string;
  today: string;
  contract: EContract | null;
  template: ContractTemplate | null;
  options: { business_types: KeyLabel[]; manager_titles: KeyLabel[]; durations: number[] };
}

export interface MembershipCard {
  id: number;
  status: 'pending' | 'approved' | 'rejected';
  serial_number: string;
  members: string[];
  national_code: Maybe<string>;
  membership_date: string;
  expiry_date: string;
  is_expired: boolean;
  rejection_reason: Maybe<string>;
  submitted_at: Maybe<string>;
}

export interface MembershipState {
  status: number;
  title: string;
  today: string;
  holder: string;
  min_members: number;
  max_members: number;
  validity_months: number;
  card: MembershipCard | null;
}

export interface AdStats {
  status: number;
  views: number;
  views_week: number;
  favorites: number;
  favorites_week: number;
  likes: number;
  likes_week: number;
  dislikes: number;
}
