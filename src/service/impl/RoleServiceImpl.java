package service.impl;

import entity.Role;
import mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.RoleService;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void addRolePermission(Long roleID, Long permissionID) {
        roleMapper.addRolePermission(roleID, permissionID);
    }

    @Override
    public void addRole(Role role) {
        roleMapper.addRole(role);
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.addRole(role);
    }

    @Override
    public Role findRoleByID(Long ID) {
        return roleMapper.findRoleByID(ID);
    }

    @Override
    public List<Role> findAllRoles() {
        return roleMapper.findAllRoles();
    }

    @Override
    public void deleteUserRole(Long roleID) {
        roleMapper.findRoleByID(roleID);
    }

    @Override
    public void deleteRole(Long ID) {
        roleMapper.deleteRole(ID);
    }
}
