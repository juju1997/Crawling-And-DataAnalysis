package dataAnalysis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import crawling.DbConnection;
import domain.Numbers;

public class SelectAll {
	
	
	public static List<Numbers> selectAll() {	//��� ��÷ ��ȣ ȣ��
		Connection conn = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<Numbers> list = new ArrayList<Numbers>();	//��ȣ�� ���� ����Ʈ
		
		try {
			
			conn = DbConnection.getConnection();
			
			String sql = "select * from lotto order by NO desc";	// 1ȸ ��÷��ȣ ���� ���
			pstm = conn.prepareStatement(sql);
			rs = pstm.executeQuery();
			
			while(rs.next()) {
				Numbers number = new Numbers();
				number.setNo(rs.getInt("no"));
				number.setA(rs.getInt("a"));
				number.setB(rs.getInt("b"));
				number.setC(rs.getInt("c"));
				number.setD(rs.getInt("d"));
				number.setE(rs.getInt("e"));
				number.setF(rs.getInt("f"));
				
				list.add(number);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("sql�����߻�");
		}finally {
			try {
				if( pstm != null ) {pstm.close();}
				if( conn != null ) {conn.close();}
			}catch(Exception e) {
				System.out.println("disConnection �����߻�");
				throw new RuntimeException(e.getMessage());
			}
		}
		
		return list;	//index(0) = 1ȸ��
		
	}
}
