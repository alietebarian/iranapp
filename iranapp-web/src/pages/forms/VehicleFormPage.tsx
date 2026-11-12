import { useState } from 'react';
import { useParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { RadioRow, SelectField, TextField } from '@/components/fields';
import { FormScreen } from '@/components/formParts';
import { EmptyNote } from '@/components/ui';
import * as api from '@/lib/api';
import { blank, text } from '@/lib/format';
import { unstash } from '@/lib/hooks';
import { CHASSIS_TYPES, VEHICLE_TYPES } from '@/lib/labels';
import type { Vehicle } from '@/lib/types';
import { ClassifiedForm } from './ClassifiedForm';

/** Add_New_Car_Add / Edite_Car_Add: post or edit a vehicle ad. */
export default function VehicleFormPage() {
  const { id } = useParams();
  const vehicle = id ? unstash<Vehicle>('vehicle', id) : null;
  if (id && !vehicle) {
    return (
      <FormScreen>
        <EmptyNote>این آگهی در دسترس نیست. لطفا آن را دوباره از محیط کاربری باز کنید.</EmptyNote>
      </FormScreen>
    );
  }
  return <VehicleForm key={id ?? 'new'} vehicle={vehicle} />;
}

function VehicleForm({ vehicle }: { vehicle: Vehicle | null }) {
  const [type, setType] = useState<string>(vehicle?.type ?? '');
  const [brandId, setBrandId] = useState(text(vehicle?.brand_id));
  const [modelId, setModelId] = useState(text(vehicle?.model_id));
  const [chassis, setChassis] = useState(text(vehicle?.chassis_type));
  const [cylinderId, setCylinderId] = useState(text(vehicle?.cylinder_volume_id));
  const [year, setYear] = useState(text(vehicle?.production_year));
  const [kilometre, setKilometre] = useState(text(vehicle?.kilometre));
  const initialPrice = blank(vehicle?.price) || String(vehicle?.price) === '0' ? '' : String(vehicle?.price);
  const [priceKind, setPriceKind] = useState(initialPrice ? 'fixed' : 'negotiable');
  const [price, setPrice] = useState(initialPrice);
  const [advertiser, setAdvertiser] = useState(vehicle?.person_or_company === 'company' ? 'company' : 'person');
  const [condition, setCondition] = useState(vehicle?.neworold === 'new' ? 'new' : 'old');

  const isCar = type === 'khodro';
  const isMotorcycle = type === 'motorcycle';
  const brands = useQuery({ queryKey: ['brands'], queryFn: api.getBrands, staleTime: Infinity });
  const models = useQuery({ queryKey: ['models', brandId], queryFn: () => api.getModels(brandId), enabled: isCar && brandId !== '', staleTime: Infinity });
  const cylinders = useQuery({ queryKey: ['cylinders'], queryFn: api.getCylinderVolumes, staleTime: Infinity });
  const hasModels = (models.data?.length ?? 0) > 0;

  const validate = (): string | null => {
    if (!type) return 'لطفا در انتخاب دسته بندی دقت فرمایید';
    if (isCar && !brandId) return 'لطفا در وارد کردن برند خودرو دقت فرمایید';
    if (isCar && hasModels && !modelId) return 'لطفا در انتخاب مدل خودرو دقت فرمایید';
    if (isCar && !chassis) return 'لطفا در انتخاب نوع شاسی دقت فرمایید.';
    if (isCar && year.length === 0) return 'لطفا در وارد کردن سال تولید دقت فرمایید';
    if (isCar && kilometre.length === 0) return 'لطفا در وارد کردن کیلومتر دقت فرمایید';
    if (isMotorcycle && !cylinderId) return 'لطفا در انتخاب حجم موتور دقت فرمایید.';
    if (isMotorcycle && year.length !== 4) return 'لطفا در وارد نمودن سال تولید دقت فرمایید.';
    if (priceKind === 'fixed' && price.length === 0) return 'لطفا در وارد کردن قیمت دقت فرمایید';
    return null;
  };

  const append = (form: FormData) => {
    form.append('type', type);
    form.append('person_or_company', advertiser);
    form.append('price', priceKind === 'fixed' ? price : '0');
    form.append('neworold', condition);
    if (isCar) {
      form.append('brand', brandId);
      if (modelId) form.append('model', modelId);
      form.append('production_year', year);
      form.append('kilometre', kilometre);
      form.append('chassis_type', chassis);
    } else if (isMotorcycle) {
      form.append('cylinder_volume', cylinderId);
      form.append('production_year', year);
    }
  };

  return (
    <ClassifiedForm
      item={vehicle}
      photosTitle="تصاویر آگهی"
      titleHint="عنوان آگهی"
      descriptionHint="توضیحات آگهی"
      mapLabel="نشان کردن روی نقشه"
      deletePhotoKey="delete_photo"
      deleteThumbnailKey="delete_thumbnail_photo"
      loading={brands.isLoading || models.isFetching || cylinders.isLoading}
      validate={validate}
      append={append}
      send={(form) => (vehicle ? api.updateVehicle(vehicle.id, form) : api.saveVehicle(form))}
    >
      <SelectField label="دسته ها" value={type} options={VEHICLE_TYPES} onChange={setType} />
      {isCar && (
        <>
          <SelectField
            label="برند خودرو"
            value={brandId}
            disabled={!brands.data}
            options={(brands.data ?? []).map((b) => ({ value: String(b.id), label: b.name }))}
            onChange={(value) => {
              setBrandId(value);
              setModelId('');
            }}
          />
          {hasModels && <SelectField label="مدل خودرو" value={modelId} options={(models.data ?? []).map((m) => ({ value: String(m.id), label: m.name }))} onChange={setModelId} />}
          <SelectField label="نوع شاسی" value={chassis} options={CHASSIS_TYPES} onChange={setChassis} />
        </>
      )}
      {isMotorcycle && <SelectField label="حجم موتور" value={cylinderId} disabled={!cylinders.data} options={(cylinders.data ?? []).map((c) => ({ value: String(c.id), label: c.value }))} onChange={setCylinderId} />}
      {(isCar || isMotorcycle) && <TextField value={year} onChange={setYear} mode="digits" maxLength={4} placeholder="سال تولید (4 رقمی)" />}
      {isCar && <TextField value={kilometre} onChange={setKilometre} mode="digits" maxLength={9} placeholder="کیلومتر" />}

      <RadioRow
        label="نوع قیمت"
        value={priceKind}
        onChange={setPriceKind}
        options={[
          { value: 'negotiable', label: 'توافقی' },
          { value: 'fixed', label: 'مقطوع' },
        ]}
      />
      <TextField value={priceKind === 'fixed' ? price : ''} onChange={setPrice} mode="digits" money maxLength={15} disabled={priceKind !== 'fixed'} placeholder={priceKind === 'fixed' ? 'قیمت (تومان)' : 'توافقی'} />
      <RadioRow
        label="آگهی دهنده"
        value={advertiser}
        onChange={setAdvertiser}
        options={[
          { value: 'person', label: 'شخصی' },
          { value: 'company', label: 'شرکت/فروشگاه' },
        ]}
      />
      <RadioRow
        value={condition}
        onChange={setCondition}
        options={[
          { value: 'new', label: 'نو' },
          { value: 'old', label: 'دست دوم' },
        ]}
      />
    </ClassifiedForm>
  );
}
