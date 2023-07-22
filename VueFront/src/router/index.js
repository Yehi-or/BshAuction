import Vue from 'vue'
import VueRouter from 'vue-router'
import HomePage from '../components/HomePage.vue'
import SignPage from '../components/SignPage.vue'
import ProductPage from '../components/ProductPage.vue'

Vue.use(VueRouter)

const routes = [
    {
      path: '/',
      name: 'home',
      component: HomePage
    },
    {
      path: '/sign',
      name: 'sign',
      component: SignPage,
    },
    {
      path: '/product/:id',
      name: 'product',
      component: ProductPage,
    }
  ]
  
  const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
  })
  
  export default router

