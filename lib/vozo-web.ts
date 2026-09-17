export const SITE_URL = "https://vozo-voice-lab.base44.app/";
export const SITE_ORIGIN = "https://vozo-voice-lab.base44.app";

export function isInternalUrl(url: string) {
  return url === SITE_ORIGIN || url.startsWith(`${SITE_ORIGIN}/`);
}

export const MOBILE_POLISH_SCRIPT = `
  (function() {
    try {
      var viewport = document.querySelector('meta[name="viewport"]');
      if (!viewport) {
        viewport = document.createElement('meta');
        viewport.name = 'viewport';
        document.head.appendChild(viewport);
      }
      viewport.setAttribute('content', 'width=device-width, initial-scale=1, maximum-scale=1, viewport-fit=cover');
      var style = document.createElement('style');
      style.id = 'vozo-android-mobile-polish';
      style.textContent = '\n        html, body { width: 100% !important; min-height: 100% !important; overscroll-behavior-y: contain; }\n        body { -webkit-tap-highlight-color: transparent; }\n        #base44-edit-badge { display: none !important; }\n        input, textarea, select, button { font-size: 16px !important; }\n        audio, video { max-width: 100% !important; }\n      ';
      if (!document.getElementById(style.id)) document.head.appendChild(style);
    } catch (e) {}
    true;
  })();
`;
