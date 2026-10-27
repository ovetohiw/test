import { cn } from "@/lib/utils";
import { ButtonHTMLAttributes } from "react";

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "ghost";
};

export function Button({ className, variant = "primary", ...props }: Props) {
  return (
    <button
      className={cn(
        "inline-flex items-center justify-center rounded-md px-4 py-2 text-sm font-medium transition disabled:opacity-50",
        variant === "primary" && "bg-[var(--accent)] text-[var(--accent-fg)] hover:brightness-110",
        variant === "secondary" && "border border-[var(--line)] bg-transparent hover:bg-white/5",
        variant === "ghost" && "hover:bg-white/5",
        className,
      )}
      {...props}
    />
  );
}
