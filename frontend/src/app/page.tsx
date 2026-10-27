import Link from "next/link";
import { Button } from "@/components/ui/button";

export default function HomePage() {
  return (
    <section className="relative overflow-hidden rounded-2xl border border-[var(--line)] bg-[var(--panel)] px-8 py-16 md:px-14 md:py-24">
      <div
        className="pointer-events-none absolute inset-0 opacity-40"
        style={{
          backgroundImage:
            "linear-gradient(120deg, transparent 20%, rgba(47,211,197,0.12) 50%, transparent 80%), url('data:image/svg+xml;utf8,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%22160%22 height=%22160%22><path d=%22M0 80h160M80 0v160%22 stroke=%22%23243445%22 stroke-width=%221%22/></svg>')",
        }}
      />
      <div className="relative max-w-2xl">
        <p className="font-[family-name:var(--font-display)] text-5xl font-semibold tracking-tight text-[var(--accent)] md:text-6xl">
          PulsePhone
        </p>
        <h1 className="mt-4 text-3xl font-semibold leading-tight md:text-4xl">Телефоны с честной ценой и быстрым заказом</h1>
        <p className="mt-4 max-w-xl text-[var(--muted)]">
          Каталог актуальных моделей, корзина и оплата через ЮKassa — от выбора до подтверждения заказа в одном сценарии.
        </p>
        <div className="mt-8 flex gap-3">
          <Link href="/catalog">
            <Button>Смотреть каталог</Button>
          </Link>
          <Link href="/register">
            <Button variant="secondary">Создать аккаунт</Button>
          </Link>
        </div>
      </div>
    </section>
  );
}
