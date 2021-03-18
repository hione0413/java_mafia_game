package client.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
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

import org.json.simple.JSONObject;

public class ClientMain extends JFrame{
	int x=450;	//Frame의 x축 넓이
	int clientNo;	//유저번호-접속할 때 배정받음
	boolean liveFlag=true;	//죽고 살고의 논리값. 죽으면 false로 돌림(말 못하게 됨)
	
	//접속관련
	JPanel p_conn;
	JTextField t_ip, t_port;
	JButton bt_conn;
	Socket client;
	ClientThread ct;
	
	//게임관련
	ClientTimer clientTimer;
	JPanel p_chatArea, p_users, p_talk;
	JTextArea area;
	JScrollPane scroll;
	JScrollBar bar;
	JButton bt_user1, bt_user2, bt_user3, bt_user4, bt_user5, bt_user6, bt_user7, bt_user8;
	JTextArea talk;
	JButton bt_talk;
	
	Vector<JButton> bt_array=new Vector<JButton>();
	LiveOrDieFrame f_lod;	//찬반투표 프레임
	MafiaKillFrame f_mkf;	//마피아가 총쏠사람 고르는 프레임
	
	public ClientMain() {
		
		p_conn = new JPanel();
		t_ip = new JTextField("192.168.149.1");	//임시
		t_port = new JTextField("7777");	//임시
		bt_conn = new JButton("서버접속");
		clientTimer = new ClientTimer(this);
		p_chatArea = new JPanel();
		area = new JTextArea();
		scroll = new JScrollPane(area);
		bar=scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());

		p_users = new JPanel();
		bt_user1 = new JButton("1"); 
		bt_user2 = new JButton("2");
		bt_user3 = new JButton("3");
		bt_user4 = new JButton("4");
		bt_user5 = new JButton("5");
		bt_user6 = new JButton("6");
		bt_user7 = new JButton("7");
		bt_user8 = new JButton("8");
		p_talk = new JPanel();
		talk = new JTextArea();
		bt_talk = new JButton("생존");
		
		
		Dimension d= new Dimension(100,25);	//유저 버튼 크기
		
		p_conn.setPreferredSize(new Dimension(x,30));
		p_chatArea.setPreferredSize(new Dimension(x,350));
		scroll.setPreferredSize(new Dimension(x-50,320));
		p_users.setPreferredSize(new Dimension(x,70));
		bt_user1.setPreferredSize(d);
		bt_user2.setPreferredSize(d);
		bt_user3.setPreferredSize(d);
		bt_user4.setPreferredSize(d);
		bt_user5.setPreferredSize(d);
		bt_user6.setPreferredSize(d);
		bt_user7.setPreferredSize(d);
		bt_user8.setPreferredSize(d);
		
		p_talk.setPreferredSize(new Dimension(x,100));
		talk.setPreferredSize(new Dimension(300,100));
		bt_talk.setPreferredSize(new Dimension(100,100));
		
		//add(timer);
		
		p_conn.add(t_ip);
		p_conn.add(t_port);
		p_conn.add(bt_conn);
		add(p_conn);
		
		add(clientTimer);
		
		add(scroll);
		
		bt_array.add(bt_user1);
		bt_array.add(bt_user2);
		bt_array.add(bt_user3);
		bt_array.add(bt_user4);
		bt_array.add(bt_user5);
		bt_array.add(bt_user6);
		bt_array.add(bt_user7);
		bt_array.add(bt_user8);
		for(int i=0;i<bt_array.size();i++) {
			p_users.add(bt_array.get(i));
		}
		add(p_users);
		
		p_talk.add(talk);
		p_talk.add(bt_talk);
		add(p_talk);
		
		bt_conn.addActionListener((e)->{
			connect();
		});
		talk.addKeyListener(new KeyAdapter() {	//특정 조건이 갖춰졌을 때만 발언권 주는 방법 연구해볼 것
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(talk.getText().length()!=1) {
					if(key==KeyEvent.VK_ENTER) {
						JSONObject obj = new JSONObject();
						obj.put("Type", "talk");
						String talkMent=clientNo+" : "+talk.getText();
						obj.put("talk", talkMent);
						ct.send(obj.toString());
						talk.setText("");
					}
				}
			}
		});

		for(int i=0;i<bt_array.size();i++) {	//각 버튼들에 리스터 부착
			int a=i;
			bt_array.get(i).addActionListener((e)->{
				int user=returnUser(a);
				//System.out.println(user+"유저를 지목");
				area.append(user+"번 유저를 지목하였습니다.\n");
				for(int z=0;z<bt_array.size();z++) {
					bt_array.get(z).setEnabled(false);
				}
			});
		}
		for(int z=0;z<bt_array.size();z++) {	//버튼 일단 꺼둠
			bt_array.get(z).setEnabled(false);
		}
		//유저버튼들에 OnClickListener()로 주면 setOnClickListener(null)를 이용해 비활성화 가능
		//removeEventListener()로도 비활성화 가능
		
		setLayout(new FlowLayout());
		setSize(x,700);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		f_lod=new LiveOrDieFrame(this);
		f_mkf=new MafiaKillFrame(this);
		
	}
	
	//ip와 port를 입력받아 Server로 접속
	public void connect() {
		String ip=t_ip.getText();
		int port=Integer.parseInt(t_port.getText());
		
		//소켓 생성시 접속이 발생한다.
		try {
			client = new Socket(ip,port);
			ct = new ClientThread(this, client);
			ct.start();
			area.append(ip+" 서버로 접속했습니다.\n");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int returnUser(int user) {
		//리턴값을 serverParser에게 보내자->server에서는 지목된 사람들을 취합
		//"누군가가 user를 지목하였습니다."
		JSONObject obj = new JSONObject();
		obj.put("Type", "voteSubmit");
		obj.put("select",""+user+"");
		obj.put("voteResult","누군가가 "+(user+1)+"번 유저를 지목하였습니다.\n");
		ct.send(obj.toString());
		return user+1;
	};
	public void setClientNo(int clientNo) {
		this.clientNo=clientNo;
	}
	
	public static void main(String[] args) {
		new ClientMain();
	}
	
	
}
