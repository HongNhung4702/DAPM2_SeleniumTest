package service;

import dao.UserDao;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public boolean login(String username, String password) {
        User user = userDao.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public enum RegisterResult {
        SUCCESS, USER_EXISTS, EMAIL_EXISTS
    }

    public RegisterResult register(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            return RegisterResult.USER_EXISTS;
        }
        if (userDao.findByEmail(user.getEmail()) != null) {
            return RegisterResult.EMAIL_EXISTS;
        }
        userDao.save(user);
        return RegisterResult.SUCCESS;
    }
}