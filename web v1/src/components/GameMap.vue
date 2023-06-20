<template>
  <div ref="parent" class="gamemap">
    <canvas ref="canvas"></canvas>
  </div>

</template>

<script>
import {GameMap} from "@/assets/scripts/GameMap"
import store from "@/store"
import {ref, onMounted, onUnmounted} from "vue"

export default {
  setup() {
    let parent = ref(null)
    let canvas = ref(null)
    onMounted(() => {
      store.commit(
        "updateGameObject",
        new GameMap(canvas.value.getContext("2d"), parent.value, store) // 创建一个GameMap对象
      )
    })

    onUnmounted(() => {
      console.log("游戏界面卸载准备销毁对象: ", "GameMap")
      store.commit("distoryGame")
    })

    return {
      parent,
      canvas
    }
  }
}
</script>

<style scoped>
div.gamemap {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

canvas {
  border-radius: 1%;
  -webkit-backface-visibility: hidden;
}
</style>
