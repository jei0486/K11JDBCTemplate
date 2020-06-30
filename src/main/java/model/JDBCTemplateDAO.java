package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import oracle.net.aso.f;

/*
 JdbcTemplate 관련 주요메소드
 
 -List query(String sql, RowMapper rowMapper)
 	: 여러개의 레코드를 반환하는 select 계열의 쿼리문인 경우 사용한다.
 	
 -List query(String sql, Object[] args , RowMapper rowMapper)
 	: 인파라미터를 가진 여러개의 레코드를 반환하는 select 계열의 쿼리문인 경우 사용한다.
 	
 -int queryForInt(Stirng sql) 혹은 queryForInt(String sql, Object[] args)
 	: 쿼리문의 실행결과가 숫자를 반환하는 select 계열의 쿼리문에 사용한다.
 	
-Object queryForObject (String sql, RowaMapper rowMapper) 
		혹은 Object queryForObject (String sql, Object[] args ,RowaMapper rowMapper) 
    :하나의 레코드를 반환하는 select 계열의 쿼리문 실행시 사용된다. 	
    
-int update(String sql)
   : 인파라미터가 없는 update/delete/insert 쿼리문을 처리할때 사용함
- int update(String sql, Object[] args)
   : 인파라미터가 있는 update/delete/ insert 쿼리문을 처리할때 사용함
 */

/*
 @Repository 의 의미
 
 mvc패턴에서  model의 역할을 해주는 클래스에 repository 어노테이션을 붙인다. database 역할 
 */

//DAO(Model) 역할의 클래스임을 명시하는 어노테이션
@Repository
public class JDBCTemplateDAO {
	
	//멤버변수
	JdbcTemplate template;
	
