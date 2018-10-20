package mapper;

import entity.Permission;
import entity.Role;
import entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
