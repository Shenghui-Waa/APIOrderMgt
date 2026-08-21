# APIOrderMgt

API Order Management | 个人 API 订单管理系统

APIOrderMgt 是一个纯本地化的个人 API 额度订单管理系统，用于记录在不同 API
提供商处购买额度的订单、支付方式和发票信息。系统无需登录，不上传数据，所有
业务数据保存在本机 SQLite 数据库中。

当前构建版本：`v1.5.3`

## 功能

- 订单列表、详情、新增、编辑和一键复制订单编号。
- 按关键字、提供商、开票状态和发票抬头类型组合筛选订单。
- 订单开票登记：开票日期、发票编号和发票抬头使用弹窗填写。
- 已开票订单自动锁定，不允许继续修改订单基本信息。
- 订单批量逻辑删除、回收站查看和恢复，订单信息永久保留。
- API 提供商的新增、查看、修改、官网跳转和批量物理删除。
- 发票抬头的新增、查看、修改、复制和按类型筛选，支持批量物理删除。
- 企业抬头支持税号（`taxCode`）；订单保存提供商和发票抬头快照，保证历史信息完整。

## 技术栈

| 模块 | 技术 |
| --- | --- |
| 后端 | Spring Boot 4.0.7、Java 17、Spring MVC、MyBatis Plus 3.5.17 |
| 数据库 | SQLite |
| 前端 | Vue 3、TypeScript、Vite、Element Plus、Vue Router |
| 部署 | Spring Boot 内嵌服务器托管前端静态资源 |

## 快速运行

### 使用已发布 JAR

在包含 JAR 文件的目录执行：

```powershell
java -jar APIOrderMgt-v1.5.3.jar
```

浏览器访问：<http://localhost:8080>

构建产物可从
[GitHub Releases](https://github.com/Shenghui-Waa/APIOrderMgt/releases) 获取。

### 数据库位置

首次启动时，程序会自动创建数据库目录、SQLite 文件、数据表和索引。默认文件为：

```text
data/api-order-mgt.db
```

具体配置写在 `backend/APIOrderMgt/src/main/resources/application-local.yml`：

```yaml
app:
  local:
    database-file-path: data/api-order-mgt.db
    server-address: localhost
    server-port: 8080
```

`application.yml` 使用 `${...:default}` 引用这些值。修改配置后重新启动应用即可生效。

## 页面与接口

前端页面路由：

| 路由 | 说明 |
| --- | --- |
| `/orders` | 订单首页 |
| `/orders/new` | 新增订单 |
| `/orders/:id` | 订单详情 |
| `/orders/:id/edit` | 编辑未开票订单 |
| `/orders/recycle-bin` | 订单回收站 |
| `/providers` | API 提供商管理 |
| `/invoice-titles` | 发票抬头管理 |

后端 API 统一前缀为 `/api/v1`：

- `/orders`：订单分页查询、筛选、新增、详情、编辑、开票和批量逻辑删除。
- `/orders/recycle-bin`：查询已删除订单；`/orders/{id}/restore` 恢复订单。
- `/providers`：提供商列表、下拉选项、增删改查和批量物理删除。
- `/invoice-titles`：发票抬头列表、Grouped Select 选项、增删改查和批量物理删除。

## 本地开发

### 前端

```powershell
cd frontend
npm install
npm run dev
```

开发服务器默认由 Vite 提供，后端接口地址和代理配置以 `frontend/vite.config.ts` 为准。

### 后端

```powershell
cd backend/APIOrderMgt
.\mvnw.cmd spring-boot:run
```

### 构建

先执行 `npm run build`，将前端 `dist` 内容更新到后端静态资源目录，再执行后端 Maven
打包。最终 JAR 是包含前端静态资源和后端运行依赖的 Spring Boot fat JAR。

```powershell
cd frontend
npm run build

cd ..\backend\APIOrderMgt
mvn package
```

## 数据删除策略

- **订单**：仅逻辑删除，写入删除时间；可在回收站恢复，历史数据不会被清除。
- **提供商、发票抬头**：物理删除。订单中的快照字段用于保留历史展示信息。

## 产物校验

`APIOrderMgt-v1.5.3.jar` SHA-256：

```text
4C1CF1AED9C8959326B257BEF39F22BF6BC936C27036F3E3E1BAAE3FC656EC3E
```

## 目录结构

```text
APIOrderMgt/
├─ backend/
│  ├─ parent/                 # 父 Maven 模块，统一管理后端依赖
│  └─ APIOrderMgt/            # Spring Boot 应用
├─ frontend/                  # Vue 3 + Element Plus 前端
├─ LICENSE
└─ README.md
```

## 发布

项目仓库：[Shenghui-Waa/APIOrderMgt](https://github.com/Shenghui-Waa/APIOrderMgt)

当前版本 `v1.5.3` 已发布 GitHub Release，可从仓库的 Releases 页面进入并下载构建产物。

## 许可证

本项目使用 [MIT License](LICENSE)。
