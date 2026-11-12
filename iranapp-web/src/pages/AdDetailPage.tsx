import { useState } from 'react';
import { useNavigate, useParams } from 'react-router';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { mdiHeadset, mdiHeart, mdiHeartOutline, mdiShareVariant, mdiThumbDown, mdiThumbUp } from '@mdi/js';
import giftUrl from '@/assets/img/ic_gift_box_ube.png';
import { DiscountBadge, hasDiscount, PLACEHOLDERS } from '@/components/cards';
import { DetailShell, LocationCard, type MenuAction } from '@/components/DetailShell';
import { AdContactDialog } from '@/components/dialogs';
import { Icon, MaskIcon, vectorUrl } from '@/components/Icon';
import { parseLatLng } from '@/lib/geo';
import { AppBar, Card, EmptyNote, Loading, Screen, Spinner, TextButton } from '@/components/ui';
import { SUPPORT_PHONE } from '@/config';
import * as api from '@/lib/api';
import { adShareText, dial, requireUser, shareText } from '@/lib/actions';
import { blank, internationalMobile, text } from '@/lib/format';
import { stash, unstash, useOnce } from '@/lib/hooks';
import { mediaUrl } from '@/lib/media';
import type { Ad, LikeType } from '@/lib/types';
import { useSession } from '@/store/session';
import { confirmDialog, toast } from '@/store/ui';

/** Eitaa (a Telegram fork) looks the account up by number and opens its private chat. */
const EITAA_PHONE_LINK = 'et://resolve?phone=';

/** ShowAdActivity: a business ad with its contact, like / favourite / share row and map. */
export default function AdDetailPage() {
  const { id = '' } = useParams();
  const query = useQuery({
    queryKey: ['ad', id],
    queryFn: () => api.getAd(id),
    initialData: () => unstash<Ad>('ad', id) ?? undefined,
    // A stashed list row is good enough to draw at once, but the server copy is fresher.
    initialDataUpdatedAt: 0,
  });

  if (query.data) return <AdDetail key={id} ad={query.data} />;
  return (
    <Screen bar={<AppBar />}>
      {query.isLoading ? <Loading /> : <EmptyNote onRetry={query.isError ? () => void query.refetch() : undefined}>آگهی مورد نظر یافت نشد.</EmptyNote>}
    </Screen>
  );
}

