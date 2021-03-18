package client.main;

import java.awt.Color;

import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientParser {
	ClientMain main;
	ClientThread clientThread;
	String msg;
	Object obj;
	JSONArray array = new JSONArray();
	int clientsCountInt;	//���� ������ �� ������

	public ClientParser(ClientMain main, ClientThread clientThread) {
		this.main = main;
		this.clientThread = clientThread;
	}

	// JSON���� Parser(JSON) ���·� �Ѱܹ���
	public void Parser(String msg) {
		this.msg = msg;
		// System.out.println("Parser�� �Ѱܹ��� ���� : "+msg);
		JSONParser parser = new JSONParser();
		String sql = null;
		Object obj2 = null;
		try {
			obj2 = parser.parse(msg);
			if (array.getClass() != obj2.getClass()) {// ?????
				JSONObject jsonObj = (JSONObject) obj2;
				String type = (String) jsonObj.get("Type");
				if (type.equals("setTimer")) {
					String min = (String) jsonObj.get("min");
					String sec = (String) jsonObj.get("sec");
					main.clientTimer.min = Integer.parseInt(min);
					main.clientTimer.sec = Integer.parseInt(sec);
					main.clientTimer.l_min.setText(min);
					main.clientTimer.l_sec.setText(sec);
					
				} else if (type.equals("sendDayMsg")) {
					String dayMsg = (String) jsonObj.get("dayMsg");
					main.area.append("----------------------------------------\n");
					main.area.append(dayMsg + "\n");
					main.area.append("----------------------------------------\n");
					
				} else if (type.equals("talk")) {
					String talk = (String) jsonObj.get("talk");
					main.area.append(talk+"\n");
					
				} else if (type.equals("mafiaTalk")) {
					String talk = (String) jsonObj.get("talk");
					main.f_mkf.area.append(talk+"\n");
					
				}else if (type.equals("vote")) {
					for (int z = 0; z < clientsCountInt; z++) { // ��ư�� 1ȸ�� Ȱ��ȭ
						if(!main.bt_talk.equals("DIE")) {
							main.bt_array.get(z).setEnabled(true);
						}
					}
					
				} else if (type.equals("voteEnd")) {
					main.area.append("��ǥ�� ����Ǿ����ϴ�.\n");
					for (int z = 0; z < main.bt_array.size(); z++) {
						main.bt_array.get(z).setEnabled(false);
					}
					
				} else if (type.equals("lastChance")) {
					main.talk.setEditable(false);
					
				} else if (type.equals("lastChanceEnd")) {
					main.talk.setEditable(true);
					
				} else if (type.equals("liveOrDieVote")) {
					main.area.append("������ǥ���� �޾ҽ��ϴ�.\n");
					main.f_lod.bt_agree.setEnabled(true);
					main.f_lod.bt_disagree.setEnabled(true);
					main.f_lod.setVisible(true);
					
				} else if(type.equals("liveOrDieVoteEnd")) {
					main.f_lod.setVisible(false);
					
				} else if(type.equals("winCivilian")) {
					JOptionPane.showMessageDialog(main, "�ù��� �¸��Ͽ����ϴ�.");
				} else if(type.equals("winMafia")) {
					JOptionPane.showMessageDialog(main, "���Ǿư� �¸��Ͽ����ϴ�.");
				} else if(type.equals("yourJob")) {
					
					String job = (String) jsonObj.get("Job");	//�ش� ������ ����
					String clientNoString=(String) jsonObj.get("ClientNo");	//�ش� ������ ���° ��������
					String clientsCount=(String) jsonObj.get("ClientsCount");	//������ �� ������
					clientsCountInt = Integer.parseInt(clientsCount);
					int clientNo = Integer.parseInt(clientNoString);
					System.out.println("����� ������ȣ�� : "+(clientNo+1));
					System.out.println("����� �������� ���� ������ :" +job);
					int userNo=clientNo+1;
					main.setClientNo(userNo);
					main.area.append("����� "+(clientNo+1)+"��° �����Դϴ�.\n");
					
					if ( job.equals("Mafia1")||job.equals("Mafia2") ) {
						System.out.println("�� ���Ǿ�");
						main.area.append("����� ���Ǿ��Դϴ�.\n�ùε��� �׿� �¸��ϼ���.\n");
					} else if ( job.equals("Civilian") ) {
						System.out.println("�� �ù�");
						main.area.append("����� �ù��Դϴ�.\n���ǾƸ� ã�Ƴ� �¸��ϼ���.\n");
					}
					
				} else if (type.equals("youDie")) {
					main.area.append("����� �׾����ϴ�.\n");
					main.bt_talk.setText("DIE");
					main.bt_talk.setBackground(Color.RED);
					main.liveFlag=false;
				} else if (type.equals("youDieNotify")) {
					String msg2 = (String) jsonObj.get("msg");
					main.area.append(msg2+"\n");
					
					String who = (String) jsonObj.get("who");
					int whoInt = Integer.parseInt(who);
					main.bt_array.get(whoInt).setText("DIE");
					main.f_mkf.bt_array.get(whoInt).setText("DIE");//���Ǿ� ��ũ
					
				} else if (type.equals("nightStart")) {
					main.talk.setEditable(false);
					main.clientTimer.bt_ex.setEnabled(false);
					main.clientTimer.bt_col.setEnabled(false);
					
				} else if (type.equals("wakeUpMafia")) {//talk ���� �� ��Ÿ�� �༭ ���Ǿ���ũ �����
					//���ǾƸ� �ƿ��
					main.area.append("���Ǿ��� ����� ���� ��ϴ�.\n");
					main.area.append("���ù� ������ ����� �Ѹ� �����ϼ���.\n");
					//main.talk.setEditable(true);
					//���ο� JFrame ���� ���� ��� ������
					main.f_mkf.setVisible(true);
					for (int i = 0; i < clientsCountInt; i++) { // ��ư�� 1ȸ�� Ȱ��ȭ
						if(!main.f_mkf.bt_array.get(i).getText().equals("DIE")) {
							main.f_mkf.bt_array.get(i).setEnabled(true);
						}
					}
					
				} else if (type.equals("nightEnd")) {
					if(!main.bt_talk.getText().equals("DIE")) {
						main.talk.setEditable(true);
						main.f_mkf.setVisible(false);
						main.clientTimer.bt_ex.setEnabled(true);
						main.clientTimer.bt_col.setEnabled(true);
					}
				}
			}
				

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

}
