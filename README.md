# NexaPay SaaS Platform

Enterprise Multi-Tenant Financial Operations Platform

## 简介

NexaPay是一个企业级多租户SaaS金融运营平台，提供统一的调度、ERP、CRM和支付风险管理控制。

## 技术栈

### 后端
- Java 17 + Spring Boot 3.2
- MyBatis-Plus
- PostgreSQL (多租户隔离)
- Redis (分布式锁 + 缓存)
- JWT 认证

### 前端
- React 18 + TypeScript
- Tailwind CSS v4
- Framer Motion
- TanStack Query v5
- React Router v6
- Recharts

## 项目结构

```
nexapay/
├── sql/                 # 数据库脚本
│   └── nexapay.sql
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── nexapay/
│       │           ├── NexaPayApplication.java
│       │           ├── config/          # 配置
│       │           ├── system/          # 系统模块 (认证/租户)
│       │           ├── erp/             # ERP模块
│       │           ├── crm/             # CRM模块
│       │           ├── payment/         # 支付模块
│       │           ├── risk/            # 风控模块
│       │           └── scheduler/       # 调度模块
│       └── resources/
│           └── application.yml
└── pom.xml

nexapay-web/             # 前端项目
├── src/
│   ├── pages/           # 页面组件
│   ├── components/      # 公共组件
│   ├── hooks/           # React Hooks
│   ├── lib/             # 工具函数
│   ├── App.tsx
│   └── main.tsx
├── package.json
├── vite.config.ts
└── tailwind.config.js
```

## 快速开始

### 后端启动

1. 创建数据库:
```bash
psql -U postgres -f sql/nexapay.sql
```

2. 修改配置 (application.yml):
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nexapay
    username: your_username
    password: your_password

  data:
    redis:
      host: localhost
      port: 6379
```

3. 启动应用:
```bash
mvn spring-boot:run
```

### 前端启动

```bash
cd nexapay-web
npm install
npm run dev
```

## 功能模块

1. **Auth** - 登录、双因素认证、租户选择
2. **Dashboard** - 租户概览和KPI
3. **Scheduler** - 任务队列 + Redis分布式锁
4. **ERP** - 订单管理、库存管理、财务管理
5. **CRM** - 销售管道、客户联系人
6. **Risk Control** - 交易监控、可配置规则引擎
7. **Payments** - 支付网关管理
8. **Tenants** - 多租户管理后台
9. **Settings** - 用户配置、API密钥、安全设置
10. **Export** - 导出HTML静态站/PDF文档

## API端点

- `POST /api/auth/login` - 用户登录
- `GET /api/auth/tenants` - 获取租户列表
- `GET /api/erp/orders` - 获取订单列表
- `GET /api/erp/inventory` - 获取库存列表
- `GET /api/crm/contacts` - 获取联系人列表
- `GET /api/payments` - 获取交易列表
- `GET /api/risk/rules` - 获取风控规则
- `GET /api/scheduler/tasks` - 获取调度任务

## 默认账号

- Username: admin
- Password: admin123
- Tenant: Demo Corp (ID: 2)

## License

Proprietary - NexaPay Confidential
