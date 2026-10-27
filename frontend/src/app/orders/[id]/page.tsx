"use client";

import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { api, Order } from "@/lib/api";
import { formatPrice } from "@/lib/utils";
import { Button } from "@/components/ui/button";

export default function OrderPage() {
  const params = useParams<{ id: string }>();
  const [order, setOrder] = useState<Order | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .order(params.id)
      .then(setOrder)
      .catch((err) => setError(err instanceof Error ? err.message : "Не найдено"));
  }, [params.id]);

  if (error) return <p className="text-red-300">{error}</p>;
  if (!order) return <p className="text-[var(--muted)]">Загрузка…</p>;

  return (
    <div className="space-y-4 rounded-xl border border-[var(--line)] bg-[var(--panel)] p-6">
      <h1 className="font-[family-name:var(--font-display)] text-3xl">Заказ #{order.id}</h1>
      <p>
        Статус: <strong>{order.status}</strong>
      </p>
      <p>Сумма: {formatPrice(order.totalCents)}</p>
      <p>
        Получение: {order.deliveryMethod}
        {order.deliveryAddress ? ` · ${order.deliveryAddress}` : ""}
      </p>
      <ul className="space-y-2">
        {order.items.map((item) => (
          <li key={`${item.productId}-${item.productName}`} className="text-sm text-[var(--muted)]">
            {item.productName} × {item.quantity} — {formatPrice(item.lineTotalCents)}
          </li>
        ))}
      </ul>
      {order.status === "PENDING_PAYMENT" && order.paymentConfirmationUrl && (
        <a href={order.paymentConfirmationUrl}>
          <Button>Оплатить через ЮKassa</Button>
        </a>
      )}
    </div>
  );
}
