#### 2017-10-19	*Version:2.5.0*

######新增
1. 新增Browser Agent混合嵌码功能

    该版本的Java Agent支持Browser Agent混合嵌码，即由Web Server（如：Nginx、Apache）将听云Browser Agent 嵌入到响应页面中，再由Server Agent将Application Server响应的性能指标通过Cookie方式嵌入到响应页面中，实现全栈溯源；具体使用参见：<a href="http://doc.tingyun.com/browser/html/tanzhenanzhuang%EF%BC%88yemian%EF%BC%89.html"> Browser混合嵌码</a>
    
2. 新增日志追溯功能
	
	日志追溯功能可以实现在应用的日志文件中打印听云全栈溯源相关数据：如Request GUID、Application ID等。支持以下日志组件：
	* log4j 1.2+
	* logback 1.2+
	
	该功能默认关闭，通以下步骤启用：````
	
	* 在听云Server控制台中启用日志追溯功能
	* 修改tingyun.properties,启用日志追溯相关plugin:
		<pre><code>nbs.class_transformer.tingyun-log4j-plugin-2.0.0.enabled=true
		nbs.class_transformer.tingyun-log4j-plugin-2.3.enabled=true
		nbs.class_transformer.tingyun-log4j-plugin-1.2.enabled=true
		nbs.class_transformer.tingyun-logback-plugin-1.2.enabled=true
		</code></pre>
		注：请根据具体使用的日志组件，启用对应的听云plugin
	* 修改log4j.properties或log4j.xml，配置layout:
		<pre><code>log4j.appender.Console.layout.ConversionPattern=%d [%t] %-5p [%c] <mark>[%A][%R]</mark>- %m%n
		// [%R] 表示Request GUID
		// [%A] 表示Application ID
		</code></pre>
		或修改logback.xml，配置<pattern>
		<pre><code>&lt;pattern>
			%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n<mark>[%A][%R]</mark>
		&lt;/pattern>
		// [%R] 表示Request GUID
		// [%A] 表示Application ID
		</code></pre>
	* 重启Application Server
3. 新增超时机制
	* Segment timeout
	
		Segment表示监控的方法，或者部分代码片段
	
		~~~~
		# Segment(Traced method or Code snippet)超时时间, 如执行时间（Duration）超过该值，则结
		# 束该Segment，以当前时间作为Segment结束时间，计算性能指标，该Segment的metric name会
		# 以“Truncate”做为前缀.
		# defualt:600 (单位：秒)
		# nbs.segment_timeout=600
		~~~~
	* Async request timeout
	
		~~~~
		# 异步调用超时时间, 如执异步调用的时间超过该值，则不记录异步调用性能
		# defualt:180 (单位：秒)
		# nbs.async_timeout=180
		~~~~
4. 支持基于JMS 1.1协议的MQ组件性能采集（如：ActiveMQ），包括Producer和Consumer，以及Message Tracing。 
5. 支持MongoDB 2.7.3 
6. 支持Cxf 2.2.5
7. 支持OkHttpClient 2.4 
8. 支持org.asynchttpclient 2.0.32 
7. 支持c3p0 0.8.4.5 
8. 支持GRPC 1.6.1 
9. 支持Jboss 4.0.2
10. 支持ojdbc 7-12.1.0.2.0

######改进
1. 优化WebLogic EJB应用的性能采集
2. 优化WebSphere EJB应用的性能采集

######修复
1. 修复Jetty6.1.9增加探针后，启动报java.lang.NoClassDefFoundError的bug
2. 修复Jboss 7增加探针后，启动报NullPointerException的bug
3. 修复部署在GlassFish 4.X容器中的应用，调用的外部服务，服务端响应为jsp页面时，无法实现全站溯源的bug
4. 修复PostgreSQL无法识别数据库名称的bug
5. 修复PostgreSQL 9以下版本驱动无法获取执行计划数据的bug
6. 修复ning.asynchttpclient-plugin segment超时后报错NullPointerException的bug
7. 修复部署在Resin 4.X容器中的应用，使用Servlet 3.0 异步时出现NullPointerException的bug
8. 修复当开启资源保护时，某种场景下出现业务不可用的bug

