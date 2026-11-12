import { lazy, Suspense, useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { mdiClose, mdiLock, mdiPlus } from '@mdi/js';
import { MAP_DEFAULT_CENTER } from '@/config';
import { locate } from '@/lib/actions';
import { cx } from '@/lib/format';
import { compressImage, mediaUrl } from '@/lib/media';
import { openRules, toast } from '@/store/ui';
import { Checkbox } from './fields';
import { Icon } from './Icon';
import type { LatLng } from '@/lib/geo';
import { AppBar, Button, LinearProgress, Modal, Screen, Spinner } from './ui';

const MapPicker = lazy(() => import('./Map').then((module) => ({ default: module.MapPicker })));

// ---------------------------------------------------------------- photos

/** One photo box of an ad form: what the ad already has on the server and what the user picked now. */
export interface PhotoSlot {
  original?: { id: string; url: string };
  /** A newly picked (already shrunk) file, replacing the original if there is one. */
  file?: File;
  preview?: string;
  /** The original was removed without a replacement. */
  removed?: boolean;
}

export function emptySlots(count: number): PhotoSlot[] {
  return Array.from({ length: count }, () => ({}));
}

export const slotHasImage = (slot: PhotoSlot) => Boolean(slot.file || (slot.original && !slot.removed));

function PhotoBox({ slot, locked, big, onPick, onClear }: { slot: PhotoSlot; locked?: boolean; big?: boolean; onPick: (file: File) => void; onClear: () => void }) {
  const input = useRef<HTMLInputElement>(null);
  const [busy, setBusy] = useState(false);
  const image = slot.preview ?? (slot.original && !slot.removed ? mediaUrl(slot.original.url) : undefined);

  return (
    <div className={cx('relative overflow-hidden rounded-[3px]', big ? 'h-full w-full' : 'aspect-square w-full')} style={{ background: locked ? '#ef9a9a' : '#efefef', boxShadow: 'var(--shadow-card)' }}>
      <button
        type="button"
        disabled={locked || busy}
        aria-label={image ? 'تغییر تصویر' : 'افزودن تصویر'}
        className="flex h-full w-full items-center justify-center"
        onClick={() => input.current?.click()}
      >
        {busy ? (
          <Spinner size={28} />
        ) : image ? (
          <img src={image} alt="" className="h-full w-full object-cover" />
        ) : (
          <Icon path={locked ? mdiLock : mdiPlus} size={30} style={{ color: locked ? '#F4511E' : '#4CAF50' }} />
        )}
      </button>
      {image && !busy && (
        <button type="button" aria-label="حذف تصویر" className="absolute top-1 left-1 flex h-7 w-7 items-center justify-center rounded-full bg-black/55 text-white" onClick={onClear}>
          <Icon path={mdiClose} size={18} />
        </button>
      )}
      <input
        ref={input}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={async (event) => {
          const file = event.target.files?.[0];
          event.target.value = '';
          if (!file) return;
          setBusy(true);
          try {
            onPick(await compressImage(file));
          } finally {
            setBusy(false);
          }
        }}
      />
    </div>
  );
}

/** Slot updates shared by both photo layouts; object URLs of replaced previews are released. */
function useSlotActions(slots: PhotoSlot[], onChange: (slots: PhotoSlot[]) => void) {
  const pick = (index: number, file: File) => {
    const old = slots[index];
    if (old.preview) URL.revokeObjectURL(old.preview);
    onChange(slots.map((slot, i) => (i === index ? { original: slot.original, file, preview: URL.createObjectURL(file) } : slot)));
  };
  const clear = (index: number) => {
    const old = slots[index];
    if (old.preview) URL.revokeObjectURL(old.preview);
    onChange(slots.map((slot, i) => (i === index ? { original: slot.original, removed: Boolean(slot.original) } : slot)));
  };
  return { pick, clear };
}

/** Vehicle / estate / job forms: one large main picture and four smaller ones beside it. */
export function ClassifiedPhotos({ slots, onChange }: { slots: PhotoSlot[]; onChange: (slots: PhotoSlot[]) => void }) {
  const { pick, clear } = useSlotActions(slots, onChange);
  return (
    <div className="flex gap-1.5 p-1" dir="rtl">
      <div className="aspect-square flex-1">
        <PhotoBox big slot={slots[0]} onPick={(file) => pick(0, file)} onClear={() => clear(0)} />
      </div>
      <div className="grid flex-1 grid-cols-2 gap-1.5">
        {[1, 2, 3, 4].map((index) => (
          <PhotoBox key={index} slot={slots[index]} onPick={(file) => pick(index, file)} onClear={() => clear(index)} />
        ))}
      </div>
    </div>
  );
}

/** Business form: two large and eight small boxes; the plan decides how many are unlocked. */
export function BusinessPhotos({ slots, onChange, max }: { slots: PhotoSlot[]; onChange: (slots: PhotoSlot[]) => void; max: number }) {
  const { pick, clear } = useSlotActions(slots, onChange);
  const box = (index: number) => <PhotoBox key={index} slot={slots[index]} locked={index >= max} onPick={(file) => pick(index, file)} onClear={() => clear(index)} />;
  return (
    <div className="p-1">
      <div className="grid grid-cols-2 gap-1.5">{[0, 1].map(box)}</div>
      <div className="mt-2.5 grid grid-cols-4 gap-1.5">{[2, 3, 4, 5, 6, 7, 8, 9].map(box)}</div>
    </div>
  );
}

// ---------------------------------------------------------------- map

/** «نشان کردن روی نقشه»: a tick that opens a map; tapping the map drops the ad's pin. */
export function MapSection({ value, onChange, label, error }: { value: LatLng | null; onChange: (value: LatLng | null) => void; label: string; error?: boolean }) {
  const [open, setOpen] = useState(value !== null);
  const [center, setCenter] = useState<LatLng | null>(null);

  useEffect(() => {
    if (!open || value || center) return;
    // Start the map at the user's own position when they allow it.
    locate()
      .then(({ latitude, longitude }) => setCenter([latitude, longitude]))
      .catch(() => setCenter(MAP_DEFAULT_CENTER));
  }, [open, value, center]);

  return (
    <>
      {error && <div className="mx-4 mb-1 text-left text-[11px] text-primary">لطفا مکان خود روی نقشه نشان دهید</div>}
      <section className="mx-[5px] mb-4 overflow-hidden rounded-lg bg-white" style={{ boxShadow: 'var(--shadow-card)' }}>
        <Checkbox checked={open} onChange={setOpen} color="#fff" className="h-[35px] w-full bg-primary px-2.5 text-sm text-white [&_svg]:!text-white">
          {label}
        </Checkbox>
        {open && (
          <>
            <Suspense
              fallback={
                <div className="flex items-center justify-center h-sdp-250">
                  <Spinner />
                </div>
              }
            >
              <MapPicker value={value} center={center} onChange={onChange} className="w-full h-sdp-250" />
            </Suspense>
            {value ? (
              <button
                type="button"
                className="tap-light flex h-[35px] w-full items-center justify-center text-sm text-[#444]"
                onClick={() => {
                  onChange(null);
                  toast.success('نشانه با موفقیت حذف گردید.');
                }}
              >
                حذف نشان روی نقشه
              </button>
            ) : (
              <div className="py-1.5 text-center text-xs text-mild">برای نشان کردن، روی محل مورد نظر در نقشه بزنید</div>
            )}
          </>
        )}
      </section>
    </>
  );
}

// ---------------------------------------------------------------- frame, terms, submit

export function FormScreen({ loading, children }: { loading?: boolean; children: ReactNode }) {
  return (
    <Screen bar={<AppBar line={false} />} className="bg-[#eee]">
      {loading ? <LinearProgress color="var(--color-primary)" /> : <div className="h-1 shrink-0" />}
      <div className="min-h-0 flex-1 overflow-y-auto pt-1">{children}</div>
    </Screen>
  );
}

export function TermsRow({ checked, onChange }: { checked: boolean; onChange: (checked: boolean) => void }) {
  return (
    <div className="flex flex-wrap items-center justify-center gap-1 py-2.5 text-[15px] text-[#444]">
      <Checkbox checked={checked} onChange={onChange}>
        با تمامی قوانین و مقررات موافقم
      </Checkbox>
      <button type="button" className="px-1.5 text-[#03A9F4]" onClick={openRules}>
        (مشاهده قوانین)
      </button>
    </div>
  );
}

export function SubmitBar({ label, onClick }: { label: string; onClick: () => void }) {
  return (
    <div className="mx-5 mt-2.5 mb-[60px]">
      <Button tone="teal" height={60} className="rounded-lg shadow-md" onClick={onClick}>
        {label}
      </Button>
    </div>
  );
}

/** ProgressDialog «درحال بارگذاری اطلاعات...» shown while the form and its photos upload. */
export function UploadingDialog() {
  return (
    <Modal className="w-[78%]">
      <div className="flex items-center gap-4 p-5 text-[15px]">
        <Spinner size={34} color="var(--color-primary)" />
        درحال بارگذاری اطلاعات...
      </div>
    </Modal>
  );
}

/** Builds the slots of an edit form from the ad's stored photos. */
export function useInitialSlots(build: () => PhotoSlot[]): [PhotoSlot[], (slots: PhotoSlot[]) => void] {
  const initial = useMemo(build, []); // eslint-disable-line react-hooks/exhaustive-deps
  return useState<PhotoSlot[]>(initial);
}
