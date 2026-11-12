import type { ReactNode } from 'react';
import { useLocation, useParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { mdiHeart, mdiThumbUp } from '@mdi/js';
import { Icon, VectorIcon } from '@/components/Icon';
import { AppBar, Card, Loading, Screen, TextButton } from '@/components/ui';
import { getAdStats } from '@/lib/api';

function StatCard({ icon, color, label, week, value }: { icon: ReactNode; color: string; label: string; week: string; value: number }) {
  return (
    <Card className="mb-2.5 flex items-center rounded-md p-3.5">
      <span style={{ color }}>{icon}</span>
      <div className="mr-3.5 flex-1">
        <div className="text-sm text-mild">{label}</div>
        <div className="text-xs text-[#9e9e9e]">{week}</div>
      </div>
      <span className="text-[26px]" style={{ color }}>
        {value}
      </span>
    </Card>
  );
}

/** AdStatsActivity («عملکرد آگهی»): views, saves and likes of one of the user's own ads. */
export default function AdStatsPage() {
  const { id = '' } = useParams();
  const location = useLocation();
  const title = (location.state as { title?: string } | null)?.title ?? '';
  const query = useQuery({ queryKey: ['ad-stats', id], queryFn: () => getAdStats(id), staleTime: 0 });
  const stats = query.data && String(query.data.status) === '200' ? query.data : null;

  return (
    <Screen bar={<AppBar title="عملکرد آگهی" line={false} />} className="bg-[#f5f5f5]">
      <div className="min-h-0 flex-1 overflow-y-auto p-3">
        {title && <div className="mb-3 text-base text-mild">{title}</div>}
        {query.isLoading ? (
          <Loading />
        ) : !stats ? (
          <div className="mt-10 flex justify-center">
            <TextButton color="var(--color-primary)" className="text-[15px]" onClick={() => void query.refetch()}>
              دریافت آمار انجام نشد. تلاش مجدد
            </TextButton>
          </div>
        ) : (
          <>
            <StatCard icon={<VectorIcon name="ic_sub_eye" size={36} />} color="#1E88E5" label="بازدید" week={`${stats.views_week} در ۷ روز اخیر`} value={stats.views} />
            <StatCard icon={<Icon path={mdiHeart} size={36} />} color="#E91E63" label="ذخیره شده" week={`${stats.favorites_week} در ۷ روز اخیر`} value={stats.favorites} />
            <StatCard icon={<Icon path={mdiThumbUp} size={36} />} color="#4CAF50" label="لایک" week={`${stats.likes_week} در ۷ روز اخیر  •  ${stats.dislikes} نپسندیده`} value={stats.likes} />
          </>
        )}
      </div>
    </Screen>
  );
}
