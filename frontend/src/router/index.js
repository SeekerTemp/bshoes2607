import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/style-guide', name: 'style-guide', component: () => import('../views/StyleGuideView.vue') },
  { path: '/:pathMatch(.*)*', redirect: '/style-guide' }
]

export default createRouter({ history: createWebHistory(), routes })
