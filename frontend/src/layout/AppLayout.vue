<script setup lang="ts">
import {
  Collection,
  Document,
  OfficeBuilding,
  Plus,
  Tickets,
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  if (route.path.startsWith('/orders')) {
    return '/orders'
  }
  return route.path
})

function navigate(path: string): void {
  router.push(path)
}
</script>

<template>
  <el-container class="app-shell">
    <el-aside class="app-aside" width="224px">
      <button class="brand" type="button" @click="navigate('/orders')">
        <span class="brand-mark">A</span>
        <span>
          <strong>API 订单管理</strong>
          <small>本地个人账务</small>
        </span>
      </button>

      <el-menu
        class="main-menu"
        :default-active="activeMenu"
        @select="navigate"
      >
        <el-menu-item index="/orders">
          <el-icon><Tickets /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/providers">
          <el-icon><OfficeBuilding /></el-icon>
          <span>API 提供商</span>
        </el-menu-item>
        <el-menu-item index="/invoice-titles">
          <el-icon><Document /></el-icon>
          <span>发票抬头</span>
        </el-menu-item>
      </el-menu>

      <div class="aside-footer">
        <el-button type="primary" :icon="Plus" @click="navigate('/orders/new')">
          新增订单
        </el-button>
        <el-button text :icon="Collection" @click="navigate('/orders/recycle-bin')">
          订单回收站
        </el-button>
      </div>
    </el-aside>

    <el-container>
      <el-header class="app-header">
        <div>
          <span class="header-caption">API Order Manager</span>
          <h1>本地 API 订单管理</h1>
        </div>
        <span class="local-indicator">仅本机数据</span>
      </el-header>
      <el-main class="app-main">
        <RouterView />
      </el-main>
    </el-container>
  </el-container>
</template>
