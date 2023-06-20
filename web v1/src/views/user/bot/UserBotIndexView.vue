<template>
  <div class="container">
    <div class="row">
      <div class="col-3">
        <div class="card">
          <div class="card-body">
            <img :src="$store.state.user.photo" alt="" />
          </div>
        </div>
      </div>
      <div class="col-9">
        <div class="card">
          <div class="card-header">
            <span class="title">我的Bot</span>

            <!-- 添加 "裁判代码" 和 "Bot示例" 按钮 -->
            <div class="btn-group float-end" role="group">
              <button
                type="button"
                class="btn btn-secondary"
                data-bs-toggle="modal"
                data-bs-target="#showRefereeCode"
              >
                裁判代码
              </button>
              <button
                type="button"
                class="btn btn-secondary"
                data-bs-toggle="modal"
                data-bs-target="#showBotExample"
              >
                Bot示例
              </button>
              <button
                type="button"
                class="btn btn-primary"
                data-bs-toggle="modal"
                data-bs-target="#add-bot-button"
              >
                创建Bot
              </button>
            </div>

            <!-- Modal -->
            <div class="modal fade" id="add-bot-button" tabindex="-1">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5">创建Bot</h1>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-title" class="form-label">名称</label>
                      <input
                        v-model="botadd.title"
                        type="text"
                        class="form-control"
                        id="add-bot-title"
                        placeholder="请输入Bot名称"
                      />
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-description" class="form-label">
                        简介
                      </label>
                      <textarea
                        v-model="botadd.description"
                        class="form-control"
                        id="add-bot-description"
                        rows="3"
                        placeholder="请输入Bot简介"
                      ></textarea>
                    </div>
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <VAceEditor
                        v-model:value="botadd.content"
                        @init="editorInit"
                        lang="java"
                        theme="textmate"
                        style="height: 300px"
                      />
                    </div>
                  </div>
                  <div class="modal-footer">
                    <div class="error-msg">{{ botadd.error_msg }}</div>
                    <button
                      type="button"
                      class="btn btn-primary"
                      @click="add_bot"
                    >
                      创建
                    </button>
                    <button
                      type="button"
                      class="btn btn-secondary"
                      data-bs-dismiss="modal"
                    >
                      取消
                    </button>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal fade" id="showRefereeCode" tabindex="-2">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5">裁判代码</h1>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <VAceEditor
                        :value="showRefereeCode.content"
                        @init="editorInit"
                        lang="java"
                        theme="textmate"
                        style="height: 70vh"
                        :readonly="true"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="modal fade" id="showBotExample" tabindex="-3">
              <div class="modal-dialog modal-xl">
                <div class="modal-content">
                  <div class="modal-header">
                    <h1 class="modal-title fs-5">Bot示例</h1>
                    <button
                      type="button"
                      class="btn-close"
                      data-bs-dismiss="modal"
                      aria-label="Close"
                    ></button>
                  </div>
                  <div class="modal-body">
                    <div class="mb-3">
                      <label for="add-bot-code" class="form-label">代码</label>
                      <VAceEditor
                        :value="showBotExample.content"
                        @init="editorInit"
                        lang="java"
                        theme="textmate"
                        style="height: 70vh"
                        :readonly="true"
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="card-body">
            <table class="table table-hover">
              <thead>
                <tr>
                  <th>Bot名称</th>
                  <th>创建时间</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                  <td>{{ bot.title }}</td>
                  <td>{{ bot.createTime }}</td>
                  <td>
                    <button
                      type="button"
                      class="btn btn-secondary"
                      data-bs-toggle="modal"
                      :data-bs-target="'#update-bot-modal-' + bot.id"
                    >
                      修改
                    </button>
                    <button
                      type="button"
                      class="btn btn-danger"
                      @click="remove_bot(bot)"
                    >
                      删除
                    </button>

                    <!-- Modal -->
                    <div
                      class="modal fade"
                      :id="'update-bot-modal-' + bot.id"
                      tabindex="-1"
                    >
                      <div class="modal-dialog modal-xl">
                        <div class="modal-content">
                          <div class="modal-header">
                            <h1 class="modal-title fs-5">创建Bot</h1>
                            <button
                              type="button"
                              class="btn-close"
                              data-bs-dismiss="modal"
                              aria-label="Close"
                            ></button>
                          </div>
                          <div class="modal-body">
                            <div class="mb-3">
                              <label for="add-bot-title" class="form-label">
                                名称
                              </label>
                              <input
                                v-model="bot.title"
                                type="text"
                                class="form-control"
                                id="add-bot-title"
                                placeholder="请输入Bot名称"
                              />
                            </div>
                            <div class="mb-3">
                              <label
                                for="add-bot-description"
                                class="form-label"
                              >
                                简介
                              </label>
                              <textarea
                                v-model="bot.description"
                                class="form-control"
                                id="add-bot-description"
                                rows="3"
                                placeholder="请输入Bot简介"
                              ></textarea>
                            </div>
                            <div class="mb-3">
                              <label for="add-bot-code" class="form-label">
                                代码
                              </label>
                              <VAceEditor
                                v-model:value="bot.content"
                                @init="editorInit"
                                lang="java"
                                theme="textmate"
                                style="height: 300px"
                              />
                            </div>
                          </div>
                          <div class="modal-footer">
                            <div class="error-msg">{{ bot.error_msg }}</div>
                            <button
                              type="button"
                              class="btn btn-primary"
                              @click="update_bot(bot)"
                            >
                              保存修改
                            </button>
                            <button
                              type="button"
                              class="btn btn-secondary"
                              data-bs-dismiss="modal"
                            >
                              取消
                            </button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import $ from 'jquery'
