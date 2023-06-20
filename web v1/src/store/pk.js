import {start_animation, stop_animation} from "@/assets/scripts/MyGameObject"

export default {
  state: {
    status: "matching", // matching 表示正在匹配，playing表示正在对战。
    socket: null,
    opponent_username: "",
    opponent_photo: "",
    a_id: 0,
    b_id: 0,
    gameObject: null,
    winer: "none",
    currentPlayer: "",
    birthLocation: "none",
    time: 30,
    nextStepX: "",
    nextStepY: "",
    last_next_x: -1,
    last_next_y: -1,
    totalStepNum: 0
  },
  getters: {},
  mutations: {  // 用来给state赋值，相当于set(), 但是是私有的，
    updateSocket(state, socket) {
      state.socket = socket
    },
    updateOpponent(state, opponent) {
      state.opponent_username = opponent.username
      state.opponent_photo = opponent.photo
    },
    updateStatus(state, status) {
      state.status = status
    },
    updateGame(state, game) {
      state.a_id = game.a_id
      state.b_id = game.b_id
    },
    updateGameObject(state, gameObject) {
      state.gameObject = gameObject
      start_animation()
    },
    updateWiner(state, winer) {
      state.winer = winer
    },
    updateCurrentPlayer(state, currentPlayer) {
      state.currentPlayer = currentPlayer
      state.totalStepNum++
    },
    updateBirthLocation(state, birthLocation) {
      state.birthLocation = birthLocation
    },
    updateTime(state, time) {
      state.time = time
    },
    updateNextStep(state, nextStep) {
      state.nextStepX = nextStep.x
      state.nextStepY = nextStep.y
    },
    updateLastStep(state, lastStep) {
      state.last_next_x = lastStep.x
      state.last_next_y = lastStep.y
    },
    updateTotalStepNum(state, totalStepNum) {
      state.totalStepNum = totalStepNum
    },
    distoryGame(state) {
      // console.log("state.gameObject",state.gameObject)
      // console.log("state.gameObject.destory",state.gameObject.destroy)

      if (state.gameObject) {
        state.gameObject.destroy()
        state.gameObject = null
        console.log("游戏结束，销毁对象")
      }
      stop_animation()
      console.log("游戏结束，停止动画")
    }
  },
  actions: {  // 实现函数，公有函数，可以被外面调用，然后调用私有函数对变量进行赋值
  },
  modules: {}
}