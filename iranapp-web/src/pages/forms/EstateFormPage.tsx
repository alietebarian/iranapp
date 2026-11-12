import { useState } from 'react';
import { useParams } from 'react-router';
import { RadioRow, SelectField, TextField } from '@/components/fields';
import { FormScreen } from '@/components/formParts';
import { EmptyNote } from '@/components/ui';
import * as api from '@/lib/api';
import { blank, text } from '@/lib/format';
import { unstash } from '@/lib/hooks';
import { ESTATE_CATEGORIES, ESTATE_SUB_CATEGORIES, estateGroup, ROOM_COUNTS } from '@/lib/labels';
import type { Estate } from '@/lib/types';
import { ClassifiedForm } from './ClassifiedForm';

/** Add_New_Home_Add / Edit_Home_Add: post or edit an estate ad. */
export default function EstateFormPage() {
  const { id } = useParams();
  const estate = id ? unstash<Estate>('estate', id) : null;
  if (id && !estate) {
    return (
      <FormScreen>
        <EmptyNote>این آگهی در دسترس نیست. لطفا آن را دوباره از محیط کاربری باز کنید.</EmptyNote>
      </FormScreen>
    );
  }
  return <EstateForm key={id ?? 'new'} estate={estate} />;
}

type AmountKind = 'fixed' | 'negotiable' | 'special';

/** An amount is stored as a number, 0 (negotiable) or -1 (the third radio: exchange / free). */
function readAmount(value: unknown): { kind: AmountKind; amount: string } {
  const raw = blank(value) ? '0' : String(value);
  if (raw === '-1') return { kind: 'special', amount: '' };
  if (raw === '0') return { kind: 'negotiable', amount: '' };
  return { kind: 'fixed', amount: raw };
}

const writeAmount = (kind: AmountKind, amount: string) => (kind === 'fixed' ? amount : kind === 'negotiable' ? '0' : '-1');

function AmountField({ label, special, hint, kind, amount, onKind, onAmount }: { label: string; special: string; hint: string; kind: AmountKind; amount: string; onKind: (kind: AmountKind) => void; onAmount: (amount: string) => void }) {
  return (
    <>
      <RadioRow
        label={label}
        value={kind}
        onChange={(value) => onKind(value as AmountKind)}
        options={[
          { value: 'negotiable', label: 'توافقی' },
          { value: 'fixed', label: 'مقطوع' },
          { value: 'special', label: special },
        ]}
      />
      <TextField value={kind === 'fixed' ? amount : ''} onChange={onAmount} mode="digits" money maxLength={15} disabled={kind !== 'fixed'} placeholder={kind === 'fixed' ? `${hint} (تومان)` : `${hint} (${kind === 'negotiable' ? 'توافقی' : special})`} />
    </>
  );
}

