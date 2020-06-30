<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<!--  
Spring - JDBC를 구현하기 위한 절차 0629(월)

1.pom.xml 에서 Spring-JDBC 를 사용하기 위한 의존설정

	1)	ojdbc6(오라클 드라이버) 의존설정  
		<dependency>
			<groupId>com.oracle</groupId>
			<artifactId>ojdbc6</artifactId>
			<version>11.2.0.3</version>
		</dependency>
		
	2)	Spring JDBC (JDBC Template)을 사용하기 위한 의존설정  
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jdbc</artifactId>
			<version>4.1.4.RELEASE</version>
		</dependency>
		
2. servlet-context.xml 에서 빈을 생성한다.

	2-1) dataSource : DB 연결정보
	<beans:bean name="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
		<beans:property name="driverClassName" value="oracle.jdbc.OracleDriver" />
		<beans:property name="url" value="jdbc:oracle:thin:@localhost:1521:orcl" />
		<beans:property name="username" value="kosmo" />
		<beans:property name="password" value="1234" />
	</beans:bean>	
	
	2-2) template : JdbcTemplate 타입의 빈 , 이를 통해 Spring-JDBC 를 구현,, 멤버변수로 쓰임  
	<beans:bean name="template" class="org.springframework.jdbc.core.JdbcTemplate">
		<beans:property name="dataSource" ref="dataSource"/>
	</beans:bean>	
	
3. 요청명을 결정하고 , 컨트롤러를 생성한다.
	servlet-context.xml 에서 기본패키지를 아래와같이 추가함
	 :) 기본패키지를 추가하는 것이 필수적 조건은 아니다. 그러나 관리할 클래스가 많을 경우 패키지를 분리해서 사용하는 것이 편리하다.
	<context:component-scan base-package="springboard" />
	
4. Service 객체 , DAO 객체를 생성한다.
	Service 는 컨트롤러와 모델 사이에서 중재역할을 하는 객체로서 컨트롤러의 모든 요청을 서비스로 넘겨주기 위해
	'커맨드객체'를 사용하게된다.
	request 객체를 model 객체에 저장한후 서비스객체로 model 을 매개변수로 전달한다.
	서비스 객체(command)에서는 model.asMap() 메소드를 통해 Map 컬렉션으로 변환한후 요청들을 처리하고 모델을 호출한다.
	
5. 게시판 구현에 필요한 객체들을 new 를 통해 생성하지 않고 주입(DI)을 통해서 구현한다.이떄 @Autowired를 사용하게된다.	
	컨트롤러에서는 DAO 객체와 각 Service 객체를 주입받아 사용한다.
	
6.어노테이션 @Repository(DAO) @Service(Command) @Autowired (Command)세가지를 이용하여 DI 를 구현한다.
	
-->
<h2>스프링 MVC 사용하기</h2>
<h2>Spring 답변형 비회원제 게시판 제작</h2>

<li>
	<a href="board/list.do" target="_blank">SPRING JDBC(JDBCTemplate)을 이용한 게시판</a>
</li>



</body>
</html>
