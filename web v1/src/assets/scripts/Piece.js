export class Piece {
  constructor(r, c, step, color) {
    this.r = r
    this.c = c

    // 将横坐标替换为canvas坐标系
    this.x = Math.floor(r) + 0.5 // 先四舍五入再加0.5
    this.y = Math.floor(c) + 0.5
    this.step = step
    this.img = new Image()
    this.img.src = `../images/${color}.png`
  }
}