<template>
  <ContentField v-if="!$store.state.user.pulling_info">
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="login">
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input v-model="username" type="text" class="form-control" id="username" aria-describedby="请输入用户名">
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input v-model="password" type="password" class="form-control" id="password" aria-describedby="请输入密码">
          </div>
          <div class="error_msg">{{ error_msg }}</div>
          <button type="submit" class="btn btn-primary">登录</button>
        </form>
        <div class="none">
          没有账号？去&nbsp;<router-link to="/user/account/register">注册</router-link>
        </div>
      </div>
    </div>
  </ContentField>
</template>


<script>
import ContentField from '@/components/ContentField.vue'
import {useStore} from 'vuex'
import {ref} from 'vue'
import router from '@/router/index'

export default {
  components: {
    ContentField
  },
  setup() {
    const store = useStore()
    let username = ref('')
    let password = ref('')
    let error_msg = ref('')

    const jwt_token = localStorage.getItem("jwt_token") // 将浏览器中的jwt_token取出来
    if (jwt_token) {  // 如果浏览器中有token
      store.commit("updateToken", jwt_token) // 调用user.js的updateToken函数
      store.dispatch("getinfo", {
        success() {
          router.push({name: "home"})
          store.commit("updatePullingInfo", true)
        },
        error() {
          store.commit("updatePullingInfo", false)
        },
      })
    } else {
      store.commit("updatePullingInfo", false)
    }

    const login = () => {
      error_msg.value = ""  // 清空error_msg
      store.dispatch("login", {  // 调用actions里的函数
        username: username.value,
        password: password.value,
        success() {
          // 登录成功，需要获取用户的详细信息
          store.dispatch("getinfo", {
            success() {
              router.push({name: 'home'})
              console.log(store.state.user)
            }
          })
        },
        error() {
          error_msg.value = "用户名或密码错误"
        }
      })
    }

    return {
      username,
      password,
      error_msg,
      login,
    }
  }
}
</script>

<style scoped>
button {
  width: 100%;
}

div.error_msg {
  color: red;
}

.row > * {
  margin: 0 auto;
}

.none {
  text-align: center;
  margin-top: 1.5vw;
}

@media only screen and (max-width: 768px) {
  .row > .col-3 {
    width: 50%;
  }
}

</style>