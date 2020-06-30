package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;

@Service
public class WriteActionCommand implements BbsCommandImpl{
	
	//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (WriteActionCommand)");
	}

	@Override
	public void execute(Model model) {
		
		//파라미터 한번에 전달받기
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		JDBCTemplateDTO jdbcTemplateDTO = (JDBCTemplateDTO)paramMap.get("jdbcTemplateDTO");
		
		//커맨드객체에 저장된 title을 로그에 출력(폼값 확인용)
		System.out.println("jdbcTemplateDTO.Title = "+jdbcTemplateDTO.getTitle());
		
		//DAO 객체 생성후 insert 처리
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		dao.write(jdbcTemplateDTO);
	}
}
