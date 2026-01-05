<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>东软颐养中心</h2>
        <p>请登录您的账户</p>
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
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
            :class="['input-custom', { 'input-focus': focusUsername }]"
            @focus="focusUsername = true"
            @blur="focusUsername = false"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
            :class="['input-custom', { 'input-focus': focusPassword }]"
            @focus="focusPassword = true"
            @blur="focusPassword = false"
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
        <p>&copy; 2026 东软颐养中心. 保留所有权利.</p>
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
  background: linear-gradient(135deg, var(--primary-lighter) 0%, var(--bg-primary) 100%);
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  background: var(--gradient-card);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-lg);
  padding: 40px 32px;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 8px 0;
}

.login-header p {
  font-size: 16px;
  color: var(--text-secondary);
  margin: 0;
}

.login-form {
  width: 100%;
}

.input-custom {
  border-radius: var(--border-radius-sm);
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.input-custom:focus-within,
.input-custom.input-focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(138, 79, 255, 0.1);
}

.form-actions {
  margin-bottom: 24px;
}

.btn-login {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: var(--border-radius-sm);
  transition: all 0.3s ease;
}

.btn-login:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.login-footer {
  text-align: center;
}

.login-footer p {
  font-size: 14px;
  color: var(--text-muted);
  margin: 0;
}
</style>