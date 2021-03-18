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
	int clientsCountInt;	//현재 접속한 총 유저수

	public ClientParser(ClientMain main, ClientThread clientThread) {
		this.main = main;
		this.clientThread = clientThread;
	}

	// JSON값을 Parser(JSON) 형태로 넘겨받음
	public void Parser(String msg) {
		this.msg = msg;
		// System.out.println("Parser가 넘겨받은 값은 : "+msg);
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
					for (int z = 0; z < clientsCountInt; z++) { // 버튼들 1회용 활성화
						if(!main.bt_talk.equals("DIE")) {
							main.bt_array.get(z).setEnabled(true);
						}
					}
					
				} else if (type.equals("voteEnd")) {
					main.area.append("투표가 종료되었습니다.\n");
					for (int z = 0; z < main.bt_array.size(); z++) {
						main.bt_array.get(z).setEnabled(false);
					}
					
				} else if (type.equals("lastChance")) {
					main.talk.setEditable(false);
					
				} else if (type.equals("lastChanceEnd")) {
					main.talk.setEditable(true);
					
				} else if (type.equals("liveOrDieVote")) {
					main.area.append("찬반투표권을 받았습니다.\n");
					main.f_lod.bt_agree.setEnabled(true);
					main.f_lod.bt_disagree.setEnabled(true);
					main.f_lod.setVisible(true);
					
				} else if(type.equals("liveOrDieVoteEnd")) {
					main.f_lod.setVisible(false);
					
				} else if(type.equals("winCivilian")) {
					JOptionPane.showMessageDialog(main, "시민이 승리하였습니다.");
				} else if(type.equals("winMafia")) {
					JOptionPane.showMessageDialog(main, "마피아가 승리하였습니다.");
				} else if(type.equals("yourJob")) {
					
					String job = (String) jsonObj.get("Job");	//해당 유저의 직업
					String clientNoString=(String) jsonObj.get("ClientNo");	//해당 유저가 몇번째 유저인지
					String clientsCount=(String) jsonObj.get("ClientsCount");	//접속한 총 유저수
					clientsCountInt = Integer.parseInt(clientsCount);
					int clientNo = Integer.parseInt(clientNoString);
					System.out.println("당신의 유저번호는 : "+(clientNo+1));
					System.out.println("당신이 서버에게 들은 직업은 :" +job);
					int userNo=clientNo+1;
					main.setClientNo(userNo);
					main.area.append("당신은 "+(clientNo+1)+"번째 유저입니다.\n");
					
					if ( job.equals("Mafia1")||job.equals("Mafia2") ) {
						System.out.println("너 마피아");
						main.area.append("당신은 마피아입니다.\n시민들을 죽여 승리하세요.\n");
					} else if ( job.equals("Civilian") ) {
						System.out.println("너 시민");
						main.area.append("당신은 시민입니다.\n마피아를 찾아내 승리하세요.\n");
					}
					
				} else if (type.equals("youDie")) {
					main.area.append("당신은 죽었습니다.\n");
					main.bt_talk.setText("DIE");
					main.bt_talk.setBackground(Color.RED);
					main.liveFlag=false;
				} else if (type.equals("youDieNotify")) {
					String msg2 = (String) jsonObj.get("msg");
					main.area.append(msg2+"\n");
					
					String who = (String) jsonObj.get("who");
					int whoInt = Integer.parseInt(who);
					main.bt_array.get(whoInt).setText("DIE");
					main.f_mkf.bt_array.get(whoInt).setText("DIE");//마피아 토크
					
				} else if (type.equals("nightStart")) {
					main.talk.setEditable(false);
					main.clientTimer.bt_ex.setEnabled(false);
					main.clientTimer.bt_col.setEnabled(false);
					
				} else if (type.equals("wakeUpMafia")) {//talk 보낼 때 스타일 줘서 마피아토크 만들기
					//마피아만 꺠우기
					main.area.append("마피아인 당신은 눈을 뜹니다.\n");
					main.area.append("오늘밤 살해할 사람을 한명 선택하세요.\n");
					//main.talk.setEditable(true);
					//새로운 JFrame 만들어서 죽일 사람 보내기
					main.f_mkf.setVisible(true);
					for (int i = 0; i < clientsCountInt; i++) { // 버튼들 1회용 활성화
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
