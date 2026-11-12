import { useState, type ReactNode } from 'react';
import { useLocation, useParams } from 'react-router';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import {
  mdiAccountPlus,
  mdiCalendarRange,
  mdiCellphone,
  mdiCheckboxMarked,
  mdiCurrencyUsd,
  mdiCash,
  mdiHeart,
  mdiHeartOutline,
  mdiMapMarker,
  mdiNote,
  mdiShareVariant,
  mdiViewDashboard,
} from '@mdi/js';
import bedroomUrl from '@/assets/img/bedroom.png';
import chassisUrl from '@/assets/img/chassis.png';
import contractUrl from '@/assets/img/contract.png';
import dateCreateUrl from '@/assets/img/date_create.png';
import educationUrl from '@/assets/img/education.png';
import enginUrl from '@/assets/img/engin.png';
import areaUrl from '@/assets/img/eraa.png';
import expertiseUrl from '@/assets/img/expertise.png';
import kmUrl from '@/assets/img/km.png';
import meterUrl from '@/assets/img/meter.png';
import { PLACEHOLDERS } from '@/components/cards';
import { DetailShell, InfoRow, LocationCard } from '@/components/DetailShell';
import { Icon, MaskIcon } from '@/components/Icon';
import { parseLatLng } from '@/lib/geo';
import { AppBar, Card, EmptyNote, Screen, Spinner } from '@/components/ui';
import * as api from '@/lib/api';
import { adShareText, requireUser, shareText } from '@/lib/actions';
import { blank, text } from '@/lib/format';
import { unstash } from '@/lib/hooks';
import { AGREEMENT_TYPES, CHASSIS_TYPES, EDUCATION_LEVELS, estateAmount, estateGroup, labelOf, roomCountLabel, VEHICLE_GROUP_LABELS, vehiclePrice } from '@/lib/labels';
import type { ClassifiedKind, Estate, Job, Photo, Vehicle } from '@/lib/types';
import { useSession } from '@/store/session';
import { toast } from '@/store/ui';
import { useNavigate } from 'react-router';

/** Detail pages of vehicle, estate and job ads (ShowCarActivity, Show_Home_Ad_Details, Show_Job_Ad_Details). */

const md = (path: string) => <Icon path={path} size={20} />;
const png = (src: string) => <MaskIcon src={src} size={20} />;

function NotFound() {
  return (
    <Screen bar={<AppBar />}>
      <EmptyNote>این آگهی در دسترس نیست. لطفا آن را دوباره از فهرست آگهی ها باز کنید.</EmptyNote>
    </Screen>
  );
}

/** Thumbnail first, then the gallery, without repeating the thumbnail. */
function gallery(item: { thumbnail_photo: unknown; photos?: Photo[] }): string[] {
  const images: string[] = [];
  if (!blank(item.thumbnail_photo)) images.push(String(item.thumbnail_photo));
  for (const photo of item.photos ?? []) if (!images.includes(photo.file_name)) images.push(photo.file_name);
  return images;
}

interface BaseProps {
  kind: ClassifiedKind;
  item: Vehicle | Estate | Job;
  placeholder: string;
  subtitle: string;
  children: ReactNode;
}