#### 2017-07-13	*Version:2.4.1.1*

######修复
1. 修复堆内存保护后引起的空指针问题

	
#### 2017-07-13	*Version:2.4.1*
   
######修复
1. 修复HttpClient 4.X HttpParams兼容性处理的Bug
2. 修复Dubbo Consumer使用Future方式时，在某些场景下，触发NullPointerException的Bug
3. 修复在Weblogic上使用HttpURLConnection 作为接口调用，当Agent处理跨应用追踪导致NullPointerException的Bug
4. 修复Resin 4使用 com/caucho/loader/DynamicClassLoader，增加探针后出现VerifyError的Bug
5. 修复Tomcat 7、 Tomcat 8 & Jetty 9.2.x 上部署Servlet 3.0 应用，增加探针后导致IllegalStateException的Bug
6. 修复当用户使用servletRequest.login将用户信息存储在Principal，当Principal的name 字段没有赋值，导致NullPointerException的Bug
7. 修复tingyun-weblogic-ejb-plugin的几个兼容性问题
8. 修复Axis 1.x版本增加探针后，某些场景下出现NullPointerException的Bug

    
#### 2017-04-25	*Version:2.4.0*

######改进
1. 增加分位值数据统计

    提供应用及Web应用过程级别性能数据99，95，75，50（可通过控制台自定义）分位的分位值，为性能问题诊断提供全方位数据基础。

2. 增加RabbitMQ性能数据采集

    支持RabbitMQ 3.5.0+ 版本的性能采集，包括Producer和Consumer，会创建不同的Message Transaction，用来统计message sent、message received的response time、message count、message size and message wait time。
    支持Message的全栈溯源，通过消息拓扑和跨应用分析实现 message transaction trace。
    通过控制台应用设置，可以开启或关闭该功能。

#### 2017-03-15	*Version:2.3.2*

######修复
1、修复Tomcat 7 及以上版本在开启Browser自动嵌码后可能引起Heap内存占用过大的Bug（强烈建议更新到该版本）
2、修复其他可能引起性能问题的Bug

#### 2017-02-16	*Version:2.3.1*

######改进

1. Database & NoSQL 实例信息识别

    实例信息包括：服务地址（IP或域名）、端口和数据库名称。在拓扑图中组件标识增加实例信息，慢应用追踪、慢SQL追踪中会展示实例信息。支持以下驱动或组件：
    * 大部分JDBC Drivers，包括C3p0, Druid等 （不包括Derby等）
    * Jedis Redis driver 1.5.2 ~ 2.9
    * Mongodb 2.6.3+
    * SpyMemcached 2.10.0+ （不包括 getBulk调用）
    * Whalin Memcached 3.0.0+（不包括 flushAll调用）
    * XMemcached 1.4.3+
2. 新增应用过载保护断路器
    * 从V2.3.1开始，Java Agent 增加断路器用来保护不会因过多的inject导致应用资源消耗过载。当内存及CPU消耗过载时，断路器启动，Agent处于静默状态，除心跳检查外，不在嵌码、创建Action、tracer。
    * 通过在tingyun.properties中配置如下参数，控制断路器：
    <pre><code>
    nbs.circuit_breaker.enabled=true // 是否开启过载保护，默认true
    nbs.circuit_breaker.heap_threshold=20 // 内存阈值（单位：%）
    nbs.circuit_breaker.gc_cpu_threshold=10 // 垃圾回收消耗CPU时间阈值（单位：%）</code></pre>
    当剩余内存的百分比小于<code>nbs.circuit_breaker.heap_threshold</code>，而且垃圾回收消耗的cpu时间大于<code>nbs.circuit_breaker.gc_cpu_threshold</code>时，探针不在创建Action，新装载的classes不在嵌码。
    *该功能可能导致控制台数据缺失。
