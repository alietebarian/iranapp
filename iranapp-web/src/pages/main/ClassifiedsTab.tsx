import { useState } from 'react';
import { useNavigate } from 'react-router';
import { mdiChevronLeft } from '@mdi/js';
import { Icon, VectorIcon } from '@/components/Icon';
import { Tabs } from '@/components/ui';
import { Tile } from './KasbokarTab';

type Pane = 'estates' | 'vehicles' | 'jobs';

/** Right to left on screen: املاک | وسایل نقلیه | استخدام (Home_Host). */
const PANES: Array<{ id: Pane; label: string }> = [
  { id: 'estates', label: 'املاک' },
  { id: 'vehicles', label: 'وسایل نقلیه' },
  { id: 'jobs', label: 'استخدام' },
];

interface TileSpec {
  icon: string;
  name: string;
  subtitle?: string;
  to: string;
}

const SECTIONS: Record<Pane, { pro: { subtitle: string; to: string }; tiles: TileSpec[] }> = {
  estates: {
    pro: { subtitle: 'آگهی های املاک کاربران پرو', to: '/estates?pro=1' },
    tiles: [
      { icon: 'ic_estate_home_sale', name: 'فروش مسکونی', subtitle: 'آپارتمان، خانه، زمین', to: '/estates?cat=1' },
      { icon: 'ic_estate_home_rent', name: 'اجاره مسکونی', subtitle: 'آپارتمان، خانه', to: '/estates?cat=2' },
      { icon: 'ic_estate_services', name: 'خدمات املاک', to: '/estates?cat=5' },
      { icon: 'ic_estate_office_sale', name: 'فروش اداری و تجاری', subtitle: 'مغازه، دفتر کار، صنعتی', to: '/estates?cat=3' },
      { icon: 'ic_estate_office_rent', name: 'اجاره اداری و تجاری', subtitle: 'مغازه، دفتر کار، صنعتی', to: '/estates?cat=4' },
    ],
  },
  vehicles: {
    pro: { subtitle: 'آگهی های وسایل نقلیه کاربران پرو', to: '/vehicles?pro=1' },
    tiles: [
      { icon: 'ic_cat_car', name: 'خودرو', to: '/vehicles?type=khodro' },
      { icon: 'ic_vehicle_motorcycle', name: 'موتور سیکلت', to: '/vehicles?type=motorcycle' },
      { icon: 'ic_cat_transport', name: 'خودرو سنگین و نیمه سنگین', to: '/vehicles?type=khordrosorn' },
      { icon: 'ic_vehicle_classic', name: 'خودرو کلاسیک', to: '/vehicles?type=khodroclasic' },
      { icon: 'ic_cat_service', name: 'لوازم وسایل نقلیه', to: '/vehicles?type=lavazem' },
      { icon: 'ic_vehicle_bicycle', name: 'سایر وسایل نقلیه', to: '/vehicles?type=other' },
    ],
  },
  jobs: {
    pro: { subtitle: 'آگهی های استخدام کاربران پرو', to: '/jobs?pro=1' },
    tiles: [
      { icon: 'ic_job_seeker', name: 'آماده به کار', to: '/jobs?type=karjoo' },
      { icon: 'ic_cat_job', name: 'استخدام', to: '/jobs?type=forsatshoghli' },
      { icon: 'ic_job_both', name: 'هر دو', to: '/jobs' },
    ],
  },
};

let rememberedPane: Pane = 'estates';

/** The «نیازمندی ها» tab: estates, vehicles and jobs, each with its «کاربران پرو» row. */
export default function ClassifiedsTab() {
  const navigate = useNavigate();
  const [pane, setPaneState] = useState<Pane>(rememberedPane);
  const section = SECTIONS[pane];
  return (
    <div className="flex h-full flex-col">
      <div className="min-h-0 flex-1 overflow-y-auto bg-home-dark px-[7px] py-2.5">
        <button
          type="button"
          className="tap-dark m-[5px] flex w-[calc(100%-10px)] items-center rounded-2xl border border-home-accent/50 bg-home-tile px-3 py-2.5 text-right"
          onClick={() => navigate(section.pro.to)}
        >
          <span className="flex shrink-0 items-center justify-center rounded-full border border-home-plate-stroke bg-home-plate size-sdp-36">
            <VectorIcon name="ic_pro_badge" gradient size={0} className="size-sdp-18" />
          </span>
          <span className="mr-3 min-w-0 flex-1">
            <span className="block text-[13px] text-home-text">کاربران پرو</span>
            <span className="mt-0.5 block text-[10px] text-home-muted">{section.pro.subtitle}</span>
          </span>
          <Icon path={mdiChevronLeft} size={18} className="text-home-nav" />
        </button>
        <div className="grid grid-cols-3">
          {section.tiles.map((tile) => (
            <Tile key={tile.to} icon={tile.icon} name={tile.name} subtitle={tile.subtitle} onClick={() => navigate(tile.to)} />
          ))}
        </div>
      </div>
      <Tabs
        tabs={PANES}
        value={pane}
        variant="dark"
        onChange={(next) => {
          rememberedPane = next;
          setPaneState(next);
        }}
      />
    </div>
  );
}
