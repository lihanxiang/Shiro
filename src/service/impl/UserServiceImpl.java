package service.impl;

import entity.Role;
import entity.User;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.UserService;
import util.Encryption;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final Encryption encryption;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, Encryption encryption) {
        this.userMapper = userMapper;
        this.encryption = encryption;
    }

    @Override
    public void addUser(User user) {
        encryption.encryptPassword(user);
        userMapper.addUser(user);
    }

    @Override
    public void addUserRole(Long userID, Long roleID) {
        userMapper.addUserRole(userID, roleID);
    }

    @Override
    public void updateUser(User user) {
        encryption.encryptPassword(user);
        userMapper.updateUser(user);
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.findUserByName(username);
    }

    @Override
    public User findUserByID(Long ID) {
        return userMapper.findUserByID(ID);
    }

    @Override
    public List<Role> findRoles(Long ID) {
        return userMapper.findRoles(ID);
    }

    @Override
    public void deleteUser(Long ID) {
        userMapper.deleteUser(ID);
    }

    @Override
    public void deleteUserRole(Long userID, Long roleID) {
        userMapper.deleteUserRole(userID, roleID);
    }

    @Override
    public void deleteAllRoles(Long ID) {
        userMapper.deleteAllRoles(ID);
    }
}
