package cn.cc.ccu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import cn.cc.ccu.po.Chattranscripts;
import cn.cc.ccu.po.User;
import cn.cc.ccu.service.ChattranscriptsService;

@Controller
@RequestMapping("chattranscripts")
public class ChattranscriptsControler {
	
	
	@Autowired
	ChattranscriptsService chattranscriptsService;
	
	@RequestMapping("addChattranscripts")
	
	public void addChattranscripts(User user, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String requestData=request.getParameter("requestData");
	
		String usernumber=requestData.split("@@")[0];
		String friendnumber=requestData.split("@@")[1];
		String message=requestData.split("@@")[2];		
		Chattranscripts chat=new Chattranscripts();
		chat.setMessage(message);
		chat.setSendnumber(usernumber);
		chat.setReceivenumber(friendnumber);		
		Date date=new Date();
		chat.setSendtime(date.getTime()+"");
		
		int result=chattranscriptsService.addChattranscripts(chat);
		PrintWriter out = response.getWriter();
		if(result>0){	
			out.print("·¢ËÍ³É¹¦");
		}else{	
			out.print("");
		}	
	}

	
	@RequestMapping("selectChattranscriptsByUsernumber")
	
	public void selectChattranscripts(User user, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String requestData=request.getParameter("requestData");
	
		String usernumber=requestData.split("@@")[0];
		String friendnumber=requestData.split("@@")[1];
		Chattranscripts chat=new Chattranscripts();
		chat.setSendnumber(usernumber);
		chat.setReceivenumber(friendnumber);		
		
		List<Chattranscripts> list=chattranscriptsService.selectChattranscriptsByUsernumber(chat);

		PrintWriter out = response.getWriter();
		if(list.size()>0){	
			Gson gson  = new Gson();		
			String json=gson.toJson(list);
			out.print(json);
		}else{
			String json=null;
			out.print(json);
		}
	}
}
