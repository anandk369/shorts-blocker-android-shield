
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
import androidx.cardview.widget.CardView
import android.os.Handler
import android.os.Looper

class BlockerOverlayManager(private val context: Context) {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var isShowing = false
    private lateinit var cardView: CardView
    private lateinit var cardParams: FrameLayout.LayoutParams
    
    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        // Initialize card view
        cardView = CardView(context).apply {
            radius = 16f * context.resources.displayMetrics.density
            setCardBackgroundColor(Color.parseColor("#8B5CF6")) // Purple color
            elevation = 8f * context.resources.displayMetrics.density
        }
        
        // Add content to the card
        val cardContent = FrameLayout(context)
        val padding = (24 * context.resources.displayMetrics.density).toInt()
        cardContent.setPadding(padding, padding, padding, padding)
        
        // Add text to the card
        val textView = TextView(context).apply {
            text = "YouTube Shorts Blocked"
            setTextColor(Color.WHITE)
            textSize = 18f
        }
        
        val textParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        textParams.gravity = Gravity.CENTER
        cardContent.addView(textView, textParams)
        
        // Add the subtitle
        val subtitleView = TextView(context).apply {
            text = "Swipe away or exit Shorts to continue"
            setTextColor(Color.WHITE)
            alpha = 0.8f
            textSize = 14f
        }
        
        val subtitleParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        subtitleParams.gravity = Gravity.CENTER
        subtitleParams.topMargin = (8 * context.resources.displayMetrics.density).toInt()
        subtitleParams.topMargin += textView.height
        cardContent.addView(subtitleView, subtitleParams)
        
        cardView.addView(cardContent)
        
        // Set up card params
        cardParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.gravity = Gravity.CENTER
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
            
            // Create the overlay container
            val rootView = FrameLayout(context)
            rootView.setBackgroundColor(Color.parseColor("#CC000000")) // Semi-transparent black
            
            // Add the card to the overlay
            rootView.addView(cardView, cardParams)
            
            // Show the overlay
            overlayView = rootView
            windowManager?.addView(overlayView, params)
            isShowing = true
            
            // Apply fade-in animation
            overlayView?.alpha = 0f
            overlayView?.animate()?.alpha(1f)?.setDuration(300)?.start()
            
            // Apply card animation
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
