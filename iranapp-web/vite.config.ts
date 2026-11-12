import { fileURLToPath, URL } from 'node:url';
import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import { VitePWA } from 'vite-plugin-pwa';

/**
 * The web app is served from a sub path of the same domain as the API and the admin panel
 * (https://iranapp.biz/web/). Change VITE_BASE_PATH in .env to move it (e.g. "/app/").
 */
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const base = env.VITE_BASE_PATH || '/web/';
  // Only used by `npm run dev`: where /api and the photo folders are proxied to.
  const backend = env.VITE_DEV_BACKEND || 'http://iranapp.biz';
  const proxied = ['/api', '/ads_photo', '/icon', '/news_photo', '/vip_ads_photo', '/ads_video'];

  return {
    base,
    define: {
      __APP_VERSION__: JSON.stringify(process.env.npm_package_version || '1.0.0'),
    },
    resolve: {
      alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) },
    },
    plugins: [
      react(),
      tailwindcss(),
      VitePWA({
        registerType: 'autoUpdate',
        includeAssets: ['favicon.png', 'apple-touch-icon.png'],
        manifest: {
          name: 'ایران آپ',
          short_name: 'ایران آپ',
          description: 'راهنمای هوشمند اصناف، خدمات و اماکن شهر شما',
          lang: 'fa',
          dir: 'rtl',
          start_url: base,
          scope: base,
          display: 'standalone',
          orientation: 'portrait',
          theme_color: '#151B29',
          background_color: '#ffffff',
          icons: [
            { src: 'pwa-192.png', sizes: '192x192', type: 'image/png' },
            { src: 'pwa-512.png', sizes: '512x512', type: 'image/png' },
            { src: 'pwa-maskable-512.png', sizes: '512x512', type: 'image/png', purpose: 'maskable' },
          ],
        },
        workbox: {
          globPatterns: ['**/*.{js,css,html,svg,png,ttf,woff2}'],
          // The help / intro screenshots are big and rarely opened: fetch them on demand.
          globIgnores: ['**/h[0-9]*-*.jpg', '**/intro*-*.jpg'],
          navigateFallback: `${base}index.html`,
          navigateFallbackDenylist: [/^\/api\//, /^\/admin/],
          importScripts: ['push-sw.js'],
          cleanupOutdatedCaches: true,
          runtimeCaching: [
            {
              urlPattern: ({ url }) => /^\/(ads_photo|icon|news_photo|vip_ads_photo)\//.test(url.pathname),
              handler: 'StaleWhileRevalidate',
              options: {
                cacheName: 'iranapp-photos',
                expiration: { maxEntries: 300, maxAgeSeconds: 60 * 60 * 24 * 14 },
              },
            },
            {
              urlPattern: ({ url }) => /\/assets\/.*\.jpg$/.test(url.pathname),
              handler: 'CacheFirst',
              options: { cacheName: 'iranapp-static-images' },
            },
            {
              urlPattern: ({ url }) => url.hostname.endsWith('tile.openstreetmap.org'),
              handler: 'CacheFirst',
              options: {
                cacheName: 'iranapp-map-tiles',
                expiration: { maxEntries: 400, maxAgeSeconds: 60 * 60 * 24 * 30 },
                cacheableResponse: { statuses: [0, 200] },
              },
            },
          ],
        },
      }),
    ],
    server: {
      host: true,
      port: 5173,
      proxy: Object.fromEntries(proxied.map((path) => [path, { target: backend, changeOrigin: true }])),
    },
    preview: {
      port: 4173,
      proxy: Object.fromEntries(proxied.map((path) => [path, { target: backend, changeOrigin: true }])),
    },
    build: {
      target: 'es2020',
      chunkSizeWarningLimit: 900,
    },
  };
});
