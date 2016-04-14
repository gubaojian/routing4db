**Routing4DB** 是由Java实现的基于[接口代理策略](http://gubaojian.blog.163.com/blog/static/166179908201332432825361/)实现的数据源路由框架。通过数据源路由实现以下功能：

一、Master-Slave读写分离实现

  1. 1写Master，读多个Slaves，示意图如下：
> ![http://routing4db.googlecode.com/svn/trunk/docs/images/WriteMasterReadSlaves.jpg](http://routing4db.googlecode.com/svn/trunk/docs/images/WriteMasterReadSlaves.jpg)

  1. 2写Master，读Master和多个Slave，示意图如下
> ![http://routing4db.googlecode.com/svn/trunk/docs/images/WriteMasterReadMasterSlaves.jpg](http://routing4db.googlecode.com/svn/trunk/docs/images/WriteMasterReadMasterSlaves.jpg)

  1. 3 Master-Standby-Slaves实现，此方式示意图如下
> ![http://routing4db.googlecode.com/svn/trunk/docs/images/MasterStandbySlaves.jpg](http://routing4db.googlecode.com/svn/trunk/docs/images/MasterStandbySlaves.jpg)

二、分库路由功能,构建分布式数据库

> 2.1单机分库功能，示意图如下：
> ![http://routing4db.googlecode.com/svn/trunk/docs/images/SingleServerShardTable.jpg](http://routing4db.googlecode.com/svn/trunk/docs/images/SingleServerShardTable.jpg)

> 2.2 多机集群分库，构建分布式数据库，示意图如下：
> ![http://routing4db.googlecode.com/svn/trunk/docs/images/MutiServerShardTable.jpg](http://routing4db.googlecode.com/svn/trunk/docs/images/MutiServerShardTable.jpg)

> 2.3 高可用多机分布式集群，示意图如下：
> ![http://routing4db.googlecode.com/svn/trunk/docs/images/MutlLevelRouting.jpg](http://routing4db.googlecode.com/svn/trunk/docs/images/MutlLevelRouting.jpg)

三、负载均衡


四、自定义数据源路由策略
> 如果框架自带的路由策略不能满足你们要求时，你可以通过的扩展路由接口，自定义路由策略。


五、指定特定数据源


六、支持单数据源事务


七、针对Mybatis的增强功能

Maven依赖
```
<dependency>
        <groupId>com.google.code.routing4db</groupId>
	<artifactId>routing4db</artifactId>
	<version>1.1.0</version>
</dependency>

<repository>
      <id>routing4db.googlecode.com</id>
      <url>http://routing4db.googlecode.com/svn/trunk/repository</url>
</repository>

```

**[快速入门参考](https://code.google.com/p/routing4db/downloads/list)**

**[Routing4DB设计文档](https://code.google.com/p/routing4db/source/browse/#svn%2Ftrunk%2Fdocs)**


如果您对此项目，有兴趣欢迎加入或交流讨论。

项目贡献者：无花

项目作者：谷宝剑

Email： gubaojian@163.com  efurture@gmail.com

QQ号:787277208