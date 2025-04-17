
import React from "react";
import { ShieldCheck } from "lucide-react";
import { cn } from "@/lib/utils";

interface ShieldAnimationProps {
  active: boolean;
  size?: "sm" | "md" | "lg";
  className?: string;
}

export function ShieldAnimation({ 
  active, 
  size = "md", 
  className 
}: ShieldAnimationProps) {
  const sizeMap = {
    sm: { icon: 24, pulse: "w-9 h-9" },
    md: { icon: 36, pulse: "w-12 h-12" },
    lg: { icon: 48, pulse: "w-16 h-16" },
  };
  
  return (
    <div className={cn("relative flex items-center justify-center", className)}>
      {active && (
        <>
          <div className={cn(
            "absolute rounded-full bg-purple-400/20 animate-pulse-slow",
            sizeMap[size].pulse
          )} />
          <div className={cn(
            "absolute rounded-full bg-purple-400/10 animate-pulse-slow [animation-delay:750ms]",
            "transform scale-[1.15]",
            sizeMap[size].pulse
          )} />
        </>
      )}
      <div className={cn(
        "relative z-10 rounded-full p-1.5",
        active ? "bg-gradient-to-br from-purple-600 to-indigo-600 text-white" : "bg-gray-200 text-gray-400"
      )}>
        <ShieldCheck size={sizeMap[size].icon} />
      </div>
    </div>
  );
}
