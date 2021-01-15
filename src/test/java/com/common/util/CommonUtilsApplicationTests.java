//package com.common.util;
//

import com.common.util.CommonUtilsApplication;
import com.common.util.realm.MyRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @reference https://zhuanlan.zhihu.com/p/54176956
 * @author
 * @date
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CommonUtilsApplication.class)
public class CommonUtilsApplicationTests {

    @Test
    public void contextLoadsTest() {
        System.out.println("11");
    }

    SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    /**
     * 在方法开始前添加一个用户
     */
    @Before
    public void addUser() {
        // simpleAccountRealm.addAccount("kingson", "111111");
        simpleAccountRealm.addAccount("kingson", "111111", "admin", "user");
    }

    /**
     * 流程如下：
     * 首先调用 Subject.login(token) 进行登录，其会自动委托给 Security Manager，调用之前必须通过 SecurityUtils.setSecurityManager() 设置；
     * SecurityManager 负责真正的身份验证逻辑；它会委托给 Authenticator 进行身份验证；
     * Authenticator 才是真正的身份验证者，Shiro API 中核心的身份认证入口点，此处可以自定义插入自己的实现；
     * Authenticator 可能会委托给相应的 AuthenticationStrategy 进行多 Realm 身份验证，默认 ModularRealmAuthenticator 会调用 AuthenticationStrategy 进行多 Realm 身份验证；
     * Authenticator 会把相应的 token 传入 Realm，从 Realm 获取身份验证信息，如果没有返回 / 抛出异常表示身份验证失败了。此处可以配置多个 Realm，将按照相应的顺序及策略进行访问。
     */
    @Test
    public void testAuthentication() {
        // 1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        // 2.主体提交认证请求
        // 设置SecurityManager环境
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        // 获取当前主体
        Subject subject = SecurityUtils.getSubject();

        // 用户信息
        UsernamePasswordToken token = new UsernamePasswordToken("kingson", "111111");
        // 登录
        subject.login(token);

        // 登出
        subject.logout();
        System.out.println("isAuthenticated:" + subject.isAuthenticated());
    }

    /**
     * 流程如下：
     */
    @Test
    public void testAuthortizetion() {
        // 1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        // 2.主体提交认证请求
        // 设置SecurityManager环境
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        // 获取当前主体
        Subject subject = SecurityUtils.getSubject();

        // 用户信息
        UsernamePasswordToken token = new UsernamePasswordToken("kingson", "111111");
        // 登录
        subject.login(token);

        // subject.isAuthenticated()方法返回一个boolean值,用于判断用户是否认证成功
        System.out.println("isAuthenticated:" + subject.isAuthenticated());

        // 判断subject是否具有admin和user两个角色权限,如没有则会报错
        subject.checkRoles("admin", "user");

        // subject.checkRoles("kingson"); // 报错
    }


    @Test
    public void testMyRealmTest() {
        // 实现自己的 Realm 实例
        MyRealm myRealm = new MyRealm();
        // 1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(myRealm);

        // 2.主体提交认证请求
        // 设置SecurityManager环境
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        // 获取当前主体
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("kingson", "111111");
        // 登录
        subject.login(token);

        // subject.isAuthenticated()方法返回一个boolean值,用于判断用户是否认证成功
        System.out.println("isAuthenticated:" + subject.isAuthenticated());
        // 判断subject是否具有admin和user两个角色权限,如没有则会报错
        subject.checkRoles("admin", "user");
        // subject.checkRole("xxx"); // 报错
        // 判断subject是否具有user:add权限
        subject.checkPermission("user:add");
    }

    @Test
    public void testShiroCriptograph() {
        String password = "111111";
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        // 加密次数：2
        int times = 2;
        // 加密算法
        String alogrithmName = "md5";

        String encodePassword = new SimpleHash(alogrithmName, password, salt, times).toString();
        System.out.printf("原始密码是 %s , 盐是： %s, 运算次数是： %d, 运算出来的密文是：%s ", password, salt, times, encodePassword);
    }

}
