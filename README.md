# Shiro 项目实战

## 写在前面的话

这不是 Shiro 的基本用法教程，也不是完整的项目，只是用 SSM 结合 Shiro 进行登录、授权等行为的模块，如果对 Shiro 还不熟悉的同学可以去看一看这个[跟我学Shiro目录贴](http://jinnianshilongnian.iteye.com/blog/2018398)

只准备做两个页面测试用，所以这个项目体现的主要还是后台开发时使用 Shiro 的情况，之前在 [Registration-login-interface2](https://github.com/lihanxiang/Registration-login-interface2) 使用了 Shiro 来做登录，但是那个也太简单了，所以单独拎出来一个项目做 Shiro 的一些基本功能（登录、授权、加密、Session 和单点登录等多个方面），由于网页只是做测试，那么就先删去验证码部分

## 请注意

这个项目更多的是提供一个解决方案（从 0 到 1），如果有需要登录界面的项目，可以在这个方案的基础上进行一些修改，再整合到你自己的项目中，就能很轻易地实现管理了

### 1. 目录结构

总共这么几个文件，就可以搭建一个项目的基本框架了

```
—— config
    |   spring
        |   spring-mvc.xml
        |   spring.mybatis.xml
        |   spring.shiro.xml
    |   db.properties
—— controller
    |   UserController
    |   RoleController
    |   PermissionController
—— entity
    |   User.java
    |   Role.java
    |   Permission.java
—— mapper
    |   PermissionMapper.java
    |   PermissionMapper.xml
    |   RoleMapper.java
    |   RoleMapper.xml
    |   UserMapper.java
    |   UserMapper.xml
—— service
    |   impl
        |   PermissionServiceImpl.java
        |   RoleServiceImpl.java
        |   UserServiceImpl.java
    |   PermissionService.java
    |   RoleService.java
    |   UserService.java
—— shiro
    |   CustomizeRealm
```

### 2. 数据表

总共是 5 张表，用户表是我自己定的，如果你的项目里有用户表，就不要再建了。前三张表是实体类，一个用户拥有一至多个角色，每个角色拥有一至多个权限，后面两张 `user_role` 和 `role_permission` 是用来表示两两之间的联系的：

`user` 表中的 salt 字段是用在以后的加密过程中的

```
CREATE DATABASE shiro;
USE shiro;

CREATE TABLE user (
    id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50),
    password VARCHAR(50),
    salt VARCHAR(50),
    role VARCHAR(50)
) CHARSET = UTF8 ENGINE = InnoDB AUTO_INCREMENT = 1;

CREATE TABLE role(
	id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role VARCHAR(50),
    description VARCHAR(50)
) CHARSET = UTF8 ENGINE = InnoDB AUTO_INCREMENT = 1000;

CREATE TABLE permission(
	id	BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    permission VARCHAR(50),
    description VARCHAR(50)
) CHARSET = UTF8 ENGINE = InnoDB AUTO_INCREMENT = 2000;

CREATE TABLE user_role(
    userID BIGINT,
    roleID BIGINT
) CHARSET = UTF8 ENGINE = InnoDB;

CREATE TABLE role_permission(
    roleID BIGINT(20),
    permissionID BIGINT(20)
) CHARSET = UTF8 ENGINE = InnoDB;
```

至于项目中 entity 包中的实体类，就是上面前三个表对应的，这里就不给出样板代码了，假设用户 ID 从 1 开始，角色 ID 从 1001 开始，权限 ID 从 2001 开始，先插入三项数据：

```
INSERT INTO user VALUES
(1, "admin", "admin", "salt1", "admin"), 
(2, "teacher", "teacher", "salt2", "teacher"), 
(3, "student", "student", "salt3", "student");

INSERT INTO role VALUES
(1001, "admin", "admin"), 
(1002, "teacher", "teacher"), 
(1003, "student", "student");

INSERT INTO permission VALUES
(2001, "create:student", "create student"),
 (2002, "create:teacher", "create teacher"), 
(2003, "create:admin", "create admin"); 

INSERT INTO user_role VALUES
(1, 1001), (2, 1002), (3, 1003);

INSERT INTO role_permission VALUES
(1001, 2001), (1001, 2002), (1001, 2003);
```

创建学生和老师账户的工作就交给管理员进行

### 3. SSM 基本配置

至于 SSM 的基本配置，还是老样子，就那几个文件，这里有一个 [Gist](https://gist.github.com/lihanxiang/a5c91b514e311268094ce56f508c9880)，直接拿下来用就行了

### 4. Shiro 基本配置

首先修改 web.xml，添加过滤器，这个名为 shiroFilter 的过滤器现在其实还没用，Shiro 作为 Spring 的 Bean，是归 Spring 管的，所以这时候要用一个类：`DelegatingFilterProxy`，这个类并不是一个过滤器，而是一个用于将 filter 作为一个 Bean，由 Spring 管理的代理,

```
	<filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>*.action</url-pattern>
    </filter-mapping>
```

这个 filter 对应文件 spring-shiro.xml 中的 shiroFilter 这个 Bean：

```
	<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <property name="securityManager" ref="securityManager"/>
        <!-- 登录页面的地址 -->
        <property name="loginUrl" value="/preLogin.action"/>
        <!-- 如果在未登录的情况下访问受限页面，就跳转至此 -->
        <property name="unauthorizedUrl" value="/unAuthorize.action"/>
        <!-- 过滤链 -->
        <property name="filterChainDefinitions">
            <value>
                <!-- 登录页面不拦截 -->
                /preLogin.action = anon
                <!-- 测试授权页面 -->
                /testAuthorize.action = roles[admin]
                <!-- 登出操作 -->
                /student/logout.action = logout
            </value>
        </property>
    </bean>

    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <property name="realm" ref="customizeRealm"/>
    </bean>

    <bean id="customizeRealm" class="shiro.CustomizeRealm"/>
```

从下往上看，首先是 customizeRealm，Shiro 登录和授权操作从这个文件中获取安全信息（角色、权限等），是一个必经之路，就相当于 DataSource，可以有多个 Realm

然后是 securityManager，顾名思义就是管理员，有关 Shiro 的操作都会与他进行交互，相当于 DispatcherServlet，需要注入自定义的 Realm

最后是 shiroFilter，需要注入 securityManager，然后给出登录地址，未授权跳转地址，成功登录地址等属性，然后是配置过滤链：

```
		<property name="filterChainDefinitions">
            <value>
                <!-- 登录页面不拦截 -->
                /preLogin.action = anon
                /admin/** = roles[admin]
                <!-- 登出操作 -->
                /student/logout.action = logout
            </value>
        </property>
```

需要验证的页面放在后面，不进行拦截的登录界面放在最前，给出常见的四种拦截器以及对应的功能：

| 拦截器名称 |   对应功能  |
|-           |-            |
|anon        |不进行拦截，用在登录页面上，或者一些不需要授权的页面（游客界面等）            |
|authc       |如果没有登录，就跳转到 `loginUrl` 指定的页面                                  |
|user        |比 authc 多了一项功能，如果用户通过 RememberMe 方式登录，也可以访问指定页面，现在做的就先用 user，免得在之后做 RememberMe 的时候再修改     |
|logout      |Shiro 清空缓存并跳转到 `loginUrl` 指定的页面，这种方式就不需要在 Controller 中写对应的登出方法了                                                                        |

**划重点：** 如果是采用注解的方式来进行权限管理，就需要在 spring-mvc.xml 中添加这两行：

```
    <aop:config proxy-target-class="true"/>
    <bean class="org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor">
        <property name="securityManager" ref="securityManager"/>
    </bean>
```

因为采用注解的方式是基于 AOP 的，第一行就是开启 AOP 注解使用的支持，这样子就能在 Controller 中对应的方法上注解 `@RequiresRoles()` 和 `@RequiresPermissions()` 来进行权管理

如果是在 spring-shiro.xml 中用过滤链来进行权限管理就不需要开启 AOP 注解支持

### 5. 基本操作的接口和映射文件

到了写 CRUD 的时候了，只给出基本的方法，在整合时根据自己的用户表再来做相对应的拓展

先给出针对 User 类的，用户的增删改查，还有为用户添加角色的操作：

```
public interface UserMapper {
    //为用户添加角色
    void addUser(User user);
    void addUserRole(@Param("userID") Long userID, @Param("roleID") Long roleID);

    //更新用户信息
    void updateUser(User user);

    User findUserByName(String username);
    User findUserByID(Long ID);
    List<User> findAllUser();
    //查找某个用户所拥有的角色
    List<Role> findRoles(Long ID);
    
    void deleteUser(Long ID);
    //并不是从角色表里删除，只是删除用户所拥有的角色（删除联系）
    void deleteUserRole(@Param("userID") Long userID, @Param("roleID") Long roleID);
    //删除用户拥有的所有角色
    void deleteAllRoles(Long ID);
}
```

对应的 mapper：

```
<mapper namespace="mapper.UserMapper">
    <insert id="addUser" parameterType="entity.User">
      INSERT INTO user(username, password)
      VALUE (#{username}, #{password});
    </insert>
    <insert id="addUserRole" parameterType="Long">
        INSERT INTO user_role (userID, roleID)
        VALUES (#{userID}, #{roleID})
    </insert>

    <update id="updateUser" parameterType="entity.User">
        UPDATE user
        SET username = #{username},role = #{role}
        WHERE id = #{ID}
    </update>

    <select id="findUserByName" parameterType="String" resultType="entity.User">
        SELECT *
        FROM user
        WHERE username = #{username}
    </select>
    <select id="findUserByID" parameterType="Long" resultType="entity.User">
        SELECT *
        FROM user
        WHERE id = #{ID}
    </select>
    <select id="findAllUser" resultType="entity.User">
        SELECT *
        FROM user
    </select>
    <select id="findRoles" parameterType="Long" resultType="entity.Role">
        SELECT r.ID, r.role, r.description
        FROM user u, role r, user_role ur
        WHERE u.id = #{ID}
        AND u.id = ur.userID
        AND r.id = ur.roleID
    </select>

    <delete id="deleteUser" parameterType="Long">
        DELETE
        FROM user
        WHERE id = #{ID}
    </delete>
    <delete id="deleteUserRole" parameterType="Long">
        DELETE
        FROM user_role
        WHERE userID = #{userID}
        AND roleID = #{roleID}
    </delete>
    <delete id="deleteAllRoles" parameterType="Long">
        DELETE
        FROM user_role
        WHERE userID = #{ID}
    </delete>
</mapper>
```

查找权限的过程是先根据 ID 来查找用户，然后查找用户拥有的角色，然后根据查到的角色来查找权限，前两步就是查找用户所拥有角色的过程

然后是针对 Role 类的，增删改查以及为角色添加权限的操作：

```
public interface RoleMapper {

    void addRole(Role role);
    void addRolePermission(@Param("roleID") Long roleID,
                           @Param("permissionID") Long permissionID);

    void updateRole(Role role);

    Role findRoleByID(Long ID);
    List<Role> findAllRoles();
    List<Permission> findPermissions(Long ID);

    //删除某个角色（用户无关）
    void deleteRole(Long ID);
    //删除某个角色拥有的某个权限
    void deleteRolePermission(@Param("roleID")Long roleID,
                              @Param("permissionID")Long permissionID);
    //删除某个角色拥有的全部权限
    void deleteAllPermission(Long ID);
}
```

对应的 mapper

```
<mapper namespace="mapper.RoleMapper">
    <insert id="addRolePermission" parameterType="Long">
        INSERT INTO role_permission (roleID, permissionID)
        SET VALUE (#{roleID}, #{permissionID})
    </insert>

    <insert id="addRole" parameterType="entity.Role">
        INSERT INTO role (role, description)
        VALUE (#{role}, #{description})
    </insert>

    <update id="updateRole" parameterType="entity.Role">
        UPDATE role
        <set>
            <if test="role != null">
                role = #{role}
            </if>
            <if test="description != null">
                description = #{description}
            </if>
        </set>
    </update>

    <select id="findRoleByID" parameterType="Long">
        SELECT *
        FROM role
        WHERE id = #{ID}
    </select>
    <select id="findAllRoles" resultType="entity.Role">
        SELECT *
        FROM role
    </select>
    <select id="findPermissions" parameterType="Long" resultType="entity.Permission">
        SELECT p.id, p.permission, p.description
        FROM user u, role r, permission p, user_role ur, role_permission rp
        WHERE u.id = #{ID}
        AND u.id = ur.userID
        AND r.id = ur.roleID
        AND r.id = rp.roleID
        AND p.id = rp.permissionID
    </select>

    <delete id="deleteRole" parameterType="Long">
        DELETE
        FROM role
        WHERE id = #{ID}
    </delete>
    <delete id="deleteRolePermission" parameterType="Long">
        DELETE
        FROM role_permission rp
        WHERE roleID = #{roleID}
        AND permissionID = #{permissionID}
    </delete>
    <delete id="deleteAllPermission" parameterType="Long">
        DELETE
        FROM role_permission rp
        WHERE roleID = #{ID}
    </delete>
</mapper>
```

最后是针对 Permission 类的，操作会少一些：

```
public interface PermissionMapper {
    void addPermission(Permission permission);

    void updatePermission(Permission permission);

    Permission findPermissionByID(Long ID);
    List<Permission> findAllPermissions();

    void deletePermission(Long ID);
}
```

对应 mapper：

```
<mapper namespace="mapper.PermissionMapper">
    <insert id="addPermission" parameterType="entity.Permission">
        INSERT INTO permission (permission, description)
        VALUE (#{permission}, #{description})
    </insert>

    <update id="updatePermission" parameterType="entity.Permission">
        UPDATE permission
        <set>
            <if test="premission != null">
                permission = #{premission}
            </if>
            <if test="description != null">
                description = #{description}
            </if>
        </set>
    </update>

    <select id="findPermissionByID" parameterType="Long">
        SELECT *
        FROM premission
        WHERE ID = #{ID}
    </select>
    <select id="findAllPermissions" parameterType="entity.Permission">
        SELECT *
        FROM premission
    </select>

    <delete id="deletePermission" parameterType="Long">
        DELETE
        FROM premission
        WHERE ID = #{ID}
    </delete>
</mapper>
```

### 6. service

service 里有着与映射器接口相同的方法，在 impl 包中对接口的实现也都是调用映射器接口的办法（老生常谈），这里就不说明了

### 7. Realm

这里我们采用自定义的 Realm：

```
public class CustomizeRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        /*
        //获取用户名
        String username = (String)principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> role = new HashSet<>();
        //根据 ID 获取用户所拥有的角色
        List<Role> roles = userService.findRoles(userService.findUserByName(username).getID());
        for (Role r :
                roles) {
            role.add(r.getRole());
        }
        authorizationInfo.setRoles(role);
        return authorizationInfo;*/
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String)authenticationToken.getPrincipal();
        String password = new String((char[])authenticationToken.getCredentials());
        //根据用户输入来查找角色
        User user = userService.findUserByName(username);
        if (user == null){
            throw new AuthenticationException("User doesn't exist");
        }
        if (!username.equals(user.getUsername())){
            throw new AuthenticationException("Wrong username");
        }
        if (!password.equals(user.getPassword())){
            throw new AuthenticationException("Wrong password");
        }
        return new SimpleAuthenticationInfo(username, password, getName());
    }
}
```

对于登录：这里将传入的 token 中获取用户名，使用 service 层的方法去数据库中查找对应记录，然后将记录中的密码字段与传入 token 的密码做对比，得出结果

对于授权：这里被注释掉的原因是，我采用的验证授权的方式是在 spring-shiro.xml 中配置过滤链来检测

对于另外这三种情况：

1. 在调用 `subject.hasRole()` 或 `subject.isPermitted()` 时

2. Controller 中方法注解 `@RequiresRoles()` 时

3. 在 jsp 中添加 shiro 标签 `<shiro:hasPermission name = "xxx"></shiro:hasPermission>` 时

则会进入 `doGetAuthorizationInfo(PrincipalCollection principalCollection)`，获取用户名，然后在数据库中查找其拥有的角色，采用 Set 的形式，添加至 `authorizationInfo`

### 8. controller

做登录的操作：

```
@RequestMapping("/login")
    public ModelAndView login(User user){
        ModelAndView modelAndView = new ModelAndView("main");
        //得到 Subject
        Subject subject = SecurityUtils.getSubject();
        //获取 token，用于验证
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try {
            //尝试登录
            subject.login(usernamePasswordToken);
            modelAndView.addObject("username", user.getUsername());
        } catch (AuthenticationException e){
            modelAndView = new ModelAndView("login");
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
    }
```

登录的流程：

1. `Subject subject = SecurityUtils.getSubject();` 通过 SecurityUtils 得到 Subject 实例

2. `UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());` ，获取用于身份验证的 Token，如用户名/密码

3. `subject.login(usernamePasswordToken);` 委托给 `SecurityManager` 进行登录，`SecurityManager` 又 委托给 `Authenticator` 进行验证（老母猪戴胸罩，你是一套又一套），`Authenticator` 将 2 中获取的 Token 传入 Realm，如果返回 null 或者抛出异常就代表登录失败

接下来在 Controller 中写两个方法：

```
    //如果没有角色 admin，无法访问这个页面
    @RequestMapping("testAuthorize")
    public String testAuthorize(){
        return "testAuthorize";
    }

    // URL 中 .jsp 会被拦截，所以这里用 unAuthorize.action 来作为过滤链中的跳转动作
    @RequestMapping("unAuthorize")
    public String unAuthorize(){
        return "unAuthorize";
    }
```

接下来运行一下做个测试：

如果在未登录的情况访问 `http://localhost:8080/testAuthorize.action`，则会要求你先登录，那我们又 student/student 作为学生登录一下，然后访问 `http://localhost:8080/testAuthorize.action`，就会跳转到 `http://localhost:8080/unAuthorize.action`：

![](https://upload-images.jianshu.io/upload_images/3426615-3eaddf7e60598c10.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这样子，登录与授权的基本模型就做完了，接下来是密码加密等操作，即将上传