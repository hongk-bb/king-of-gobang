<template>
  <div class="result-board">
    <div class="result-board-text" v-if="$store.state.pk.winer === 'A' && $store.state.record.is_record">
      Black is Winer
    </div>
    <div class="result-board-text" v-else-if="$store.state.pk.winer === 'B' && $store.state.record.is_record">
      White is Winer
    </div>
    <div class="result-board-button">
      <button @click="restart" type="button" class="btn btn-warning btn-lg">
        返回
      </button>
    </div>
  </div>
</template>

<script>
import {useStore} from 'vuex'
import router from '@/router/index'
import {onUnmounted} from "vue"

export default {
  setup() {
    const store = useStore()

    const restart = () => {
      console.log("录像结束准备销毁对象: ", "RecordResultBoard")
      store.commit("distoryGame")
      store.commit("updateIsRecord", false)  // 进入匹配界面
      store.commit("updateWiner", "none")
      router.push({name: "record_index"})
      // 如果当前浏览器宽度小于400px, 则浏览器刷新
      if (window.innerWidth < 400) {
        // location.reload()
      }
    }

    // let prepareJump = null
    // onMounted(() => {
    //   prepareJump = setTimeout(() => {
    //     restart()
    //   }, 30000)
    // })

    onUnmounted(() => {
      // clearTimeout(prepareJump)
      restart()
    })

    return {
      restart,
    }
  }
}

</script>

<style scoped>
div.result-board {
  opacity: 0.5;
}
div.result-board:hover {
  opacity: 0.8;
  transition: opacity 0.5s;
}


@media (min-width: 768px) {
  div.result-board {
    height: 30vh;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 35vw;
  }

  div.result-board-text {
    text-align: center;
    color: white;
    font-size: 30px;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
  }

  div.result-board-button {
    text-align: center;
    padding-top: 7vh;
  }
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
    top: 30vh;
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

  .btn-group-lg > .btn, .btn-lg {
    font-size: inherit;
  }
}
</style>