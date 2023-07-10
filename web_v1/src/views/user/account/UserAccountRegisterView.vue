<template>
  <ContentField>
    <div class="row justify-content-md-center">
      <div class="col-3">
        <form @submit.prevent="register">
          <div class="mb-3">
            <label for="username" class="form-label">用户名</label>
            <input
              v-model="username"
              type="text"
              class="form-control"
              id="username"
              aria-describedby="请输入用户名"
            />
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码</label>
            <input
              v-model="password"
              type="password"
              class="form-control"
              id="password"
              aria-describedby="请输入密码"
            />
          </div>
          <div class="mb-3">
            <label for="confirmedPassword" class="form-label">确认密码</label>
            <input
              v-model="confirmedPassword"
              type="password"
              class="form-control"
              id="confirmedPassword"
              aria-describedby="请再次输入密码"
            />
          </div>
          <div class="error_msg">{{ error_msg }}</div>
          <button type="submit" class="btn btn-primary">注册</button>
        </form>
        <div class="none">
          已有账号？去&nbsp;
          <router-link to="/user/account/login">登录</router-link>
        </div>
      </div>
    </div>
  </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { ref } from 'vue' // export 则需要加{}
import router from '@/router' // export default 则不需要加{}
import $ from 'jquery'

export default {
  components: {
    ContentField
  },

  setup() {
    let username = ref('')
    let password = ref('')
    let confirmedPassword = ref('')
    let error_msg = ref('')

    const register = () => {
      $.ajax({
        // url: "http://localhost:3000/user/account/register/",
        url: 'http://117.50.185.162:3000/api/user/account/register/',
        type: 'post',
        data: {
          username: username.value,
          password: password.value,
          confirmPassword: confirmedPassword.value
        },
        success(resp) {
          if (resp.error_msg === 'success') {
            router.push({ name: 'user_account_login' })
          } else {
            error_msg.value = resp.error_msg
          }
        }
      })
    }

    return {
      username,
      password,
      confirmedPassword,
      error_msg,
      register
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
