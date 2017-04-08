package cn.cc.ccu.tools;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cc.ccu.po.User;
import cn.cc.ccu.service.UserService;



public class Number {
	
	
	@Autowired
	UserService userService;
	public String getNumber() {
		List<User> list=userService.getNumber();
		int num=0;
		for(int i=0;i<list.size();i++){
			
			if(Integer.parseInt(list.get(1).getNumber())>num){
				num=Integer.parseInt(list.get(1).getNumber());
			}
		}
		return (num+1)+"";
	}
	
	

}
