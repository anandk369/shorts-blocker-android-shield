
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface StatusCardProps {
  title: string;
  description: string;
  status: boolean;
  onAction: () => void;
  actionText: string;
  actionSuccessText: string;
  className?: string;
}

export function StatusCard({
  title,
  description,
  status,
  onAction,
  actionText,
  actionSuccessText,
  className
}: StatusCardProps) {
  return (
    <Card className={cn("overflow-hidden", className)}>
      <CardHeader>
        <CardTitle className="flex items-center justify-between">
          {title}
          <span className={cn(
            "inline-block h-2.5 w-2.5 rounded-full",
            status ? "bg-green-500" : "bg-gray-300"
          )}></span>
        </CardTitle>
        <CardDescription>{description}</CardDescription>
      </CardHeader>
      <CardContent>
        <Button 
          variant={status ? "outline" : "default"}
          onClick={onAction}
          className={cn(
            "w-full",
            status && "border-green-200 bg-green-50 text-green-700 hover:bg-green-100"
          )}
        >
          {status ? actionSuccessText : actionText}
        </Button>
      </CardContent>
    </Card>
  );
}
