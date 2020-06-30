package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;

/*
 @Service
 	: 해당 어노테이션은 컴파일러에게 해당 클래스가 서비스 역할을 
 	하는 클래스임을 표기하는 역할을 함
 	@Override 처럼 반드시 있어야하는건 아님
 
 */


@Service
public class EditCommand implements BbsCommandImpl{
	
	//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (Edit)");
	}

	@Override
	public void execute(Model model) {
		
		//파라미터 한번에 받기
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		String idx = req.getParameter("idx");
		
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		JDBCTemplateDTO dto = dao.view(idx);
		model.addAttribute("viewRow",dto);
	}
}
