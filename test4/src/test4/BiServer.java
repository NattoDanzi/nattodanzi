package test4;

import java.net.*;
import java.io.*;
import java.util.*;
public class BiServer{
	private ServerSocket server;
	private BManager bMan=new BManager();   // �޽��� �����
	private static BiServer biserver = new BiServer();

	public BiServer(){}
	void startServer()                         // ������ �����Ѵ�.
	{
		try{
			server=new ServerSocket(8890);
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

	public static BiServer getInstance()
	{
		return  biserver ;
	}
	public static void main(String[] args){
		BiServer server= biserver.getInstance();
		server.startServer();
	}
}