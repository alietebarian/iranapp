import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import {
  mdiAccountSupervisor,
  mdiBell,
  mdiCardAccountDetails,
  mdiCardAccountMail,
  mdiCellphoneArrowDown,
  mdiClipboardText,
  mdiHeadset,
  mdiHelpCircle,
  mdiMap,
  mdiMapMarker,
  mdiNote,
  mdiPlus,
  mdiShareVariant,
} from '@mdi/js';
import { Icon } from '@/components/Icon';
import { BAZAAR_LINK } from '@/config';
import { requireUser, shareText } from '@/lib/actions';
import { useSession } from '@/store/session';
import { alertDialog, openAdTypeDialog, openCityPicker, openContactUs, openNotifications, openRules } from '@/store/ui';

function Item({ icon, label, onClick }: { icon: string; label: string; onClick: () => void }) {
  return (
    <button type="button" className="tap-light flex h-[50px] w-full items-center pr-5 text-right text-sm text-[#555]" onClick={onClick}>
      <Icon path={icon} size={26} className="text-primary" />
      <span className="mr-[30px]">{label}</span>
    </button>
  );
}

function Heading({ children }: { children: string }) {
  return <div className="flex h-[50px] items-center bg-[#eee] pr-[30px] text-sm text-[#555]">{children}</div>;
}

interface InstallPromptEvent extends Event {
  prompt: () => Promise<void>;
}

let installPrompt: InstallPromptEvent | null = null;
window.addEventListener('beforeinstallprompt', (event) => {
  event.preventDefault();
  installPrompt = event as InstallPromptEvent;
});

const isStandalone = () => window.matchMedia('(display-mode: standalone)').matches || (navigator as { standalone?: boolean }).standalone === true;

/** MenuFragment: account, settings and the rest of the app's pages. */
export default function MenuTab() {
  const navigate = useNavigate();
  const user = useSession((state) => state.user);
  const [installed, setInstalled] = useState(isStandalone);

  useEffect(() => {
    const onInstalled = () => setInstalled(true);
    window.addEventListener('appinstalled', onInstalled);
    return () => window.removeEventListener('appinstalled', onInstalled);
  }, []);

  const install = () => {
    if (installPrompt) {
      void installPrompt.prompt();
      installPrompt = null;
      return;
    }
    void alertDialog(
      'برای نصب ایران آپ روی گوشی:\n\n• در آیفون (مرورگر Safari): دکمه «اشتراک گذاری» را بزنید و «Add to Home Screen» را انتخاب کنید.\n• در اندروید (مرورگر Chrome): از منوی سه نقطه «Add to Home screen» یا «نصب برنامه» را انتخاب کنید.',
      'متوجه شدم',
      'نصب برنامه',
    );
  };

  return (
    <div className="h-full overflow-y-auto bg-white">
      {user ? (
        <>
          <Item icon={mdiAccountSupervisor} label="محیط کاربری" onClick={() => requireUser(navigate) && navigate('/dashboard')} />
          <Item icon={mdiPlus} label="درج آگهی" onClick={() => requireUser(navigate) && openAdTypeDialog()} />
        </>
      ) : (
        <>
          <Item icon={mdiAccountSupervisor} label="ثبت نام" onClick={() => navigate('/signup')} />
          <Item icon={mdiAccountSupervisor} label="ورود" onClick={() => navigate('/login')} />
        </>
      )}

      <Heading>تنظیمات</Heading>
      <Item icon={mdiMapMarker} label="انتخاب شهر" onClick={openCityPicker} />
      <Item icon={mdiBell} label="دریافت اطلاعیه ها" onClick={openNotifications} />
      <Item icon={mdiMap} label="اطراف من" onClick={() => navigate('/around')} />

      <Heading>دیگر</Heading>
      <Item
        icon={mdiClipboardText}
        label="قرارداد الکترونیک"
        onClick={() => requireUser(navigate, 'برای ثبت قرارداد الکترونیک ابتدا وارد حساب کاربری خود شوید') && navigate('/e-contract')}
      />
      <Item
        icon={mdiCardAccountDetails}
        label="کارت عضویت"
        onClick={() => requireUser(navigate, 'برای دریافت کارت عضویت ابتدا وارد حساب کاربری خود شوید') && navigate('/membership-card')}
      />
      <Item icon={mdiHelpCircle} label="راهنمای برنامه" onClick={() => navigate('/help')} />
      <Item icon={mdiShareVariant} label="معرفی به دوستان" onClick={() => void shareText(`باایران آپ در تخفیف ها به روز باشید\n\n${BAZAAR_LINK}\n${window.location.origin}${import.meta.env.BASE_URL}`)} />
      {!installed && <Item icon={mdiCellphoneArrowDown} label="نصب برنامه روی گوشی" onClick={install} />}

      <Heading>درباره ما</Heading>
      <Item icon={mdiCardAccountMail} label="درباره ما" onClick={() => navigate('/about')} />
      <Item icon={mdiHeadset} label="ارتباط با ما" onClick={openContactUs} />
      <Item icon={mdiNote} label="قوانین و مقررات" onClick={openRules} />
    </div>
  );
}
