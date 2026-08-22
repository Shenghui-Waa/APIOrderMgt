export type InvoiceStatus = 'UNISSUED' | 'ISSUED' | 'VOIDED'
export type InvoiceTitleType = 'PERSONAL' | 'COMPANY'
export type PaymentMethod = 'ALIPAY' | 'WECHAT' | 'BANK_CARD'

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  requestId?: string
}

export interface PageResult<T> {
  page: number
  pageSize: number
  total: number
  records: T[]
}

export interface Provider {
  id: number
  name: string
  websiteUrl: string
  createdAt?: string
  updatedAt?: string
}

export interface InvoiceTitle {
  id: number
  titleType: InvoiceTitleType
  name: string
  taxCode?: string | null
  createdAt?: string
  updatedAt?: string
}

export interface InvoiceTitleGroup {
  label: string
  options: InvoiceTitle[]
}

export interface Order {
  id: number
  orderNo: string
  providerId: number
  providerName: string
  providerWebsiteUrl?: string
  amount: number | string
  amountCent?: number
  paymentMethod: PaymentMethod
  invoiceStatus: InvoiceStatus
  invoiceDate?: string | null
  invoiceNo?: string | null
  invoiceTitleId?: number | null
  invoiceTitleName?: string | null
  invoiceTitleType?: InvoiceTitleType | null
  invoiceTaxCode?: string | null
  invoiceId?: number | null
  invoiceBatchId?: number | null
  createdAt?: string
  updatedAt?: string
}

export interface OrderPayload {
  orderNo: string
  providerId: number | undefined
  amount: number | undefined
  paymentMethod: PaymentMethod | ''
}

export interface InvoicePayload {
  invoiceDate: string
  invoiceNo: string
  invoiceTitleId: number | undefined
  orderIds?: number[]
}

export interface InvoiceBatch {
  id: number
  invoiceDate: string
  invoiceNo: string
  invoiceTitleId: number
  invoiceTitleName?: string | null
  invoiceTitleType?: InvoiceTitleType | null
  invoiceTaxCode?: string | null
  orderIds: number[]
  totalAmount?: number | string
  status?: 'ISSUED' | 'VOIDED'
  createdAt?: string
  updatedAt?: string
}
