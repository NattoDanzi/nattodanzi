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

	public static final int WIDTH = 780;
	public static final int HEIGHT = 700;

	private static final int EXTERNAL_BUFFER_SIZE = 1000;  //버퍼 SIZE 지정

	private int count = 0; 											//카드 번호 카운트 하는 변수
	private int nCount = 1;											//첫번 째 카드인지 두번 째 카드인지 검사하는 변수
	private int end=0;										//6쌍 다 마추면 끝내기
	private int grade = 0; //점수
	private int ograde= 0;

	private Container content;

	private JMenuItem m,m1;

	//상위 패널
	private JPanel topPanel =  new JPanel();
	//하위 버튼 패널
	private JPanel buttonPanel = new JPanel();

	private JPanel p		=	new JPanel(); ;//111111111111 서버관련 패널
	private JPanel p2		=	new JPanel();
	private JPanel p3		=	new JPanel();
	private JPanel p2_1	=	new JPanel();


	private Card[] card		=	new Card[30];				//card의 정보를 가지는 클래스배열 선언
	private Card temp 		=	null; 							//card 정보를 복사할 임시 클래스 객체
	private JButton black[] =	new JButton[30]; 		// 블랙덱 버튼
	private JButton start;

	private ImageIcon cardIcon[]	= new ImageIcon[31]; //이미지 아이콘 배열
	private ImageIcon startIcon		= null;


	// 11111111111서버관련

	private TextArea msgView	=	new TextArea("", 1,1,1);   // 메시지를 보여주는 영역
	private TextField sendBox	=	new TextField("");         // 보낼 메시지를 적는 상자
	private TextField nameBox	=	new TextField();          // 사용자 이름 상자
	private TextField roomBox	=	new TextField("0");        // 방 번호 상자

	private Label pInfo		=	new Label("대기실:  명");
	private Label infoView	=	new Label("< 자바 8조 프로젝트 >", 1);
	private Label info1P		=	new Label("나의 점수 : "+grade+"점",1);
	private Label info2P		=	new Label("상대 점수 : "+ograde+"점",1);

	private List pList				=	new List();  // 사용자 명단을 보여주는 리스트
	private Button startButton	=	new Button("게임 시작");    // 게임 시작 버튼
	private Button stopButton	=	new Button("기권");         // 기권 버튼
	private Button enterButton=	new Button("입장하기");    // 입장하기 버튼
	private Button exitButton	=	new Button("대기실로");      // 대기실로 버튼

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private int roomNumber	 = -1;
	private String userName	 = null;
	private String Player			 = null;
	private String Add				 = null;// 서버접속주소
	private String msg			 = null;
	private boolean enable	 = false;//카드 뒤집을 수 있을지
	private int a 						 = 0; //상대편 카드배열 받을 때 사용할 인덱스

	// 서버연결 다이얼로그
	private JDialog		dia			 =	new JDialog(this);
	private JTextField	ipField		 =	null;
	private JTextField	portField  	 =	null;
	private JButton		conButton =	null;



	public Bicycle()	//생성자
	{
		setSize(WIDTH,HEIGHT); //위치 임의 지정
		setTitle(" One-Pair Card Game ");
		content = getContentPane();
		content.setBackground(Color.WHITE);
		content.setLayout(new BorderLayout());

		//메뉴바 생성 메소드 호출
		menubar();
		//상위 패널
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
		p.add(new Label("이     름:", 2));p.add(nameBox);
		p.add(new Label("방 번호:", 2)); p.add(roomBox);
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

		// 서버연결 다이얼로그 초기화 시작

		dia = new JDialog(this);
		Container diacont = dia.getContentPane();
		dia.setTitle("연결정보입력창");
		dia.setSize(300,125);
		diacont.setLayout(new GridLayout(3,1));

		JPanel ipPane = new JPanel();
		JPanel portPane = new JPanel();
		JPanel buttonPane = new JPanel();
		ipPane.setLayout(new BorderLayout());
		portPane.setLayout(new BorderLayout());
		buttonPane.setLayout(new FlowLayout());

		JLabel ipLabel = new JLabel("주소 : ");
		JLabel portLabel = new JLabel("포트 : ");
		ipField = new JTextField("203.241.249.59");
		portField = new JTextField("7777");
		conButton = new JButton("연결하기");

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
		// 서버연결 다이얼로그 초기화 끝


		//이미지 아이콘 배열에 그림 설정
		cardIconMake();
		//카드 입력 메소드 호출
		randomCard();
		//버튼 생성 메소드 호출
		button();

		//버튼 하위 패널에 추가.

	}//생성자 닫기
	//1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111


	public void run()
	{
		try{
			while((msg=reader.readLine())!=null){
				if(msg.startsWith("[ROOM]")){ // 방에 입장
					if(!msg.equals("[ROOM]0")){ // 대기실이 아닌 방이면
						enterButton.setEnabled(false);
						exitButton.setEnabled(true);
						infoView.setText(msg.substring(6)+"번 방에 입장하셨습니다.");
					}
					else infoView.setText("대기실에 입장하셨습니다.");
					roomNumber=Integer.parseInt(msg.substring(6));     // 방 번호 지정

					//if(board.isRunning()){ //게임 진행중
					//	board.stopGame(); //게임 정지
				}
				else if(msg.startsWith("[FULL]")){ // 방이 꽉 찬 상태이면
					infoView.setText("방이 차서 입장할 수 없습니다.");
				}
				else if(msg.startsWith("[PLAYERS]")){ // 방에있는 사용자 명단
					nameList(msg.substring(9));
				}
				else if(msg.startsWith("[ENTER]")){        // 손님 입장
					pList.add(msg.substring(7));
					playersInfo();
					msgView.append("["+ msg.substring(7)+"]님이 입장하였습니다.\n");
				}
				else if(msg.startsWith("[EXIT]")){          // 손님 퇴장
					pList.remove(msg.substring(6));		// 리스트에서 제거
					playersInfo();                        			// 인원수를 다시 계산하여 보여준다.
					msgView.append("["+msg.substring(6)+"]님이 다른 방으로 입장하였습니다.\n");
					if(roomNumber!=0) endGame("상대가 나갔습니다.");
				}
				else if(msg.startsWith("[DISCONNECT]")){     // 손님 접속 종료
					pList.remove(msg.substring(12));
					playersInfo();
					msgView.append("["+msg.substring(12)+"]님이 접속을 끊었습니다.\n");
					if(roomNumber!=0) endGame("상대가 나갔습니다.");
				}
				else if(msg.startsWith("Player"))
				{
					Player = msg.toString();
					System.out.println(Player);	// 확인용
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

				// 1P로 부터 카드배열 받는다
				else if(msg.startsWith("[RESET]")){
					card[a] = new Card(); // 카드 초기화
					card[a].setcNumber(a);

					card[a].setmark(Integer.parseInt(msg.substring(7)));
					a=a+1;
				}

				// 상대가 기권하면
				else if(msg.startsWith("[DROPGAME]")){
					endGame("상대가 기권하였습니다.");
				}

				// 상대방선택한 카드를 뒤집는다.
				else if(msg.startsWith("[CARD]")){
					int x=Integer.parseInt(msg.substring(6));
					black[x].setIcon(cardIcon[card[x].getmark()]);
					if(card[x].getbool() == false)															//덮힌 카드 클릭
					{
						card[x].setbool(true);																	//카드 배열 정보에 선택됨 표시
						black[x].setBorder(new BevelBorder(BevelBorder.LOWERED));	//선택된 카드 음각으로 표시
						if(nCount++ == 1)	temp = card[x]; 												//처음 클릭 할 경우 temp에 처음 카드 값 저장
						else																										//두 번 클릭 할 경우
						{
							nCount = 1;																						//다음 번에 처음 클릭 하도록 설정
							try
							{
								if(!(comparison(temp.getmark(), card[x].getmark())))			//두 카드가 서로 다르면 원래대로 덮기
								{
									card[temp.getcNumber()].setbool(false); 								//덮힘 표시
									card[x].setbool(false);
									//두 장의 카드를 모드 오픈 한뒤 일정 시간 뒤에 닫도록 하는 클래스 호출!
									 Thread t = new Thread(new Closer(black[temp.getcNumber()], black[x], cardIcon[30]));
									 t.start();

									black[temp.getcNumber()].setBorder(new BevelBorder(BevelBorder.RAISED)) ;//첫 번째 카드 양각처리
									black[x].setBorder(new BevelBorder(BevelBorder.RAISED));								//두 번째 카드 양각처리
								//	enable=false; // 상대방이 틀렸을 때 내 차례

								}
								else																						//두 카드가 같으면 계속하기
								{
									ograde++;
									info2P.setText("상대 점수 : "+ograde+"점");
									if(++end == 15)
									{//15쌍 다 맞출 경우
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
				else msgView.append(msg+"\n"); // 약속된 메시지를 영역에 보여줌
			}
		}catch(IOException ie){
			msgView.append(ie+"\n");
		}
	}
	//11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111
	//액션 정의
	public void actionPerformed(ActionEvent e)
	{

		String com =  e.getActionCommand();


		//오른쪽 박스 액션
		if(e.getSource() == sendBox){ // 메시지 입력상자
			String msg=sendBox.getText();
			if(msg.length()==0) return;
			if(msg.length()>=30) msg=msg.substring(0,30);
			try{
				writer.println("[MSG]"+msg);
				sendBox.setText("");
			}catch(Exception ie){}
		}
		else if(e.getSource() == enterButton){ // 입장 버튼
			try{
				if(Integer.parseInt(roomBox.getText())<1){
					infoView.setText("방번호가 잘못되었습니다. 1이상");
					return;
				}
				writer.println("[ROOM]"+Integer.parseInt(roomBox.getText()));
				msgView.setText("");
				}catch(Exception ie){
					infoView.setText("입력하신 사항에 오류가 았습니다.");
				}
			}
		else if(e.getSource() == exitButton){ // 대기실로
			try{
				goToWaitRoom();
				startButton.setEnabled(false);
				stopButton.setEnabled(false);
			}catch(Exception ie){// e
			}
		}
		else if(e.getSource() == startButton){ // 게임시작
			try{
				writer.println("[START]");
				infoView.setText("상대의 결정을 기다립니다.");
				startButton.setEnabled(false);
				}catch(Exception ie){//e
				}
			}
		else if(e.getSource() == stopButton){
			try{
				writer.println("[DROPGAME]");
				endGame("기권하였습니다.");
				}catch(Exception ie){
				}
			}
		///////// 메뉴 액션

		if(com.equals("서버연결")){ //서버연결 //스타트 버튼 수정 바람
			dia.show(true);
		}

		else if(com.equals("연결하기"))
		{
			dia.show(false);
			connect();
		}
		//EXIT
		if(com.equals("종료")) System.exit(0); //종료

		//선택된 카드 찾기
		else
		{
			if(enable == true){ // 나의 턴일 때
				for(count = 0; count <30; count++){
					if(e.getSource() == black[count])
					{
						writer.println("[CARD]"+count); //선택한 카드를 상대편으로 보낸다.
						break;
					}
				}

				black[count].setIcon(cardIcon[card[count].getmark()]);						//선택 된 카드 뒤집기
				if(card[count].getbool() == false)															//덮힌 카드 클릭
				{
					card[count].setbool(true);																	//카드 배열 정보에 선택됨 표시
					black[count].setBorder(new BevelBorder(BevelBorder.LOWERED));	//선택된 카드 음각으로 표시
					if(nCount++ == 1)	temp = card[count]; 												//처음 클릭 할 경우 temp에 처음 카드 값 저장
					else																										//두 번 클릭 할 경우
					{
						nCount = 1;																						//다음 번에 처음 클릭 하도록 설정
						try
						{
							if(!(comparison(temp.getmark(), card[count].getmark())))			//두 카드가 서로 다르면 원래대로 덮기
							{
								card[temp.getcNumber()].setbool(false); 								//덮힘 표시
								card[count].setbool(false);

								//두 장의 카드를 모드 오픈 한뒤 일정 시간 뒤에 닫도록 하는 클래스 호출!
								 Thread t = new Thread(new Closer(black[temp.getcNumber()], black[count], cardIcon[30]));
								 t.start();

								black[temp.getcNumber()].setBorder(new BevelBorder(BevelBorder.RAISED)) ;//첫 번째 카드 양가처리
								black[count].setBorder(new BevelBorder(BevelBorder.RAISED));						//두 번째 카드 양각처리

								// Server 로 메시지 전송 ( 내가 + 틀렸다 )
								writer.println(Player + "wrong");
								System.out.println(Player+"wrong");
							}
							else // 맞출 경우
							{
								grade++;
								info1P.setText("나의 점수 : "+grade+"점");
								if(++end == 15) //15쌍 다 맞출 경우
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



	}//actionPerformed 닫기

	//1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111


	void goToWaitRoom(){
		if(userName==null){
			String name=nameBox.getText().trim();
			if(name.length()<=2 || name.length()>10){
				infoView.setText("이름이 잘못되었습니다. 3~10자");
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
		infoView.setText("대기실에 입장하셨습니다.");
		roomBox.setText("0");
		enterButton.setEnabled(true);
		exitButton.setEnabled(false);
	}


	private void endGame(String msg){ //게임 끝났을때
		enable = false; // 카드 클릭 금지
		grade=0;
		ograde=0;
		end=0;
		info2P.setText("상대 점수 : "+ograde+"점");
		info1P.setText("나의 점수 : "+grade+"점");
		for(int i = 0; i <30; i++)			//card배열 생성 및 초기화
		{
			int num= i % 15 + 1;
			card[i] = new Card();
			card[i].setcNumber(i);
			card[i].setmark(num);
			black[i].setIcon(cardIcon[30]); //카드 덮기
			black[i].setBorder(new BevelBorder(BevelBorder.RAISED)); //카드 양각처리
		}
		infoView.setText(msg);
		startButton.setEnabled(false);
		stopButton.setEnabled(false);
		writer.println("[ENDGAME]");
		try{ Thread.sleep(2000); }catch(Exception e){}

		//if(board.isRunning()) board.stopGame(); //게임중지
		if(pList.getItemCount()==2) startButton.setEnabled(true);
	}

	private void nameList(String msg){ // 사용자 리스트에서 사용자 추출 후 pList에 추가
		pList.removeAll();
		StringTokenizer st=new StringTokenizer(msg, "\t");
		while(st.hasMoreElements()) pList.add(st.nextToken());

		playersInfo();
	}

	private void playersInfo(){ // 방에있는 접속자 수
		int count=pList.getItemCount();
		if(roomNumber==0) pInfo.setText("대기실: "+count+"명");
		else pInfo.setText(roomNumber+" 번 방: "+count+"명");

		if(count==2 && roomNumber!=0) startButton.setEnabled(true); // 게임시작 활성화 점검
		else startButton.setEnabled(false);
	}
	//111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111

	//메인
	public static void main(String[] args)
	{
		Bicycle bi = new Bicycle();
		bi.setResizable(false);
		bi.setVisible(true);
	}
	//111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111

	// 접속
	private void connect()
	{
		try{
			msgView.append("서버에 연결을 요청합니다.\n");
			socket=new Socket(ipField.getText(), Integer.parseInt(portField.getText()));
			msgView.append("---연결 성공--.\n");
			msgView.append("이름을 입력하고 대기실로 입장하세요.\n");
			exitButton.setEnabled(true); //대기실 버튼 활성화
			m.setEnabled(false);
			reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer=new PrintWriter(socket.getOutputStream(), true);
			new Thread(this).start();
			//board.setWriter(writer);
			}catch(Exception e)
			{
				msgView.append(e+"\n\n연결 실패..\n");
			}

		}

	//메뉴바 생성 메소드
	public void menubar()
	{
		JMenu menu = new JMenu("Menu");


		m = new JMenuItem("서버연결");
		m.addActionListener(this);
		menu.add(m);

		m1 = new JMenuItem("종료");
		m1.addActionListener(this);
		menu.add(m1);

		JMenuBar mBar = new JMenuBar();
		mBar.add(menu);
		setJMenuBar(mBar);
	}

	//버튼 배열 생성 메소드
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

	//card배열에 생성 및 랜덤 값 입력 메소드
	public void randomCard()
	{
		for(int i = 0; i <30; i++)			//card배열 생성 및 초기화
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

	//첫 번째 카드와 두 번째 카드의 mark값 비교 메소드
	public boolean comparison(int a, int b)
	{
		if(a == b) return true;
			return false;
	}

	//이미지배열 생성 & 저장 메소드
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

		cardIcon[30] = new ImageIcon("card30.gif");
	}

/*
	//RESET 메소드
	public void reset()
	{
		nCount =1; 		//첫번 째 인지 두 번째 인지 조사하는 변수 초기화
		end = count =0; 			//짝 검사하는 변수 초기화

		for(int i = 0; i <30; i++)			//card배열 생성 및 초기화
		{
			int num= i % 15 + 1;
			card[i] = new Card();
			card[i].setcNumber(i);
			card[i].setmark(num);
			black[i].setIcon(cardIcon[30]); //카드 덮기
			black[i].setBorder(new BevelBorder(BevelBorder.RAISED)); //카드 양각처리
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
}//클래스 닫기