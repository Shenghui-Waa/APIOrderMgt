<script setup lang="ts">
import {
  Delete,
  EditPen,
  Link,
  Plus,
  View,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { providerApi } from '../api'
import type { Provider } from '../types'

const loading = ref(false)
const providers = ref<Provider[]>([])
const selectedIds = ref<number[]>([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEditing = ref(false)
const currentProvider = ref<Provider>()
const formRef = ref()

const form = reactive({
  name: '',
  websiteUrl: '',
})

const rules = {
  name: [
    { required: true, message: '请输入提供商名称', trigger: 'blur' },
    { max: 100, message: '名称不能超过 100 个字符', trigger: 'blur' },
  ],
  websiteUrl: [
    { required: true, message: '请输入官网链接', trigger: 'blur' },
    { type: 'url', message: '请输入有效的 HTTP 或 HTTPS 链接', trigger: 'blur' },
  ],
}

async function loadProviders(): Promise<void> {
  loading.value = true
  try {
    providers.value = await providerApi.list()
  } catch (error) {
    ElMessage.error((error as Error).message)
  } finally {
    loading.value = false
  }
}

function openCreate(): void {
  isEditing.value = false
  form.name = ''
  form.websiteUrl = ''
  dialogVisible.value = true
}

function openEdit(provider: Provider): void {
  isEditing.value = true
  currentProvider.value = provider
  form.name = provider.name
  form.websiteUrl = provider.websiteUrl
  dialogVisible.value = true
}

async function openDetail(provider: Provider): Promise<void> {
  try {
    currentProvider.value = await providerApi.detail(provider.id)
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
  try {
    if (isEditing.value && currentProvider.value) {
      await providerApi.update(currentProvider.value.id, form)
      ElMessage.success('提供商已修改')
    } else {
      await providerApi.create(form)
      ElMessage.success('提供商已添加')
    }
    dialogVisible.value = false
    loadProviders()
  } catch (error) {
    ElMessage.error((error as Error).message)
  }
}

async function deleteSelected(): Promise<void> {
  if (!selectedIds.value.length) {
    ElMessage.warning('请先选择提供商')
    return
  }
  try {
    await ElMessageBox.confirm(
      `将永久删除 ${selectedIds.value.length} 个提供商，确定继续吗？`,
      '删除提供商',
      { type: 'warning', confirmButtonText: '永久删除' },
    )
    await providerApi.batchDelete(selectedIds.value)
    ElMessage.success('提供商已永久删除')
    selectedIds.value = []
    loadProviders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error((error as Error).message)
    }
  }
}

function openWebsite(provider: Provider): void {
  window.open(provider.websiteUrl, '_blank', 'noopener,noreferrer')
}

onMounted(loadProviders)
</script>

<template>
  <section class="page-section">
    <div class="page-heading">
      <div>
        <p class="eyebrow">基础数据</p>
        <h2>API 提供商</h2>
        <p class="muted">维护可选 API 提供商及其官网地址。</p>
      </div>
      <div class="page-actions">
        <el-button type="danger" plain :icon="Delete" @click="deleteSelected">
          批量删除
        </el-button>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增提供商</el-button>
      </div>
    </div>

    <div v-loading="loading" class="card-grid">
      <article v-for="provider in providers" :key="provider.id" class="resource-card">
        <el-checkbox
          v-model="selectedIds"
          class="card-select"
          :value="provider.id"
          :aria-label="`选择${provider.name}`"
        />
        <div class="resource-card-title">
          <span class="resource-icon provider-icon">API</span>
          <div>
            <h3>{{ provider.name }}</h3>
            <a :href="provider.websiteUrl" target="_blank" rel="noreferrer">
              {{ provider.websiteUrl }}
            </a>
          </div>
        </div>
        <div class="card-actions">
          <el-button text type="primary" :icon="View" @click="openDetail(provider)">查看</el-button>
          <el-button text type="primary" :icon="EditPen" @click="openEdit(provider)">修改</el-button>
          <el-button text type="primary" :icon="Link" @click="openWebsite(provider)">官网</el-button>
        </div>
      </article>
      <el-empty v-if="!loading && !providers.length" description="暂无 API 提供商" />
    </div>

    <el-dialog v-model="dialogVisible" :title="isEditing ? '修改提供商' : '新增提供商'" width="470px">
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="dialog-form">
        <el-form-item label="名称" prop="name">
          <el-input v-model.trim="form.name" placeholder="如：OpenAI" />
        </el-form-item>
        <el-form-item label="官网链接" prop="websiteUrl">
          <el-input v-model.trim="form.websiteUrl" placeholder="https://example.com" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="提供商详情" width="470px">
      <el-descriptions v-if="currentProvider" :column="1" border>
        <el-descriptions-item label="名称">{{ currentProvider.name }}</el-descriptions-item>
        <el-descriptions-item label="官网链接">
          <el-link :href="currentProvider.websiteUrl" target="_blank" type="primary">
            {{ currentProvider.websiteUrl }}
          </el-link>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </section>
</template>
