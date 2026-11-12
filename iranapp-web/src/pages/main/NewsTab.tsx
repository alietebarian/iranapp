import { useNavigate } from 'react-router';
import { Photo } from '@/components/cards';
import { PagedList } from '@/components/PagedList';
import { EmptyNote } from '@/components/ui';
import { getNews } from '@/lib/api';
import { stash, usePagedList, useScrollRestore } from '@/lib/hooks';

/** NewsFragment: the news list. */
export default function NewsTab() {
  const navigate = useNavigate();
  const list = usePagedList(['news'], getNews);
  const scrollRef = useScrollRestore<HTMLDivElement>();
  return (
    <div ref={scrollRef} className="h-full overflow-y-auto bg-[#eee]">
      <PagedList
        list={list}
        empty={<EmptyNote>هنوز خبری منتشر نشده است</EmptyNote>}
        render={(news) => (
          <div
            key={news.id}
            role="link"
            tabIndex={0}
            className="tap-light m-[5px] flex h-[100px] cursor-pointer rounded-[3px] bg-white"
            style={{ boxShadow: 'var(--shadow-card)' }}
            onClick={() => {
              stash('news', news.id, news);
              navigate(`/news/${news.id}`);
            }}
            onKeyDown={(event) => event.key === 'Enter' && navigate(`/news/${news.id}`)}
          >
            <Photo src={news.photos?.[0]?.file_name} className="m-[3px] w-[94px] shrink-0 rounded-[3px] shadow" alt="" />
            <div className="flex min-w-0 flex-1 flex-col">
              <div className="line-clamp-2 flex-1 p-2.5 text-base leading-7 text-primary">{news.title}</div>
              <div className="mb-[3px] pr-2.5 text-xs text-[#777]">{news.created_at}</div>
            </div>
          </div>
        )}
      />
    </div>
  );
}
