package springboard.command;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;

@Service
public class DeleteActionCommand implements BbsCommandImpl{
	
//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (DeleteActionCommand)");
	}

	@Override
	public void execute(Model model) {
	
		Map<String, Object> map = model.asMap();
		HttpServletRequest req = (HttpServletRequest)map.get("req");
		
		//폼값 받기
		String idx = req.getParameter("idx");
		String pass = req.getParameter("pass");
		
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		dao.delete(idx,pass);
	}
}
