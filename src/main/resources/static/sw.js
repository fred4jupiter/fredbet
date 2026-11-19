"use strict";
// Minimal service worker of the installable web app (PWA).
// It deliberately has NO fetch handler and caches nothing: all pages are rendered on the server
// and depend on the user session, so every request goes to the server exactly as without a service worker.
// jQuery is not available in a service worker, therefore the plain event API is used here.
self.addEventListener('install', function () {
    self.skipWaiting();
});

self.addEventListener('activate', function (event) {
    event.waitUntil(self.clients.claim());
});
