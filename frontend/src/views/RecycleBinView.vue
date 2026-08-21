<script setup lang="ts">
import { ArrowLeft, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api'
import type { Order } from '../types'
import { formatAmount, getPaymentText } from '../utils/display'

const router = useRouter()
const loading = ref(false)
const orders = ref<Order[]>([])

async function loadOrders(): Promise<void> {
  loading.value = true
  try {
    const data = await orderApi.recycleBin({ page: 1, pageSize: 100 })
    orders.value = data.records
  } catch (error) {
    ElMessage.error((error as Error).message)
  } finally {
    loading.value = false
  }
}

async function restoreOrder(order: Order): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确认恢复订单“${order.orderNo}”吗？`,
      '恢复订单',
      { type: 'info' },
    )
    await orderApi.restore(order.id)
    ElMessage.success('订单已恢复')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message)
    }
  }
}

onMounted(loadOrders)
</script>

<template>
  <section class="page-section">
    <div class="page-heading compact-heading">
      <div>
        <el-button text :icon="ArrowLeft" @click="router.push('/orders')">返回订单列表</el-button>
        <p class="eyebrow">数据恢复</p>
        <h2>订单回收站</h2>
        <p class="muted">逻辑删除的订单会永久保留，可在此恢复。</p>
      </div>
    </div>

    <el-table v-loading="loading" :data="orders" class="data-table">
      <el-table-column prop="orderNo" label="订单编号" min-width="200" />
      <el-table-column prop="providerName" label="提供商" min-width="150" />
      <el-table-column label="订单金额" min-width="120">
        <template #default="{ row }">{{ formatAmount(row.amount) }}</template>
      </el-table-column>
      <el-table-column label="支付方式" min-width="120">
        <template #default="{ row }">{{ getPaymentText(row.paymentMethod) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" :icon="RefreshRight" @click="restoreOrder(row)">
            恢复
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>
