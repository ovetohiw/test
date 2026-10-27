"use client";

import { FormEvent, useState } from "react";
import { useRouter } from "next/navigation";
import { api } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function CheckoutPage() {
  const router = useRouter();
  const [contactName, setContactName] = useState("");
  const [contactPhone, setContactPhone] = useState("");
  const [deliveryMethod, setDeliveryMethod] = useState<"PICKUP" | "DELIVERY">("PICKUP");
  const [deliveryAddress, setDeliveryAddress] = useState("");
  const [error, setError] = useState("");

  async function onSubmit(event: FormEvent) {
    event.preventDefault();
    try {
      const order = await api.createOrder({
        contactName,
        contactPhone,
        deliveryMethod,
        deliveryAddress: deliveryMethod === "DELIVERY" ? deliveryAddress : undefined,
      });
      if (order.paymentConfirmationUrl) {
        window.location.href = order.paymentConfirmationUrl;
        return;
      }
      router.push(`/orders/${order.id}`);
    } catch (err) {
      setError(err instanceof Error ? err.message : "Не удалось создать заказ");
    }
  }

  return (
    <div className="mx-auto max-w-lg space-y-4 rounded-xl border border-[var(--line)] bg-[var(--panel)] p-6">
      <h1 className="font-[family-name:var(--font-display)] text-2xl">Оформление заказа</h1>
      <form className="space-y-3" onSubmit={onSubmit}>
        <Input value={contactName} onChange={(e) => setContactName(e.target.value)} placeholder="Контактное имя" required />
        <Input value={contactPhone} onChange={(e) => setContactPhone(e.target.value)} placeholder="Телефон" required />
        <select
          className="w-full rounded-md border border-[var(--line)] bg-[var(--panel)] px-3 py-2 text-sm"
          value={deliveryMethod}
          onChange={(e) => setDeliveryMethod(e.target.value as "PICKUP" | "DELIVERY")}
        >
          <option value="PICKUP">Самовывоз</option>
          <option value="DELIVERY">Доставка</option>
        </select>
        {deliveryMethod === "DELIVERY" && (
          <Input
            value={deliveryAddress}
            onChange={(e) => setDeliveryAddress(e.target.value)}
            placeholder="Адрес доставки"
            required
          />
        )}
        <Button type="submit" className="w-full">
          Перейти к оплате ЮKassa
        </Button>
      </form>
      {error && <p className="text-red-300">{error}</p>}
    </div>
  );
}
