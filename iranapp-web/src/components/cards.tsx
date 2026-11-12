import { useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router';
import { mdiMapMarker, mdiPhone, mdiPlay, mdiThumbUp } from '@mdi/js';
import decagramUrl from '@/assets/img/decagram.png';
import pendingUrl from '@/assets/img/pending.png';
import placeholderUrl from '@/assets/img/place_holder.jpg';
import placeholderCarUrl from '@/assets/img/place_holder_car.jpg';
import placeholderHomeUrl from '@/assets/img/place_holder_home.jpg';
import placeholderJobUrl from '@/assets/img/place_holder_job.jpg';
import { likeAd, unlikeAd } from '@/lib/api';
import { requireUser } from '@/lib/actions';
import { blank, cx, text } from '@/lib/format';
import { stash } from '@/lib/hooks';
import { AGREEMENT_TYPES, estateAmount, estateGroup, labelOf, vehiclePrice } from '@/lib/labels';
import { mediaUrl } from '@/lib/media';
import type { Ad, Estate, Job, Vehicle } from '@/lib/types';
import { Icon, MaskIcon, VectorIcon } from './Icon';

/** An image that falls back to the app's grey placeholder when missing or broken. */
export function Photo({ src, fallback = placeholderUrl, className, alt = '' }: { src?: string | null; fallback?: string; className?: string; alt?: string }) {
  const [failed, setFailed] = useState(false);
  const url = !failed ? mediaUrl(src) : undefined;
  return (
    <img
      src={url ?? fallback}
      alt={alt}
      loading="lazy"
      decoding="async"
      className={cx('bg-[#c7c7c7] object-cover', className)}
      onError={() => setFailed(true)}
    />
  );
}

/** The «کاربر پرو» tag on ads of users with an approved electronic contract. */
export function ProBadge() {
  return (
    <span className="mx-[5px] mt-[5px] inline-flex items-center gap-1 self-start rounded-[10px] border border-pro-accent bg-pro-bg py-px pr-[5px] pl-2 text-[10px] text-pro-text">
      <VectorIcon name="ic_pro_user" size={12} color="var(--color-pro-accent)" />
      کاربر پرو
    </span>
  );
}

function Row({ children, onClick, image }: { children: ReactNode; onClick: () => void; image: ReactNode }) {
  return (
    <div
      role="link"
      tabIndex={0}
      className="tap-light m-[3px] flex cursor-pointer overflow-hidden rounded-[3px] bg-white"
      style={{ boxShadow: 'var(--shadow-card)' }}
      onClick={onClick}
      onKeyDown={(event) => {
        if (event.key === 'Enter') onClick();
      }}
    >
      <div className="flex min-w-0 flex-1 flex-col">{children}</div>
      {image}
    </div>
  );
}

function AddressLine({ children }: { children: ReactNode }) {
  return (
    <div className="flex items-center gap-1 py-[5px] pr-0 pl-[5px]">
      <Icon path={mdiMapMarker} size={20} className="text-primary" />
      <span className="min-w-0 flex-1 truncate text-xs">{children}</span>
    </div>
  );
}

function PendingMark() {
  return <MaskIcon src={pendingUrl} size={20} color="#FB8C00" className="mr-[5px] mb-1 self-end" />;
}

/** The first usable phone of a business ad: mobile, then the two land lines. */
export function firstPhone(ad: Pick<Ad, 'mobile' | 'tel1' | 'tel2'>): string {
  for (const phone of [ad.mobile, ad.tel1, ad.tel2]) {
    if (!blank(phone) && phone !== '0') return String(phone);
  }
  return '';
}

export function hasDiscount(ad: Pick<Ad, 'discount'>): boolean {
  return !blank(ad.discount) && ad.discount !== '0';
}

/** rv_ads_in_list: a business ad in every list of the app. */
export function AdCard({ ad }: { ad: Ad }) {
  const navigate = useNavigate();
  const [, rerender] = useState(0);
  const liked = ad.user_like_type === 'like';
  const phone = firstPhone(ad);

  /** Flips the vote locally first so the row reacts at once; the server's counts win when they arrive. */
  const toggleLike = async () => {
    if (!requireUser(navigate)) return;
    const before = { type: ad.user_like_type ?? null, likes: ad.likes ?? 0, dislikes: ad.dislikes ?? 0 };
    if (liked) {
      ad.user_like_type = null;
      ad.likes = Math.max(0, before.likes - 1);
    } else {
      ad.user_like_type = 'like';
      ad.likes = before.likes + 1;
      if (before.type === 'dislike') ad.dislikes = Math.max(0, before.dislikes - 1);
    }
    rerender((n) => n + 1);
    try {
      const result = liked ? await unlikeAd(ad.id, 'like') : await likeAd(ad.id, 'like');
      if (String(result.status) === '200') {
        ad.user_like_type = result.like_type ?? null;
        ad.likes = result.likes_count ?? ad.likes;
        ad.dislikes = result.dislikes_count ?? ad.dislikes;
      } else if (String(result.status) !== '204') {
        throw new Error('like');
      }
    } catch {
      ad.user_like_type = before.type;
      ad.likes = before.likes;
      ad.dislikes = before.dislikes;
    }
    rerender((n) => n + 1);
  };

  return (
    <Row
      onClick={() => {
        stash('ad', ad.id, ad);
        navigate(`/ad/${ad.id}`);
      }}
      image={
        <div className="relative shrink-0 self-start overflow-hidden rounded-[3px] size-sdp-80">
          <Photo src={ad.photos?.[0]?.file_name} className="h-full w-full" alt={ad.title} />
          {!blank(ad.video_url) && (
            <span className="absolute top-1/2 left-1/2 flex h-7 w-7 -translate-x-1/2 -translate-y-1/2 items-center justify-center rounded-full bg-black/60 text-white">
              <Icon path={mdiPlay} size={20} />
            </span>
          )}
        </div>
      }
    >
      <div className="m-[5px] line-clamp-2 text-sm text-primary">{ad.title}</div>
      <AddressLine>{text(ad.address)}</AddressLine>
      <div className="flex items-center pl-[2px]">
        <div className={cx('flex min-w-0 flex-1 items-center gap-1', !phone && 'invisible')}>
          <Icon path={mdiPhone} size={20} className="text-primary" />
          <span className="m-[5px] truncate text-xs" dir="ltr">
            {phone}
          </span>
        </div>
        <button
          type="button"
          aria-label={liked ? 'برداشتن پسند' : 'پسندیدن'}
          aria-pressed={liked}
          className="tap-light flex items-center gap-[3px] rounded-full px-1.5 py-1"
          style={{ color: liked ? '#4CAF50' : '#999999' }}
          onClick={(event) => {
            event.stopPropagation();
            void toggleLike();
          }}
        >
          <span className="text-[11px]">{ad.likes ?? 0}</span>
          <Icon path={mdiThumbUp} size={18} />
        </button>
        {hasDiscount(ad) && <DiscountBadge discount={String(ad.discount)} size={35} />}
      </div>
      {ad.status === 'pending' && <PendingMark />}
    </Row>
  );
}

/**
 * The red rosette carrying the discount. Usually a number ("50"), but owners also type ranges
 * such as "10 الی 15", which get a smaller type so they still fit.
 */
export function DiscountBadge({ discount, size }: { discount: string; size: number }) {
  const value = discount.trim();
  const numeric = /^[\d.]+$/.test(value);
  const fontSize = numeric ? size * 0.28 : Math.max(7, size * (value.length > 8 ? 0.15 : 0.19));
  return (
    <span className="relative flex shrink-0 items-center justify-center" style={{ width: size, height: size }}>
      <MaskIcon src={decagramUrl} size={size} color="var(--color-primary)" className="absolute inset-0 opacity-90" />
      <span className="relative px-[12%] text-center leading-none text-white" style={{ fontSize }} dir={numeric ? 'ltr' : 'rtl'}>
        {numeric ? `%${value}` : `${value}%`}
      </span>
    </span>
  );
}

function classifiedPhoto(item: { thumbnail_photo: unknown; photos?: Array<{ file_name: string }> }): string | undefined {
  if (!blank(item.thumbnail_photo)) return String(item.thumbnail_photo);
  return item.photos?.[0]?.file_name;
}

function isPro(item: { is_pro?: unknown }): boolean {
  return String(item.is_pro ?? '0') === '1';
}

/** "اصفهان،خانه اصفهان / 3 روز پیش" */
function placeAndAge(item: { city_name: unknown; region_name: unknown }, age: unknown): string {
  const place = [text(item.city_name), text(item.region_name)].filter(Boolean).join('،');
  return blank(age) ? place : `${place} / ${age}`;
}

/** rv_car_in_list */
export function VehicleCard({ vehicle, listType }: { vehicle: Vehicle; listType?: string }) {
  const navigate = useNavigate();
  return (
    <Row
      onClick={() => {
        stash('vehicle', vehicle.id, vehicle);
        navigate(`/vehicle/${vehicle.id}`, { state: { listType } });
      }}
      image={<Photo src={classifiedPhoto(vehicle)} fallback={placeholderCarUrl} className="shrink-0 self-start rounded-[3px] size-sdp-80" alt={vehicle.ads_title} />}
    >
      <div className="m-[5px] line-clamp-2 text-sm text-primary">{vehicle.ads_title}</div>
      {isPro(vehicle) && <ProBadge />}
      <div className="m-[5px] text-sm text-[#009688]">{vehiclePrice(vehicle)}</div>
      <AddressLine>{placeAndAge(vehicle, vehicle.passed_time)}</AddressLine>
      {vehicle.status === 'pending' && <PendingMark />}
    </Row>
  );
}

/** The price lines an estate shows: a sale price, or deposit + monthly rent. */
export function estatePriceLines(estate: Estate): string[] {
  const group = estateGroup(estate);
  if (group === '1' || group === '3') {
    const price = estateAmount(estate.price_kharid, 'توافقی', 'جهت معاوضه');
    return [price.endsWith('تومان') ? `قیمت : ${price}` : price];
  }
  if (group === '2' || group === '4') {
    return [
      `ودیعه : ${estateAmount(estate.pre_pay_ejare, 'توافقی', 'مجانی')}`,
      `اجاره ماهیانه : ${estateAmount(estate.monthly_price_ejare, 'توافقی', 'مجانی')}`,
    ];
  }
  return [];
}

/** rv_home_in_list */
export function EstateCard({ estate }: { estate: Estate }) {
  const navigate = useNavigate();
  const lines = estatePriceLines(estate);
  const sale = lines.length === 1;
  return (
    <Row
      onClick={() => {
        stash('estate', estate.id, estate);
        navigate(`/estate/${estate.id}`);
      }}
      image={<Photo src={classifiedPhoto(estate)} fallback={placeholderHomeUrl} className="min-h-sdp-80 shrink-0 self-stretch rounded-[3px] w-sdp-90" alt={estate.ads_title} />}
    >
      <div className="mx-[5px] mt-[3px] line-clamp-2 text-sm text-primary">{estate.ads_title}</div>
      {isPro(estate) && <ProBadge />}
      {lines.map((line) => (
        <div key={line} className={cx('mx-[5px] text-xs', sale ? 'text-[#009688]' : 'text-[#444]')}>
          {line}
        </div>
      ))}
      <AddressLine>{placeAndAge(estate, estate.elapsed_time)}</AddressLine>
      {estate.status === 'pending' && <PendingMark />}
    </Row>
  );
}

/** "استخدام / تمام وقت" */
export function jobSummary(job: Job): string {
  const kind = job.type === 'karjoo' ? 'آماده به کار' : 'استخدام';
  const agreement = labelOf(AGREEMENT_TYPES, job.agremment_type);
  return agreement ? `${kind} / ${agreement}` : kind;
}

/** rv_job_in_list */
export function JobCard({ job }: { job: Job }) {
  const navigate = useNavigate();
  return (
    <Row
      onClick={() => {
        stash('job', job.id, job);
        navigate(`/job/${job.id}`);
      }}
      image={<Photo src={classifiedPhoto(job)} fallback={placeholderJobUrl} className="shrink-0 self-start rounded-[3px] size-sdp-80" alt={job.ads_title} />}
    >
      <div className="m-[5px] line-clamp-2 text-sm text-primary">{job.ads_title}</div>
      {isPro(job) && <ProBadge />}
      <div className="m-[5px] text-[10px] text-[#009688]">{jobSummary(job)}</div>
      <AddressLine>{placeAndAge(job, job.elapsed_time)}</AddressLine>
      {job.status === 'pending' && <PendingMark />}
    </Row>
  );
}

export const PLACEHOLDERS = {
  ad: placeholderUrl,
  vehicle: placeholderCarUrl,
  estate: placeholderHomeUrl,
  job: placeholderJobUrl,
};
