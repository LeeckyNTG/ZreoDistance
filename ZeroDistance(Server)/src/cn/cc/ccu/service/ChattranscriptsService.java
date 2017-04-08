package cn.cc.ccu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.cc.ccu.mapper.ChattranscriptsMapper;
import cn.cc.ccu.po.Chattranscripts;

public class ChattranscriptsService {
	
	@Autowired
	
	ChattranscriptsMapper chattranscriptsMapper;
	
	
	public int addChattranscripts(Chattranscripts chattranscripts) {
		
		int result=chattranscriptsMapper.insertSelective(chattranscripts);
		
		return result;
		
	}
	
	
	public List<Chattranscripts> selectChattranscriptsByUsernumber(Chattranscripts chat) {
		
		List<Chattranscripts> list=chattranscriptsMapper.selectChattranscriptsByUsernumber(chat);
		
		return list;
		
	}

}
