import { useCallback, useEffect, useState, type ReactNode } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router';
import useEmblaCarousel from 'embla-carousel-react';
import { mdiKeyVariant } from '@mdi/js';
import intro1 from '@/assets/img/intro1.jpg';
import intro2 from '@/assets/img/intro2.jpg';
import intro3 from '@/assets/img/intro3.jpg';
import userUrl from '@/assets/img/user1.png';
import { ChangeMobileDialog, ForgotPasswordDialog, JalaliDateDialog } from '@/components/dialogs';
import { PasswordField, TextField } from '@/components/fields';
import { Icon, MaskIcon } from '@/components/Icon';
import { Button, Logo } from '@/components/ui';
import * as api from '@/lib/api';
import { cx } from '@/lib/format';
import * as jalali from '@/lib/jalali';
import { useSession } from '@/store/session';
import { openRules, toast } from '@/store/ui';

function useReturnPath(): string {
  const location = useLocation();
  const from = (location.state as { from?: string } | null)?.from;
  return from && !/^\/(intro|welcome|login|signup|confirm)/.test(from) ? from : '/';
}

// ---------------------------------------------------------------- intro

const INTRO_PAGES = [intro1, intro2, intro3];

/** IntroActivity: three full-screen pictures shown once to a new user. */
export function IntroPage() {
  const navigate = useNavigate();
  const setIntroSeen = useSession((state) => state.setIntroSeen);
  const [emblaRef, embla] = useEmblaCarousel({ direction: 'rtl' });
  const [index, setIndex] = useState(0);
  const last = index === INTRO_PAGES.length - 1;

  useEffect(() => {
    if (!embla) return;
    const onSelect = () => setIndex(embla.selectedScrollSnap());
    embla.on('select', onSelect);
    return () => {
      embla.off('select', onSelect);
    };
  }, [embla]);

  const next = () => {
    if (!last) return embla?.scrollNext();
    setIntroSeen();
    navigate('/', { replace: true });
  };

  return (
    <div className="relative h-full bg-[#0a1045]">
      <div className="h-full overflow-hidden" ref={emblaRef} dir="rtl">
        <div className="flex h-full">
          {INTRO_PAGES.map((src) => (
            <img key={src} src={src} alt="" className="h-full w-full shrink-0 object-cover" />
          ))}
        </div>
      </div>
      <button
        type="button"
        className="tap absolute top-4 left-4 flex h-[35px] min-w-[120px] items-center justify-center rounded-[15px] px-4 text-base text-white shadow"
        style={{ background: 'rgb(183 28 28 / 0.78)', top: 'calc(16px + env(safe-area-inset-top))' }}
        onClick={next}
      >
        {last ? 'ورود / ثبت نام' : 'بعدی'}
      </button>
      <div className="absolute inset-x-0 bottom-10 flex justify-center gap-2">
        {INTRO_PAGES.map((src, i) => (
          <span key={src} className={cx('h-2.5 w-2.5 rounded-full border border-white', i === index ? 'bg-white' : 'bg-transparent')} />
        ))}
      </div>
    </div>
  );
}

// ---------------------------------------------------------------- welcome

/** SelectLoginOrRegiser: the two buttons a guest sees after the splash. */
export function WelcomePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const user = useSession((state) => state.user);
  if (user?.verified) return <Navigate to="/" replace />;
  return (
    <div className="animate-page-in flex h-full flex-col items-center justify-center bg-white">
      <Logo width={240} height={85} />
      <div className="mt-[60px] w-full px-5">
        <button type="button" className="tap h-[55px] w-full rounded-[3px] bg-primary text-xl text-white shadow" onClick={() => navigate('/login', { state: location.state })}>
          ورود به ایران آپ
        </button>
        <button type="button" className="tap mt-2.5 h-[55px] w-full rounded-[3px] bg-primary text-xl text-white shadow" onClick={() => navigate('/signup', { state: location.state })}>
          ثبت نام
        </button>
      </div>
    </div>
  );
}

// ---------------------------------------------------------------- shared frame

