import { useRef, useState } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { mdiDeleteForever, mdiPlus } from '@mdi/js';
import { TextField } from '@/components/fields';
import { Icon } from '@/components/Icon';
import { AppBar, Button, Card, LinearProgress, Logo, Screen } from '@/components/ui';
import { APP_VERSION } from '@/config';
import * as api from '@/lib/api';
import { isValidNationalCode, squeeze } from '@/lib/format';
import * as jalali from '@/lib/jalali';
import type { MembershipCard, MembershipState } from '@/lib/types';
import { alertDialog, toast } from '@/store/ui';

function StatusCard({ color, title, children }: { color: string; title: string; children: string }) {
  return (
    <Card className="m-[5px] overflow-hidden rounded-lg">
      <div className="flex h-[35px] items-center justify-center text-sm text-white" style={{ background: color }}>
        {title}
      </div>
      <p className="whitespace-pre-line p-3 text-sm leading-7 text-[#444]">{children}</p>
    </Card>
  );
}

/** The card itself; grey with a stamp while pending or once expired. */
function CardFace({ card, stamp }: { card: MembershipCard; stamp?: string }) {
  return (
    <div
      className="mx-3 mt-2.5 rounded-[14px] px-[18px] pt-3 pb-4 text-white shadow-lg"
      style={{ background: stamp ? 'radial-gradient(circle at 75% 30%, #9e9e9e, #616161)' : 'radial-gradient(circle at 75% 30%, #ef5350, var(--color-primary))' }}
    >
      <div className="flex items-center justify-between">
        <Logo color="#fff" width={110} height={34} />
        {stamp ? <span className="rounded-md border-[1.5px] border-white px-2 py-0.5 text-xs">{stamp}</span> : <span className="text-sm">کارت عضویت</span>}
      </div>
      <div className="my-2 h-px bg-white/35" />
      <div className="text-center text-xs text-white/80">شماره سریال</div>
      <div className="mb-2.5 text-center font-mono text-[21px] font-bold tracking-wider" dir="ltr">
        {card.serial_number}
      </div>
      <div className="text-xs text-white/80">اعضا</div>
      <div className="mb-2.5 whitespace-pre-line text-[15px] leading-7">{(card.members ?? []).map((name, index) => `${index + 1}. ${name}`).join('\n')}</div>
      <div className="flex">
        <div className="flex-1">
          <div className="text-xs text-white/80">تاریخ عضویت</div>
          <div className="text-[15px]">{card.membership_date}</div>
        </div>
        <div className="flex-1 text-left">
          <div className="text-xs text-white/80">اعتبار تا</div>
          <div className="text-[15px]">{card.expiry_date}</div>
        </div>
      </div>
    </div>
  );
}

/** MembershipCardActivity («کارت عضویت»): request a card for 1 to 6 people and see it once approved. */
export default function MembershipCardPage() {
  const queryClient = useQueryClient();
  const scrollRef = useRef<HTMLDivElement>(null);
  const query = useQuery({ queryKey: ['membership-card'], queryFn: api.getMembershipCard, staleTime: 0 });
  const data = query.data && String(query.data.status) === '200' ? query.data : null;
  const card = data?.card ?? null;

  const reload = () => {
    scrollRef.current?.scrollTo({ top: 0 });
    return queryClient.invalidateQueries({ queryKey: ['membership-card'] });
  };

  return (
    <Screen bar={<AppBar />} className="bg-[#eee]">
      <div className="shrink-0 px-2.5 py-2.5 text-center text-base text-white" style={{ background: 'radial-gradient(circle at 75% 50%, #ef5350, var(--color-primary))' }}>
        {data?.title || 'کارت هدیه معرفی به مراکز طرف قرارداد'}
      </div>
      {query.isFetching && <LinearProgress color="var(--color-primary)" />}
      {query.isLoading ? null : !data ? (
        <div className="flex flex-1 flex-col items-center justify-center">
          <p className="p-5 text-center text-sm leading-7 text-mild">
            دریافت اطلاعات کارت عضویت ممکن نشد.
            <br />
            لطفا اتصال اینترنت خود را بررسی کنید.
          </p>
          <Button tone="teal" className="!w-40" onClick={() => void query.refetch()}>
            تلاش دوباره
          </Button>
        </div>
      ) : (
        <div ref={scrollRef} className="min-h-0 flex-1 overflow-y-auto pb-5">
          {card?.status === 'pending' && (
            <>
              <StatusCard color="#FFA000" title="در انتظار بررسی">
                {`درخواست کارت عضویت شما در تاریخ ${card.submitted_at ?? ''} ارسال شد و در حال بررسی توسط کارشناسان ایران اپ است.\nنتیجه بررسی از طریق اعلان به شما اطلاع داده می شود.`}
              </StatusCard>
              <CardFace card={card} stamp="در انتظار تایید" />
            </>
          )}
          {card?.status === 'approved' &&
            (card.is_expired ? (
              <>
                <StatusCard color="#616161" title="اعتبار کارت به پایان رسیده است">{`اعتبار کارت عضویت شما در تاریخ ${card.expiry_date} به پایان رسیده است.`}</StatusCard>
                <CardFace card={card} stamp="منقضی شده" />
              </>
            ) : (
              <CardFace card={card} />
            ))}
          {card?.status === 'rejected' && (
            <StatusCard color="#b71c1c" title="درخواست شما نیاز به اصلاح دارد">
              {`توضیحات کارشناس: ${card.rejection_reason ?? ''}\n\nلطفا موارد گفته شده را اصلاح کنید و درخواست را دوباره ارسال نمایید.`}
            </StatusCard>
          )}
          {(!card || card.status === 'rejected') && <RequestForm key={card?.id ?? 'new'} data={data} previous={card} reload={reload} />}
        </div>
      )}
    </Screen>
  );
}

