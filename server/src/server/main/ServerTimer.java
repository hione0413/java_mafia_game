package server.main;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.json.simple.JSONObject;


//timerFlag=true;
//min=2;
//timerThread.start();
/*
 * ���Ǿ� Ÿ�̸�
 * ���� : 
 *  1) min(��), sec(��) ���� ��
 *  2) timerFlag�� true�� �ְ�
 *  3) timerThread.start() ��, Thread �����ϸ� Ÿ�̸� ī��Ʈ ����
 *  
 *  �� : 00:25
 *  �� : 02:30
 *  ��ǥ : 00:25
 *  ���Ĺݷ� : 00:25
 *  ������ǥ : 00:10
 * */

public class ServerTimer extends JPanel{
	JPanel p_timer, p_bt;
	JLabel l_min, l_center, l_sec;
	JButton bt_start;
	ServerMain main;
	
	Thread timerThread;
	DayThread dayThread;
	
	boolean timerFlag=false;
	int min=0;
	int sec=0;
	int sleepSpeed=1000;
	
	public ServerTimer(ServerMain main) {
		this.main = main;
		p_timer = new JPanel();
		l_min = new JLabel(""+min+"");
		l_center = new JLabel(" : ");
		l_sec = new JLabel(""+sec+"");
		p_bt = new JPanel();
		bt_start = new JButton("���ӽ���");
		
		
		p_timer.setPreferredSize(new Dimension(200, 40));
		p_bt.setPreferredSize(new Dimension(200, 40));
		p_timer.add(l_min);
		p_timer.add(l_center);
		p_timer.add(l_sec);
		add(p_timer);
		
		p_bt.add(bt_start);
		add(p_bt);
		
		/*timerThread = new Thread() {
			public void run() {
				timer();
			}
		};*/
		dayThread = new DayThread(this);
		
		bt_start.addActionListener((e)->{
			if( main.list.size() >= 3 ) {
				dayThread.gameFlag=true;//���ӽ���
				bt_start.setEnabled(false);
				dayThread.start();
			} else {
				JOptionPane.showMessageDialog(null, "������ 3�� �̻� �����ؾ� ���� �����մϴ�.");
			}
		});
		
		//setLayout(new FlowLayout());
		//setSize(250, 135);
		setPreferredSize(new Dimension(250,100));
		setVisible(true);
	}
	
	public void timer() {
		System.out.println("Ÿ�̸� ����");
		l_min.setText(""+min+"");
		l_sec.setText(""+sec+"");
		while(timerFlag) {
			sendSetTimer();
			sec--;
			if(sec<0) {
				min--;
				sec=59;
			}
			timeUp();
			l_min.setText("0"+min+"");	//�� �Է�
			if(sec>=10) {l_sec.setText(""+sec+"");}else {l_sec.setText("0"+sec+"");}	//���Է�
			
			try {
				timerThread.sleep(sleepSpeed);	//�ӵ�����
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	//wait�� notify �������� �׳� timer�� �׶����� ���� new �ض�
	public synchronized void timeUp() {
		if(min<0) {
			min=0;
			sec=0;
			l_min.setText("0"+min+"");
			l_sec.setText("0"+sec+"");

			timerFlag=false;
			bt_start.setEnabled(true);
			System.out.println("Ÿ�̸� ����");
		}
	}
	public void timePlus() {
		if(sec<40) {
			sec+=20;
		}else {
			min++;
			sec=59-(20-sec);		
		}
		l_min.setText("0"+min+"");	//�� �Է�
		if(sec>=10) {l_sec.setText(""+sec+"");}else {l_sec.setText("0"+sec+"");}
		timeUp();
	}
	
	public void timeMinus() {		
		if(sec>20) {
			sec-=20;
		}else if(sec<=20) {
			min--;
			sec=59-(20-sec);		
		}
		l_min.setText("0"+min+"");	//�� �Է�
		if(sec>=10) {l_sec.setText(""+sec+"");}else {l_sec.setText("0"+sec+"");}
		timeUp();
	}
	
	//min�� sec ���� clientParser�� ������ client ���� timer �� ��������
	public void sendSetTimer() {
		JSONObject obj = new JSONObject();
		obj.put("Type", "setTimer");
		obj.put("min", ""+min+""); 
		obj.put("sec", ""+sec+"");
		for(int i=0;i<main.list.size();i++) {
			ServerThread st=main.list.get(i);
			//System.out.println("�������� Ŭ���̾�Ʈ���� �����ϴ� timer ���� JSON�� : "+obj.toString());
			st.send(obj.toString());
		}
	}
}
