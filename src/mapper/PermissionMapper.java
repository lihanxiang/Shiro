package mapper;

import entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PermissionMapper {
    void addPermission(Permission permission);

    void updatePermission(Permission permission);

    Permission findPermissionByID(Long ID);
    List<Permission> findAllPermissions();

    void deletePermission(Long ID);
}
