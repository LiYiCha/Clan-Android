# Clan Management System

一个基于Android MVVM架构的宗门管理系统应用。

## 项目概述

这是一个现代化的宗门管理系统，采用Android MVVM架构模式开发，提供宗门成员管理、角色故事经历、活动组织、任务处理等功能。

## 技术架构

- **架构模式**: MVVM (Model-View-ViewModel)
- **开发语言**: Kotlin
- **UI框架**: Android原生 + Material Design
- **依赖注入**: Hilt/Dagger
- **网络请求**: Retrofit + OkHttp
- **数据存储**: Room数据库
- **异步处理**: Coroutines + Flow
- **构建工具**: Gradle

## 项目结构

```
clan-mvvm/
├── app/                          # 主应用模块
├── module-auth/                  # 用户认证模块
├── library-base/                 # 基础库模块
├── library-mvvmlazy/             # MVVM框架封装
├── library-home/                 # home模块
├── library-other/                # 其他模块（待开发）
├── config.gradle                 # 全局配置
├── build.gradle.kts              # 项目构建配置
├── module.build.gradle           # 公共模块构建配置
└── gradle/                       # Gradle配置
```

## 功能模块

### ✅ 已完成模块

#### 1. 用户认证模块 (module-auth)
- **登录功能**: 用户登录界面
- **界面截图**: 
  
  ![登录界面](image/login.png)
  
- **技术特点**:
  - Material Design风格界面
  - TextInputLayout输入框
  - 响应式UI设计
  - 独立模块化管理

### 🚧 待开发模块

#### 2. 主页模块 (Home)
- **功能规划**:
  - 宗门概览仪表板
  - 快速导航菜单
  - 最新动态展示
  - 个人信息卡片

#### 3. 成员管理模块
- **功能规划**:
  - 成员信息录入
  - 成员树展示
  - 成员搜索筛选
  - 关系图谱

#### 4. 活动管理模块
- **功能规划**:
  - 活动发布
  - 报名管理
  - 活动提醒
  - 历史活动回顾
  
#### 5. 其他模块
- ???

## 开发环境

- **Android Studio**: Arctic Fox 或更高版本
- **最低API级别**: 21 (Android 5.0)
- **目标API级别**: 34 (Android 14)
- **Kotlin版本**: 1.8+

## 快速开始

1. **克隆项目**
   ```bash
   git clone [项目地址]
   cd clan-mvvm
   ```

2. **配置开发环境**
   - 安装Android Studio
   - 配置Android SDK
   - 安装必要的插件

3. **构建项目**
   ```bash
   ./gradlew build
   ```

4. **运行应用**
   - 连接Android设备或启动模拟器
   - 在Android Studio中点击运行按钮

## 模块依赖

```
app/
├── library-base/          # 基础库
├── library-mvvmlazy/      # MVVM框架
└── module-auth/           # 认证模块

module-auth/
├── library-base/          # 基础库
└── library-mvvmlazy/      # MVVM框架
```

## 主题配置

- **基础主题**: AppCompat主题
- **登录模块**: Material Design主题 (独立配置)
- **颜色方案**: 蓝色主色调 (#2D6BF5)

## 贡献指南

1. Fork本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

此项目采用MIT许可证 - 详情请查看LICENSE文件

## 联系方式

- 项目维护者: 
- 邮箱: 
- 项目地址: 

---

**开发状态**: 打副本中 🚀
**最后更新**: $(date +%Y-%m-%d)