import { useStore } from 'vuex'
import { ref, reactive } from 'vue'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor'
import ace from 'ace-builds'

import 'ace-builds/src-noconflict/ace'
import 'ace-builds/src-noconflict/theme-textmate'
import 'ace-builds/src-noconflict/mode-java'

export default {
  components: {
    VAceEditor
  },

  setup() {
    ace.config.set(
      'basePath',
      'https://cdn.jsdelivr.net/npm/ace-builds@' +
        require('ace-builds').version +
        '/src-noconflict/'
    )

    const store = useStore()
    let bots = ref([])

    const botadd = reactive({
      title: '',
      description: '',
      content: '',
      error_msg: ''
    })

    const showRefereeCode = reactive({
      content:
        'private boolean judgeSuccess(List<Piece> pieces) {\n' +
        '    if (pieces.size() < 5) return false;  // 不够五个棋子\n' +
        '    int[][] g = new int[this.rows][this.cols];\n' +
        '    for (Piece piece : pieces) {\n' +
        '        int x = piece.getX();\n' +
        '        int y = piece.getY();\n' +
        '        g[x][y] = 1;\n' +
        '    }\n' +
        '    Piece currentPiece = pieces.get(pieces.size() - 1);\n' +
        '    int x = currentPiece.getX();\n' +
        '    int y = currentPiece.getY();\n' +
        '    int[] dx = {-1, -1, -1, 0}, dy = {1, 0, -1, -1};\n' +
        '    for (int i = 0; i < 4; i++) { // 8个方向都需要判断\n' +
        '        int newX = x - 4 * dx[i], newY = y - 4 * dy[i];\n' +
        '        int num = 0;\n' +
        '        for (int j = 0; j < 9; j++, newX += dx[i], newY += dy[i]) {\n' +
        '            if (newX < 0 || newX >= 15 || newY < 0 || newY >= 15) continue;\n' +
        '            if (g[newX][newY] == 0) num = 0;\n' +
        '            else if (++num == 5) {\n' +
        '                return true;\n' +
        '            }\n' +
        '        }\n' +
        '    }\n' +
        '    return false;\n' +
        '}'
    })

    // 显示Bot示例
    const showBotExample = reactive({
      content:
        'package com.gobang.botrunningsystem.utils;\n' +
        'import java.util.*;\n' +
        '/*\n' +
        '    类名必须为Bot\n' +
        '    package和implements后面一行必须完全一样\n' +
        '    基于"(自己横坐标操作)-(自己的纵坐标操作)-(对手的横坐标操作)-(对手的纵坐标操作)"字符串处理\n' +
        '    例如：(1#2#3#4)-(5#6#7#8)-(9#10#6#7)-(13#14#4#3)\n' +
        ' */\n' +
        'public class Bot implements com.gobang.botrunningsystem.utils.BotInterface {\n' +
        '    private int[][] board; // 棋盘棋子的摆放情况：0无子，1我方，2地方\n' +
        '    @Override\n' +
        '    public List<Integer> nextMove(String input) {\n' +
        '        board = parseInput(input);\n' +
        '        List<Integer> res = new ArrayList<>();\n' +
        '        Random random = new Random();\n' +
        '        int x, y;\n' +
        '        do {\n' +
        '            x = random.nextInt(15);\n' +
        '            y = random.nextInt(15);\n' +
        '        } while (isOccupied(x, y, input));\n' +
        '        res.add(x);\n' +
        '        res.add(y);\n' +
        '        return res;\n' +
        '    }\n' +
        '\n' +
        '    private boolean isOccupied(int x, int y, String input) {\n' +
        '        return board[x][y] != 0;\n' +
        '    }\n' +
        '\n' +
        '    private int[][] parseBoard(String input) {\n' +
        '        int[][] board = new int[15][15];\n' +
        '        String[] parts = input.split("-");\n' +
        '        int[] myX = parseSteps(parts[0]);\n' +
        '        int[] myY = parseSteps(parts[1]);\n' +
        '        int[] oppX = parseSteps(parts[2]);\n' +
        '        int[] oppY = parseSteps(parts[3]);\n' +
        '\n' +
        '        for (int i = 0; i < myX.length; i++) {\n' +
        '            board[myX[i]][myY[i]] = 1;\n' +
        '        }\n' +
        '        for (int i = 0; i < oppX.length; i++) {\n' +
        '            board[oppX[i]][oppY[i]] = 2;\n' +
        '        }\n' +
        '\n' +
        '        return board;\n' +
        '    }\n' +
        '    private int[] parseSteps(String s) {\n' +
        '        if (s.equals("()")) {\n' +
        '            return new int[0];\n' +
        '        }\n' +
        '        s = s.substring(1, s.length() - 1);\n' +
        '        String[] parts = s.split("#");\n' +
        '        int[] steps = new int[parts.length];\n' +
        '        for (int i = 0; i < parts.length; i++) {\n' +
        '            steps[i] = Integer.parseInt(parts[i]);\n' +
        '        }\n' +
        '        return steps;\n' +
        '    }\n' +
        '}'
    })

    const refresh_bots = () => {
      $.ajax({
        url: 'http://117.50.185.162:3000/api/user/bot/getlist/',
        type: 'get',
        headers: {
          Authorization: 'Bearer ' + store.state.user.token
        },
        success(resp) {
          console.log(resp)
          bots.value = resp
        },
        error(resp) {
          console.log(resp)
        }
      })
    }

    refresh_bots()

    const add_bot = () => {
      botadd.error_msg = ''
      $.ajax({
        url: 'http://117.50.185.162:3000/api/user/bot/add/',
        type: 'post',
        data: {
          title: botadd.title,
          description: botadd.description,
          content: botadd.content
        },
        headers: {
          Authorization: 'Bearer ' + store.state.user.token
        },
        success(resp) {
          if (resp.error_msg === 'success') {
            (botadd.title = ''),
              (botadd.description = ''),
              (botadd.content = ''),
              Modal.getInstance('#add-bot-button').hide()
            refresh_bots()
          } else {
            botadd.error_msg = resp.error_msg
          }
        }
      })
    }

    const update_bot = bot => {
      botadd.error_msg = ''
      $.ajax({
        url: 'http://117.50.185.162:3000/api/user/bot/update/',
        type: 'post',
        data: {
          bot_id: bot.id,
          title: bot.title,
          description: bot.description,
          content: bot.content
        },
        headers: {
          Authorization: 'Bearer ' + store.state.user.token
        },
        success(resp) {
          if (resp.error_msg === 'success') {
            Modal.getInstance('#update-bot-modal-' + bot.id).hide()
            refresh_bots()
          } else {
            botadd.error_msg = resp.error_msg
          }
        }
      })
    }

    const remove_bot = bot => {
      $.ajax({
        url: 'http://117.50.185.162:3000/api/user/bot/remove/',
        type: 'post',
        data: {
          bot_id: bot.id
        },
        headers: {
          Authorization: 'Bearer ' + store.state.user.token
        },
        success(resp) {
          if (resp.error_msg === 'success') {
            refresh_bots()
          }
        }
      })
    }

    return {
      bots,
      botadd,
      add_bot,
      update_bot,
      remove_bot,
      showRefereeCode,
      showBotExample,
      editorInit: null
    }
  }
}
</script>

<style scoped>
div.card {
  margin: 5vh auto;
}

img {
  width: 100%;
}

span.title {
  font-size: 130%;
}

button.btn-secondary {
  margin-right: 10px;
}

div.error-msg {
  color: red;
}

th,
td {
  text-align: center;
  vertical-align: middle;
}
</style>
