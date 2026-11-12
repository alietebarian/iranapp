import { useState, type ReactNode } from 'react';
import heartsUrl from '@/assets/img/hearts.png';
import { AdCard, EstateCard, JobCard, VehicleCard } from '@/components/cards';
import { MaskIcon } from '@/components/Icon';
import { PagedList } from '@/components/PagedList';
import { Tabs } from '@/components/ui';
import * as api from '@/lib/api';
import { usePagedList, useScrollRestore } from '@/lib/hooks';
import { useSession } from '@/store/session';

type Pane = 'ads' | 'estates' | 'vehicles' | 'jobs';

/** Right to left on screen: کسب و کار | املاک | وسایل نقلیه | استخدام (Fav_Host). */
const PANES: Array<{ id: Pane; label: string }> = [
  { id: 'ads', label: 'کسب و کار' },
  { id: 'estates', label: 'املاک' },
  { id: 'vehicles', label: 'وسایل نقلیه' },
  { id: 'jobs', label: 'استخدام' },
];

const EMPTY = (
  <div className="flex h-full min-h-[300px] flex-col items-center justify-center gap-2 px-6 text-center text-sm">
    <MaskIcon src={heartsUrl} size={70} color="var(--color-primary)" className="opacity-50" />
    شما هنوز موردی به علاقه مندی های خود اضافه نکرده اید
  </div>
);

function Pane<T>({ name, fetchPage, render }: { name: string; fetchPage: (offset: number) => Promise<T[]>; render: (item: T) => ReactNode }) {
  const userId = useSession((state) => state.user?.id);
  const list = usePagedList(['favorites', name, userId], fetchPage);
  const scrollRef = useScrollRestore<HTMLDivElement>(name);
  return (
    <div ref={scrollRef} className="h-full overflow-y-auto">
      <PagedList list={list} render={render} empty={EMPTY} />
    </div>
  );
}

let rememberedPane: Pane = 'ads';

/** «انتخاب من»: the ads the user saved, by section. */
export default function FavoritesTab() {
  const loggedIn = useSession((state) => Boolean(state.user?.verified && state.token));
  const [pane, setPaneState] = useState<Pane>(rememberedPane);
  return (
    <div className="flex h-full flex-col bg-[#eee]">
      <div className="min-h-0 flex-1">
        {!loggedIn ? (
          <div className="flex h-full items-center justify-center p-5 text-center text-sm">لطفا به قسمت منو رفته و وارد حساب خود شوید</div>
        ) : pane === 'ads' ? (
          <Pane key="ads" name="ads" fetchPage={api.getFavoriteAds} render={(ad) => <AdCard key={ad.id} ad={ad} />} />
        ) : pane === 'estates' ? (
          <Pane key="estates" name="estates" fetchPage={api.getFavoriteEstates} render={(estate) => <EstateCard key={estate.id} estate={estate} />} />
        ) : pane === 'vehicles' ? (
          <Pane key="vehicles" name="vehicles" fetchPage={api.getFavoriteVehicles} render={(vehicle) => <VehicleCard key={vehicle.id} vehicle={vehicle} />} />
        ) : (
          <Pane key="jobs" name="jobs" fetchPage={api.getFavoriteJobs} render={(job) => <JobCard key={job.id} job={job} />} />
        )}
      </div>
      <Tabs
        tabs={PANES}
        value={pane}
        variant="light"
        onChange={(next) => {
          rememberedPane = next;
          setPaneState(next);
        }}
      />
    </div>
  );
}