	//setter 메소드 생성 ( Sptring-JDBC 2차 버전) :) public void setTemplate(JdbcTemplate template) {
	/*
	 servlet-context.xml 에서 생성된 dao 빈을 자동으로 주입받기위해
	 생성한 setter 메소드
	 */
	
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
	}
	
	
	public JDBCTemplateDAO() {
		/*
		 	컨트롤러에서 @Autowired 를 통해 자동 주입받았던 빈을 
		 	정적변수인 JdbcTemplateConst.template을 통해 가져온다.
		 	즉, DB 연결정보를 웹어플리케이션 어디서든 사용할 수 있다.
		 */
		
		//JDBCTemplateDAO Spring-JDBC 1파 버전에서 사용함 ( new 로 DAO 개게 생성)
		//this.template = JdbcTemplateConst.template;
		System.out.println("JDBCTemplateDAO()생성자 호출");
	}

	public void close() {
		//JDBCTemplate 에서는 사용하지않음
	}
	
	//게시물 수 카운트
	public int getTotalCount(Map<String, Object>map) {
		
		String sql = "SELECT COUNT(*) FROM springboard ";
		
		if(map.get("Word")!=null) {
			sql+=" WHERE "+ map.get("Column")+" LIKE '%" +map.get("Word")+"%' ";
		}
		System.out.println(sql);
		return template.queryForObject(sql, Integer.class);
	}
	
	//게시판 리스트 (페이지 처리 X)
	public ArrayList<JDBCTemplateDTO> list (Map<String, Object>map){
		
		String sql = "SELECT * FROM springboard ";
		if(map.get("Word")!=null) {
			sql += " WHERE "+map.get("Column") + " LIKE '%"+map.get("Word")+"%' ";
		}
//		sql +=" ORDER BY idx DESC"; -> 답변글 사용하지 않을 경우 
		sql +=" ORDER BY bgroup DESC, bstep ASC"; //답변글 적용시 
		
		
		System.out.println(sql);
		/*
		 	query 메소드의 반환타입은 List 계열의 컬렉션이므로 제네릭 부분만 우리가 필요한 DTO 객체로 대체하면 된다.
		 	나머지는 RowMapper 객체가 모두 알아서 처리해준다.
		 */
		return (ArrayList<JDBCTemplateDTO>)template.query(sql, 
				new BeanPropertyRowMapper<JDBCTemplateDTO>(JDBCTemplateDTO.class));
		
	}
	
	//글쓰기 처리
	public void write(final JDBCTemplateDTO jdbcTemplateDTO) {
		
		//template.update => preparedstatement 의  executeUpdate() 의 느낌...
		template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				/*
				 답변형 게시판에서 원본글인 경우에는 idx(일련번호)와 bgroup(그룹번호)가 반드시 일치해야한다.
				 또한 NEXTVAL은 한문장에서 여러번 사용하더라도 같은 시퀀스를 반환한다.
				 */
				String sql = "INSERT INTO springboard ( idx, name, title, contents, hits, bgroup, bstep, bindent, pass)"
						+ " VALUES ( springboard_seq.NEXTVAL , ?,?,?,0,"
						+ " springboard_seq.NEXTVAL , 0,0,?)";
				
				PreparedStatement psmt = con.prepareStatement(sql);
				
				psmt.setString(1, jdbcTemplateDTO.getName());
				psmt.setString(2, jdbcTemplateDTO.getTitle());
				psmt.setString(3, jdbcTemplateDTO.getContents());
				psmt.setString(4, jdbcTemplateDTO.getPass());
				
				return psmt;
			}
		});
	}
	
	//글 수정하기
	public void edit(final JDBCTemplateDTO jdbcTemplateDTO) {
		/*
		 해당 게시판에서 패스워드는 변경의 대상이 아니라 검증의 대상으로만 사용됨
		 따라서 set절이 아니라 where 절에 삽입된다.
		 */
		
		String sql = "UPDATE springboard SET name=? , title=? , contents=? WHERE idx=? AND pass=?";
		
		/*
		 매개변수 DTO 객체를 아래 익명클래스 내부에서 사용해야 하므로 반드시 final을 붙여줘야한다.
		 */
		template.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, jdbcTemplateDTO.getName());
				ps.setString(2, jdbcTemplateDTO.getTitle());
				ps.setString(3, jdbcTemplateDTO.getContents());
				ps.setInt(4, jdbcTemplateDTO.getIdx());
				ps.setString(5, jdbcTemplateDTO.getPass());
			}
		});
	}
	
	
	
	
	
	/*
	 매개변수로 전달되는 idx를 아래 익명 클래스내부에서 사용하기 위해서는 반드시
	 final 로 선언해야 사용가능하다.
	 (자바의 규칙)
	 */
	//조회수 증가
	//매개변수를 final 로 선언한 이유는 내부클래스에서 사용해야되기 때문이다. final 을 지울시 에러가 난다.
	public void updateHit(final String idx) {
		String sql = "UPDATE springboard SET hits = hits+1 WHERE idx=?";
		
		template.update(sql, 
				//익명클래스 //하나의 객체를 생성한것 함수내에서 매개변수로 쓰는것
				new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, Integer.parseInt(idx));
			}
		});
		
	}
	
	
	//상세보기 처리
	public JDBCTemplateDTO view(String idx) {
		
		//조회수 증가
		updateHit(idx);
		
		JDBCTemplateDTO dto = new JDBCTemplateDTO();

		String sql = "SELECT * FROM springboard WHERE idx = "+idx;
		/*
		 queryForObject()메소드는 반환결과가 0개이거나 2개이상인 경우 
		 예외가 발생하므로 반드시 예외처리를 해주는것이 좋다 (반환되는 행이 1개가 아닌경우)
		 */
		try {
			
			dto = template.queryForObject(sql, new BeanPropertyRowMapper<JDBCTemplateDTO>(JDBCTemplateDTO.class));
		} catch (Exception e) {
			System.out.println("View()실행시 예외발생");
			e.printStackTrace();
		}
		
		return dto;
	}
	
	//패스워드 검증 
	public int password(String idx , String pass){
		
		int retNum = 0;
		String sql = "SELECT * FROM springboard WHERE pass= '"+pass +"' AND idx="+idx;
		
		/*
		 만약 패스워드가 틀린 경우라면 반환되는 행이 0 개 이므로 예외처리를 하고 있다. 
		 queryForObject() 는 반환되는 행이 1개 일때만 정상 동작한다.
		 */
		try {
			JDBCTemplateDTO dto = template.queryForObject(sql, new BeanPropertyRowMapper<JDBCTemplateDTO>(JDBCTemplateDTO.class));
			
			/*
			 idx 와 pass에 해당하는 게시물이 정상적으로 가져와졌을때는
			 해당 idx값을 반환값으로 사용한다. 
			 */
			retNum = dto.getIdx();
		} catch (Exception e) {
			/*
			 만약 일치하지 않아 예외가 발생되면 0을 반환한다.
			 일련번호는 시퀀스를 사용하므로 항상 0보다는 큰 값을 가지게 된다. 
			 */
			System.out.println("password  sql 예외발생");
			e.printStackTrace();
		}
		
		return retNum;
	}
	
	
	//게시글 삭제처리
	public void delete(final String idx, final String pass) {
		
		String sql = "DELETE FROM springboard WHERE idx=? AND pass=?";
		
		template.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, idx);
				ps.setString(2, pass);
			}
		});
	}
	
	//답변글 쓰기
	public void reply(final JDBCTemplateDTO dto) {
		//답변 글쓰기 전 레코드 업데이트
		replyPrevUpdate(dto.getBgroup(),dto.getBstep());//bstep 때문에 !
		/*
		 원본글의 경우 idx 와 bgroup은 동일한 값을 입력함
		 답변글의 경우 원본글의 group 번호를 그대로 가져와서 입력함
		 즉, idx는 시퀀스를 통해 bgroup 은 원본글과 동일하게 입력
		 */
		String sql = "INSERT INTO springboard (idx,name,title,contents, pass, bgroup,bstep,bindent) VALUES (springboard_seq.nextval,? ,? ,? ,? ,? ,? ,?)";
		
		/*
		 답변글인 경우 원본글의 step+1, indent+1 처리하여 입력한다
		 */
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1,dto.getName() );
				ps.setString(2,dto.getTitle() );
				ps.setString(3,dto.getContents() );
				ps.setString(4,dto.getPass() );
				ps.setInt(5,dto.getBgroup() );
				ps.setInt(6,dto.getBstep()+1 );
				ps.setInt(7,dto.getBindent()+1);
			}
		});
	}
	
	//답변글 쓰기전 레코드 일괄 업데이트(step 을 뒤로 밀어주기 위한 로직)
	public void replyPrevUpdate( final int strGroup,final int strStep) {
		
		//새치기 하는 느낌 원래 있던 레코드들의 step 이 +1 돼서 인터셉트 가능
		String sql = "UPDATE springboard SET bstep = bstep +1 WHERE bgroup = ? and bstep > ? ";
		
		template.update(sql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, strGroup);
				ps.setInt(2, strStep);
			}
		});
	}
	
	//리스트 처리 (페이지 O)
	public ArrayList<JDBCTemplateDTO> listPage(Map<String, Object> map) {

		int start = Integer.parseInt(map.get("start").toString());
		int end = Integer.parseInt(map.get("end").toString());

		String sql = "" + "SELECT * FROM (" + "    SELECT Tb.*, rownum rNum FROM ("
				+ "        SELECT * FROM springboard ";
		if (map.get("Word") != null) {
			sql += " WHERE " + map.get("Column") + " " + " LIKE '%" + map.get("Word") + "%' ";
		}
		sql += " ORDER BY bgroup DESC, bstep ASC" + "    ) Tb" + ")" + " WHERE rNum BETWEEN " + start + " and " + end;

		return (ArrayList<JDBCTemplateDTO>) template.query(sql,
				new BeanPropertyRowMapper<JDBCTemplateDTO>(JDBCTemplateDTO.class));
	}
	
	
	
}
