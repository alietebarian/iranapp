import { useEffect, useMemo, useRef, useState, type ReactNode } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { JalaliDateDialog } from '@/components/dialogs';
import { Checkbox, RadioRow, TextArea, TextField } from '@/components/fields';
import { AppBar, Button, Card, LinearProgress, Logo, Modal, Screen } from '@/components/ui';
import { APP_VERSION } from '@/config';
import * as api from '@/lib/api';
import { copyText } from '@/lib/actions';
import { isValidNationalCode, toLatinDigits } from '@/lib/format';
import * as jalali from '@/lib/jalali';
import type { ContractTemplate, EContract, EContractState } from '@/lib/types';
import { useSession } from '@/store/session';
import { alertDialog, toast } from '@/store/ui';

const HIGHLIGHT = '#b71c1c';
const DEFAULT_BLANK = '..............';

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

function SectionCard({ title, children, color = 'var(--color-primary)' }: { title: string; children: ReactNode; color?: string }) {
  return (
    <Card className="m-[5px] overflow-hidden rounded-lg pb-2.5">
      <div className="flex h-[35px] items-center justify-center text-sm text-white" style={{ background: color }}>
        {title}
      </div>
      <div className="px-[5px] pt-1">{children}</div>
    </Card>
  );
}

const Label = ({ children }: { children: string }) => <div className="mx-[5px] mt-2.5 mb-1 text-[13px] text-[#757575]">{children}</div>;

function PickerBox({ children, onClick }: { children: string; onClick?: () => void }) {
  return (
    <button type="button" disabled={!onClick} className="mb-1 flex h-[45px] w-full items-center rounded-[3px] border border-[#f44336] bg-[#f4f4f4] px-2.5 text-right text-sm text-[#333]" onClick={onClick}>
      {children}
    </button>
  );
}

/**
 * The stored text opens with the title and date («قرارداد همکاری», «تاریخ: ...»), which the header
 * card already shows; drop them so they are not printed twice.
 */
function withoutHeader(text: string): string {
  const blocks = text.split('\n\n');
  if (blocks.length >= 3 && blocks[1].startsWith('تاریخ:')) return blocks.slice(2).join('\n\n');
  return text;
}

/** The contract with the user's values in red, the way it will be stored. */
function Preview({ template, values }: { template: ContractTemplate; values: Record<string, string> }) {
  const blank = template.blank || DEFAULT_BLANK;
  return (
    <div className="p-3 text-sm leading-8 text-[#333]">
      {template.sections.map((section, index) => (
        <p key={index} className="mb-4 whitespace-pre-line">
          {section.heading && (
            <>
              <b style={{ color: HIGHLIGHT }}>{section.heading}:</b>
              {'\n'}
            </>
          )}
          {section.text.split(/(\{\w+\})/g).map((part, i) => {
            const match = /^\{(\w+)\}$/.exec(part);
            if (!match) return part;
            const value = values[match[1]];
            return value ? (
              <b key={i} style={{ color: HIGHLIGHT }}>
                {value}
              </b>
            ) : (
              blank
            );
          })}
        </p>
      ))}
    </div>
  );
}

interface FormState {
  businessType: string;
  businessName: string;
  managerTitle: string;
  managerName: string;
  nationalCode: string;
  phone: string;
  mobile: string;
  address: string;
  subject: string;
  discount: string;
  duration: number;
  start: jalali.JDate;
  accepted: boolean;
}

