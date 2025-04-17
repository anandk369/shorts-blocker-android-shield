
import { registerPlugin } from '@capacitor/core';

export interface ShortsBlockerPlugin {
  start(): Promise<void>;
  stop(): Promise<void>;
  checkPermission(): Promise<{ granted: boolean }>;
  requestPermission(): Promise<{ granted: boolean }>;
  checkOverlayPermission(): Promise<{ granted: boolean }>;
  requestOverlayPermission(): Promise<{ granted: boolean }>;
  isServiceRunning(): Promise<{ running: boolean }>;
}

const AccessibilityService = registerPlugin<ShortsBlockerPlugin>('ShortsBlocker');

export default AccessibilityService;
