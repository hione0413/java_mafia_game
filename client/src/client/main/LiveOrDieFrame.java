package client.main;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

public class LiveOrDieFrame extends JFrame {
	JLabel label;
	JButton bt_agree, bt_disagree;
	ClientMain main;
	
	public LiveOrDieFrame(ClientMain main) {
		this.main=main;
		label = new JLabel("�����ڸ� ������Ű�ڽ��ϱ�?");
		bt_agree = new JButton("����");
		bt_disagree = new JButton("�ݴ�");
		
		add(label);
		add(bt_agree);
		add(bt_disagree);
		
		bt_agree.addActionListener((e)->{
			JSONObject obj = new JSONObject();
			obj.put("Type", "liveOrDieVoteResult");
			obj.put("liveOrDieSelect", "����");
			main.ct.send(obj.toString());
			bt_agree.setEnabled(false);
			bt_disagree.setEnabled(false);
		});
		
		bt_disagree.addActionListener((e)->{
			JSONObject obj = new JSONObject();
			obj.put("Type", "liveOrDieVoteResult");
			obj.put("liveOrDieSelect", "�ݴ�");
			main.ct.send(obj.toString());
			bt_agree.setEnabled(false);
			bt_disagree.setEnabled(false);
		});
		
		setLocationRelativeTo(main);
		setLayout(new FlowLayout());
		setSize(250,100);
		setVisible(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
