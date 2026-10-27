"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { api, Cart } from "@/lib/api";
import { formatPrice } from "@/lib/utils";
import { Button } from "@/components/ui/button";

export default function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null);
  const [error, setError] = useState("");

  async function load() {
    try {
      setCart(await api.cart());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Нужен вход");
    }
  }

  useEffect(() => {
    void load();
  }, []);

  async function updateQty(id: number, quantity: number) {
    setCart(await api.updateCartItem(id, quantity));
  }

  async function remove(id: number) {
    setCart(await api.removeCartItem(id));
  }

  if (error) {
    return (
      <p>
        {error}. <Link href="/login">Войти</Link>
      </p>
    );
  }
  if (!cart) return <p className="text-[var(--muted)]">Загрузка…</p>;

  return (
    <div className="space-y-6">
      <h1 className="font-[family-name:var(--font-display)] text-3xl">Корзина</h1>
      {cart.items.length === 0 ? (
        <p className="text-[var(--muted)]">
          Пусто. <Link href="/catalog">В каталог</Link>
        </p>
      ) : (
        <>
          <ul className="space-y-3">
            {cart.items.map((item) => (
              <li key={item.id} className="flex flex-wrap items-center justify-between gap-3 rounded-xl border border-[var(--line)] bg-[var(--panel)] p-4">
                <div>
                  <p className="font-medium">{item.productName}</p>
                  <p className="text-sm text-[var(--muted)]">
                    {item.brand} · {formatPrice(item.unitPriceCents)}
                  </p>
                </div>
                <div className="flex items-center gap-2">
                  <Button variant="secondary" onClick={() => updateQty(item.id, Math.max(1, item.quantity - 1))}>
                    -
                  </Button>
                  <span>{item.quantity}</span>
                  <Button variant="secondary" onClick={() => updateQty(item.id, item.quantity + 1)}>
                    +
                  </Button>
                  <Button variant="ghost" onClick={() => remove(item.id)}>
                    Удалить
                  </Button>
                </div>
              </li>
            ))}
          </ul>
          <div className="flex items-center justify-between">
            <p className="text-xl">Итого: {formatPrice(cart.totalCents)}</p>
            <Link href="/checkout">
              <Button>Оформить</Button>
            </Link>
          </div>
        </>
      )}
    </div>
  );
}
