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
	//Dispatcher dispatcher;	//Ŭ���̾�Ʈ�� ��û���� �м� ��ü!
	
	boolean clientFlag = true;
	Vector<ServerThread> list=new Vector<ServerThread>();	//�������� ������ �ƹ�Ÿ ���� ���� ��ü!!
	
	//���� : Mafia, Civilian
	Vector<String> jobs=new Vector<String>();	//���ӿ��� �� �������� ������ ������
	Vector<String> liveJobs=new Vector<String>();	//������ �������� ����
	
	Vector<String> jobsReady=new Vector<String>();	//�ο����� ���� ��������
	
	ServerTimer timer;
	
	public ServerMain() {
		p_north = new JPanel();
		t_ip = new JTextField();
		t_port = new JTextField();
		bt = new JButton("���� ����");
		area = new JTextArea();
		scroll = new JScrollPane(area);
		bar=scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
		
		timer = new ServerTimer(this);
		
		//area.setFont(new Font("����", Font.BOLD, 15));
		t_ip.setPreferredSize(new Dimension(200, 25));
		t_port.setPreferredSize(new Dimension(80, 25));
		
		p_north.add(t_ip);
		p_north.add(t_port);
		p_north.add(bt);
		
		//���α׷� ������ ���ÿ� ������ �Ҵ��صα�
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
			area.append("���� ����\n");
			
			while(clientFlag) {
				Socket client=server.accept();	//���ӹ߻�
				String clientIp=client.getInetAddress().getHostAddress();
				area.append(clientIp+" Ŭ���̾�Ʈ ����\n");
				//��뿡�� ���° �������� �˷��ֱ�
				
				//���Ӱ� ���ÿ� ��ȭ�� ���� �ƹ�Ÿ ����
				ServerThread st=new ServerThread(this, client);
				st.start();
				
				//client�� list�� ���
				list.add(st);
				area.append("����"+list.size()+"�� ���� ��\n");
				if(list.size()>=8) {
					clientFlag=false;
				}
				//���⿡ 8���� �� wait �ɸ��� �Ѹ� ������ �ٽ� Thread ���ư��� �����
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//���� �ο����� ���� ������ �����صα�
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
	
	//���� �ο����� ���� ������ �����صα�
	public Vector<String> inputJobs(int JobsNumber) { //�� �ϵ��ڵ� ��
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
		System.out.println("ServerMain�� settingJobs : "+jobsReady.getClass()+jobsReady);
		return jobsReady;
	}
	
	public static void main(String[] args) {
		new ServerMain();
	}
}