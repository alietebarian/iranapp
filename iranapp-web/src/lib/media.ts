import { MEDIA_HOSTS } from '@/config';
import { blank } from './format';

/**
 * Photo links come back absolute and on plain http (http://iranapp.biz/ads_photo/x.jpg). Served
 * from an https page those would be blocked as mixed content, so links to our own hosts are
 * turned into same-origin paths (/ads_photo/x.jpg).
 */
export function mediaUrl(url: string | null | undefined): string | undefined {
  if (blank(url)) return undefined;
  const value = String(url);
  try {
    const parsed = new URL(value, window.location.origin);
    if (MEDIA_HOSTS.includes(parsed.hostname.toLowerCase())) {
      return parsed.pathname + parsed.search;
    }
    return parsed.href;
  } catch {
    return value;
  }
}

/** Sub category icons arrive as a bare file name ("67ea.jpg") from one endpoint and a full link from another. */
export function iconUrl(icon: string | null | undefined): string | undefined {
  if (blank(icon)) return undefined;
  const value = String(icon).trim();
  if (!value) return undefined;
  return /^https?:\/\//i.test(value) || value.startsWith('/') ? mediaUrl(value) : `/icon/${value}`;
}

const MAX_SIDE = 1280;
const JPEG_QUALITY = 0.82;

/**
 * Shrinks a picked photo before upload (the app does the same in Compress_image): phone cameras
 * produce 3-12 MB files, the server only needs a screen-sized JPEG.
 * Falls back to the original file if the browser cannot decode it.
 */
export async function compressImage(file: File): Promise<File> {
  if (!file.type.startsWith('image/') || file.type === 'image/gif') return file;
  try {
    const bitmap = await loadBitmap(file);
    const scale = Math.min(1, MAX_SIDE / Math.max(bitmap.width, bitmap.height));
    const width = Math.max(1, Math.round(bitmap.width * scale));
    const height = Math.max(1, Math.round(bitmap.height * scale));
    const canvas = document.createElement('canvas');
    canvas.width = width;
    canvas.height = height;
    const context = canvas.getContext('2d');
    if (!context) return file;
    // JPEG has no transparency: paint white under PNGs.
    context.fillStyle = '#ffffff';
    context.fillRect(0, 0, width, height);
    context.drawImage(bitmap.source, 0, 0, width, height);
    bitmap.close();
    const blob = await new Promise<Blob | null>((resolve) => canvas.toBlob(resolve, 'image/jpeg', JPEG_QUALITY));
    if (!blob) return file;
    if (scale === 1 && blob.size >= file.size) return file;
    return new File([blob], `${Date.now()}.jpg`, { type: 'image/jpeg' });
  } catch {
    return file;
  }
}

interface Bitmap {
  source: CanvasImageSource;
  width: number;
  height: number;
  close: () => void;
}

async function loadBitmap(file: File): Promise<Bitmap> {
  if ('createImageBitmap' in window) {
    try {
      const bitmap = await createImageBitmap(file, { imageOrientation: 'from-image' });
      return { source: bitmap, width: bitmap.width, height: bitmap.height, close: () => bitmap.close() };
    } catch {
      // Some Safari versions reject the options bag or HEIC input; fall through to <img>.
    }
  }
  const url = URL.createObjectURL(file);
  try {
    const image = await new Promise<HTMLImageElement>((resolve, reject) => {
      const img = new Image();
      img.onload = () => resolve(img);
      img.onerror = () => reject(new Error('decode'));
      img.src = url;
    });
    return { source: image, width: image.naturalWidth, height: image.naturalHeight, close: () => undefined };
  } finally {
    // The decoded image stays usable after the URL is released.
    window.setTimeout(() => URL.revokeObjectURL(url), 10_000);
  }
}
