package cn.cc.ccu.mapper;

import java.util.List;

import cn.cc.ccu.po.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    
    List<User> selectByLogin(User user);  
    
    User selectUserByNumber(String number);
    
    
    User selectEmailToUsername(String username);
    
    
    
    
    List<User> selectByAll();
}