3. 增加对Axis 1.* 版本的支持
4. 增加对GRPC 1.0.1+ 版本的支持   
5. 热点代码自适应嵌码(Beta 2)
	* 更稳定、资源消耗更少

######修复
1. 修复使用URLConnection时，External指标重复计算的bug
2. 修复Glassfish 4.X版本无法全栈溯源的bug
3. 修复Tomcat 7 以上版本导致Web Action 里<code>ServletRequestListener.requestInitialized()</code>方法消耗过多时间的bug

#### 2016-08-31	*Version:2.3.0*

######改进

1. JDBC 性能数据采集重构,支持更多的JDBC drivers:
    * DB2 9.1 - 10.x
    * Derby 10.6.1.0 - 10.x
    * Generic JDBC Drivers
    * H2 1.0.x - 1.4.x
    * HSQL 1.7.2.2 - 2.x
    * Informix JDBC Drivers
    * jTDS 1.2 - 1.3.x
    * MariaDB 1.1.7 - 1.3.x
    * Microsoft SQLServer 2.0 - 4.2
    * MySQL 5.1.4 - 6.0.2+
    * Oracle ojdbc14, ojdbc5, ojdbc6, ojdbc7
    * PostgreSQL 8.0+ - 9.4-1206 (仅支持JDBC4和JDBC41, 执行计划仅支持PostgreSQL Server 9.0以上版本 )
    * Sybase (jConnect) JDBC 3 driver
    * c3p0 0.9+
    * druid
    
**备注** 数据库指标中包含数据库厂商, 如 Database MySQL/SS_User/SELECT, 无法识别的数据库厂商使用JDBC代替, 如Database JDBC/SS_User/SELECT

2. Dubbo 2.0+ 跨应分析
    * Consume以Service Bean命名应用过程, 如:DubboConsumer/com.alibaba.dubbo.demo.DemoService/sayHello
    * Consume向Provider发出的请求, 会被识别为外部服务, 当provider处理缓慢时可以跨应用分析
    * Provider以Service Bean 实现类命名应用过程, 如:DubboProvider/com.alibaba.dubbo.demo.DemoService/sayHello
3. JMS 1.1 和 RabbitMQ 3.5.0+(Beta)
    * Performance, 采集Produce和Consumer两端的消息处理性能数据, 包括: RemoteAddress、Destination、Queue name or Topic name
    * Message transaction, 在慢应用分析里可以追踪一个消息从生产到消费的完整链路
    * Message bytes, 消息的流量信息,单位byte
    * Message wait, 消息等待处理的时间, 指produce产生该消息的时间到Consume获取到该消息的时间,单位毫秒
4. 热点代码自适应嵌码(Beta)
    * 根据调用关系自动学习业务代码的热点方法, 经过8小时的深度分析后, 这些方法将会被听云监控,采集该方法的性能及错误信息
    * 可以在宝贝的性能分解表格里看到以HotSpot分类的性能指标
    * 在Slow trace中, 亦可以看到该方法的调用链及性能数据
    * 重要: 该功能会对CPU、Memory有一定消耗, 请权衡使用
    * 该功能默认关闭, 可通过听云控制台启用 
5. Mule ESB 3.4 - 3.6
    * Server端识别HTTP Transport为Web应用过程 
    * Mule ESB Server实例会被识别为一个Container, 采集内存、CPU及JVM相关指标
    * JMS, 消息的发送与接收
    * Transaction, 跨应用分析  
6. 自定义实例名称
    * 之前识别实例通过host + port方式, 该版本探针可以通过在tingyun.properties中配置nbs.instance.name=****, 来指定实例名称
7. 外部服务错误
    * 支持Http协议外部服务错误的采集
    * 错误次数、错误类型等指标
    * 错误Trace
  
