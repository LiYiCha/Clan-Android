# Library-Captcha 验证码模块

基于 [anji-captcha](https://gitee.com/anji-plus/captcha) 封装的 Android 验证码组件。

## 功能特性

- ✅ 滑块拼图验证码
- ✅ 文字点选验证码
- ✅ 支持自定义服务器地址
- ✅ 简洁的 API 调用方式
- ✅ Kotlin 协程支持

## 快速开始

### 1. 添加依赖

在模块的 `build.gradle` 中添加：

```gradle
implementation(project(":library-captcha"))
```

### 2. 初始化

在 `Application` 中初始化（推荐）：

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化验证码服务，使用你的后端地址
        CaptchaManager.init(
            context = this,
            serverUrl = "http://your-server:8080/api/v1/captcha/"
        )
    }
}
```

或者在使用前初始化：

```kotlin
// 在 Activity/Fragment 中
CaptchaManager.init(applicationContext, "http://your-server:8080/api/v1/captcha/")
```

### 3. 显示验证码

#### 滑块拼图验证码

```kotlin
CaptchaManager.showBlockPuzzleCaptcha(
    context = this,
    onSuccess = { code ->
        // 验证成功，code 用于登录接口
        Log.d("Captcha", "验证成功: $code")
        viewModel.login(username, password, code)
    },
    onFailure = { error ->
        // 验证失败
        Toast.makeText(this, "验证失败: $error", Toast.LENGTH_SHORT).show()
    },
    onCancel = {
        // 用户取消（可选）
        Log.d("Captcha", "用户取消验证")
    }
)
```

#### 文字点选验证码

```kotlin
CaptchaManager.showWordCaptcha(
    context = this,
    onSuccess = { code ->
        viewModel.login(username, password, code)
    },
    onFailure = { error ->
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }
)
```

## 在登录模块中使用

### LoginActivity.kt

```kotlin
class LoginActivity : AppCompatActivity() {
    
    private val viewModel: LoginViewModel by viewModels()
    
    private fun onLoginClick() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()
        
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
            return
        }
        
        // 显示验证码
        CaptchaManager.showBlockPuzzleCaptcha(
            context = this,
            onSuccess = { code ->
                // 验证成功，执行登录
                viewModel.login(username, password, code)
            },
            onFailure = { error ->
                Toast.makeText(this, "验证失败", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
```

### LoginRepository.kt

```kotlin
class LoginRepository {
    
    private val authApi by lazy {
        RetrofitClient.instance.create(AuthApi::class.java)
    }
    
    suspend fun login(username: String, password: String, captchaCode: String): CommonResult<LoginResponse> {
        return authApi.login(username, password, captchaCode)
    }
}
```

## 后端接口

验证码模块需要后端提供以下接口：

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取验证码 | POST | `/api/v1/captcha/get` | 获取验证码图片 |
| 验证验证码 | POST | `/api/v1/captcha/check` | 验证用户操作 |

后端使用 [anji-captcha](https://gitee.com/anji-plus/captcha) 库实现。

## 配置说明

### 服务器地址格式

```
http://your-server:port/api/v1/captcha/
```

- 必须以 `/` 结尾
- Android 模拟器访问本机使用 `10.0.2.2` 替代 `localhost`

### 开发环境示例

```kotlin
// 模拟器访问本机
CaptchaManager.init(this, "http://10.0.2.2:8080/api/v1/captcha/")

// 真机访问局域网
CaptchaManager.init(this, "http://192.168.1.100:8080/api/v1/captcha/")

// 生产环境
CaptchaManager.init(this, "https://api.example.com/api/v1/captcha/")
```

## API 参考

### CaptchaManager

| 方法 | 说明 |
|------|------|
| `init(context, serverUrl)` | 初始化验证码服务 |
| `isInitialized()` | 检查是否已初始化 |
| `getServerUrl()` | 获取当前服务器地址 |
| `showBlockPuzzleCaptcha(...)` | 显示滑块拼图验证码 |
| `showWordCaptcha(...)` | 显示文字点选验证码 |

## 注意事项

1. **必须先初始化**：调用 `showXxxCaptcha` 前必须先调用 `init()`
2. **网络权限**：确保 `AndroidManifest.xml` 中声明了网络权限
3. **HTTP 明文**：如果使用 HTTP（非 HTTPS），需要配置 `android:usesCleartextTraffic="true"`

## 许可证

MIT License
