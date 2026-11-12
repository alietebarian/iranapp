import { useEffect, useState } from 'react';
import { NavLink, Outlet } from 'react-router';
import { mdiMenu } from '@mdi/js';
import heartUrl from '@/assets/img/heart.png';
import kasbokarUrl from '@/assets/img/kasbokar.png';
import newspaperUrl from '@/assets/img/newspaper.png';
import { VipAdDialog } from '@/components/dialogs';
import { Icon, MaskIcon, vectorUrl } from '@/components/Icon';
import { AppBar } from '@/components/ui';
import { getHomeVipAd } from '@/lib/api';
import type { VipAd } from '@/lib/types';
import { useSession } from '@/store/session';
import { alertDialog } from '@/store/ui';

/** Bottom bar of MainActivity, listed right to left as it appears on screen. */
const TABS = [
  { to: '/', label: 'کسب و کار', mask: kasbokarUrl },
  { to: '/news', label: 'اخبار', mask: newspaperUrl },
  { to: '/classifieds', label: 'نیازمندی ها', mask: vectorUrl('ic_nav_classifieds') },
  { to: '/favorites', label: 'انتخاب من', mask: heartUrl },
  { to: '/menu', label: 'منو', path: mdiMenu },
];

let vipAdChecked = false;

/** activity_main: the dark logo bar, the current tab, and the five-item bottom bar. */
export default function MainLayout() {
  const cityId = useSession((state) => state.cityId);
  const guideHintSeen = useSession((state) => state.guideHintSeen);
  const setGuideHintSeen = useSession((state) => state.setGuideHintSeen);
  const [vipAd, setVipAd] = useState<VipAd | null>(null);

  // A promoted ad may cover the screen once per visit, as the splash screen arranges in the app.
  useEffect(() => {
    if (vipAdChecked || !cityId || cityId === '0') return;
    vipAdChecked = true;
    getHomeVipAd(cityId)
      .then((ad) => ad && setVipAd(ad))
      .catch(() => undefined);
  }, [cityId]);

  useEffect(() => {
    if (guideHintSeen) return;
    setGuideHintSeen();
    void alertDialog('برای استفاده بهتر از تمامی خدمات این برنامه حتماً ابتدا از قسمت منو به راهنمای برنامه مراجعه نمایید', 'متوجه شدم');
  }, [guideHintSeen, setGuideHintSeen]);

  return (
    <div className="flex h-full flex-col bg-[#eee]">
      <AppBar back={false} dark />
      <main className="relative min-h-0 flex-1">
        <Outlet />
      </main>
      <nav className="z-20 flex shrink-0 bg-home-bar pb-safe" style={{ boxShadow: '0 -2px 5px rgb(0 0 0 / 0.3)' }} aria-label="بخش های برنامه">
        {TABS.map((tab) => (
          <NavLink
            key={tab.to}
            to={tab.to}
            end={tab.to === '/'}
            className={({ isActive }) => `tap-dark flex h-14 flex-1 flex-col items-center justify-center gap-0.5 text-xs ${isActive ? 'text-home-accent' : 'text-home-nav'}`}
          >
            {tab.path ? <Icon path={tab.path} size={25} /> : <MaskIcon src={tab.mask!} size={25} />}
            <span>{tab.label}</span>
          </NavLink>
        ))}
      </nav>
      {vipAd && <VipAdDialog ad={vipAd} onClose={() => setVipAd(null)} />}
    </div>
  );
}
