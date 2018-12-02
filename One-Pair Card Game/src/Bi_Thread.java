import java.net.*;
import java.io.*;
import java.util.*;

// 클라이언트와 통신하는 스레드 클래스
class Bi_Thread extends Thread{
	private int roomNumber=-1;      // 방 번호
	private int cnt =0;
	private String userName=null;  // 사용자 이름
	private Socket socket;              // 소켓
	private BManager bMan;		// 메시지 방송자

	// 게임 준비 여부, true이면 게임을 시작할 준비가 되었음을 의미한다.
	private boolean ready=false;

	private BufferedReader reader;     // 입력 스트림
	private PrintWriter writer;           // 출력 스트림
	Bi_Thread(Socket socket, BManager bMan){     // 생성자
		this.socket=socket;
		this.bMan = bMan;
	}
	Socket getSocket(){               // 소켓을 반환한다.
		return socket;
	}
	int getRoomNumber(){             // 방 번호를 반환한다.
		return roomNumber;
	}
	String getUserName(){             // 사용자 이름을 반환한다.
		return userName;
	}
	boolean isReady(){                 // 준비 상태를 반환한다.
		return ready;
	}
	public void run(){
		try{
		reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer=new PrintWriter(socket.getOutputStream(), true);

		String msg;                     // 클라이언트의 메시지
		while((msg=reader.readLine())!=null)
		{
			// msg가 "[NAME]"으로 시작되는 메시지이면
			if(msg.startsWith("[NAME]"))
			{
				userName=msg.substring(6);          // userName을 정한다.
			}

			// msg가 "[ROOM]"으로 시작되면 방 번호를 정한다.
			else if(msg.startsWith("[ROOM]"))
			{
				int roomNum=Integer.parseInt(msg.substring(6));
				if( !bMan.isFull(roomNum))             // 방이 찬 상태가 아니면
				{
					// 현재 방의 다른 사용에게 사용자의 퇴장을 알린다.
					if(roomNumber!=-1)
						bMan.sendToOthers(this, "[EXIT]"+userName);

					// 사용자의 새 방 번호를 지정한다.
					roomNumber=roomNum;

					// 사용자에게 메시지를 그대로 전송하여 입장할 수 있음을 알린다.
					writer.println(msg);

					// 사용자에게 새 방에 있는 사용자 이름 리스트를 전송한다.
					writer.println(bMan.getNamesInRoom(roomNumber));

					// 새 방에 있는 다른 사용자에게 사용자의 입장을 알린다.
					bMan.sendToOthers(this, "[ENTER]"+userName);
				}
				else writer.println("[FULL]");        // 사용자에 방이 찼음을 알린다.
			}

			else if(roomNumber>=1 && msg.startsWith("[CARD]")) //카드 뒤집기
				bMan.sendToOthers(this,msg);

			else if(msg.startsWith("[RESET]"))
				bMan.sendToOthers(this,msg);

			// 대화 메시지를 방에 전송한다.
			else if(msg.startsWith("[MSG]"))
				bMan.sendToRoom(roomNumber, "["+userName+"]: "+msg.substring(5));

			// "[START]" 메시지이면
			else if(msg.startsWith("[ENDGAME]"))
				ready=false;

			else if(msg.startsWith("[START]"))
			{
				ready=true;   // 게임을 시작할 준비가 되었다.

				// 다른 사용자도 게임을 시작한 준비가 되었으면
				if(bMan.isReady(roomNumber))
				{
					// 1p2p를 정하고 전송한다
					bMan.sendTo(0, "Player1");
					bMan.sendTo(0, "T");
					bMan.sendTo(1, "Player2");
					bMan.sendTo(1, "F");
				}
			}
			// 사용자가 게임을 기권하는 메시지를 보내면
			else if(msg.startsWith("[DROPGAME]"))
			{
				ready=false;
				// 상대편에게 사용자의 기권을 알린다.
				bMan.sendToOthers(this, "[DROPGAME]");
			}
			else if(msg.startsWith("Player1wrong"))
			{
				bMan.sendTo(0, "F");
				bMan.sendTo(1, "T");
			}
			else if(msg.startsWith("Player2wrong"))
			{
				bMan.sendTo(0, "T");
				bMan.sendTo(1, "F");
			}
		}
		}catch(Exception e){
		}finally{
			try{
				bMan.remove(this);
				if(reader!=null) reader.close();
				if(writer!=null) writer.close();
				if(socket!=null) socket.close();
				reader=null; writer=null; socket=null;
				System.out.println(userName+"님이 접속을 끊었습니다.");
				System.out.println("접속자 수: "+bMan.size());
				// 사용자가 접속을 끊었음을 같은 방에 알린다.
				bMan.sendToRoom(roomNumber,"[DISCONNECT]"+userName);
			}catch(Exception e){}
		}
	}
}