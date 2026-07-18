# Temp Note

一个轻量级的 Android 临时笔记应用，笔记内容设有自动过期时间。

## 功能特点

- **自动过期**：每条笔记可设置 6 小时、24 小时或 7 天的保留时长，到期后自动移入回收站
- **回收站保护**：回收站中的笔记保留 30 天，之后自动永久删除
- **简洁编辑**：打开即可编辑，退出时自动保存
- **数据持久化**：使用 Room 数据库本地存储，无需网络权限

## 技术栈

- **语言**：Kotlin
- **架构**：MVVM (ViewBinding + ViewModel)
- **数据库**：Room (SQLite)
- **异步**：Kotlin Coroutines + Flow
- **后台任务**：WorkManager（每小时自动清理过期笔记）
- **UI 组件**：RecyclerView (GridLayoutManager)、Material Design

## 构建要求

- Android Studio Hedgehog 或更高版本
- JDK 11+
- compileSdk / targetSdk: 33
- minSdk: 24

## 权限说明

- 无任何网络权限
- 无需任何系统特殊权限
- 纯本地数据存储