/** What the three pages share: the title card with share / favourite, the description and phones, the map. */
function ClassifiedDetail({ kind, item, placeholder, subtitle, children }: BaseProps) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const loggedIn = useSession((state) => Boolean(state.user?.verified && state.token));
  const [busy, setBusy] = useState(false);
  const [override, setOverride] = useState<boolean | null>(null);

  const check = useQuery({
    queryKey: ['classified-fav', kind, String(item.id)],
    queryFn: () => api.isClassifiedFavorite(kind, item.id),
    enabled: loggedIn,
    staleTime: 0,
  });
  const favorite = override ?? check.data ?? false;

  const toggle = async () => {
    if (!loggedIn) {
      toast.failure('لطفا ابتدا ثبت نام نمایید.');
      requireUser(navigate);
      return;
    }
    setBusy(true);
    try {
      const now = await api.toggleClassifiedFavorite(kind, item.id);
      setOverride(now);
      toast.success(now ? 'آگهی مورد نظر با موفقیت به لیست علاقه مندی ها افزوده شد' : 'آگهی مورد نظر با موفقیت از لیست علاقه مندی ها حذف گردید');
      void queryClient.invalidateQueries({ queryKey: ['favorites'] });
    } catch {
      toast.failure('لطفا نحوه ی اتصال به اینترنت دستگاه خود را بررسی نمایید');
    } finally {
      setBusy(false);
    }
  };

  const phones = [item.telephone1, item.telephone2].filter((phone) => !blank(phone)).map(String);

  return (
    <DetailShell title={item.ads_title} images={gallery(item)} placeholder={placeholder} pending={item.status === 'pending'}>
      <Card className="mx-[5px] mt-2.5 mb-[5px] flex items-stretch">
        <button type="button" className="tap-light flex w-[60px] shrink-0 flex-col items-center justify-center gap-0.5 text-[10px]" onClick={() => void shareText(adShareText(item.ads_title, phones, item.address))}>
          <Icon path={mdiShareVariant} size={25} className="text-primary" />
          اشتراک گذاری
        </button>
        <div className="flex min-w-0 flex-1 flex-col items-center py-1 text-center">
          <h1 className="p-[5px] text-base font-normal text-[#009688]">{item.ads_title}</h1>
          <div className="mb-2 px-[5px] text-sm text-[#607D8B]">{subtitle}</div>
        </div>
        <button type="button" disabled={busy || check.isLoading} className="tap-light flex w-20 shrink-0 flex-col items-center justify-center gap-0.5 text-[8px]" onClick={() => void toggle()}>
          {busy || (loggedIn && check.isLoading) ? <Spinner size={25} /> : <Icon path={favorite ? mdiHeart : mdiHeartOutline} size={25} className="text-primary" />}
          افزودن به انتخاب من
        </button>
      </Card>

      <Card className="mx-[5px] mt-2.5 mb-[5px] py-1 pl-[5px]">{children}</Card>

      <Card className="mx-[5px] mt-2.5 mb-[5px] p-1">
        {!blank(item.description) && <p className="mb-[5px] whitespace-pre-line px-1 text-sm leading-8">{String(item.description)}</p>}
        {phones.map((phone) => (
          <a key={phone} href={`tel:${phone}`} className="mt-[5px] flex items-center gap-1 text-sm text-[#2196F3]">
            <Icon path={mdiCellphone} size={22} className="text-primary" />
            <span dir="ltr">{phone}</span>
          </a>
        ))}
        <div className="h-1" />
      </Card>

      <div className="h-2.5" />
      <LocationCard position={parseLatLng(item.latitude, item.longitude)} />
    </DetailShell>
  );
}

function place(item: { city_name: unknown; region_name: unknown }): string {
  return [text(item.city_name), text(item.region_name)].filter(Boolean).join('،');
}

export function VehicleDetailPage() {
  const { id } = useParams();
  const location = useLocation();
  const vehicle = unstash<Vehicle>('vehicle', id);
  if (!vehicle) return <NotFound />;
  // The list the ad was opened from decides the layout in the app; fall back to the ad's own type.
  const type = (location.state as { listType?: string } | null)?.listType || vehicle.type;

  return (
    <ClassifiedDetail kind="vehicles" item={vehicle} placeholder={PLACEHOLDERS.vehicle} subtitle={vehiclePrice(vehicle)}>
      <InfoRow label="موقعیت" icon={md(mdiMapMarker)} value={place(vehicle)} />
      <InfoRow label="گروه بندی" icon={md(mdiViewDashboard)} value={type === 'khodro' ? text(vehicle.brand) : (VEHICLE_GROUP_LABELS[type] ?? '')} />
      {type === 'khodro' && !blank(vehicle.model_id) && <InfoRow label="مدل خودرو" icon={md(mdiCalendarRange)} value={text(vehicle.model_name)} />}
      {type === 'khodro' && <InfoRow label="نوع شاسی" icon={png(chassisUrl)} value={labelOf(CHASSIS_TYPES, vehicle.chassis_type)} />}
      {(type === 'khodro' || type === 'motorcycle') && <InfoRow label="سال تولید" icon={png(dateCreateUrl)} value={text(vehicle.production_year)} />}
      {type === 'khodro' && <InfoRow label="کیلومتر" icon={png(kmUrl)} value={text(vehicle.kilometre)} />}
      {type === 'motorcycle' && <InfoRow label="حجم موتور" icon={png(enginUrl)} value={<span dir="ltr">{text(vehicle.cylinder_volume)}</span>} />}
    </ClassifiedDetail>
  );
}

