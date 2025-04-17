
package app.lovable.plugin

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "ShortsBlocker")
class ShortsBlockerPlugin : Plugin() {

    private val OVERLAY_PERMISSION_REQ_CODE = 1234
    private val ACCESSIBILITY_PERMISSION_REQ_CODE = 5678
    private var savedOverlayCall: PluginCall? = null
    private var savedAccessibilityCall: PluginCall? = null

    @PluginMethod
    fun start(call: PluginCall) {
        val intent = Intent(context, YoutubeShortsBlockerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
        call.resolve()
    }

    @PluginMethod
    fun stop(call: PluginCall) {
        val intent = Intent(context, YoutubeShortsBlockerService::class.java)
        context.stopService(intent)
        call.resolve()
    }

    @PluginMethod
    fun checkPermission(call: PluginCall) {
        val ret = JSObject()
        val enabled = isAccessibilityServiceEnabled(context, YoutubeShortsBlockerService::class.java)
        ret.put("granted", enabled)
        call.resolve(ret)
    }

    @PluginMethod
    fun checkOverlayPermission(call: PluginCall) {
        val ret = JSObject()
        val hasPermission = Settings.canDrawOverlays(context)
        ret.put("granted", hasPermission)
        call.resolve(ret)
    }

    @PluginMethod
    fun requestPermission(call: PluginCall) {
        savedAccessibilityCall = call
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivityForResult(call, intent, ACCESSIBILITY_PERMISSION_REQ_CODE)
    }

    @PluginMethod
    fun requestOverlayPermission(call: PluginCall) {
        savedOverlayCall = call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + context.packageName)
            )
            startActivityForResult(call, intent, OVERLAY_PERMISSION_REQ_CODE)
        } else {
            val ret = JSObject()
            ret.put("granted", true)
            call.resolve(ret)
        }
    }

    @PluginMethod
    fun isServiceRunning(call: PluginCall) {
        val ret = JSObject()
        val running = YoutubeShortsBlockerService.isRunning
        ret.put("running", running)
        call.resolve(ret)
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.handleOnActivityResult(requestCode, resultCode, data)

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE && savedOverlayCall != null) {
            val ret = JSObject()
            val hasPermission = Settings.canDrawOverlays(context)
            ret.put("granted", hasPermission)
            savedOverlayCall!!.resolve(ret)
            savedOverlayCall = null
        } else if (requestCode == ACCESSIBILITY_PERMISSION_REQ_CODE && savedAccessibilityCall != null) {
            val ret = JSObject()
            val enabled = isAccessibilityServiceEnabled(context, YoutubeShortsBlockerService::class.java)
            ret.put("granted", enabled)
            savedAccessibilityCall!!.resolve(ret)
            savedAccessibilityCall = null
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<out AccessibilityService>): Boolean {
        val prefString = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return prefString?.contains(context.packageName + "/" + serviceClass.name) == true
    }

    class YoutubeShortsBlockerService : AccessibilityService() {
        companion object {
            var isRunning = false
        }

        private var windowManager: WindowManager? = null
        private lateinit var overlayManager: BlockerOverlayManager
        private var isYouTubeActive = false
        private var isShortsActive = false

        override fun onCreate() {
            super.onCreate()
            isRunning = true
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        }

        override fun onDestroy() {
            super.onDestroy()
            isRunning = false
            removeOverlay()
        }

        override fun onServiceConnected() {
            val info = AccessibilityServiceInfo()
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            info.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            info.notificationTimeout = 100
            serviceInfo = info
            
            // Start as foreground service for Android 8+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Create notification channel
                NotificationHelper.createNotificationChannel(this)
                
                val notification = android.app.Notification.Builder(this, NotificationHelper.CHANNEL_ID)
                    .setContentTitle("YouTube Shorts Blocker")
                    .setContentText("Running in background to block Shorts content")
                    .setSmallIcon(R.drawable.ic_shield)
                    .build()
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
                } else {
                    startForeground(1, notification)
                }
            }
        }

        override fun onAccessibilityEvent(event: AccessibilityEvent) {
            if (event.packageName == null) return

            // Check if YouTube app is in foreground
            val packageName = event.packageName.toString()
            isYouTubeActive = packageName == "com.google.android.youtube"

            if (!isYouTubeActive) {
                removeOverlay()
                isShortsActive = false
                return
            }

            // For YouTube app, check if we're in Shorts
            if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || 
                event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
                val rootNode = rootInActiveWindow ?: return

                val isCurrentlyInShorts = isShortsInterface(rootNode)
                rootNode.recycle()

                // State has changed, update overlay
                if (isCurrentlyInShorts != isShortsActive) {
                    isShortsActive = isCurrentlyInShorts
                    
                    if (isShortsActive) {
                        showOverlay()
                    } else {
                        removeOverlay()
                    }
                }
            }
        }

        private fun isShortsInterface(rootNode: AccessibilityNodeInfo): Boolean {
            // Method 1: Check URL in browser bar for "/shorts/"
            val urlBarNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/url_bar")
            for (urlNode in urlBarNodes) {
                if (urlNode.text?.toString()?.contains("/shorts/") == true) {
                    return true
                }
            }

            // Method 2: Look for Shorts-specific UI elements
            val shortsTabNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/shorts_tab")
            if (shortsTabNodes.isNotEmpty()) {
                for (node in shortsTabNodes) {
                    if (node.isSelected) {
                        return true
                    }
                }
            }

            // Method 3: Check for Shorts title text
            val allNodes = rootNode.findAccessibilityNodeInfosByText("Shorts")
            for (node in allNodes) {
                if (node.className?.toString()?.contains("TextView") == true) {
                    val nodeCompat = AccessibilityNodeInfoCompat.wrap(node)
                    if (nodeCompat.isSelected || nodeCompat.isChecked) {
                        return true
                    }
                }
            }

            // Method 4: Look for vertical video player which is typical for Shorts
            val playerNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/player_view")
            if (playerNodes.isNotEmpty()) {
                // Check nearby elements for Shorts indicators
                val shortsIndicators = rootNode.findAccessibilityNodeInfosByText("Like")
                val commentsIndicators = rootNode.findAccessibilityNodeInfosByText("Comments")
                
                if (shortsIndicators.isNotEmpty() && commentsIndicators.isNotEmpty()) {
                    return true
                }
            }

            return false
        }

        private fun showOverlay() {
            overlayManager.showOverlay()
        }

        private fun removeOverlay() {
            overlayManager.hideOverlay()
        }

        override fun onInterrupt() {
            // Service interrupted
        }
    }
}
