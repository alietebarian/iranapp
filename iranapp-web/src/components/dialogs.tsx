import { useEffect, useMemo, useState, type ReactNode } from 'react';
import { useNavigate } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import {
  mdiCheck,
  mdiClockOutline,
  mdiClose,
  mdiCrosshairsGps,
  mdiEmail,
  mdiHeadset,
  mdiInformation,
  mdiInstagram,
  mdiKeyVariant,
  mdiMap,
  mdiMapMarker,
  mdiPhone,
  mdiPhoneClassic,
  mdiCellphone,
  mdiSend,
  mdiWeb,
} from '@mdi/js';
import { SMS_NUMBER } from '@/config';
import { LINK_TO_US_HTML, RULES_HTML } from '@/content/texts';
import * as api from '@/lib/api';
import { dial, openExternal } from '@/lib/actions';
import { categoryIcon, subCategoryIcon } from '@/lib/categoryIcons';
import { blank, cx, text, withScheme } from '@/lib/format';
import { stash } from '@/lib/hooks';
import * as jalali from '@/lib/jalali';
import { iconUrl, mediaUrl } from '@/lib/media';
import { enablePush, pushSupport } from '@/lib/push';
import type { Ad, Category, VipAd } from '@/lib/types';
import { useSession } from '@/store/session';
import { toast, useUi } from '@/store/ui';
import { SelectField, TextField, Checkbox } from './fields';
import { Icon, VectorIcon } from './Icon';
import { DialogHeader, Modal, Spinner, TextButton } from './ui';

// ---------------------------------------------------------------- city picker

