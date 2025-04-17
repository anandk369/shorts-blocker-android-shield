
package app.lovable.plugin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

object NotificationHelper {
    const val CHANNEL_ID = "shorts_blocker_channel"
    
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannelInternal(context)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannelInternal(context: Context) {
        val name = "YouTube Shorts Blocker"
        val descriptionText = "Background service for blocking YouTube Shorts"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            setShowBadge(false)
        }
        
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
