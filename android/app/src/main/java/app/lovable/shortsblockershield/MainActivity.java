
package app.lovable.shortsblockershield;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Register plugins
        // Add plugins registration here if needed
        registerPlugin(app.lovable.plugin.ShortsBlockerPlugin.class);
    }
}
