<script setup lang="ts">
import { ArrowLeft, Check, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi, providerApi } from '../api'
import type { OrderPayload, Provider } from '../types'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const providers = ref<Provider[]>([])
const isEditing = computed(() => Boolean(route.params.id))

const form = reactive<OrderPayload>({
  orderNo: '',
  providerId: undefined,
  amount: undefined,
  paymentMethod: '',
})

const rules = {
  orderNo: [
    { required: true, message: '请输入订单编号', trigger: 'blur' },
    { max: 100, message: '订单编号不能超过 100 个字符', trigger: 'blur' },
  ],
  providerId: [{ required: true, message: '请选择提供商', trigger: 'change' }],
  amount: [{ required: true, message: '请输入大于 0 的订单金额', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }],
}

async function loadData(): Promise<void> {
  try {
    providers.value = await providerApi.options()
    if (!isEditing.value) {
      return
    }
    const order = await orderApi.detail(Number(route.params.id))
    if (order.invoiceStatus === 'ISSUED') {
      ElMessage.warning('已开票订单不能修改')
      router.replace(`/orders/${order.id}`)
      return
    }
    form.orderNo = order.orderNo
    form.providerId = order.providerId
    form.amount = Number(order.amount)
    form.paymentMethod = order.paymentMethod
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

async function submit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid || !form.amount || form.amount <= 0) {
    if (form.amount !== undefined && form.amount <= 0) {
      ElMessage.warning('订单金额必须大于 ￥0')
    }
    return
  }

  loading.value = true
  try {
    const order = isEditing.value
      ? await orderApi.update(Number(route.params.id), form)
      : await orderApi.create(form)
    ElMessage.success(isEditing.value ? '订单已更新' : '订单已创建')
    router.replace(`/orders/${order.id}`)
  } catch (error) {
    ElMessage.error((error as Error).message)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<template>
  <section class="page-section form-page">
    <div class="page-heading compact-heading">
      <div>
        <el-button text :icon="ArrowLeft" @click="router.back()">返回</el-button>
        <p class="eyebrow">订单信息</p>
        <h2>{{ isEditing ? '修改订单' : '新增订单' }}</h2>
      </div>
    </div>

    <div class="form-layout">
      <el-card class="form-card" shadow="never">
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="订单编号" prop="orderNo">
            <el-input v-model.trim="form.orderNo" placeholder="请输入订单编号" />
          </el-form-item>
          <el-form-item label="API 提供商" prop="providerId">
            <el-select v-model="form.providerId" class="full-control" placeholder="请选择提供商">
              <el-option
                v-for="provider in providers"
                :key="provider.id"
                :label="provider.name"
                :value="provider.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="订单金额（人民币）" prop="amount">
            <el-input-number
              v-model="form.amount"
              class="full-control"
              :min="0.01"
              :precision="2"
              :step="10"
              controls-position="right"
            />
          </el-form-item>
          <el-form-item label="支付方式" prop="paymentMethod">
            <el-select v-model="form.paymentMethod" class="full-control" placeholder="请选择支付方式">
              <el-option label="支付宝" value="ALIPAY" />
              <el-option label="微信" value="WECHAT" />
              <el-option label="银行卡" value="BANK_CARD" />
            </el-select>
          </el-form-item>
          <div class="form-actions">
            <el-button @click="router.back()">取消</el-button>
            <el-button type="primary" :icon="Check" :loading="loading" @click="submit">
              保存订单
            </el-button>
          </div>
        </el-form>
      </el-card>
      <aside class="form-tip">
        <el-icon><WarningFilled /></el-icon>
        <div>
          <h3>填写说明</h3>
          <p>订单金额按人民币元填写，最多保留两位小数。</p>
          <p>订单开具发票后，订单编号、提供商、金额和支付方式将不能修改。</p>
        </div>
      </aside>
    </div>
  </section>
</template>
