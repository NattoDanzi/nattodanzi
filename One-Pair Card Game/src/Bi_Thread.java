import java.net.*;
import java.io.*;
import java.util.*;

// Ŭ���̾�Ʈ�� ����ϴ� ������ Ŭ����
class Bi_Thread extends Thread{
	private int roomNumber=-1;      // �� ��ȣ
	private int cnt =0;
	private String userName=null;  // ����� �̸�
	private Socket socket;              // ����
	private BManager bMan;		// �޽��� �����

	// ���� �غ� ����, true�̸� ������ ������ �غ� �Ǿ����� �ǹ��Ѵ�.
	private boolean ready=false;

	private BufferedReader reader;     // �Է� ��Ʈ��
	private PrintWriter writer;           // ��� ��Ʈ��
	Bi_Thread(Socket socket, BManager bMan){     // ������
		this.socket=socket;
		this.bMan = bMan;
	}
	Socket getSocket(){               // ������ ��ȯ�Ѵ�.
		return socket;
	}
	int getRoomNumber(){             // �� ��ȣ�� ��ȯ�Ѵ�.
		return roomNumber;
	}
	String getUserName(){             // ����� �̸��� ��ȯ�Ѵ�.
		return userName;
	}
	boolean isReady(){                 // �غ� ���¸� ��ȯ�Ѵ�.
		return ready;
	}
	public void run(){
		try{
		reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
		writer=new PrintWriter(socket.getOutputStream(), true);

		String msg;                     // Ŭ���̾�Ʈ�� �޽���
		while((msg=reader.readLine())!=null)
		{
			// msg�� "[NAME]"���� ���۵Ǵ� �޽����̸�
			if(msg.startsWith("[NAME]"))
			{
				userName=msg.substring(6);          // userName�� ���Ѵ�.
			}

			// msg�� "[ROOM]"���� ���۵Ǹ� �� ��ȣ�� ���Ѵ�.
			else if(msg.startsWith("[ROOM]"))
			{
				int roomNum=Integer.parseInt(msg.substring(6));
				if( !bMan.isFull(roomNum))             // ���� �� ���°� �ƴϸ�
				{
					// ���� ���� �ٸ� ��뿡�� ������� ������ �˸���.
					if(roomNumber!=-1)
						bMan.sendToOthers(this, "[EXIT]"+userName);

					// ������� �� �� ��ȣ�� �����Ѵ�.
					roomNumber=roomNum;

					// ����ڿ��� �޽����� �״�� �����Ͽ� ������ �� ������ �˸���.
					writer.println(msg);

					// ����ڿ��� �� �濡 �ִ� ����� �̸� ����Ʈ�� �����Ѵ�.
					writer.println(bMan.getNamesInRoom(roomNumber));

					// �� �濡 �ִ� �ٸ� ����ڿ��� ������� ������ �˸���.
					bMan.sendToOthers(this, "[ENTER]"+userName);
				}
				else writer.println("[FULL]");        // ����ڿ� ���� á���� �˸���.
			}

			else if(roomNumber>=1 && msg.startsWith("[CARD]")) //ī�� ������
				bMan.sendToOthers(this,msg);

			else if(msg.startsWith("[RESET]"))
				bMan.sendToOthers(this,msg);

			// ��ȭ �޽����� �濡 �����Ѵ�.
			else if(msg.startsWith("[MSG]"))
				bMan.sendToRoom(roomNumber, "["+userName+"]: "+msg.substring(5));

			// "[START]" �޽����̸�
			else if(msg.startsWith("[ENDGAME]"))
				ready=false;

			else if(msg.startsWith("[START]"))
			{
				ready=true;   // ������ ������ �غ� �Ǿ���.

				// �ٸ� ����ڵ� ������ ������ �غ� �Ǿ�����
				if(bMan.isReady(roomNumber))
				{
					// 1p2p�� ���ϰ� �����Ѵ�
					bMan.sendTo(0, "Player1");
					bMan.sendTo(0, "T");
					bMan.sendTo(1, "Player2");
					bMan.sendTo(1, "F");
				}
			}
			// ����ڰ� ������ ����ϴ� �޽����� ������
			else if(msg.startsWith("[DROPGAME]"))
			{
				ready=false;
				// ������� ������� ����� �˸���.
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
				System.out.println(userName+"���� ������ �������ϴ�.");
				System.out.println("������ ��: "+bMan.size());
				// ����ڰ� ������ �������� ���� �濡 �˸���.
				bMan.sendToRoom(roomNumber,"[DISCONNECT]"+userName);
			}catch(Exception e){}
		}
	}
}