
import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'app.lovable.35799ec692e74b02be2d874ecbb4bcd4',
  appName: 'YouTube Shorts Blocker',
  webDir: 'dist',
  server: {
    url: 'https://35799ec6-92e7-4b02-be2d-874ecbb4bcd4.lovableproject.com?forceHideBadge=true',
    cleartext: true
  },
  plugins: {
    ShortsBlocker: {
      enabled: true
    }
  },
  android: {
    buildOptions: {
      keystorePath: "shortsblockershield.keystore",
      keystoreAlias: "shortsblockershield",
      minSdkVersion: 21,
      targetSdkVersion: 33
    },
    networkSecurityConfig: "network_security_config.xml"
  }
};

export default config;
