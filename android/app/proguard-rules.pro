
# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep your application class if you have one
-keep class app.lovable.shortsblockershield.** { *; }

# Preserve the Capacitor plugins
-keep class app.lovable.plugin.** { *; }

# Preserve the special static methods that are required in all enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep the required classes for Capacitor
-keep class com.getcapacitor.** { *; }
-keep public class * extends com.getcapacitor.Plugin
-keep class * implements com.getcapacitor.Plugin { *; }
-keepclassmembers class * implements com.getcapacitor.Plugin {
    @com.getcapacitor.PluginMethod public <methods>;
}
