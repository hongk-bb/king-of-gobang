const MY_GAME_OBJECTS = []

export class MyGameObject {
  constructor() {
    MY_GAME_OBJECTS.push(this)
    this.has_called_start = false  // 记录是否执行过start函数
    this.timedelta = 0  // 保证两次执行的时间间隔。
    this.game_id = Symbol("game_id")
  }

  start() {
  }

  update() {
  }

  on_destroy() {
  }

  destroy() {
    this.on_destroy()
    // console.log("this: ", this)
    for (let i in MY_GAME_OBJECTS) {
      const obj = MY_GAME_OBJECTS[i]
      // console.log("obj: ", obj)
      if (obj.game_id === this.game_id) {
        console.log("销毁对象: ", obj)
        MY_GAME_OBJECTS.splice(i)  // 删除该位置的元素
        break
      }
    }
  }
}

// if (MyGameObject.prototype.game_id === undefined) {
//   MyGameObject.prototype.game_id = 0
// }


let animation = null
let last_timestamp  // 记录上一次执行的时刻。
const step = timestamp => {
  console.log("帧渲染")
  for (let obj of MY_GAME_OBJECTS) {
    if (!obj.has_called_start) {
      obj.start()
      obj.has_called_start = true
    } else {
      obj.timedelta = timestamp - last_timestamp
      obj.update()
    }
  }

  last_timestamp = timestamp
  animation = requestAnimationFrame(step)

}

export function stop_animation() {
  cancelAnimationFrame(animation)
}

export function start_animation() {
  animation = requestAnimationFrame(step)
}