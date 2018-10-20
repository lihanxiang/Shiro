package mapper;

import entity.Permission;
import entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
