# APIOrderMgt

API Order Management | 个人 API 订单管理系统

APIOrderMgt 是一个纯本地化的个人 API 额度订单管理系统，用于记录在不同 API
提供商处购买额度的订单、支付方式和发票信息。系统无需登录，不上传数据，所有
业务数据保存在本机 SQLite 数据库中。

当前版本：`v1.7.5`

## 功能

- 订单列表、详情、新增、编辑和一键复制订单编号。
- 按关键字、提供商、开票状态和发票抬头类型组合筛选订单。
- 支持多订单合并开票，发票批次保存发票信息及关联订单明细。
- 支持重开发票；原发票批次保留为历史记录，新批次重新关联订单。
- 支持作废发票；作废后订单恢复为未开票状态，可再次开票。
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

### 本地启动

后端启动后，浏览器访问：<http://localhost:8080>。
发布方式不影响本地数据目录和接口行为。

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

- `/orders`：订单分页查询、筛选、新增、详情、编辑和批量逻辑删除；
  `/orders/{id}/invoice` 保留为单订单兼容开票入口。
- `/orders/recycle-bin`：查询已删除订单；`/orders/{id}/restore` 恢复订单。
- `/invoices`：创建发票批次、查询发票批次、重开发票和作废发票。
- `/providers`：提供商列表、下拉选项、增删改查和批量物理删除。
- `/invoice-titles`：发票抬头列表、Grouped Select 选项、增删改查和批量物理删除。

发票批次接口：

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| POST | `/invoices` | 根据 `orderIds` 创建发票批次 |
| GET | `/invoices/{id}` | 查询批次、抬头快照和关联订单 |
| POST | `/invoices/{id}/reissue` | 作废原批次并创建新批次 |
| POST | `/invoices/{id}/void` | 作废批次并恢复订单为未开票 |

开票请求字段：`orderIds`、`invoiceDate`、`invoiceNo`、`invoiceTitleId`。

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
打包。运行时由 Spring Boot 统一提供前端页面和后端接口。

```powershell
cd frontend
npm run build

cd ..\backend\APIOrderMgt
mvn package
```

## 数据删除策略

- **订单**：仅逻辑删除，写入删除时间；可在回收站恢复，历史数据不会被清除。
- **提供商、发票抬头**：物理删除。订单中的快照字段用于保留历史展示信息。
- **发票批次**：作废不删除批次及明细，历史发票信息和重开链路永久保留。

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

当前版本 `v1.7.5`。功能、接口和数据模型说明均以当前源码为准。

## 许可证

本项目使用 [MIT License](LICENSE)。
