"use client";

import { FormEvent, useEffect, useState } from "react";
import { api, Order, Product } from "@/lib/api";
import { formatPrice } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function AdminPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [error, setError] = useState("");
  const [name, setName] = useState("");
  const [brand, setBrand] = useState("");
  const [price, setPrice] = useState("");
  const [stock, setStock] = useState("1");

  async function load() {
    try {
      setProducts(await api.adminProducts());
      setOrders(await api.adminOrders());
    } catch (err) {
      setError(err instanceof Error ? err.message : "Нет доступа");
    }
  }

  useEffect(() => {
    void load();
  }, []);

  async function createProduct(event: FormEvent) {
    event.preventDefault();
    await api.adminCreateProduct({
      name,
      brand,
      description: "",
      priceCents: Math.round(Number(price) * 100),
      stock: Number(stock),
      specsJson: "{}",
      active: true,
    });
    setName("");
    setBrand("");
    setPrice("");
    await load();
  }

  async function toggleActive(product: Product) {
    await api.adminSetActive(product.id, !product.active);
    await load();
  }

  if (error) return <p className="text-red-300">{error}</p>;

  return (
    <div className="space-y-8">
      <h1 className="font-[family-name:var(--font-display)] text-3xl">Админка</h1>
      <section className="space-y-3">
        <h2 className="text-xl">Новый товар</h2>
        <form onSubmit={createProduct} className="grid gap-3 md:grid-cols-5">
          <Input placeholder="Название" value={name} onChange={(e) => setName(e.target.value)} required />
          <Input placeholder="Бренд" value={brand} onChange={(e) => setBrand(e.target.value)} required />
          <Input placeholder="Цена ₽" value={price} onChange={(e) => setPrice(e.target.value)} required />
          <Input placeholder="Сток" value={stock} onChange={(e) => setStock(e.target.value)} required />
          <Button type="submit">Создать</Button>
        </form>
      </section>
      <section className="space-y-3">
        <h2 className="text-xl">Товары</h2>
        <ul className="space-y-2">
          {products.map((product) => (
            <li key={product.id} className="flex items-center justify-between rounded-lg border border-[var(--line)] p-3">
              <span>
                {product.name} · {formatPrice(product.priceCents)} · {product.active ? "в продаже" : "снят"}
              </span>
              <Button variant="secondary" onClick={() => toggleActive(product)}>
                {product.active ? "Снять" : "Вернуть"}
              </Button>
            </li>
          ))}
        </ul>
      </section>
      <section className="space-y-3">
        <h2 className="text-xl">Заказы</h2>
        <ul className="space-y-2">
          {orders.map((order) => (
            <li key={order.id} className="rounded-lg border border-[var(--line)] p-3 text-sm">
              #{order.id} · {order.status} · {formatPrice(order.totalCents)} · {order.contactName}
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
