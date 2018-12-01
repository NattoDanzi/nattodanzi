package test4;

import java.util.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.geom.*;

import java.io.File;
import javax.sound.sampled.*;

public class Bicycle extends JFrame implements Runnable, ActionListener
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 780;
	public static final int HEIGHT = 700;

	private static final int EXTERNAL_BUFFER_SIZE = 1000;  //���� SIZE ����

	private int count = 0; 											//ī�� ��ȣ ī��Ʈ �ϴ� ����
	private int nCount = 1;											//ù�� ° ī������ �ι� ° ī������ �˻��ϴ� ����
	private int end=0;										//6�� �� ���߸� ������
	private int grade = 0; //����
	private int ograde= 0;

	private Container content;

	private JMenuItem m,m1;

	//���� �г�
	private JPanel topPanel =  new JPanel();
	//���� ��ư �г�
	private JPanel buttonPanel = new JPanel();

	private JPanel p		=	new JPanel(); ;//111111111111 �������� �г�
	private JPanel p2		=	new JPanel();
	private JPanel p3		=	new JPanel();
	private JPanel p2_1	=	new JPanel();


	private Card[] card		=	new Card[30];				//card�� ������ ������ Ŭ�����迭 ����
	private Card temp 		=	null; 							//card ������ ������ �ӽ� Ŭ���� ��ü
	private JButton black[] =	new JButton[30]; 		// ���� ��ư
	private JButton start;

	private ImageIcon cardIcon[]	= new ImageIcon[31]; //�̹��� ������ �迭
	private ImageIcon startIcon		= null;


	// 11111111111��������

	private TextArea msgView	=	new TextArea("", 1,1,1);   // �޽����� �����ִ� ����
	private TextField sendBox	=	new TextField("");         // ���� �޽����� ���� ����
	private TextField nameBox	=	new TextField();          // ����� �̸� ����
	private TextField roomBox	=	new TextField("0");        // �� ��ȣ ����

	private Label pInfo		=	new Label("����:  ��");
	private Label infoView	=	new Label("< ȯ���մϴ� >", 1);
	private Label info1P		=	new Label("���� ���� : "+grade+"��",1);
	private Label info2P		=	new Label("��� ���� : "+ograde+"��",1);

	private List pList				=	new List();  // ����� ����� �����ִ� ����Ʈ
	private Button startButton	=	new Button("���� ����");    // ���� ���� ��ư
	private Button stopButton	=	new Button("���");         // ��� ��ư
	private Button enterButton=	new Button("�����ϱ�");    // �����ϱ� ��ư
	private Button exitButton	=	new Button("���Ƿ�");      // ���Ƿ� ��ư

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private int roomNumber	 = -1;
	private String userName	 = null;
	private String Player			 = null;
	private String Add				 = null;// ���������ּ�
	private String msg			 = null;
	private boolean enable	 = false;//ī�� ������ �� ������
	private int a 						 = 0; //����� ī��迭 ���� �� ����� �ε���

	// �������� ���̾�α�
	private JDialog		dia			 =	new JDialog(this);
	private JTextField	ipField		 =	null;
	private JTextField	portField  	 =	null;
	private JButton		conButton =	null;



	public Bicycle()	//������
	{
		setSize(WIDTH,HEIGHT); //��ġ ���� ����
		setTitle(" One-Pair Card Game ");
		content = getContentPane();
		content.setBackground(Color.WHITE);
		content.setLayout(new BorderLayout());

		//�޴��� ���� �޼ҵ� ȣ��
		menubar();
		//���� �г�
		topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout());

		start = new JButton();
		startIcon = new ImageIcon("start.gif");
		start.setIcon(startIcon);
		start.addActionListener(this);
		start.setBorder(new BevelBorder(BevelBorder.RAISED));
		content.add(start);


		//1111111111111111111111

		p.setBackground(new Color(200,255,255));
		p.setLayout(new GridLayout(3,3));
		p.add(new Label("��     ��:", 2));p.add(nameBox);
		p.add(new Label("�� ��ȣ:", 2)); p.add(roomBox);
		p.add(enterButton); p.add(exitButton);
		enterButton.setEnabled(false);
		exitButton.setEnabled(false);
		p.setBounds(515,20, 250,70);

		p2.setBackground(new Color(255,255,100));
		p2.setLayout(new BorderLayout());
		p2_1.add(startButton); p2_1.add(stopButton);
		p2.add(pInfo,"North"); p2.add(pList,"Center"); p2.add(p2_1,"South");
		startButton.setEnabled(false); stopButton.setEnabled(false);
		p2.setBounds(515,100,250,180);

		p3.setLayout(new BorderLayout());
		p3.add(msgView,"Center");
		p3.add(sendBox, "South");
		p3.setBounds(515, 340, 250,250);


		info1P.setBounds(515,285,115,45);
		info1P.setBackground(new Color(255,100,100));
		info2P.setBounds(650,285,115,45);
		info2P.setBackground(new Color(100,100,255));

		infoView.setBounds(515,600,250,30);
		infoView.setBackground(new Color(200,200,255));

		content.add(infoView);
		content.add(info1P);
		content.add(info2P);
		content.add(p);
		content.add(p2);
		content.add(p3);
		content.add(topPanel,BorderLayout.CENTER);

		sendBox.addActionListener(this);
		enterButton.addActionListener(this);
		exitButton.addActionListener(this);
		startButton.addActionListener(this);
		stopButton.addActionListener(this);

		// �������� ���̾�α� �ʱ�ȭ ����

		dia = new JDialog(this);
		Container diacont = dia.getContentPane();
		dia.setTitle("���������Է�â");
		dia.setSize(300,125);
		diacont.setLayout(new GridLayout(3,1));

		JPanel ipPane = new JPanel();
		JPanel portPane = new JPanel();
		JPanel buttonPane = new JPanel();
		ipPane.setLayout(new BorderLayout());
		portPane.setLayout(new BorderLayout());
		buttonPane.setLayout(new FlowLayout());

		JLabel ipLabel = new JLabel("�ּ� : ");
		JLabel portLabel = new JLabel("��Ʈ : ");
		ipField = new JTextField("203.241.228.116");
		portField = new JTextField("7200");
		conButton = new JButton("�����ϱ�");

		conButton.addActionListener(this);

		ipPane.add(ipLabel,BorderLayout.WEST);
		ipPane.add(ipField,BorderLayout.CENTER);
		portPane.add(portLabel,BorderLayout.WEST);
		portPane.add(portField,BorderLayout.CENTER);
		buttonPane.add(conButton);

		diacont.add(ipPane);
		diacont.add(portPane);
		diacont.add(buttonPane);

		dia.setModal(true);
		// �������� ���̾�α� �ʱ�ȭ ��


		//�̹��� ������ �迭�� �׸� ����
		cardIconMake();
		//ī�� �Է� �޼ҵ� ȣ��
		randomCard();
		//��ư ���� �޼ҵ� ȣ��
		button();

		//��ư ���� �гο� �߰�.

	}//������ �ݱ�
	//1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111


	public void run()
	{
		try{
			while((msg=reader.readLine())!=null){
				if(msg.startsWith("[ROOM]")){ // �濡 ����
					if(!msg.equals("[ROOM]0")){ // ������ �ƴ� ���̸�
						enterButton.setEnabled(false);
						exitButton.setEnabled(true);
						infoView.setText(msg.substring(6)+"�� �濡 �����ϼ̽��ϴ�.");
					}
					else infoView.setText("���ǿ� �����ϼ̽��ϴ�.");
					roomNumber=Integer.parseInt(msg.substring(6));     // �� ��ȣ ����

					//if(board.isRunning()){ //���� ������
					//	board.stopGame(); //���� ����
				}
				else if(msg.startsWith("[FULL]")){ // ���� �� �� �����̸�
					infoView.setText("���� ���� ������ �� �����ϴ�.");
				}
				else if(msg.startsWith("[PLAYERS]")){ // �濡�ִ� ����� ���
					nameList(msg.substring(9));
				}
				else if(msg.startsWith("[ENTER]")){        // �մ� ����
					pList.add(msg.substring(7));
					playersInfo();
					msgView.append("["+ msg.substring(7)+"]���� �����Ͽ����ϴ�.\n");
				}
				else if(msg.startsWith("[EXIT]")){          // �մ� ����
					pList.remove(msg.substring(6));		// ����Ʈ���� ����
					playersInfo();                        			// �ο����� �ٽ� ����Ͽ� �����ش�.
					msgView.append("["+msg.substring(6)+"]���� �ٸ� ������ �����Ͽ����ϴ�.\n");
					if(roomNumber!=0) endGame("��밡 �������ϴ�.");
				}
				else if(msg.startsWith("[DISCONNECT]")){     // �մ� ���� ����
					pList.remove(msg.substring(12));
					playersInfo();
					msgView.append("["+msg.substring(12)+"]���� ������ �������ϴ�.\n");
					if(roomNumber!=0) endGame("��밡 �������ϴ�.");
				}
				else if(msg.startsWith("Player"))
				{
					Player = msg.toString();
					System.out.println(Player);	// Ȯ�ο�
				}

				else if(msg.startsWith("T"))
				{
					enable = true;
					infoView.setText("Play");
				}
				else if(msg.startsWith("F"))
				{
					enable = false;
					infoView.setText("Wait");
				}

				// 1P�� ���� ī��迭 �޴´�
				else if(msg.startsWith("[RESET]")){
					card[a] = new Card(); // ī�� �ʱ�ȭ
					card[a].setcNumber(a);

					card[a].setmark(Integer.parseInt(msg.substring(7)));
					a=a+1;
				}

				// ��밡 ����ϸ�
				else if(msg.startsWith("[DROPGAME]")){
					endGame("��밡 ����Ͽ����ϴ�.");
				}

				// ���漱���� ī�带 �����´�.
				else if(msg.startsWith("[CARD]")){
					int x=Integer.parseInt(msg.substring(6));
					black[x].setIcon(cardIcon[card[x].getmark()]);
					if(card[x].getbool() == false)															//���� ī�� Ŭ��
					{
						card[x].setbool(true);																	//ī�� �迭 ������ ���õ� ǥ��
						black[x].setBorder(new BevelBorder(BevelBorder.LOWERED));	//���õ� ī�� �������� ǥ��
						if(nCount++ == 1)	temp = card[x]; 												//ó�� Ŭ�� �� ��� temp�� ó�� ī�� �� ����
						else																										//�� �� Ŭ�� �� ���
						{
							nCount = 1;																						//���� ���� ó�� Ŭ�� �ϵ��� ����
							try
							{
								if(!(comparison(temp.getmark(), card[x].getmark())))			//�� ī�尡 ���� �ٸ��� ������� ����
								{
									card[temp.getcNumber()].setbool(false); 								//���� ǥ��
									card[x].setbool(false);
									//�� ���� ī�带 ��� ���� �ѵ� ���� �ð� �ڿ� �ݵ��� �ϴ� Ŭ���� ȣ��!
									 Thread t = new Thread(new Closer(black[temp.getcNumber()], black[x], cardIcon[30]));
									 t.start();

									black[temp.getcNumber()].setBorder(new BevelBorder(BevelBorder.RAISED)) ;//ù ��° ī�� �簢ó��
									black[x].setBorder(new BevelBorder(BevelBorder.RAISED));								//�� ��° ī�� �簢ó��
								//	enable=false; // ������ Ʋ���� �� �� ����

								}
								else																						//�� ī�尡 ������ ����ϱ�
								{
									ograde++;
									info2P.setText("��� ���� : "+ograde+"��");
									if(++end == 15)
									{//15�� �� ���� ���
										if(ograde < 8){
											JOptionPane.showMessageDialog(null, "Winner!!!");
											endGame("Winner!!!");
										}
										else{
											JOptionPane.showMessageDialog(null, "Loser..");
											endGame("Loser..");
										}
									}
								}
							}catch(Exception ae){}
						}
					}
				}
				else msgView.append(msg+"\n"); // ��ӵ� �޽����� ������ ������
			}
		}catch(IOException ie){
			msgView.append(ie+"\n");
		}
	}
	//11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
	//�׼� ����
	public void actionPerformed(ActionEvent e)
	{

		String com =  e.getActionCommand();


		//������ �ڽ� �׼�
		if(e.getSource() == sendBox){ // �޽��� �Է»���
			String msg=sendBox.getText();
			if(msg.length()==0) return;
			if(msg.length()>=30) msg=msg.substring(0,30);
			try{
				writer.println("[MSG]"+msg);
				sendBox.setText("");
			}catch(Exception ie){}
		}
		else if(e.getSource() == enterButton){ // ���� ��ư
			try{
				if(Integer.parseInt(roomBox.getText())<1){
					infoView.setText("���ȣ�� �߸��Ǿ����ϴ�. 1�̻�");
					return;
				}
				writer.println("[ROOM]"+Integer.parseInt(roomBox.getText()));
				msgView.setText("");
				}catch(Exception ie){
					infoView.setText("�Է��Ͻ� ���׿� ������ �ҽ��ϴ�.");
				}
			}
		else if(e.getSource() == exitButton){ // ���Ƿ�
			try{
				goToWaitRoom();
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
			}catch(Exception ie){// e
			}
		}
		else if(e.getSource() == startButton){ // ���ӽ���
			try{
				writer.println("[START]");
				infoView.setText("����� ������ ��ٸ��ϴ�.");
				startButton.setEnabled(false);
				}catch(Exception ie){//e
				}
			}
		else if(e.getSource() == stopButton){
			try{
				writer.println("[DROPGAME]");
				endGame("����Ͽ����ϴ�.");
				}catch(Exception ie){
				}
			}
		///////// �޴� �׼�

		if(com.equals("��������")){ //�������� //��ŸƮ ��ư ���� �ٶ�
			dia.show(true);
		}

		else if(com.equals("�����ϱ�"))
		{
			dia.show(false);
			connect();
		}
		//EXIT
		if(com.equals("����")) System.exit(0); //����

		//���õ� ī�� ã��
		else
		{
			if(enable == true){ // ���� ���� ��
				for(count = 0; count <30; count++){
					if(e.getSource() == black[count])
					{
						writer.println("[CARD]"+count); //������ ī�带 ��������� ������.
						break;
					}
				}

				black[count].setIcon(cardIcon[card[count].getmark()]);						//���� �� ī�� ������
				if(card[count].getbool() == false)															//���� ī�� Ŭ��
				{
					card[count].setbool(true);																	//ī�� �迭 ������ ���õ� ǥ��
					black[count].setBorder(new BevelBorder(BevelBorder.LOWERED));	//���õ� ī�� �������� ǥ��
					if(nCount++ == 1)	temp = card[count]; 												//ó�� Ŭ�� �� ��� temp�� ó�� ī�� �� ����
					else																										//�� �� Ŭ�� �� ���
					{
						nCount = 1;																						//���� ���� ó�� Ŭ�� �ϵ��� ����
						try
						{
							if(!(comparison(temp.getmark(), card[count].getmark())))			//�� ī�尡 ���� �ٸ��� ������� ����
							{
								card[temp.getcNumber()].setbool(false); 								//���� ǥ��
								card[count].setbool(false);

								//�� ���� ī�带 ��� ���� �ѵ� ���� �ð� �ڿ� �ݵ��� �ϴ� Ŭ���� ȣ��!
								 Thread t = new Thread(new Closer(black[temp.getcNumber()], black[count], cardIcon[30]));
								 t.start();

								black[temp.getcNumber()].setBorder(new BevelBorder(BevelBorder.RAISED)) ;//ù ��° ī�� �簡ó��
								black[count].setBorder(new BevelBorder(BevelBorder.RAISED));						//�� ��° ī�� �簢ó��

								// Server �� �޽��� ���� ( ���� + Ʋ�ȴ� )
								writer.println(Player + "wrong");
								System.out.println(Player+"wrong");
							}
							else // ���� ���
							{
								grade++;
								info1P.setText("���� ���� : "+grade+"��");
								if(++end == 15) //15�� �� ���� ���
								{
									if(grade > 7){
										JOptionPane.showMessageDialog(null, "Winner!!!");
										endGame("Winner!!!");
									}
									else{
										JOptionPane.showMessageDialog(null, "Loser..");
										endGame("Loser..");
									}
								}
							}
						}catch(Exception ae){}
					}
				}
			}
		}



	}//actionPerformed �ݱ�

	//1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111


	void goToWaitRoom(){
		if(userName==null){
			String name=nameBox.getText().trim();
			if(name.length()<=2 || name.length()>10){
				infoView.setText("�̸��� �߸��Ǿ����ϴ�. 3~10��");
				nameBox.requestFocus();
				return;
			}

			userName=name;
			writer.println("[NAME]"+userName);
			nameBox.setText(userName);
			nameBox.setEditable(false);
		}
		msgView.setText("");
		writer.println("[ROOM]0");
		infoView.setText("���ǿ� �����ϼ̽��ϴ�.");
		roomBox.setText("0");
		enterButton.setEnabled(true);
		exitButton.setEnabled(false);
	}


	private void endGame(String msg){ //���� ��������
		enable = false; // ī�� Ŭ�� ����
		grade=0;
		ograde=0;
		end=0;
		info2P.setText("��� ���� : "+ograde+"��");
		info1P.setText("���� ���� : "+grade+"��");
		for(int i = 0; i <30; i++)			//card�迭 ���� �� �ʱ�ȭ
		{
			int num= i % 15 + 1;
			card[i] = new Card();
			card[i].setcNumber(i);
			card[i].setmark(num);
			black[i].setIcon(cardIcon[30]); //ī�� ����
			black[i].setBorder(new BevelBorder(BevelBorder.RAISED)); //ī�� �簢ó��
		}
		infoView.setText(msg);
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		writer.println("[ENDGAME]");
		try{ Thread.sleep(2000); }catch(Exception e){}

		//if(board.isRunning()) board.stopGame(); //��������
		if(pList.getItemCount()==2) startButton.setEnabled(true);
	}

	private void nameList(String msg){ // ����� ����Ʈ���� ����� ���� �� pList�� �߰�
		pList.removeAll();
		StringTokenizer st=new StringTokenizer(msg, "\t");
		while(st.hasMoreElements()) pList.add(st.nextToken());

		playersInfo();
	}

	private void playersInfo(){ // �濡�ִ� ������ ��
		int count=pList.getItemCount();
		if(roomNumber==0) pInfo.setText("����: "+count+"��");
		else pInfo.setText(roomNumber+" �� ��: "+count+"��");

		if(count==2 && roomNumber!=0) startButton.setEnabled(true); // ���ӽ��� Ȱ��ȭ ����
		else startButton.setEnabled(false);
	}
	//111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111

	//����
	public static void main(String[] args)
	{
		Bicycle bi = new Bicycle();
		bi.setResizable(false);
		bi.setVisible(true);
	}
	//111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111

	// ����
	private void connect()
	{
		try{
			msgView.append("������ ������ ��û�մϴ�.\n");
			socket=new Socket(ipField.getText(), Integer.parseInt(portField.getText()));
			msgView.append("---���� ����--.\n");
			msgView.append("�̸��� �Է��ϰ� ���Ƿ� �����ϼ���.\n");
			exitButton.setEnabled(true); //���� ��ư Ȱ��ȭ
			m.setEnabled(false);
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream(), true);
			new Thread(this).start();
			//board.setWriter(writer);
			}catch(Exception e)
			{
				msgView.append(e+"\n\n���� ����..\n");
			}

		}

	//�޴��� ���� �޼ҵ�
	public void menubar()
	{
		JMenu menu = new JMenu("Menu");


		m = new JMenuItem("��������");
		m.addActionListener(this);
		menu.add(m);

		m1 = new JMenuItem("����");
		m1.addActionListener(this);
		menu.add(m1);

		JMenuBar mBar = new JMenuBar();
		mBar.add(menu);
		setJMenuBar(mBar);
	}

	//��ư �迭 ���� �޼ҵ�
	public void button()
	{
		for(int a=0; a<30; a++)
		{
			black[a] = new JButton();
			black[a].setIcon(cardIcon[30]);
			black[a].addActionListener(this);
			buttonPanel.add(black[a]);
			black[a].setBorder(new BevelBorder(BevelBorder.RAISED));
		}
		buttonPanel.setLayout(new GridLayout(6, 5, 5, 10));
		buttonPanel.setBorder(new LineBorder(Color.WHITE,10));
		content.add(buttonPanel, BorderLayout.WEST);
	}

	//card�迭�� ���� �� ���� �� �Է� �޼ҵ�
	public void randomCard()
	{
		for(int i = 0; i <30; i++)			//card�迭 ���� �� �ʱ�ȭ
		{
			int num= i % 15 + 1;
			card[i] = new Card();
			card[i].setcNumber(i);
		//	card[i].setmark(num);
		}
		card[0].setmark(0);
		card[1].setmark(8);
		card[2].setmark(4);
		card[3].setmark(8);
		card[4].setmark(2);
		card[5].setmark(2);
		card[6].setmark(4);
		card[7].setmark(0);
		card[8].setmark(1);
		card[9].setmark(13);
		card[10].setmark(14);
		card[11].setmark(11);
		card[12].setmark(11);
		card[13].setmark(3);
		card[14].setmark(14);
		card[15].setmark(3);
		card[16].setmark(10);
		card[17].setmark(5);
		card[18].setmark(6);
		card[19].setmark(5);
		card[20].setmark(9);
		card[21].setmark(10);
		card[22].setmark(6);
		card[23].setmark(1);
		card[24].setmark(7);
		card[25].setmark(13);
		card[26].setmark(7);
		card[27].setmark(9);
		card[28].setmark(12);
		card[29].setmark(12);
		/*
		for(int i = 0; i<5; i++)
		{
			int temp, num;
			num = (int)(Math.random()*15)+15;
			temp = card[num].getmark();
			card[num].setmark(card[i].getmark());
			card[i].setmark(temp);
		}*/

	}

	//ù ��° ī��� �� ��° ī���� mark�� �� �޼ҵ�
	public boolean comparison(int a, int b)
	{
		if(a == b) return true;
			return false;
	}

	//�̹����迭 ���� & ���� �޼ҵ�
	public void cardIconMake()
	{
		cardIcon[0] = new ImageIcon("card0.jpg");
		cardIcon[1] = new ImageIcon("card1.jpg");
		cardIcon[2] = new ImageIcon("card2.jpg");
		cardIcon[3] = new ImageIcon("card3.jpg");
		cardIcon[4] = new ImageIcon("card4.jpg");
		cardIcon[5] = new ImageIcon("card5.jpg");
		cardIcon[6] = new ImageIcon("card6.jpg");
		cardIcon[7] = new ImageIcon("card7.jpg");
		cardIcon[8] = new ImageIcon("card8.jpg");
		cardIcon[9] = new ImageIcon("card9.jpg");
		cardIcon[10] = new ImageIcon("card10.jpg");
		cardIcon[11] = new ImageIcon("card11.jpg");
		cardIcon[12] = new ImageIcon("card12.jpg");
		cardIcon[13] = new ImageIcon("card13.jpg");
		cardIcon[14] = new ImageIcon("card14.jpg");

		cardIcon[15] = new ImageIcon("card14.jpg");
		cardIcon[16] = new ImageIcon("card3.jpg");
		cardIcon[17] = new ImageIcon("card10.jpg");
		cardIcon[18] = new ImageIcon("card4.jpg");
		cardIcon[19] = new ImageIcon("card13.jpg");
		cardIcon[20] = new ImageIcon("card5.jpg");
		cardIcon[21] = new ImageIcon("card6.jpg");
		cardIcon[22] = new ImageIcon("card12.jpg");
		cardIcon[23] = new ImageIcon("card7.jpg");
		cardIcon[24] = new ImageIcon("card11.jpg");
		cardIcon[25] = new ImageIcon("card2.jpg");
		cardIcon[26] = new ImageIcon("card1.jpg");
		cardIcon[27] = new ImageIcon("card8.jpg");
		cardIcon[28] = new ImageIcon("card9.jpg");
		cardIcon[29] = new ImageIcon("card0.jpg");

		cardIcon[30] = new ImageIcon("card30.jpg");
	}

/*
	//RESET �޼ҵ�
	public void reset()
	{
		nCount =1; 		//ù�� ° ���� �� ��° ���� �����ϴ� ���� �ʱ�ȭ
		end = count =0; 			//¦ �˻��ϴ� ���� �ʱ�ȭ

		for(int i = 0; i <30; i++)			//card�迭 ���� �� �ʱ�ȭ
		{
			int num= i % 15 + 1;
			card[i] = new Card();
			card[i].setcNumber(i);
			card[i].setmark(num);
			black[i].setIcon(cardIcon[30]); //ī�� ����
			black[i].setBorder(new BevelBorder(BevelBorder.RAISED)); //ī�� �簢ó��
		}
		for(int i = 0; i<100; i++)
		{
			int temp, num;
			num = (int)(Math.random()*15)+15;
			temp = card[num].getmark();
			card[num].setmark(card[i].getmark());
			card[i].setmark(temp);
		}
	}
	*/
}//Ŭ���� �ݱ