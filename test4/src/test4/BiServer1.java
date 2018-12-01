package test4;

import java.net.*;
import java.io.*;
import java.util.*;
public class BiServer1{
	private ServerSocket server;
	private BManager bMan=new BManager();   // �޽��� �����

	public BiServer1(){}
	void startServer()                         // ������ �����Ѵ�.
	{
		try{
			server=new ServerSocket(8891);
			System.out.println("���������� �����Ǿ����ϴ�.");
			while(true){

				// Ŭ���̾�Ʈ�� ����� �����带 ��´�.
				Socket socket=server.accept();

				// �����带 ����� �����Ų��.
				Bi_Thread ot = new Bi_Thread(socket, bMan);
				ot.start();

				// bMan�� �����带 �߰��Ѵ�.
				bMan.add(ot);

				System.out.println("������ ��: "+bMan.size());
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void main(String[] args){
		BiServer1 server=new BiServer1();
		server.startServer();
	}
}