
package app.lovable.shortsblockerplugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            // Check if accessibility service is enabled
            String enabledServices = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            
            if (enabledServices != null && enabledServices.contains(context.getPackageName() + "/app.lovable.shortsblockerplugin.AccessibilityServicePlugin$ShortsBlockerService")) {
                // Start our service
                Intent serviceIntent = new Intent(context, AccessibilityServicePlugin.ShortsBlockerService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
