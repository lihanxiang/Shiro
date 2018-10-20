package service;

import entity.Permission;

import java.util.List;

public interface PermissionService {
    void addPermission(Permission permission);

    void updatePermission(Permission permission);

    Permission findPermissionByID(Long ID);
    List<Permission> findAllPermissions();

    void deletePermission(Long ID);
    void deleteRolePermission(Long permissionID);
}
