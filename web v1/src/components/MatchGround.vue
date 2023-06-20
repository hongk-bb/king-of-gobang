<template>
  <div class="root-box">
    <div class="matchground">
      <div class="row">
        <div class="col-4">
          <div class="user-photo left-photo">
            <img :src="$store.state.user.photo" alt="" />
          </div>
          <div class="user-username">
            {{ $store.state.user.username }}
          </div>
        </div>
        <!-- 下拉选择框 -->
        <div class="col-4">
          <div class="user-select-bot">
            <select
              v-model="select_bot"
              class="form-select"
              aria-label="Default select example"
            >
              <option value="-1" selected>亲自出马</option>
              <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                {{ bot.title }}
              </option>
            </select>
          </div>
        </div>
        <div class="col-4">
          <div class="user-photo right-photo">
            <img :src="$store.state.pk.opponent_photo" alt="" />
          </div>
          <div class="user-username">
            {{ $store.state.pk.opponent_username }}
          </div>
        </div>
        <div class="col-12" style="text-align: center; padding-top: 15vh">
          <button
            @click="click_match_btn"
            type="button"
            class="btn btn-warning btn-lg"
            style="font-size: inherit"
          >
            {{ match_btn_info }}
          </button>
        </div>
      </div>
    </div>
    <div id="overlay" ref="overlayRef">
      <div id="message">网络波动, 若长时间匹配不成功请刷新...</div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex'
import $ from 'jquery'

export default {
  setup() {
    const store = useStore()
    let match_btn_info = ref('开始匹配')
    let bots = ref([])
    let select_bot = ref('-1')

    const overlayRef = ref()

    const click_match_btn = () => {
      if (match_btn_info.value === '开始匹配') {
        match_btn_info.value = '取消匹配'

        /*获取WebSocket实例*/
        const ws = store.state.pk.socket

        /*确保WebSocket实例存在且未关闭*/
        try {
          ws.send(
            JSON.stringify({
              event: 'start-matching',
              bot_id: select_bot.value
            })
          )
          overlayRef.value.style.display = 'none'
        } catch (error) {
          overlayRef.value.style.display = 'block'
          setTimeout(() => {
            overlayRef.value.style.display = 'none'
          }, 2000)
        }
      } else {
        match_btn_info.value = '开始匹配'

        /*获取WebSocket实例*/
        const ws = store.state.pk.socket

        /*确保WebSocket实例存在且未关闭*/
        try {
          ws.send(
            JSON.stringify({
              event: 'stop-matching',
              bot_id: select_bot.value
            })
          )
          overlayRef.value.style.display = 'none'
        } catch (error) {
          overlayRef.value.style.display = 'block'
          setTimeout(() => {
            overlayRef.value.style.display = 'none'
          }, 2000)
        }
      }
    }

    const refresh_bots = () => {
      $.ajax({
        url: 'http://117.50.185.162:3000/api/user/bot/getlist/',
        type: 'get',
        headers: {
          Authorization: 'Bearer ' + store.state.user.token
        },
        success(resp) {
          console.log(resp)
          bots.value = resp
        },
        error(resp) {
          console.log(resp)
        }
      })
    }

    refresh_bots() // 从云端动态获取bots

    return {
      match_btn_info,
      click_match_btn,
      bots,
      select_bot,
      overlayRef
    }
  }
}
</script>

<style scoped>
.root-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}

div.matchground {
  width: 60vw;
  height: 70vh;
  background-color: rgba(50, 50, 50, 0.5);
  margin: 60px auto 0;
}

div.user-photo {
  text-align: center;
  padding-top: 10vh;
}

div.user-photo > img {
  border-radius: 50%;
  width: 20vh;
}

div.user-username {
  text-align: center;
  font-size: 2vw;
  font-weight: 600;
  color: white;
  padding-top: 2vh;
}

div.user-select-bot {
  padding-top: 20vh;
}

div.user-select-bot > select {
  /* width: 60%; */
  margin: 0 auto;
  margin-top: 10vh;
  font-size: 1vw;
}

@media (max-width: 767px) {
  /* 在宽度小于等于767px的屏幕上设置头像大小为屏幕宽度的14% */
  div.matchground {
    width: 80vw;
    height: 65vh;
    background-color: rgba(50, 50, 50, 0.5);
    margin: 40px auto 0;
    font-size: 1.5vh;
  }

  div.user-photo {
    text-align: center;
    padding-top: 10vh;
  }

  div.user-photo > img {
    border-radius: 50%;
    width: 14vw;
  }

  div.user-username {
    text-align: center;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
  }

  div.user-select-bot {
    padding-top: 20vh;
  }

  div.user-select-bot > select {
    font-size: inherit;
    min-width: 18vh;
    margin: 0 auto;
    margin-top: 10vh;
  }
}

@media (max-width: 400px) {
  div.user-select-bot > select {
    margin-left: -2vh;
  }
}

#overlay {
  display: none;
  position: absolute;
  top: 70px;
  width: 75vw;
  height: 100%;
  font-size: 15px;
  line-height: 15px;
}

#message {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 1px 1px 10px rgba(0, 0, 0, 0.8);
}
</style>