export function EstateDetailPage() {
  const { id } = useParams();
  const estate = unstash<Estate>('estate', id);
  if (!estate) return <NotFound />;
  const group = estateGroup(estate);
  const sale = group === '1' || group === '3';
  const rent = group === '2' || group === '4';
  const requested = estate.sell_or_buy === 'buy';

  return (
    <ClassifiedDetail kind="estates" item={estate} placeholder={PLACEHOLDERS.estate} subtitle={text(estate.elapsed_time)}>
      {(sale || rent) && <InfoRow label="متراژ (متر مربع)" icon={png(meterUrl)} value={`${text(estate.meters)}متر`} />}
      {sale && <InfoRow label="قیمت کل" icon={md(mdiCash)} value={estateAmount(estate.price_kharid, 'توافقی', 'جهت معاوضه')} />}
      {rent && <InfoRow label="ودیعه" icon={md(mdiCash)} value={estateAmount(estate.pre_pay_ejare, 'توافقی', 'مجانی')} />}
      {rent && <InfoRow label="اجاره ماهیانه" icon={md(mdiCurrencyUsd)} value={estateAmount(estate.monthly_price_ejare, 'توافقی', 'مجانی')} />}
      {group === '3' && <InfoRow label="سند اداری" icon={md(mdiNote)} value={String(estate.sanad_edari) === '1' ? 'دارد' : 'ندارد'} />}
      {(sale || rent) && <InfoRow label="نوع" icon={md(mdiViewDashboard)} value={requested ? 'درخواستی' : sale ? 'فروشی' : 'ارائه'} />}
      <InfoRow label="نوع آگهی دهنده" icon={md(mdiAccountPlus)} value={estate.user_type === 'moshaver_amlak' ? 'مشاور املاک' : estate.user_type === 'person' ? 'شخصی' : ''} />
      {(sale || rent) && String(estate.category_id) !== '8' && <InfoRow label="تعداد اتاق" icon={png(bedroomUrl)} value={roomCountLabel(estate.rooms_count)} />}
      {(sale || rent) && <InfoRow label="حومه شهر" icon={png(areaUrl)} value={String(estate.is_in_hoome) === '0' || blank(estate.is_in_hoome) ? 'نیست' : 'هست'} />}
      <InfoRow label="موقعیت" icon={md(mdiMapMarker)} value={place(estate)} />
    </ClassifiedDetail>
  );
}

export function JobDetailPage() {
  const { id } = useParams();
  const job = unstash<Job>('job', id);
  if (!job) return <NotFound />;
  return (
    <ClassifiedDetail kind="employs" item={job} placeholder={PLACEHOLDERS.job} subtitle={text(job.elapsed_time)}>
      <InfoRow label="موقعیت" icon={md(mdiMapMarker)} value={place(job)} />
      <InfoRow label="گروه بندی" icon={md(mdiViewDashboard)} value="استخدام" />
      <InfoRow label="میزان تحصیلات" icon={png(educationUrl)} value={labelOf(EDUCATION_LEVELS, job.education_level)} />
      <InfoRow label="نوع قرارداد" icon={png(contractUrl)} value={labelOf(AGREEMENT_TYPES, job.agremment_type)} />
      <InfoRow label="آماده به کار / استخدام" icon={md(mdiCheckboxMarked)} value={job.type === 'forsatshoghli' ? 'استخدام' : 'آماده به کار'} />
      <InfoRow label="تخصص" icon={png(expertiseUrl)} value={text(job.specialty)} />
    </ClassifiedDetail>
  );
}
