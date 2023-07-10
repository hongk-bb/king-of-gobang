import {createRouter, createWebHistory} from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView'
import RecordIndexView from '@/views/record/RecordIndexView'
import RecordContentView from '@/views/record/RecordContentView'
import RanklistIndexView from '@/views/ranklist/RanklistIndexView'
import UserBotIndexView from '@/views/user/bot/UserBotIndexView'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView'
import NotFound from '@/views/error/NotFound'
import store from "@/store"

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",  // 重定向到对战也面
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/:recordId/",  // 路由中加参数
    name: "record_content",
    component: RecordContentView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/:catchAll(.*)",  // 输入格式错误或乱码，则重定向至404页面
    redirect: "/404/"
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// router起作用之前执行，每次通过router进入页面之前会调用该函数，
// to: 跳转至那个页面  from：从哪个页面跳转过去   next: 页面要不要执行下一步操作
router.beforeEach((to, from, next) => {
  // console.log("is_login: ", store.state.user.is_login)
  const jwt_token = localStorage.getItem("jwt_token") // 将浏览器中的jwt_token取出来
  if (jwt_token) {  // 如果浏览器中有token
    store.commit("updateToken", jwt_token) // 调用user.js的updateToken函数
    store.dispatch("getinfo", {
      success() {
        store.commit("updatePullingInfo", true)
      },
      error() {
        store.commit("updatePullingInfo", false)
      },
    })
  } else {
    store.commit("updatePullingInfo", false)
  }
  // console.log("jwt_token: ", jwt_token)
  // console.log("to: ", to, " from: ", from)
  // console.log("store.state.user.is_login:" + store.state.user.is_login)
  if (to.meta.requestAuth && !store.state.user.is_login) {  // 如果该页面是需要授权并且没有登录的时候，重定向到登录页面
    next({name: "user_account_login"})
  } else if (!from.name && to.name === "record_content") {
    console.log("跳转到record_index")
    next({name: "record_index"})
  } else {
    next()
  }
})

export default router
