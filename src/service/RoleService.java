package service;

import entity.Role;

import java.util.List;

public interface RoleService {
    void addRolePermission(Long roleID, Long permissionID);
    void addRole(Role role);

    void updateRole(Role role);

    Role findRoleByID(Long ID);
    List<Role> findAllRoles();

    //删除与用户相关的角色
    void deleteUserRole(Long roleID);
    //删除某个角色（用户无关）
    void deleteRole(Long ID);

}
