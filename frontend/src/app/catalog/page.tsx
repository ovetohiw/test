"use client";

import Link from "next/link";
import { FormEvent, useEffect, useState } from "react";
import { api, Product } from "@/lib/api";
import { formatPrice } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export default function CatalogPage() {
  const [products, setProducts] = useState<Product[]>([]);
  const [brand, setBrand] = useState("");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [error, setError] = useState("");

  async function load(params?: { brand?: string; minPrice?: string; maxPrice?: string }) {
    try {
      setError("");
      const min = params?.minPrice ? String(Number(params.minPrice) * 100) : undefined;
      const max = params?.maxPrice ? String(Number(params.maxPrice) * 100) : undefined;
      setProducts(await api.products({ brand: params?.brand, minPrice: min, maxPrice: max }));
    } catch (err) {
      setError(err instanceof Error ? err.message : "Не удалось загрузить каталог");
    }
  }

  useEffect(() => {
    void load();
  }, []);

  function onFilter(event: FormEvent) {
    event.preventDefault();
    void load({ brand, minPrice, maxPrice });
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="font-[family-name:var(--font-display)] text-3xl">Каталог</h1>
        <p className="text-[var(--muted)]">Фильтр по бренду и цене</p>
      </div>
      <form onSubmit={onFilter} className="grid gap-3 md:grid-cols-4">
        <Input placeholder="Бренд" value={brand} onChange={(e) => setBrand(e.target.value)} />
        <Input placeholder="Цена от, ₽" value={minPrice} onChange={(e) => setMinPrice(e.target.value)} />
        <Input placeholder="Цена до, ₽" value={maxPrice} onChange={(e) => setMaxPrice(e.target.value)} />
        <Button type="submit">Применить</Button>
      </form>
      {error && <p className="text-red-300">{error}</p>}
      <div className="grid gap-4 md:grid-cols-2">
        {products.map((product) => (
          <Link
            key={product.id}
            href={`/catalog/${product.id}`}
            className="rounded-xl border border-[var(--line)] bg-[var(--panel)] p-5 transition hover:border-[var(--accent)]"
          >
            <p className="text-sm text-[var(--muted)]">{product.brand}</p>
            <h2 className="mt-1 text-xl font-semibold">{product.name}</h2>
            <p className="mt-3 text-[var(--accent)]">{formatPrice(product.priceCents)}</p>
            <p className="mt-1 text-sm text-[var(--muted)]">В наличии: {product.stock}</p>
          </Link>
        ))}
      </div>
    </div>
  );
}
