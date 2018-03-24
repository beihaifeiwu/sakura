# MySpring  [![image](http://pub.idqqimg.com/wpa/images/group.png)](https://jq.qq.com/?_wv=1027&k=5hMphCE)

> SpringBoot加速器

## 目录

- [简介](#简介)
- [最佳实践](#最佳实践)
- [快速入门](#快速入门)
- [功能特性](#功能特性)
- [附录](#附录)

## 简介

MySpring是一个SpringBoot项目的加速器，其基于SpringBoot在项目实践中遇到的问题
进行扩充并支持了许多SpringBoot未直接支持的框架和用法。内建GIS函数库，提供多
个便捷服务和工具类，可以提高基于SpringBoot项目的开发效率

## 最佳实践

* **日志**

日志推荐使用**log4j2**，目前许多项目都是直接使用log4j2作为日志框架（例如
Elasticsearch），且log4j2的性能还是很不错的，可以查看与logback的[性能对比](https://logging.apache.org/log4j/2.x/performance.html)。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

* **JSON**

Json序列化框架推荐使用**Jackson**，其拥有丰富的功能模块，且支持JTS（地理信息
类库），SpringBoot对其的定制也很方便

```properties
spring.jackson.default-property-inclusion=non_null
spring.jackson.deserialization.accept-float-as-int=true
spring.jackson.deserialization.fail-on-unknown-properties=false
spring.jackson.serialization.indent_output=true
spring.jackson.mapper.use-static-typing=false
spring.jackson.mapper.ignore-duplicate-module-registrations=true
```

* **缓存**

轻量级的缓存推荐使用[**Caffeine**](https://github.com/ben-manes/caffeine)

```properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=2000,expireAfterWrite=1h
```

```xml
    <dependency>
      <groupId>com.github.ben-manes.caffeine</groupId>
      <artifactId>caffeine</artifactId>
    </dependency>
```

## 快速入门

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.0.M6</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxx.test</groupId>
    <artifactId>test-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <!-- Import dependency management from MySpring -->
                <groupId>com.github.beihaifeiwu.myspring</groupId>
                <artifactId>myspring-bom</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- jpa -->
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-hibernate</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>provided</scope>
            <optional>true</optional>
        </dependency>

        <!-- 内嵌数据库，正式使用时请删除 -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
    </dependencies>

</project>
```

## 功能特性

- [Core](#core)
- [Beetl](#beetl)
- [Curator](#curator)
- [JPA](#jpa)
- [Mybatis](#mybatis)
- [Spatial](#spatial)
- [Springfox](#springfox)

### **Core**

```xml
        <!-- 其它 myspring-starter-xxx 模块会默认引入本模块 -->
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter</artifactId>
        </dependency>
```

* **SpringBoot**上下文的便捷静态函数：

 |类名              |   功能
 |------------------|----------------------
 |SpringBeans       |获取Spring管理的Bean
 |SpringContext     |获取各种Spring服务和配置（conversionService，basePackage, serverPort, executor...)
 |SpringEnvironment |获取Spring环境变量

* [**OkHttp3**](http://square.github.io/okhttp/)

MySpring 默认在Spring容器中维护一个OkHttpClient实例，可以通过Properties配置其
属性。在有spring-web模块存在时,**RestTemplate**也会使用此实例：

```xml
myspring.okhttp.connection-timeout=10000
myspring.okhttp.follow-redirects=true
myspring.okhttp.follow-ssl-redirects=true
myspring.okhttp.retry-on-connection-failure=true

myspring.okhttp.logging.level=basic
myspring.okhttp.logging.log-level=info
myspring.okhttp.logging.log-name=com.github.beihaifeiwu.myspring.http.okhttp
```

> 为 OkHttp 配置**请求**拦截器的时候需要加上Qualifier注解 **@OkHttpInterceptor**，否则不会被MySpring识别

> 为 OkHttp 配置**网络**拦截器的时候需要加上Qualifier注解 **@OkHttpNetworkInterceptor**，否则不会被MySpring识别

* **Template** 服务：

TemplateService 是一个可以脱离Web环境使用的模板服务，其在各种模板引擎之上提
供一层抽象，对外提供服务并通过后缀名区分不同模板,支持的模板如下：

 | 模板引擎      |  后缀
 |---------------|-------------
 |Beetl          |.btl
 |Freemarker     |.ftl
 |Thymeleaf      |.html
 |Velocity       |.vm

> Spring最近的版本已经移除了对Velocity的支持，MySpring从Spring旧版本中抽取了对
> Velocity支持的源码并对其做了一些删减和改进，可以引入相应starter使用:

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-velocity</artifactId>
        </dependency>
```

* **Spring Data Repository** 增强

MongoTemplate以及ElasticsearchTemplate提供了大量特定于数据库的使用方法，其
无法用Repository的方式表达，故对默认的Repository实现进行了增强，暴露大量Template
里面的方法：

 | 数据库        |  Repository扩展
 |---------------|--------------------------
 |MongoDB        |com.github.beihaifeiwu.myspring.data.mongodb.repository.MongoExtRepository
 |MongoDB(Async) |com.github.beihaifeiwu.myspring.data.mongodb.repository.ReactiveMongoExtRepository
 |Elasticsearch  |com.github.beihaifeiwu.myspring.data.elasticsearch.repository.ElasticsearchExtRepository

```java
public interface DepartmentRepository extends MongoExtRepository<Department, String> {
    Department findByName(String name);
}
public interface ArticleRepository extends ElasticsearchExtRepository<Article, String> {
}
```

> MongoTemplate 实体映射时去除了 **_class** 类型绑定字段, 并提供了对 **JTS** 的Geometry类型的序列化和反序列化支持

> ElasticsearchTemplate 使用 **JacksonEntityMapper** 替换默认的实现，并识别 @JsonProperty 作为字段映射

* **Spring Web** 增强

**RestExceptionHandler**提供了对统一的异常处理，可以通过在配置文件**MySpring-exception-mapping.properties**
中定义异常消息返回内容的方式，对异常进行统一管理：

```properties
org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.status=400
org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.error=Type Mismatch
# 可以使用SPEL表达式
org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.message=The method param #{ex.name} type mismatched #{T(org.springframework.util.StringUtils).uncapitalize(T(com.google.common.base.Throwables).getRootCause(ex).message)}.
```

**@ParamName**提供了对Controller方法Bean参数的请求参数名映射支持：

```java
    @NotNull
    @ParamName("from_x")
    private Double fromX;

    @NotNull
    @ParamName("from_y")
    private Double fromY;
```

**MultipartFileSender**提供了对文件下载断点续传的支持：

```java
    MultipartFileSender.fromFile(file)
            .with(request)
            .with(response)
            .serveResource();
```

**HttpOptionMethodFilter**拦截对OPTION请求的处理(MySpring默认开启）：

```properties
spring.mvc.dispatch-options-request=false
myspring.web.cors.enabled=true
```

**ShallowEtagHeaderExtFilter**扩展了Spring对ETag的支持，可选择匹配路径：

```properties
myspring.web.shallow-etag.enabled=true
myspring.web.shallow-etag.write-weak-etag=true
myspring.web.shallow-etag.exclude-paths=/map/*/file,/map/*/origin_file,/map/*/sqlite
```

* 便捷工具类库：

 |类名     |   功能
 |---------|----------------------
 |EX       |异常包装工具类，支持对lambda表达式的Unchecked转换
 |HTTP     |基于OkHttp封装的http工具类，支持流式API
 |JSON     |封装Jackson的常用操作，并会尝试使用Spring装配的ObjectMapper
 |Lazy     |懒计算(加载)包装对象
 |Network  |网络相关方法（host ip，mac等）
 |REGEX    |常用正则表达式常量和正则表达式缓存支持，参考[Hutool](https://github.com/looly/hutool)
 |Threads  |封装线程池创建和销毁的工具方法

### [**Beetl**](http://ibeetl.com/)

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-beetl</artifactId>
        </dependency>
```

MySpring提供了对Beetl这个轻量级模板引擎的开箱即用的支持：

```properties
myspring.beetl.cache=false
myspring.beetl.charset=UTF-8
myspring.beetl.check-template-location=false
myspring.beetl.content-type=text/html
```

### [**Curator**](https://curator.apache.org/)

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-curator</artifactId>
        </dependency>
```

MySpring提供了对Curator操作的支持，提供了CuratorTemplate服务，
实现了类似@JmsListener的注解驱动MDP：

```java
    @ZNodeListener("${curator.test.path.node-cache}")
    public void onSceneNode(@Payload String data,
                            @Header("path") String path,
                            ChildData childData) {
        System.out.format("****** [%s]: %s", path, data);
        System.out.println();

        assertThat(childData)
                .isNotNull()
                .hasFieldOrPropertyWithValue("path", path)
                .hasFieldOrPropertyWithValue("path", "/data/scene")
                .hasFieldOrPropertyWithValue("data", data == null ? new byte[0] : data.getBytes());
    }
```

```properties
myspring.curator.connect-string=localhost:2181
```

支持Zookeeper内嵌服务：

```properties
myspring.zookeeper.embedded.enable=true
myspring.zookeeper.embedded.port=2181
```

### JPA

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-hibernate</artifactId>
        </dependency>
```

MySpring使用基于Hibernate的JPA实现，并集成了对Spatial，QueryDSL的支持

### Mybatis

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-mybatis</artifactId>
        </dependency>
```

MySpring使用了Mybatis的增强类库[MybatisPlus](http://mp.baomidou.com/)，其简化了
Mybatis的CRUD操作并支持活动记录模式。MybatisPlus也对MBG做了扩展，可以使用
[mybatisplus-maven-plugin](https://github.com/baomidou/mybatisplus-maven-plugin)，生成MybatisPlus代码。

> MybatisPlus代码生成可参考[配置](http://git.oschina.net/baomidou/mybatisplus-maven-plugin)

### Spatial

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-spatial</artifactId>
        </dependency>
```

MySpring基于[H2GIS](http://www.h2gis.org/)抽取了一整套的地理信息处理函数，H2GIS
的实现依赖于数据库，MySpring去除了对数据库的依赖，真正使其成为了一套函数类库，
其支持的函数列表参见 http://www.h2gis.org/docs/1.3.1/functions/

### [**Springfox**](http://springfox.github.io/springfox/)

```xml
        <dependency>
            <groupId>com.github.beihaifeiwu.myspring</groupId>
            <artifactId>myspring-starter-springfox</artifactId>
        </dependency>
```

Springfox是基于Swagger一个开源框架，其提供了对SpringMVC的支持，可基于SpringMVC
的注解配合Swagger注解提供文档生成和展示。

```java
    @ApiOperation(value = "获取地区列表", notes = "根据场景，返回该场景的地区信息")
    @GetMapping
    public List<CountryUnit> getRegions(@RequestParam(value = "country", required = false) String country,
                                        @RequestParam(value = "parent", required = false) @Positive Integer parent) {
        if (parent != null) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(country),
                    "country must be provided while query by parent.");
        }
        return regionService.getRegionsInScene(sceneContext, country, parent);
    }
```

## 附录

### QueryDSL maven插件配置

```xml
        <plugin>
            <groupId>com.mysema.maven</groupId>
            <artifactId>apt-maven-plugin</artifactId>
            <version>1.1.3</version>
            <configuration>
                <outputDirectory>target/generated-sources/java</outputDirectory>
                <logOnlyOnError>true</logOnlyOnError>
                <processors>
                    <processor>com.querydsl.apt.QuerydslAnnotationProcessor</processor>
                    <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                    <processor>
                        org.springframework.data.mongodb.repository.support.MongoAnnotationProcessor
                    </processor>
                </processors>
            </configuration>
            <executions>
                <execution>
                    <id>querydsl</id>
                    <goals>
                        <goal>process</goal>
                    </goals>
                    <phase>generate-sources</phase>
                </execution>
            </executions>
            <dependencies>
                <dependency>
                    <groupId>com.querydsl</groupId>
                    <artifactId>querydsl-apt</artifactId>
                    <version>${querydsl.version}</version>
                </dependency>
                <dependency>
                    <groupId>com.querydsl</groupId>
                    <artifactId>querydsl-jpa</artifactId>
                    <version>${querydsl.version}</version>
                </dependency>
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-data-mongodb</artifactId>
                    <version>${spring-boot.version}</version>
                </dependency>
            </dependencies>
        </plugin>
```

### Docker & DockerCompose

> 安装

```bash
curl -sSL https://get.daocloud.io/docker | sh

curl -L https://get.daocloud.io/docker/compose/releases/download/1.15.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose
```

> Spring Boot 可执行jar包的常用**Dockerfile**格式

```properties
FROM openjdk:8u131-jdk

WORKDIR /app

COPY ./lib/application-1.0.0.jar /app/application.jar

ENTRYPOINT ["java","-jar","/app/application.jar"]
```

> 增加中文和拼音分词的Elasticsearch镜像Dockerfile

```properties
FROM docker.elastic.co/elasticsearch/elasticsearch:5.5.1

USER root
RUN cd /usr/share/elasticsearch \
       && ./bin/elasticsearch-plugin remove x-pack --purge \
       && ./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.5.1/elasticsearch-analysis-ik-5.5.1.zip \
       && ./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-pinyin/releases/download/v5.5.1/elasticsearch-analysis-pinyin-5.5.1.zip \
       && chown -R elasticsearch:elasticsearch ./plugins

USER elasticsearch
```

> 常用命令

```bash
docker rmi $(docker images -q -f dangling=true) # 移除悬挂的镜像（没有repository和tag）
```

### Keytool

* keystore生成
```bash
keytool -genkey -alias app -keyalg RSA -keysize 1024 -validity 365 -keystore app.jks -storepass password -dname "CN=(名字与姓氏), OU=(组织单位名称), O=(组织名称), L=(城市或区域名称), ST=(州或省份名称), C=(单位的两字母国家代码)"
```

* 查看keystore
```bash
keytool -list  -v -keystore app.jks -storepass password
```

* 导入keystore
```bash
keytool-importkeystore -srckeystore other.jks -srcstorepass other -srcalias other -destalias local -destkeystore app.jks -deststorepass password
```

* 导入证书
```bash
keytool -import -alias app -file app.cer -keystore app.jks -storepass password
```

* 导出证书
```bash
keytool -export -alias app -keystore app.jks -file app.cer -storepass password
```

* 查看导出的证书
```bash
keytool -printcert -file app.cer
```

* 删除证书条目
```bash
keytool -delete -alias app -keystore app.jks -storepass password
```

* 修改证书条目口令
```bash
keytool -keypasswd -alias app -keypass app -new holly  -keystore app.jks -storepass password
```

* 修改keystore口令
```bash
keytool -storepasswd -keystore app.jks -storepass password -new holly
```

* 修改keystore中别名为app的信息
```bash
keytool -selfcert -alias app -keystore app.jks -storepass password -dname "cn=holly,ou=holly,o=holly,c=us"
```

* 修改别名
```bash
keytool -changealias -keystore app.jks -storepass password -alias other -destalias app
```