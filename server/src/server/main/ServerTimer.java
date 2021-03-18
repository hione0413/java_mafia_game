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
 * 마피아 타이머
 * 사용법 : 
 *  1) min(분), sec(초) 세팅 후
 *  2) timerFlag를 true로 주고
 *  3) timerThread.start() 즉, Thread 가동하면 타이머 카운트 시작
 *  
 *  밤 : 00:25
 *  낮 : 02:30
 *  투표 : 00:25
 *  최후반론 : 00:25
 *  찬반투표 : 00:10
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
		bt_start = new JButton("게임시작");
		
		
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
				dayThread.gameFlag=true;//게임시작
				bt_start.setEnabled(false);
				dayThread.start();
			} else {
				JOptionPane.showMessageDialog(null, "유저가 3명 이상 접속해야 시작 가능합니다.");
			}
		});
		
		//setLayout(new FlowLayout());
		//setSize(250, 135);
		setPreferredSize(new Dimension(250,100));
		setVisible(true);
	}
	
	public void timer() {
		System.out.println("타이머 가동");
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
			l_min.setText("0"+min+"");	//분 입력
			if(sec>=10) {l_sec.setText(""+sec+"");}else {l_sec.setText("0"+sec+"");}	//초입력
			
			try {
				timerThread.sleep(sleepSpeed);	//속도조절
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}
		
	}
	
	//wait랑 notify 걸지말고 그냥 timer를 그때마다 새로 new 해라
	public synchronized void timeUp() {
		if(min<0) {
			min=0;
			sec=0;
			l_min.setText("0"+min+"");
			l_sec.setText("0"+sec+"");

			timerFlag=false;
			bt_start.setEnabled(true);
			System.out.println("타이머 정지");
		}
	}
	public void timePlus() {
		if(sec<40) {
			sec+=20;
		}else {
			min++;
			sec=59-(20-sec);		
		}
		l_min.setText("0"+min+"");	//분 입력
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
		l_min.setText("0"+min+"");	//분 입력
		if(sec>=10) {l_sec.setText(""+sec+"");}else {l_sec.setText("0"+sec+"");}
		timeUp();
	}
	
	//min과 sec 값을 clientParser로 보내서 client 들의 timer 를 세팅해줌
	public void sendSetTimer() {
		JSONObject obj = new JSONObject();
		obj.put("Type", "setTimer");
		obj.put("min", ""+min+""); 
		obj.put("sec", ""+sec+"");
		for(int i=0;i<main.list.size();i++) {
			ServerThread st=main.list.get(i);
			//System.out.println("서버에서 클라이언트에게 전송하는 timer 관련 JSON은 : "+obj.toString());
			st.send(obj.toString());
		}
	}
}
