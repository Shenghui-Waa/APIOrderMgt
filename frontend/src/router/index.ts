import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '../layout/AppLayout.vue'
import InvoiceTitleView from '../views/InvoiceTitleView.vue'
import OrderDetailView from '../views/OrderDetailView.vue'
import OrderFormView from '../views/OrderFormView.vue'
import OrderListView from '../views/OrderListView.vue'
import ProviderView from '../views/ProviderView.vue'
import RecycleBinView from '../views/RecycleBinView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: AppLayout,
      redirect: '/orders',
      children: [
        { path: 'orders', component: OrderListView },
        { path: 'orders/new', component: OrderFormView },
        { path: 'orders/:id', component: OrderDetailView },
        { path: 'orders/:id/edit', component: OrderFormView },
        { path: 'orders/recycle-bin', component: RecycleBinView },
        { path: 'providers', component: ProviderView },
        { path: 'invoice-titles', component: InvoiceTitleView },
      ],
    },
  ],
})

export default router
