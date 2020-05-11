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
		//크롤링해서 DB에 저장하는 용
		Connection conn = null;
		PreparedStatement pstm = null;
		
		try {
			List<String> list = new ArrayList<String>();
			for(int i=1; i<=32; i++) {
				
				String url = "http://www.sajuforum.com/01forum/lotto/lotto_winnum.php?&pages=" + i;
				
				Document doc = Jsoup.connect(url).get();
				
				Elements ele = doc.select("tbody");
				
				Iterator<Element> ie1 = ele.select("td[style=background:white;]").iterator();	//로또 번호 DOM
				
				while (ie1.hasNext()) {
					list.add(ie1.next().text()); //모든 당첨번호 저장 (제일 최근 당첨번호는 img라서 저장불가)
				}
			}
			
			List<String> parsingList = new ArrayList<String>();	//당첨번호 6개씩 담을 list
			try {
				
				conn = DbConnection.getConnection();	//연결은 최초 1회만
				
				for(int j=0; j<list.size(); j++) {
					parsingList.add(list.get(j));
					if(parsingList.size() == 6) {	//당첨번호 6개씩 list화
						System.out.println(parsingList);
						
						String sql = "insert into lotto(no, a, b, c, d, e, f) values(?,?,?,?,?,?,?)";
						pstm = conn.prepareStatement(sql);	//한번 열고 닫아 줘야함 (또 날리면 프로세스 과부화로 연결종료)
						

						if(parsingList.get(0).equals("") || parsingList.get(1).equals(null)) {	//못가져온 첫번째 당첨번호에 대한 처리
							parsingList.set(0,"7");
							parsingList.set(1,"24");
							parsingList.set(2,"29");
							parsingList.set(3,"30");
							parsingList.set(4,"34");
							parsingList.set(5,"35");
						}	//돌리기 전 최신번호로 업데이트 해주어야함
							
						//1회차가 제일 최신 회차임
						//많은 번호가 한번에 들어가서 insert순서 좀 뒤죽박죽임 select할때 order by 해줘야함
						//select * from lotto order by no desc;
						//총 no수 +1 - no 하면 진짜 회차정보 출력
						
						
						pstm.setInt(1,(j+1)/6);	//6개씩 묶어줘서 나누기 6
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
				System.out.println("sql문 문제발생");
			}finally {
				try {
					if( pstm != null ) {pstm.close();}
					if( conn != null ) {conn.close();}
				}catch(Exception e) {
					System.out.println("disConnection 문제발생");
					throw new RuntimeException(e.getMessage());
				}
			}
		}catch(Exception e) {
			System.out.println("크롤링 문제 발생");
			e.printStackTrace();
		}
	}
}
