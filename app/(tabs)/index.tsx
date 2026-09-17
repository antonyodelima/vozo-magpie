import { useCallback, useEffect, useRef, useState } from "react";
import type { ComponentType } from "react";
import {
  ActivityIndicator,
  BackHandler,
  Linking,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  View,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { WebView, type WebViewNavigation } from "react-native-webview";
import type { WebViewErrorEvent, WebViewMessageEvent } from "react-native-webview/lib/WebViewTypes";
import { StatusBar } from "expo-status-bar";

import { isInternalUrl, MOBILE_POLISH_SCRIPT, SITE_URL } from "@/lib/vozo-web";

const NativeWebView = WebView as unknown as ComponentType<any>;

export default function HomeScreen() {
  const webViewRef = useRef<WebView>(null);
  const [canGoBack, setCanGoBack] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [hasError, setHasError] = useState(false);

  const handleBackPress = useCallback(() => {
    if (canGoBack) {
      webViewRef.current?.goBack();
      return true;
    }
    return false;
  }, [canGoBack]);

  useEffect(() => {
    if (Platform.OS !== "android") return;
    const subscription = BackHandler.addEventListener("hardwareBackPress", handleBackPress);
    return () => subscription.remove();
  }, [handleBackPress]);

  const handleNavigation = useCallback((request: WebViewNavigation) => {
    if (isInternalUrl(request.url) || request.url === "about:blank") return true;
    Linking.openURL(request.url).catch(() => undefined);
    return false;
  }, []);

  const handleMessage = useCallback((_event: WebViewMessageEvent) => {
    // Reserved for future native actions from the Vozo web app.
  }, []);

  const handleError = useCallback((_event: WebViewErrorEvent) => {
    setIsLoading(false);
    setHasError(true);
  }, []);

  if (Platform.OS === "web") {
    return (
      <View style={styles.root}>
        <StatusBar style="light" />
        <SafeAreaView style={styles.previewState}>
          <Text style={styles.previewTitle}>Vozo Magpie Android</Text>
          <Text style={styles.previewText}>
            A WebView nativa é usada no APK Android. No preview web, abra o estúdio diretamente no navegador.
          </Text>
          <Pressable
            accessibilityRole="button"
            onPress={() => Linking.openURL(SITE_URL)}
            style={({ pressed }) => [styles.retryButton, pressed && styles.pressed]}
          >
            <Text style={styles.retryLabel}>Abrir Vozo Magpie</Text>
          </Pressable>
        </SafeAreaView>
      </View>
    );
  }

  return (
    <View style={styles.root}>
      <StatusBar style="light" backgroundColor="#0b0b12" />
      <SafeAreaView edges={["top", "bottom"]} style={styles.safeArea}>
        {hasError ? (
          <View style={styles.errorState}>
            <Text style={styles.errorTitle}>Não foi possível carregar o Vozo Magpie</Text>
            <Text style={styles.errorText}>Verifique sua conexão e tente novamente.</Text>
            <Pressable
              accessibilityRole="button"
              onPress={() => {
                setHasError(false);
                setIsLoading(true);
                webViewRef.current?.reload();
              }}
              style={({ pressed }) => [styles.retryButton, pressed && styles.pressed]}
            >
              <Text style={styles.retryLabel}>Tentar novamente</Text>
            </Pressable>
          </View>
        ) : (
          <View style={styles.webViewContainer}>
            <NativeWebView
              ref={webViewRef}
              source={{ uri: SITE_URL }}
              style={styles.webView}
              originWhitelist={["https://*", "http://*"]}
              javaScriptEnabled
              domStorageEnabled
              allowsInlineMediaPlayback
              mediaPlaybackRequiresUserAction={false}
              allowsFullscreenVideo
              allowsBackForwardNavigationGestures={false}
              setSupportMultipleWindows={false}
              thirdPartyCookiesEnabled
              sharedCookiesEnabled
              cacheEnabled
              startInLoadingState
              injectedJavaScriptBeforeContentLoaded={MOBILE_POLISH_SCRIPT}
              onNavigationStateChange={(state: WebViewNavigation) => {
                setCanGoBack(state.canGoBack);
                if (state.url && !isInternalUrl(state.url) && state.url !== "about:blank") {
                  Linking.openURL(state.url).catch(() => undefined);
                }
              }}
              onShouldStartLoadWithRequest={handleNavigation}
              onLoadStart={() => {
                setHasError(false);
                setIsLoading(true);
              }}
              onLoadEnd={() => setIsLoading(false)}
              onError={handleError}
              onHttpError={() => setHasError(true)}
              onMessage={handleMessage}
              renderLoading={() => (
                <View style={styles.loadingState}>
                  <ActivityIndicator size="large" color="#c56cff" />
                  <Text style={styles.loadingLabel}>Abrindo Vozo Magpie…</Text>
                </View>
              )}
            />
            {isLoading ? (
              <View pointerEvents="none" style={styles.loadingOverlay}>
                <ActivityIndicator size="small" color="#c56cff" />
              </View>
            ) : null}
          </View>
        )}
      </SafeAreaView>
    </View>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: "#0b0b12" },
  safeArea: { flex: 1, backgroundColor: "#0b0b12" },
  webViewContainer: { flex: 1, overflow: "hidden" },
  webView: { flex: 1, backgroundColor: "#0b0b12" },
  loadingOverlay: {
    position: "absolute",
    top: 10,
    right: 14,
    width: 28,
    height: 28,
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 14,
    backgroundColor: "rgba(20, 16, 31, 0.75)",
  },
  loadingState: {
    ...StyleSheet.absoluteFillObject,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
    backgroundColor: "#0b0b12",
  },
  loadingLabel: { color: "#cfc8dc", fontSize: 15 },
  previewState: { flex: 1, alignItems: "center", justifyContent: "center", padding: 28 },
  previewTitle: { color: "#ffffff", fontSize: 25, fontWeight: "700", textAlign: "center" },
  previewText: { color: "#aaa2b8", fontSize: 15, lineHeight: 22, marginTop: 12, textAlign: "center" },
  errorState: { flex: 1, alignItems: "center", justifyContent: "center", padding: 28 },
  errorTitle: { color: "#ffffff", fontSize: 20, lineHeight: 27, fontWeight: "700", textAlign: "center" },
  errorText: { color: "#aaa2b8", fontSize: 15, lineHeight: 22, marginTop: 10, textAlign: "center" },
  retryButton: { marginTop: 24, borderRadius: 12, paddingHorizontal: 22, paddingVertical: 13, backgroundColor: "#bc5cff" },
  retryLabel: { color: "#170c22", fontWeight: "700", fontSize: 15 },
  pressed: { opacity: 0.78, transform: [{ scale: 0.98 }] },
});
