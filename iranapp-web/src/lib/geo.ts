/** Kept apart from components/Map so pages can read coordinates without loading Leaflet. */

export type LatLng = [number, number];

/** The API sends coordinates as numbers, numeric strings, null or the string "null". */
export function parseLatLng(latitude: unknown, longitude: unknown): LatLng | null {
  if (latitude === null || latitude === undefined || latitude === '' || latitude === 'null') return null;
  const lat = Number(latitude);
  const lng = Number(longitude);
  if (!Number.isFinite(lat) || !Number.isFinite(lng) || (lat === 0 && lng === 0)) return null;
  return [lat, lng];
}
