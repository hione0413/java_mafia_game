package server.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerMain extends JFrame{
	JPanel p_north;
	JTextField t_ip;
	JTextField t_port;
	JButton bt;
	JTextArea area;
	JScrollPane scroll;
	JScrollBar bar;
	ServerSocket server;
	int port=7777;
	Thread serverThread;
	//Dispatcher dispatcher;	//클라이언트의 요청내용 분석 객체!
	
	boolean clientFlag = true;
	Vector<ServerThread> list=new Vector<ServerThread>();	//서버측에 생성된 아바타 들을 담을 객체!!
	
	//직업 : Mafia, Civilian
	Vector<String> jobs=new Vector<String>();	//게임에서 각 유저에게 배정된 직업들
	Vector<String> liveJobs=new Vector<String>();	//유저들 생존여부 판정
	
	Vector<String> jobsReady=new Vector<String>();	//인원수에 따른 직업세팅
	
	ServerTimer timer;
	
	public ServerMain() {
		p_north = new JPanel();
		t_ip = new JTextField();
		t_port = new JTextField();
		bt = new JButton("서버 가동");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		bar=scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
		
		timer = new ServerTimer(this);
		
		//area.setFont(new Font("굴림", Font.BOLD, 15));
		t_ip.setPreferredSize(new Dimension(200, 25));
		t_port.setPreferredSize(new Dimension(80, 25));
		
		p_north.add(t_ip);
		p_north.add(t_port);
		p_north.add(bt);
		
		//프로그램 가동과 동시에 아이피 할당해두기
		t_ip.setText(getIp());
		t_port.setText(Integer.toString(port));
		
		serverThread=new Thread() {
			public void run() {
				runServer();
			}
		};
		bt.addActionListener((e)->{
			serverThread.start();
			bt.setEnabled(false);
		});
		add(timer, BorderLayout.SOUTH);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		setVisible(true);
		setSize(400,500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	
	public String getIp() {
		String ip=null;
		try {
			InetAddress inet=InetAddress.getLocalHost();
			ip=inet.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	public void runServer() {
		String ip=t_ip.getText();
		port=Integer.parseInt(t_port.getText());
		try {
			server=new ServerSocket(port);
			area.append("서버 가동\n");
			
			while(clientFlag) {
				Socket client=server.accept();	//접속발생
				String clientIp=client.getInetAddress().getHostAddress();
				area.append(clientIp+" 클라이언트 접속\n");
				//상대에게 몇번째 유저인지 알려주기
				
				//접속과 동시에 대화를 나눌 아바타 생성
				ServerThread st=new ServerThread(this, client);
				st.start();
				
				//client를 list에 담기
				list.add(st);
				area.append("현재"+list.size()+"명 접속 중\n");
				if(list.size()>=8) {
					clientFlag=false;
				}
				//여기에 8명일 땐 wait 걸리다 한명 나가면 다시 Thread 돌아가게 만들기
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//접속 인원수에 따른 직업들 세팅해두기
	public void settintJobs() {
		int clientCount=list.size();
		switch(clientCount) {
			case 3 : inputJobs(3);break;
			case 4 : inputJobs(4);break;
			case 5 : inputJobs(5);break;
			case 6 : inputJobs(6);break;
			case 7 : inputJobs(7);break;
			case 8 : inputJobs(8);
		}
	}
	
	//접속 인원수에 따른 직업들 세팅해두기
	public Vector<String> inputJobs(int JobsNumber) { //아 하드코딩 좆
		if(JobsNumber==3||JobsNumber==4||JobsNumber==5){
			jobsReady.add("Mafia1");
			for(int i=0;i<list.size()-1;i++) {
				jobsReady.add("Civilian");
			}
		}else if(JobsNumber==6||JobsNumber==7||JobsNumber==8) {
			jobsReady.add("Mafia1");
			jobsReady.add("Mafia2");
			for(int i=0;i<list.size()-2;i++) {
				jobsReady.add("Civilian");
			}
		}
		System.out.println("ServerMain의 settingJobs : "+jobsReady.getClass()+jobsReady);
		return jobsReady;
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}