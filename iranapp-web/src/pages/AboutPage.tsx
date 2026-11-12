import type { ReactNode } from 'react';
import { mdiCheck, mdiChevronLeft, mdiContentCopy, mdiEmail, mdiInstagram, mdiMapMarker, mdiPhone, mdiWeb } from '@mdi/js';
import logoRedUrl from '@/assets/img/iran_app_logo_red.png';
import { Icon, VectorIcon, vectorUrl } from '@/components/Icon';
import { AppBar, Logo, Screen } from '@/components/ui';
import { BAZAAR_LINK } from '@/config';
import { ABOUT_COMPANY_TEXT } from '@/content/texts';
import { copyText, openExternal } from '@/lib/actions';
import { toast } from '@/store/ui';

interface RowSpec {
  icon: ReactNode;
  label: string;
  value: string;
  /** A tel:, sms: or web address to open; without it the row copies its value (when `copy`). */
  target?: string;
  copy?: boolean;
  /** Show the value as a highlighted number. */
  pill?: boolean;
}

const md = (path: string) => <Icon path={path} size={20} />;

const LICENCES: RowSpec[] = [
  { icon: md(mdiCheck), label: 'پروانه کسب', value: 'اتحادیه فناوری اطلاعات رایانه' },
  { icon: md(mdiCheck), label: 'پروانه کسب', value: 'کانون آگهی تبلیغات' },
  { icon: md(mdiCheck), label: 'ثبت شرکت - شماره ثبت', value: '54916', copy: true, pill: true },
  { icon: md(mdiCheck), label: 'مجوز فعالیت از وزارت فرهنگ و ارشاد اسلامی - شماره مجوز', value: '1-1-718196-63-5-1', copy: true, pill: true },
];

const CONTACTS: RowSpec[] = [
  { icon: md(mdiPhone), label: 'شماره تماس', value: '09380052014', target: 'tel:09380052014' },
  { icon: md(mdiPhone), label: 'شماره تماس', value: '09131094406', target: 'tel:09131094406' },
  { icon: md(mdiInstagram), label: 'اینستاگرام', value: '@iranianappes', target: 'https://instagram.com/iranianappes' },
  { icon: <img src={vectorUrl('ic_eitaa')} alt="" className="h-5 w-5 rounded" />, label: 'کانال ایتا', value: '@iranapp', target: 'https://eitaa.com/iranapp' },
  { icon: md(mdiEmail), label: 'پیامک نظرات، انتقادات و پیشنهادات', value: '985000538027', target: 'sms:+985000538027' },
  { icon: md(mdiMapMarker), label: 'کد پستی', value: '8157834911', copy: true },
  { icon: md(mdiWeb), label: 'وب‌سایت', value: 'iranapp.biz', target: 'http://iranapp.biz' },
  { icon: <VectorIcon name="ic_cat_store" size={20} />, label: 'دانلود و امتیاز به ایران آپ', value: 'کافه‌بازار', target: BAZAAR_LINK },
];

async function copy(value: string) {
  if (await copyText(value)) toast.success(`کپی شد: ${value}`);
}

function Row({ row }: { row: RowSpec }) {
  const interactive = Boolean(row.target || row.copy);
  const open = () => {
    if (!row.target) return void copy(row.value);
    if (/^(tel|sms):/.test(row.target)) window.location.href = row.target;
    else openExternal(row.target);
  };
  return (
    <button
      type="button"
      disabled={!interactive}
      className={`flex min-h-16 w-full items-center px-3.5 py-2.5 text-right ${interactive ? 'tap-dark' : ''}`}
      onClick={open}
      onContextMenu={(event) => {
        // A long press copies any value, including phone numbers and links.
        if (!interactive) return;
        event.preventDefault();
        void copy(row.value);
      }}
    >
      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-home-plate-stroke bg-home-plate text-home-accent">{row.icon}</span>
      <span className="mr-3 min-w-0 flex-1">
        <span className="block text-xs text-home-muted">{row.label}</span>
        <span
          className={`mt-0.5 inline-block text-[15px] ${row.pill ? 'rounded-xl border border-home-accent/40 bg-home-accent/15 px-2.5 py-0.5 text-home-accent' : 'text-home-text'}`}
          dir={/^[\d@+\-.\w]+$/.test(row.value) ? 'ltr' : undefined}
        >
          {row.value}
        </span>
      </span>
      {interactive && <Icon path={row.target ? mdiChevronLeft : mdiContentCopy} size={20} className="mr-2 text-home-nav" />}
    </button>
  );
}

function Section({ title, rows }: { title: string; rows: RowSpec[] }) {
  return (
    <>
      <h2 className="mt-7 mb-2.5 text-base font-normal text-home-text">{title}</h2>
      <div className="divide-y divide-home-tile-stroke rounded-2xl border border-home-tile-stroke bg-home-tile">
        {rows.map((row) => (
          <Row key={row.label + row.value} row={row} />
        ))}
      </div>
    </>
  );
}

/** AboutActivity («درباره ما»): the company page with licences and contact details. */
export default function AboutPage() {
  return (
    <Screen bar={<AppBar title="درباره ما" dark line={false} />} className="bg-home-dark">
      <div className="min-h-0 flex-1 overflow-y-auto px-4 py-6">
        <div className="mx-auto flex h-[88px] w-[88px] items-center justify-center rounded-full bg-white shadow-lg">
          <img src={logoRedUrl} alt="" className="h-[58px] w-[58px] object-contain" />
        </div>
        <div className="mt-4 flex justify-center">
          <Logo color="var(--color-home-text)" width={150} height={42} />
        </div>
        <div className="mx-auto mt-2.5 h-[3px] w-9 rounded-sm bg-home-accent" />
        <p className="mt-3 text-center text-sm text-home-muted">راهنمای هوشمند اصناف، خدمات و اماکن شهر شما</p>
        <div className="mt-4 flex flex-wrap justify-center gap-2">
          {['پروانه کسب', 'ثبت شرکت', 'مجوز ارشاد'].map((chip) => (
            <span key={chip} className="flex items-center gap-1 rounded-2xl border border-home-tile-stroke bg-home-tile px-3 py-1.5 text-xs text-home-muted">
              <Icon path={mdiCheck} size={14} className="text-home-accent" />
              {chip}
            </span>
          ))}
        </div>

        <h2 className="mt-7 mb-2.5 text-base font-normal text-home-text">درباره ما</h2>
        <p className="whitespace-pre-line rounded-2xl border border-home-tile-stroke bg-home-tile p-4 text-sm leading-8 text-home-text">{ABOUT_COMPANY_TEXT}</p>

        <Section title="مجوزها و ثبت‌ها" rows={LICENCES} />
        <Section title="ارتباط با ما" rows={CONTACTS} />
        <p className="mt-5 text-center text-xs text-home-muted">ایران آپ - نسخه وب</p>
      </div>
    </Screen>
  );
}
