/*
 * 접속한 클라이언트간 서로 독립적으로 메세지를 주고 받으려면
 * 하나의 프로그램 내에서 독립적으로 수행가능한 실행단위인
 * 쓰레드가 필요하다!
 * */

package server.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

//이 클래스는 접속하는 클라이언트마다 1:1 대응하여 대화를 나눌 아바타와 같다!
public class ServerThread extends Thread{
	ServerMain main;
	Socket client;
	BufferedReader buffr;
	BufferedWriter buffw;
	ServerParser serverParser;
	boolean flag=true;
	
	public ServerThread(ServerMain main, Socket client) {
		this.main=main;
		this.client=client;
		serverParser=new ServerParser(main, this);
		
		try {
			buffr = new BufferedReader(new InputStreamReader(client.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void listen() {
		try {
			String msg=buffr.readLine();
			
			for(int i=0;i<main.list.size();i++) {
				ServerThread st=main.list.get(i);
				st.send(msg);	//받은 채팅문 다른 Client 들에게도 돌리기
			}
			//main.area.append(msg+"\n");	//기록남기기->serverParser 만들어서 수정할 예정
			serverParser.Parser(msg);
			main.bar.setValue(main.bar.getMaximum());
		} catch (IOException e) {
			//클라이언트가 나가면 catch문으로 들어온다.
			flag=false;
			main.list.remove(this);
			main.area.append("유저가 나갔습니다.\n");
			main.area.append("현재 "+main.list.size()+"명 이용중\n");
		}
	}
	public void send(String msg) {
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		while(flag) {
			listen();
		}
	}
}
