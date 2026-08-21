<script setup lang="ts">
import {
  ArrowLeft,
  CopyDocument,
  EditPen,
  Link,
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '../api'
import type { Order } from '../types'
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

onMounted(loadOrder)
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
  </section>
</template>