/** EContractActivity («قرارداد الکترونیک»): request, follow up and pay for a cooperation contract. */
export default function EContractPage() {
  const queryClient = useQueryClient();
  const query = useQuery({ queryKey: ['e-contract'], queryFn: api.getEContract, staleTime: 0 });
  const data = query.data && String(query.data.status) === '200' ? query.data : null;
  const scrollRef = useRef<HTMLDivElement>(null);

  const reload = () => {
    scrollRef.current?.scrollTo({ top: 0 });
    return queryClient.invalidateQueries({ queryKey: ['e-contract'] });
  };

  return (
    <Screen bar={<AppBar />} className="bg-[#eee]">
      {query.isFetching && <LinearProgress color="var(--color-primary)" />}
      {query.isLoading ? null : !data ? (
        <div className="flex flex-1 flex-col items-center justify-center">
          <p className="p-5 text-center text-sm leading-7 text-mild">
            دریافت اطلاعات قرارداد ممکن نشد.
            <br />
            لطفا اتصال اینترنت خود را بررسی کنید.
          </p>
          <Button tone="teal" className="!w-40" onClick={() => void query.refetch()}>
            تلاش دوباره
          </Button>
        </div>
      ) : (
        <div ref={scrollRef} className="min-h-0 flex-1 overflow-y-auto pb-5">
          <Body key={`${data.contract?.id ?? 'new'}-${data.contract?.status ?? ''}`} data={data} reload={reload} />
        </div>
      )}
    </Screen>
  );
}

function Body({ data, reload }: { data: EContractState; reload: () => Promise<void> }) {
  const contract = data.contract;
  const status = contract?.status ?? 'none';
  const showsForm = status === 'none' || status === 'rejected';
  const updateUser = useSession((state) => state.updateUser);

  useEffect(() => {
    if (data.role) updateUser({ role: data.role });
  }, [data.role, updateUser]);

  if (showsForm) return <ContractForm data={data} previous={status === 'rejected' ? contract : null} reload={reload} />;
  if (!contract) return null;

  let card: ReactNode;
  if (status === 'pending') {
    card = (
      <StatusCard color="#FFA000" title="در انتظار بررسی">
        {`درخواست قرارداد شما در تاریخ ${contract.submitted_at ?? ''} ارسال شد و در حال بررسی توسط کارشناسان ایران اپ است.\nنتیجه بررسی از طریق اعلان به شما اطلاع داده می شود.`}
      </StatusCard>
    );
  } else if (status === 'awaiting_payment') {
    card = (
      <StatusCard color="#1976D2" title="در انتظار پرداخت">
        درخواست قرارداد شما بررسی شد. برای ثبت نهایی قرارداد، مبلغ زیر را به شماره کارت اعلام شده واریز کنید و سپس دکمه «هزینه را پرداخت کردم» را بزنید.
      </StatusCard>
    );
  } else if (status === 'payment_submitted') {
    card = (
      <StatusCard color="#FFA000" title="پرداخت شما در حال بررسی است">
        {`شما در تاریخ ${contract.payment_submitted_at ?? ''} اعلام کردید که مبلغ ${contract.amount_formatted ?? ''} تومان را پرداخت کرده اید.\nپس از تایید واریز توسط ایران اپ، قرارداد شما ثبت نهایی می شود و نتیجه از طریق اعلان به شما اطلاع داده می شود.`}
      </StatusCard>
    );
  } else {
    card = <ApprovedCard contract={contract} />;
  }

  return (
    <>
      {card}
      {status === 'awaiting_payment' && <PaymentCard contract={contract} reload={reload} />}
      <HeaderCard title={data.template?.title}>{`تاریخ: ${contract.contract_date}        شماره قرارداد: ${contract.id}`}</HeaderCard>
      <SectionCard title="متن کامل قرارداد">
        <p className="whitespace-pre-line p-2 text-sm leading-8 text-[#333]">{withoutHeader(contract.contract_text ?? '')}</p>
      </SectionCard>
    </>
  );
}

function HeaderCard({ title, children }: { title?: string; children: string }) {
  return (
    <Card className="m-[5px] flex flex-col items-center rounded-lg p-3">
      <Logo width={170} height={50} />
      <div className="mt-2 text-lg text-primary">{title || 'قرارداد همکاری'}</div>
      <div className="my-2 h-px w-full bg-[#e0e0e0]" />
      <div className="w-full whitespace-pre text-sm text-[#444]">{children}</div>
    </Card>
  );
}

