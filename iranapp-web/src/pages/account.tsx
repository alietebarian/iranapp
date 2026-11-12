import { useState } from 'react';
import { useNavigate } from 'react-router';
import { mdiKeyVariant, mdiLock } from '@mdi/js';
import { PasswordField, TextField } from '@/components/fields';
import { Icon } from '@/components/Icon';
import { Button } from '@/components/ui';
import * as api from '@/lib/api';
import { useSession } from '@/store/session';
import { toast } from '@/store/ui';
import { AuthFrame } from './auth';

/** UpdateUserInfoActivity: change first and last name (the mobile number is fixed). */
export function EditAccountPage() {
  const navigate = useNavigate();
  const user = useSession((state) => state.user);
  const updateUser = useSession((state) => state.updateUser);
  const [firstName, setFirstName] = useState(user?.firstName ?? '');
  const [lastName, setLastName] = useState(user?.lastName ?? '');
  const [tried, setTried] = useState(false);
  const [lockedHint, setLockedHint] = useState(false);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setTried(true);
    const name = firstName.trim();
    const family = lastName.trim();
    if (!name || !family) return;
    setBusy(true);
    try {
      const result = await api.changeAccountInfo(name, family);
      if (String(result.status) === '204') {
        updateUser({ firstName: name, lastName: family });
        toast.success('اطلاعات شما با موفقیت تغییر کرد');
        navigate(-1);
      } else {
        toast.failure('لطفا دوباره تلاش کنید');
      }
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthFrame
      title="ویرایش اطلاعات"
      footer={
        <button
          type="button"
          className="tap flex h-14 shrink-0 items-center justify-center gap-2 text-base text-white pb-safe"
          style={{ background: 'radial-gradient(circle, #009688, #00796b)' }}
          onClick={() => navigate('/account/password')}
        >
          <Icon path={mdiKeyVariant} size={20} />
          تغییر رمز ورود
        </button>
      }
    >
      <TextField value={firstName} onChange={setFirstName} height={50} placeholder="نام" warn={tried && !firstName.trim() && 'لطفا نام خود را وارد کنید'} />
      <TextField value={lastName} onChange={setLastName} height={50} placeholder="نام خانوادگی" warn={tried && !lastName.trim() && 'لطفا نام خانوادگی خود را وارد کنید'} />
      <div onClick={() => setLockedHint(true)}>
        <TextField
          value={user?.mobile ?? ''}
          onChange={() => undefined}
          disabled
          height={50}
          mode="digits"
          placeholder="شماره همراه"
          trailing={<Icon path={mdiLock} size={20} />}
          warn={lockedHint && 'تغییر دادن شماره همراه امکان پذیر نیست'}
        />
      </div>
      <Button className="mt-1 mb-3" loading={busy} onClick={() => void submit()}>
        اعمال تغییرات
      </Button>
    </AuthFrame>
  );
}

/** UpdatePassword */
export function ChangePasswordPage() {
  const navigate = useNavigate();
  const [current, setCurrent] = useState('');
  const [next, setNext] = useState('');
  const [again, setAgain] = useState('');
  const [tried, setTried] = useState(false);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setTried(true);
    if (!current || next.length <= 5 || next !== again) return;
    setBusy(true);
    try {
      const result = await api.changePassword(current, next);
      if (String(result.status) === '204') {
        toast.success('رمز عبور شما با موفقیت تغییر کرد');
        navigate(-1);
      } else if (result.error === 'old_password_does_not_match') {
        toast.failure('کلمه عبور فعلی را اشتباه وارد کرده اید');
      } else {
        toast.failure('لطفا دوباره تلاش کنید');
      }
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthFrame title="تغییر رمز ورود">
      <PasswordField value={current} onChange={setCurrent} height={50} placeholder="کلمه عبور فعلی" warn={tried && !current && 'لطفا کلمه عبور فعلی خود را وارد کنید'} />
      <PasswordField value={next} onChange={setNext} height={50} placeholder="کلمه عبور جدید" autoComplete="new-password" warn={tried && next.length <= 5 && 'لطفا کلمه عبور جدید را وارد کنید (حد اقل 6 کاراکتر)'} />
      <PasswordField value={again} onChange={setAgain} height={50} placeholder="تکرار کلمه عبور جدید" autoComplete="new-password" warn={tried && next !== again && 'لطفا کلمه عبور جدید را دوباره وارد کنید'} onEnter={() => void submit()} />
      <Button className="mt-3 mb-3" loading={busy} onClick={() => void submit()}>
        اعمال تغییرات
      </Button>
    </AuthFrame>
  );
}
