package yingdg.exercise.jfinalwebapp.config;

import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.cache.EhCache;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import yingdg.exercise.jfinalwebapp.config.interceptor.GlobalInterceptor;
import yingdg.exercise.jfinalwebapp.controller.HelloController;
import yingdg.exercise.jfinalwebapp.controller.IndexController;
import yingdg.exercise.jfinalwebapp.controller.UserController;
import yingdg.exercise.jfinalwebapp.model.User;

/**
 * Web环境配置
 */
public class JFinalConfig extends com.jfinal.config.JFinalConfig {

    /*
     配置JFinal常量值
      */
    @Override
    public void configConstant(Constants cons) {
        cons.setDevMode(true); // 控制台显示请求信息
        cons.setEncoding("utf-8");
        cons.setViewType(ViewType.JSP);
        cons.setUrlParaSeparator("-"); // 默认以"-"分隔

        /*
        PropKit工具类用来操作外部配置文件 ,
        PropKit 可以方便地在系统任意处使用
         */
        // 第一次使用use加载的配置将成为主配置，可以通过PropKit.get(...)直接取值
        // PropKit.use("config.txt");
    }

    /*
     配置访问路由（Servlet）
     规则：controllerKey（可包含"/"）/method/v0-v1(参数，getPara()取值)
     支持Rest风格Url
      */
    @Override
    public void configRoute(Routes routes) {
        routes.add("/hello", HelloController.class);

        // 自定义Route
        routes.add(new Routes() {
            @Override
            public void config() {
                add("/", IndexController.class);// Rest风格Url，只写controllerKey
                add("/user", UserController.class);
            }
        });
    }

    /*
     配置插件，如C3P0数据源，ActiveRecord等
      */
    @Override
    public void configPlugin(Plugins plugins) {
        loadPropertyFile("jdbc.properties");
        // 默认mysql数据源
        C3p0Plugin c3p0Plugin = new C3p0Plugin(
                getProperty("jdbc.url"),
                getProperty("jdbc.username"),
                getProperty("jdbc.password"),
                getProperty("jdbc.driverClassName"));
        plugins.add(c3p0Plugin);

        /*
        最先创建的 ActiveRecrodPlugin 实例将会成为主数据源，可以省略 configName，实例中的配置将默认成为主配置。
        此外还可以通过设置 configName 为 DbKit .MAIN_CONFIG_NAME（"main"） 常量来设置主配置。
         */
        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(c3p0Plugin); // 默认指定数据源的名称为main
        // activeRecordPlugin.setDialect(new OracleDialect()); // 配置Oracle方言
        // 配置属性名(字段名)大小写不敏感容器工厂（一般用于Oracle）
        // activeRecordPlugin.setContainerFactory(new CaseInsensitiveContainerFactory());
        // 配置事务
        // activeRecordPlugin.setTransactionLevel(8);
        // 配置缓存
        activeRecordPlugin.setCache(new EhCache());
        // 开启数据库中的表与Model的自动映射
        activeRecordPlugin.addMapping("User", User.class);
        // activeRecordPlugin.addMapping("User", "id", User.class); // 主键名默认id，但可以手动指定

        plugins.add(activeRecordPlugin);

        // redis
        // 非第一次使用use加载的配置，需要通过每次使用use来指定配置文件名再来取值
//        String redisHost = PropKit.use("redis_config.txt").get("host");
//        int redisPort = PropKit.use("redis_config.txt").getInt("port");
//        RedisPlugin rp = new RedisPlugin("Redis", redisHost, redisPort);
//        plugins.add(rp);

        // Druid
        // 非第一次使用 use加载的配置，也可以先得到一个Prop对象，再通过该对象来获取值
//        Prop p = PropKit.use("db_config.txt");
//        DruidPlugin dp = new DruidPlugin(p.get("jdbcUrl"), p.get("user"), p.get("password"));
//        plugins.add(dp);

        // 配置 JFinal 集成的缓存插件，需要ehcache包
        plugins.add(new EhCachePlugin());
    }

    /*
     配置拦截器，粒度分为Global，Class，Method三层
      */
    @Override
    public void configInterceptor(Interceptors interceptors) {
        // 添加全局自定义拦截器
        interceptors.add(new GlobalInterceptor());
    }

    /*
     配置处理器，
     可以接管所有 web web请求，并对应用拥有完全的控制权，
     可以很方便地实现更高层的功能性扩展
      */
    @Override
    public void configHandler(Handlers handlers) {
        // 添加自定义处理器
        // handlers.add(new ResourceHandler());
    }

    /*
    系统启动完成后调用
     */
    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
    }

    /*
    系统关闭前调用
     */
    @Override
    public void beforeJFinalStop() {
        super.beforeJFinalStop();
    }

}