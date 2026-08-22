import { request } from './http'
import type {
  InvoicePayload,
  InvoiceBatch,
  InvoiceTitle,
  Order,
  OrderPayload,
  PageResult,
  Provider,
} from '../types'

function normalizeOrder(order: Order & { amountCent?: number }): Order {
  return {
    ...order,
    amount: order.amount ?? (order.amountCent ? order.amountCent / 100 : 0),
  }
}

function normalizePage(data: PageResult<Order>): PageResult<Order> {
  return {
    ...data,
    records: data.records.map((order) => normalizeOrder(order)),
  }
}

export const orderApi = {
  list: (params: Record<string, unknown>) =>
    request<PageResult<Order>>('/orders', { params }).then(normalizePage),
  detail: (id: number) => request<Order>(`/orders/${id}`).then(normalizeOrder),
  create: (payload: OrderPayload) => request<Order>('/orders', {
    method: 'POST',
    body: payload,
  }),
  update: (id: number, payload: OrderPayload) => request<Order>(`/orders/${id}`, {
    method: 'PUT',
    body: payload,
  }),
  batchDelete: (ids: number[]) => request<void>('/orders/batch-delete', {
    method: 'POST',
    body: { ids },
  }),
  recycleBin: (params: Record<string, unknown>) =>
    request<PageResult<Order>>('/orders/recycle-bin', { params }).then(normalizePage),
  restore: (id: number) => request<void>(`/orders/${id}/restore`, { method: 'POST' }),
}

export const invoiceApi = {
  create: (payload: InvoicePayload) => request<InvoiceBatch>('/invoices', {
    method: 'POST',
    body: payload,
  }),
  detail: (id: number) => request<InvoiceBatch>(`/invoices/${id}`),
  reissue: (id: number, payload: InvoicePayload) =>
    request<InvoiceBatch>(`/invoices/${id}/reissue`, {
      method: 'POST',
      body: payload,
    }),
  void: (id: number) => request<void>(`/invoices/${id}/void`, {
    method: 'POST',
  }),
}

export const providerApi = {
  list: () => request<Provider[]>('/providers'),
  options: () => request<Provider[]>('/providers/options'),
  detail: (id: number) => request<Provider>(`/providers/${id}`),
  create: (payload: Omit<Provider, 'id'>) => request<Provider>('/providers', {
    method: 'POST',
    body: payload,
  }),
  update: (id: number, payload: Omit<Provider, 'id'>) =>
    request<Provider>(`/providers/${id}`, { method: 'PUT', body: payload }),
  batchDelete: (ids: number[]) => request<void>('/providers/batch-delete', {
    method: 'POST',
    body: { ids },
  }),
}

export const invoiceTitleApi = {
  list: (titleType?: string) => request<InvoiceTitle[]>('/invoice-titles', {
    params: { titleType },
  }),
  options: () => request<Array<{
    label: string
    options: Array<{ value: number; label: string }>
  }>>('/invoice-titles/options').then((groups) => groups.map((group) => ({
    label: group.label,
    options: group.options.map((option) => ({
      id: option.value,
      name: option.label,
      titleType: group.label.includes('企业') ? 'COMPANY' : 'PERSONAL',
    } as InvoiceTitle)),
  }))),
  detail: (id: number) => request<InvoiceTitle>(`/invoice-titles/${id}`),
  create: (payload: Omit<InvoiceTitle, 'id'>) =>
    request<InvoiceTitle>('/invoice-titles', { method: 'POST', body: payload }),
  update: (id: number, payload: Omit<InvoiceTitle, 'id'>) =>
    request<InvoiceTitle>(`/invoice-titles/${id}`, { method: 'PUT', body: payload }),
  batchDelete: (ids: number[]) => request<void>('/invoice-titles/batch-delete', {
    method: 'POST',
    body: { ids },
  }),
}
