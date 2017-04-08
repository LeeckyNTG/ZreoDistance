package cn.cc.ccu.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;

import cn.cc.ccu.po.Friend;
import cn.cc.ccu.service.FriendService;
import cn.cc.ccu.service.UserService;


@Controller

@RequestMapping("friend")
public class FriendController {
	
	
	@Autowired
	FriendService friendService;
	
	@Autowired
	UserService userService;
	
	@RequestMapping("addFriend")
	
	public void addFriend(Friend friend, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String requestData=request.getParameter("requestData");
		
		String [] str=requestData.split("@@");
		
		String friendNumber=str[0];
		String selfNumber=str[1];
		
		int pd=userService.selectUserByNumber(friendNumber);
		PrintWriter out = response.getWriter();
		if(pd>0){
			friend.setFriendnumber(friendNumber);			
			friend.setUsernumber(selfNumber);			
			int result=friendService.addFriend(friend);
			
			Friend friend2=new Friend();
			
			friend2.setUsernumber(friendNumber);
			friend2.setFriendnumber(selfNumber);
			
			int result2=friendService.addFriend(friend2);
			
			
			if(result>0&&result2>0){
				out.print("添加成功");
			}else{
				out.print("");
			}
		
		}else{
			out.print("查无此人");
		}
		
	}
		@RequestMapping("SelectFriendByNumber")
		
		public void SelectFriendByNumber(Friend friend, HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html");
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			String number=request.getParameter("requestData");			
			List<Friend> list=friendService.SelectFriendByNumber(number);	
			
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
		
		
		@RequestMapping("deleteFriend")
		public void deleteFriend(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
			response.setContentType("text/html");
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");	
			
			
			String requestData=request.getParameter("requestData");
			
			String [] str=requestData.split("@@");
			
			String friendNumber=str[0];
			String selfNumber=str[1];
			
			Friend friend1=new Friend();
			friend1.setFriendnumber(str[0]);
			friend1.setUsernumber(str[1]);
			Friend friend2=new Friend();
			friend2.setFriendnumber(str[1]);
			friend2.setUsernumber(str[0]);
			
			int rd=friendService.deleteFriend(friend1);
			int rd1=friendService.deleteFriend(friend2);
			
			PrintWriter out = response.getWriter();
			
			if(rd>0&&rd1>0){	
				out.print("删除成功！");
			}else{
				out.print("");
			}	
		
	}
	
	
	

}
