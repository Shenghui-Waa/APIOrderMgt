import type { InvoiceStatus, InvoiceTitleType, PaymentMethod } from '../types'
import { ElMessage } from 'element-plus'

const paymentText: Record<PaymentMethod, string> = {
  ALIPAY: '支付宝',
  WECHAT: '微信',
  BANK_CARD: '银行卡',
}

const titleTypeText: Record<InvoiceTitleType, string> = {
  PERSONAL: '个人',
  COMPANY: '企业',
}

export function getPaymentText(value: PaymentMethod): string {
  return paymentText[value] ?? value
}

export function getTitleTypeText(value?: InvoiceTitleType | null): string {
  return value ? titleTypeText[value] : '-'
}

export function getInvoiceStatusText(value: InvoiceStatus): string {
  return value === 'ISSUED' ? '已开具' : '未开具'
}

export function formatAmount(value: number | string): string {
  return Number(value).toLocaleString('zh-CN', {
    style: 'currency',
    currency: 'CNY',
  })
}

export function formatDateTime(value?: string): string {
  if (!value) {
    return '-'
  }
  return new Intl.DateTimeFormat('zh-CN', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

export async function copyText(value: string, successText = '已复制'): Promise<void> {
  await navigator.clipboard.writeText(value)
  ElMessage.success(successText)
}
