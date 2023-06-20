<template>
  <div>
    <nav class="navbar navbar-expand-md navbar-light my-theme">
      <div class="container" ref="navbarRef">
        <router-link class="navbar-brand" :to="{name: 'home'}">King Of Gobang</router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
                aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation"
                ref="targetRef" @click="showUserContent"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse text-center" id="navbarText">
          <ul class="navbar-nav me-auto mb-lg-0">
            <li class="nav-item">
              <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{ name: 'pk_index'}">
                对战
              </router-link>
            </li>
            <li class="nav-item">
              <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'"
                           :to="{ name: 'record_index'}">对局列表
              </router-link>
            </li>
            <li class="nav-item">
              <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'"
                           :to="{ name: 'ranklist_index'}">对战排行
              </router-link>
            </li>
          </ul>
          <ul class="navbar-nav" v-if="$store.state.user.is_login">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown"
                 aria-expanded="false">
                {{ $store.state.user.username }}
              </a>
              <ul class="dropdown-menu" ref="userRef">
                <li>
                  <router-link class="dropdown-item" :to="{ name: 'user_bot_index'}">个人中心</router-link>
                </li>
                <li>
                  <hr class="dropdown-divider">
                </li>
                <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
              </ul>
            </li>
          </ul>
          <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
            <li class="nav-item">
              <router-link class="nav-link" :to="{name: 'user_account_login'}" role="button">
                登录
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" :to="{name: 'user_account_register'}" role="button">
                注册
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </div>


</template>

<script>
import {useRoute} from "vue-router"
import {computed, onMounted, ref} from "vue" // 进行实时计算
import {useStore} from "vuex"
import {onClickOutside} from '@vueuse/core'

export default {
  setup() {
    // 入口
    const store = useStore()
    const route = useRoute() // 取得当前是哪个页面
    let route_name = computed(() => route.name)

    const targetRef = ref(null)
    const navbarRef = ref(null)
    const userRef = ref(null)

    const showUserContent = () => {
      userRef.value?.classList.add("show")
    }

    onMounted(() => {
      onClickOutside(targetRef, () => {
        // 如果navbar高度大于40，且没有collapsed类，就点击一下
        if (navbarRef.value.offsetHeight > 40 && !navbarRef.value?.classList.contains("collapsed")) {
          targetRef.value.click()
        } else {
          userRef.value?.classList.remove("show")
        }
      })
    })


    const logout = () => {
      // 退出登录事件
      store.dispatch("logout")
    }

    return {
      route_name,
      logout,
      targetRef,
      navbarRef,
      userRef,
      showUserContent
    }
  }
}
</script>

<!-- scoped作用：在这个css样式里面加上一个随机字符串，这样就不会影响其他页面的css了 -->
<style scoped>
.my-theme {
  background-color: rgba(239, 232, 232, 0.5);
  font-weight: 700;
}

.nav-link {
  color: rgb(87 79 79 / 55%);
}

.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: none;
}

.overlay.active {
  display: block;
}
</style>
