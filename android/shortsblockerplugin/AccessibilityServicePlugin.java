
package app.lovable.shortsblockerplugin;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.List;

@CapacitorPlugin(name = "AccessibilityService")
public class AccessibilityServicePlugin extends Plugin {

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final int ACCESSIBILITY_PERMISSION_REQ_CODE = 5678;
    private static PluginCall savedOverlayCall;
    private static PluginCall savedAccessibilityCall;

    @PluginMethod
    public void start(PluginCall call) {
        Intent intent = new Intent(getContext(), ShortsBlockerService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getContext().startForegroundService(intent);
        } else {
            getContext().startService(intent);
        }
        call.resolve();
    }

    @PluginMethod
    public void stop(PluginCall call) {
        Intent intent = new Intent(getContext(), ShortsBlockerService.class);
        getContext().stopService(intent);
        call.resolve();
    }

    @PluginMethod
    public void checkPermission(PluginCall call) {
        JSObject ret = new JSObject();
        boolean enabled = isAccessibilityServiceEnabled(getContext(), ShortsBlockerService.class);
        ret.put("granted", enabled);
        call.resolve(ret);
    }

    @PluginMethod
    public void checkOverlayPermission(PluginCall call) {
        JSObject ret = new JSObject();
        boolean hasPermission = Settings.canDrawOverlays(getContext());
        ret.put("granted", hasPermission);
        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermission(PluginCall call) {
        savedAccessibilityCall = call;
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(call, intent, ACCESSIBILITY_PERMISSION_REQ_CODE);
    }

    @PluginMethod
    public void requestOverlayPermission(PluginCall call) {
        savedOverlayCall = call;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getContext().getPackageName()));
            startActivityForResult(call, intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void isServiceRunning(PluginCall call) {
        JSObject ret = new JSObject();
        boolean running = ShortsBlockerService.isRunning;
        ret.put("running", running);
        call.resolve(ret);
    }

    @Override
    protected void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        super.handleOnActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE && savedOverlayCall != null) {
            JSObject ret = new JSObject();
            boolean hasPermission = Settings.canDrawOverlays(getContext());
            ret.put("granted", hasPermission);
            savedOverlayCall.resolve(ret);
            savedOverlayCall = null;
        } else if (requestCode == ACCESSIBILITY_PERMISSION_REQ_CODE && savedAccessibilityCall != null) {
            JSObject ret = new JSObject();
            boolean enabled = isAccessibilityServiceEnabled(getContext(), ShortsBlockerService.class);
            ret.put("granted", enabled);
            savedAccessibilityCall.resolve(ret);
            savedAccessibilityCall = null;
        }
    }

    private boolean isAccessibilityServiceEnabled(Context context, Class<? extends AccessibilityService> service) {
        String prefString = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        return prefString != null && prefString.contains(context.getPackageName() + "/" + service.getName());
    }

    public static class ShortsBlockerService extends AccessibilityService {
        public static boolean isRunning = false;
        private WindowManager windowManager;
        private FrameLayout overlayView;
        private boolean isYouTubeActive = false;
        private boolean isShortsActive = false;

        @Override
        public void onCreate() {
            super.onCreate();
            isRunning = true;
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            isRunning = false;
            removeOverlay();
        }

        @Override
        public void onAccessibilityEvent(AccessibilityEvent event) {
            if (event.getPackageName() == null) return;

            // Check if YouTube app is in foreground
            String packageName = event.getPackageName().toString();
            isYouTubeActive = packageName.equals("com.google.android.youtube");

            if (!isYouTubeActive) {
                removeOverlay();
                isShortsActive = false;
                return;
            }

            // For YouTube app, check if we're in Shorts
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || 
                event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                if (rootNode == null) return;

                boolean isCurrentlyInShorts = isShortsInterface(rootNode);
                rootNode.recycle();

                // State has changed, update overlay
                if (isCurrentlyInShorts != isShortsActive) {
                    isShortsActive = isCurrentlyInShorts;
                    
                    if (isShortsActive) {
                        showOverlay();
                    } else {
                        removeOverlay();
                    }
                }
            }
        }

        private boolean isShortsInterface(AccessibilityNodeInfo rootNode) {
            // Method 1: Check URL in browser bar for "/shorts/"
            List<AccessibilityNodeInfo> urlBarNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/url_bar");
            for (AccessibilityNodeInfo urlNode : urlBarNodes) {
                if (urlNode.getText() != null && urlNode.getText().toString().contains("/shorts/")) {
                    return true;
                }
            }

            // Method 2: Look for Shorts-specific UI elements
            List<AccessibilityNodeInfo> shortsTabNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_tab");
            if (!shortsTabNodes.isEmpty()) {
                for (AccessibilityNodeInfo node : shortsTabNodes) {
                    if (node.isSelected()) {
                        return true;
                    }
                }
            }

            // Method 3: Check for Shorts title text
            List<AccessibilityNodeInfo> allNodes = rootNode.findAccessibilityNodeInfosByText("Shorts");
            for (AccessibilityNodeInfo node : allNodes) {
                if (node.getClassName().toString().contains("TextView")) {
                    AccessibilityNodeInfoCompat compat = AccessibilityNodeInfoCompat.wrap(node);
                    if (compat.isSelected() || compat.isChecked()) {
                        return true;
                    }
                }
            }

            return false;
        }

        private void showOverlay() {
            if (overlayView != null) return;
            if (!Settings.canDrawOverlays(this)) return;

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                            : WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    android.graphics.PixelFormat.TRANSLUCENT);

            overlayView = new FrameLayout(this);
            overlayView.setBackgroundColor(0xFF000000); // Black background

            TextView textView = new TextView(this);
            textView.setText("YouTube Shorts Blocked");
            textView.setTextColor(0xFFFFFFFF); // White text
            textView.setTextSize(24);

            FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            textParams.gravity = android.view.Gravity.CENTER;
            overlayView.addView(textView, textParams);

            windowManager.addView(overlayView, params);
        }

        private void removeOverlay() {
            if (overlayView != null && windowManager != null) {
                try {
                    windowManager.removeView(overlayView);
                } catch (IllegalArgumentException e) {
                    // View already removed
                }
                overlayView = null;
            }
        }

        @Override
        public void onInterrupt() {
            // Service interrupted
        }

        @Override
        protected void onServiceConnected() {
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS | AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            info.notificationTimeout = 100;
            this.setServiceInfo(info);
        }
    }
}
