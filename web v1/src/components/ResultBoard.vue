<template>
  <div class="result-board">
    <div
      class="result-board-text"
      v-if="
        $store.state.pk.winer === 'A' &&
        $store.state.pk.a_id === parseInt($store.state.user.id)
      "
    >
      Win
    </div>
    <div
      class="result-board-text"
      v-else-if="
        $store.state.pk.winer === 'B' &&
        $store.state.pk.b_id === parseInt($store.state.user.id)
      "
    >
      Win
    </div>
    <div class="result-board-text" v-else>Lose</div>
    <div class="result-board-button">
      <button @click="restart" type="button" class="btn btn-warning btn-lg" style="font-size:inherit">
        再来!
      </button>
    </div>
  </div>
</template>

<script>
import {useStore} from "vuex"
import victoryMusic from "@/assets/audios/victory.mp3"
import defeatMusic from "@/assets/audios/defeat.mp3"
import {onMounted, onUnmounted} from "vue"

import anonymous from "@/assets/images/anonymous.png"
// import router from "@/router"

export default {
  setup() {
    const store = useStore()

    const playVictoryMusic = () => {
      new Audio(victoryMusic).play()
    }

    const playDefeatMusic = () => {
      new Audio(defeatMusic).play()
    }

    let setTimeID
    onMounted(() => {
      if (
        store.state.pk.winer === "A" &&
        store.state.pk.a_id === parseInt(store.state.user.id)
      ) {
        playVictoryMusic() // 播放胜利音乐
      } else if (
        store.state.pk.winer === "B" &&
        store.state.pk.b_id === parseInt(store.state.user.id)
      ) {
        playVictoryMusic() // 播放胜利音乐
      } else if (store.state.pk.winer !== "none") {
        playDefeatMusic() // 播放失败音乐
      }
      setTimeID = setTimeout(() => {
          restart()
      }, 10000)
    })

    onUnmounted(() => {
      clearInterval(setTimeID)
      restart()
    })

    const restart = () => {
      console.log("游戏结束准备销毁对象: ", "ResultBoard")
      store.commit("distoryGame")
      store.commit("updateIsRecord", false)  // 进入匹配界面
      store.commit("updateStatus", "matching") // 进入匹配界面
      store.commit("updateWiner", "none")
      store.commit("updateBirthLocation", "none")
      store.commit("updateOpponent", {
        username: "我的对手",
        photo: anonymous
      })
      // 如果当前浏览器宽度小于400px, 则浏览器刷新
      if (window.innerWidth < 400) {
        // location.reload()
      }
      // location.reload()
    }

    return {
      restart
    }
  }
}
</script>

<style scoped>

  div.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 40vh;
    left: 35vw;
  }

  div.result-board-text {
    text-align: center;
    color: white;
    font-size: 50px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
  }

  div.result-board-button {
    text-align: center;
    padding-top: 3vh;
  }


@media (max-width: 767px) {
  div {
    font-size: 2vh;
  }

  div.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 35vh;
    left: 35vw;
  }

  div.result-board-text {
    text-align: center;
    color: white;
    font-weight: 600;
    font-size: large;
    font-style: italic;
    padding-top: 5vh;
  }

  div.result-board-button {
    text-align: center;
    padding-top: 7vh;
  }
}
</style>
