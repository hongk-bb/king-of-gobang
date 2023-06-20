import $ from 'jquery'

export default {
  state: {
    id: '',
    username: '',
    photo: '',
    token: '',
    is_login: false,
    pulling_info: true
  },
  getters: {},
  mutations: {
    // 用来修改数据
    updateUser(state, user) {
      state.id = user.id
      state.username = user.username
      state.photo = user.photo
      state.is_login = user.is_login
    },
    updateToken(state, token) {
      state.token = token
    },
    logout(state) {
      // 退出登录
      state.id = ''
      state.username = ''
      state.photo = ''
      state.token = ''
      state.is_login = false
    },
    updatePullingInfo(state, pulling_info) {
      state.pulling_info = pulling_info
    }
  },
  actions: {
    login(context, data) {
      // 登录
      $.ajax({
        // url: "http://localhost:3000/user/account/token/",
        url: 'http://117.50.185.162:3000/api/user/account/token/',
        type: 'post',
        data: {
          username: data.username,
          password: data.password
        },
        success(resp) {
          if (resp.error_msg === 'success') {
            localStorage.setItem('jwt_token', resp.token)
            context.commit('updateToken', resp.token)
            data.success(resp)
          } else {
            data.error(resp)
          }
        },
        error(resp) {
          data.error(resp)
        }
      })
    },
    getinfo(context, data) {
      // 向后端发送请求，从用户的token来获取用户的详细信息。
      $.ajax({
        // url: "http://localhost:3000/user/account/info/",
        url: 'http://117.50.185.162:3000/api/user/account/info/',
        type: 'get',
        headers: {
          Authorization: 'Bearer ' + context.state.token
        },
        success(resp) {
          if (resp.error_msg === 'success') {
            context.commit('updateUser', {
              ...resp, // 将resp的内容解析出来
              is_login: true
            })
            data.success(resp)
          } else {
            data.error(resp)
          }
        },
        error(resp) {
          data.error(resp)
        }
      })
    },
    logout(context) {
      localStorage.removeItem('jwt_token')
      context.commit('logout')
    }
  },
  modules: {}
}
