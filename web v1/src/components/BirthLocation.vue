<template>
  <div class="result-board">
<!--    <div class="result-board-text"-->
<!--    >-->
<!--      你是黑棋 - 先手-->
<!--    </div>-->
    <div
      class="result-board-text"
      v-if="
        $store.state.pk.status === 'matching' &&
        $store.state.pk.birthLocation === 'black'
        && $store.state.pk.winer === 'none'
      "
    >
      你是黑棋 - 先手
    </div>
    <div
      class="result-board-text"
      v-if="
        $store.state.pk.status === 'matching' &&
        $store.state.pk.birthLocation === 'white'
        && $store.state.pk.winer === 'none'
      "
    >
      你是白棋 - 后手
    </div>
  </div>
</template>

<script>
export default {
  setup() {
  }
}
</script>

<style scoped>

div.result-board {
  height: 20vh;
  width: 30vw;
  background-color: rgba(50, 50, 50, 0.9);
  position: absolute;
  top: 35vh;
  left: 35vw;
}

div.result-board-text {
  text-align: center;
  color: white;
  font-size: 2vw;
  font-weight: 600;
  font-style: italic;
  padding-top: 5vh;
}


@media (max-width: 767px) {

  div.result-board {
    height: 35vw;
    width: 30vw;
    background-color: rgba(50, 50, 50, 0.9);
    position: absolute;
    top: 30vh;
    left: 35vw;
  }

  div.result-board-text {
    text-align: center;
    color: white;
    font-size: 6vw;
    font-weight: 600;
    font-style: italic;
    padding-top: 5vh;
  }
}
</style>
