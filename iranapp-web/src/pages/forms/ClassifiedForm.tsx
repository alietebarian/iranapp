import { useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router';
import { useQueryClient } from '@tanstack/react-query';
import { FormSection, TextArea, TextField } from '@/components/fields';
import { ClassifiedPhotos, emptySlots, FormScreen, MapSection, SubmitBar, TermsRow, UploadingDialog, useInitialSlots, type PhotoSlot } from '@/components/formParts';
import { LocationSelects, type LocationValue } from '@/components/LocationSelects';
import { parseLatLng, type LatLng } from '@/lib/geo';
import { APP_VERSION, TERMS_VERSION } from '@/config';
import { blank, text } from '@/lib/format';
import type { Estate, Job, Vehicle } from '@/lib/types';
import { toast } from '@/store/ui';

type Item = Vehicle | Estate | Job;

interface Props {
  /** The ad being edited, or null for a new one. */
  item: Item | null;
  /** Hints of the two text boxes differ a little between the three forms. */
  titleHint: string;
  descriptionHint: string;
  photosTitle: string;
  mapLabel: string;
  /** Field names the three update endpoints use for removing stored pictures. */
  deletePhotoKey: string;
  deleteThumbnailKey: string;
  loading?: boolean;
  /** The form's own block («اطلاعات تکمیلی»). */
  children: ReactNode;
  /** Checks the form's own fields; returns the message to show or null. */
  validate: () => string | null;
  /** Adds the form's own fields to the request. */
  append: (form: FormData) => void;
  send: (form: FormData) => Promise<{ status: number | string; error?: string; message?: string }>;
}

/**
 * What Add_New_Car_Add, Add_New_Home_Add and Add_New_Job (and their Edit twins) have in common:
 * five pictures, title and description, the advertiser's name and phones, the location with an
 * optional pin on the map, the terms tick and the upload itself.
 */
export function ClassifiedForm({ item, titleHint, descriptionHint, photosTitle, mapLabel, deletePhotoKey, deleteThumbnailKey, loading, children, validate, append, send }: Props) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const isNew = item === null;

  const [slots, setSlots] = useInitialSlots(() => {
    const initial: PhotoSlot[] = emptySlots(5);
    if (item && !blank(item.thumbnail_photo)) initial[0] = { original: { id: 'thumbnail', url: String(item.thumbnail_photo) } };
    (item?.photos ?? [])
      .filter((photo) => photo.file_name !== item?.thumbnail_photo)
      .slice(0, 4)
      .forEach((photo, index) => {
        initial[index + 1] = { original: { id: String(photo.id), url: photo.file_name } };
      });
    return initial;
  });
  const [title, setTitle] = useState(item?.ads_title ?? '');
  const [description, setDescription] = useState(text(item?.description));
  const [name, setName] = useState(text(item?.ads_owner_name));
  const [phone1, setPhone1] = useState(text(item?.telephone1));
  const [phone2, setPhone2] = useState(text(item?.telephone2));
  const [location, setLocation] = useState<LocationValue>({
    provinceId: text(item?.province_id),
    cityId: text(item?.city_id),
    regionId: text(item?.region_id),
  });
  const [address, setAddress] = useState(text(item?.address));
  const [pin, setPin] = useState<LatLng | null>(parseLatLng(item?.latitude, item?.longitude));
  const [accepted, setAccepted] = useState(false);
  const [uploading, setUploading] = useState(false);

  /** The first problem, in the order the app checks them. */
  const firstError = (): string | null => {
    if (title.trim().length === 0) return 'لطفا در وارد کردن عنوان آگهی دقت فرمایید';
    if (description.trim().length === 0) return 'لطفا در وارد نمودن توضیحات آگهی دقت نمایید.';
    const own = validate();
    if (own) return own;
    if (name.trim().length === 0) return 'لطفا در وارد کردن نام و نام خانوادگی دقت فرمایید';
    if (phone1.length !== 11) return 'لطفا در وارد کردن شماره تلفن 1 دقت فرمایید';
    if (phone2.length !== 0 && phone2.length !== 11) return 'لطفا در وارد کردن شماره تلفن 2 دقت فرمایید';
    if (!location.provinceId) return 'لطفا در انتخاب استان دقت فرمایید';
    if (!location.cityId) return 'لطفا در انتخاب شهر دقت فرمایید';
    if (!location.regionId) return 'لطفا در انتخاب منطقه دقت فرمایید';
    if (address.trim().length === 0) return 'لطفا در وارد نمودن آدرس خود دقت فرمایید';
    if (isNew && !accepted) return 'برای ثبت آگهی باید با قوانین و مقررات موافقت کنید';
    return null;
  };

  const submit = async () => {
    const error = firstError();
    if (error) return toast.failure(error);

    const form = new FormData();
    // Edits keep the consent given when the ad was posted.
    form.append('terms_accepted', '1');
    form.append('terms_version', TERMS_VERSION);
    form.append('app_version', APP_VERSION);
    form.append('ads_title', title.trim());
    form.append('description', description.trim());
    form.append('region_id', location.regionId);
    form.append('address', address.trim());
    form.append('telephone1', phone1);
    if (phone2) form.append('telephone2', phone2);
    form.append('ads_owner_name', name.trim());
    if (pin) {
      form.append('latitude', String(pin[0]));
      form.append('longitude', String(pin[1]));
    }
    append(form);

    const [main, ...others] = slots;
    if (main.file) form.append('thumbnail_photo', main.file, main.file.name);
    else if (main.original && main.removed) form.append(deleteThumbnailKey, '1');
    for (const slot of others) {
      if (slot.file) form.append('photos[]', slot.file, slot.file.name);
      // A replaced or removed stored picture has to be deleted on the server.
      if (slot.original && (slot.file || slot.removed)) form.append(`${deletePhotoKey}[]`, slot.original.id);
    }

    setUploading(true);
    try {
      const result = await send(form);
      if (String(result.status) === '200') {
        toast.success(isNew ? 'آگهی شما با موفقیت ثبت گردید و پس از تایید مدیر بنمایش گذاشته می شود' : 'تغییرات آگهی با موفقیت ثبت گردید و پس از تایید مدیر بنمایش گذاشته می شود');
        await queryClient.invalidateQueries({ queryKey: ['mine'] });
        navigate('/dashboard', { replace: true });
      } else if (result.error !== 'token_expired' && result.error !== 'token_invalid') {
        toast.failure(result.message || 'در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.');
      }
    } catch {
      toast.failure('در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.');
    } finally {
      setUploading(false);
    }
  };

  return (
    <FormScreen loading={loading}>
      <FormSection title={photosTitle}>
        <ClassifiedPhotos slots={slots} onChange={setSlots} />
      </FormSection>

      <FormSection title="مشخصات آگهی">
        <TextField value={title} onChange={setTitle} maxLength={200} placeholder={titleHint} />
        <TextArea value={description} onChange={setDescription} placeholder={descriptionHint} />
      </FormSection>

      <FormSection title="اطلاعات تکمیلی">{children}</FormSection>

      <FormSection title="اطلاعات شخصی">
        <TextField value={name} onChange={setName} maxLength={200} placeholder="نام و نام خانوادگی" autoComplete="name" />
        <TextField value={phone1} onChange={setPhone1} mode="digits" maxLength={11} placeholder="تلفن تماس 1 (11 رقم)" autoComplete="tel" />
        <TextField value={phone2} onChange={setPhone2} mode="digits" maxLength={11} placeholder="تلفن تماس 2 (11 رقم - اختیاری)" />
      </FormSection>

      <FormSection title="اطلاعات موقعیت">
        <LocationSelects value={location} onChange={setLocation} />
        <div className="mb-1 px-1 text-xs text-mild">آدرس شما در برنامه نمایش داده نمی شود</div>
        <TextArea value={address} onChange={setAddress} height={80} placeholder="آدرس" />
      </FormSection>

      <MapSection value={pin} onChange={setPin} label={mapLabel} />

      {isNew && <TermsRow checked={accepted} onChange={setAccepted} />}
      <SubmitBar label="ثبت آگهی" onClick={() => void submit()} />
      {uploading && <UploadingDialog />}
    </FormScreen>
  );
}
