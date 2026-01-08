<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>东软颐养中心</h2>
        <el-tabs v-model="userType" class="login-tabs" style="margin-bottom: 20px;">
          <el-tab-pane label="管理端" name="admin">
            <p>管理员登录入口</p>
          </el-tab-pane>
          <el-tab-pane label="老人端" name="elder">
            <p>老人/家属登录入口</p>
          </el-tab-pane>
        </el-tabs>
        
        <!-- 管理端登录 -->
        <div v-if="userType === 'admin'">
          <el-alert
            title="管理端账号"
            type="info"
            :closable="false"
            show-icon
            style="margin-bottom: 20px;"
          >
            <template #default>
              <p>用户名: <strong>admin</strong></p>
              <p>密码: <strong>123456</strong></p>
            </template>
          </el-alert>
          <el-form
            ref="adminFormRef"
            :model="adminForm"
            :rules="adminRules"
            class="login-form"
          >
            <el-form-item prop="username">
              <el-input
                v-model="adminForm.username"
                placeholder="请输入管理员用户名"
                prefix-icon="User"
                clearable
                :class="['input-custom', { 'input-focus': focusAdminUsername }]"
                @focus="focusAdminUsername = true"
                @blur="focusAdminUsername = false"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="adminForm.password"
                type="password"
                placeholder="请输入管理员密码"
                prefix-icon="Lock"
                show-password
                clearable
                :class="['input-custom', { 'input-focus': focusAdminPassword }]"
                @focus="focusAdminPassword = true"
                @blur="focusAdminPassword = false"
              />
            </el-form-item>
            <el-form-item class="form-actions">
              <el-button
                type="primary"
                native-type="submit"
                @click="handleAdminLogin"
                :loading="adminLoading"
                :class="'btn-login'"
              >
                {{ adminLoading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
        
        <!-- 老人端登录/注册 -->
        <div v-else>
          <el-tabs v-model="elderTab" class="login-tabs">
            <el-tab-pane label="登录" name="elderLogin">
              <p>请登录您的账号</p>
            </el-tab-pane>
            <el-tab-pane label="注册" name="elderRegister">
              <p>创建新账号</p>
            </el-tab-pane>
          </el-tabs>
          
          <!-- 老人端登录表单 -->
          <el-form
            v-if="elderTab === 'elderLogin'"
            ref="elderLoginFormRef"
            :model="elderLoginForm"
            :rules="elderLoginRules"
            class="login-form"
          >
            <el-form-item prop="phone">
              <el-input
                v-model="elderLoginForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                clearable
                :class="['input-custom', { 'input-focus': focusElderPhone }]"
                @focus="focusElderPhone = true"
                @blur="focusElderPhone = false"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="elderLoginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                clearable
                :class="['input-custom', { 'input-focus': focusElderPassword }]"
                @focus="focusElderPassword = true"
                @blur="focusElderPassword = false"
              />
            </el-form-item>
            <el-form-item class="form-actions">
              <el-button
                type="primary"
                native-type="submit"
                @click="handleElderLogin"
                :loading="elderLoading"
                :class="'btn-login'"
              >
                {{ elderLoading ? '登录中...' : '登录' }}
              </el-button>
            </el-form-item>
          </el-form>
          
          <!-- 老人端注册表单 -->
          <el-form
            v-else
            ref="elderRegisterFormRef"
            :model="elderRegisterForm"
            :rules="elderRegisterRules"
            class="login-form"
          >
            <el-form-item prop="name">
              <el-input
                v-model="elderRegisterForm.name"
                placeholder="请输入真实姓名"
                prefix-icon="UserFilled"
                clearable
                :class="['input-custom', { 'input-focus': focusRegisterName }]"
                @focus="focusRegisterName = true"
                @blur="focusRegisterName = false"
              />
            </el-form-item>
            <el-form-item prop="idCard">
              <el-input
                v-model="elderRegisterForm.idCard"
                placeholder="请输入身份证号"
                prefix-icon="Postcard"
                clearable
                :class="['input-custom', { 'input-focus': focusRegisterIdCard }]"
                @focus="focusRegisterIdCard = true"
                @blur="focusRegisterIdCard = false"
              />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input
                v-model="elderRegisterForm.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                clearable
                :class="['input-custom', { 'input-focus': focusRegisterPhone }]"
                @focus="focusRegisterPhone = true"
                @blur="focusRegisterPhone = false"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="elderRegisterForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                clearable
                :class="['input-custom', { 'input-focus': focusRegisterPassword }]"
                @focus="focusRegisterPassword = true"
                @blur="focusRegisterPassword = false"
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="elderRegisterForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                prefix-icon="Check"
                show-password
                clearable
                :class="['input-custom', { 'input-focus': focusRegisterConfirmPassword }]"
                @focus="focusRegisterConfirmPassword = true"
                @blur="focusRegisterConfirmPassword = false"
              />
            </el-form-item>
            <el-form-item class="form-actions">
              <el-button
                type="primary"
                native-type="submit"
                @click="handleElderRegister"
                :loading="registerLoading"
                :class="'btn-login'"
              >
                {{ registerLoading ? '注册中...' : '注册' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
      
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
import { login, register } from '@/api/auth'

const router = useRouter()

// 用户类型切换（管理端/老人端）
const userType = ref('admin')

// 老人端标签页切换（登录/注册）
const elderTab = ref('elderLogin')

// 管理端登录相关
const adminFormRef = ref()
const adminLoading = ref(false)
const focusAdminUsername = ref(false)
const focusAdminPassword = ref(false)

const adminForm = reactive({
  username: 'admin', // 默认填充管理员账号
  password: '123456' // 默认填充管理员密码
})

const adminRules = {
  username: [
    { required: true, message: '请输入管理员用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入管理员密码', trigger: 'blur' }
  ]
}

// 老人端登录相关
const elderLoginFormRef = ref()
const elderLoading = ref(false)
const focusElderPhone = ref(false)
const focusElderPassword = ref(false)

const elderLoginForm = reactive({
  phone: '',
  password: ''
})

const elderLoginRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' }
  ]
}

// 老人端注册相关
const elderRegisterFormRef = ref()
const registerLoading = ref(false)
const focusRegisterPhone = ref(false)
const focusRegisterPassword = ref(false)
const focusRegisterConfirmPassword = ref(false)
const focusRegisterName = ref(false)
const focusRegisterIdCard = ref(false)

const elderRegisterForm = reactive({
  phone: '',
  password: '',
  confirmPassword: '',
  name: '',
  idCard: ''
})

const elderRegisterRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 32, message: '密码长度在 6 到 32 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== elderRegisterForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  name: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])\d{3}[0-9Xx]$/, message: '请输入正确的身份证号', trigger: 'blur' }
  ]
}

