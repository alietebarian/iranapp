import { lazy, Suspense, useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router';
import { mdiArrowRight, mdiDotsVertical } from '@mdi/js';
import { cx } from '@/lib/format';
import { PENDING_MESSAGE } from '@/lib/labels';
import { Icon } from './Icon';
import { ImageSlider } from './ImageSlider';
import type { LatLng } from '@/lib/geo';
import { Card, Spinner } from './ui';

const MapView = lazy(() => import('./Map'));

export interface MenuAction {
  label: string;
  onClick?: () => void;
}

interface Props {
  title: string;
  images: string[];
  placeholder: string;
  pending?: boolean;
  /** The ⋮ menu of the owner (stats, delete, edit). */
  menu?: MenuAction[];
  /** Height of the photo area in sdp (260 for ads, 220 for news). */
  photoHeight?: number;
  children: ReactNode;
}

/**
 * The frame of every detail page (activity_show_ad and friends): photos under a see-through bar
 * that turns white and shows the title once the photos have scrolled away.
 */
export function DetailShell({ title, images, placeholder, pending, menu, photoHeight = 260, children }: Props) {
  const navigate = useNavigate();
  const [collapsed, setCollapsed] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="animate-page-in relative h-full bg-[#eee]">
      <div
        className={cx('absolute inset-x-0 top-0 z-30 pt-safe transition-colors', collapsed ? 'bg-white text-primary' : 'text-white')}
        style={collapsed ? { boxShadow: 'var(--shadow-bar)' } : { backgroundImage: 'linear-gradient(to bottom, rgb(0 0 0 / 0.55), rgb(0 0 0 / 0.18) 70%, transparent)' }}
      >
        <div className="flex h-14 items-center">
          <button type="button" aria-label="بازگشت" className="flex h-14 w-14 shrink-0 items-center justify-center" onClick={() => navigate(-1)}>
            <Icon path={mdiArrowRight} />
          </button>
          <div className="min-w-0 flex-1 truncate text-center text-sm">{collapsed ? title : ''}</div>
          <div className="relative flex h-14 w-14 shrink-0 items-center justify-center">
            {menu && menu.length > 0 && (
              <button type="button" aria-label="گزینه های آگهی" aria-haspopup="menu" className="flex h-12 w-12 items-center justify-center" onClick={() => setMenuOpen(!menuOpen)}>
                <Icon path={mdiDotsVertical} />
              </button>
            )}
            {menuOpen && menu && (
              <>
                <div className="fixed inset-0 z-10" onClick={() => setMenuOpen(false)} />
                <div className="animate-pop-in absolute top-11 left-2 z-20 min-w-[190px] rounded-[3px] bg-white py-1 text-[#333] shadow-xl" role="menu">
                  {menu.map((action) => (
                    <button
                      key={action.label}
                      type="button"
                      role="menuitem"
                      disabled={!action.onClick}
                      className={cx('block w-full px-4 py-3 text-center text-sm', action.onClick ? 'tap-light' : 'text-[#999]')}
                      onClick={() => {
                        setMenuOpen(false);
                        action.onClick?.();
                      }}
                    >
                      {action.label}
                    </button>
                  ))}
                </div>
              </>
            )}
          </div>
        </div>
        {collapsed && <div className="h-px bg-primary" />}
      </div>

      <div
        className="h-full overflow-y-auto"
        onScroll={(event) => {
          const node = event.currentTarget;
          const photos = node.firstElementChild as HTMLElement | null;
          setCollapsed(node.scrollTop > (photos?.offsetHeight ?? 300) - 60);
        }}
      >
        <ImageSlider images={images} placeholder={placeholder} height={`calc(var(--sdp) * ${photoHeight})`} />
        {pending && <div className="mt-[3px] bg-[#FB8C00] py-[5px] text-center text-sm text-white">{PENDING_MESSAGE}</div>}
        {children}
        <div className="h-4 pb-safe" />
      </div>
    </div>
  );
}

/** The map card at the bottom of a detail page; nothing when the ad has no location. */
export function LocationCard({ position }: { position: LatLng | null }) {
  if (!position) return null;
  return (
    <Card className="mx-[5px] mb-[15px] overflow-hidden">
      <Suspense
        fallback={
          <div className="flex items-center justify-center h-sdp-250">
            <Spinner />
          </div>
        }
      >
        <MapView position={position} className="w-full h-sdp-250" />
      </Suspense>
    </Card>
  );
}

/** One "label … value" line of the property tables on vehicle / estate / job pages. */
export function InfoRow({ label, icon, value }: { label: string; icon: ReactNode; value: ReactNode }) {
  return (
    <div className="flex items-center text-sm text-[#607D8B]">
      <div className="flex flex-1 items-center gap-0.5">
        <span className="flex h-[30px] w-[30px] items-center justify-center text-primary">{icon}</span>
        <span>{label}</span>
      </div>
      <div className="flex-1 pl-1.5">{value}</div>
    </div>
  );
}
