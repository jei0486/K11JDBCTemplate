package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;

@Service
public class ReplyCommand implements BbsCommandImpl{
	
	//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (ReplyCommand)");
	}
	
	@Override
	public void execute(Model model) {
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest)map.get("req");
		
		String idx = req.getParameter("idx");
		
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		JDBCTemplateDTO dto = dao.view(idx);
		
		/*
		 기존 게시물을 가져와서 제목과 내용 부분에 아래와 같이 문자열 처리를 한다.
		 내용의 경우 textarea 에 표현해야 하므로 <br> 태그 대신 \n\r 이 들어가야 한다.
		 */
		dto.setTitle("[RE]"+dto.getTitle());
		dto.setContents("\n\r\n\r--[원본글]---\n\r"+dto.getContents());
		
		model.addAttribute("replyRow",dto);
		
	}

}