######修复
1. 修复极端情况下探针停止上传数据的bug
2. 修复Browser嵌码可能导致页面被截断的bug
3. 修复Jetty8.0+ 跨应用分析的功能
4. 修复resin4.0+ 访问无响应的bug

######已知问题
1. 某种情况下Glassfish 无法跨应用分析
2. 不支持单个字段的SQL语句混淆, 配置"混淆SQL字段"后, SQL为全部混淆

####2016-04-07	*Version:2.2.0*

######改进
1. 支持Play 2.4
2. 支持EJB 2.x/3.x
3. 支持Netty 4.0+
4. 支持AsyncHttpClient 1.6+
######修复
1. 修复NodeJs Agent到Glassfish4拓扑展示

####2016-02-23	*Version:2.1.3*

######改进
1. 跨应用分析的功能重构，支持与Network产品、App产品、Browser产品跨应用分析的功能。
2. 支持对Play 1.2.6的错误和异常信息的采集

####2016-01-28	*Version:2.1.2*

######改进
1. 用户请求参数大小由256B更改为4KB

######修复
1. 修复容器Jboss、WebSphere自身的连接池无法获取sql参数的bug

####2016-01-21	*Version:2.1.1*

######改进
1. 支持报表端配置探针自定义方法监控功能

######修复
1. 修复Struts2 Response结果为json时无法获取请求参数的bug

####2016-01-05	*Version:2.1.0*

######改进
1. 全面支持WebService(包括SOAP和REST)性能数据采集、应用过程命名、跨应用分析等
	```
	服务端（EndPoint）: 在Web应用过程中可以看到以WebService和RestWebService开头的应用过程，其Metric Name 命名规则为(Rest)Webservice/WebserviceName.OperatorName
	客户端（Client）: 在外部服务的HTTP中会看到WebService调用的URI，其命名规则为http(s)://domain:port/WebServiceName.OperatorName
	```
2. 支持Apache Axis2 1.5+
3. 支持Apache CXF 2.7.0+
4. 支持Spring WS 2.0.0+
5. 支持Java JAX-WS
6. 支持Java JAX-RS (REST)
7. 支持Jersey 2.0+ (REST)
8. 支持Resteasy 2.2+ (REST)

######修复
1. 修复Jboss 无法获取Session数据的bug

####2015-12-10	*Version:2.0.1*

######改进
1. 支持与Network产品跨应用分析的功能

####2015-11-17	*Version:2.0.0*

######改进
1. 优化探针启动速度
2. 支持Weblogic 10、Weblogic 12容器Browser自动代码注入
3. 支持WebSphere 7、WebSphere 8容器Browser自动代码注入
4. 支持WildFly 8容器Browser自动代码注入
5. 增加对Jetty 9.3的支持
6. 增加对Spring AOP性能数据的采集
7. 通过@RequestMapping自动命名应用过程
	```
	通常情况下，使用Spring Annotation @RequestMapping命名Web应用过程比直接使用ControllerName + MethodName更有意义。
	可以通过设置tingyun.properties文件中“nbs.inspect.spring_annotations.enabled=false”禁用该特性。
	```
8. 增加对Servlet 3.0的支持
9. Dubbo 支持跨应用分析
10. 增加对MongoDB 错误的采集
11. 增加对Jedis错误的采集
12. NoSQL operator不再归类，直接使用原始command name
13. 自定义嵌码扩展支持更灵活的配置，如：接口、基类、返回值等
14. 提供自定义嵌码扩展API
	```
	可以通过在业务方法标注@Trace，实现对该方法的性能采集。需要将com.tingyun:tingyun-agent-api:2.0.0加到classpath里。
	```
15. 增加死锁检查
	```
    增加Deadlock探测线程，用于发现线程是否发生死锁。当发现线程死锁后，探针会上报一个Deadlock的错误，包含线程信息、堆栈等信息。
    因该探测线程会消耗一下资源，默认情况下该功能关闭，如果您的应用不是密集计算的多线程应用，可以通过设置tingyun.properties文件中“nbs.deadlock_detector.enabled=true"启用该功能。
    ```

