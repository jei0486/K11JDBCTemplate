package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;
import model.JdbcTemplateConst;
import springboard.command.BbsCommandImpl;
import springboard.command.DeleteActionCommand;
import springboard.command.EditActionCommand;
import springboard.command.EditCommand;
import springboard.command.ListCommand;
import springboard.command.ReplyActionCommand;
import springboard.command.ReplyCommand;
import springboard.command.ViewCommand;
import springboard.command.WriteActionCommand;


/*
@Autowired
 : 스프링 설정파일에서 생성된 빈을 자동으로 주입받을때 사용하는 어노테이션
	 -생성자, 필드(멤버변수), 메소드(setter)에 적용 가능
	 -setXXX()의 형식이 아니어도 적용가능
	 -타입을 이용해 자동으로 프로퍼티 값을 설정
	 -따라서 빈을 주입받을 객체가 존재하지않거나, 같은 타입이 2개이상 존재하면 예외가 발셍함
 	- @Autowired 는 한수내의 지역변수로 선언할 수 없다 , 반드시 전역변수로 선언해야한다.
 
@Controller 
	: 
 
 */
@Controller
public class BbsController {
	/*
	 스프링 애플리케이션이 구동될때 미리 생성된 JdbcTemplate 타입의 빈을 자동으로 주입받게된다.
	 */
	
//1차 버전에서 사용	
//	private JdbcTemplate template;
	
//	@Autowired
//	public void setTemplate(JdbcTemplate template) {
//		this.template = template;
//		System.out.println("@Autowired -> JDBCTemplate 연결성공");
//		//model 패키지에 가면 static 상수 만들어 놓은게 있음->//해당 어플리케이션 전체에서 사용하겠다는 의미
//		
//		JdbcTemplateConst.template = this.template;
//	}
	
	/*
	 BbsCommandImpl 타입의 멤버변수 선언
	 멤버변수 이므로 클래스 내에서 전역적으로 사용한다. 
	 해당 클래스의 모든 command 객체는 위 인터페이스를 구현하여 정의하게 된다.
	 
	 스프링 MVC는 커맨드 객체의 클래스 이름과 동일한 속성 이름을 사용해서 커맨드 객체를 뷰에 전달한다.
	(이 때, 커맨드 객체 클래스명의 맨 앞글자는 소문자로 바꿈)
	 */
	BbsCommandImpl command = null;

	public void setCommand(BbsCommandImpl command) {
		this.command = command;
	}

	
	/*
	 JDBCTemplateDAO dao; 
	 
	 	컨트롤러에서는 dao 를 생성하지않고 service객체에서 dao 생성과 처리를 맡기지만 
	 	지금 BbsController 에서는 password 검증메소드에서 dao를 사용중이라서 
	 	선언함
	 */
	