// 管理端登录
const handleAdminLogin = async () => {
  try {
    await adminFormRef.value.validate()
    adminLoading.value = true
    
    // 登录请求
    const response = await login({
      username: adminForm.username,
      password: adminForm.password
    })
    
    if (response.data.success) {
      // 保存 token 和用户信息到 localStorage
      localStorage.setItem('token', response.data.data.token)
      localStorage.setItem('username', response.data.data.username)
      localStorage.setItem('role', response.data.data.role || 'admin')
      localStorage.setItem('userType', response.data.data.role || 'admin')
      localStorage.setItem('userId', response.data.data.id)
      
      ElMessage.success('管理员登录成功')
      // 跳转到管理端首页（房间管理）
      router.push('/rooms')
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
      console.error('管理员登录失败:', error)
      // 尝试从错误响应中获取具体的错误信息
      let errorMsg = '登录失败，请重试'
      if (error.response?.data?.message) {
        errorMsg = error.response.data.message
      } else if (error.response?.data) {
        errorMsg = JSON.stringify(error.response.data)
      } else if (error.message) {
        errorMsg = error.message
      }
      ElMessage.error(errorMsg)
    } finally {
      adminLoading.value = false
    }
}

// 老人端登录
const handleElderLogin = async () => {
  try {
    await elderLoginFormRef.value.validate()
    elderLoading.value = true
    
    // 登录请求（使用手机号作为用户名）
    const response = await login({
      username: elderLoginForm.phone,
      password: elderLoginForm.password
    })
    
    if (response.data.success) {
      // 保存 token 和用户信息到 localStorage
      localStorage.setItem('token', response.data.data.token)
      localStorage.setItem('username', response.data.data.username)
      localStorage.setItem('role', response.data.data.role || 'elder')
      localStorage.setItem('userType', response.data.data.role || 'elder')
      localStorage.setItem('userId', response.data.data.id)
      localStorage.setItem('phone', elderLoginForm.phone)
      
      ElMessage.success('登录成功')
      // 跳转到老人端首页
      router.push('/elder/home')
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
      console.error('老人端登录失败:', error)
      // 尝试从错误响应中获取具体的错误信息
      let errorMsg = '登录失败，请重试'
      if (error.response?.data?.message) {
        errorMsg = error.response.data.message
      } else if (error.response?.data) {
        errorMsg = JSON.stringify(error.response.data)
      } else if (error.message) {
        errorMsg = error.message
      }
      ElMessage.error(errorMsg)
    } finally {
      elderLoading.value = false
    }
}

// 老人端注册
const handleElderRegister = async () => {
  try {
    await elderRegisterFormRef.value.validate()
    registerLoading.value = true
    
    // 注册请求（使用手机号作为用户名）
    const response = await register({
      username: elderRegisterForm.phone,
      password: elderRegisterForm.password,
      realName: elderRegisterForm.name,
      idCard: elderRegisterForm.idCard,
      phone: elderRegisterForm.phone,
      role: 'elder'
    })
    
    if (response.data.success) {
      // 存储注册信息到localStorage
      localStorage.setItem('elderName', elderRegisterForm.name)
      localStorage.setItem('elderIdCard', elderRegisterForm.idCard)
      localStorage.setItem('elderPhone', elderRegisterForm.phone)
      
      ElMessage.success('注册成功，请登录')
      // 切换到登录标签页
      elderTab.value = 'elderLogin'
      // 清空注册表单
      Object.assign(elderRegisterForm, {
        phone: '',
        password: '',
        confirmPassword: ''
      })
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    console.error('老人端注册失败:', error)
    ElMessage.error(error.response?.data?.message || '注册失败，请重试')
  } finally {
    registerLoading.value = false
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
  margin: 0 0 16px 0;
}

.login-header p {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

.login-tabs {
  margin-top: 16px;
}

.login-tabs .el-tabs__nav {
  justify-content: center;
}

.login-tabs .el-tabs__tab {
  font-size: 16px;
  padding: 10px 24px;
}

.login-tabs .el-tabs__active-bar {
  height: 3px;
  background-color: var(--primary-color);
}

.login-tabs .el-tabs__tab.is-active {
  color: var(--primary-color);
  font-weight: 600;
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