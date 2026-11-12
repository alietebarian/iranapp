import { useRef, useState } from 'react';
import { useNavigate } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { mdiCellphone, mdiChevronLeft, mdiCircle, mdiMagnify, mdiMapMarker, mdiPlus } from '@mdi/js';
import { AdCard } from '@/components/cards';
import { SubCategoriesDialog } from '@/components/dialogs';
import { SelectField } from '@/components/fields';
import { Icon, VectorIcon } from '@/components/Icon';
import { PagedList } from '@/components/PagedList';
import { EmptyNote, LinearProgress, Loading, Tabs } from '@/components/ui';
import * as api from '@/lib/api';
import { requireUser } from '@/lib/actions';
import { categoryIcon, DEFAULT_ICON } from '@/lib/categoryIcons';
import { cx, thousands } from '@/lib/format';
import { usePagedList, useScrollRestore } from '@/lib/hooks';
import { iconUrl } from '@/lib/media';
import type { Category } from '@/lib/types';
import { hasChosenCity, useSession } from '@/store/session';
import { openAdTypeDialog, openCityPicker } from '@/store/ui';

type Pane = 'newest' | 'categories' | 'search';

/** Right to left on screen: جدیدترینها | دسته بندی ها | جستجو (KasbokarFragment). */
const PANES: Array<{ id: Pane; label: string }> = [
  { id: 'newest', label: 'جدیدترینها' },
  { id: 'categories', label: 'دسته بندی ها' },
  { id: 'search', label: 'جستجو' },
];

let rememberedPane: Pane = 'categories';

/** The landing tab «کسب و کار»: its three panes with the tab strip under them. */
export default function KasbokarTab() {
  const [pane, setPaneState] = useState<Pane>(rememberedPane);
  const setPane = (next: Pane) => {
    rememberedPane = next;
    setPaneState(next);
  };
  return (
    <div className="flex h-full flex-col">
      <div className="relative min-h-0 flex-1">
        {/* The search pane stays mounted so its filters and results survive a tab switch. */}
        <div className={cx('h-full', pane !== 'search' && 'hidden')}>
          <SearchPane />
        </div>
        {pane === 'categories' && <CategoriesPane />}
        {pane === 'newest' && <NewestPane />}
      </div>
      <Tabs tabs={PANES} value={pane} onChange={setPane} variant="dark" />
    </div>
  );
}

// ---------------------------------------------------------------- categories

const ABOUT_TILE: Category = { id: 'about', name: 'درباره ما' };

/** A tile of the dark grids: a round plate with a gradient icon and a name under it. */
export function Tile({ icon, remoteIcon, name, subtitle, onClick }: { icon: string | null; remoteIcon?: string; name: string; subtitle?: string; onClick: () => void }) {
  return (
    <button
      type="button"
      className="tap-dark m-[5px] flex flex-col items-center rounded-2xl border border-home-tile-stroke bg-home-tile px-1 pt-3.5 pb-2.5"
      onClick={onClick}
    >
      <span className="flex items-center justify-center rounded-full border border-home-plate-stroke bg-home-plate size-sdp-46">
        {icon === null && remoteIcon ? (
          <img src={remoteIcon} alt="" className="rounded-sm object-contain size-sdp-22" />
        ) : (
          <VectorIcon name={icon ?? DEFAULT_ICON} gradient size={0} className="size-sdp-22" />
        )}
      </span>
      <span className="mt-[9px] line-clamp-2 text-center text-[11px] leading-[18px] text-home-text">{name}</span>
      {subtitle && <span className="mt-0.5 line-clamp-2 text-center text-[9px] leading-4 text-home-muted">{subtitle}</span>}
    </button>
  );
}

