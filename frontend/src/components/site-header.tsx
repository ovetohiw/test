"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { api, User } from "@/lib/api";
import { Button } from "@/components/ui/button";

export function SiteHeader() {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    api.me().then(setUser).catch(() => setUser(null));
  }, []);

  async function logout() {
    await api.logout();
    setUser(null);
    window.location.href = "/";
  }

  return (
    <header className="border-b border-[var(--line)] bg-[var(--panel)]/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-4 py-4">
        <Link href="/" className="font-[family-name:var(--font-display)] text-2xl tracking-tight text-[var(--accent)]">
          PulsePhone
        </Link>
        <nav className="flex flex-wrap items-center gap-3 text-sm">
          <Link href="/catalog">Каталог</Link>
          <Link href="/cart">Корзина</Link>
          {user?.role === "ADMIN" && <Link href="/admin">Админ</Link>}
          {user ? (
            <>
              <Link href="/orders">Заказы</Link>
              <span className="text-[var(--muted)]">{user.fullName}</span>
              <Button variant="secondary" onClick={logout}>
                Выйти
              </Button>
            </>
          ) : (
            <>
              <Link href="/login">Вход</Link>
              <Link href="/register">
                <Button>Регистрация</Button>
              </Link>
            </>
          )}
        </nav>
      </div>
    </header>
  );
}
