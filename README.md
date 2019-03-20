# Dust：类Truffle的数据库开发工具

**提醒：这个项目是一个概念验证的Demo，仅支持的数据库为Postgresql。**

Dust受到了[Truffle](https://truffleframework.com/docs/truffle/overview)的启发，后者是一个专注于以太坊智能合约的开发框架，可以方便的开发、测试和部署智能合约。

长期以来，在数据库开发领域缺少一种通用的开发工具，可以方便地将数据库开发相关的内容进行有效地管理，形成开发、测试、部署的完整闭环。DB Migration实践虽然在一定程度上解决了这个问题，但它是作为敏捷开发项目实践的补充，并非专门针对数据库开发。

Dust的出现将终结这种境况。使用Dust，数据库的开发不再游离于正规的软件开发过程之外，所有与开发有关的数据库代码将通过它进行统一的管理。整个开发流程如下：
1. 创建Dust工程。
1. 开发数据库制品，如表、存储过程、函数、任务、触发器等。
1. 完成相应的测试代码。
1. 本地运行测试。
1. 部署数据库制品到不同的环境。

不仅仅如此，Dust还可以与目前广为接受的持续集成有机结合，成为整个开发部署流水线的一环。

Dust的诞生离不开成功开源软件的支持：
- cli，[picocli](https://picocli.info/)
- 自动化测试，[spock](http://spockframework.org/)
- 本地数据库测试环境，[testcontainers](https://www.testcontainers.org/)
- 轻量级的DB Migration工具，[flyway](https://flywaydb.org/)

## 安装

假设已经安装了java和gradle，通过源码编译和安装步骤如下：

- git clone \< dust 代码仓库\>
- 方法1：
  - gradle installdist，它将在工程的 build/install/dust/ 下安装，bin目录下为可执行文件
- 方法2：生成安装包
  - gradle distTar/distZip，将生成tar或zip的安装包

## 使用

1. 创建工程：dust create app_name，它将创建dust工程目录（它本质上是一个gradle工程），主要结构如下：
   - README.md，readme文件
   - artifacts，数据库制品目录
   - migrations，迁移脚本目录
   - test，测试代码目录
   - dust-config.json，dust配置文件
   - build.gradle，gradle build文件
1. 进入工程目录：cd app_name
1. 在artifacts目录下创建所需数据库制品，如表或存储过程之类，建议每个制品一个文件。
1. 在test目录下创建测试代码，测试代码为spock specification。这个目录下包含相应的一些示例代码，展示了spock和testcontainer的使用。
1. 运行测试：dust test
1. 在migrations目录创建迁移脚本，迁移脚本的命名格式如下：V版本__描述.java，如：V1__CreateUser.java。迁移脚本内容类似：
    ~~~
    public class V1__CreateUser extends DustBaseMigration {
        @Override
        protected String[] files() {
            return new String[]{
                    "./artifacts/myuser.sql"  // 数据制品文件名
            };
        }
    }
    ~~~
1. 部署制品：dust deploy

## 关于配置

目前配置文件（dust-conf.json），主要的作用就是定义deploy命令的不同数据源。这些不同数据源可视为不同的部署环境，典型的配置文件内容如下：
~~~
{
  "development": {
    "url": "jdbc:postgresql://127.0.0.1:5432/earth_test?useUnicode=true&characterEncoding=utf8",
    "user": "earth_admin",
    "password": "admin"
  },
  "production": {
    "url": "jdbc:postgresql://127.0.0.1:5432/jupiter_test?useUnicode=true&characterEncoding=utf8",
    "user": "jupiter_admin",
    "password": "admin"
  }
}
~~~

你也可以定义自己的数据源名称，如myds，在上述文件里添加：
~~~
  "myds": {
    "url": "myurl",
    "user": "myuser",
    "password": "mypassword"
  }
~~~

## 关于部署

dust支持多数据库环境，这样可以将测试和产品环境进行有效隔离。数据源定义在dust-config.json中，通过指定不同的远程数据源定义名即可：
~~~
dust deploy --datasource myds
~~~

如不指定 --datasource，则缺省为 development。

## 关于测试

测试基于testcontainers完成，由于采用了jdbc，因此，理论上testcontainers中支持的数据库，dust都应该支持。目前作为原型demo，dust仅仅只支持postgresql，如果想要将其应用于其他数据库，请做如下改动：

1. 按照上面的命令生成相应的 dust 工程。
1. 在build.gradle中替换如下行：
    ~~~
    runtime 'org.postgresql:postgresql:42.2.5'
    testCompile "org.testcontainers:postgresql:1.10.6"
    ~~~
1. 在test中使用所选数据库对应的testcontainer，实际例子可以参照 /test/DatabaseSpec.groovy。

## 关于部署

部署脚本放置于 migrations 目录，不要删除或修改以下几个文件：
- DustBaseMigration.java
- DustConfiguration.groovy
- MigrationApp.java

迁移脚本中要求实现的 files() ，其指定了数据库制品的执行顺序，甚至你也可以在 artifacts 目录下添加数据初始化文件，然后通过部署脚本完成部署。如：
~~~
@Override
protected String[] files() {
    return new String[]{
            "./artifacts/myuser.sql",         // 创建 myuser 表
            "./artifacts/insert_myuser.sql",  // 插入数据
    };
}
~~~

由于迁移脚本采用的是 flyway 的 Java-based migrations ，若发现当前提供的 DustBaseMigration 类无法满足你的要求，请直接使用 flyway 提供的相应工具类。