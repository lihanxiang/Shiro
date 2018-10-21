package shiro;

import entity.Permission;
import entity.Role;
import entity.User;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import service.UserService;
import util.Encryption;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        //根据用户输入来查找角色
        User user = userService.findUserByName(username);
        if (user == null){
            throw new AuthenticationException("User doesn't exist");
        }
        if (!username.equals(user.getUsername())){
            throw new AuthenticationException("Wrong username");
        }
        return new SimpleAuthenticationInfo(
                user.getUsername(),
                user.getPassword(),
                ByteSource.Util.bytes(user.getSalt()),
                getName()
        );
    }
}
