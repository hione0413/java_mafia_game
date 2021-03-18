package client.main;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONObject;

public class MafiaKillFrame extends JFrame{
	JLabel label;
	JButton bt_user1, bt_user2, bt_user3, bt_user4, bt_user5, bt_user6, bt_user7, bt_user8;
	JPanel p_bt;
	JTextArea area;
	JScrollPane scroll;
	JScrollBar bar;
	JTextField t;
	Vector<JButton> bt_array=new Vector<JButton>();
	ClientMain main;
	
	public MafiaKillFrame(ClientMain main) {
		this.main=main;
		
		label = new JLabel("누구를 죽이겠습니까?");
		p_bt = new JPanel();
		Dimension d= new Dimension(50,25);	//유저 버튼 크기
		bt_user1 = new JButton("1"); 
		bt_user2 = new JButton("2");
		bt_user3 = new JButton("3");
		bt_user4 = new JButton("4");
		bt_user5 = new JButton("5");
		bt_user6 = new JButton("6");
		bt_user7 = new JButton("7");
		bt_user8 = new JButton("8");
		
		area = new JTextArea();
		scroll = new JScrollPane(area);
		bar=scroll.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
		
		t = new JTextField(20);
		
		label.setPreferredSize(new Dimension(200,30));
		p_bt.setPreferredSize(new Dimension(220,70));
		area.setPreferredSize(new Dimension(220,200));
		t.setPreferredSize(new Dimension(200,30));
		
		bt_user1.setPreferredSize(d);
		bt_user2.setPreferredSize(d);
		bt_user3.setPreferredSize(d);
		bt_user4.setPreferredSize(d);
		bt_user5.setPreferredSize(d);
		bt_user6.setPreferredSize(d);
		bt_user7.setPreferredSize(d);
		bt_user8.setPreferredSize(d);
		
		bt_array.add(bt_user1);
		bt_array.add(bt_user2);
		bt_array.add(bt_user3);
		bt_array.add(bt_user4);
		bt_array.add(bt_user5);
		bt_array.add(bt_user6);
		bt_array.add(bt_user7);
		bt_array.add(bt_user8);
		
		add(label);
		for(int i=0;i<bt_array.size();i++) {
			p_bt.add(bt_array.get(i));
			bt_array.get(i).setEnabled(false);
		}
		add(p_bt);
		add(scroll);
		add(t);
		
		t.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(t.getText().length()!=0) {
					if(key==KeyEvent.VK_ENTER) {
						JSONObject obj = new JSONObject();
						obj.put("Type", "mafiaTalk");
						String talkMent=main.clientNo+" : "+t.getText();
						obj.put("talk", talkMent);
						main.ct.send(obj.toString());
						//area.append(t.getText()+"\n");
						t.setText("");
					}
				}
			}
		});
		for(int i=0;i<bt_array.size();i++) {	//각 버튼들에 리스터 부착
			int a=i;
			bt_array.get(i).addActionListener((e)->{
				int user=returnUser(a);
				System.out.println("마피아가 "+user+"유저를 지목");
				//area.append(user+"번 유저를 지목하였습니다.\n");
			});
		}
		
		setLayout(new FlowLayout());
		setSize(250,420);
		setVisible(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public int returnUser(int user) {
		//리턴값을 serverParser에게 보내자->server에서는 지목된 사람들을 취합
		JSONObject obj = new JSONObject();
		obj.put("Type", "mafiaKillPoint");
		obj.put("choice",""+user+"");
		obj.put("choiceMsg","마피아가 "+(user+1)+"번 유저를 지목하였습니다.\n");
		main.ct.send(obj.toString());
		return user+1;
	};
}
