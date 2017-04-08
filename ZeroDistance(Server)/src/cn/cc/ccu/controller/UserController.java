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

import cn.cc.ccu.po.User;
import cn.cc.ccu.service.UserService;
import cn.cc.ccu.tools.EmailYz;
import cn.cc.ccu.tools.Number;

import com.google.gson.Gson;

@Controller
@RequestMapping("user")
public class UserController {

	@Autowired
	UserService userService;

	@RequestMapping("login")
	public void login(User user, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String requestData = request.getParameter("requestData");
		System.out.println(requestData);
		String number = requestData.split("@@")[0];
		String password = requestData.split("@@")[1];
		user.setNumber(number);
		user.setPassword(password);
		User login = userService.login(user);
		PrintWriter out = response.getWriter();

		if (login != null) {
			request.getSession().setAttribute("user", login);
			Gson gson = new Gson();
			String json = gson.toJson(login);
			out.print(json);
		} else {
			String json = null;
			out.print(json);
		}

	}

	@RequestMapping("getEmailToUsername")
	public void getEmailToUsername(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String username = request.getParameter("requestData");

		String name = userService.getEmailToUsername(username);
		PrintWriter out = response.getWriter();

		Gson gson = new Gson();
		String json = gson.toJson(name);
		out.print(json);

	}

	@RequestMapping("getYzm")
	public void getYzm(User user, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String email = request.getParameter("requestData");
		EmailYz emailYz = new EmailYz(email);
		PrintWriter out = response.getWriter();
		String yzm = null;
		try {
			yzm = emailYz.sendYzm();
			out.print(yzm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@RequestMapping("register")
	public void register(User user, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String requestData = request.getParameter("requestData");
		String email = requestData.split("@@")[0];
		String password = requestData.split("@@")[1];
		user.setEmail(email);
		user.setPassword(password);

		List<User> list = userService.getNumber();
		int num = 0;
		for (int i = 0; i < list.size(); i++) {

			if (Integer.parseInt(list.get(i).getNumber()) > num) {
				num = Integer.parseInt(list.get(i).getNumber());
			}
		}

		user.setNumber((num + 1) + "");

		int result = userService.insert(user);
		PrintWriter out = response.getWriter();
		if (result > 0) {
			out.print(user.getNumber());
		} else {
			out.print("");
		}
	}
}
