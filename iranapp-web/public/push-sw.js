/* Loaded by the generated service worker (workbox importScripts).
   Shows push notifications sent through Firebase Cloud Messaging and opens the right page when
   one is tapped. The server sends a "notification" (title, body) plus "data" (status,
   content_id, msg), the same payload the Android app receives. */

// The "status" codes of NotificationDialog in the Android app.
const AD_STATUS = '0';
const NEWS_STATUS = '1';
const E_CONTRACT_STATUS = '20';
const MEMBERSHIP_STATUS = '40';

self.addEventListener('push', (event) => {
  let payload = {};
  try {
    payload = event.data ? event.data.json() : {};
  } catch (e) {
    payload = { notification: { body: event.data ? event.data.text() : '' } };
  }
  const notification = payload.notification || {};
  const data = payload.data || {};
  const title = notification.title || 'ایران آپ';
  const scope = self.registration.scope;
  event.waitUntil(
    self.registration.showNotification(title, {
      body: notification.body || data.msg || '',
      icon: scope + 'pwa-192.png',
      badge: scope + 'pwa-192.png',
      dir: 'rtl',
      lang: 'fa',
      data,
    }),
  );
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const data = event.notification.data || {};
  const scope = self.registration.scope;
  let target = scope;
  if (data.content_id && String(data.status) === AD_STATUS) target = scope + 'ad/' + data.content_id;
  else if (data.content_id && String(data.status) === NEWS_STATUS) target = scope + 'news/' + data.content_id;
  else if (String(data.status) === E_CONTRACT_STATUS) target = scope + 'e-contract';
  else if (String(data.status) === MEMBERSHIP_STATUS) target = scope + 'membership-card';

  event.waitUntil(
    self.clients.matchAll({ type: 'window', includeUncontrolled: true }).then((clients) => {
      for (const client of clients) {
        if (client.url.startsWith(scope) && 'focus' in client) {
          client.navigate(target);
          return client.focus();
        }
      }
      return self.clients.openWindow(target);
    }),
  );
});