/** The red glow behind the white card that login, sign-up, confirmation and account pages share. */
export function AuthFrame({ title, avatar, children, footer }: { title: string; avatar?: boolean; children: ReactNode; footer?: ReactNode }) {
  return (
    <div className="animate-page-in flex h-full flex-col bg-[#eee]">
      <div className="relative flex-1 overflow-y-auto">
        <div className="absolute inset-x-0 top-0 bg-gradient-red shadow-md" style={{ height: avatar ? 'calc(var(--sdp) * 340)' : 'calc(var(--sdp) * 290)' }} />
        <div className="relative flex flex-col items-center pt-safe">
          {avatar && (
            <div className="mt-[calc(var(--sdp)*20)] flex h-[130px] w-[130px] items-center justify-center rounded-full bg-primary">
              <MaskIcon src={userUrl} size={90} color="#fff" />
            </div>
          )}
          <h1 className={cx('px-4 text-center text-xl font-normal text-white', avatar ? 'mt-4' : 'mt-5')}>{title}</h1>
          <div className="mx-2.5 mt-5 mb-4 w-[calc(100%-20px)] rounded-md bg-white px-[5px] pt-2.5 pb-1 shadow">{children}</div>
        </div>
      </div>
      {footer}
    </div>
  );
}

// ---------------------------------------------------------------- login

