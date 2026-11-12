import { useParams } from 'react-router';
import { useQuery } from '@tanstack/react-query';
import { PLACEHOLDERS } from '@/components/cards';
import { DetailShell } from '@/components/DetailShell';
import { AppBar, Card, EmptyNote, Loading, Screen } from '@/components/ui';
import { getNewsItem } from '@/lib/api';
import { unstash } from '@/lib/hooks';
import type { News } from '@/lib/types';

/** ShowNews: one news item with its photos. */
export default function NewsDetailPage() {
  const { id = '' } = useParams();
  const stashed = unstash<News>('news', id);
  const query = useQuery({ queryKey: ['news-item', id], queryFn: () => getNewsItem(id), enabled: !stashed });
  const news = stashed ?? query.data;

  if (!news) {
    return (
      <Screen bar={<AppBar />}>
        {query.isLoading ? <Loading /> : <EmptyNote onRetry={query.isError ? () => void query.refetch() : undefined}>خبر مورد نظر یافت نشد.</EmptyNote>}
      </Screen>
    );
  }

  return (
    <DetailShell title={news.title} images={(news.photos ?? []).map((photo) => photo.file_name)} placeholder={PLACEHOLDERS.ad} photoHeight={220}>
      <Card className="mt-[5px] mb-[5px] rounded-none px-5 py-2">
        <h1 className="text-base font-normal leading-7 text-[#333]">{news.title}</h1>
        <div className="mt-2.5 text-xs text-[#777]">{news.created_at_fa ?? news.created_at}</div>
      </Card>
      <Card className="m-[5px]">
        <p className="m-2.5 whitespace-pre-line py-1 text-sm leading-8 text-[#555]">{news.passage}</p>
      </Card>
    </DetailShell>
  );
}