	//2차 버전에서 사용
	JDBCTemplateDAO dao;
	@Autowired
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
	}
	
	/*
	 2차버전 :) 8개 new 생성한 커맨드객체 를 전역 변수로 생성하고 @Autowired 어노테이션을 붙여줌
	 
	 Spring 에서는 new를 통해 객체를 생성하지않고 스프링 컨테이너에 의해 미리 생성된 빈(객체)을 주입(DI)받아서 
	 사용하게 된다.
	 따라서 기본 클래스가 아닌 개발자가 직접 정의하여 자주 사용되는 객체 들은 아래와 같이
	 자동주입 받아서 사용하는 것이  스프링 개발방법론에 부합하는 방법이다.
	 */
	@Autowired
	ListCommand listCommand;
	@Autowired
	ViewCommand viewCommand;
	@Autowired
	WriteActionCommand writeActionCommand;
	@Autowired
	EditCommand editCommand;
	@Autowired
	EditActionCommand editActionCommand;
	@Autowired
	DeleteActionCommand deleteActionCommand;
	@Autowired
	ReplyCommand replyCommand;
	@Autowired
	ReplyActionCommand replyActionCommand;
	
	
	//게시판 리스트
	@RequestMapping("/board/list.do")
	public String list(Model model,HttpServletRequest req){
		
		/*
		 사용자로부터 받은 모든 요청은 HttpServletRequest 객체에 저장되고,
		 이른 커맨드객체로 전달하기 위해 model 에 저장후 매개변수로 전달한다.
		 */
		model.addAttribute("req",req);
		
		/*
		 컨트롤러는 사용자의 요청을 분석한 후 해당 요청에 맞는 서비스객체만
		 호출하고, 실제 DAO의 호출이나 비즈니스 로직은 아래 Command 객체가 처리하게된다.
		 */
// 1차버전		
//		command = new ListCommand();
		command = listCommand;
		command.execute(model);
		
		return "07Board/list";
	}
	
	@RequestMapping("/board/write.do")
	public String write(Model model) {
		return "07Board/write";
	}
	
	/*
	 글쓰기 처리 : post 로 전송되므로 두가지 속성을 모두 사용하여 매핑
	 */
	@RequestMapping(value = "/board/writeAction.do", method = RequestMethod.POST)
	public String WriteAction(Model model, HttpServletRequest req, JDBCTemplateDTO jdbcTemplateDTO){
		
		//request 객체를 모델에 저장
		model.addAttribute("req",req);
		//View에서 전송한 폼값을 한꺼번에 저장한 커맨드 객체로 DTO를 저장
		model.addAttribute("jdbcTemplateDTO",jdbcTemplateDTO);
		command = writeActionCommand;
//		command = new WriteActionCommand();
		command.execute(model);
		
		//글쓰기 처리 완료후  list.do 로 로케이션 (이동) 된다.
		return "redirect:list.do?nowPage=1";
	}
	
	@RequestMapping("/board/view.do")
	public String view(Model model , HttpServletRequest req) {
		
		model.addAttribute("req",req);
		//command = new ViewCommand();
		command = viewCommand;
		command.execute(model);
		
		return "07Board/view";
	}
	
	@RequestMapping("/board/password.do")
	public String password(Model model , HttpServletRequest req) {
		
		model.addAttribute("idx",req.getParameter("idx"));
		
		return "07Board/password";
	}
	
	@RequestMapping("/board/passwordAction.do")
	public String passwordAction(Model model , HttpServletRequest req) {
		
		String modePage = null;
		
		String idx = req.getParameter("idx");
		String mode = req.getParameter("mode");
		String nowPage = req.getParameter("nowPage");
		String pass = req.getParameter("pass");
		
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		int rowExist = dao.password(idx,pass);
		
		if(rowExist<=0) {
			model.addAttribute("isCorrMsg","패스워드가 일치하지 않습니다.");
			model.addAttribute("idx",idx);
			modePage = "07Board/password";
		}
		else {
			System.out.println("검증완료");
			
			if(mode.equals("edit")) {
				//수정이면 수정폼으로 이동한다.
				model.addAttribute("req",req);
//				command = new EditCommand();
				command = editCommand;
				command.execute(model);
				modePage = "07Board/edit";
			}
			//삭제처리
			else if(mode.equals("delete")) {
				//패스워드 검증후 문제가 없다면 즉시 삭제처리한다.
				model.addAttribute("req",req);
				//command = new DeleteActionCommand();
				command = deleteActionCommand;
				command.execute(model);
				/*
				 컨트롤러에서 뷰의 경로를 반환할때 아래와 같이 redirect 하게 되면 
				 list.do?nowPage=10 이와 같이 URL 을 자동으로 조립해서 
				 로케이션 시켜준다.
				 */
				model.addAttribute("nowPage",req.getParameter("nowPage"));
				modePage = "redirect:list.do";
			}
			
		}
		return modePage;
	}
	
	//수정하기
	@RequestMapping("/board/editAction.do")
	public String editAction(Model model, HttpServletRequest req, JDBCTemplateDTO jdbcTemplateDTO){
		
		//request 객체를 모델에 저장
		model.addAttribute("req",req);
		//View에서 전송한 폼값을 한꺼번에 저장한 커맨드 객체로 DTO를 저장
		model.addAttribute("jdbcTemplateDTO",jdbcTemplateDTO);
		//EditActionCommand => 서비스 객체
//		command = new EditActionCommand();
		command = editActionCommand;
		command.execute(model);
		
		/*
		 게시물 수정후 상세보기 페이지로 돌아가기 위해서는 idx값이 반드시 필요하다 이동시 쿼리스트링 형태로 redirect 하지않고
		 model 객체에 필요한 파라미터를 저장하면 자동으로 URL 을 조립하여 View를 호출해준다
		 */
		model.addAttribute("idx",req.getParameter("idx"));
		model.addAttribute("nowPage",req.getParameter("nowPage"));
		
		//수정하기 처리 완료후  redirect:view.do 로 로케이션 (이동) 된다.
		return "redirect:view.do";
	}
	//답변달기 
	@RequestMapping("/board/reply.do")
	public String reply(Model model, HttpServletRequest req){
		System.out.println("reply 메소드 호출");
		
		model.addAttribute("req",req);
		//command = new ReplyCommand();
		command = replyCommand;
		command.execute(model);
		
		model.addAttribute("idx",req.getParameter("idx"));
		
		return "07Board/reply";
	}
	
	//답변글 쓰기 처리
	@RequestMapping("/board/replyAction.do")
	public String replyAction(Model model, HttpServletRequest req, JDBCTemplateDTO jdbcTemplateDTO){

		//작성된 내용은 커맨드 객체를 통해 한번에 폼값받음
		model.addAttribute("jdbcTemplateDTO",jdbcTemplateDTO);
		model.addAttribute("req",req);
		
//		command = new ReplyActionCommand();
		command = replyActionCommand;
		command.execute(model);
		
		//답글쓰기 완료후 리스트 페이지로 이동함
		model.addAttribute("nowPage",req.getParameter("nowPage"));
		return "redirect:list.do";
	}
	
	
}