/** CityPickerDialogFragment: pick a province, then one of its cities. */
export function CityPickerDialog({ onClose }: { onClose: () => void }) {
  const [province, setProvince] = useState<{ id: string; name: string } | null>(null);
  const setCity = useSession((state) => state.setCity);

  const provinces = useQuery({ queryKey: ['provinces'], queryFn: api.getProvinces, staleTime: Infinity });
  const cities = useQuery({
    queryKey: ['cities', province?.id],
    queryFn: () => api.getCities(province!.id),
    enabled: province !== null,
    staleTime: Infinity,
  });
  const active = province ? cities : provinces;

  const pickCity = (city: { id: string; name: string }) => {
    setCity(city, province ?? undefined);
    const { pushToken, sendNewsNotifications, sendAdsNotifications } = useSession.getState();
    // The server sends ad notifications by city, so tell it about the move (edit_notify_city).
    if (pushToken) {
      void api.updateNotificationSettings(pushToken, sendNewsNotifications, sendAdsNotifications, city.id).catch(() => undefined);
    }
    onClose();
  };

  return (
    <Modal onClose={onClose}>
      <DialogHeader
        title={province ? 'لطفا شهر خود را انتخاب کنید' : 'لطفا استان خود را انتخاب کنید'}
        icon={<Icon path={mdiMapMarker} size={26} />}
        onBack={province ? () => setProvince(null) : undefined}
      />
      <div className="min-h-[180px] flex-1 overflow-y-auto">
        {active.isLoading && (
          <div className="flex justify-center py-16">
            <Spinner />
          </div>
        )}
        {active.isError && (
          <div className="flex justify-center py-16">
            <TextButton color="var(--color-primary)" onClick={() => void active.refetch()}>
              تلاش مجدد
            </TextButton>
          </div>
        )}
        {active.data?.map((row) => (
          <button
            key={row.id}
            type="button"
            className="tap-light block w-full border-b border-[#f3f3f3] px-2.5 py-2.5 text-right text-sm text-[#004D40]"
            onClick={() => {
              const picked = { id: String(row.id), name: row.name };
              if (province) pickCity(picked);
              else setProvince(picked);
            }}
          >
            {row.name}
          </button>
        ))}
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- ad type

const AD_TYPES = [
  { label: 'آگهی کسب و کار', path: '/new/business' },
  { label: 'آگهی املاک', path: '/new/estate' },
  { label: 'آگهی وسایل نقلیه', path: '/new/vehicle' },
  { label: 'آگهی استخدام', path: '/new/job' },
];

/** Select_Add_Type_Dialog: which of the four ad forms to open. */
export function AdTypeDialog({ onClose }: { onClose: () => void }) {
  const navigate = useNavigate();
  return (
    <Modal onClose={onClose}>
      <DialogHeader title="انتخاب نوع آگهی" icon={<Icon path={mdiCheck} size={30} />} />
      <div className="px-2.5 pb-4">
        {AD_TYPES.map((type) => (
          <button
            key={type.path}
            type="button"
            className="tap mt-[5px] flex h-[45px] w-full items-center justify-center bg-[#0097A7] text-base text-white"
            onClick={() => {
              onClose();
              navigate(type.path);
            }}
          >
            {type.label}
          </button>
        ))}
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- long texts

/** Show_Rouls_Dialog */
export function RulesDialog({ onClose }: { onClose: () => void }) {
  return (
    <Modal onClose={onClose}>
      <DialogHeader title="قوانین" icon={<Icon path={mdiInformation} size={30} />} />
      <div className="rich-text flex-1 overflow-y-auto px-3 py-1 text-sm leading-7 text-[#444]" dangerouslySetInnerHTML={{ __html: RULES_HTML }} />
      <div className="flex shrink-0">
        <TextButton color="#00BCD4" onClick={onClose}>
          بازگشت
        </TextButton>
      </div>
    </Modal>
  );
}

/** Show_Link_To_Us («ارتباط با ما») */
export function ContactUsDialog({ onClose }: { onClose: () => void }) {
  return (
    <Modal onClose={onClose}>
      <DialogHeader title="ارتباط با ما" icon={<Icon path={mdiHeadset} size={24} />} />
      <div className="rich-text px-3 py-2 text-sm leading-7 text-[#444]" dangerouslySetInnerHTML={{ __html: LINK_TO_US_HTML }} />
      <div className="flex shrink-0">
        <TextButton color="#00BCD4" onClick={onClose}>
          بازگشت
        </TextButton>
        <TextButton color="#00BCD4" onClick={() => (window.location.href = `sms:+${SMS_NUMBER}`)}>
          ارسال پیامک
        </TextButton>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- notifications

const PUSH_MESSAGES: Record<string, string> = {
  'not-configured': 'دریافت اطلاعیه در نسخه وب هنوز فعال نشده است',
  unsupported: 'مرورگر شما از دریافت اطلاعیه پشتیبانی نمی کند',
  'needs-install': 'برای دریافت اطلاعیه در آیفون، ابتدا از دکمه اشتراک گذاری مرورگر گزینه «Add to Home Screen» را بزنید و برنامه را از صفحه اصلی باز کنید',
  denied: 'اجازه نمایش اعلان برای این سایت بسته شده است؛ آن را از تنظیمات مرورگر باز کنید',
};

/** NotifKindChooserDialogFragment: which notifications (ads / news) this device receives. */
export function NotificationsDialog({ onClose }: { onClose: () => void }) {
  const stored = useSession();
  const [ads, setAds] = useState(stored.sendAdsNotifications);
  const [news, setNews] = useState(stored.sendNewsNotifications);
  const [saving, setSaving] = useState(false);
  const support = pushSupport();

  const save = async () => {
    if (support !== 'ready') {
      toast.failure(PUSH_MESSAGES[support]);
      return;
    }
    setSaving(true);
    try {
      const token = await enablePush();
      if (!token) {
        toast.failure(PUSH_MESSAGES[pushSupport()] ?? 'برای دریافت اطلاعیه باید اجازه نمایش اعلان را بدهید');
        return;
      }
      const result = await api.updateNotificationSettings(token, news, ads, stored.cityId);
      if (String(result.status) === '204') {
        stored.setNotificationPrefs(news, ads);
        toast.success('انتخاب شما ذخیره شد');
        onClose();
      } else {
        toast.failure('لطفا دوباره تلاش کنید');
      }
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal onClose={saving ? undefined : onClose} className="border border-primary">
      <div className="p-4">
        <Checkbox checked={ads} onChange={setAds} className="w-full py-2 text-[15px]">
          آگهی ها
        </Checkbox>
        <Checkbox checked={news} onChange={setNews} className="w-full py-2 text-[15px]">
          اخبار
        </Checkbox>
        {support !== 'ready' && <p className="mt-2 text-xs leading-6 text-mild">{PUSH_MESSAGES[support]}</p>}
        <div className="mt-4 flex items-center">
          {saving ? (
            <span className="px-4">
              <Spinner size={28} />
            </span>
          ) : (
            <>
              <TextButton onClick={() => void save()}>ذخیره</TextButton>
              <TextButton onClick={onClose}>انصراف</TextButton>
            </>
          )}
        </div>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- forgot password

/** ForgotPassDialog: mobile -> SMS code -> new password, in one dialog. */
export function ForgotPasswordDialog({ onClose }: { onClose: () => void }) {
  const [step, setStep] = useState<1 | 2 | 3>(1);
  const [mobile, setMobile] = useState('');
  const [code, setCode] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);

  const run = async () => {
    if (step === 1 && mobile.trim().length !== 11) return toast.failure('لطفا شماره خود را وارد کنید');
    if (step === 2 && code.trim().length === 0) return toast.failure('لطفا کد تایید را وارد کنید');
    if (step === 3 && password.trim().length < 6) return toast.failure('رمز عبور حد اقل باید 6 حرف باشد');
    setBusy(true);
    try {
      if (step === 1) {
        const result = await api.sendResetPasswordCode(mobile);
        if (String(result.status) === '204') {
          setStep(2);
          toast.success('به زودی یک کد تایید به شماره شما ارسال میشود. لطفا آن را در این کادر وارد کنید.');
        } else toast.failure('حسابی با این شماره ثبت نشده است');
      } else if (step === 2) {
        const result = await api.verifyResetPasswordCode(mobile, code);
        if (String(result.status) === '204') setStep(3);
        else toast.failure('کد تاییدی که وارد کرده اید اشتباه است');
      } else {
        const result = await api.resetPassword(mobile, code, password);
        if (String(result.status) === '204') {
          toast.success('رمز عبور شما با موفقیت تغییر کرد');
          onClose();
        } else toast.failure('لطفا دوباره تلاش کنید');
      }
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal onClose={busy ? undefined : onClose}>
      <DialogHeader title="رمز عبور جدید" icon={<Icon path={mdiKeyVariant} size={24} />} />
      <div className="px-[5px] pt-1">
        {step === 1 && <TextField value={mobile} onChange={setMobile} mode="digits" maxLength={11} height={42} placeholder="شماره همراه خود را وارد کنید" onEnter={() => void run()} />}
        {step === 2 && <TextField value={code} onChange={setCode} mode="digits" maxLength={8} height={42} placeholder="کد تایید را وارد کنید" onEnter={() => void run()} autoComplete="one-time-code" />}
        {step === 3 && <TextField value={password} onChange={setPassword} height={42} placeholder="رمز عبور جدید را وارد کنید (6 حرف)" onEnter={() => void run()} />}
      </div>
      <div className="mt-3 flex items-center">
        {busy ? (
          <span className="px-5 py-2">
            <Spinner size={28} />
          </span>
        ) : (
          <TextButton onClick={() => void run()}>{step === 1 ? 'درخواست کد' : step === 2 ? 'ارسال کد' : 'ثبت رمز'}</TextButton>
        )}
        <TextButton onClick={onClose}>انصراف</TextButton>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- change mobile

/** ChangeMobileDialog: fix a mistyped number before the SMS code is confirmed. */
export function ChangeMobileDialog({ onClose, onChanged }: { onClose: () => void; onChanged: () => void }) {
  const user = useSession((state) => state.user);
  const updateUser = useSession((state) => state.updateUser);
  const [mobile, setMobile] = useState('');
  const [warn, setWarn] = useState(false);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    if (!user) return;
    if (mobile.trim().length !== 11) return setWarn(true);
    setBusy(true);
    try {
      const result = await api.changeMobile(user.mobile, mobile);
      if (String(result.status) === '204') {
        updateUser({ mobile });
        toast.success('شماره شما با موفقیت تغییر کرد');
        onChanged();
        onClose();
      } else if (result.error === 'mobile_existed') toast.failure('این شماره قبلا ثبت شده است');
      else toast.failure('لطفا دوباره تلاش کنید');
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    } finally {
      setBusy(false);
    }
  };

  return (
    <Modal onClose={busy ? undefined : onClose}>
      <DialogHeader title="تغییر شماره موبایل" icon={<Icon path={mdiInformation} size={30} />} />
      <div className="px-[5px] pt-1">
        <TextField value={user?.mobile ?? ''} onChange={() => undefined} disabled height={42} placeholder="شماره قدیمی" mode="digits" />
        <TextField
          value={mobile}
          onChange={(value) => {
            setMobile(value);
            setWarn(false);
          }}
          mode="digits"
          maxLength={11}
          height={42}
          placeholder="شماره جدید"
          warn={warn && 'لطفا شماره جدید خود را وارد کنید'}
          onEnter={() => void submit()}
        />
      </div>
      <div className="mt-3 flex items-center">
        {busy ? (
          <span className="px-5 py-2">
            <Spinner size={28} />
          </span>
        ) : (
          <TextButton onClick={() => void submit()}>ثبت</TextButton>
        )}
        <TextButton onClick={onClose}>رد کردن</TextButton>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- jalali date

/** Day / month / year wheels of the app's date dialogs, as three pickers. */
export function JalaliDateDialog({
  title,
  initial,
  minYear,
  maxYear,
  onPick,
  onClose,
}: {
  title: string;
  initial: jalali.JDate;
  minYear: number;
  maxYear: number;
  /** Return an error message to reject the date and keep the dialog open. */
  onPick: (date: jalali.JDate) => string | void;
  onClose: () => void;
}) {
  const [year, setYear] = useState(initial[0]);
  const [month, setMonth] = useState(initial[1]);
  const [day, setDay] = useState(initial[2]);
  const length = jalali.monthLength(year, month);

  useEffect(() => {
    if (day > length) setDay(length);
  }, [day, length]);

  const years = useMemo(() => {
    const out = [];
    for (let y = maxYear; y >= minYear; y--) out.push({ value: String(y), label: String(y) });
    return out;
  }, [minYear, maxYear]);

  return (
    <Modal onClose={onClose} className="w-[84%]">
      <div className="px-5 pt-5 text-base text-[#212121]">{title}</div>
      <div className="flex gap-2 px-4 pt-4">
        <SelectField className="flex-1" placeholder="" value={String(day)} onChange={(v) => setDay(Number(v))} options={Array.from({ length }, (_, i) => ({ value: String(i + 1), label: String(i + 1) }))} />
        <SelectField className="flex-[1.4]" placeholder="" value={String(month)} onChange={(v) => setMonth(Number(v))} options={jalali.MONTH_NAMES.map((name, i) => ({ value: String(i + 1), label: name }))} />
        <SelectField className="flex-1" placeholder="" value={String(year)} onChange={(v) => setYear(Number(v))} options={years} />
      </div>
      <div className="flex justify-end px-2 pb-2" dir="ltr">
        <TextButton
          color="var(--color-primary)"
          className="text-base"
          onClick={() => {
            const error = onPick([year, month, Math.min(day, length)]);
            if (error) toast.failure(error);
            else onClose();
          }}
        >
          تایید
        </TextButton>
        <TextButton color="var(--color-primary)" className="text-base" onClick={onClose}>
          انصراف
        </TextButton>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- ad contact info

function ContactRow({ icon, children, onClick, ltr }: { icon: string; children: string; onClick?: () => void; ltr?: boolean }) {
  return (
    <button
      type="button"
      disabled={!onClick}
      className={cx('flex w-full items-center gap-2 p-1 text-sm', onClick ? 'tap-light text-[#2196F3]' : 'text-[#555]', ltr && 'flex-row-reverse')}
      onClick={onClick}
    >
      <Icon path={icon} size={16} className="text-primary" />
      <span className={cx('min-w-0 flex-1 break-words', ltr ? 'text-left' : 'text-right')} dir={ltr ? 'ltr' : undefined}>
        {children}
      </span>
    </button>
  );
}

function ContactCard({ title, icon, children }: { title: string; icon: string; children: ReactNode }) {
  return (
    <div className="mb-2 rounded-[3px] bg-white p-1" style={{ boxShadow: 'var(--shadow-card)' }}>
      <div className="flex items-center gap-2 px-1 pb-1 text-sm">
        <Icon path={icon} size={16} className="text-primary" />
        {title}
      </div>
      <div className="mb-1 h-px bg-[#bcbcbc]" />
      {children}
    </div>
  );
}

/** ContactFormDialog («اطلاعات تماس» of a business ad). */
export function AdContactDialog({ ad, onClose }: { ad: Ad; onClose: () => void }) {
  const phones = [
    { value: ad.mobile, icon: mdiPhone },
    { value: ad.tel1, icon: mdiPhoneClassic },
    { value: ad.tel2, icon: mdiPhoneClassic },
  ].filter((phone) => !blank(phone.value) && phone.value !== '0');
  const hasInternet = [ad.link, ad.email, ad.telegram, ad.instagram].some((value) => !blank(value));
  const place = [text(ad.province_name), text(ad.city_name)].filter(Boolean).join(' - ');

  return (
    <Modal onClose={onClose} className="!bg-[#eee]">
      <DialogHeader title="اطلاعات تماس" icon={<Icon path={mdiHeadset} size={24} />} />
      <div className="flex-1 overflow-y-auto px-[5px] pt-1 pb-2">
        {phones.length > 0 && (
          <ContactCard title="شماره ها" icon={mdiCellphone}>
            {phones.map((phone) => (
              <ContactRow key={`${phone.icon}${phone.value}`} icon={phone.icon} onClick={() => dial(String(phone.value))}>
                {String(phone.value)}
              </ContactRow>
            ))}
          </ContactCard>
        )}
        {hasInternet && (
          <ContactCard title="تماس اینترنتی" icon={mdiWeb}>
            {!blank(ad.link) && (
              <ContactRow ltr icon={mdiWeb} onClick={() => openExternal(withScheme(String(ad.link)))}>
                {String(ad.link)}
              </ContactRow>
            )}
            {!blank(ad.email) && (
              <ContactRow ltr icon={mdiEmail} onClick={() => (window.location.href = `mailto:${ad.email}`)}>
                {String(ad.email)}
              </ContactRow>
            )}
            {!blank(ad.telegram) && (
              <ContactRow ltr icon={mdiSend} onClick={() => openExternal(socialLink(String(ad.telegram), 'https://t.me/'))}>
                {String(ad.telegram)}
              </ContactRow>
            )}
            {!blank(ad.instagram) && (
              <ContactRow ltr icon={mdiInstagram} onClick={() => openExternal(socialLink(String(ad.instagram), 'https://instagram.com/'))}>
                {String(ad.instagram)}
              </ContactRow>
            )}
          </ContactCard>
        )}
        <ContactCard title="آدرس" icon={mdiMap}>
          {place && <ContactRow icon={mdiMapMarker}>{place}</ContactRow>}
          <ContactRow icon={mdiCrosshairsGps}>{text(ad.address) || '-'}</ContactRow>
          {!blank(ad.working_time) && <ContactRow icon={mdiClockOutline}>{String(ad.working_time)}</ContactRow>}
        </ContactCard>
      </div>
    </Modal>
  );
}

/** Owners type "@name", "name", "t.me/name" or a full link; all of them should open. */
function socialLink(value: string, base: string): string {
  const trimmed = value.trim();
  if (/^https?:\/\//i.test(trimmed)) return trimmed;
  if (/^[\w.-]+\.[a-z]{2,}\//i.test(trimmed)) return `https://${trimmed}`;
  return base + trimmed.replace(/^@/, '');
}

// ---------------------------------------------------------------- sub categories

/** SubcategoriesDialogFragment: the sub categories of a category that have ads in the city. */
export function SubCategoriesDialog({ category, onClose }: { category: Category; onClose: () => void }) {
  const navigate = useNavigate();
  const cityId = useSession((state) => state.cityId);
  const parentIcon = categoryIcon(category.name);
  const query = useQuery({
    queryKey: ['city-subcategories', category.id, cityId],
    queryFn: () => api.getCitySubCategories(category.id, cityId),
  });

  return (
    <Modal onClose={onClose}>
      <div className="min-h-[120px] overflow-y-auto p-1">
        {query.isLoading && (
          <div className="flex justify-center py-10">
            <Spinner />
          </div>
        )}
        {(query.isError || query.data?.length === 0) && <div className="py-10 text-center text-sm">این دسته خالی است</div>}
        <div className="grid grid-cols-3">
          {query.data?.map((sub) => {
            const own = subCategoryIcon(sub.name, null);
            const remote = iconUrl(sub.icon);
            // Same rule as the app: a known name gets its line icon, otherwise the server's
            // thumbnail, otherwise the parent category's icon.
            const useRemote = own === 'ic_cat_default' && remote;
            return (
              <button
                key={sub.id}
                type="button"
                className="tap-light flex flex-col items-center rounded p-[3px] pb-2"
                onClick={() => {
                  onClose();
                  navigate(`/ads/${sub.id}`, { state: { title: sub.name } });
                }}
              >
                <span className="mt-1 flex items-center justify-center rounded-full border border-sub-plate-stroke bg-sub-plate size-sdp-42">
                  {useRemote ? (
                    <img src={remote} alt="" className="rounded-sm object-contain size-sdp-20" />
                  ) : (
                    <VectorIcon name={subCategoryIcon(sub.name, parentIcon)} color="var(--color-sub-icon)" className="size-sdp-20" size={0} />
                  )}
                </span>
                <span className="mt-[5px] line-clamp-2 text-center text-[10px] leading-4 text-[#37474F]">{sub.name}</span>
              </button>
            );
          })}
        </div>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- VIP ad

/** VipAdDialog: a full-screen promoted ad shown once when the app opens. */
export function VipAdDialog({ ad, onClose }: { ad: VipAd; onClose: () => void }) {
  const navigate = useNavigate();
  const image = mediaUrl(ad.vip_ads_photo) ?? mediaUrl(ad.photos?.[0]?.file_name);
  return (
    <Modal full onClose={onClose} className="!bg-black">
      <div
        className="relative h-full w-full cursor-pointer"
        onClick={() => {
          onClose();
          stash('ad', ad.id, ad);
          navigate(`/ad/${ad.id}`);
        }}
      >
        {image && <img src={image} alt={ad.title} className="h-full w-full object-cover" />}
        <div className="absolute inset-x-0.5 bottom-0.5 rounded-[3px] bg-white/45 p-1 text-center text-[#263238] backdrop-blur-sm">{ad.title}</div>
        <button
          type="button"
          aria-label="بستن"
          className="absolute top-3 right-3 flex h-10 w-10 items-center justify-center rounded-full bg-black/50 text-white"
          onClick={(event) => {
            event.stopPropagation();
            onClose();
          }}
        >
          <Icon path={mdiClose} />
        </button>
      </div>
    </Modal>
  );
}

// ---------------------------------------------------------------- host

/** The dialogs that can be opened from any screen (see store/ui). */
export function GlobalDialogs() {
  const ui = useUi();
  return (
    <>
      {ui.cityPickerOpen && <CityPickerDialog onClose={() => ui.set({ cityPickerOpen: false })} />}
      {ui.adTypeOpen && <AdTypeDialog onClose={() => ui.set({ adTypeOpen: false })} />}
      {ui.rulesOpen && <RulesDialog onClose={() => ui.set({ rulesOpen: false })} />}
      {ui.contactUsOpen && <ContactUsDialog onClose={() => ui.set({ contactUsOpen: false })} />}
      {ui.notificationsOpen && <NotificationsDialog onClose={() => ui.set({ notificationsOpen: false })} />}
    </>
  );
}
