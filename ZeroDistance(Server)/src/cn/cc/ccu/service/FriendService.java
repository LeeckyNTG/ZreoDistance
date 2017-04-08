package cn.cc.ccu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cc.ccu.mapper.FriendMapper;
import cn.cc.ccu.po.Friend;

public class FriendService {
	
	@Autowired
	
	FriendMapper friendMapper;
	
	
	public int addFriend(Friend friend) {
		
		List<Friend> list=friendMapper.SelectFriendByInsert(friend);
		
		int result=1;
		
		if(list.size()==0){
		
			result=friendMapper.insertSelective(friend);
		}
		
		return result;
	}
	
	
	public List<Friend> SelectFriendByNumber(String number) {
		
		List<Friend> list=friendMapper.SelectFriendByNumber(number);
			
		return list;
		
	}
	
	
	public int deleteFriend(Friend friend) {
		
		int result=friendMapper.deleteFriend(friend);
		
		return result;
		
	}

}