function EstateForm({ estate }: { estate: Estate | null }) {
  const [group, setGroup] = useState(estate ? estateGroup(estate) : '');
  const [subCategory, setSubCategory] = useState(estate ? String(estate.category_id) : '');
  const [meters, setMeters] = useState(estate && String(estate.meters) !== '0' ? text(estate.meters) : '');
  const [price, setPrice] = useState(() => readAmount(estate?.price_kharid));
  const [deposit, setDeposit] = useState(() => readAmount(estate?.pre_pay_ejare));
  const [rent, setRent] = useState(() => readAmount(estate?.monthly_price_ejare));
  const [rooms, setRooms] = useState(estate && !blank(estate.rooms_count) ? String(estate.rooms_count) : '');
  const [userType, setUserType] = useState(estate?.user_type === 'moshaver_amlak' ? 'moshaver_amlak' : 'person');
  const [hasDeed, setHasDeed] = useState(String(estate?.sanad_edari) === '1' ? '1' : '0');
  const [sellOrBuy, setSellOrBuy] = useState(estate?.sell_or_buy === 'buy' ? 'buy' : 'sell');
  const [inSuburb, setInSuburb] = useState(String(estate?.is_in_hoome) === '1' ? '1' : '0');

  const sale = group === '1' || group === '3';
  const lease = group === '2' || group === '4';
  // Land («زمین و کلنگی») has no rooms.
  const needsRooms = (sale || lease) && subCategory !== '8';

  const validate = (): string | null => {
    if (!group) return 'لطفا در انتخاب دسته بندی دقت فرمایید';
    if (!subCategory) return 'لطفا در انتخاب زیر دسته دقت فرمایید';
    if (sale) {
      if (price.kind === 'fixed' && price.amount.length === 0) return 'لطفا در وارد نمودن قیمت کل دقت فرمایید.';
      if (meters.length === 0) return 'لطفا در وارد نمودن متراژ دقت نمایید.';
    }
    if (lease) {
      if (deposit.kind === 'fixed' && deposit.amount.length === 0) return 'لطفا در وارد نمودن مبلغ ودیعه دقت فرمایید.';
      if (rent.kind === 'fixed' && rent.amount.length === 0) return 'لطفا در وارد نمودن مبلغ اجاره دقت فرمایید.';
    }
    if (needsRooms && rooms === '') return 'لطفا در انتخاب تعداد اتاق دقت نمایید.';
    if (lease && meters.length === 0) return 'لطفا در وارد نمودن متراژ دقت نمایید.';
    return null;
  };

  const append = (form: FormData) => {
    form.append('category_id', subCategory);
    form.append('ejare_or_kharid', group === '4' ? 'ejare' : 'kharid');
    form.append('type_karbari', 'maskooni');
    form.append('user_type', userType);
    form.append('sell_or_buy', sellOrBuy);
    if (sale || lease) {
      form.append('rooms_count', needsRooms ? rooms : '0');
      form.append('meters', meters);
      form.append('is_in_hoome', inSuburb);
    }
    if (sale) form.append('price_kharid', writeAmount(price.kind, price.amount));
    if (lease) {
      form.append('pre_pay_ejare', writeAmount(deposit.kind, deposit.amount));
      form.append('monthly_price_ejare', writeAmount(rent.kind, rent.amount));
    }
    // The update endpoint treats the mere presence of this field as "has a deed".
    if (group === '3' && (hasDeed === '1' || !estate)) form.append('sanad_edari', hasDeed);
  };

  return (
    <ClassifiedForm
      item={estate}
      photosTitle="تصاویر آگهی"
      titleHint="عنوان آگهی (ملک، آزادی ،۲۰۰ متر)"
      descriptionHint="توضیحات آگهی"
      mapLabel="نشان کردن روی نقشه"
      deletePhotoKey="delete_photo"
      deleteThumbnailKey="delete_thumbnail_photo"
      validate={validate}
      append={append}
      send={(form) => (estate ? api.updateEstate(estate.id, form) : api.saveEstate(form))}
    >
      <SelectField
        label="دسته ها"
        value={group}
        options={ESTATE_CATEGORIES}
        onChange={(value) => {
          setGroup(value);
          setSubCategory('');
        }}
      />
      <SelectField label="زیر دسته" value={subCategory} disabled={!group} options={ESTATE_SUB_CATEGORIES[group] ?? []} onChange={setSubCategory} />

      {(sale || lease) && <TextField value={meters} onChange={setMeters} mode="digits" maxLength={7} placeholder="متراژ (متر مربع)" />}
      {sale && <AmountField label="نوع قیمت" special="جهت معاوضه" hint="قیمت کل" kind={price.kind} amount={price.amount} onKind={(kind) => setPrice({ ...price, kind })} onAmount={(amount) => setPrice({ ...price, amount })} />}
      {lease && (
        <>
          <AmountField label="نوع ودیعه :" special="مجانی" hint="ودیعه" kind={deposit.kind} amount={deposit.amount} onKind={(kind) => setDeposit({ ...deposit, kind })} onAmount={(amount) => setDeposit({ ...deposit, amount })} />
          <AmountField label="نوع اجاره :" special="مجانی" hint="اجاره" kind={rent.kind} amount={rent.amount} onKind={(kind) => setRent({ ...rent, kind })} onAmount={(amount) => setRent({ ...rent, amount })} />
        </>
      )}
      {needsRooms && <SelectField label="تعداد اتاق" value={rooms} options={ROOM_COUNTS} onChange={setRooms} />}

      <RadioRow
        label="نوع آگهی دهنده"
        value={userType}
        onChange={setUserType}
        options={[
          { value: 'person', label: 'شخصی' },
          { value: 'moshaver_amlak', label: 'مشاوره املاک' },
        ]}
      />
      {group === '3' && (
        <RadioRow
          label="سند اداری"
          value={hasDeed}
          onChange={setHasDeed}
          options={[
            { value: '1', label: 'دارد' },
            { value: '0', label: 'ندارد' },
          ]}
        />
      )}
      {(sale || lease) && (
        <>
          <RadioRow
            label="نوع آگهی"
            value={sellOrBuy}
            onChange={setSellOrBuy}
            options={[
              { value: 'sell', label: sale ? 'فروشی' : 'ارائه' },
              { value: 'buy', label: 'درخواستی' },
            ]}
          />
          <RadioRow
            label="حومه شهر"
            value={inSuburb}
            onChange={setInSuburb}
            options={[
              { value: '0', label: 'نیست' },
              { value: '1', label: 'هست' },
            ]}
          />
        </>
      )}
    </ClassifiedForm>
  );
}
