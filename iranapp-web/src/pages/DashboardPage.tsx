import { useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router';
import { useQueryClient } from '@tanstack/react-query';
import { mdiArrowRight, mdiBookOpenVariant, mdiDeleteForever, mdiMapMarker, mdiPlus, mdiReplay } from '@mdi/js';
import accountUrl from '@/assets/img/account.png';
import { AdCard, estatePriceLines, jobSummary, Photo, PLACEHOLDERS } from '@/components/cards';
import { Icon, MaskIcon } from '@/components/Icon';
import { PagedList } from '@/components/PagedList';
import { EmptyNote, Tabs } from '@/components/ui';
import * as api from '@/lib/api';
import { blank, text } from '@/lib/format';
import { stash, usePagedList, useScrollRestore } from '@/lib/hooks';
import { STATUS_LABELS, vehiclePrice } from '@/lib/labels';
import type { Estate, Id, Job, Vehicle } from '@/lib/types';
import { useSession } from '@/store/session';
import { confirmDialog, openAdTypeDialog, toast } from '@/store/ui';

type Pane = 'ads' | 'vehicles' | 'estates' | 'jobs';

/** Right to left on screen: کسب و کار | وسایل نقلیه | املاک | استخدام (DashboardActivity). */
const PANES: Array<{ id: Pane; label: string }> = [
  { id: 'ads', label: 'کسب و کار' },
  { id: 'vehicles', label: 'وسایل نقلیه' },
  { id: 'estates', label: 'املاک' },
  { id: 'jobs', label: 'استخدام' },
];

const EMPTY = <EmptyNote>شما هنوز هیچ آگهی به ثبت نرسانده اید</EmptyNote>;

interface MineProps {
  id: Id;
  title: string;
  status: string;
  image: string | undefined;
  placeholder: string;
  lines: string[];
  address: string;
  onEdit: () => void;
  onDelete: () => Promise<unknown>;
}

/** rv_my_car_in_list: an own ad with its review status; tapping it offers edit and delete. */
function MineCard({ title, status, image, placeholder, lines, address, onEdit, onDelete }: MineProps) {
  const queryClient = useQueryClient();
  const [actions, setActions] = useState(false);
  const state = STATUS_LABELS[status] ?? { label: status, color: '#757575' };

  const remove = async () => {
    if (!(await confirmDialog({ message: 'آیا از حذف این محصول اطمینان دارید؟', positive: 'بله', negative: 'خیر' }))) return;
    try {
      await onDelete();
      await queryClient.invalidateQueries({ queryKey: ['mine'] });
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    }
  };

  return (
    <div className="relative m-[3px] overflow-hidden rounded-[3px] bg-white" style={{ boxShadow: 'var(--shadow-card)' }}>
      <button type="button" className="block w-full text-right" onClick={() => setActions(true)}>
        <span className="flex">
          <span className="flex min-w-0 flex-1 flex-col">
            <span className="m-[5px] line-clamp-2 text-sm text-primary">{title}</span>
            {lines.map((line) => (
              <span key={line} className="mx-[5px] text-sm text-[#009688]">
                {line}
              </span>
            ))}
            <span className="flex items-center gap-1 py-[5px] pl-[5px]">
              <Icon path={mdiMapMarker} size={20} className="text-primary" />
              <span className="min-w-0 flex-1 truncate text-xs">{address}</span>
            </span>
          </span>
          <Photo src={image} fallback={placeholder} className="shrink-0 rounded-[3px] size-sdp-80" />
        </span>
        <span className="flex h-[25px] items-center justify-center text-sm text-white" style={{ background: state.color }}>
          {state.label}
        </span>
      </button>
      {actions && (
        <div className="animate-fade-in absolute inset-0 flex flex-col bg-[#455A64] text-sm text-white">
          <button type="button" className="tap-dark flex h-[30px] items-center gap-2 border-b border-white pr-[5px]" onClick={() => setActions(false)}>
            <Icon path={mdiReplay} size={20} />
            بازگشت
          </button>
          <div className="flex flex-1">
            <button
              type="button"
              className="tap-dark flex flex-1 items-center justify-center gap-1.5 border-l border-white"
              onClick={() => {
                setActions(false);
                onEdit();
              }}
            >
              <Icon path={mdiBookOpenVariant} size={25} />
              ویرایش آگهی
            </button>
            <button
              type="button"
              className="tap-dark flex flex-1 items-center justify-center gap-1.5"
              onClick={() => {
                setActions(false);
                void remove();
              }}
            >
              <Icon path={mdiDeleteForever} size={25} />
              حذف آگهی
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function thumb(item: { thumbnail_photo: unknown; photos?: Array<{ file_name: string }> }) {
  return !blank(item.thumbnail_photo) ? String(item.thumbnail_photo) : item.photos?.[0]?.file_name;
}

const placeOf = (item: { city_name: unknown; region_name: unknown }) => [text(item.city_name), text(item.region_name)].filter(Boolean).join('،');

function PaneList<T>({ name, fetchPage, render }: { name: string; fetchPage: (offset: number) => Promise<T[]>; render: (item: T) => ReactNode }) {
  const userId = useSession((state) => state.user?.id);
  const list = usePagedList(['mine', name, userId], fetchPage);
  return <PagedList list={list} render={render} empty={EMPTY} />;
}

let rememberedPane: Pane = 'ads';

/** DashboardActivity («محیط کاربری»): the user's card and their ads in the four sections. */
export default function DashboardPage() {
  const navigate = useNavigate();
  const user = useSession((state) => state.user);
  const cityName = useSession((state) => state.cityName);
  const provinceName = useSession((state) => state.provinceName);
  const signOut = useSession((state) => state.signOut);
  const queryClient = useQueryClient();
  const [pane, setPaneState] = useState<Pane>(rememberedPane);
  const scrollRef = useScrollRestore<HTMLDivElement>(pane);

  const logout = async () => {
    if (!(await confirmDialog({ message: 'آیا مایل به خروج از حساب کاربری خود هستید؟', positive: 'آری', negative: 'خیر' }))) return;
    signOut();
    queryClient.clear();
    navigate('/', { replace: true });
  };

  const edit = (kind: 'vehicle' | 'estate' | 'job', item: Vehicle | Estate | Job) => {
    stash(kind, item.id, item);
    navigate(`/edit/${kind}/${item.id}`);
  };

  return (
    <div className="animate-page-in flex h-full flex-col bg-[#eee]">
      <header className="shrink-0 bg-primary pt-safe text-white shadow-md">
        <div className="relative flex px-4 pt-5">
          <button type="button" aria-label="بازگشت" className="absolute top-1 right-1 flex h-10 w-10 items-center justify-center" onClick={() => navigate(-1)}>
            <Icon path={mdiArrowRight} size={22} />
          </button>
          <div className="mt-4 flex h-[90px] w-[90px] shrink-0 items-center justify-center rounded-full bg-white">
            <MaskIcon src={accountUrl} size={70} color="var(--color-primary)" />
          </div>
          <div className="mr-4 min-w-0 flex-1 pt-3 text-sm">
            <div className="truncate">
              {user?.firstName} {user?.lastName}
            </div>
            <div className="mt-1" dir="ltr" style={{ textAlign: 'right' }}>
              {user?.mobile}
            </div>
            <div className="mt-1 truncate">{[provinceName, cityName].filter(Boolean).join(' - ') || 'شهر انتخاب نشده'}</div>
            <div className="mt-2.5 flex gap-1.5 text-black">
              <button type="button" className="tap h-[33px] flex-[1.5] rounded-[3px] bg-white shadow" onClick={() => navigate('/account/edit')}>
                ویرایش حساب
              </button>
              <button type="button" className="tap h-[33px] flex-1 rounded-[3px] bg-white shadow" onClick={() => void logout()}>
                خروج
              </button>
            </div>
          </div>
        </div>
        <button type="button" className="tap mx-2.5 mt-4 mb-1.5 flex h-[50px] w-[calc(100%-20px)] items-center justify-center gap-1 rounded-[3px] bg-white text-sm text-[#444] shadow" onClick={openAdTypeDialog}>
          <Icon path={mdiPlus} size={25} />
          آگهی جدید
        </button>
        <Tabs
          tabs={PANES}
          value={pane}
          variant="red"
          onChange={(next) => {
            rememberedPane = next;
            setPaneState(next);
          }}
        />
      </header>

      <div ref={scrollRef} key={pane} className="min-h-0 flex-1 overflow-y-auto pt-0.5">
        {pane === 'ads' && <PaneList name="ads" fetchPage={api.getMyAds} render={(ad) => <AdCard key={ad.id} ad={ad} />} />}
        {pane === 'vehicles' && (
          <PaneList
            name="vehicles"
            fetchPage={api.getMyVehicles}
            render={(vehicle) => (
              <MineCard
                key={vehicle.id}
                id={vehicle.id}
                title={vehicle.ads_title}
                status={vehicle.status}
                image={thumb(vehicle)}
                placeholder={PLACEHOLDERS.vehicle}
                lines={[vehiclePrice(vehicle)]}
                address={placeOf(vehicle)}
                onEdit={() => edit('vehicle', vehicle)}
                onDelete={() => api.deleteVehicle(vehicle.id)}
              />
            )}
          />
        )}
        {pane === 'estates' && (
          <PaneList
            name="estates"
            fetchPage={api.getMyEstates}
            render={(estate) => (
              <MineCard
                key={estate.id}
                id={estate.id}
                title={estate.ads_title}
                status={estate.status}
                image={thumb(estate)}
                placeholder={PLACEHOLDERS.estate}
                lines={estatePriceLines(estate)}
                address={placeOf(estate)}
                onEdit={() => edit('estate', estate)}
                onDelete={() => api.deleteEstate(estate.id)}
              />
            )}
          />
        )}
        {pane === 'jobs' && (
          <PaneList
            name="jobs"
            fetchPage={api.getMyJobs}
            render={(job) => (
              <MineCard
                key={job.id}
                id={job.id}
                title={job.ads_title}
                status={job.status}
                image={thumb(job)}
                placeholder={PLACEHOLDERS.job}
                lines={[jobSummary(job)]}
                address={placeOf(job)}
                onEdit={() => edit('job', job)}
                onDelete={() => api.deleteJob(job.id)}
              />
            )}
          />
        )}
      </div>
    </div>
  );
}
