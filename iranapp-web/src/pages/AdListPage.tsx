import { useState } from 'react';
import { useParams } from 'react-router';
import { AdCard } from '@/components/cards';
import { VipAdDialog } from '@/components/dialogs';
import { PagedList } from '@/components/PagedList';
import { AppBar, EmptyNote, Screen } from '@/components/ui';
import { getSubCategoryAds } from '@/lib/api';
import { usePagedList, useScrollRestore } from '@/lib/hooks';
import type { VipAd } from '@/lib/types';
import { useSession } from '@/store/session';

const shownVipAds = new Set<string>();

/** AdListActivity: the ads of one sub category in the user's city. */
export default function AdListPage() {
  const { subCategoryId = '' } = useParams();
  const cityId = useSession((state) => state.cityId);
  const [vipAd, setVipAd] = useState<VipAd | null>(null);
  const scrollRef = useScrollRestore<HTMLDivElement>();

  const list = usePagedList(['ads-sub', cityId, subCategoryId], async (offset) => {
    const page = await getSubCategoryAds(cityId, subCategoryId, offset);
    if (offset === 0 && page.vipAd && !shownVipAds.has(subCategoryId)) {
      shownVipAds.add(subCategoryId);
      setVipAd(page.vipAd);
    }
    return page.list;
  });

  return (
    <Screen bar={<AppBar />} className="bg-[#eee]">
      <div ref={scrollRef} className="min-h-0 flex-1 overflow-y-auto px-[5px] pt-[5px]">
        <PagedList list={list} render={(ad) => <AdCard key={ad.id} ad={ad} />} empty={<EmptyNote>متاسفانه هیچ داده ای یافت نشد</EmptyNote>} />
      </div>
      {vipAd && <VipAdDialog ad={vipAd} onClose={() => setVipAd(null)} />}
    </Screen>
  );
}
