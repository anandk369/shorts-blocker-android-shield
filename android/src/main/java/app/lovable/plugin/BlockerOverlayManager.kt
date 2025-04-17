
package app.lovable.plugin

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.ImageView
import androidx.cardview.widget.CardView
import android.os.Handler
import android.os.Looper

class BlockerOverlayManager(private val context: Context) {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isShowing = false
    
    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    
    fun showOverlay() {
        if (isShowing || !Settings.canDrawOverlays(context)) return
        
        try {
            // Create layout parameters
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else
                    WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            
            // Inflate overlay layout
            val inflater = LayoutInflater.from(context)
            val rootView = inflater.inflate(R.layout.blocker_overlay, null) as FrameLayout
            
            // Show the overlay
            overlayView = rootView
            windowManager?.addView(overlayView, params)
            isShowing = true
            
            // Apply fade-in animation
            overlayView?.alpha = 0f
            overlayView?.animate()?.alpha(1f)?.setDuration(300)?.start()
            
            // Apply card animation
            val cardView = rootView.findViewById<CardView>(R.id.blocker_card)
            cardView.scaleX = 0.9f
            cardView.scaleY = 0.9f
            cardView.alpha = 0f
            cardView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(400)
                .start()
        } catch (e: Exception) {
            // Handle potential exceptions when adding the view
            e.printStackTrace()
        }
    }
    
    fun hideOverlay() {
        if (!isShowing || overlayView == null) return
        
        try {
            // Apply fade-out animation
            overlayView?.animate()
                ?.alpha(0f)
                ?.setDuration(250)
                ?.withEndAction {
                    try {
                        windowManager?.removeView(overlayView)
                    } catch (e: IllegalArgumentException) {
                        // View already removed
                    }
                    overlayView = null
                    isShowing = false
                }
                ?.start()
        } catch (e: Exception) {
            // Handle potential exceptions when removing the view
            e.printStackTrace()
        }
    }
    
    fun isOverlayShowing(): Boolean {
        return isShowing
    }
}
