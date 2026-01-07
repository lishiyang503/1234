<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>颐养中心管理系统</h2>
        <p>Elderly Care System</p>
      </div>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="手机号"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="密码"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item class="form-actions">
          <el-button
            type="primary"
            native-type="submit"
            @click="handleLogin"
            :loading="loading"
            :class="'btn-login'"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <p>技术支持：×××单位</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)
const focusUsername = ref(false)
const focusPassword = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true
    
    // 模拟登录请求
    const response = await login(loginForm)
    
    // 保存 token 到 localStorage
    localStorage.setItem('token', response.data.token)
    localStorage.setItem('username', loginForm.username)
    
    ElMessage.success('登录成功')
    router.push('/rooms')
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.response?.data?.message || '登录失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--bg-primary);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--bg-secondary);
  border-radius: var(--border-radius);
  border: 1px solid var(--border-color);
  padding: 40px 32px;
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.login-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.login-form {
  width: 100%;
}

.form-actions {
  margin-bottom: 24px;
}

.btn-login {
  width: 100%;
  height: 40px;
  font-size: 16px;
  font-weight: 500;
  border-radius: var(--border-radius);
  transition: all 0.2s ease;
}

.login-footer {
  text-align: center;
}

.login-footer p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}
</style>