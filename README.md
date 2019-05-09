# baglock-sdk-java-demo

顺丰箱包锁的demo程序

## 一 环境准备
- 1 安装jar到本地maven仓库,也可以使用deploy方式发布到公司的私有仓库中去。
```shell
mvn install:install-file -Dfile=libs/baglock-sdk-java-1.0.0.jar -DgroupId=com.xeiot.baglock -DartifactId=baglock-sdk-java -Dversion=1.0.0 -Dpackaging=jar
```
- 2 引入sdk到项目中,在pom.xml中添加依赖

```xml
 <!--引入箱包锁的java sdk-->
<dependency>
    <groupId>com.xeiot.baglock</groupId>
    <artifactId>baglock-sdk-java</artifactId>
    <version>1.0.1</version>
</dependency>
```
## 二 执行测试用例
> 参考BagLockTest
