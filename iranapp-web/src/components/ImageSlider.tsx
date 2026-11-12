import { useEffect, useState } from 'react';
import useEmblaCarousel from 'embla-carousel-react';
import { mdiClose } from '@mdi/js';
import { cx } from '@/lib/format';
import { mediaUrl } from '@/lib/media';
import { Icon } from './Icon';
import { Modal } from './ui';

interface Props {
  images: string[];
  placeholder: string;
  /** CSS height of the picture area (the dots sit below it). */
  height: string;
}

/** The swipeable photo header of every detail page, with dots; tapping a photo opens it full screen. */
export function ImageSlider({ images, placeholder, height }: Props) {
  const urls = images.map((image) => mediaUrl(image)).filter((url): url is string => Boolean(url));
  const [emblaRef, embla] = useEmblaCarousel({ direction: 'rtl', loop: false });
  const [index, setIndex] = useState(0);
  const [zoomed, setZoomed] = useState<string | null>(null);

  useEffect(() => {
    if (!embla) return;
    const onSelect = () => setIndex(embla.selectedScrollSnap());
    embla.on('select', onSelect);
    return () => {
      embla.off('select', onSelect);
    };
  }, [embla]);

  return (
    <div className="bg-white">
      <div className="overflow-hidden bg-[#eee]" style={{ height }} ref={emblaRef} dir="rtl">
        <div className="flex h-full">
          {urls.length === 0 ? (
            <img src={placeholder} alt="" className="h-full w-full shrink-0 object-cover" />
          ) : (
            urls.map((url) => (
              <button key={url} type="button" className="h-full w-full shrink-0" aria-label="نمایش تصویر" onClick={() => setZoomed(url)}>
                <img
                  src={url}
                  alt=""
                  className="h-full w-full object-cover"
                  onError={(event) => {
                    event.currentTarget.src = placeholder;
                  }}
                />
              </button>
            ))
          )}
        </div>
      </div>
      <div className="flex h-[26px] items-center justify-center gap-1.5">
        {urls.length > 1 &&
          urls.map((url, i) => (
            <button
              key={url}
              type="button"
              aria-label={`تصویر ${i + 1}`}
              className={cx('h-2 w-2 rounded-full transition-colors', i === index ? 'bg-primary' : 'bg-[#cfcfcf]')}
              onClick={() => embla?.scrollTo(i)}
            />
          ))}
      </div>
      {zoomed && (
        <Modal full onClose={() => setZoomed(null)} className="!bg-black">
          <div className="relative flex h-full items-center justify-center" onClick={() => setZoomed(null)}>
            <img src={zoomed} alt="" className="max-h-full max-w-full object-contain" />
            <button type="button" aria-label="بستن" className="absolute top-3 right-3 flex h-10 w-10 items-center justify-center rounded-full bg-black/50 text-white">
              <Icon path={mdiClose} />
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
}
