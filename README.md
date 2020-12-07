# 企业电商平台系统1.0
企业电子商务平台系统（以青橙商城为原型）

## 一、简介

此项目是SSM+dubbo完成的全品类B2C形式的企业电子商务平台，系统采用分布式架构。
部署方式：war包部署

项目开发过程中的的笔记已上传至个人博客：。

### 1. 技术方案

网站整体采用SSM框架、使用dubbo进行各个服务间的通讯。其中的版本参见pom文件。

**网站后台**
	前端主要使用Vue.js+ElementUI来实现数据的绑定及渲染，后端主要使用阿里云oss对象存储完成图片的存储，整体使用了spring security权限框架。

**网站前台**

​	网站前台使用redis作为缓存中间件，使用cas整合spring security作为单点登录，使用rabbitmq作为消息中间件，使用阿里云短息业务完成短信发送，使用elastic search作为搜索中间件，使用thymeleaf 实现页面渲染及网页静态化。

### 2. 已完成的功能

**网站后台：**

后台登录

商品列表的管理

商品回收站

商品审核（上架）

品牌管理

商品分类管理

商品规格参数管理

**网站前台：**

用户注册（阿里云）

用户的登录

搜索栏关键字的搜索

商品详细页

商品加入购物车

购物车管理

首页分类的缓存功能

## 二、注意的问题

- 本项目依赖了zookeeper，cas，elastic search ，redis，rabbitmq，请提前启动相关服务。
- 项目的登录页存放在cas服务器下首页已经放在其他资源的文件夹下。
- 在上传时已经去掉了阿里云的accesskey，请自行补齐。

以下为阿里云accesskey的位置：

> 1.enterprise\emterprise_sms\src\main\resources\sms.properties;
>
> 2.enterprise\enterprise_common_web\src\main\resources\applicationContext-config.xml