######修复
1. 修复Jetty8.1.5获取容器信息的bug
2. 修复使用Spring-data-redis无法采集redis数据的bug
3. 修复dubbo-admin运行失败的bug
4. 修复开启应用自动命名后，关闭Web应用过程自动命名失效的bug

####2015-09-22	*Version:1.3.0*

######改进
1. 支持Browser自动嵌入页面性能采集代码
2. 支持Browser手动嵌入页面性能采集代码
3. 支持Dubbo跨应用分析

####2015-09-01	*Version:1.2.4*

######改进
1. 支持Dubbo Provider性能采集
2. 支持Thrift Server性能采集

######修复
1. 修复了derby数据库拓扑图上无法显示的bug

####2015-08-07	*Version:1.2.3*

######改进
1. 增加数据库厂商的采集
2. 增加应用拓扑数据采集

######修复
1. 修复跨应用分析无法获取数据库执行时间的bug

####2015-06-20	*Version:1.2.2*

######改进
1. 跨应用分析

	```
	当使用HttpURLConnection或者Apache HttpClient(3.x 、4.x)访问另外一个服务时，通过在Request Header中增加“X-Tingyun-Id”实现跨应用的分析。
	可以通过设置tingyun.properties文件中“nbs.transaction_tracer.enabled=false”禁用该特性。
	```
2. 支持Thrift 0.8.0+性能采集
3. 支持Dubbo consumer性能采集

######修复
1. 修复特定编码下resin应用乱码的bug

####2015-06-17 *Version:1.2.1*

######修复
1. 修复当设置`nbs.agent_log_file_count=1`时，日志文件无法滚动

####2015-06-10	*Version:1.2.0*

######改进
1. 支持Play2.3.x性能采集
2. 支持Scala
3. 支持AKKa
4. 支持Async Task

######修复
1. 修复MetricName转义时多产生%2F
		
		
####2015-05-12	*Version:1.1.0*
######改进
1. 外部服务URL聚合
	```
	http://www.example.com/vendor/1234/order --> http://www.example.com/vendor/*/order
	```

####2015-05-05	*Version:1.0.9*
######改进
1. 支持通过URL参数命名Web Action
2. 支持通过URL参数命名外部服务

######修复
1. 修复对HttpClient 4的支持
2. 修复对CommonsHttp 的支持
3. 修改特殊情况下探针无法上传数据的bug
		
####2015-04-12	*Version:1.0.8*
######改进
1. NoSQL性能分类
2. 支持自定义监测
	```
	在tingyun-agent-java.jar同级目录下创建extensions/xx.xml文件，可自定义监控的方法，探针在启动时读取改文件，实现对性能的采集。
	```	

####2015-03-06 *Version:1.0.7*
######改进
1. Memcached增加分类

####2015-02-13 *Version:1.0.6*
######改进
1. 使用HttpClient传输数据
2. 增加对redis数据的汇总

######修复
1. 修复redis采集不到get方法性能的bug

####2015-01-05 *Version:1.0.5*
######改进
1. 使用AgentProxy.agentHandler 存储 invokeHandler

######修复
1. 修复只采集一级tracer的bug
2. 修复无法采集CPU的Bug

####2015-01-03 *Version:1.0.4*
######修复
1. 修复无法采集stacktrace的bug

####2014-12-30 *Version:1.0.3*
######改进
1. 报表端禁用

####2014-11-27 *Version:1.0.2*
######改进
1. 默认不采集静态资源的性能

####2014-11-13 *Version:1.0.1*
######改进
1. 增加Thread profile
2. 自动安装(支持JBoss,GlassFish,Jetty和Tomcat)
3. 默认关闭auto app naming

####2014-09-26 *Version:1.0.0*
######改进
1. Application request controller and dispatch activity
2. Database Operator
3. External web services calls
4. View resolve
5. Uncaught Exceptions and counts
6. Process Memory and CPU usage
