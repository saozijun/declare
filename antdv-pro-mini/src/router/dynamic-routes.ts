import type { RouteRecordRaw } from 'vue-router'
export const ROOT_ROUTE_REDIRECT_PATH = '/project'
import { useUserStore } from '~@/stores/user'

const Layout = () => import('~/layouts/index.vue')
const basicRouteMap = {
  // iframe模式下使用
  Iframe: () => import('~/pages/common/iframe.vue'),
  // 一般用于存在子集的页面
  RouteView: () => import('~/pages/common/route-view.vue'),
}

// 定义不同角色的路由
const adminRoutes = [
  {
    path: '/analysis',
    name: 'Analysis', 
    component: () => import('~/pages/analysis/index.vue'),
    meta: {
      title: '工作台',
      icon: 'FundProjectionScreenOutlined',
      roles: []
    },
  },
  {
    path: '/project',
    name: 'Project', 
    component: () => import('~/pages/admin/project.vue'),
    meta: {
      title: '项目管理',
      icon: 'FolderOpenOutlined',
      roles: ['admin']
    },
  },
  {
    path: '/admin/project-application',
    name: 'ProjectApplication',
    component: () => import('~/pages/admin/projectApplication.vue'),
    meta: { title: '项目申报管理', icon: 'ProfileOutlined' , roles: ['admin']}
  }, 
  {
    path: '/category',
    name: 'Category', 
    component: () => import('~/pages/admin/category.vue'),
    meta: {
      title: '项目分类管理',
      icon: 'AppstoreOutlined',
      roles: ['admin']
    },
  },
  {
    path: '/expert/review',
    name: 'ExpertReview', 
    component: () => import('~/pages/expert/review.vue'),
    meta: {
      title: '项目管理',
      icon: 'FolderOpenOutlined',
      roles: ['expert']
    },
  },
  {
    path: '/expert/chat-list',
    name: 'ExpertChatList', 
    component: () => import('~/pages/expert/chat-list.vue'),
    meta: {
      title: '沟通交流',
      icon: 'MessageOutlined',
      roles: ['expert']
    },
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('~/pages/system/settings/index.vue'),
    meta: {
      title: '个人中心',
      hideInMenu: true,
      roles: ['admin', 'expert'] // 所有角色可见
    },
  },
  {
    path: '/system',
    redirect: '/system/role',
    name: 'System',
    meta: {
      title: '系统管理',
      icon: 'SettingOutlined',
      roles: ['admin'] // 仅管理员可见
    },
    component: basicRouteMap.RouteView,
    children: [
      {
        path: '/system/banner',
        name: 'Banner',
        component: () => import('~/pages/system/banner/index.vue'),
        meta: {
          title: '轮播图管理',
          roles: ['admin']
        },
      },
      {
        path: '/system/role',
        name: 'Role',
        component: () => import('~/pages/system/role/index.vue'),
        meta: {
          title: '角色管理',
          roles: []
        },
      },
      {
        path: '/system/user',
        name: 'User',
        component: () => import('~/pages/system/user/index.vue'),
        meta: {
          title: '用户管理',
          roles: ['admin']
        },
      },
      {
        path: '/system/expert',
        name: 'Expert',
        component: () => import('~/pages/system/expert/index.vue'),
        meta: {
          title: '专家管理',
          roles: ['admin']
        },
      },
    ],
  }
]

export const rootRoute: RouteRecordRaw = {
  path: '/',
  name: 'rootPath',
  redirect: ()=>{
    const userStore = useUserStore()
    if (userStore.userInfo.role === 'admin') {
      return ROOT_ROUTE_REDIRECT_PATH
    } else if (userStore.userInfo.role === 'expert') {
      return '/expert/review'
    } else {
      return ROOT_ROUTE_REDIRECT_PATH
    }
  },
  component: Layout,
  children: [],
}

// 根据角色过滤路由
const filterRoutesByRole = (routes: any[], role: string) => {
  return routes.filter(route => {
    if (route.meta && route.meta.roles) {
      return route.meta.roles.includes(role)
    }
    return true
  }).map(route => {
    if (route.children) {
      route.children = filterRoutesByRole(route.children, role)
    }
    return route
  })
}

// 导出路由
export default adminRoutes as RouteRecordRaw[]

// 导出获取角色路由的方法
export const getRoutesByRole = (role: string) => {
  return filterRoutesByRole(adminRoutes, role) as RouteRecordRaw[]
}