function AdDetail({ ad }: { ad: Ad }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const user = useSession((state) => state.user);
  const loggedIn = useSession((state) => Boolean(state.user?.verified && state.token));
  const [contactOpen, setContactOpen] = useState(false);

  const prefs = useQuery({
    queryKey: ['ad-prefs', String(ad.id), loggedIn],
    queryFn: () => (loggedIn ? api.getAdPrefs(ad.id) : api.getAdLikeCounts(ad.id)),
    staleTime: 0,
  });
  // The server ignores the owner's own views, which is why the token is sent when there is one.
  useOnce(() => void api.recordAdView(ad.id).catch(() => undefined));

  const [like, setLike] = useState<LikeType | null | undefined>(undefined);
  const [favorite, setFavorite] = useState<boolean | undefined>(undefined);
  const [counts, setCounts] = useState<{ likes: number; dislikes: number } | undefined>(undefined);
  const currentLike = like !== undefined ? like : (prefs.data?.like ?? null);
  const isFavorite = favorite ?? prefs.data?.favorite ?? false;
  const likes = counts?.likes ?? prefs.data?.likes ?? 0;
  const dislikes = counts?.dislikes ?? prefs.data?.dislikes ?? 0;

  /** Same arithmetic as the app: the tapped vote toggles, and replaces the opposite one. */
  const vote = (type: LikeType) => {
    if (!requireUser(navigate)) return;
    const removing = currentLike === type;
    const next = { likes, dislikes };
    if (removing) next[type === 'like' ? 'likes' : 'dislikes'] = Math.max(0, next[type === 'like' ? 'likes' : 'dislikes'] - 1);
    else {
      next[type === 'like' ? 'likes' : 'dislikes'] += 1;
      if (currentLike) next[currentLike === 'like' ? 'likes' : 'dislikes'] = Math.max(0, next[currentLike === 'like' ? 'likes' : 'dislikes'] - 1);
    }
    setCounts(next);
    setLike(removing ? null : type);
    ad.user_like_type = removing ? null : type;
    ad.likes = next.likes;
    stash('ad', ad.id, ad);
    const call = removing ? api.unlikeAd(ad.id, type) : api.likeAd(ad.id, type);
    call.catch(() => toast.failure('لطفا نحوه ی اتصال به اینترنت دستگاه خود را بررسی نمایید'));
  };

  const toggleFavorite = () => {
    if (!requireUser(navigate)) return;
    setFavorite(!isFavorite);
    api
      .toggleAdFavorite(ad.id)
      .then(() => queryClient.invalidateQueries({ queryKey: ['favorites'] }))
      .catch(() => setFavorite(isFavorite));
  };

  const remove = async () => {
    if (!(await confirmDialog({ message: 'آیا از حذف آگهی اطمینان دارید؟', positive: 'آری', negative: 'خیر' }))) return;
    try {
      await api.deleteAd(ad.id);
      await queryClient.invalidateQueries();
      navigate(-1);
    } catch {
      toast.failure('لطفا دوباره تلاش کنید');
    }
  };

  const isOwner = Boolean(user && loggedIn && !blank(ad.user_id) && String(ad.user_id) === user.id);
  const canEdit = String(ad.max_number_of_update ?? '') !== String(ad.updates_count ?? '');
  const menu: MenuAction[] | undefined = isOwner
    ? [
        { label: 'عملکرد آگهی', onClick: () => navigate(`/ad/${ad.id}/stats`, { state: { title: ad.title } }) },
        { label: 'حذف آگهی', onClick: () => void remove() },
        canEdit
          ? {
              label: 'ویرایش آگهی',
              onClick: () => {
                stash('ad', ad.id, ad);
                navigate(`/edit/business/${ad.id}`);
              },
            }
          : { label: 'شما به حد اکثر تعداد تغییر در آگهی رسیدید' },
      ]
    : undefined;

  const messenger = internationalMobile(ad.mobile);
  const video = mediaUrl(ad.video_url);
  const discounted = hasDiscount(ad);

  const askDiscount = async () => {
    if (await confirmDialog({ message: 'برای دریافت تخفیف از این مرکز با ما تماس بگیرید.', positive: 'تماس', negative: 'رد کردن' })) dial(SUPPORT_PHONE);
  };

  return (
    <DetailShell title={ad.title} images={(ad.photos ?? []).map((photo) => photo.file_name)} placeholder={PLACEHOLDERS.ad} pending={ad.status === 'pending'} menu={menu}>
      <button
        type="button"
        className="tap mx-2.5 mt-[5px] mb-[5px] flex w-[calc(100%-20px)] items-center justify-center gap-2.5 rounded-[3px] bg-[#00796B] text-base text-white h-sdp-50"
        style={{ boxShadow: 'var(--shadow-card)' }}
        onClick={() => setContactOpen(true)}
      >
        <Icon path={mdiHeadset} size={25} />
        ارتباط با ما
      </button>

      <div className="relative">
        <Card className="mx-[5px] mt-[30px] mb-[5px] p-1">
          <h1 className="p-[5px] pl-[70px] text-base font-normal text-[#444]">{ad.title}</h1>
          {!blank(ad.notes) && <p className="mb-[5px] whitespace-pre-line px-1 text-sm leading-8">{String(ad.notes)}</p>}
        </Card>
        {discounted && (
          <div className="absolute top-[-2px] left-2.5">
            <DiscountBadge discount={String(ad.discount)} size={60} />
          </div>
        )}
      </div>

      {video && (
        <Card className="mx-[5px] mt-[15px] overflow-hidden">
          <div className="p-[5px] text-sm text-[#444]">ویدیو آگهی</div>
          <video src={video} controls playsInline preload="metadata" className="block h-[220px] w-full bg-black" dir="ltr" />
        </Card>
      )}

      {!blank(ad.ads_owner_name) && (
        <Card className="mx-[5px] mt-[15px] mb-2.5 flex items-center gap-1 px-1 py-1.5 text-sm">
          <span>با مدیریت:</span>
          <span className="text-[#555]">{String(ad.ads_owner_name)}</span>
        </Card>
      )}

      {messenger && (
        <Card className="mx-[5px] mt-[5px] flex items-center px-1 text-sm">
          <span className="flex-1 text-[#555]">پیام به آگهی‌دهنده:</span>
          <a href={`https://wa.me/${messenger}`} target="_blank" rel="noopener noreferrer" className="tap-light flex flex-col items-center rounded p-1.5 text-[9px]">
            <img src={vectorUrl('ic_whatsapp')} alt="" className="h-[34px] w-[34px]" />
            واتساپ
          </a>
          <a href={`${EITAA_PHONE_LINK}${messenger}`} className="tap-light mr-2.5 flex flex-col items-center rounded p-1.5 text-[9px]">
            <img src={vectorUrl('ic_eitaa')} alt="" className="h-[34px] w-[34px] rounded-lg" />
            ایتا
          </a>
        </Card>
      )}

      <Card className="mx-[5px] mt-2.5 mb-[15px] min-h-[62px]">
        {prefs.isLoading ? (
          <div className="flex h-[62px] items-center justify-center">
            <Spinner size={30} />
          </div>
        ) : prefs.isError ? (
          <div className="flex h-[62px] items-center justify-center">
            <TextButton color="#1976d2" onClick={() => void prefs.refetch()}>
              تلاش مجدد
            </TextButton>
          </div>
        ) : (
          <div className="flex pt-[5px] pb-1">
            <PrefButton label={String(likes)} icon={mdiThumbUp} color={currentLike === 'like' ? '#4CAF50' : '#999999'} big onClick={() => vote('like')} aria="پسندیدم" />
            <PrefButton label={String(dislikes)} icon={mdiThumbDown} color={currentLike === 'dislike' ? '#f44336' : '#999999'} big onClick={() => vote('dislike')} aria="نپسندیدم" />
            <span className="w-[30px]" />
            <PrefButton label="افزودن به انتخاب من" icon={isFavorite ? mdiHeart : mdiHeartOutline} color="#E91E63" onClick={toggleFavorite} />
            <PrefButton label="اشتراک گذاری" icon={mdiShareVariant} color="#00BCD4" onClick={() => void shareText(adShareText(ad.title, [ad.tel1, ad.tel2], ad.address))} />
          </div>
        )}
      </Card>

      {discounted && (
        <button type="button" className="tap mx-[5px] mb-3 block w-[calc(100%-10px)] rounded-[3px] bg-primary py-[3px]" style={{ boxShadow: 'var(--shadow-card)' }} onClick={() => void askDiscount()}>
          <span className="flex h-[45px] items-center rounded-[3px] bg-white text-primary">
            <MaskIcon src={giftUrl} size={0} style={{ width: 56, height: 46 }} />
            <span className="flex-1 text-center text-sm">برای استفاده از تخفیف این مرکز کلیک نمایید</span>
          </span>
        </button>
      )}

      <LocationCard position={parseLatLng(ad.latitude, ad.longitude)} />
      {!blank(ad.address) && <p className="mx-3 -mt-2 mb-3 text-xs leading-6 text-mild">{text(ad.address)}</p>}

      {contactOpen && <AdContactDialog ad={ad} onClose={() => setContactOpen(false)} />}
    </DetailShell>
  );
}

function PrefButton({ label, icon, color, onClick, big, aria }: { label: string; icon: string; color: string; onClick: () => void; big?: boolean; aria?: string }) {
  return (
    <button type="button" aria-label={aria} className="tap-light flex flex-1 flex-col items-center rounded py-0.5" onClick={onClick}>
      <Icon path={icon} size={30} style={{ color }} />
      <span className={big ? 'text-[11px]' : 'text-[8px] text-[#666]'} style={big ? { color } : undefined}>
        {label}
      </span>
    </button>
  );
}
