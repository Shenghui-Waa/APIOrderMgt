<script setup lang="ts">
import {
  CopyDocument,
  Delete,
  EditPen,
  Plus,
  View,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { invoiceTitleApi } from '../api'
import type { InvoiceTitle, InvoiceTitleType } from '../types'
import { copyText, getTitleTypeText } from '../utils/display'

const loading = ref(false)
const titles = ref<InvoiceTitle[]>([])
const selectedIds = ref<number[]>([])
const titleTypeFilter = ref('')
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEditing = ref(false)
const currentTitle = ref<InvoiceTitle>()
const formRef = ref()

const form = reactive({
  titleType: 'PERSONAL' as InvoiceTitleType,
  name: '',
  taxCode: '',
})

const rules = computed(() => ({
  titleType: [{ required: true, message: '请选择抬头类型', trigger: 'change' }],
  name: [
    { required: true, message: '请输入抬头名称', trigger: 'blur' },
    { max: 150, message: '抬头名称不能超过 150 个字符', trigger: 'blur' },
  ],
  taxCode: form.titleType === 'COMPANY'
    ? [{ required: true, message: '企业抬头必须填写统一社会信用代码', trigger: 'blur' }]
    : [],
}))

async function loadTitles(): Promise<void> {
  loading.value = true
  try {
    titles.value = await invoiceTitleApi.list(titleTypeFilter.value || undefined)
  } catch (error) {
    ElMessage.error((error as Error).message)
  } finally {
    loading.value = false
  }
}

function openCreate(): void {
  isEditing.value = false
  form.titleType = 'PERSONAL'
  form.name = ''
  form.taxCode = ''
  dialogVisible.value = true
}

function openEdit(title: InvoiceTitle): void {
  isEditing.value = true
  currentTitle.value = title
  form.titleType = title.titleType
  form.name = title.name
  form.taxCode = title.taxCode || ''
  dialogVisible.value = true
}

async function openDetail(title: InvoiceTitle): Promise<void> {
  try {
    currentTitle.value = await invoiceTitleApi.detail(title.id)
    detailVisible.value = true
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

async function submit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  const payload = {
    titleType: form.titleType,
    name: form.name,
    taxCode: form.titleType === 'COMPANY' ? form.taxCode : null,
  }
  try {
    if (isEditing.value && currentTitle.value) {
      await invoiceTitleApi.update(currentTitle.value.id, payload)
      ElMessage.success('发票抬头已修改')
    } else {
      await invoiceTitleApi.create(payload)
      ElMessage.success('发票抬头已添加')
    }
    dialogVisible.value = false
    loadTitles()
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

async function deleteSelected(): Promise<void> {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择发票抬头')
    return
  }
  try {
    await ElMessageBox.confirm(
      `将永久删除 ${selectedIds.value.length} 个发票抬头，确定继续吗？`,
      '删除发票抬头',
      { type: 'warning', confirmButtonText: '永久删除' },
    )
    await invoiceTitleApi.batchDelete(selectedIds.value)
    ElMessage.success('发票抬头已永久删除')
    selectedIds.value = []
    loadTitles()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message)
    }
  }
}

function getCopyContent(title: InvoiceTitle): string {
  if (title.titleType === 'COMPANY') {
    return `${title.name}\n统一社会信用代码：${title.taxCode || ''}`
  }
  return title.name
}

watch(() => form.titleType, (value) => {
  if (value === 'PERSONAL') {
    form.taxCode = ''
  }
})

watch(titleTypeFilter, loadTitles)
onMounted(loadTitles)
</script>

<template>
  <section class="page-section">
    <div class="page-heading">
      <div>
        <p class="eyebrow">基础数据</p>
        <h2>发票抬头</h2>
        <p class="muted">企业抬头需维护统一社会信用代码，开票时将保留快照。</p>
      </div>
      <div class="page-actions">
        <el-button type="danger" plain :icon="Delete" @click="deleteSelected">
          批量删除
        </el-button>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增抬头</el-button>
      </div>
    </div>

    <div class="title-filter">
      <el-radio-group v-model="titleTypeFilter">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="PERSONAL">个人</el-radio-button>
        <el-radio-button label="COMPANY">企业</el-radio-button>
      </el-radio-group>
    </div>

    <div v-loading="loading" class="card-grid">
      <article v-for="title in titles" :key="title.id" class="resource-card title-card">
        <el-checkbox
          v-model="selectedIds"
          class="card-select"
          :value="title.id"
          :aria-label="`选择${title.name}`"
        />
        <div class="resource-card-title">
          <span class="resource-icon title-icon">{{ getTitleTypeText(title.titleType) }}</span>
          <div>
            <el-tag :type="title.titleType === 'COMPANY' ? 'warning' : 'success'" size="small">
              {{ getTitleTypeText(title.titleType) }}抬头
            </el-tag>
            <h3>{{ title.name }}</h3>
            <p>{{ title.titleType === 'COMPANY' ? title.taxCode : '个人抬头' }}</p>
          </div>
        </div>
        <div class="card-actions">
          <el-button text type="primary" :icon="View" @click="openDetail(title)">查看</el-button>
          <el-button text type="primary" :icon="EditPen" @click="openEdit(title)">修改</el-button>
          <el-button text type="primary" :icon="CopyDocument" @click="copyText(getCopyContent(title))">
            复制
          </el-button>
        </div>
      </article>
      <el-empty v-if="!loading && !titles.length" description="暂无发票抬头" />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEditing ? '修改发票抬头' : '新增发票抬头'" width="470px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <el-form-item label="抬头类型" prop="titleType">
          <el-radio-group v-model="form.titleType">
            <el-radio-button label="PERSONAL">个人</el-radio-button>
            <el-radio-button label="COMPANY">企业</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="抬头名称" prop="name">
          <el-input v-model.trim="form.name" placeholder="请输入发票抬头名称" />
        </el-form-item>
        <el-form-item v-if="form.titleType === 'COMPANY'" label="统一社会信用代码" prop="taxCode">
          <el-input v-model.trim="form.taxCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="发票抬头详情" width="470px">
      <el-descriptions v-if="currentTitle" :column="1" border>
        <el-descriptions-item label="抬头类型">
          {{ getTitleTypeText(currentTitle.titleType) }}
        </el-descriptions-item>
        <el-descriptions-item label="抬头名称">{{ currentTitle.name }}</el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码">
          {{ currentTitle.taxCode || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>
