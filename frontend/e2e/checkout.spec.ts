import { expect, test } from "@playwright/test";

test("catalog to paid order via stub YooKassa", async ({ page }) => {
  const email = `buyer-${Date.now()}@example.com`;
  await page.goto("/register", { waitUntil: "domcontentloaded" });
  await page.getByPlaceholder("Имя").fill("E2E Buyer");
  await page.getByPlaceholder("Email").fill(email);
  await page.getByPlaceholder("Пароль (мин. 8)").fill("Password123!");
  await page.getByRole("button", { name: "Создать аккаунт" }).click();
  await page.waitForURL("**/catalog", { timeout: 60_000 });

  await page.getByRole("link").filter({ hasText: "Nova" }).first().click();
  await page.getByRole("button", { name: "В корзину" }).click();
  await page.waitForURL("**/cart", { timeout: 60_000 });
  await page.getByRole("link", { name: "Оформить" }).click();

  await page.getByPlaceholder("Контактное имя").fill("E2E Buyer");
  await page.getByPlaceholder("Телефон").fill("+79990001122");
  await Promise.all([
    page.waitForURL(/localhost:8080.*stub/, { timeout: 60_000 }),
    page.getByRole("button", { name: "Перейти к оплате ЮKassa" }).click(),
  ]);

  await page.getByRole("link", { name: "Оплатить успешно" }).click();
  await page.waitForURL("**/orders/**", { timeout: 60_000 });
  await expect(page.getByText("PAID")).toBeVisible({ timeout: 30_000 });
});
