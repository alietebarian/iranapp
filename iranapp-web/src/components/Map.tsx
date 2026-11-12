import { useEffect, useMemo, type ReactNode } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { MapContainer, Marker, TileLayer, useMap, useMapEvents } from 'react-leaflet';
import { MAP_ATTRIBUTION, MAP_DEFAULT_CENTER, MAP_TILE_URL } from '@/config';
import type { LatLng } from '@/lib/geo';

/** Maps (OpenStreetMap through Leaflet) in place of the app's Google Maps fragment. */


const PIN_SVG =
  '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="38" height="38"><path fill="#b71c1c" stroke="#fff" stroke-width="0.6" d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5a2.5 2.5 0 1 1 0-5 2.5 2.5 0 0 1 0 5z"/></svg>';

/** A red drop pin, like the default Google marker (Leaflet's own images need extra bundler setup). */
export const pinIcon = L.divIcon({ html: PIN_SVG, className: '', iconSize: [38, 38], iconAnchor: [19, 37] });

function Recenter({ center, zoom }: { center: LatLng; zoom?: number }) {
  const map = useMap();
  useEffect(() => {
    map.setView(center, zoom ?? map.getZoom());
  }, [map, center, zoom]);
  return null;
}

function Tiles() {
  return <TileLayer url={MAP_TILE_URL} attribution={MAP_ATTRIBUTION} maxZoom={19} />;
}

/** A read-only map with one marker (the ad's location). */
export default function MapView({ position, className, zoom = 15 }: { position: LatLng; className?: string; zoom?: number }) {
  return (
    <MapContainer center={position} zoom={zoom} className={className} scrollWheelZoom={false} attributionControl>
      <Tiles />
      <Marker position={position} icon={pinIcon} />
      <Recenter center={position} />
    </MapContainer>
  );
}

function ClickToPick({ onPick }: { onPick: (position: LatLng) => void }) {
  useMapEvents({
    click: (event) => onPick([event.latlng.lat, event.latlng.lng]),
  });
  return null;
}

/** «نشان کردن روی نقشه»: tap the map to drop (or move) the ad's pin. */
export function MapPicker({
  value,
  onChange,
  center,
  className,
}: {
  value: LatLng | null;
  onChange: (position: LatLng) => void;
  /** Where to look before a pin exists (the user's location or the chosen city). */
  center?: LatLng | null;
  className?: string;
}) {
  const start = useMemo(() => value ?? center ?? MAP_DEFAULT_CENTER, [value, center]);
  return (
    <MapContainer center={start} zoom={value ? 15 : 12} className={className} scrollWheelZoom={false}>
      <Tiles />
      <ClickToPick onPick={onChange} />
      {value && <Marker position={value} icon={pinIcon} />}
      {!value && center && <Recenter center={center} zoom={14} />}
    </MapContainer>
  );
}

export interface MapMarker {
  id: string | number;
  position: LatLng;
  onClick: () => void;
}

function MoveWatcher({ onMoveEnd }: { onMoveEnd: (center: LatLng) => void }) {
  useMapEvents({
    moveend: (event) => {
      const c = event.target.getCenter();
      onMoveEnd([c.lat, c.lng]);
    },
  });
  return null;
}

/** «اطراف من»: many markers, and a callback when the user drags the map somewhere else. */
export function MarkersMap({
  center,
  markers,
  onMoveEnd,
  className,
  children,
}: {
  center: LatLng;
  markers: MapMarker[];
  onMoveEnd: (center: LatLng) => void;
  className?: string;
  children?: ReactNode;
}) {
  return (
    <MapContainer center={center} zoom={15} className={className} zoomControl={false}>
      <Tiles />
      <MoveWatcher onMoveEnd={onMoveEnd} />
      {markers.map((marker) => (
        <Marker key={marker.id} position={marker.position} icon={pinIcon} eventHandlers={{ click: marker.onClick }} />
      ))}
      {children}
    </MapContainer>
  );
}

/** Blue dot for the user's own position. */
export function MyLocationDot({ position }: { position: LatLng }) {
  const icon = useMemo(
    () =>
      L.divIcon({
        html: '<span style="display:block;width:16px;height:16px;border-radius:50%;background:#1976d2;border:3px solid #fff;box-shadow:0 0 0 2px rgba(25,118,210,.35)"></span>',
        className: '',
        iconSize: [16, 16],
        iconAnchor: [8, 8],
      }),
    [],
  );
  return <Marker position={position} icon={icon} interactive={false} />;
}

export function FlyTo({ position }: { position: LatLng | null }) {
  const map = useMap();
  useEffect(() => {
    if (position) map.setView(position, 15);
  }, [map, position]);
  return null;
}
