export type Role = "USER" | "ADMIN";

export type User = {
  id: number;
  email: string;
  fullName: string;
  role: Role;
};

export type Product = {
  id: number;
  name: string;
  brand: string;
  description: string;
  priceCents: number;
  stock: number;
  specsJson: string;
  imageUrl: string | null;
  active: boolean;
};

export type Cart = {
  items: Array<{
    id: number;
    productId: number;
    productName: string;
    brand: string;
    unitPriceCents: number;
    quantity: number;
    stock: number;
    imageUrl: string | null;
    lineTotalCents: number;
  }>;
  totalCents: number;
};

export type Order = {
  id: number;
  status: "PENDING_PAYMENT" | "PAID" | "CANCELLED";
  totalCents: number;
  contactName: string;
  contactPhone: string;
  deliveryMethod: "PICKUP" | "DELIVERY";
  deliveryAddress: string | null;
  createdAt: string;
  items: Array<{
    productId: number;
    productName: string;
    unitPriceCents: number;
    quantity: number;
    lineTotalCents: number;
  }>;
  paymentConfirmationUrl: string | null;
  paymentStatus: string | null;
};

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    ...init,
    credentials: "include",
    headers: {
      ...(init?.body instanceof FormData ? {} : { "Content-Type": "application/json" }),
      ...init?.headers,
    },
  });
  if (!response.ok) {
    let message = "Request failed";
    try {
      const data = await response.json();
      message = data.message || message;
    } catch {
      /* ignore */
    }
    throw new ApiError(response.status, message);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export const api = {
  me: () => request<User>("/api/auth/me"),
  login: (email: string, password: string) =>
    request<User>("/api/auth/login", { method: "POST", body: JSON.stringify({ email, password }) }),
  register: (email: string, password: string, fullName: string) =>
    request<User>("/api/auth/register", {
      method: "POST",
      body: JSON.stringify({ email, password, fullName }),
    }),
  logout: () => request<void>("/api/auth/logout", { method: "POST" }),
  products: (params?: { brand?: string; minPrice?: string; maxPrice?: string }) => {
    const query = new URLSearchParams();
    if (params?.brand) query.set("brand", params.brand);
    if (params?.minPrice) query.set("minPrice", params.minPrice);
    if (params?.maxPrice) query.set("maxPrice", params.maxPrice);
    const suffix = query.toString() ? `?${query}` : "";
    return request<Product[]>(`/api/products${suffix}`);
  },
  product: (id: string | number) => request<Product>(`/api/products/${id}`),
  cart: () => request<Cart>("/api/cart"),
  addToCart: (productId: number, quantity: number) =>
    request<Cart>("/api/cart/items", { method: "POST", body: JSON.stringify({ productId, quantity }) }),
  updateCartItem: (id: number, quantity: number) =>
    request<Cart>(`/api/cart/items/${id}`, { method: "PUT", body: JSON.stringify({ quantity }) }),
  removeCartItem: (id: number) => request<Cart>(`/api/cart/items/${id}`, { method: "DELETE" }),
  createOrder: (body: {
    contactName: string;
    contactPhone: string;
    deliveryMethod: "PICKUP" | "DELIVERY";
    deliveryAddress?: string;
  }) => request<Order>("/api/orders", { method: "POST", body: JSON.stringify(body) }),
  orders: () => request<Order[]>("/api/orders"),
  order: (id: string | number) => request<Order>(`/api/orders/${id}`),
  adminProducts: () => request<Product[]>("/api/admin/products"),
  adminCreateProduct: (body: Record<string, unknown>) =>
    request<Product>("/api/admin/products", { method: "POST", body: JSON.stringify(body) }),
  adminUpdateProduct: (id: number, body: Record<string, unknown>) =>
    request<Product>(`/api/admin/products/${id}`, { method: "PUT", body: JSON.stringify(body) }),
  adminSetActive: (id: number, active: boolean) =>
    request<Product>(`/api/admin/products/${id}/active`, {
      method: "POST",
      body: JSON.stringify({ active }),
    }),
  adminOrders: () => request<Order[]>("/api/admin/orders"),
};
