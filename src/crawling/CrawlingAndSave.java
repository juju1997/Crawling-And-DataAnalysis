package crawling;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlingAndSave {
	
	public static void main(String[] args) throws Exception {
		//ũ�Ѹ��ؼ� DB�� �����ϴ� ��
		Connection conn = null;
		PreparedStatement pstm = null;
		
		try {
			List<String> list = new ArrayList<String>();
			for(int i=1; i<=32; i++) {
				
				String url = "http://www.sajuforum.com/01forum/lotto/lotto_winnum.php?&pages=" + i;
				
				Document doc = Jsoup.connect(url).get();
				
				Elements ele = doc.select("tbody");
				
				Iterator<Element> ie1 = ele.select("td[style=background:white;]").iterator();	//�ζ� ��ȣ DOM
				
				while (ie1.hasNext()) {
					list.add(ie1.next().text()); //��� ��÷��ȣ ���� (���� �ֱ� ��÷��ȣ�� img�� ����Ұ�)
				}
			}
			
			List<String> parsingList = new ArrayList<String>();	//��÷��ȣ 6���� ���� list
			try {
				
				conn = DbConnection.getConnection();	//������ ���� 1ȸ��
				
				for(int j=0; j<list.size(); j++) {
					parsingList.add(list.get(j));
					if(parsingList.size() == 6) {	//��÷��ȣ 6���� listȭ
						System.out.println(parsingList);
						
						String sql = "insert into lotto(no, a, b, c, d, e, f) values(?,?,?,?,?,?,?)";
						pstm = conn.prepareStatement(sql);	//�ѹ� ���� �ݾ� ����� (�� ������ ���μ��� ����ȭ�� ��������)
						

						if(parsingList.get(0).equals("") || parsingList.get(1).equals(null)) {	//�������� ù��° ��÷��ȣ�� ���� ó��
							parsingList.set(0,"7");
							parsingList.set(1,"24");
							parsingList.set(2,"29");
							parsingList.set(3,"30");
							parsingList.set(4,"34");
							parsingList.set(5,"35");
						}	//������ �� �ֽŹ�ȣ�� ������Ʈ ���־����
							
						//1ȸ���� ���� �ֽ� ȸ����
						//���� ��ȣ�� �ѹ��� ���� insert���� �� ���׹����� select�Ҷ� order by �������
						//select * from lotto order by no desc;
						//�� no�� +1 - no �ϸ� ��¥ ȸ������ ���
						
						
						pstm.setInt(1,(j+1)/6);	//6���� �����༭ ������ 6
						pstm.setInt(2, Integer.parseInt(parsingList.get(0)));
						pstm.setInt(3, Integer.parseInt(parsingList.get(1)));
						pstm.setInt(4, Integer.parseInt(parsingList.get(2)));
						pstm.setInt(5, Integer.parseInt(parsingList.get(3)));
						pstm.setInt(6, Integer.parseInt(parsingList.get(4)));
						pstm.setInt(7, Integer.parseInt(parsingList.get(5)));
						
						pstm.executeUpdate();
						
						pstm.close();
						parsingList.clear();
					}
				}
			}catch(Exception sqle) {
				sqle.printStackTrace();
				System.out.println("sql�� �����߻�");
			}finally {
				try {
					if( pstm != null ) {pstm.close();}
					if( conn != null ) {conn.close();}
				}catch(Exception e) {
					System.out.println("disConnection �����߻�");
					throw new RuntimeException(e.getMessage());
				}
			}
		}catch(Exception e) {
			System.out.println("ũ�Ѹ� ���� �߻�");
			e.printStackTrace();
		}
	}
}