/** Approved: free or paid, and how long is left; a warning in the last days. */
function ApprovedCard({ contract }: { contract: EContract }) {
  let text = `قرارداد شما در تاریخ ${contract.reviewed_at ?? ''} تایید و ثبت شد و حساب کاربری شما «کاربر پرو» است.`;
  text += contract.billing === 'paid' ? `\nنوع قرارداد: پولی (${contract.amount_formatted ?? ''} تومان)` : '\nنوع قرارداد: رایگان';
  text += `\nمدت قرارداد: از ${contract.start_date} تا ${contract.end_date}`;
  let color = '#388E3C';
  let title = 'قرارداد شما تایید شد';
  const daysLeft = contract.days_left;
  if (daysLeft !== null && daysLeft !== undefined) {
    if (daysLeft < 0) {
      color = '#757575';
      title = 'قرارداد شما به پایان رسیده است';
      text += '\n\nبرای تمدید قرارداد با ایران اپ تماس بگیرید.';
    } else if (daysLeft <= (contract.expiry_reminder_days ?? 15)) {
      color = '#E65100';
      title = daysLeft === 0 ? 'قرارداد شما امروز به پایان می رسد' : `قرارداد شما ${daysLeft} روز دیگر به پایان می رسد`;
      text += '\n\nبرای تمدید قرارداد با ایران اپ تماس بگیرید.';
    } else {
      text += `\n${daysLeft} روز تا پایان قرارداد باقی مانده است.`;
    }
  }
  return (
    <StatusCard color={color} title={title}>
      {text}
    </StatusCard>
  );
}

/** The amount, the card to pay to and the «هزینه را پرداخت کردم» button. */
function PaymentCard({ contract, reload }: { contract: EContract; reload: () => Promise<void> }) {
  const [reference, setReference] = useState('');
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setBusy(true);
    try {
      const result = await api.submitEContractPayment(toLatinDigits(reference.trim()));
      if (String(result.status) === '200') toast.success('پرداخت شما اعلام شد؛ پس از تایید، قرارداد ثبت نهایی می شود');
      else toast.failure(result.message ?? 'لطفا دوباره تلاش کنید');
      await reload();
    } catch {
      toast.failure('ارسال ممکن نشد؛ لطفا اتصال اینترنت خود را بررسی کنید');
    } finally {
      setBusy(false);
    }
  };

  return (
    <SectionCard title="پرداخت هزینه قرارداد" color="#1976D2">
      <Label>مبلغ قابل پرداخت</Label>
      <div className="mx-[5px] text-xl text-primary">{contract.amount_formatted} تومان</div>
      <Label>واریز به شماره کارت</Label>
      <button
        type="button"
        className="tap-light mx-[3px] flex w-[calc(100%-6px)] items-center rounded-[3px] border border-[#f44336] bg-[#f4f4f4] px-3 py-2"
        onClick={async () => {
          if (contract.card_number && (await copyText(contract.card_number))) toast.success('شماره کارت کپی شد');
        }}
      >
        <span className="flex-1 text-center text-[19px] text-[#222]" dir="ltr">
          {contract.card_number_formatted}
        </span>
        <span className="text-[13px] text-[#1976D2]">کپی</span>
      </button>
      <div className="mx-[5px] mt-1 text-[13px] text-mild">به نام: {contract.card_holder}</div>
      {contract.payment_message && <p className="mx-[5px] mt-2.5 whitespace-pre-line text-sm leading-7 text-[#444]">{contract.payment_message}</p>}
      {contract.payment_rejection_reason && (
        <p className="mx-[5px] mt-2.5 whitespace-pre-line bg-[#FDECEA] p-2.5 text-sm leading-7 text-primary">پرداخت قبلی شما تایید نشد: {contract.payment_rejection_reason}</p>
      )}
      <div className="mt-2.5">
        <TextField value={reference} onChange={setReference} maxLength={100} height={50} placeholder="شماره پیگیری واریز (اختیاری)" />
      </div>
      <div className="px-[5px]">
        <Button tone="teal" height={52} loading={busy} onClick={() => void submit()}>
          هزینه را پرداخت کردم
        </Button>
      </div>
    </SectionCard>
  );
}

