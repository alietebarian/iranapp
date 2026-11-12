import { useState, type ReactNode } from 'react';
import { useParams, useSearchParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { mdiClose, mdiFilterVariant, mdiMagnify } from '@mdi/js';
import { EstateCard, JobCard, VehicleCard } from '@/components/cards';
import { SelectField, TextField, type Option } from '@/components/fields';
import { Icon } from '@/components/Icon';
import { EMPTY_LOCATION, LocationSelects, type LocationValue } from '@/components/LocationSelects';
import { PagedList } from '@/components/PagedList';
import { AppBar, EmptyNote, Modal, Screen } from '@/components/ui';
import * as api from '@/lib/api';
import { cx } from '@/lib/format';
import { usePagedList, useScrollRestore } from '@/lib/hooks';
import { AGREEMENT_TYPES, CHASSIS_TYPES, EDUCATION_LEVELS, ESTATE_CATEGORIES, ESTATE_SUB_CATEGORIES, JOB_TYPES, ROOM_COUNTS, VEHICLE_TYPES } from '@/lib/labels';
import type { Estate, Job, Vehicle } from '@/lib/types';
import { useSession } from '@/store/session';

type Kind = 'vehicles' | 'estates' | 'jobs';
type Filters = Record<string, string>;

interface State {
  keyword: string;
  fields: Filters;
  location: LocationValue;
}

const TITLES: Record<Kind, string> = {
  vehicles: 'فیلتر آگهی ها وسایل نقلیه',
  estates: 'فیلتر آگهی ها املاک',
  jobs: 'فیلتر آگهی ها استخدام',
};

/** The server only applies a range when both ends are sent, so an open end gets a wide default. */
function range(out: Record<string, string>, from: string | undefined, to: string | undefined, fromKey: string, toKey: string) {
  if (!from && !to) return;
  out[fromKey] = from || '0';
  out[toKey] = to || '999999999999';
}

function buildParams(kind: Kind, state: State, userCityId: string): Record<string, string> {
  const f = state.fields;
  const out: Record<string, string> = {};
  const { regionId, cityId, provinceId } = state.location;
  if (regionId) out.region_id = regionId;
  else if (cityId) out.city_id = cityId;
  else if (provinceId) out.province_id = provinceId;
  else out.city_id = userCityId;

  const keyword = state.keyword.trim();
  if (kind === 'vehicles') {
    if (keyword) out.ads_title = keyword;
    if (f.ordering) out.ordering = f.ordering;
    if (f.model) out.model_id = f.model;
    else if (f.brand) out.brand_id = f.brand;
    else if (f.type) out.type = f.type;
    if (f.cylinder) out.cylinder_volume_id = f.cylinder;
    if (f.chassis) out.chassis_type = f.chassis;
    range(out, f.yearFrom, f.yearTo, 'production_year_from', 'production_year_to');
    if (!f.ordering) range(out, f.priceFrom, f.priceTo, 'price_from', 'price_to');
    range(out, f.kmFrom, f.kmTo, 'kilometer_from', 'kilometer_to');
  } else if (kind === 'estates') {
    if (keyword) out.keyword = keyword;
    const category = f.sub || f.cat;
    if (category) out.category_id = category;
    if (f.inSuburb) out.is_in_hoome = f.inSuburb;
    if (f.userType) out.user_type = f.userType;
    if (f.rooms) out.rooms_count = f.rooms;
    if (f.sellOrBuy) out.sell_or_buy = f.sellOrBuy;
    if (f.sanad) out.sanad_edari = f.sanad;
    range(out, f.priceFrom, f.priceTo, 'price_kharid_from', 'price_kharid_to');
    range(out, f.meterFrom, f.meterTo, 'meters_from', 'meters_to');
    range(out, f.rentFrom, f.rentTo, 'monthly_price_from', 'monthly_price_to');
    range(out, f.depositFrom, f.depositTo, 'vadiee_from', 'vadiee_to');
  } else {
    if (keyword) out.keyword = keyword;
    if (f.type) out.type = f.type;
    if (f.education) out.education_level = f.education;
    if (f.speciality) out.speciality_id = f.speciality;
    if (f.agreement) out.agreement_type = f.agreement;
  }
  return out;
}

/** Car_Search / Home_Search / Job_Search: a keyword box plus a sheet of filters. */
export default function ClassifiedSearchPage() {
  const { kind: kindParam } = useParams();
  const kind: Kind = kindParam === 'estates' || kindParam === 'jobs' ? kindParam : 'vehicles';
  const [params] = useSearchParams();
  const userCityId = useSession((state) => state.cityId);
  const scrollRef = useScrollRestore<HTMLDivElement>();

  const initial: State = {
    keyword: '',
    location: EMPTY_LOCATION,
    fields: (kind === 'estates'
      ? { cat: params.get('cat') ?? '', sub: params.get('sub') ?? '' }
      : { type: params.get('type') ?? '' }) as Filters,
  };
  const [draft, setDraft] = useState<State>(initial);
  const [applied, setApplied] = useState<State>(initial);
  const [filtersOpen, setFiltersOpen] = useState(false);

  const query = buildParams(kind, applied, userCityId);
  const list = usePagedList<Vehicle | Estate | Job>(['classified-search', kind, query], (offset) => {
    if (kind === 'vehicles') return api.searchVehicles(query, offset);
    if (kind === 'estates') return api.searchEstates(query, offset);
    return api.searchJobs(query, offset);
  });

  const apply = (next: State = draft) => {
    (document.activeElement as HTMLElement | null)?.blur();
    setApplied(next);
    setFiltersOpen(false);
  };
  const filterCount = Object.values(applied.fields).filter(Boolean).length + (applied.location.provinceId ? 1 : 0);

  return (
    <Screen bar={<AppBar />} className="bg-[#eee]">
      <div className="z-10 flex shrink-0 items-center gap-1 bg-white px-2 py-1.5 shadow">
        <button type="button" aria-label="جستجو" className="tap-light flex h-11 w-11 shrink-0 items-center justify-center rounded-full text-mild" onClick={() => apply()}>
          <Icon path={mdiMagnify} size={28} />
        </button>
        <input
          type="search"
          value={draft.keyword}
          enterKeyHint="search"
          placeholder="کلمه مورد نظر را وارد کنید"
          aria-label="کلمه مورد نظر را وارد کنید"
          className="h-11 min-w-0 flex-1 border-b border-[#bdbdbd] px-1 text-[15px] focus:border-primary"
          onChange={(event) => setDraft({ ...draft, keyword: event.target.value })}
          onKeyDown={(event) => event.key === 'Enter' && apply()}
        />
        <button
          type="button"
          aria-label="فیلترها"
          className={cx('tap-light relative flex h-11 w-11 shrink-0 items-center justify-center rounded-full', filterCount ? 'text-primary' : 'text-mild')}
          onClick={() => setFiltersOpen(true)}
        >
          <Icon path={mdiFilterVariant} size={26} />
          {filterCount > 0 && <span className="absolute top-1 left-1 flex h-4 min-w-4 items-center justify-center rounded-full bg-primary px-1 text-[10px] text-white">{filterCount}</span>}
        </button>
      </div>

      <div ref={scrollRef} className="min-h-0 flex-1 overflow-y-auto pt-[5px]">
        <PagedList
          list={list}
          empty={<EmptyNote>هیج آگهی برای متن مورد نظر شما ثبت نشده است</EmptyNote>}
          render={(item) =>
            kind === 'vehicles' ? (
              <VehicleCard key={item.id} vehicle={item as Vehicle} />
            ) : kind === 'estates' ? (
              <EstateCard key={item.id} estate={item as Estate} />
            ) : (
              <JobCard key={item.id} job={item as Job} />
            )
          }
        />
      </div>

      {filtersOpen && (
        <Modal full onClose={() => setFiltersOpen(false)}>
          <div className="flex h-14 shrink-0 items-center bg-primary pt-safe pr-4 text-white">
            <span className="flex-1 text-[15px]">{TITLES[kind]}</span>
            <button type="button" aria-label="بستن" className="flex h-14 w-14 items-center justify-center" onClick={() => setFiltersOpen(false)}>
              <Icon path={mdiClose} />
            </button>
          </div>
          <div className="min-h-0 flex-1 overflow-y-auto bg-[#eee] px-1.5 py-2">
            <FilterFields kind={kind} state={draft} onChange={setDraft} />
          </div>
          <div className="flex shrink-0 gap-2 bg-white p-2 pb-safe shadow-[0_-2px_6px_rgb(0_0_0/0.15)]">
            <button type="button" className="tap h-11 flex-1 rounded-[3px] bg-[#00897B] text-white" onClick={() => apply()}>
              اعمال
            </button>
            <button
              type="button"
              className="tap-light h-11 flex-1 rounded-[3px] border border-[#bdbdbd] text-[#555]"
              onClick={() => {
                const cleared: State = { keyword: draft.keyword, fields: {} as Filters, location: EMPTY_LOCATION };
                setDraft(cleared);
                apply(cleared);
              }}
            >
              حذف فیلتر ها
            </button>
          </div>
        </Modal>
      )}
    </Screen>
  );
}

function Group({ title, children }: { title: string; children: ReactNode }) {
  return (
    <section className="mb-2 rounded-[3px] bg-white px-1.5 pt-2 pb-0.5" style={{ boxShadow: 'var(--shadow-card)' }}>
      <h2 className="mb-2 px-1 text-sm font-normal text-[#00796B]">{title}</h2>
      {children}
    </section>
  );
}

function RangeFields({ from, to, onFrom, onTo, label, money, maxLength }: { from: string; to: string; onFrom: (v: string) => void; onTo: (v: string) => void; label: string; money?: boolean; maxLength?: number }) {
  return (
    <div className="flex gap-2">
      <TextField className="flex-1" mode="digits" money={money} maxLength={maxLength} value={from} onChange={onFrom} placeholder={`از ${label}`} />
      <TextField className="flex-1" mode="digits" money={money} maxLength={maxLength} value={to} onChange={onTo} placeholder={`تا ${label}`} />
    </div>
  );
}

const ALL = 'همه';
const named = (rows: Array<{ id: string | number; name: string }> | undefined): Option[] => (rows ?? []).map((row) => ({ value: String(row.id), label: row.name }));

function FilterFields({ kind, state, onChange }: { kind: Kind; state: State; onChange: (state: State) => void }) {
  const f = state.fields;
  const set = (patch: Record<string, string>) => onChange({ ...state, fields: { ...f, ...patch } as Filters });
  const v = (key: string) => f[key] ?? '';

  const brands = useQuery({ queryKey: ['brands'], queryFn: api.getBrands, enabled: kind === 'vehicles', staleTime: Infinity });
  const models = useQuery({ queryKey: ['models', v('brand')], queryFn: () => api.getModels(v('brand')), enabled: kind === 'vehicles' && v('brand') !== '', staleTime: Infinity });
  const cylinders = useQuery({ queryKey: ['cylinders'], queryFn: api.getCylinderVolumes, enabled: kind === 'vehicles', staleTime: Infinity });
  const specialities = useQuery({ queryKey: ['specialities'], queryFn: api.getSpecialities, enabled: kind === 'jobs', staleTime: Infinity });

  const location = (
    <Group title="فیلتر بر اساس موقعیت مکانی">
      <LocationSelects value={state.location} onChange={(value) => onChange({ ...state, location: value })} placeholder={ALL} />
    </Group>
  );

  if (kind === 'vehicles') {
    return (
      <>
        <Group title="مرتب سازی">
          <div className="mb-2.5 flex gap-1.5">
            {[
              { value: 'latest', label: 'جدیدترین ها' },
              { value: 'lowest_price', label: 'ارزانترین ها' },
              { value: 'highest_price', label: 'گرانترین ها' },
            ].map((option) => (
              <button
                key={option.value}
                type="button"
                aria-pressed={v('ordering') === option.value}
                className={cx('h-9 flex-1 rounded-[3px] border text-xs', v('ordering') === option.value ? 'border-primary bg-primary text-white' : 'border-[#bdbdbd] text-[#555]')}
                onClick={() => set({ ordering: v('ordering') === option.value ? '' : option.value })}
              >
                {option.label}
              </button>
            ))}
          </div>
        </Group>
        <Group title="فیلتر بر اساس دسته بندی">
          <SelectField label="نوع دسته بندی" placeholder={ALL} value={v('type')} options={VEHICLE_TYPES} onChange={(type) => set({ type })} />
        </Group>
        <Group title="فیلتر بر اساس مدل و برند">
          <SelectField label="برند خودرو" placeholder={ALL} value={v('brand')} options={named(brands.data)} onChange={(brand) => set({ brand, model: '' })} />
          <SelectField label="مدل خودرو" placeholder={ALL} value={v('model')} disabled={!v('brand')} options={named(models.data)} onChange={(model) => set({ model })} />
        </Group>
        <Group title="فیلتر بر اساس حجم موتور">
          <SelectField label="حجم موتور" placeholder={ALL} value={v('cylinder')} options={(cylinders.data ?? []).map((c) => ({ value: String(c.id), label: c.value }))} onChange={(cylinder) => set({ cylinder })} />
        </Group>
        <Group title="فیلتر بر اساس نوع شاسی">
          <SelectField label="نوع شاسی" placeholder={ALL} value={v('chassis')} options={CHASSIS_TYPES} onChange={(chassis) => set({ chassis })} />
        </Group>
        <Group title="فیلتر بر اساس سال تولید">
          <RangeFields label="سال (4 رقم)" maxLength={4} from={v('yearFrom')} to={v('yearTo')} onFrom={(yearFrom) => set({ yearFrom })} onTo={(yearTo) => set({ yearTo })} />
        </Group>
        <Group title="فیلتر بر اساس قیمت">
          <RangeFields label="قیمت" money from={v('priceFrom')} to={v('priceTo')} onFrom={(priceFrom) => set({ priceFrom })} onTo={(priceTo) => set({ priceTo })} />
        </Group>
        <Group title="فیلتر بر اساس کیلومتر">
          <RangeFields label="کیلومتر" from={v('kmFrom')} to={v('kmTo')} onFrom={(kmFrom) => set({ kmFrom })} onTo={(kmTo) => set({ kmTo })} />
        </Group>
        {location}
      </>
    );
  }

  if (kind === 'estates') {
    const cat = v('cat');
    const sale = cat === '1' || cat === '3';
    const rent = cat === '2' || cat === '4';
    return (
      <>
        <Group title="فیلتر بر اساس دسته بندی">
          <SelectField label="نوع دسته بندی" placeholder={ALL} value={cat} options={ESTATE_CATEGORIES} onChange={(next) => set({ cat: next, sub: '' })} />
          <SelectField label="نوع زیر دسته بندی" placeholder={ALL} value={v('sub')} disabled={!cat} options={ESTATE_SUB_CATEGORIES[cat] ?? []} onChange={(sub) => set({ sub })} />
        </Group>
        <Group title="فیلتر بر اساس حومه شهر">
          <SelectField
            label="حومه شهر"
            placeholder={ALL}
            value={v('inSuburb')}
            options={[
              { value: '0', label: 'نیست' },
              { value: '1', label: 'هست' },
            ]}
            onChange={(inSuburb) => set({ inSuburb })}
          />
        </Group>
        <Group title="فیلتر بر اساس نوع آگهی دهنده">
          <SelectField
            label="نوع آگهی دهنده"
            placeholder={ALL}
            value={v('userType')}
            options={[
              { value: 'person', label: 'شخصی' },
              { value: 'moshaver_amlak', label: 'مشاور املاک' },
            ]}
            onChange={(userType) => set({ userType })}
          />
        </Group>
        <Group title="فیلتر بر تعداد اتاق">
          <SelectField label="تعداد اتاق" placeholder={ALL} value={v('rooms')} options={ROOM_COUNTS} onChange={(rooms) => set({ rooms })} />
        </Group>
        <Group title="فیلتر بر اساس نوع آگهی">
          <SelectField
            label="نوع آگهی"
            placeholder={ALL}
            value={v('sellOrBuy')}
            options={[
              { value: 'sell', label: sale ? 'فروشی' : rent ? 'ارائه' : 'ارائه یا فروشی' },
              { value: 'buy', label: 'درخواستی' },
            ]}
            onChange={(sellOrBuy) => set({ sellOrBuy })}
          />
        </Group>
        {cat === '3' && (
          <Group title="فیلتر بر اساس سند اداری">
            <SelectField
              label="سند اداری"
              placeholder={ALL}
              value={v('sanad')}
              options={[
                { value: '1', label: 'دارد' },
                { value: '0', label: 'ندارد' },
              ]}
              onChange={(sanad) => set({ sanad })}
            />
          </Group>
        )}
        {!rent && (
          <Group title="فیلتر بر اساس قیمت">
            <RangeFields label="قیمت" money from={v('priceFrom')} to={v('priceTo')} onFrom={(priceFrom) => set({ priceFrom })} onTo={(priceTo) => set({ priceTo })} />
          </Group>
        )}
        <Group title="فیلتر بر اساس متراژ">
          <RangeFields label="متراژ (متر مربع)" from={v('meterFrom')} to={v('meterTo')} onFrom={(meterFrom) => set({ meterFrom })} onTo={(meterTo) => set({ meterTo })} />
        </Group>
        {!sale && (
          <>
            <Group title="فیلتر بر اساس اجاره ماهیانه">
              <RangeFields label="اجاره ماهیانه" money from={v('rentFrom')} to={v('rentTo')} onFrom={(rentFrom) => set({ rentFrom })} onTo={(rentTo) => set({ rentTo })} />
            </Group>
            <Group title="فیلتر بر اساس ودیعه">
              <RangeFields label="ودیعه" money from={v('depositFrom')} to={v('depositTo')} onFrom={(depositFrom) => set({ depositFrom })} onTo={(depositTo) => set({ depositTo })} />
            </Group>
          </>
        )}
        {location}
      </>
    );
  }

  return (
    <>
      <Group title="فیلتر بر اساس آماده به کار/استخدام">
        <SelectField label="آماده به کار/استخدام" placeholder={ALL} value={v('type')} options={JOB_TYPES} onChange={(type) => set({ type })} />
      </Group>
      <Group title="فیلتر بر اساس میزان تحصیلات">
        <SelectField label="میزان تحصیلات" placeholder={ALL} value={v('education')} options={EDUCATION_LEVELS} onChange={(education) => set({ education })} />
      </Group>
      <Group title="فیلتر بر اساس نوع تخصص">
        <SelectField label="نوع تخصص" placeholder={ALL} value={v('speciality')} options={named(specialities.data)} onChange={(speciality) => set({ speciality })} />
      </Group>
      <Group title="فیلتر بر اساس نوع قرارداد">
        <SelectField label="نوع قرارداد" placeholder={ALL} value={v('agreement')} options={AGREEMENT_TYPES} onChange={(agreement) => set({ agreement })} />
      </Group>
      {location}
    </>
  );
}
