package com.kopo.project200;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		return "list";
	}
	
	@RequestMapping(value = "/hello_api", method = RequestMethod.GET)
	@ResponseBody
	public HashMap<String, String> helloApi(Locale locale, Model model) {
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("name", "홍길동");
		result.put("version", "1.0");
		return result;
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Locale locale, Model model) {
		DBCommon db = new DBCommon<Student>("c:/tomcat/s.db", "s");
		db.createTable(new Student());
		model.addAttribute("m1", "테이블이 생성되었습니다.");
		return "message";
	}

	@RequestMapping(value = "/i", method = RequestMethod.GET)
	public String i1(Locale locale, Model model) {
		return "insert";
	}
	
	@ResponseBody
	@RequestMapping(value = "/insert_api", method = RequestMethod.GET)
	public HashMap<String, String> insertApi(Locale locale, Model model
			, @RequestParam("name") String name, @RequestParam("score") int score) {
		DBCommon db = new DBCommon<Student>("c:/tomcat/s.db", "s");
		db.insertData(new Student(name, score));
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("message", "success");
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/list_api", method = RequestMethod.GET)
	public ArrayList<Student> listApi(Locale locale, Model model) {
		DBCommon db = new DBCommon<Student>("c:/tomcat/s.db", "s");
		ArrayList<Student> result = db.selectArrayList(new Student());
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/update_api", method = RequestMethod.GET)
	public HashMap<String, String> updateApi(Locale locale, Model model
			, @RequestParam("idx") int idx, @RequestParam("name") String name, @RequestParam("score") int score) {
		DBCommon db = new DBCommon<Student>("c:/tomcat/s.db", "s");
		db.updateData(new Student(idx, name, score));
		HashMap<String, String> result = new HashMap<String, String>();
		result.put("message", "success");
		return result;
	}
}
