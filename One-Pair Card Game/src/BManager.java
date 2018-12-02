import java.net.*;
import java.io.*;
import java.util.*;

// 메시지를 전달하는 클래스
class BManager extends Vector{
	BManager(){}
	void add(Bi_Thread ot){           // 스레드를 추가한다.
		super.add(ot);
	}
	void remove(Bi_Thread ot){        // 스레드를 제거한다.
		super.remove(ot);
	}
	Bi_Thread getOT(int i){            // i번째 스레드를 반환한다.
		return (Bi_Thread)elementAt(i);
	}
	Socket getSocket(int i){              // i번째 스레드의 소켓을 반환한다.
		return getOT(i).getSocket();
	}
	// i번째 스레드와 연결된 클라이언트에게 메시지를 전송한다.
	void sendTo(int i, String msg){
		try{
			PrintWriter pw= new PrintWriter(getSocket(i).getOutputStream(), true);
			pw.println(msg);
		}catch(Exception e){}
	}
	int getRoomNumber(int i){            // i번째 스레드의 방 번호를 반환한다.
		return getOT(i).getRoomNumber();
	}
	synchronized boolean isFull(int roomNum){    // 방이 찼는지 알아본다.
		if(roomNum==0)return false;                 // 대기실은 차지 않는다.

		// 다른 방은 2명 이상 입장할 수 없다.
		int count=0;
		for(int i=0;i<size();i++)
			if(roomNum==getRoomNumber(i))count++;
		if(count>=2)return true;
			return false;
	}
	// roomNum 방에 msg를 전송한다.
	void sendToRoom(int roomNum, String msg)
	{
		for(int i=0;i<size();i++)
		{
			if(roomNum==getRoomNumber(i))
				sendTo(i, msg);
		}
	}
	// ot와 같은 방에 있는 다른 사용자에게 msg를 전달한다.
	void sendToOthers(Bi_Thread ot, String msg)
	{
		for(int i=0;i<size();i++)
		{
			if(getRoomNumber(i)==ot.getRoomNumber() && getOT(i) != ot)
				sendTo(i, msg);
		}
	}

	// 게임을 시작할 준비가 되었는가를 반환한다.
	// 두 명의 사용자 모두 준비된 상태이면 true를 반환한다.
	synchronized boolean isReady(int roomNum)
	{
		int count=0;
		for(int i=0;i<size();i++)
		{
			if(roomNum==getRoomNumber(i) && getOT(i).isReady())
				count++;
		}
		if(count==2)
			return true;

		return false;
	}

	// roomNum방에 있는 사용자들의 이름을 반환한다.
	String getNamesInRoom(int roomNum)
	{
		StringBuffer sb=new StringBuffer("[PLAYERS]");
		for(int i=0;i<size();i++)
			if(roomNum==getRoomNumber(i))
				sb.append(getOT(i).getUserName()+"\t");
		return sb.toString();
	}
}