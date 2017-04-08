package cn.cc.ccu.mapper;

import java.util.List;

import cn.cc.ccu.po.Friend;

public interface FriendMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Friend record);

    int insertSelective(Friend record);

    Friend selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Friend record);

    int updateByPrimaryKey(Friend record);
    
    List<Friend> SelectFriendByNumber(String number);
    
    List<Friend> SelectFriendByInsert(Friend record);
    
    int deleteFriend(Friend record);
    
    
    
    
    
    
}