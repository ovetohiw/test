"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { api, Order } from "@/lib/api";
import { formatPrice } from "@/lib/utils";

export default function OrdersPage() {
  const [orders, setOrders] = useState<Order[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .orders()
      .then(setOrders)
      .catch((err) => setError(err instanceof Error ? err.message : "Ошибка"));
  }, []);

  if (error) return <p className="text-red-300">{error}</p>;

  return (
    <div className="space-y-4">
      <h1 className="font-[family-name:var(--font-display)] text-3xl">Мои заказы</h1>
      <ul className="space-y-3">
        {orders.map((order) => (
          <li key={order.id}>
            <Link
              href={`/orders/${order.id}`}
              className="block rounded-xl border border-[var(--line)] bg-[var(--panel)] p-4 hover:border-[var(--accent)]"
            >
              <p className="font-medium">Заказ #{order.id}</p>
              <p className="text-sm text-[var(--muted)]">
                {order.status} · {formatPrice(order.totalCents)}
              </p>
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
