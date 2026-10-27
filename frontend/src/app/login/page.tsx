"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("buyer@phoneshop.local");
  const [password, setPassword] = useState("Password123!");
  const [error, setError] = useState("");

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    try {
      await api.login(email, password);
      router.push("/catalog");
      router.refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ошибка входа");
    }
  }

  return (
    <div className="mx-auto max-w-md space-y-4 rounded-xl border border-[var(--line)] bg-[var(--panel)] p-6">
      <h1 className="font-[family-name:var(--font-display)] text-2xl">Вход</h1>
      <form className="space-y-3" onSubmit={onSubmit}>
        <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
        <Input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Пароль"
          required
        />
        <Button type="submit" className="w-full">
          Войти
        </Button>
      </form>
      {error && <p className="text-red-300">{error}</p>}
      <p className="text-sm text-[var(--muted)]">
        Нет аккаунта? <Link href="/register">Регистрация</Link>
      </p>
    </div>
  );
}
