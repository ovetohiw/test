import { describe, expect, it } from "vitest";
import { formatPrice } from "./utils";

describe("formatPrice", () => {
  it("formats kopecks as RUB", () => {
    expect(formatPrice(4999000)).toContain("49");
  });
});
