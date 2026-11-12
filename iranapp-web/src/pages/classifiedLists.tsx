import type { ReactNode } from 'react';
import { useNavigate, useSearchParams } from 'react-router';
import { mdiChevronLeft, mdiMagnify } from '@mdi/js';
import carUrl from '@/assets/img/car1.png';
import workerUrl from '@/assets/img/worker.png';
import { EstateCard, JobCard, VehicleCard } from '@/components/cards';
import { Icon, MaskIcon } from '@/components/Icon';
import { PagedList } from '@/components/PagedList';
import { AppBar, Screen } from '@/components/ui';
import * as api from '@/lib/api';
import { usePagedList, useScrollRestore } from '@/lib/hooks';
import { ESTATE_SUB_CATEGORIES } from '@/lib/labels';
import { useSession } from '@/store/session';

function SearchButton({ to }: { to: string }) {
  const navigate = useNavigate();
  return (
    <button type="button" aria-label="جستجو" className="tap-light flex h-12 w-12 items-center justify-center rounded-full" onClick={() => navigate(to)}>
      <Icon path={mdiMagnify} />
    </button>
  );
}

function NoRows({ icon }: { icon?: string }) {
  return (
    <div className="flex min-h-[320px] flex-col items-center justify-center gap-1.5 text-sm text-[#444]">
      {icon && <MaskIcon src={icon} size={40} color="var(--color-primary)" />}
      متاسفانه هیچ داده ای یافت نشد
    </div>
  );
}

function ListScreen({ searchTo, children }: { searchTo?: string; children: ReactNode }) {
  const scrollRef = useScrollRestore<HTMLDivElement>();
  return (
    <Screen bar={<AppBar action={searchTo ? <SearchButton to={searchTo} /> : undefined} />} className="bg-[#eee]">
      <div ref={scrollRef} className="min-h-0 flex-1 overflow-y-auto pt-[5px]">
        {children}
      </div>
    </Screen>
  );
}

/** Show_Car_list: vehicles of one type, or of pro users (?pro=1). */
export function VehicleListPage() {
  const [params] = useSearchParams();
  const cityId = useSession((state) => state.cityId);
  const proOnly = params.get('pro') === '1';
  const type = params.get('type') ?? '';
  const list = usePagedList(['vehicles', cityId, proOnly, type], (offset) => api.getVehicles({ type, proOnly, cityId }, offset));
  return (
    <ListScreen searchTo={proOnly ? undefined : `/search/vehicles?type=${type}`}>
      <PagedList list={list} render={(vehicle) => <VehicleCard key={vehicle.id} vehicle={vehicle} listType={type} />} empty={<NoRows icon={carUrl} />} />
    </ListScreen>
  );
}

/** Show_Home_List: estates of a category with its sub categories on top, or of pro users. */
export function EstateListPage() {
  const navigate = useNavigate();
  const [params] = useSearchParams();
  const cityId = useSession((state) => state.cityId);
  const proOnly = params.get('pro') === '1';
  const category = params.get('cat') ?? '';
  const sub = params.get('sub') ?? '';
  const list = usePagedList(['estates', cityId, proOnly, category, sub], (offset) => api.getEstates({ categoryId: sub || category, proOnly, cityId }, offset));
  const subCategories = !proOnly && !sub ? (ESTATE_SUB_CATEGORIES[category] ?? []) : [];

  return (
    <ListScreen searchTo={proOnly ? undefined : `/search/estates?cat=${category}&sub=${sub}`}>
      {subCategories.map((item) => (
        <button
          key={item.value}
          type="button"
          className="tap-light mx-[5px] mb-[5px] flex w-[calc(100%-10px)] items-center rounded-[3px] bg-white p-2.5 text-right text-base text-[#009688]"
          style={{ boxShadow: 'var(--shadow-card)' }}
          onClick={() => navigate(`/estates?cat=${category}&sub=${item.value}`)}
        >
          <span className="flex-1">{item.label}</span>
          <Icon path={mdiChevronLeft} size={25} />
        </button>
      ))}
      <PagedList list={list} render={(estate) => <EstateCard key={estate.id} estate={estate} />} empty={<NoRows />} />
    </ListScreen>
  );
}

/** Show_Job_list: job ads (hiring, seeking, both), or of pro users. */
export function JobListPage() {
  const [params] = useSearchParams();
  const cityId = useSession((state) => state.cityId);
  const proOnly = params.get('pro') === '1';
  const type = params.get('type') ?? '';
  const list = usePagedList(['jobs', cityId, proOnly, type], (offset) => api.getJobs({ type, proOnly, cityId }, offset));
  return (
    <ListScreen searchTo={proOnly ? undefined : `/search/jobs?type=${type}`}>
      <PagedList list={list} render={(job) => <JobCard key={job.id} job={job} />} empty={<NoRows icon={workerUrl} />} />
    </ListScreen>
  );
}
