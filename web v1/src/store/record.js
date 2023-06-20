export default {
  state: {
    is_record: false,
    a_steps_x: "",
    a_steps_y: "",
    b_steps_x: "",
    b_steps_y: "",
    record_winer: "",
  },
  getters: {},
  mutations: {  // 用来给state赋值，相当于set(), 但是是私有的，
    updateIsRecord(state, is_record) {
      state.is_record = is_record
    },
    updateSteps(state, data) {
      state.a_steps_x = data.a_steps_x
      state.a_steps_y = data.a_steps_y
      state.b_steps_x = data.b_steps_x
      state.b_steps_y = data.b_steps_y
    },
    updateRecordWiner(state, winer) {
      state.record_winer = winer
    }
  },
  actions: {  // 实现函数，公有函数，可以被外面调用，然后调用私有函数对变量进行赋值
  },
  modules: {}
}