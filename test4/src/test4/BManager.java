package test4;

import java.net.*;
import java.io.*;
import java.util.*;

// �޽����� �����ϴ� Ŭ����
class BManager extends Vector{
	BManager(){}
	void add(Bi_Thread ot){           // �����带 �߰��Ѵ�.
		super.add(ot);
	}
	void remove(Bi_Thread ot){        // �����带 �����Ѵ�.
		super.remove(ot);
	}
	Bi_Thread getOT(int i){            // i��° �����带 ��ȯ�Ѵ�.
		return (Bi_Thread)elementAt(i);
	}
	Socket getSocket(int i){              // i��° �������� ������ ��ȯ�Ѵ�.
		return getOT(i).getSocket();
	}
	// i��° ������� ����� Ŭ���̾�Ʈ���� �޽����� �����Ѵ�.
	void sendTo(int i, String msg){
		try{
			PrintWriter pw= new PrintWriter(getSocket(i).getOutputStream(), true);
			pw.println(msg);
		}catch(Exception e){}
	}
	int getRoomNumber(int i){            // i��° �������� �� ��ȣ�� ��ȯ�Ѵ�.
		return getOT(i).getRoomNumber();
	}
	synchronized boolean isFull(int roomNum){    // ���� á���� �˾ƺ���.
		if(roomNum==0)return false;                 // ������ ���� �ʴ´�.

		// �ٸ� ���� 2�� �̻� ������ �� ����.
		int count=0;
		for(int i=0;i<size();i++)
			if(roomNum==getRoomNumber(i))count++;
		if(count>=2)return true;
			return false;
	}
	// roomNum �濡 msg�� �����Ѵ�.
	void sendToRoom(int roomNum, String msg)
	{
		for(int i=0;i<size();i++)
		{
			if(roomNum==getRoomNumber(i))
				sendTo(i, msg);
		}
	}
	// ot�� ���� �濡 �ִ� �ٸ� ����ڿ��� msg�� �����Ѵ�.
	void sendToOthers(Bi_Thread ot, String msg)
	{
		for(int i=0;i<size();i++)
		{
			if(getRoomNumber(i)==ot.getRoomNumber() && getOT(i) != ot)
				sendTo(i, msg);
		}
	}

	// ������ ������ �غ� �Ǿ��°��� ��ȯ�Ѵ�.
	// �� ���� ����� ��� �غ�� �����̸� true�� ��ȯ�Ѵ�.
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

	// roomNum�濡 �ִ� ����ڵ��� �̸��� ��ȯ�Ѵ�.
	String getNamesInRoom(int roomNum)
	{
		StringBuffer sb=new StringBuffer("[PLAYERS]");
		for(int i=0;i<size();i++)
			if(roomNum==getRoomNumber(i))
				sb.append(getOT(i).getUserName()+"\t");
		return sb.toString();
	}
}