/** WithDiscounts: the city bar, install statistics and the grid of categories that have ads. */
function CategoriesPane() {
  const navigate = useNavigate();
  const session = useSession();
  const chosen = hasChosenCity(session);
  const scrollRef = useScrollRestore<HTMLDivElement>('categories');
  const [opened, setOpened] = useState<Category | null>(null);
  const [fabExtended, setFabExtended] = useState(true);
  const lastScroll = useRef(0);

  const categories = useQuery({
    queryKey: ['city-categories', session.cityId],
    queryFn: () => api.getCityCategories(session.cityId),
    enabled: session.cityId !== '0',
  });
  const stats = useQuery({ queryKey: ['install-stats'], queryFn: api.getInstallStats, staleTime: 5 * 60_000 });

  const list = categories.data?.categories ?? [];
  const loading = categories.isLoading && session.cityId !== '0';
  const cityHasNoAds = categories.isSuccess && list.length === 0;
  const hasProvince = session.provinceName && session.provinceName !== session.cityName;

  return (
    <div className="relative flex h-full flex-col bg-home-dark">
      <button
        type="button"
        className={cx('tap-dark mx-3 mt-3 flex min-h-[52px] shrink-0 items-center rounded-2xl border bg-home-tile px-3 py-2', chosen ? 'border-home-tile-stroke' : 'border-home-accent/50')}
        onClick={openCityPicker}
      >
        <span className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full border border-home-plate-stroke bg-home-plate text-home-accent">
          <Icon path={mdiMapMarker} size={18} />
        </span>
        <span className="mx-2.5 line-clamp-2 min-w-0 flex-1 text-right text-[13px] text-home-text">
          {chosen ? `شهر شما: ${session.cityName}${hasProvince ? `، ${session.provinceName}` : ''}` : 'شهر خود را انتخاب کنید تا دسته بندی های شهرتان نمایش داده شود'}
        </span>
        <span className="flex shrink-0 items-center text-[13px] text-home-accent">
          {chosen ? 'تغییر' : 'انتخاب شهر'}
          <Icon path={mdiChevronLeft} size={18} />
        </span>
      </button>

      {stats.data && String(stats.data.status) === '200' && (
        <div className="mx-3 mt-3 shrink-0 rounded-2xl border border-home-tile-stroke bg-home-tile pt-2.5 pb-3">
          <div className="flex items-center gap-2 px-3.5 text-xs text-home-text">
            <span className="flex h-[26px] w-[26px] items-center justify-center rounded-full border border-home-plate-stroke bg-home-plate text-home-accent">
              <Icon path={mdiCellphone} size={14} />
            </span>
            آمار نصب ایران آپ
          </div>
          <div className="mt-2.5 flex items-center">
            <Stat value={stats.data.total} label="کل نصب‌ها" accent />
            <span className="h-7 w-px bg-home-tile-stroke" />
            <Stat value={stats.data.month} label="۳۰ روز اخیر" />
            <span className="h-7 w-px bg-home-tile-stroke" />
            <Stat value={stats.data.today} label="امروز" />
          </div>
        </div>
      )}

      {loading ? (
        <Loading color="var(--color-home-accent)" />
      ) : (
        <div
          ref={scrollRef}
          className="min-h-0 flex-1 overflow-y-auto"
          onScroll={(event) => {
            const top = event.currentTarget.scrollTop;
            if (top > lastScroll.current + 4) setFabExtended(false);
            else if (top < lastScroll.current - 4 || top === 0) setFabExtended(true);
            lastScroll.current = top;
          }}
        >
          <div className="grid grid-cols-3 px-[7px] pt-2.5">
            {[...list, ABOUT_TILE].map((category) => (
              <Tile
                key={category.id}
                name={category.name}
                icon={category.id === 'about' ? 'ic_cat_about' : categoryIcon(category.name)}
                remoteIcon={iconUrl(category.icon)}
                onClick={() => (category.id === 'about' ? navigate('/about') : setOpened(category))}
              />
            ))}
          </div>
          {cityHasNoAds && (
            <div className="mx-7 mt-3 mb-6">
              <span className="mx-auto flex items-center justify-center rounded-full bg-home-accent/15 size-sdp-64">
                <VectorIcon name="ic_cat_default" color="var(--color-home-accent)" size={0} className="size-sdp-32" />
              </span>
              <p className="mt-[18px] text-center text-sm text-home-text">متاسفانه هنوز در شهر شما آگهی ثبت نشده است</p>
              <Hint>شما میتوانید با زدن دکمه «ثبت آگهی» در پایین صفحه، اولین آگهی شهرتان را ثبت کنید.</Hint>
              <Hint>برای دیدن آگهی های شهرهای اطراف، روی نوار شهر در بالای صفحه بزنید و شهر دیگری انتخاب کنید.</Hint>
            </div>
          )}
          <div className="h-[84px]" />
        </div>
      )}

      <button
        type="button"
        aria-label="ثبت آگهی"
        className="tap absolute bottom-4 left-4 flex h-12 items-center gap-1.5 rounded-full bg-home-accent px-3.5 text-sm text-white shadow-lg transition-all"
        onClick={() => requireUser(navigate) && openAdTypeDialog()}
      >
        <Icon path={mdiPlus} size={22} />
        {fabExtended && <span className="pl-1.5">ثبت آگهی</span>}
      </button>

      {opened && <SubCategoriesDialog category={opened} onClose={() => setOpened(null)} />}
    </div>
  );
}

function Stat({ value, label, accent }: { value: number; label: string; accent?: boolean }) {
  return (
    <div className="flex flex-1 flex-col items-center">
      <span className={cx('text-base', accent ? 'text-home-accent' : 'text-home-text')}>{thousands(value)}</span>
      <span className="mt-0.5 text-[10px] text-home-muted">{label}</span>
    </div>
  );
}

function Hint({ children }: { children: string }) {
  return (
    <div className="mt-3.5 flex items-center gap-3.5 text-xs leading-6 text-home-muted">
      <Icon path={mdiCircle} size={8} className="text-home-accent" />
      <span className="flex-1">{children}</span>
    </div>
  );
}

// ---------------------------------------------------------------- newest

