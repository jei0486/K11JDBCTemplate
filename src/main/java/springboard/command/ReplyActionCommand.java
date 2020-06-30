package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;

@Service
public class ReplyActionCommand implements BbsCommandImpl{
	//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (ReplyActionCommand)");
	}
	
	@Override
	public void execute(Model model) {
		
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest)map.get("req");
		
		JDBCTemplateDTO jdbcTemplateDTO = (JDBCTemplateDTO)map.get("jdbcTemplateDTO");
	
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		dao.reply(jdbcTemplateDTO);
		
	}

}