const Label = ({ children }: { children: string }) => <div className="mx-[5px] mt-2.5 mb-1 text-[13px] text-[#757575]">{children}</div>;
const ReadOnly = ({ children }: { children: string }) => (
  <div className="mb-1 flex h-[45px] items-center rounded-[3px] border border-[#f44336] bg-[#f4f4f4] px-2.5 text-sm text-[#757575]">{children}</div>
);

/** A new request, or the rejected one pre-filled for correction. */
function RequestForm({ data, previous, reload }: { data: MembershipState; previous: MembershipCard | null; reload: () => Promise<void> }) {
  const [members, setMembers] = useState<string[]>(() => (previous?.members?.length ? previous.members.slice(0, data.max_members) : [data.holder ?? '']));
  const [nationalCode, setNationalCode] = useState(previous?.national_code ?? '');
  const [busy, setBusy] = useState(false);
  const start = jalali.parse(data.today);

  const submit = async () => {
    // Same rules as the server: 1 to 6 names of at least 3 characters; empty rows are skipped.
    const names: string[] = [];
    for (let i = 0; i < members.length; i++) {
      const name = squeeze(members[i]);
      if (!name) continue;
      if (name.length < 3) return toast.failure(`نام و نام خانوادگی عضو ${i + 1} را کامل وارد کنید`);
      names.push(name);
    }
    if (names.length < data.min_members) return toast.failure('نام دست کم یک عضو را وارد کنید');
    if (names.length > data.max_members) return toast.failure(`حداکثر ${data.max_members} عضو را می توانید ثبت کنید`);
    if (!nationalCode) return toast.failure('کد ملی عضو اول (صاحب کارت) را وارد کنید');
    if (!isValidNationalCode(nationalCode)) return toast.failure('کد ملی عضو اول (صاحب کارت) معتبر نیست');

    setBusy(true);
    try {
      const result = await api.submitMembershipCard(names, nationalCode, APP_VERSION);
      const status = String(result.status);
      if (status === '201') {
        toast.success('درخواست کارت عضویت شما ثبت شد و پس از بررسی، نتیجه به شما اعلام می شود');
        await reload();
      } else if (status === '422') {
        await alertDialog((result.errors ?? []).map((line) => `• ${line}`).join('\n'), 'باشه', 'لطفا موارد زیر را اصلاح کنید');
      } else if (status === '409') {
        toast.failure(result.message ?? 'درخواست شما پیش از این ثبت شده است');
        await reload();
      } else {
        toast.failure('لطفا دوباره تلاش کنید');
      }
    } catch {
      toast.failure('ارسال ممکن نشد؛ لطفا اتصال اینترنت خود را بررسی کنید');
    } finally {
      setBusy(false);
    }
  };

  return (
    <>
      <Card className="m-[5px] overflow-hidden rounded-lg pb-2.5">
        <div className="flex h-[35px] items-center justify-center bg-primary text-sm text-white">درخواست کارت عضویت</div>
        <div className="px-[5px]">
          {/* The server sets the real dates when the request arrives; these are what it will set today. */}
          <Label>تاریخ عضویت</Label>
          <ReadOnly>{data.today}</ReadOnly>
          {start && <div className="mx-[5px] mt-1 text-[13px] text-hot">اعتبار کارت: یک سال، تا {jalali.format(jalali.addMonths(start, data.validity_months))}</div>}
          <Label>شماره سریال</Label>
          <ReadOnly>{previous ? previous.serial_number : 'پس از ثبت درخواست به شما اختصاص داده می شود'}</ReadOnly>
          <Label>کد ملی صاحب کارت (عضو اول)</Label>
          <TextField value={nationalCode} onChange={setNationalCode} mode="digits" maxLength={10} height={50} placeholder="کد ملی 10 رقمی" />
          <Label>{`نام اعضا (حداقل ${data.min_members} و حداکثر ${data.max_members} نفر)`}</Label>
          {members.map((name, index) => (
            <div key={index} className="flex items-start gap-1">
              <TextField
                className="min-w-0 flex-1"
                value={name}
                maxLength={100}
                height={50}
                autoComplete="name"
                placeholder={`نام و نام خانوادگی عضو ${index + 1}`}
                onChange={(value) => setMembers(members.map((old, i) => (i === index ? value : old)))}
              />
              {members.length > data.min_members && (
                <button type="button" aria-label={`حذف عضو ${index + 1}`} className="tap-light mt-[5px] flex h-10 w-10 items-center justify-center rounded-full text-[#9e9e9e]" onClick={() => setMembers(members.filter((_, i) => i !== index))}>
                  <Icon path={mdiDeleteForever} />
                </button>
              )}
            </div>
          ))}
          {members.length < data.max_members && (
            <button type="button" className="tap-light flex h-10 items-center gap-1 rounded px-2 text-sm text-primary" onClick={() => setMembers([...members, ''])}>
              <Icon path={mdiPlus} size={22} />
              افزودن عضو
            </button>
          )}
        </div>
      </Card>
      <div className="mx-5 mt-2.5">
        <Button tone="teal" height={60} loading={busy} className="rounded-lg shadow-md" onClick={() => void submit()}>
          {previous ? 'ارسال مجدد درخواست' : 'ثبت درخواست کارت عضویت'}
        </Button>
      </div>
    </>
  );
}
