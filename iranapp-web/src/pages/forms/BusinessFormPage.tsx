import { useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { FormSection, SelectField, TextArea, TextField } from '@/components/fields';
import { BusinessPhotos, emptySlots, FormScreen, MapSection, SubmitBar, TermsRow, UploadingDialog, useInitialSlots, type PhotoSlot } from '@/components/formParts';
import { LocationSelects, type LocationValue } from '@/components/LocationSelects';
import { parseLatLng, type LatLng } from '@/lib/geo';
import { EmptyNote } from '@/components/ui';
import { APP_VERSION, DEFAULT_AD_PLAN_ID, TERMS_VERSION } from '@/config';
import * as api from '@/lib/api';
import { isValidEmail, text } from '@/lib/format';
import { unstash } from '@/lib/hooks';
import type { Ad } from '@/lib/types';
import { toast } from '@/store/ui';

const SLOT_COUNT = 10;

/** NewAdActivity: post or edit a business ad («آگهی کسب و کار»). */
export default function BusinessFormPage() {
  const { id } = useParams();
  const ad = id ? unstash<Ad>('ad', id) : null;
  if (id && !ad) {
    return (
      <FormScreen>
        <EmptyNote>این آگهی در دسترس نیست. لطفا آن را دوباره از محیط کاربری باز کنید.</EmptyNote>
      </FormScreen>
    );
  }
  return <BusinessForm key={id ?? 'new'} ad={ad} />;
}

function BusinessForm({ ad }: { ad: Ad | null }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const isNew = ad === null;

  const [title, setTitle] = useState(ad?.title ?? '');
  const [categoryId, setCategoryId] = useState(ad?.category_id ? String(ad.category_id) : '');
  const [subCategoryId, setSubCategoryId] = useState(ad ? String(ad.sub_category_id) : '');
  const [notes, setNotes] = useState(text(ad?.notes));
  const [workingTime, setWorkingTime] = useState(text(ad?.working_time));
  const [ownerName, setOwnerName] = useState(text(ad?.ads_owner_name));
  const [discount, setDiscount] = useState(ad ? (text(ad.discount).replace(/\D/g, '').slice(0, 2) || '0') : '');
  const [mobile, setMobile] = useState(text(ad?.mobile));
  const [tel1, setTel1] = useState(text(ad?.tel1));
  const [tel2, setTel2] = useState(text(ad?.tel2));
  const [link, setLink] = useState(text(ad?.link));
  const [telegram, setTelegram] = useState(text(ad?.telegram));
  const [instagram, setInstagram] = useState(text(ad?.instagram));
  const [email, setEmail] = useState(text(ad?.email));
  const [location, setLocation] = useState<LocationValue>({ provinceId: ad?.province_id ? String(ad.province_id) : '', cityId: ad ? String(ad.city_id) : '', regionId: '' });
  const [address, setAddress] = useState(text(ad?.address));
  const [pin, setPin] = useState<LatLng | null>(parseLatLng(ad?.latitude, ad?.longitude));
  const [slots, setSlots] = useInitialSlots(() => {
    const initial: PhotoSlot[] = emptySlots(SLOT_COUNT);
    (ad?.photos ?? []).slice(0, SLOT_COUNT).forEach((photo, index) => {
      initial[index] = { original: { id: String(photo.id), url: photo.file_name } };
    });
    return initial;
  });
  const [accepted, setAccepted] = useState(false);
  const [tried, setTried] = useState(false);
  const [uploading, setUploading] = useState(false);

  const categories = useQuery({ queryKey: ['categories'], queryFn: api.getCategories, staleTime: Infinity });
  const subCategories = useQuery({
    queryKey: ['subcategories-all', categoryId],
    queryFn: () => api.getAllSubCategories(categoryId),
    enabled: categoryId !== '',
    staleTime: Infinity,
  });
  const plans = useQuery({ queryKey: ['plans'], queryFn: api.getPlans, staleTime: Infinity });
  // Plan 17 (free) is the default; if the server no longer lists it, use the first plan.
  const planId = ad ? String(ad.ads_plan_id) : DEFAULT_AD_PLAN_ID;
  const plan = plans.data?.find((p) => String(p.id) === planId) ?? plans.data?.[0];
  const maxPhotos = Math.min(SLOT_COUNT, Number(plan?.max_number_of_photos ?? 0));

  const errors = {
    title: title.trim().length === 0,
    category: categoryId === '',
    subCategory: subCategoryId === '',
    notes: notes.trim().length === 0,
    owner: ownerName.trim().length === 0,
    discount: discount.trim().length === 0,
    email: email.length !== 0 && !isValidEmail(email),
    province: location.provinceId === '',
    city: location.cityId === '',
    address: address.trim().length === 0,
    map: pin === null,
  };
  const hasErrors = Object.values(errors).some(Boolean);

  const submit = async () => {
    setTried(true);
    if (isNew && !accepted) toast.failure('برای ثبت آگهی باید با قوانین و مقررات موافقت کنید');
    if (hasErrors || (isNew && !accepted)) return;

    const form = new FormData();
    form.append('terms_accepted', isNew ? '1' : '0');
    form.append('terms_version', TERMS_VERSION);
    form.append('app_version', APP_VERSION);
    form.append('title', title.trim());
    form.append('city_id', location.cityId);
    form.append('sub_category_id', subCategoryId);
    form.append('ads_plan_id', DEFAULT_AD_PLAN_ID);
    form.append('mobile', mobile.trim());
    form.append('notes', notes.trim());
    form.append('ads_owner_name', ownerName.trim());
    if (Number.parseInt(discount, 10) > 0) {
      form.append('discount', discount.trim());
      form.append('type', 'discount');
    } else {
      form.append('type', 'need');
    }
    if (pin) {
      form.append('latitude', String(pin[0]));
      form.append('longitude', String(pin[1]));
    }
    const optional: Record<string, string> = { address, tel1, tel2, working_time: workingTime, link, telegram, instagram, email };
    for (const [key, value] of Object.entries(optional)) if (value.trim()) form.append(key, value.trim());

    for (const slot of slots.slice(0, isNew ? maxPhotos : SLOT_COUNT)) {
      if (isNew) {
        if (slot.file) form.append('photos[]', slot.file, slot.file.name);
      } else if (slot.original && slot.file) {
        form.append('photos_to_edit_id[]', slot.original.id);
        form.append('photo_to_edit[]', slot.file, slot.file.name);
      } else if (slot.original && slot.removed) {
        form.append('ids_to_delete[]', slot.original.id);
      } else if (slot.file) {
        form.append('new_photos[]', slot.file, slot.file.name);
      }
    }

    setUploading(true);
    try {
      const result = ad ? await api.updateAd(ad.id, form) : await api.saveAd(form);
      const status = String(result.status);
      if (status === '200') {
        toast.success(isNew ? 'آگهی شما بدرستی ذخیره گردید و پس از تایید مدیر به نمایش در می آید.' : 'تغییرات در آگهی بدرستی ذخیره گردید و پس از تایید مدیر به نمایش در می آید.');
        await queryClient.invalidateQueries({ queryKey: ['mine'] });
        if (ad) queryClient.removeQueries({ queryKey: ['ad', String(ad.id)] });
        // After an edit go back past the ad's page (it shows the old data), like the app does.
        if (isNew) navigate('/dashboard', { replace: true });
        else navigate(-2);
      } else if (result.error === 'maximum_limit_reached') {
        toast.failure('شما به حد اکثر تعداد تغییر در آگهی رسیدید');
      } else if (result.error !== 'token_expired' && result.error !== 'token_invalid') {
        toast.failure(result.message || (isNew ? 'در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.' : 'در ثبت تغییرات به مشکل برخوردیم لطفا مجددا تلاش فرمایید.'));
      }
    } catch {
      toast.failure(isNew ? 'در ثبت آگهی به مشکل برخوردیم لطفا مجددا تلاش فرمایید.' : 'در ثبت تغییرات به مشکل برخوردیم لطفا مجددا تلاش فرمایید.');
    } finally {
      setUploading(false);
    }
  };

  const warn = (flag: boolean, message: string) => tried && flag && message;

  return (
    <FormScreen loading={categories.isLoading || subCategories.isFetching || plans.isLoading}>
      <FormSection title="مشخصات آگهی">
        <TextField value={title} onChange={setTitle} placeholder="عنوان (اجباری)" warn={warn(errors.title, 'لطفا عنوان آگهی خود را وارد کنید')} />
        <SelectField
          label="دسته ها (اجباری)"
          placeholder="انتخاب کنید"
          value={categoryId}
          disabled={!categories.data}
          options={(categories.data ?? []).map((c) => ({ value: String(c.id), label: c.name }))}
          warn={warn(errors.category, 'لطفا دسته بندی آگهی خود را انتخاب کنید')}
          onChange={(value) => {
            setCategoryId(value);
            setSubCategoryId('');
          }}
        />
        <SelectField
          label="زیر دسته ها (اجباری)"
          placeholder="انتخاب کنید"
          value={subCategoryId}
          disabled={!categoryId || !subCategories.data}
          options={(subCategories.data ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
          warn={warn(errors.subCategory, 'لطفا دسته بندی آگهی خود را انتخاب کنید')}
          onChange={setSubCategoryId}
        />
        <TextArea value={notes} onChange={setNotes} height={150} placeholder="متن آگهی (اجباری)" warn={warn(errors.notes, 'لطفا متن آگهی خود را وارد کنید')} />
        <TextField value={workingTime} onChange={setWorkingTime} placeholder="ساعات کاری (اختیاری)" />
        <TextField value={ownerName} onChange={setOwnerName} placeholder="نام مدیر آگهی (اجباری)" warn={warn(errors.owner, 'لطفا نام مدیر را وارد کنید')} />
        <TextField value={discount} onChange={setDiscount} mode="digits" maxLength={2} placeholder="درصد تخفیف (اجباری)" warn={warn(errors.discount, 'لطفا درصد تخفیف را وارد نمایید.')} />
      </FormSection>

      <FormSection title="اطلاعات تکمیلی">
        <TextField value={mobile} onChange={setMobile} mode="digits" maxLength={11} placeholder="تلفن همراه (اختیاری)" />
        <TextField value={tel1} onChange={setTel1} mode="digits" maxLength={11} placeholder="تلفن ثابت (اختیاری)" />
        <TextField value={tel2} onChange={setTel2} mode="digits" maxLength={11} placeholder="تلفن ثابت دوم (اختیاری)" />
        <TextField value={link} onChange={setLink} mode="url" placeholder="لینک مرتبط (اختیاری)" />
        <TextField value={telegram} onChange={setTelegram} mode="url" placeholder="آدرس تلگرام (اختیاری)" />
        <TextField value={instagram} onChange={setInstagram} mode="url" placeholder="آدرس اینستاگرام (اختیاری)" />
        <TextField value={email} onChange={setEmail} mode="email" placeholder="آدرس ایمیل (اختیاری)" warn={warn(errors.email, 'لطفا ایمیل خود رابصورت صحیح وارد نمایید')} />
        {tried && (errors.province || errors.city) && <div className="mb-0.5 px-1 text-left text-[11px] text-warn">لطفا استان و شهر خود را انتخاب کنید</div>}
        <LocationSelects value={location} onChange={setLocation} withRegion={false} placeholder="انتخاب کنید" suffix=" (اجباری)" />
        <TextArea value={address} onChange={setAddress} height={80} placeholder="آدرس (اجباری)" warn={warn(errors.address, 'لطفاآدرس خود را وارد کنید')} />
      </FormSection>

      <MapSection value={pin} onChange={setPin} label="نشان کردن روی نقشه اجباری" error={tried && errors.map} />

      <FormSection title="تصاویر">
        <BusinessPhotos slots={slots} onChange={setSlots} max={maxPhotos} />
      </FormSection>

      {tried && hasErrors && <p className="px-2.5 text-center text-sm text-primary">در اطلاعات آگهی شما اشکالاتی وجود دارد. لطفا آنها را رفع کنید.</p>}
      {isNew && <TermsRow checked={accepted} onChange={setAccepted} />}
      <SubmitBar label="ثبت آگهی" onClick={() => void submit()} />
      {uploading && <UploadingDialog />}
    </FormScreen>
  );
}
