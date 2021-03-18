package client.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientThread extends Thread{
	ClientMain main;
	Socket client;	//��ȭ�� ����
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
			main.area.append("������ ������ϴ�.\n");
			flag=false;
		}
	}
	
	//talk���� ���� ĥ ������ send() ȣ��
	public void send(String msg) {
		if(main.liveFlag) {
			//������ ������ ��� �ƹ�Ÿ�� write�� send�� ServerThread�� listen�� �̿��� ȣ������
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
