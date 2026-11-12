import { useCallback, useEffect, useLayoutEffect, useMemo, useRef, type RefObject } from 'react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { useLocation } from 'react-router';
import { PAGE_SIZE } from './api';

/**
 * A list that loads ten rows at a time as the user reaches its end (offset / limit, like every
 * list of the Android app). Attach `sentinel` to an element after the last row.
 */
export function usePagedList<T>(key: readonly unknown[], fetchPage: (offset: number) => Promise<T[]>, enabled = true) {
  const query = useInfiniteQuery({
    queryKey: key,
    queryFn: ({ pageParam }) => fetchPage(pageParam),
    initialPageParam: 0,
    getNextPageParam: (lastPage, pages) => (lastPage.length < PAGE_SIZE ? undefined : pages.length * PAGE_SIZE),
    enabled,
  });

  const items = useMemo(() => query.data?.pages.flat() ?? [], [query.data]);
  const { hasNextPage, isFetchingNextPage, fetchNextPage } = query;
  const observer = useRef<IntersectionObserver | null>(null);

  const sentinel = useCallback(
    (node: HTMLElement | null) => {
      observer.current?.disconnect();
      if (!node || !hasNextPage) return;
      observer.current = new IntersectionObserver(
        (entries) => {
          if (entries[0].isIntersecting && !isFetchingNextPage) void fetchNextPage();
        },
        { rootMargin: '300px' },
      );
      observer.current.observe(node);
    },
    [hasNextPage, isFetchingNextPage, fetchNextPage],
  );

  return {
    items,
    sentinel,
    isLoading: query.isLoading,
    isError: query.isError,
    isEmpty: query.isSuccess && items.length === 0,
    isFetchingMore: isFetchingNextPage,
    refetch: query.refetch,
  };
}

const scrollPositions = new Map<string, number>();

/**
 * Pages scroll inside their own container, which the browser does not remember. This puts a
 * list back where it was when the user returns to it with the back button.
 */
export function useScrollRestore<T extends HTMLElement>(suffix = ''): RefObject<T | null> {
  const ref = useRef<T | null>(null);
  const location = useLocation();
  const key = `${location.key}:${suffix}`;

  useLayoutEffect(() => {
    const node = ref.current;
    if (!node) return;
    const saved = scrollPositions.get(key);
    if (saved) node.scrollTop = saved;
    const onScroll = () => scrollPositions.set(key, node.scrollTop);
    node.addEventListener('scroll', onScroll, { passive: true });
    return () => node.removeEventListener('scroll', onScroll);
  }, [key]);

  return ref;
}

/**
 * Vehicle, estate and job ads have no "get by id" endpoint: the Android app hands the whole
 * object to the detail screen. Here it travels through sessionStorage so a refresh of the
 * detail page still works.
 */
export function stash<T>(kind: string, id: string | number, item: T): void {
  try {
    sessionStorage.setItem(`iranapp:${kind}:${id}`, JSON.stringify(item));
  } catch {
    // Storage full or blocked (private mode): the page simply won't survive a refresh.
  }
}

export function unstash<T>(kind: string, id: string | number | undefined): T | null {
  if (id === undefined) return null;
  try {
    const raw = sessionStorage.getItem(`iranapp:${kind}:${id}`);
    return raw ? (JSON.parse(raw) as T) : null;
  } catch {
    return null;
  }
}

/** Runs the callback once after mount (e.g. counting a view). */
export function useOnce(effect: () => void): void {
  const done = useRef(false);
  useEffect(() => {
    if (done.current) return;
    done.current = true;
    effect();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);
}
