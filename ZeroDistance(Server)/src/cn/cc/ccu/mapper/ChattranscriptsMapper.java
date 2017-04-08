package cn.cc.ccu.mapper;

import java.util.List;

import cn.cc.ccu.po.Chattranscripts;

public interface ChattranscriptsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Chattranscripts record);

    int insertSelective(Chattranscripts record);

    Chattranscripts selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Chattranscripts record);

    int updateByPrimaryKey(Chattranscripts record);
    
    List<Chattranscripts> selectChattranscriptsByUsernumber(Chattranscripts record);
    
    
}