export function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const returnPath = useReturnPath();
  const signIn = useSession((state) => state.signIn);
  const pushToken = useSession((state) => state.pushToken);
  const [mobile, setMobile] = useState('');
  const [password, setPassword] = useState('');
  const [tried, setTried] = useState(false);
  const [busy, setBusy] = useState(false);
  const [forgot, setForgot] = useState(false);

  const submit = async () => {
    setTried(true);
    if (mobile.length !== 11 || password.length <= 5) return;
    setBusy(true);
    try {
      const result = await api.login(mobile, password, pushToken);
      if (String(result.status) === '200' && result.user) {
        signIn(result.user, mobile);
        toast.success('شما وارد حسابتان شدید');
        const verified = String(result.user.is_mobile_verified) === '1';
        navigate(verified ? returnPath : '/confirm', { replace: true, state: location.state });
      } else if (String(result.status) === '401') {
        toast.failure('شماره یا رمز خود را اشتباه وارد کرده اید');
        setPassword('');
        setTried(false);
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
    <AuthFrame title="اگر قبلا ثبت نام کرده اید ، وارد شوید" avatar>
      <TextField value={mobile} onChange={setMobile} mode="digits" maxLength={11} height={50} placeholder="شماره همراه" autoComplete="username" warn={tried && mobile.length !== 11 && 'لطفا شماره همراه خود را وارد کنید'} />
      <PasswordField value={password} onChange={setPassword} height={50} placeholder="کلمه عبور" warn={tried && password.length <= 5 && 'لطفا کلمه عبور را وارد کنید'} onEnter={() => void submit()} />
      <Button className="mt-4" loading={busy} onClick={() => void submit()}>
        ورود
      </Button>
      <div className="mt-1 flex items-center justify-between py-2 pr-1">
        <button type="button" className="flex items-center gap-1 text-sm text-[#009688]" onClick={() => setForgot(true)}>
          <Icon path={mdiKeyVariant} size={20} />
          رمز عبور خود را فراموش کرده اید؟
        </button>
        <button type="button" className="px-2 text-sm text-[#009688]" onClick={() => navigate('/signup', { replace: true, state: location.state })}>
          ثبت نام
        </button>
      </div>
      {forgot && <ForgotPasswordDialog onClose={() => setForgot(false)} />}
    </AuthFrame>
  );
}

// ---------------------------------------------------------------- sign up

export function SignUpPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const setUnverifiedUser = useSession((state) => state.setUnverifiedUser);
  const pushToken = useSession((state) => state.pushToken);
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [birthDate, setBirthDate] = useState<jalali.JDate | null>(null);
  const [mobile, setMobile] = useState('');
  const [password, setPassword] = useState('');
  const [again, setAgain] = useState('');
  const [tried, setTried] = useState(false);
  const [busy, setBusy] = useState(false);
  const [picking, setPicking] = useState(false);
  const today = jalali.today();

  const name = firstName.trim();
  const family = lastName.trim();
  const valid = name.length > 2 && family.length > 2 && birthDate !== null && mobile.length === 11 && password.length > 5 && password === again;

  const submit = async () => {
    setTried(true);
    if (!valid || !birthDate) return;
    setBusy(true);
    try {
      const result = await api.register({ firstName: name, lastName: family, birthDate: jalali.format(birthDate), mobile, password, pushToken });
      if (String(result.status) === '204') {
        setUnverifiedUser({ id: String(result.user_id ?? ''), firstName: name, lastName: family, mobile });
        toast.success('به زودی کد فعال سازی برای شما ارسال خواهد شد.');
        navigate('/confirm', { replace: true, state: location.state });
      } else if (String(result.status) === '400') {
        const errors = result.errors;
        if (!errors || errors.mobile) toast.failure('شما قبلا با این شماره ثبت نام کرده اید ، لطفا وارد حساب کاربری خود شوید');
        else toast.failure(Object.values(errors)[0]?.[0] ?? 'لطفا دوباره تلاش کنید');
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
    <AuthFrame title="ثبت نام کنید">
      <TextField value={firstName} onChange={setFirstName} height={50} placeholder="نام" autoComplete="given-name" warn={tried && name.length <= 2 && 'لطفا نام خود را وارد کنید'} />
      <TextField value={lastName} onChange={setLastName} height={50} placeholder="نام خانوادگی" autoComplete="family-name" warn={tried && family.length <= 2 && 'لطفا نام خانوادگی خود را وارد کنید'} />
      <div className="mb-2.5">
        {tried && !birthDate && <div className="mb-0.5 px-1 text-left text-[11px] text-warn">لطفا تاریخ تولد خود را انتخاب کنید</div>}
        <button
          type="button"
          className={cx('h-[50px] w-full rounded-[3px] border border-[#f44336] bg-[#f4f4f4] px-2.5 text-right text-base', birthDate ? 'text-[#333]' : 'text-[#9e9e9e]')}
          onClick={() => setPicking(true)}
        >
          {birthDate ? jalali.formatLong(birthDate) : 'تاریخ تولد'}
        </button>
      </div>
      <TextField value={mobile} onChange={setMobile} mode="digits" maxLength={11} height={50} placeholder="شماره همراه" autoComplete="tel" warn={tried && mobile.length !== 11 && 'لطفا شماره همراه خود را وارد کنید'} />
      <PasswordField value={password} onChange={setPassword} height={50} placeholder="کلمه عبور (حد اقل 6 کاراکتر)" autoComplete="new-password" warn={tried && password.length <= 5 && 'لطفا کلمه عبور را وارد کنید'} />
      <PasswordField value={again} onChange={setAgain} height={50} placeholder="تکرار کلمه عبور" autoComplete="new-password" warn={tried && password !== again && 'لطفا کلمه عبور را دوباره وارد کنید'} onEnter={() => void submit()} />
      <Button className="mt-3" loading={busy} onClick={() => void submit()}>
        ثبت نام
      </Button>
      <div className="flex items-center justify-between py-2 pr-1">
        <button type="button" className="text-sm text-[#009688]" onClick={openRules}>
          قوانین و مقررات
        </button>
        <button type="button" className="px-2 text-sm text-[#009688]" onClick={() => navigate('/login', { replace: true, state: location.state })}>
          ورود
        </button>
      </div>
      {picking && (
        <JalaliDateDialog
          title="تاریخ تولد"
          initial={birthDate ?? [today[0] - 25, 1, 1]}
          minYear={1300}
          maxYear={today[0]}
          onClose={() => setPicking(false)}
          onPick={(date) => {
            if (jalali.compare(date, today) >= 0) return 'تاریخ تولد باید پیش از امروز باشد';
            setBirthDate(date);
          }}
        />
      )}
    </AuthFrame>
  );
}

// ---------------------------------------------------------------- confirmation

const CODE_LENGTH = 4;
const RESEND_SECONDS = 30;

/** ConfirmationActivity: enter the SMS code; «پیامک دریافت نکردید؟» unlocks after 30 seconds. */
export function ConfirmPage() {
  const navigate = useNavigate();
  const returnPath = useReturnPath();
  const user = useSession((state) => state.user);
  const signIn = useSession((state) => state.signIn);
  const signOut = useSession((state) => state.signOut);
  const [code, setCode] = useState('');
  const [busy, setBusy] = useState(false);
  const [seconds, setSeconds] = useState(RESEND_SECONDS);
  const [changing, setChanging] = useState(false);
  const mobile = user?.mobile ?? '';

  useEffect(() => {
    if (seconds <= 0) return;
    const timer = window.setTimeout(() => setSeconds((s) => s - 1), 1000);
    return () => window.clearTimeout(timer);
  }, [seconds]);

  const submit = useCallback(
    async (value: string) => {
      if (value.length !== CODE_LENGTH) return toast.failure('لطفا در وارد نمودن کد دقت فرمایید');
      setBusy(true);
      try {
        const result = await api.verifyAndLogin(mobile, value);
        if (String(result.status) === '200' && result.user) {
          signIn(result.user, mobile);
          toast.success('شما وارد حسابتان شدید');
          navigate(returnPath, { replace: true });
        } else if (String(result.status) === '401') {
          toast.failure('کد را اشتباه وارد کرده اید');
        } else {
          toast.failure('لطفا دوباره تلاش کنید');
        }
      } catch {
        toast.failure('لطفا دوباره تلاش کنید');
      } finally {
        setBusy(false);
      }
    },
    [mobile, navigate, returnPath, signIn],
  );

  const resend = async () => {
    if (seconds > 0) return;
    setSeconds(RESEND_SECONDS);
    toast.success('به زودی کد فعال سازی جدید برای شما ارسال خواهد شد.');
    try {
      await api.resendVerifyCode(mobile);
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    }
  };

  if (!user) return <Navigate to="/welcome" replace />;
  if (user.verified && useSession.getState().token) return <Navigate to={returnPath} replace />;

  return (
    <AuthFrame
      title="تایید شماره"
      avatar
      footer={
        <button
          type="button"
          disabled={seconds > 0}
          className="flex h-14 shrink-0 items-center justify-center gap-3 text-white pb-safe"
          style={{ background: seconds > 0 ? 'radial-gradient(circle, #595959, #454545)' : 'radial-gradient(circle, #009688, #00796b)' }}
          onClick={() => void resend()}
        >
          <span>پیامک دریافت نکردید؟</span>
          {seconds > 0 && <span>{seconds}</span>}
        </button>
      }
    >
      <p className="mt-2 mb-3 text-center text-sm text-[#333]">
        لطفا کد فرستاده شده به شماره <span dir="ltr">{mobile}</span> را در کادر زیر وارد کنید
      </p>
      <TextField
        value={code}
        onChange={(value) => {
          setCode(value);
          // The SMS autofill of the phone fills the whole code at once: send it straight away.
          if (value.length === CODE_LENGTH && !busy) void submit(value);
        }}
        mode="digits"
        maxLength={CODE_LENGTH}
        height={50}
        placeholder="کد فعال سازی"
        autoComplete="one-time-code"
        onEnter={() => void submit(code)}
      />
      <Button className="mt-3" loading={busy} onClick={() => void submit(code)}>
        تایید
      </Button>
      <div className="flex items-center justify-between py-2 pr-1">
        <button type="button" className="text-sm text-[#009688]" onClick={() => setChanging(true)}>
          تغییر شماره
        </button>
        <button
          type="button"
          className="px-2 text-sm text-mild"
          onClick={() => {
            signOut();
            navigate('/welcome', { replace: true });
          }}
        >
          خروج
        </button>
      </div>
      {changing && <ChangeMobileDialog onClose={() => setChanging(false)} onChanged={() => setSeconds(RESEND_SECONDS)} />}
    </AuthFrame>
  );
}
