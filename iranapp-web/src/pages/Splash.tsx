import { useEffect } from 'react';
import { Logo, LinearProgress } from '@/components/ui';
import { APP_VERSION } from '@/config';

const WORDS = ['نیازمندیهای', 'همیشه', 'همراه'];

/** activity_splash_screen: the logo, the slogan rising in word by word, a progress line. */
export default function Splash({ onDone }: { onDone: () => void }) {
  useEffect(() => {
    // The app waits four seconds here; on the web a shorter pause is enough for the slogan.
    const timer = window.setTimeout(onDone, 1900);
    return () => window.clearTimeout(timer);
  }, [onDone]);

  return (
    <div className="relative flex h-full flex-col items-center justify-center" style={{ background: 'radial-gradient(circle, #ddd, #eee 70%)' }}>
      <Logo width={220} height={78} className="animate-fade-in" />
      <div className="mt-6 flex h-10 items-end gap-1.5 font-sina text-base font-bold text-primary">
        {WORDS.map((word, index) => (
          <span key={word} style={{ animation: `splash-word 420ms ease-out ${250 + index * 180}ms both` }}>
            {word}
          </span>
        ))}
      </div>
      <div className="absolute bottom-4 text-xs text-mild" dir="ltr">
        {APP_VERSION.replace('web-', 'نسخه وب ')}
      </div>
      <LinearProgress className="absolute inset-x-0 bottom-0" color="var(--color-primary)" />
    </div>
  );
}
