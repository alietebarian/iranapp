import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router';
import { keepPreviousData, useQuery } from '@tanstack/react-query';
import { mdiArrowRight, mdiClose, mdiCrosshairsGps } from '@mdi/js';
import { Photo } from '@/components/cards';
import { SelectField } from '@/components/fields';
import { Icon } from '@/components/Icon';
import { FlyTo, MarkersMap, MyLocationDot } from '@/components/Map';
import { parseLatLng, type LatLng } from '@/lib/geo';
import { LinearProgress, Spinner } from '@/components/ui';
import { MAP_DEFAULT_CENTER } from '@/config';
import * as api from '@/lib/api';
import { locate } from '@/lib/actions';
import { stash } from '@/lib/hooks';
import type { Ad } from '@/lib/types';
import { toast } from '@/store/ui';

/** AroundMeActivity («اطراف من»): business ads within two kilometres of the map's centre. */
export default function AroundMePage() {
  const navigate = useNavigate();
  const [me, setMe] = useState<LatLng | null>(null);
  const [locating, setLocating] = useState(true);
  const [center, setCenter] = useState<LatLng | null>(null);
  const [flyTo, setFlyTo] = useState<LatLng | null>(null);
  const [categoryId, setCategoryId] = useState('');
  const [subCategoryId, setSubCategoryId] = useState('');
  const [selected, setSelected] = useState<Ad | null>(null);

  const findMe = () => {
    setLocating(true);
    locate()
      .then(({ latitude, longitude }) => {
        const position: LatLng = [latitude, longitude];
        setMe(position);
        setCenter(position);
        setFlyTo([...position]);
      })
      .catch((error: Error) => {
        toast.failure(error.message);
        setCenter((current) => current ?? MAP_DEFAULT_CENTER);
      })
      .finally(() => setLocating(false));
  };
  useEffect(findMe, []);

  const categories = useQuery({ queryKey: ['categories'], queryFn: api.getCategories, staleTime: Infinity });
  const subCategories = useQuery({
    queryKey: ['subcategories-all', categoryId],
    queryFn: () => api.getAllSubCategories(categoryId),
    enabled: categoryId !== '',
    staleTime: Infinity,
  });

  // Round the centre so small drags reuse the last answer instead of asking the server again.
  const rounded = center ? ([Number(center[0].toFixed(3)), Number(center[1].toFixed(3))] as LatLng) : null;
  const ads = useQuery({
    queryKey: ['nearest-ads', rounded, categoryId, subCategoryId],
    queryFn: () => api.getNearestAds(rounded![0], rounded![1], categoryId, subCategoryId),
    enabled: rounded !== null,
    placeholderData: keepPreviousData,
  });

  const markers = useMemo(
    () =>
      (ads.data ?? []).flatMap((ad) => {
        const position = parseLatLng(ad.latitude, ad.longitude);
        return position ? [{ id: ad.id, position, onClick: () => setSelected(ad) }] : [];
      }),
    [ads.data],
  );
  const nothingFound = ads.isSuccess && !ads.isFetching && markers.length === 0;
  const lookingAtMe = me && rounded && Math.abs(me[0] - rounded[0]) < 0.002 && Math.abs(me[1] - rounded[1]) < 0.002;

  return (
    <div className="animate-page-in relative h-full">
      {center ? (
        <MarkersMap center={center} markers={markers} onMoveEnd={setCenter} className="h-full w-full">
          {me && <MyLocationDot position={me} />}
          <FlyTo position={flyTo} />
        </MarkersMap>
      ) : (
        <div className="flex h-full items-center justify-center bg-[#e5e3df]">
          <Spinner />
        </div>
      )}

      <div className="absolute inset-x-0 top-0 z-[1000] bg-white pt-safe shadow-md">
        <div className="flex items-center gap-1 p-[5px]">
          <button type="button" aria-label="بازگشت" className="tap-light flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-primary" onClick={() => navigate(-1)}>
            <Icon path={mdiArrowRight} />
          </button>
          <SelectField
            bare
            className="min-w-0 flex-1"
            label="دسته:"
            placeholder="انتخاب کنید"
            value={categoryId}
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
            disabled={!categoryId}
            options={(subCategories.data ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
            onChange={setSubCategoryId}
          />
        </div>
        {ads.isFetching || subCategories.isFetching || locating ? <LinearProgress className="!h-0.5" color="var(--color-primary)" /> : <div className="h-0.5" />}
      </div>

      <button
        type="button"
        aria-label="موقعیت من"
        className="tap absolute bottom-24 left-3 z-[1000] flex h-12 w-12 items-center justify-center rounded-full bg-white text-primary shadow-lg"
        onClick={findMe}
      >
        <Icon path={mdiCrosshairsGps} />
      </button>

      {nothingFound && !selected && (
        <div className="absolute inset-x-2.5 bottom-[50px] z-[1000] rounded-[3px] bg-[#FF9800]/85 p-2 text-center text-sm text-white shadow">
          {lookingAtMe ? 'در نزدیکی شما آگهی وجود ندارد. شما میتوانید با تکان دادن نقشه اگهی های دورتر را ببینید.' : 'در منطقه ای که میبینید آگهی وجود ندارد.'}
        </div>
      )}

      {selected && (
        <div className="animate-slide-up absolute inset-x-2.5 bottom-4 z-[1000] flex items-center rounded-md border border-primary bg-white shadow-xl pb-safe">
          <button type="button" aria-label="بستن" className="flex h-[70px] w-10 shrink-0 items-center justify-center text-mild" onClick={() => setSelected(null)}>
            <Icon path={mdiClose} size={20} />
          </button>
          <button
            type="button"
            className="flex min-w-0 flex-1 items-center text-right"
            onClick={() => {
              stash('ad', selected.id, selected);
              navigate(`/ad/${selected.id}`);
            }}
          >
            <span className="line-clamp-2 min-w-0 flex-1 px-2.5 text-center text-base">{selected.title}</span>
            <Photo src={selected.photos?.[0]?.file_name} className="h-[70px] w-[70px] shrink-0 rounded-md" />
          </button>
        </div>
      )}
    </div>
  );
}
