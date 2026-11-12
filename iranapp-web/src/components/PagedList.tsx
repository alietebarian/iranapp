import type { ReactNode } from 'react';
import type { usePagedList } from '@/lib/hooks';
import { EmptyNote, LinearProgress, Loading } from './ui';

interface Props<T> {
  list: ReturnType<typeof usePagedList<T>>;
  render: (item: T, index: number) => ReactNode;
  /** Shown when the first page comes back empty. */
  empty: ReactNode;
  dark?: boolean;
}

/** The body of every endless list: spinner, rows, "loading more" bar, empty and error states. */
export function PagedList<T>({ list, render, empty, dark }: Props<T>) {
  if (list.isLoading) return <Loading color={dark ? 'var(--color-home-accent)' : undefined} />;
  if (list.isError && list.items.length === 0) {
    return (
      <EmptyNote dark={dark} onRetry={() => void list.refetch()}>
        دریافت اطلاعات انجام نشد. لطفا اتصال اینترنت خود را بررسی کنید.
      </EmptyNote>
    );
  }
  if (list.isEmpty) return <>{empty}</>;
  return (
    <>
      {list.items.map(render)}
      <div ref={list.sentinel} className="h-px" />
      {list.isFetchingMore && <LinearProgress />}
    </>
  );
}
