package crawling;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbConnection {
	public static Connection dbConn;
	
	public static Connection getConnection() {
		Connection conn = null;
		try {
			String user = "scott";
			String pw = "tiger";
			String url = "jdbc:oracle:thin:@localhost:1521:xe";
			
			Class.forName("oracle.jdbc.driver.OracleDriver"); 
			conn = DriverManager.getConnection(url, user, pw);
			
			System.out.println("DB연결 성공");
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
}
