import "./scripts/load-env.js";
import type { ExpoConfig } from "expo/config";

const env = {
  appName: "Vozo Magpie",
  appSlug: "vozo-magpie-android",
  androidPackage: "com.vozomagpie.android",
  iosBundleId: "com.vozomagpie.android",
  scheme: "vozomagpie",
};

const config: ExpoConfig = {
  name: env.appName,
  slug: env.appSlug,
  version: "1.0.0",
  orientation: "portrait",
  icon: "./assets/images/icon.png",
  scheme: env.scheme,
  userInterfaceStyle: "dark",
  newArchEnabled: true,
  ios: {
    supportsTablet: true,
    bundleIdentifier: env.iosBundleId,
    infoPlist: {
      ITSAppUsesNonExemptEncryption: false,
      NSMicrophoneUsageDescription: "Permita que o Vozo Magpie grave amostras de voz e converta áudios.",
    },
  },
  android: {
    adaptiveIcon: {
      backgroundColor: "#160c24",
      foregroundImage: "./assets/images/android-icon-foreground.png",
      backgroundImage: "./assets/images/android-icon-background.png",
      monochromeImage: "./assets/images/android-icon-monochrome.png",
    },
    edgeToEdgeEnabled: true,
    predictiveBackGestureEnabled: false,
    package: env.androidPackage,
    permissions: ["INTERNET", "RECORD_AUDIO"],
    intentFilters: [
      {
        action: "VIEW",
        data: [{ scheme: env.scheme, host: "*" }],
        category: ["BROWSABLE", "DEFAULT"],
      },
    ],
  },
  web: {
    bundler: "metro",
    output: "static",
    favicon: "./assets/images/favicon.png",
  },
  plugins: [
    "expo-router",
    [
      "expo-splash-screen",
      {
        image: "./assets/images/splash-icon.png",
        imageWidth: 200,
        resizeMode: "contain",
        backgroundColor: "#0b0b12",
        dark: { backgroundColor: "#0b0b12" },
      },
    ],
    [
      "expo-build-properties",
      {
        android: {
          buildArchs: ["armeabi-v7a", "arm64-v8a"],
          minSdkVersion: 24,
        },
      },
    ],
  ],
  experiments: {
    typedRoutes: true,
    reactCompiler: true,
  },
};

export default config;
