<template>
  <PlayGround></PlayGround>
  <RecordResultBoard v-if="$store.state.pk.winer !== 'none'"></RecordResultBoard>
</template>

<script>

import PlayGround from '@/components/PlayGround.vue'
import RecordResultBoard from '@/components/RecordResultBoard.vue'

export default {
  components: {
    PlayGround,
    RecordResultBoard,
  },
  setup() {

  },
}

</script>

<style scoped>

</style>