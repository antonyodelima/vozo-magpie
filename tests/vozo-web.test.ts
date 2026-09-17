import { describe, expect, it } from "vitest";

import { isInternalUrl, MOBILE_POLISH_SCRIPT, SITE_ORIGIN, SITE_URL } from "../lib/vozo-web";

describe("Vozo WebView URL policy", () => {
  it("keeps the configured Vozo origin and nested routes inside the app", () => {
    expect(isInternalUrl(SITE_ORIGIN)).toBe(true);
    expect(isInternalUrl(`${SITE_ORIGIN}/gravar`)).toBe(true);
    expect(isInternalUrl(`${SITE_ORIGIN}/tts?voice=demo`)).toBe(true);
  });

  it("does not treat lookalike domains as internal", () => {
    expect(isInternalUrl("https://vozo-voice-lab.base44.app.evil.example/gravar")).toBe(false);
    expect(isInternalUrl("https://example.com/vozo-voice-lab.base44.app/gravar")).toBe(false);
    expect(isInternalUrl("http://vozo-voice-lab.base44.app/gravar")).toBe(false);
  });

  it("uses HTTPS and includes the mobile viewport hardening script", () => {
    expect(SITE_URL.startsWith("https://")).toBe(true);
    expect(MOBILE_POLISH_SCRIPT).toContain("viewport-fit=cover");
    expect(MOBILE_POLISH_SCRIPT).toContain("base44-edit-badge");
  });
});
