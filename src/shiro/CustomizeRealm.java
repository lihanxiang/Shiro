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
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomizeRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String)principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> role = new HashSet<>();
        List<Role> roles = userService.findRoles(username);
        for (Role r :
                roles) {
            role.add(r.getRole());
        }
        authorizationInfo.setRoles(role);
        Set<String> permission = new HashSet<>();
        List<Permission> permissions = userService.findPermissions(username);
        for (Permission p :
                permissions) {
            permission.add(p.getPermission());
        }
        authorizationInfo.setStringPermissions(permission);
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String)authenticationToken.getPrincipal();
        String password = new String((char[])authenticationToken.getCredentials());
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