/** A new contract, or the rejected one pre-filled for correction. */
function ContractForm({ data, previous, reload }: { data: EContractState; previous: EContract | null; reload: () => Promise<void> }) {
  const user = useSession((state) => state.user);
  const today = useMemo(() => jalali.parse(data.today) ?? jalali.today(), [data.today]);
  const [template, setTemplate] = useState<ContractTemplate | null>(data.template);
  const [templateNotice, setTemplateNotice] = useState<string | null>(null);
  const [pickingDate, setPickingDate] = useState(false);
  const [pickingDuration, setPickingDuration] = useState(false);
  const [busy, setBusy] = useState(false);
  const previewRef = useRef<HTMLDivElement>(null);

  const [form, setForm] = useState<FormState>(() => {
    // A start date that has passed while the request was being reviewed moves up to today.
    const previousStart = jalali.parse(previous?.start_date);
    return {
      businessType: previous?.business_type ?? '',
      businessName: previous?.business_name ?? '',
      managerTitle: previous?.manager_title ?? '',
      managerName: previous?.manager_name ?? '',
      nationalCode: previous?.national_code ?? '',
      phone: previous?.phone ?? '',
      mobile: previous?.mobile ?? user?.mobile ?? '',
      address: previous?.address ?? '',
      subject: previous?.subject ?? '',
      discount: previous ? String(previous.discount_percent) : '',
      duration: previous?.duration_months ?? 12,
      start: previousStart && jalali.compare(previousStart, today) > 0 ? previousStart : today,
      accepted: false,
    };
  });
  const set = (patch: Partial<FormState>) => setForm((current) => ({ ...current, ...patch }));
  const end = jalali.addMonths(form.start, form.duration);
  const labelOf = (options: Array<{ key: string; label: string }>, key: string) => options.find((option) => option.key === key)?.label ?? '';

  const values: Record<string, string> = {
    business_type: labelOf(data.options.business_types, form.businessType),
    business_name: form.businessName.trim(),
    manager_title: labelOf(data.options.manager_titles, form.managerTitle),
    manager_name: form.managerName.trim(),
    national_code: form.nationalCode,
    phone: form.phone,
    mobile: form.mobile,
    address: form.address.trim(),
    subject: form.subject.trim(),
    start_date: jalali.format(form.start),
    end_date: jalali.format(end),
    duration: `${form.duration} ماه`,
    discount_percent: form.discount,
  };

  /** Same rules as the server (EContractController::rules), so most mistakes are caught here. */
  const validate = (): string | null => {
    if (!form.businessType) return 'نوع کسب و کار (شرکت، موسسه، مجموعه یا فروشگاه) را انتخاب کنید';
    if (form.businessName.trim().length < 2) return 'نام شرکت / موسسه / مجموعه / فروشگاه را وارد کنید';
    if (!form.managerTitle) return 'عنوان مدیر (آقا یا خانم) را انتخاب کنید';
    if (form.managerName.trim().length < 3) return 'نام و نام خانوادگی مدیر را وارد کنید';
    if (!isValidNationalCode(form.nationalCode)) return 'کد ملی وارد شده معتبر نیست';
    if (!/^0\d{9,10}$/.test(form.phone)) return 'شماره تلفن ثابت را همراه با کد شهر وارد کنید';
    if (!/^09\d{9}$/.test(form.mobile)) return 'شماره همراه باید 11 رقم و با 09 شروع شود';
    if (form.address.trim().length < 10) return 'آدرس را کامل وارد کنید';
    if (form.subject.trim().length < 2) return 'خدمات / محصولات موضوع قرارداد را وارد کنید';
    const percent = Number.parseInt(form.discount, 10) || 0;
    if (percent < 1 || percent > 100) return 'درصد تخفیف باید بین 1 تا 100 باشد';
    if (!form.accepted) return 'برای ارسال قرارداد باید متن آن را مطالعه کرده و شرایط را بپذیرید';
    return null;
  };

  const submit = async () => {
    const error = validate();
    if (error) return toast.failure(error);
    setBusy(true);
    try {
      const result = await api.submitEContract({
        business_type: form.businessType,
        business_name: form.businessName.trim(),
        manager_title: form.managerTitle,
        manager_name: form.managerName.trim(),
        national_code: form.nationalCode,
        phone: form.phone,
        mobile: form.mobile,
        address: toLatinDigits(form.address.trim()),
        subject: toLatinDigits(form.subject.trim()),
        start_date: jalali.format(form.start),
        duration_months: form.duration,
        discount_percent: form.discount,
        template_version: template?.version ?? '',
        app_version: APP_VERSION,
      });
      const status = String(result.status);
      if (status === '201') {
        toast.success('درخواست قرارداد شما ارسال شد و پس از بررسی، نتیجه به شما اعلام می شود');
        await reload();
      } else if (status === '422') {
        await alertDialog((result.errors ?? []).map((line) => `• ${line}`).join('\n'), 'باشه', 'لطفا موارد زیر را اصلاح کنید');
      } else if (status === '409' && result.error === 'template_changed' && result.template) {
        // An admin edited the wording while the form was open: keep what was typed, show the
        // new text and ask for the terms to be accepted again.
        setTemplate(result.template);
        set({ accepted: false });
        setTemplateNotice(result.message || 'متن قرارداد به روز شده است. لطفا متن جدید را مطالعه کنید و دوباره شرایط را بپذیرید.');
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

  const options = (list: Array<{ key: string; label: string }>) => list.map((item) => ({ value: item.key, label: item.label }));

  return (
    <>
      {previous && (
        <StatusCard color={HIGHLIGHT} title="قرارداد شما نیاز به اصلاح دارد">
          {`دلیل: ${previous.rejection_reason ?? ''}\n\nلطفا اطلاعات قرارداد را بازبینی و اصلاح کنید و دوباره ارسال نمایید.`}
        </StatusCard>
      )}
      <HeaderCard title={template?.title}>{`تاریخ: ${data.today}`}</HeaderCard>

      <SectionCard title="مشخصات طرف دوم قرارداد">
        <Label>نوع کسب و کار</Label>
        <RadioRow value={form.businessType} onChange={(businessType) => set({ businessType })} options={options(data.options.business_types)} color="var(--color-primary)" />
        <TextField value={form.businessName} onChange={(businessName) => set({ businessName })} maxLength={200} height={50} placeholder="نام شرکت / موسسه / مجموعه / فروشگاه" />
        <Label>مدیریت</Label>
        <RadioRow value={form.managerTitle} onChange={(managerTitle) => set({ managerTitle })} options={options(data.options.manager_titles)} color="var(--color-primary)" />
        <TextField value={form.managerName} onChange={(managerName) => set({ managerName })} maxLength={200} height={50} placeholder="نام و نام خانوادگی مدیر" />
        <TextField value={form.nationalCode} onChange={(nationalCode) => set({ nationalCode })} mode="digits" maxLength={10} height={50} placeholder="کد ملی (10 رقم)" />
        <TextField value={form.phone} onChange={(phone) => set({ phone })} mode="digits" maxLength={11} height={50} placeholder="تلفن ثابت با کد شهر (مثلا 03132123456)" />
        <TextField value={form.mobile} onChange={(mobile) => set({ mobile })} mode="digits" maxLength={11} height={50} placeholder="شماره همراه (09xxxxxxxxx)" />
        <TextArea value={form.address} onChange={(address) => set({ address })} maxLength={1000} height={100} placeholder="آدرس کامل" />
      </SectionCard>

      <SectionCard title="موضوع، مدت و تخفیف">
        <TextField value={form.subject} onChange={(subject) => set({ subject })} maxLength={255} height={50} placeholder="خدمات / محصولات (مثلا: پوشاک مردانه)" />
        <Label>تاریخ شروع قرارداد</Label>
        <PickerBox onClick={() => setPickingDate(true)}>{jalali.format(form.start)}</PickerBox>
        <Label>مدت قرارداد</Label>
        <PickerBox onClick={() => setPickingDuration(true)}>{`${form.duration} ماه`}</PickerBox>
        <div className="mx-[5px] mt-1 mb-2 text-[13px] text-hot">تاریخ پایان قرارداد: {jalali.format(end)}</div>
        <TextField value={form.discount} onChange={(discount) => set({ discount })} mode="digits" maxLength={3} height={50} placeholder="درصد تخفیف برای کاربران ایران اپ (1 تا 100)" />
      </SectionCard>

      <div ref={previewRef}>
        <SectionCard title="متن کامل قرارداد">
          <div className="mx-2 mt-2 text-xs text-[#9e9e9e]">اطلاعاتی که وارد می کنید به رنگ قرمز در متن قرارداد نمایش داده می شود.</div>
          {template && <Preview template={template} values={values} />}
        </SectionCard>
      </div>

      <Checkbox checked={form.accepted} onChange={(accepted) => set({ accepted })} className="mx-2.5 mt-2.5 text-sm leading-6 text-[#444]">
        متن قرارداد را به طور کامل مطالعه کرده ام و با تمامی شرایط آن موافقم
      </Checkbox>
      <div className="mx-5 mt-2.5">
        <Button tone="teal" height={60} loading={busy} className="rounded-lg shadow-md" onClick={() => void submit()}>
          {previous ? 'ارسال مجدد درخواست قرارداد' : 'ارسال درخواست قرارداد'}
        </Button>
      </div>

      {pickingDate && (
        <JalaliDateDialog
          title="تاریخ شروع قرارداد"
          initial={form.start}
          minYear={today[0]}
          maxYear={today[0] + 1}
          onClose={() => setPickingDate(false)}
          onPick={(date) => {
            if (jalali.compare(date, today) < 0) return 'تاریخ شروع قرارداد نمی تواند پیش از امروز باشد';
            set({ start: date });
          }}
        />
      )}
      {pickingDuration && (
        <Modal onClose={() => setPickingDuration(false)} className="w-[78%]">
          <div className="px-5 pt-5 pb-2 text-base text-[#212121]">مدت قرارداد</div>
          {data.options.durations.map((months) => (
            <button
              key={months}
              type="button"
              className="tap-light block w-full px-5 py-3 text-right text-[15px]"
              onClick={() => {
                set({ duration: months });
                setPickingDuration(false);
              }}
            >
              {months} ماه
            </button>
          ))}
          <div className="h-2" />
        </Modal>
      )}
      {templateNotice && (
        <Modal className="w-[82%]">
          <div className="px-5 pt-5 text-base text-[#212121]">متن قرارداد به روز شد</div>
          <p className="px-5 pt-2 text-[15px] leading-7 text-[#555]">{templateNotice}</p>
          <div className="flex justify-end p-2" dir="ltr">
            <button
              type="button"
              className="tap-light rounded px-4 py-2.5 text-base text-primary"
              onClick={() => {
                setTemplateNotice(null);
                previewRef.current?.scrollIntoView({ behavior: 'smooth' });
              }}
            >
              مطالعه متن جدید
            </button>
          </div>
        </Modal>
      )}
    </>
  );
}
