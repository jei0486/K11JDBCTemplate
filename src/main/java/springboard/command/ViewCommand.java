package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;
@Service
public class ViewCommand implements BbsCommandImpl{
	
	
	//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (View)");
	}

	@Override
	public void execute(Model model) {
		Map<String, Object> paramMap = model.asMap();
		
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//request 객체를 통해 폼값을 받음
		String idx = req.getParameter("idx");
		String nowPage = req.getParameter("nowPage");
		
		//DAO의 view메소드를 호출하여 idx 에 해당하는 레코드 하나를 반환받음
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		JDBCTemplateDTO dto = new JDBCTemplateDTO();
		
		dto = dao.view(idx);
		//내용에 대해 줄바꿈 처리
		dto.setContents(dto.getContents().replace("\r\n", "<br/>"));
		
		model.addAttribute("viewRow", dto);
		model.addAttribute("nowPage", nowPage);
		
	}
}
