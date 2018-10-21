package service;

import entity.Role;
import entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserService {

    void addUser(User user);
    void addUserRole(Long userID, Long roleID);
    void updateUser(User user);

    User findUserByName(String username);
    User findUserByID(Long ID);
    List<Role> findRoles(Long ID);

    void deleteUser(Long ID);
    void deleteUserRole(@Param("userID") Long userID, @Param("userID") Long roleID);
    void deleteAllRoles(Long ID);
}