/** Newest: the latest ads of the city. */
function NewestPane() {
  const cityId = useSession((state) => state.cityId);
  const list = usePagedList(['ads-latest', cityId], (offset) => api.getLatestAds(cityId, offset));
  const scrollRef = useScrollRestore<HTMLDivElement>('newest');
  return (
    <div ref={scrollRef} className="h-full overflow-y-auto bg-[#eee]">
      <PagedList
        list={list}
        render={(ad) => <AdCard key={ad.id} ad={ad} />}
        empty={
          <div className="m-[30px] text-sm text-mild">
            <p className="text-center text-[15px]">متاسفانه هنوز در شهر شما آگهی ثبت نشده است</p>
            <LightHint>شما میتوانید با رفتن به تب «منو» و با زدن بر روی «درج آگهی» اولین آگهی شهرتان را ثبت کنید.</LightHint>
            <LightHint>برای دیدن آگهی های شهرهای اطراف به تب «منو» بروید، و با زدن بر روی «تعیین موقعیت» شهر های اطراف خود را انتخاب کنید.</LightHint>
          </div>
        }
      />
    </div>
  );
}

function LightHint({ children }: { children: string }) {
  return (
    <div className="mt-5 flex items-start gap-5">
      <Icon path={mdiCircle} size={14} className="mt-1.5 text-primary/50" />
      <span className="flex-1 leading-7">{children}</span>
    </div>
  );
}

// ---------------------------------------------------------------- search

interface Criteria {
  title: string;
  categoryId: string;
  subCategoryId: string;
  discountOnly: boolean;
}

/** Search: a keyword with category / sub category / ad type filters, searched in the user's city. */
function SearchPane() {
  const cityId = useSession((state) => state.cityId);
  const [title, setTitle] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [subCategoryId, setSubCategoryId] = useState('');
  const [adType, setAdType] = useState('');
  const [criteria, setCriteria] = useState<Criteria | null>(null);
  const scrollRef = useScrollRestore<HTMLDivElement>('search');

  const categories = useQuery({ queryKey: ['categories'], queryFn: api.getCategories, staleTime: Infinity });
  const subCategories = useQuery({
    queryKey: ['subcategories-all', categoryId],
    queryFn: () => api.getAllSubCategories(categoryId),
    enabled: categoryId !== '',
    staleTime: Infinity,
  });

  const list = usePagedList(
    ['ads-search', cityId, criteria],
    (offset) => api.searchAds({ title: criteria!.title, categoryId: criteria!.categoryId, subCategoryId: criteria!.subCategoryId, discountOnly: criteria!.discountOnly, cityId }, offset),
    criteria !== null,
  );

  const search = () => {
    (document.activeElement as HTMLElement | null)?.blur();
    setCriteria({ title: title.trim(), categoryId, subCategoryId, discountOnly: adType === 'discount' });
  };

  return (
    <div className="flex h-full flex-col bg-[#eee]">
      <div className="shrink-0 bg-white shadow">
        <div className="m-2.5 flex items-center gap-1">
          <input
            type="search"
            value={title}
            enterKeyHint="search"
            placeholder="کلمه مورد نظر را وارد کنید"
            aria-label="کلمه مورد نظر را وارد کنید"
            className="h-11 min-w-0 flex-1 border-b border-[#bdbdbd] px-1 text-[15px] focus:border-primary"
            onChange={(event) => setTitle(event.target.value)}
            onKeyDown={(event) => event.key === 'Enter' && search()}
          />
          <button type="button" aria-label="جستجو" className="tap-light flex h-[50px] w-[50px] items-center justify-center rounded-full text-mild" onClick={search}>
            <Icon path={mdiMagnify} size={30} />
          </button>
        </div>
        <div className="flex gap-2 px-2">
          <SelectField
            bare
            className="min-w-0 flex-1"
            label="دسته:"
            placeholder="انتخاب کنید"
            value={categoryId}
            disabled={!categories.data}
            options={(categories.data ?? []).map((c) => ({ value: String(c.id), label: c.name }))}
            onChange={(value) => {
              setCategoryId(value);
              setSubCategoryId('');
            }}
          />
          <SelectField
            bare
            className="min-w-0 flex-1"
            label="زیر دسته:"
            placeholder="انتخاب کنید"
            value={subCategoryId}
            disabled={!categoryId || !subCategories.data}
            options={(subCategories.data ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
            onChange={setSubCategoryId}
          />
        </div>
        <div className="flex px-2 pb-1">
          <SelectField
            bare
            className="w-1/2"
            label="نوع آگهی:"
            placeholder="همه موارد"
            value={adType}
            options={[{ value: 'discount', label: 'تخفیف دار ها' }]}
            onChange={setAdType}
          />
        </div>
        {categories.isLoading || subCategories.isFetching ? <LinearProgress color="var(--color-primary)" /> : <div className="h-1" />}
        <div className="h-px bg-primary" />
      </div>
      <div ref={scrollRef} className="min-h-0 flex-1 overflow-y-auto">
        {criteria !== null && (
          <PagedList list={list} render={(ad) => <AdCard key={ad.id} ad={ad} />} empty={<EmptyNote>هیج آگهی برای متن مورد نظر شما ثبت نشده است</EmptyNote>} />
        )}
      </div>
    </div>
  );
}
