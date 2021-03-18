package client.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

public class ClientTimer extends JPanel{
	JPanel p_timer, p_bt;
	JLabel l_min, l_center, l_sec;
	JButton bt_ex, bt_col;
	ClientMain clientMain;
	
	boolean timerFlag=false;
	int min=0;
	int sec=0;
	
	public ClientTimer(ClientMain clientMain) {
		this.clientMain = clientMain;
		p_timer = new JPanel();
		l_min = new JLabel(""+min+"");
		l_center = new JLabel(" : ");
		l_sec = new JLabel(""+sec+"");
		p_bt = new JPanel();
		bt_ex = new JButton("시간연장");
		bt_col = new JButton("시간단축");
		
		p_timer.setPreferredSize(new Dimension(200, 25));
		p_bt.setPreferredSize(new Dimension(200, 40));
		p_timer.add(l_min);
		p_timer.add(l_center);
		p_timer.add(l_sec);
		add(p_timer);
		
		p_bt.add(bt_ex);
		p_bt.add(bt_col);
		add(p_bt);
	
		bt_ex.addActionListener((e)->{
			//timePlus();
			JSONObject obj = new JSONObject();
			obj.put("Type", "timePlus");
			clientMain.ct.send(obj.toString());
			bt_ex.setEnabled(false);
		});
		bt_col.addActionListener((e)->{
			//timeMinus();
			JSONObject obj = new JSONObject();
			obj.put("Type", "timeMinus");
			clientMain.ct.send(obj.toString());
			bt_col.setEnabled(false);
		});
		
		setPreferredSize(new Dimension(250, 90));
		setVisible(true);
	}
	
	public void timeUp() {
		if(min<0) {
			min=0;
			sec=0;
			l_min.setText("0"+min+"");
			l_sec.setText("0"+sec+"");
			timerFlag=false;
			bt_ex.setEnabled(true);
			bt_col.setEnabled(true);
			//이 위치에 밤낮 바뀌는 변수 줄 예정
		}
	}
}
