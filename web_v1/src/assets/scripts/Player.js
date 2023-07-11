import { MyGameObject } from './MyGameObject'
import { Piece } from './Piece'
import { useStore } from 'vuex'

export class Player extends MyGameObject {
  constructor(info, gamemap) {
    super()
    // this.game_id = MyGameObject.prototype.game_id++

    this.id = info.id // 取出对应玩家的id
    this.colorIn = info.colorIn
    this.colorOut = info.colorOut
    this.color = info.color
    this.gamemap = gamemap

    this.pieces = [] // 用来保存组成该玩家棋情的所有棋子。
    this.store = gamemap.store

    this.next_piece = ''

    this.next_x = -1
    this.next_y = -1
    this.status = 'idle' // wait表示等待对方监听，idle表示等待玩家操作，play表示正在下，die表示死亡。

    this.store = useStore()
    this.step = 0

    this.testTime = null
  }

  start() {}

  set_next_position(x, y) {
    this.next_x = x
    this.next_y = y
    this.store.commit('updateNextStep', {
      x: this.next_x.toString(),
      y: this.next_y.toString()
    })
  }

  next_step() {
    const x = this.next_x
    const y = this.next_y

    this.next_piece = new Piece(
      x,
      y,
      this.store.state.pk.totalStepNum,
      this.color
    )
    this.store.commit('updateNextStep', {
      x: this.next_piece.x.toString(),
      y: this.next_piece.y.toString()
    })
    // this.next_x = -1;
    // this.next_y = -1;
    this.status = 'move'
  }

  update_move() {
    this.pieces.push(this.next_piece)
    this.status = 'idle'
  }

  update() {
    // console.log("update gamemap")
    if (this.status === 'move') this.update_move()
    this.render()
  }

  render() {
    const L = this.gamemap.L
    const ctx = this.gamemap.ctx

    const last_piece = this.pieces[this.pieces.length - 1]
    const x = parseFloat(this.store.state.pk.nextStepX)
    const y = parseFloat(this.store.state.pk.nextStepY)
    const last_next_x = parseFloat(this.store.state.pk.last_next_x)
    const last_next_y = parseFloat(this.store.state.pk.last_next_y)
    const r1 = Math.floor(L * 0.1),
      r2 = Math.floor(L * 0.4)

    // console.log("r1=", r1, " r2=", r2)

    let commonGradient = null

    if (last_piece) {
      let xx = x >= 0 ? x : last_next_x
      let yy = y >= 0 ? y : last_next_y
      xx = Math.floor(xx * L)
      yy = Math.floor(yy * L)
      // console.log("光晕绘制: xx=", xx, " yy=", yy)
      const haloOpacity = Math.max(0, 0.4 - Math.sin(Date.now() / 250) * 0.4)
      // 绘制动态光晕
      commonGradient = ctx.createRadialGradient(
        xx,
        yy,
        r2,
        xx,
        yy,
        r2 + r1 * (1 + 3 * haloOpacity)
      )
      commonGradient.addColorStop(
        0,
        `rgba(255,${20 * (1 + 6 * haloOpacity)},${
          20 * (1 + 2 * haloOpacity)
        },${haloOpacity})`
      )
      commonGradient.addColorStop(1, `rgba(255, 20, 20, 0)`)
      ctx.save()
      ctx.fillStyle = commonGradient
      ctx.beginPath()
      ctx.arc(xx, yy, r2 * (1 + 3 * haloOpacity), 0, 2 * Math.PI)
      ctx.arc(xx, yy, r2 - r2 * 0.005, 0, 2 * Math.PI, true)
      ctx.fill()
      ctx.closePath()
      ctx.restore()
    }

    // 实现渐变圆
    for (const piece of this.pieces) {
      ctx.beginPath()
      let xx = Math.floor(piece.x * L),
        yy = Math.floor(piece.y * L)
      // console.log("圆绘制: xx=", xx, " yy=", yy)
      ctx.arc(xx, yy, r2, 0, Math.PI * 2) // 绘制棋子
      if (xx >= 0 && yy >= 0) {
        commonGradient = ctx.createRadialGradient(xx, yy, r1, xx, yy, r2) // 创建渐变圆
        commonGradient.addColorStop(0, this.colorIn)
        commonGradient.addColorStop(1, this.colorOut)
        ctx.fillStyle = commonGradient
        ctx.fill()
        ctx.closePath()
        //  绘制数字
        // ctx.fillStyle = this.color
        // ctx.textAlign = 'center'
        // ctx.textBaseline = 'middle'
        // ctx.font = Math.floor(L * 0.42) + 'px bold serif'
        // ctx.fillText(piece.step.toString(), xx, yy + r1 * 0.1)
      }
    }
    commonGradient = null
  }

  destroy() {
    super.destroy()
  }

  //   绘制目标线

  // const x = parseFloat(this.store.state.pk.nextStepX)
  // const y = parseFloat(this.store.state.pk.nextStepY)
  // console.log("this.store.state.pk.nextStepX=", this.store.state.pk.nextStepX)
  // console.log("this.store.state.pk.nextStepY=", this.store.state.pk.nextStepY)
  // if (x >= 0 && y >= 0) {
  //   let dx = [0, 1, 0, -1], dy = [-1, 0, 1, 0]
  //   for (let i = 0; i < 4; i++) {
  //     ctx.beginPath()
  //
  //     ctx.lineWidth = Math.floor(L / 20)//设置线条宽度10
  //
  //     ctx.strokeStyle = 'red'//设置线条颜色为红色
  //     ctx.moveTo(Math.floor((x - dx[i] * 1 / 15) * L), Math.floor((y - dy[i] * 1 / 15) * L))
  //     ctx.lineTo(Math.floor((x - dx[i] * 1 / 5) * L), Math.floor((y - dy[i] * 1 / 5) * L))
  //     ctx.stroke()
  //     ctx.closePath()
  //   }
  // } else if (this.store.state.pk.winer != "none") {
  //   let dx = [0, 1, 0, -1], dy = [-1, 0, 1, 0]
  //   for (let i = 0; i < 4; i++) {
  //     ctx.beginPath()
  //     ctx.lineWidth = Math.floor(L / 20)//设置线条宽度10
  //     ctx.strokeStyle = 'red'//设置线条颜色为红色
  //
  //     const last_next_x = parseFloat(this.store.state.pk.last_next_x)
  //     const last_next_y = parseFloat(this.store.state.pk.last_next_y)
  //     ctx.moveTo(Math.floor((last_next_x - dx[i] * 1 / 15) * L), Math.floor((last_next_y - dy[i] * 1 / 15) * L))
  //     ctx.lineTo(Math.floor((last_next_x - dx[i] * 1 / 5) * L), Math.floor((last_next_y - dy[i] * 1 / 5) * L))
  //     ctx.stroke()
  //     ctx.closePath()
  //   }
  // }
}
