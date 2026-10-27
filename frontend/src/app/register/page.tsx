"use client";

import Link from "next/link";
import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function RegisterPage() {
  const router = useRouter();
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    try {
      await api.register(email, password, fullName);
      router.push("/catalog");
      router.refresh();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ошибка регистрации");
    }
  }

  return (
    <div className="mx-auto max-w-md space-y-4 rounded-xl border border-[var(--line)] bg-[var(--panel)] p-6">
      <h1 className="font-[family-name:var(--font-display)] text-2xl">Регистрация</h1>
      <form className="space-y-3" onSubmit={onSubmit}>
        <Input value={fullName} onChange={(e) => setFullName(e.target.value)} placeholder="Имя" required />
        <Input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email" required />
        <Input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Пароль (мин. 8)"
          minLength={8}
          required
        />
        <Button type="submit" className="w-full">
          Создать аккаунт
        </Button>
      </form>
      {error && <p className="text-red-300">{error}</p>}
      <p className="text-sm text-[var(--muted)]">
        Уже есть аккаунт? <Link href="/login">Войти</Link>
      </p>
    </div>
  );
}
