package server.main;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ServerParser {
	ServerMain main;
	ServerThread serverThread;
	String msg;
	Object obj;
	JSONArray array = new JSONArray();
	
	public ServerParser(ServerMain main, ServerThread clientThread) {
		this.main=main;
		this.serverThread=clientThread;
	}
	
	//JSON값을 Parser(JSON) 형태로 넘겨받음
	public void Parser(String msg) {
		this.msg=msg;
		//System.out.println("Parser가 넘겨받은 값은 : "+msg);
		JSONParser parser = new JSONParser();
		String sql=null;
		Object obj2 = null;
		try {
			obj2=parser.parse(msg);
			if(array.getClass() != obj2.getClass() ) {//?????
				JSONObject jsonObj = (JSONObject)obj2;
				String type = (String)jsonObj.get("Type");
				if(type.equals("talk")) {
					String talk = (String)jsonObj.get("talk");
					main.area.append(talk+"\n");
					
				}if(type.equals("mafiaTalk")) {
					String talk = (String)jsonObj.get("talk");
					//main.area.append(talk);
					
				}else if(type.equals("timePlus")) {
					main.timer.timeUp();
					
				}else if(type.equals("timeMinus")) {
					main.timer.timeMinus();
					
				}else if(type.equals("voteSubmit")) {	
					//투표용지 받아서 투표함에 넣음
					String select=(String)jsonObj.get("select");
					main.timer.dayThread.stackVote(select);
					
					//받은걸 유저에게 뿌리자
					String voteResult=(String)jsonObj.get("voteResult");
					JSONObject obj = new JSONObject();
					obj.put("Type", "talk");
					obj.put("talk", voteResult); 
					main.area.append(msg+"\n");
					for(int i=0;i<main.list.size();i++) {
						ServerThread st=main.list.get(i);
						st.send(obj.toString());
					}
					main.area.append(voteResult);
					
				}else if (type.equals("liveOrDieVoteResult")) {	
					//찬반투표 결과 받음
					String select=(String)jsonObj.get("liveOrDieSelect");
					System.out.println("ServerParser에서 받은 찬반표는 "+select);
					if(select.equals("찬성")) {
						//찬성표 올림
						main.timer.dayThread.liveOrDieVote(0);
					} else {
						//반대표 올림
						main.timer.dayThread.liveOrDieVote(1);
					}
				} else if (type.equals("mafiaKillPoint")){
					String choice = (String)jsonObj.get("choice");
					main.timer.dayThread.killPoint=Integer.parseInt(choice);
					
					//지목 받은걸 다른 마피아에게 뿌리자
					String choiceMsg = (String)jsonObj.get("choiceMsg");
					JSONObject obj = new JSONObject();
					obj.put("Type", "mafiaTalk");
					obj.put("talk", choiceMsg); 
					//main.area.append(msg+"\n");
					
					ServerThread st=main.list.get(main.timer.dayThread.whoIsMafia1);
					ServerThread st2=main.list.get(main.timer.dayThread.whoIsMafia2);
					st.send(obj.toString());
					st2.send(obj.toString());
					
					main.area.append("마피아 : "+choiceMsg+"\n");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
}
