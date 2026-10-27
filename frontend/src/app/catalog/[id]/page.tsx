"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { api, Product } from "@/lib/api";
import { formatPrice } from "@/lib/utils";
import { Button } from "@/components/ui/button";

export default function ProductPage() {
  const params = useParams<{ id: string }>();
  const router = useRouter();
  const [product, setProduct] = useState<Product | null>(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    api
      .product(params.id)
      .then(setProduct)
      .catch((err) => setError(err instanceof Error ? err.message : "Не найдено"));
  }, [params.id]);

  async function addToCart() {
    if (!product) return;
    try {
      await api.addToCart(product.id, 1);
      setMessage("Добавлено в корзину");
      router.push("/cart");
    } catch (err) {
      setError(err instanceof Error ? err.message : "Не удалось добавить");
    }
  }

  if (error && !product) return <p className="text-red-300">{error}</p>;
  if (!product) return <p className="text-[var(--muted)]">Загрузка…</p>;

  return (
    <article className="grid gap-8 md:grid-cols-2">
      <div className="flex min-h-72 items-center justify-center rounded-2xl border border-[var(--line)] bg-[var(--panel)]">
        {product.imageUrl ? (
          // eslint-disable-next-line @next/next/no-img-element
          <img src={product.imageUrl} alt={product.name} className="max-h-80 object-contain" />
        ) : (
          <span className="text-[var(--muted)]">Нет фото</span>
        )}
      </div>
      <div>
        <p className="text-sm text-[var(--muted)]">{product.brand}</p>
        <h1 className="mt-2 font-[family-name:var(--font-display)] text-4xl">{product.name}</h1>
        <p className="mt-4 text-2xl text-[var(--accent)]">{formatPrice(product.priceCents)}</p>
        <p className="mt-4 text-[var(--muted)]">{product.description}</p>
        <pre className="mt-4 overflow-auto rounded-md border border-[var(--line)] bg-black/20 p-3 text-xs">
          {product.specsJson}
        </pre>
        <p className="mt-3 text-sm">В наличии: {product.stock}</p>
        <div className="mt-6 flex gap-3">
          <Button onClick={addToCart} disabled={product.stock < 1}>
            В корзину
          </Button>
        </div>
        {message && <p className="mt-3 text-[var(--accent)]">{message}</p>}
        {error && <p className="mt-3 text-red-300">{error}</p>}
      </div>
    </article>
  );
}
