<script setup lang="ts">
import {
  ArrowLeft,
  CircleClose,
  CopyDocument,
  EditPen,
  Link,
  RefreshLeft,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { invoiceApi, invoiceTitleApi, orderApi } from '../api'
import type { InvoicePayload, InvoiceTitleGroup, Order } from '../types'
import {
  copyText,
  formatAmount,
  formatDateTime,
  getInvoiceStatusText,
  getPaymentText,
  getTitleTypeText,
} from '../utils/display'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref<Order>()
const invoiceDialogVisible = ref(false)
const invoiceGroups = ref<InvoiceTitleGroup[]>([])
const invoiceForm = reactive<InvoicePayload>({
  invoiceDate: '',
  invoiceNo: '',
  invoiceTitleId: undefined,
  orderIds: [],
})

function getInvoiceId(): number | undefined {
  return order.value?.invoiceId ?? order.value?.invoiceBatchId ?? undefined
}

async function openReissue(): Promise<void> {
  const invoiceId = getInvoiceId()
  if (!invoiceId || !order.value) {
    ElMessage.warning('当前订单缺少发票批次信息，暂时无法重开')
    return
  }
  try {
    const invoice = await invoiceApi.detail(invoiceId)
    invoiceForm.invoiceDate = invoice.invoiceDate || order.value.invoiceDate || ''
    invoiceForm.invoiceNo = invoice.invoiceNo || order.value.invoiceNo || ''
    invoiceForm.invoiceTitleId = invoice.invoiceTitleId
      || order.value.invoiceTitleId || undefined
    invoiceForm.orderIds = invoice.orderIds?.length
      ? invoice.orderIds
      : [order.value.id]
  } catch (error) {
    ElMessage.error((error as Error).message)
    return
  }
  invoiceDialogVisible.value = true
}

async function submitReissue(): Promise<void> {
  const invoiceId = getInvoiceId()
  if (!invoiceId || !invoiceForm.invoiceDate || !invoiceForm.invoiceNo
    || !invoiceForm.invoiceTitleId) {
    ElMessage.warning('请完整填写重开发票信息')
    return
  }
  try {
    await invoiceApi.reissue(invoiceId, invoiceForm)
    ElMessage.success('发票已重开')
    invoiceDialogVisible.value = false
    await loadOrder()
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

async function voidInvoice(): Promise<void> {
  const invoiceId = getInvoiceId()
  if (!invoiceId) {
    ElMessage.warning('当前订单缺少发票批次信息，暂时无法作废')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定作废发票 ${order.value?.invoiceNo || ''} 吗？作废后订单将恢复为未开票状态。`,
      '作废发票',
      { type: 'warning', confirmButtonText: '确认作废' },
    )
    await invoiceApi.void(invoiceId)
    ElMessage.success('发票已作废，订单恢复为未开票')
    await loadOrder()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message)
    }
  }
}

async function loadOrder(): Promise<void> {
  loading.value = true
  try {
    order.value = await orderApi.detail(Number(route.params.id))
  } catch (error) {
    ElMessage.error((error as Error).message)
  } finally {
    loading.value = false
  }
}

function openProviderWebsite(): void {
  if (order.value?.providerWebsiteUrl) {
    window.open(order.value.providerWebsiteUrl, '_blank', 'noopener,noreferrer')
  }
}

onMounted(async () => {
  await Promise.all([
    loadOrder(),
    invoiceTitleApi.options().then((groups) => {
      invoiceGroups.value = groups
    }),
  ])
})
</script>

<template>
  <section v-loading="loading" class="page-section detail-page">
    <div class="page-heading compact-heading">
      <div>
        <el-button text :icon="ArrowLeft" @click="router.push('/orders')">返回订单列表</el-button>
        <p class="eyebrow">订单详情</p>
        <h2>{{ order?.orderNo || '加载中...' }}</h2>
      </div>
      <el-button
        v-if="order?.invoiceStatus === 'UNISSUED'"
        type="primary"
        :icon="EditPen"
        @click="router.push(`/orders/${order.id}/edit`)"
      >
        修改订单
      </el-button>
      <div v-else-if="order?.invoiceStatus === 'ISSUED'" class="page-actions">
        <el-button type="warning" :icon="RefreshLeft" @click="openReissue">重开发票</el-button>
        <el-button type="danger" plain :icon="CircleClose" @click="voidInvoice">作废发票</el-button>
      </div>
    </div>

    <el-card v-if="order" class="detail-card" shadow="never">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">
          <div class="copy-cell">
            <span>{{ order.orderNo }}</span>
            <el-button text type="primary" :icon="CopyDocument" @click="copyText(order.orderNo)">
              复制
            </el-button>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ formatAmount(order.amount) }}</el-descriptions-item>
        <el-descriptions-item label="API 提供商">
          <span>{{ order.providerName }}</span>
          <el-button
            v-if="order.providerWebsiteUrl"
            text
            type="primary"
            :icon="Link"
            @click="openProviderWebsite"
          >
            访问官网
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ getPaymentText(order.paymentMethod) }}</el-descriptions-item>
        <el-descriptions-item label="开票状态">
          <el-tag :type="order.invoiceStatus === 'ISSUED' ? 'success' : 'info'">
            {{ getInvoiceStatusText(order.invoiceStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatDateTime(order.createdAt) }}</el-descriptions-item>
        <template v-if="order.invoiceStatus === 'ISSUED'">
          <el-descriptions-item label="开票日期">{{ order.invoiceDate }}</el-descriptions-item>
          <el-descriptions-item label="发票号码">{{ order.invoiceNo }}</el-descriptions-item>
          <el-descriptions-item label="发票抬头名称">{{ order.invoiceTitleName }}</el-descriptions-item>
          <el-descriptions-item label="抬头类型">
            {{ getTitleTypeText(order.invoiceTitleType) }}
          </el-descriptions-item>
          <el-descriptions-item label="统一社会信用代码" :span="2">
            {{ order.invoiceTaxCode || '-' }}
          </el-descriptions-item>
        </template>
      </el-descriptions>
    </el-card>

    <el-dialog v-model="invoiceDialogVisible" title="重开发票" width="480px" destroy-on-close>
      <p class="invoice-dialog-note">
        重开后原发票记录会保留，当前订单将关联新的发票信息。
      </p>
      <el-form label-position="top" class="dialog-form">
        <el-form-item label="开票日期" required>
          <el-date-picker
            v-model="invoiceForm.invoiceDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择开票日期"
            class="dialog-full-control"
          />
        </el-form-item>
        <el-form-item label="新发票编号" required>
          <el-input v-model.trim="invoiceForm.invoiceNo" placeholder="请输入新发票编号" />
        </el-form-item>
        <el-form-item label="发票抬头" required>
          <el-select
            v-model="invoiceForm.invoiceTitleId"
            class="dialog-full-control"
            placeholder="请选择发票抬头"
          >
            <el-option-group
              v-for="group in invoiceGroups"
              :key="group.label"
              :label="group.label"
            >
              <el-option
                v-for="title in group.options"
                :key="title.id"
                :label="title.name"
                :value="title.id"
              />
            </el-option-group>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="invoiceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReissue">确认重开</el-button>
      </template>
    </el-dialog>
  </section>
</template>
