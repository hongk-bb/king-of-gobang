<template>
  <div class="timing">
    <div class="row">
      <div class="col-2">
        <div class="user-photo">
          <img :src="$store.state.user.photo" alt="">
        </div>
      </div>
      <div class="col-3">
        <div class="piece"
             :class="{ 'active': isCurrentPlayer }"
        >
          {{ Piece1 }}
        </div>
        <div class="user-username left-name"
        >
          {{ $store.state.user.username }}
        </div>
      </div>
      <div class="col-2">
        <div class="timing-board"
             :class="{ 'active': isCurrentPlayer }"
        >
          {{ time }}
        </div>
      </div>
      <div class="col-3">
        <div class="piece"
             :class="{ 'active': !isCurrentPlayer }"
        >
          {{ Piece2 }}
        </div>
        <div class="user-username right-name"

        >
          {{ $store.state.pk.opponent_username }}
        </div>
      </div>
      <div class="col-2">
        <div class="user-photo">
          <img :src="$store.state.pk.opponent_photo" alt="">
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {computed, onMounted, onUnmounted, ref} from 'vue'
import {useStore} from 'vuex'
// import Vuex from 'vuex'

export default {
  setup() {
    const store = useStore()

    let Piece1 = ref("")
    let Piece2 = ref("")

    const time = computed(() => store.state.pk.time)
    // const time = ref(30);

    let interval_id = null
    onMounted(() => {
      console.log("store.state.pk.birthLocation: ", store.state.pk.birthLocation)
      if (store.state.pk.birthLocation == 'black') {
        Piece1.value = "Black"
        Piece2.value = "White"
      } else {
        Piece1.value = "White"
        Piece2.value = "Black"
      }

      interval_id = setInterval(() => {
        if (time.value <= 0 || store.state.pk.winer !== "none") {
          clearInterval(interval_id)
        } else {
          store.commit('updateTime', time.value - 1)
        }
      }, 1000)
    })

    onUnmounted(() => {
      clearInterval(interval_id)
    })


    const isCurrentPlayer = computed(() => {
      return store.state.pk.currentPlayer == '' && Piece1.value === "Black" ||
        store.state.pk.currentPlayer == 'A' && Piece1.value === "White" ||
        store.state.pk.currentPlayer == 'B' && Piece1.value === "Black"
    })

    const isMyPlay = computed(() => {
      return true
    })

    return {
      isCurrentPlayer,
      isMyPlay,
      time,
      Piece1,
      Piece2,
    }
  }
}

</script>

<style scoped>

div.timing {
  width: 100%;
  height: 100%;
  color: white;
  background-color: rgba(50, 50, 50, 0.5);
}

div.user-photo {
  text-align: center;
  padding-top: 3vh;
}

div.user-photo > img {
  border-radius: 50%;
  width: 5vh;
}

div.user-username {
  text-align: center;
  font-weight: 600;

  padding-top: 2vh;
}

div.piece {
  text-align: center;
  font-weight: 600;

  padding-top: 5vh;
}

.active {
  color: #fc2d2d;
}

div.timing-board {
  text-align: center;
  font-weight: 600;
  font-size: 50px;
  padding-top: 10px;
  /*padding-top: 5vh;*/
}

.left-name {
  width: 20vw;
  margin-left: -5vw;
}

.right-name {
  width: 20vw;
}

@media (max-width: 767px) {
  .timing > .row {
    padding: 10px;
  }

  div.timing-board {
    margin-left: -10px;
  }
}

</style>