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
		label = new JLabel("용의자를 사형시키겠습니까?");
		bt_agree = new JButton("찬성");
		bt_disagree = new JButton("반대");
		
		add(label);
		add(bt_agree);
		add(bt_disagree);
		
		bt_agree.addActionListener((e)->{
			JSONObject obj = new JSONObject();
			obj.put("Type", "liveOrDieVoteResult");
			obj.put("liveOrDieSelect", "찬성");
			main.ct.send(obj.toString());
			bt_agree.setEnabled(false);
			bt_disagree.setEnabled(false);
		});
		
		bt_disagree.addActionListener((e)->{
			JSONObject obj = new JSONObject();
			obj.put("Type", "liveOrDieVoteResult");
			obj.put("liveOrDieSelect", "반대");
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
