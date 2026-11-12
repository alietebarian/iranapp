import { useState } from 'react';
import { useParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { RadioRow, SelectField } from '@/components/fields';
import { FormScreen } from '@/components/formParts';
import { EmptyNote } from '@/components/ui';
import * as api from '@/lib/api';
import { text } from '@/lib/format';
import { unstash } from '@/lib/hooks';
import { AGREEMENT_TYPES, EDUCATION_LEVELS, JOB_TYPES } from '@/lib/labels';
import type { Job } from '@/lib/types';
import { ClassifiedForm } from './ClassifiedForm';

/** Add_New_Job / Edit_Job: post or edit a job ad (hiring or seeking work). */
export default function JobFormPage() {
  const { id } = useParams();
  const job = id ? unstash<Job>('job', id) : null;
  if (id && !job) {
    return (
      <FormScreen>
        <EmptyNote>این آگهی در دسترس نیست. لطفا آن را دوباره از محیط کاربری باز کنید.</EmptyNote>
      </FormScreen>
    );
  }
  return <JobForm key={id ?? 'new'} job={job} />;
}

function JobForm({ job }: { job: Job | null }) {
  const [type, setType] = useState(text(job?.type));
  const [education, setEducation] = useState(text(job?.education_level));
  const [specialtyId, setSpecialtyId] = useState(text(job?.specialty_id));
  const [agreement, setAgreement] = useState(text(job?.agremment_type));
  const [advertiser, setAdvertiser] = useState(job?.person_or_company === 'company' ? 'company' : 'person');
  const specialities = useQuery({ queryKey: ['specialities'], queryFn: api.getSpecialities, staleTime: Infinity });

  const validate = (): string | null => {
    if (!type) return 'لطفا در انتخاب نوع آگهی دقت فرمایید';
    if (!education) return 'لطفا در انتخاب میزان تحصیلات دقت فرمایید';
    if (!specialtyId) return 'لطفا در انتخاب تخصص دقت فرمایید';
    if (!agreement) return 'لطفا در انتخاب نوع قرارداد دقت فرمایید.';
    return null;
  };

  const append = (form: FormData) => {
    form.append('type', type);
    form.append('education_level', education);
    form.append('specialty', specialtyId);
    form.append('agremment_type', agreement);
    form.append('person_or_company', advertiser);
  };

  return (
    <ClassifiedForm
      item={job}
      photosTitle="تصاویر آگهی (اختیاری)"
      titleHint="عنوان آگهی ( اجباری )"
      descriptionHint="توضیحات آگهی ( اجباری )"
      mapLabel="نشان کردن روی نقشه ( اختیاری )"
      deletePhotoKey="photo_to_delete"
      deleteThumbnailKey="delete_thumbnail"
      loading={specialities.isLoading}
      validate={validate}
      append={append}
      send={(form) => (job ? api.updateJob(job.id, form) : api.saveJob(form))}
    >
      <SelectField label="آماده به کار / استخدام ( اجباری )" value={type} options={JOB_TYPES} onChange={setType} />
      <SelectField label="میزان تحصیلات ( اجباری )" value={education} options={EDUCATION_LEVELS} onChange={setEducation} />
      <SelectField
        label="تخصص ( اجباری )"
        value={specialtyId}
        disabled={!specialities.data}
        options={(specialities.data ?? []).map((s) => ({ value: String(s.id), label: s.name }))}
        onChange={setSpecialtyId}
      />
      <SelectField label="نوع قرارداد ( اجباری )" value={agreement} options={AGREEMENT_TYPES} onChange={setAgreement} />
      <RadioRow
        label="آگهی دهنده ( اجباری )"
        value={advertiser}
        onChange={setAdvertiser}
        options={[
          { value: 'person', label: 'فرد' },
          { value: 'company', label: 'شرکت/فروشگاه' },
        ]}
      />
    </ClassifiedForm>
  );
}
