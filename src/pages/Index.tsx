
import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Switch } from "@/components/ui/switch";
import { toast } from "@/components/ui/use-toast";
import { Heading } from "@/components/ui/heading";
import { StatusCard } from "@/components/StatusCard";
import { ShieldAnimation } from "@/components/ShieldAnimation";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { ShieldCheck, ShieldAlert, Play, Settings, Info, ExternalLink } from "lucide-react";
import AccessibilityService from "@/services/AccessibilityPlugin";

const Index = () => {
  const [accessibilityPermission, setAccessibilityPermission] = useState(false);
  const [overlayPermission, setOverlayPermission] = useState(false);
  const [serviceRunning, setServiceRunning] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkPermissions();
  }, []);

  const checkPermissions = async () => {
    try {
      setLoading(true);
      
      // Check accessibility permission
      const accessResult = await AccessibilityService.checkPermission();
      setAccessibilityPermission(accessResult.granted);
      
      // Check overlay permission
      const overlayResult = await AccessibilityService.checkOverlayPermission();
      setOverlayPermission(overlayResult.granted);
      
      // Check if service is running
      const serviceResult = await AccessibilityService.isServiceRunning();
      setServiceRunning(serviceResult.running);
    } catch (error) {
      console.error("Error checking permissions:", error);
      toast({
        title: "Error",
        description: "Failed to check app permissions",
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  const requestAccessibilityPermission = async () => {
    try {
      const result = await AccessibilityService.requestPermission();
      setAccessibilityPermission(result.granted);
      if (result.granted) {
        toast({
          title: "Success",
          description: "Accessibility permission granted!",
        });
      }
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to request accessibility permission",
        variant: "destructive",
      });
    }
  };

  const requestOverlayPermission = async () => {
    try {
      const result = await AccessibilityService.requestOverlayPermission();
      setOverlayPermission(result.granted);
      if (result.granted) {
        toast({
          title: "Success",
          description: "Overlay permission granted!",
        });
      }
    } catch (error) {
      toast({
        title: "Error",
        description: "Failed to request overlay permission",
        variant: "destructive",
      });
    }
  };

  const toggleService = async () => {
    try {
      if (serviceRunning) {
        await AccessibilityService.stop();
        setServiceRunning(false);
        toast({
          title: "Service Stopped",
          description: "YouTube Shorts Blocker is now disabled",
        });
      } else {
        if (!accessibilityPermission || !overlayPermission) {
          toast({
            title: "Required Permissions",
            description: "Please grant all required permissions first",
            variant: "destructive",
          });
          return;
        }
        
        await AccessibilityService.start();
        setServiceRunning(true);
        toast({
          title: "Service Started",
          description: "YouTube Shorts Blocker is now active!",
        });
      }
    } catch (error) {
      console.error("Error toggling service:", error);
      toast({
        title: "Error",
        description: "Failed to toggle service",
        variant: "destructive",
      });
    }
  };

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-4 bg-gradient-to-br from-purple-50 to-blue-50">
      <header className="mb-6 text-center">
        <div className="flex items-center justify-center mb-2">
          <ShieldAnimation active={true} size="lg" />
        </div>
        <Heading level="h1" className="text-3xl font-bold text-gray-800">
          YouTube Shorts Blocker
        </Heading>
        <p className="text-gray-600 mt-1">Take control of your screen time</p>
      </header>

      <Tabs defaultValue="protection" className="w-full max-w-md">
        <TabsList className="grid w-full grid-cols-3 mb-6">
          <TabsTrigger value="protection" className="flex items-center justify-center gap-1.5">
            <ShieldCheck size={16} />
            <span>Protection</span>
          </TabsTrigger>
          <TabsTrigger value="settings" className="flex items-center justify-center gap-1.5">
            <Settings size={16} />
            <span>Settings</span>
          </TabsTrigger>
          <TabsTrigger value="about" className="flex items-center justify-center gap-1.5">
            <Info size={16} />
            <span>About</span>
          </TabsTrigger>
        </TabsList>
        
        <TabsContent value="protection" className="space-y-6 mt-2">
          <Card className="p-6 shadow-lg border-t-4 border-t-purple-500 animate-shield-appear">
            <div className="flex items-center mb-4">
              <ShieldAnimation active={serviceRunning} />
              <div className="ml-4">
                <h2 className="font-bold text-xl text-gray-800">
                  {serviceRunning ? "Protection Active" : "Protection Inactive"}
                </h2>
                <p className="text-sm text-gray-600">
                  {serviceRunning 
                    ? "YouTube Shorts are being blocked" 
                    : "YouTube Shorts are not currently blocked"}
                </p>
              </div>
            </div>
            
            <div className="flex items-center justify-between px-3 py-4 bg-gray-50 rounded-lg">
              <div className="flex-1">
                <span className="font-medium text-gray-700">Shorts Blocker</span>
              </div>
              <Switch
                checked={serviceRunning}
                onCheckedChange={toggleService}
                disabled={loading || (!accessibilityPermission || !overlayPermission)}
              />
            </div>
            
            <div className="mt-5 bg-purple-50 border border-purple-100 rounded-lg p-3 text-sm text-purple-700">
              {!accessibilityPermission || !overlayPermission ? (
                <div className="flex gap-2">
                  <ShieldAlert size={18} className="text-purple-700 flex-shrink-0 mt-0.5" />
                  <span>Complete the setup in Settings tab to enable protection</span>
                </div>
              ) : !serviceRunning ? (
                <div className="flex gap-2">
                  <Play size={18} className="text-purple-700 flex-shrink-0 mt-0.5" />
                  <span>Toggle the switch to start blocking YouTube Shorts</span>
                </div>
              ) : (
                <div className="flex gap-2">
                  <ShieldCheck size={18} className="text-purple-700 flex-shrink-0 mt-0.5" />
                  <span>Active and protecting you from YouTube Shorts! Go try it out.</span>
                </div>
              )}
            </div>
          </Card>
        </TabsContent>
        
        <TabsContent value="settings" className="space-y-4 mt-2">
          <StatusCard
            title="Accessibility Service"
            description="Detects YouTube Shorts content"
            status={accessibilityPermission}
            onAction={requestAccessibilityPermission}
            actionText="Grant Permission"
            actionSuccessText="Permission Granted ✓"
            className="animate-shield-appear"
          />
          
          <StatusCard
            title="Overlay Permission"
            description="Allows blocking screen content"
            status={overlayPermission}
            onAction={requestOverlayPermission}
            actionText="Grant Permission"
            actionSuccessText="Permission Granted ✓"
            className="animate-shield-appear [animation-delay:75ms]"
          />
          
          <Card className="p-4 mt-4 bg-blue-50 text-blue-800 border-blue-100 animate-shield-appear [animation-delay:150ms]">
            <div className="flex gap-2">
              <Info size={18} className="flex-shrink-0 mt-0.5" />
              <div className="text-sm">
                <p>Both permissions are required for the app to function properly. The app will start automatically when your device boots up.</p>
              </div>
            </div>
          </Card>
        </TabsContent>
        
        <TabsContent value="about" className="space-y-4 mt-2">
          <Card className="p-6 animate-shield-appear">
            <Heading level="h3" className="mb-3 text-gray-800 flex items-center">
              <Info size={18} className="mr-2" />
              How It Works
            </Heading>
            <ul className="space-y-2 text-sm text-gray-700">
              <li className="flex gap-2">
                <span className="text-purple-600 font-bold">1.</span>
                <span>The app uses Android's Accessibility Service to monitor the YouTube app</span>
              </li>
              <li className="flex gap-2">
                <span className="text-purple-600 font-bold">2.</span>
                <span>When Shorts content is detected, an overlay is displayed</span>
              </li>
              <li className="flex gap-2">
                <span className="text-purple-600 font-bold">3.</span>
                <span>The overlay automatically disappears when you leave Shorts</span>
              </li>
              <li className="flex gap-2">
                <span className="text-purple-600 font-bold">4.</span>
                <span>No data is collected or transmitted outside your device</span>
              </li>
            </ul>
            
            <div className="mt-5 border-t border-gray-100 pt-4">
              <Heading level="h3" className="mb-3 text-gray-800 flex items-center">
                <ExternalLink size={18} className="mr-2" />
                Resources
              </Heading>
              <div className="space-y-2 text-sm">
                <a href="#" className="block py-2 px-3 bg-purple-50 text-purple-700 rounded-md hover:bg-purple-100">
                  Read Documentation
                </a>
                <a href="#" className="block py-2 px-3 bg-purple-50 text-purple-700 rounded-md hover:bg-purple-100">
                  Privacy Policy
                </a>
              </div>
            </div>
          </Card>
        </TabsContent>
      </Tabs>
      
      <footer className="mt-8 text-center text-xs text-gray-500">
        <p>© 2025 YouTube Shorts Blocker</p>
        <p className="mt-1">Your screen time, your control</p>
      </footer>
    </div>
  );
};

export default Index;
