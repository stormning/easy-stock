package com.slyak.es.service;

import com.slyak.es.domain.User;
import com.slyak.es.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Transactional
    public User register(UserRegistrationRequest userRequest) {
        // 检查用户名是否已存在
        if (userRepository.existsByName(userRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户实体对象并设置属性
        User user = new User();
        user.setName(userRequest.getUsername());
        user.setPwd(userRequest.getPassword());
        // 在这里可以设置其他的属性，比如创建时间等

        // 保存用户对象到数据库
        return userRepository.save(user);
    }
}

