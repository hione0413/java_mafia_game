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
	int x=450;	//Frame�� x�� ����
	int clientNo;	//������ȣ-������ �� ��������
	boolean liveFlag=true;	//�װ� ����� ����. ������ false�� ����(�� ���ϰ� ��)
	
	//���Ӱ���
	JPanel p_conn;
	JTextField t_ip, t_port;
	JButton bt_conn;
	Socket client;
	ClientThread ct;
	
	//���Ӱ���
	ClientTimer clientTimer;
	JPanel p_chatArea, p_users, p_talk;
	JTextArea area;
	JScrollPane scroll;
	JScrollBar bar;
	JButton bt_user1, bt_user2, bt_user3, bt_user4, bt_user5, bt_user6, bt_user7, bt_user8;
	JTextArea talk;
	JButton bt_talk;
	
	Vector<JButton> bt_array=new Vector<JButton>();
	LiveOrDieFrame f_lod;	//������ǥ ������
	MafiaKillFrame f_mkf;	//���Ǿư� �ѽ��� ���� ������
	
	public ClientMain() {
		
		p_conn = new JPanel();
		t_ip = new JTextField("192.168.149.1");	//�ӽ�
		t_port = new JTextField("7777");	//�ӽ�
		bt_conn = new JButton("��������");
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
		bt_talk = new JButton("����");
		
		
		Dimension d= new Dimension(100,25);	//���� ��ư ũ��
		
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
		talk.addKeyListener(new KeyAdapter() {	//Ư�� ������ �������� ���� �߾�� �ִ� ��� �����غ� ��
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

		for(int i=0;i<bt_array.size();i++) {	//�� ��ư�鿡 ������ ����
			int a=i;
			bt_array.get(i).addActionListener((e)->{
				int user=returnUser(a);
				//System.out.println(user+"������ ����");
				area.append(user+"�� ������ �����Ͽ����ϴ�.\n");
				for(int z=0;z<bt_array.size();z++) {
					bt_array.get(z).setEnabled(false);
				}
			});
		}
		for(int z=0;z<bt_array.size();z++) {	//��ư �ϴ� ����
			bt_array.get(z).setEnabled(false);
		}
		//������ư�鿡 OnClickListener()�� �ָ� setOnClickListener(null)�� �̿��� ��Ȱ��ȭ ����
		//removeEventListener()�ε� ��Ȱ��ȭ ����
		
		setLayout(new FlowLayout());
		setSize(x,700);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		f_lod=new LiveOrDieFrame(this);
		f_mkf=new MafiaKillFrame(this);
		
	}
	
	//ip�� port�� �Է¹޾� Server�� ����
	public void connect() {
		String ip=t_ip.getText();
		int port=Integer.parseInt(t_port.getText());
		
		//���� ������ ������ �߻��Ѵ�.
		try {
			client = new Socket(ip,port);
			ct = new ClientThread(this, client);
			ct.start();
			area.append(ip+" ������ �����߽��ϴ�.\n");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int returnUser(int user) {
		//���ϰ��� serverParser���� ������->server������ ����� ������� ����
		//"�������� user�� �����Ͽ����ϴ�."
		JSONObject obj = new JSONObject();
		obj.put("Type", "voteSubmit");
		obj.put("select",""+user+"");
		obj.put("voteResult","�������� "+(user+1)+"�� ������ �����Ͽ����ϴ�.\n");
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
