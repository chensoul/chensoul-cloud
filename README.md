# chensoul-projects

## 开发原则

1. 每开发一个功能，要包含详细的自动化测试
2. 每发现一个bug，修复时要包含详细的自动化测试，测试内容包含能复现 bug 的测试
3. [十二要素应用宣言](https://12factor.net/zh_cn/)
   1. 基准代码：一份基准代码，多份部署。每个应用只对应一份基准代码，但可以同时存在多份部署
   2. 依赖：显式声明依赖关系
   3. 配置：**代码和配置严格分离，将应用的配置存储于环境变量中**
   4. 后端服务：把后端服务当作附加资源
   5. 构建，发布，运行：**严格区分构建，发布，运行这三个步骤**
   6. 进程：以一个或多个无状态进程运行应用
   7. 端口绑定：通过端口绑定来提供服务
   8. 并发：通过进程模型进行扩展
   9. 易处理：快速启动和优雅终止可最大化健壮性
   10. 开发环境与线上环境等价：尽可能的保持开发，预发布，线上环境相同
   11. 日志：把日志当作事件流
   12. 管理进程：后台管理任务当作一次性进程运行
4. 评价一个项目是否优秀的其中一个因素：在不修改基础代码和基础数据的情况下，是否可以随时开源

## 参考

- https://github.com/microsphere-projects

## Stargazers over time

[![Stargazers over time](https://starchart.cc/chensoul/chensoul-projects.svg?variant=adaptive)](https://starchart.cc/chensoul/chensoul-projects)
