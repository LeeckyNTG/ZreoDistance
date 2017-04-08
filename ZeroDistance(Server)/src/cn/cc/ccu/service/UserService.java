package cn.cc.ccu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cc.ccu.mapper.FriendMapper;
import cn.cc.ccu.mapper.UserMapper;
import cn.cc.ccu.po.User;



public class UserService {

	

	@Autowired	
	UserMapper userMapper;
	
	public User login(User user) {
		
		List<User> list=userMapper.selectByLogin(user);
		
		if(list.size()==1){
			return list.get(0);
		}else{
			return null;
		}
		
	}
	
	public List<User> getNumber() {
		
		List<User> list=userMapper.selectByAll();
		
		return list;
	}
	public int insert(User user){	
		
		
		int result=userMapper.insertSelective(user);
		return result;
	}
	
	public int selectUserByNumber(String number) {
		
		
		User user=userMapper.selectUserByNumber(number);	
		
		if(user!=null)
		
			return 1;
		else
			return 0;
	}
	
	public String getEmailToUsername(String username) {
		
		
		User name=userMapper.selectEmailToUsername(username);	
		
		
		return name.getEmail();
	}
	
	
}
