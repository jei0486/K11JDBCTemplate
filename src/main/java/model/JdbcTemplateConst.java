package model;

import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcTemplateConst {

	/*
	 * 해당 웹 어플리케이션 전테에서 사용하기 위해 static변수를 가진 클래스를 하나 생성한다.
	 * 
	 JDBCTemplate을 웹 어플리케이션  어디에서나 사용할 수 있도록
	 하기위해 static (정적) 변수로 생성한다.
	 */
	public static JdbcTemplate template;
}
