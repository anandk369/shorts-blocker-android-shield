
import * as React from "react";
import { cn } from "@/lib/utils";

interface HeadingProps extends React.HTMLAttributes<HTMLHeadingElement> {
  level?: "h1" | "h2" | "h3" | "h4" | "h5" | "h6";
  children: React.ReactNode;
  className?: string;
}

export function Heading({
  level = "h1",
  children,
  className,
  ...props
}: HeadingProps) {
  const Tag = level;

  return (
    <Tag
      className={cn(
        level === "h1" && "text-3xl font-bold",
        level === "h2" && "text-2xl font-semibold",
        level === "h3" && "text-xl font-medium",
        level === "h4" && "text-lg font-medium",
        level === "h5" && "text-base font-medium",
        level === "h6" && "text-sm font-medium",
        className
      )}
      {...props}
    >
      {children}
    </Tag>
  );
}
