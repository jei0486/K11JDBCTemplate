package springboard.command;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import model.JDBCTemplateDAO;
import model.JDBCTemplateDTO;
import springboard.util.EnvFileReader;
import springboard.util.PagingUtil;

/*
  BbsCommandImpl를 구현했으므로 execute() 메소드는 반드시 오버라이딩 해야한다
 또한 해당 객체는 부모타입인 BbsCommandImpl 로 참조할 수 있다.
 
@Service 의 역할:  컨트롤러와 DAO 중간 역할 

Service 역할의 클래스임을 명시함
Service 객체는 Controller 와 Model 사이에서 중재 역할을 함
 */
@Service
public class ListCommand implements BbsCommandImpl{

	//DAO 객체 생성 (2차 버전) 
	@Autowired
	JDBCTemplateDAO dao;
	public void setDao(JDBCTemplateDAO dao) {
		this.dao = dao;
		System.out.println("JDBCTemplateDAO 자동 주입 (List)");
	}


	/*
	 컨트롤러에서 인자로 전달해준 model 객체를 매개변수로 전달받는다.
	 model 객체에는 사용자가 요청한 정보인 request 객체가 저장되어있다.
	 */
	@Override
	public void execute(Model model) {
		System.out.println("ListCommand > execute() 호출");
		/*
		 컨트롤러에서 넘겨준 파라미터를 asMap() 메소드를 통해 Map 컬렉션으로 변환한다.
		 그리고 request 객체를 형변환하여 가져온다. 
		 */
		Map<String, Object> paramMap = model.asMap();
		HttpServletRequest req = (HttpServletRequest)paramMap.get("req");
		
		//DAO 객체 생성(1차 버전)
		//JDBCTemplateDAO dao = new JDBCTemplateDAO();
		
		//검색어 관련 폼값 처리
		String addQueryString = "";
		String  searchColumn = req.getParameter("searchColumn");
		String  searchWord = req.getParameter("searchWord");
		if(searchWord!=null) {
			addQueryString = String.format("searchColumn=%s&searchWord=%s&", searchColumn,searchWord);
			paramMap.put("Column",searchColumn);
			paramMap.put("Word",searchWord);
		}
		//전체 레코드 수 카운트 하기
		int totalRecordCount = dao.getTotalCount(paramMap);
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		//페이지 처리부분 START
		
		//Environment 객체를 이용한 외부파일 읽어오기
		int pageSize = Integer.parseInt(EnvFileReader.getValue("SpringBbsInit.properties","springBoard.pageSize"));
		int blockPage = Integer.parseInt(EnvFileReader.getValue("SpringBbsInit.properties","springBoard.blockPage"));
		
		//전체 페이지 수 계산
		int totalPage = (int)Math.ceil((double)totalRecordCount/pageSize);
		
		//현재 페이지 번호 첫 진입이라면 무조건 1페이지로 지정
		int nowPage = req.getParameter("nowPage")==null ? 1 : Integer.parseInt(req.getParameter("nowPage"));
		
		//리스트에 출력할 게시물의 시작/종료 구간(select 절의 between)에 사용
		int start = (nowPage-1)*pageSize +1;
		int end = nowPage * pageSize;
		
		paramMap.put("start",start);
		paramMap.put("end",end);
		
		//페이지 처리부분 END
		////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		
		//출력할 리스트 가져오기
		//ArrayList<JDBCTemplateDTO> listRows = dao.list(paramMap);//페이지 X
		ArrayList<JDBCTemplateDTO> listRows = dao.listPage(paramMap); //페이지O
		
		//가상번호 부여하기
		int virtualNum = 0;
		int countNum = 0;
		
		for(JDBCTemplateDTO row : listRows) {
			
			//전체 게시물의 갯수에서 하나씩 차감하면서 가상번호 부여
			/*
			  virtualNum = totalRecordCount--; row.setVirtualNum(virtualNum);
			 */
			
			//페이지 번호 적용하여 가상번호 계산
			virtualNum = totalRecordCount-(((nowPage-1)*pageSize)+countNum++);
			row.setVirtualNum(virtualNum);
			
			
			//답변글에 대한 리스트 처리(re.gif 이미지를 제목에 삽입)
			String reSpace = "";
			//해당 게시물의 indent가 0보다 크다면 ( 답변글이라면...)
			if(row.getBindent()>0) {
				//indent 의 크기만큼 공백(&nbsp;)을 추가해준다.
				for(int i=0;i<row.getBindent();i++) {
					reSpace +="&nbsp;&nbsp;";
				}
				//reply 이미지를 추가해준다.
				row.setTitle(reSpace 
						+"<img src='../images/re3.gif'>"
						+row.getTitle());
			}
		}
		
		String pagingImg =  PagingUtil.pagingImg(totalRecordCount, pageSize, blockPage, nowPage, 
								req.getContextPath()+"/board/list.do?"+addQueryString);
		//model 객체에 출력리스트 저장
		model.addAttribute("pagingImg", pagingImg);
		model.addAttribute("totalPage", totalPage);//전체 페이지수 
		model.addAttribute("nowPage", nowPage);//현재 페이지 번호
		model.addAttribute("listRows", listRows);
		
		//JDBCTemplate 에서는 자원반납을 하지않는다. 따라서 스프링에서는 close 의 의미가 없음
		//dao.close();
	}
	
}
