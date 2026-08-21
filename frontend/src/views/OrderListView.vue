<script setup lang="ts">
import {
  Delete,
  DocumentChecked,
  EditPen,
  Plus,
  Refresh,
  Search,
  View,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { invoiceTitleApi, orderApi, providerApi } from '../api'
import type {
  InvoicePayload,
  InvoiceTitle,
  InvoiceTitleGroup,
  Order,
  Provider,
} from '../types'
import {
  copyText,
  formatAmount,
  getInvoiceStatusText,
  getPaymentText,
  getTitleTypeText,
} from '../utils/display'

const router = useRouter()
const loading = ref(false)
const orders = ref<Order[]>([])
const providers = ref<Provider[]>([])
const invoiceGroups = ref<InvoiceTitleGroup[]>([])
const selectedOrders = ref<Order[]>([])
const total = ref(0)
const invoiceDialogVisible = ref(false)
const titleDialogVisible = ref(false)
const selectedTitle = ref<InvoiceTitle>()
const currentOrder = ref<Order>()

const filters = reactive({
  keyword: '',
  providerIds: [] as number[],
  invoiceStatus: '',
  invoiceTitleType: '',
  page: 1,
  pageSize: 10,
})

const invoiceForm = reactive<InvoicePayload>({
  invoiceDate: '',
  invoiceNo: '',
  invoiceTitleId: undefined,
})

async function loadOptions(): Promise<void> {
  const [providerData, titleData] = await Promise.all([
    providerApi.options(),
    invoiceTitleApi.options(),
  ])
  providers.value = providerData
  invoiceGroups.value = titleData
}

async function loadOrders(): Promise<void> {
  loading.value = true
  try {
    const data = await orderApi.list(filters)
    orders.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error((error as Error).message)
  } finally {
    loading.value = false
  }
}

function search(): void {
  filters.page = 1
  loadOrders()
}

function resetFilters(): void {
  filters.keyword = ''
  filters.providerIds = []
  filters.invoiceStatus = ''
  filters.invoiceTitleType = ''
  search()
}

function openInvoice(order: Order): void {
  currentOrder.value = order
  invoiceForm.invoiceDate = ''
  invoiceForm.invoiceNo = ''
  invoiceForm.invoiceTitleId = undefined
  invoiceDialogVisible.value = true
}

async function submitInvoice(): Promise<void> {
  if (!currentOrder.value || !invoiceForm.invoiceDate || !invoiceForm.invoiceNo
    || !invoiceForm.invoiceTitleId) {
    ElMessage.warning('请完整填写开票信息')
    return
  }
  try {
    await orderApi.issueInvoice(currentOrder.value.id, invoiceForm)
    ElMessage.success('发票已开具')
    invoiceDialogVisible.value = false
    loadOrders()
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

async function deleteSelected(): Promise<void> {
  if (!selectedOrders.value.length) {
    ElMessage.warning('请先选择需要删除的订单')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定逻辑删除已选中的 ${selectedOrders.value.length} 个订单吗？`,
      '删除订单',
      { type: 'warning' },
    )
    await orderApi.batchDelete(selectedOrders.value.map((item) => item.id))
    ElMessage.success('订单已移入回收站')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message)
    }
  }
}

async function showTitle(order: Order): Promise<void> {
  if (!order.invoiceTitleId) {
    return
  }
  try {
    selectedTitle.value = await invoiceTitleApi.detail(order.invoiceTitleId)
    titleDialogVisible.value = true
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

onMounted(async () => {
  try {
    await loadOptions()
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
  loadOrders()
})
</script>

<template>
  <section class="page-section">
    <div class="page-heading">
      <div>
        <p class="eyebrow">订单总览</p>
        <h2>购买订单</h2>
        <p class="muted">记录每一笔 API 额度购买与开票信息。</p>
      </div>
      <div class="page-actions">
        <el-button :icon="Refresh" @click="loadOrders">刷新</el-button>
        <el-button type="primary" :icon="Plus" @click="router.push('/orders/new')">
          新增订单
        </el-button>
      </div>
    </div>

    <div class="filter-panel">
      <el-input
        v-model="filters.keyword"
        class="keyword-input"
        placeholder="订单编号、提供商、发票编号"
        clearable
        :prefix-icon="Search"
        @keyup.enter="search"
      />
      <el-select
        v-model="filters.providerIds"
        class="filter-control"
        multiple
        collapse-tags
        collapse-tags-tooltip
        placeholder="全部提供商"
      >
        <el-option
          v-for="provider in providers"
          :key="provider.id"
          :label="provider.name"
          :value="provider.id"
        />
      </el-select>
      <el-select v-model="filters.invoiceStatus" class="filter-control" placeholder="开票状态">
        <el-option label="全部开票状态" value="" />
        <el-option label="未开具" value="UNISSUED" />
        <el-option label="已开具" value="ISSUED" />
      </el-select>
      <el-select v-model="filters.invoiceTitleType" class="filter-control" placeholder="抬头类型">
        <el-option label="全部抬头类型" value="" />
        <el-option label="个人" value="PERSONAL" />
        <el-option label="企业" value="COMPANY" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="search">查询</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </div>

    <div class="table-toolbar">
      <span>共 <b>{{ total }}</b> 条订单</span>
      <el-button
        type="danger"
        plain
        :icon="Delete"
        :disabled="!selectedOrders.length"
        @click="deleteSelected"
      >
        批量删除
      </el-button>
    </div>

    <el-table
      v-loading="loading"
      :data="orders"
      class="data-table"
      @selection-change="selectedOrders = $event"
    >
      <el-table-column type="selection" width="46" />
      <el-table-column label="订单编号" min-width="190">
        <template #default="{ row }">
          <div class="copy-cell">
            <span>{{ row.orderNo }}</span>
            <el-button text size="small" @click="copyText(row.orderNo)">复制</el-button>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="providerName" label="提供商" min-width="140" />
      <el-table-column label="订单金额" min-width="125">
        <template #default="{ row }">{{ formatAmount(row.amount) }}</template>
      </el-table-column>
      <el-table-column label="支付方式" min-width="110">
        <template #default="{ row }">{{ getPaymentText(row.paymentMethod) }}</template>
      </el-table-column>
      <el-table-column label="已开发票" min-width="112">
        <template #default="{ row }">
          <el-tag :type="row.invoiceStatus === 'ISSUED' ? 'success' : 'info'" effect="light">
            {{ getInvoiceStatusText(row.invoiceStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="抬头类型" min-width="120">
        <template #default="{ row }">
          <el-button
            v-if="row.invoiceStatus === 'ISSUED'"
            text
            type="primary"
            @click="showTitle(row)"
          >
            {{ getTitleTypeText(row.invoiceTitleType) }}
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="218" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" :icon="View" @click="router.push(`/orders/${row.id}`)">
            详情
          </el-button>
          <el-button
            v-if="row.invoiceStatus === 'UNISSUED'"
            text
            type="primary"
            :icon="EditPen"
            @click="router.push(`/orders/${row.id}/edit`)"
          >
            修改
          </el-button>
          <el-button
            v-if="row.invoiceStatus === 'UNISSUED'"
            text
            type="success"
            :icon="DocumentChecked"
            @click="openInvoice(row)"
          >
            开具发票
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="filters.page"
      v-model:page-size="filters.pageSize"
      class="table-pagination"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      @change="loadOrders"
    />

    <el-dialog v-model="invoiceDialogVisible" title="开具发票" width="480px" destroy-on-close>
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
        <el-form-item label="发票编号" required>
          <el-input v-model.trim="invoiceForm.invoiceNo" placeholder="请输入发票编号" />
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
        <el-button type="primary" @click="submitInvoice">确认开具</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="titleDialogVisible" title="发票抬头" width="440px">
      <el-descriptions v-if="selectedTitle" :column="1" border>
        <el-descriptions-item label="抬头类型">
          {{ getTitleTypeText(selectedTitle.titleType) }}
        </el-descriptions-item>
        <el-descriptions-item label="抬头名称">{{ selectedTitle.name }}</el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码">
          {{ selectedTitle.taxCode || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>
