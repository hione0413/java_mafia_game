package client.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientThread extends Thread{
	ClientMain main;
	Socket client;	//대화용 소켓
	BufferedReader buffr;
	BufferedWriter buffw;
	ClientParser clientParser;
	boolean flag=true;
	
	
	public ClientThread(ClientMain main, Socket client) {
		this.main=main;
		clientParser=new ClientParser(main, this);
				
		try {
			buffr=new BufferedReader(new InputStreamReader(client.getInputStream()));
			buffw=new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void listen() {
		try {
			String msg=buffr.readLine();
			//main.area.append(msg+"\n");
			clientParser.Parser(msg);
			main.bar.setValue(main.bar.getMaximum());
		} catch (IOException e) {
			main.area.append("서버가 끊겼습니다.\n");
			flag=false;
		}
	}
	
	//talk에서 엔터 칠 때마다 send() 호출
	public void send(String msg) {
		if(main.liveFlag) {
			//서버에 접속한 모든 아바타의 write의 send를 ServerThread의 listen을 이용해 호출하자
			try {
				buffw.write(msg+"\n");
				buffw.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		while(flag) {
			listen();
		}
	}
}
