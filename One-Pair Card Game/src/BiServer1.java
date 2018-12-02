import java.net.*;
import java.io.*;
import java.util.*;
public class BiServer{
	private ServerSocket server;
	private BManager bMan=new BManager();   // 메시지 방송자

	public BiServer(){}
	void startServer()                         // 서버를 실행한다.
	{
		try{
			server=new ServerSocket(7776);
			System.out.println("서버소켓이 생성되었습니다.");
			while(true){

				// 클라이언트와 연결된 스레드를 얻는다.
				Socket socket=server.accept();

				// 스레드를 만들고 실행시킨다.
				Bi_Thread ot = new Bi_Thread(socket, bMan);
				ot.start();

				// bMan에 스레드를 추가한다.
				bMan.add(ot);

				System.out.println("접속자 수: "+bMan.size());
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void main(String[] args){
		BiServer server=new BiServer();
		server.startServer();
	}
}