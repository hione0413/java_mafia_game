/*
 * ������ Ŭ���̾�Ʈ�� ���� ���������� �޼����� �ְ� ��������
 * �ϳ��� ���α׷� ������ ���������� ���డ���� ���������
 * �����尡 �ʿ��ϴ�!
 * */

package server.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

//�� Ŭ������ �����ϴ� Ŭ���̾�Ʈ���� 1:1 �����Ͽ� ��ȭ�� ���� �ƹ�Ÿ�� ����!
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
				st.send(msg);	//���� ä�ù� �ٸ� Client �鿡�Ե� ������
			}
			//main.area.append(msg+"\n");	//��ϳ����->serverParser ���� ������ ����
			serverParser.Parser(msg);
			main.bar.setValue(main.bar.getMaximum());
		} catch (IOException e) {
			//Ŭ���̾�Ʈ�� ������ catch������ ���´�.
			flag=false;
			main.list.remove(this);
			main.area.append("������ �������ϴ�.\n");
			main.area.append("���� "+main.list.size()+"�� �̿���\n");
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
