import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import useEmblaCarousel from 'embla-carousel-react';
import h1 from '@/assets/img/h1.jpg';
import h2 from '@/assets/img/h2.jpg';
import h3 from '@/assets/img/h3.jpg';
import h4 from '@/assets/img/h4.jpg';
import h5 from '@/assets/img/h5.jpg';
import h9 from '@/assets/img/h9.jpg';
import h10 from '@/assets/img/h10.jpg';
import { cx } from '@/lib/format';

/** The seven guide pictures, in the order the app pages through them. */
const PAGES = [h1, h2, h3, h4, h5, h9, h10];

/** HelpActivity («راهنمای برنامه»): annotated screenshots with «بعدی» / «بازگشت». */
export default function HelpPage() {
  const navigate = useNavigate();
  const [emblaRef, embla] = useEmblaCarousel({ direction: 'rtl' });
  const [index, setIndex] = useState(0);

  useEffect(() => {
    if (!embla) return;
    const onSelect = () => setIndex(embla.selectedScrollSnap());
    embla.on('select', onSelect);
    return () => {
      embla.off('select', onSelect);
    };
  }, [embla]);

  const last = index === PAGES.length - 1;

  return (
    <div className="animate-page-in flex h-full flex-col bg-white">
      <div className="relative min-h-0 flex-1 bg-[#222]">
        <div className="h-full overflow-hidden" ref={emblaRef} dir="rtl">
          <div className="flex h-full">
            {PAGES.map((src, i) => (
              <img key={src} src={src} alt={`راهنما ${i + 1}`} loading={i < 2 ? 'eager' : 'lazy'} className="h-full w-full shrink-0 object-contain" />
            ))}
          </div>
        </div>
        <div className="absolute inset-x-0 top-2 flex justify-center gap-1.5 pt-safe">
          {PAGES.map((src, i) => (
            <span key={src} className={cx('h-2.5 w-2.5 rounded-full border border-white', i === index ? 'bg-white' : 'bg-transparent')} />
          ))}
        </div>
      </div>
      <div className="flex h-[55px] shrink-0 border-t border-[#444] pb-safe">
        <button type="button" className="tap-light flex-1 text-[#444]" onClick={() => navigate(-1)}>
          بازگشت
        </button>
        <span className="w-px bg-[#444]" />
        <button type="button" className="tap-light flex-1 text-[#00BCD4]" onClick={() => (last ? navigate(-1) : embla?.scrollNext())}>
          {last ? 'پایان' : 'بعدی'}
        </button>
      </div>
    </div>
  );
}
