import {MyGameObject} from "./MyGameObject"
import {Player} from "./Player"
// import router from "@/router"

// 地图是15*15,从[this.L / 2, this.L / 2]开始{左上角}。 0~14
export class GameMap extends MyGameObject {
  constructor(ctx, parent, store) {
    super()
    // this.game_id = MyGameObject.prototype.game_id++

    this.ctx = ctx
    this.ctx.imageSmoothingEnabled = true
    this.ctx.imageSmoothingQuality = "high"

    this.parent = parent
    this.L = 0 // 地图中每一小块的宽度
    this.store = store

    this.rows = 15 // 棋盘大小，多分配一个，棋盘上下左右需要预留空间。实际是14*14
    this.cols = 15


    // this.dpr = (window.devicePixelRatio || 1) * 5

    this.dpr = (Math.ceil(window.devicePixelRatio) + 1) || 2

    this.stars = [
      [3, 3],
      [3, 11],
      [7, 7],
      [11, 3],
      [11, 11]
    ]

    this.players = [
      new Player({id: 0, colorIn: "#636766", colorOut: "#0D0D0D", color: "white"}, this),
      new Player({id: 1, colorIn: "#F9F9F9", colorOut: "#AAAAAA", color: "black"}, this)
    ]
  }

  add_listening_events() {
    if (this.store.state.record.is_record) {
      // 如果是录像
      let k = 0
      let aIdx = 0,
        bIdx = 0
      const a_steps_x = this.store.state.record.a_steps_x.split("#")
      const a_steps_y = this.store.state.record.a_steps_y.split("#")
      const b_steps_x = this.store.state.record.b_steps_x.split("#")
      const b_steps_y = this.store.state.record.b_steps_y.split("#")
      const winer = this.store.state.record.record_winer
      const totalStepNum = a_steps_x.length + b_steps_x.length // 总步数

      console.log(totalStepNum)
      const [player0, player1] = this.players
      const interval_id = setInterval(() => {
        // console.log("k:", k)
        // console.log("location.pathname:", location.pathname)
        if (!k) this.store.commit("updateTotalStepNum", 0)

        let prepareJump = null
        if (k >= totalStepNum || (
          location.pathname === "/record/"
        )) {
          clearInterval(interval_id)
          if (location.href.indexOf("record") !== -1) {
            if (winer === "A") {
              this.store.commit("updateWiner", winer)
              player1.status = "die"
            }
            if (winer === "B") {
              player0.status = "die"
              this.store.commit("updateWiner", winer)
            }
            console.log(this.store.state.pk.winer)
            console.log(this.store.state.record.is_record)
          }
        } else if (k < totalStepNum && k % 2 == 0) {
          player0.set_next_position(
            parseInt(a_steps_x[aIdx]),
            parseInt(a_steps_y[aIdx])
          )
          this.store.commit("updateCurrentPlayer", "A")
          aIdx++
        } else if (k < totalStepNum && k % 2 != 0) {
          player1.set_next_position(
            parseInt(b_steps_x[bIdx]),
            parseInt(b_steps_y[bIdx])
          )
          this.store.commit("updateCurrentPlayer", "B")
          bIdx++
        }
        k++
        if (k < totalStepNum) clearInterval(prepareJump)
      }, 300) // 每1000ms设置下一步位置
    } else {
      // 如果不是录像
      this.ctx.canvas.focus()

      // if (this.store.state.pk.birthLocation === "none") {
      //   this.store.commit("distoryGame")
      //   this.store.commit("updateIsRecord", false)  // 进入匹配界面
      //   this.store.commit("updateStatus", "matching") // 进入匹配界面
      //   this.store.commit("updateWiner", "none")
      // }

      this.ctx.canvas.addEventListener("click", (e) => {
        let r = e.clientX
        let c = e.clientY
        let rect = this.ctx.canvas.getBoundingClientRect() // 用来获取canvas相对于window的位置
        r -= rect.left
        c -= rect.top
        r /= this.L
        c /= this.L

        console.log("r: " + r + " c: " + c)

        if (r > 0 && c > 0) {
          this.store.state.pk.socket.send(
            JSON.stringify({
              event: "move",
              nextStepX: r,
              nextStepY: c
            })
          )
        }
      })
    }
  }

  start() {
    this.add_listening_events()
  }

  update_size() {
    this.L = Math.min(
      this.parent.clientWidth / this.cols,
      this.parent.clientHeight / this.rows
    )

    // 适配高分屏, 修复canvas模糊锯齿问题
    this.ctx.canvas.width = this.L * this.cols * this.dpr
    this.ctx.canvas.height = this.L * this.rows * this.dpr
    this.ctx.canvas.style.width = `${this.L * this.cols}px`
    this.ctx.canvas.style.height = `${this.L * this.rows}px`
    this.ctx.scale(this.dpr, this.dpr)

    // 清除画布
    // this.ctx.clearRect(0, 0, this.L * this.cols, this.L * this.rows)
  }

  // 判断两个玩家是否准备好
  check_ready() {
    for (const player of this.players) {
      if (player.status !== "idle") return false
    }
    return true
  }

  next_step() {
    const [player0, player1] = this.players
    if (this.store.state.pk.currentPlayer === "A") {
      player0.next_step()
    } else if (this.store.state.pk.currentPlayer === "B") {
      player1.next_step()
    }
    // this.store.commit("updateCurrentPlayer", "")
  }

  update() {
    // console.log("update gamemap")
    this.update_size()
    if (this.check_ready()) {
      this.next_step()
    }
    this.render()
    this.ctx.save()
  }

  render() {
    // 绘制棋盘
    this.ctx.fillStyle = "#F6E6B6"
    this.ctx.fillRect(0, 0, this.rows * this.L, this.cols * this.L)
    for (let r = 0; r < this.rows - 1; r++) {
      for (let c = 0; c < this.rows - 1; c++) {
        this.ctx.beginPath()
        this.ctx.lineWidth = this.L / 20
        this.ctx.strokeStyle = "#C0A379"
        this.ctx.rect((r + 0.5) * this.L, (c + 0.5) * this.L, this.L, this.L)
        this.ctx.stroke()
        this.ctx.closePath()
      }
    }
    // 绘制星位
    for (let star of this.stars) {
      let [x, y] = star
      this.ctx.fillStyle = "#C0A379"
      this.ctx.beginPath()
      this.ctx.arc(
        (x + 0.5) * this.L,
        (y + 0.5) * this.L,
        this.L / 4,
        0,
        2 * Math.PI
      )
      this.ctx.fill()
      this.ctx.closePath()
    }
    // this.ctx.scale(this.dpr, this.dpr)
  }

  on_destroy() {
    if (!this.players) return
    const [player0, player1] = this.players
    player0.destroy()
    player1.destroy()
  }

  destroy() {
    this.on_destroy()
    super.destroy()
  }
}
