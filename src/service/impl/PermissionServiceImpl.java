package service.impl;

import entity.Permission;
import mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.PermissionService;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService{

    private final PermissionMapper permissionMapper;

    @Autowired
    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public void addPermission(Permission permission) {
        permissionMapper.addPermission(permission);
    }

    @Override
    public void updatePermission(Permission permission) {
        permissionMapper.updatePermission(permission);
    }

    @Override
    public Permission findPermissionByID(Long ID) {
        return permissionMapper.findPermissionByID(ID);
    }

    @Override
    public List<Permission> findAllPermissions() {
        return permissionMapper.findAllPermissions();
    }

    @Override
    public void deletePermission(Long ID) {
        permissionMapper.deletePermission(ID);
    }

    @Override
    public void deleteRolePermission(Long permissionID) {
        deletePermission(permissionID);
    }
}
