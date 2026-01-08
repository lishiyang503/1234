<template>
  <div class="elder-ai-chat">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>智慧健康助手</h2>
      <p>小护将为您提供健康建议与生活指导</p>
    </div>

    <!-- 聊天卡片 -->
    <el-card class="chat-card">
      <template #header>
        <div class="card-header">
          <el-icon><ChatDotSquare /></el-icon>
          <span>小护 · AI 健康助手</span>
        </div>
      </template>

      <!-- 聊天区域 -->
      <div class="chat-container">
        <div class="chat-messages" ref="chatMessages">

          <!-- 欢迎语 -->
          <div v-if="messages.length === 0" class="message ai-message">
            <div class="bubble">
              您好！我是您的智慧健康助手小护 😊  
              <br />  
              您可以向我咨询健康、饮食、生活习惯等问题。
            </div>
          </div>

          <!-- 消息列表 -->
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.sender === 'user' ? 'user-message' : 'ai-message']"
          >
            <div class="bubble">
              {{ msg.content }}
            </div>
            <div class="time">{{ msg.time }}</div>
          </div>

          <!-- 加载中 -->
          <div v-if="loading" class="message ai-message">
            <div class="bubble loading">
              <el-icon class="spin"><Loading /></el-icon>
              小护正在思考中……
            </div>
          </div>
        </div>

        <!-- 快捷问题 -->
        <div v-if="messages.length === 0 && !loading" class="quick-questions">
          <p>常见问题：</p>
          <el-button
            v-for="q in quickQuestions"
            :key="q"
            size="large"
            @click="selectQuickQuestion(q)"
          >
            {{ q }}
          </el-button>
        </div>

        <!-- 输入框 -->
        <div class="input-area">
          <el-input
            v-model="input"
            size="large"
            placeholder="请输入您想咨询的问题…"
            @keyup.enter="sendMessage"
            :disabled="loading"
          >
            <template #append>
              <el-button
                type="primary"
                size="large"
                :disabled="!input.trim() || loading"
                @click="sendMessage"
              >
                <el-icon><Send /></el-icon>
                发送
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ChatDotSquare,
  Loading,
  Message as Send
} from '@element-plus/icons-vue'
import request from '@/utils/request'

// 聊天数据
const messages = ref([])
const input = ref('')
const loading = ref(false)
const chatMessages = ref(null)

// 快捷问题
const quickQuestions = [
  '今天我的身体状况如何？',
  '血压有点高需要注意什么？',
  '老年人应该怎么饮食？',
  '怎样保持良好的睡眠？'
]

// 发送消息
const sendMessage = async () => {
  const text = input.value.trim()
  if (!text || loading.value) return

  // 添加用户消息
  messages.value.push({
    sender: 'user',
    content: text,
    time: new Date().toLocaleTimeString()
  })

  input.value = ''
  loading.value = true
  await nextTick()
  scrollToBottom()

  try {
    // ✅ 与后端 AiChatController 完全一致
    const res = await request.post('/ai/chat', {
      question: text
    })

    if (res.data.code === 200) {
      messages.value.push({
        sender: 'ai',
        content: res.data.data,
        time: new Date().toLocaleTimeString()
      })
    } else {
      ElMessage.error(res.data.msg || 'AI 回复失败')
    }
  } catch (err) {
    ElMessage.error('AI 服务异常')
    console.error(err)
  } finally {
    loading.value = false
    nextTick(scrollToBottom)
  }
}

// 快捷问题
const selectQuickQuestion = (q) => {
  input.value = q
  sendMessage()
}

// 滚动到底部
const scrollToBottom = () => {
  if (chatMessages.value) {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  }
}
</script>

<style scoped>
.elder-ai-chat {
  padding: 32px;
}

.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-header h2 {
  font-size: 34px;
  font-weight: 600;
}

.page-header p {
  font-size: 20px;
  color: #606266;
}

.chat-card {
  max-width: 900px;
  margin: 0 auto;
  border-radius: 12px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 600;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 620px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #f9f9f9;
  border-radius: 8px;
}

.message {
  margin-bottom: 20px;
}

.user-message {
  text-align: right;
}

.bubble {
  display: inline-block;
  padding: 18px 22px;
  border-radius: 12px;
  font-size: 20px;
  line-height: 1.6;
  max-width: 80%;
  background-color: #ecf5ff;
}

.user-message .bubble {
  background-color: #409eff;
  color: #fff;
}

.ai-message .bubble {
  background-color: #ffffff;
}

.time {
  font-size: 14px;
  color: #909399;
  margin-top: 6px;
}

.loading {
  display: flex;
  align-items: center;
  gap: 12px;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.quick-questions {
  margin: 20px 0;
}

.quick-questions p {
  font-size: 18px;
  margin-bottom: 12px;
}

.quick-questions .el-button {
  margin-bottom: 10px;
  width: 100%;
  font-size: 18px;
}

.input-area {
  margin-top: 20px;
}
</style>
