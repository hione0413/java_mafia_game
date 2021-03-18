package server.main;

import java.util.Random;
import java.util.Vector;

import org.json.simple.JSONObject;

/*
 * ��� client (main.list[i] �鿡�� min�� sec �������ֱ�)
 * server �ڽŵ� main.timer�� �ð� üũ
 * */
public class DayThread extends Thread{
	ServerTimer timer;
	Random random;
	
	boolean gameFlag=false;	//���� ���� �� true ������, ������ false��
	boolean dayFlag=false;	//false�� ������, true �� ��ħ����
	
	int whoIsMafia1;
	int whoIsMafia2;
	
	int hangMan=10;	//��ǥ�� ����� ���. 10�� ���̰�
	int killPoint=10;	//���Ǿư� �㿡 ������ ���.
	
	//������ǥ ���� �ݴ�ǥ ������
	int liveOrDieVoteAgree;
	int liveOrDieVoteDisagree;
	
	//��ǥ��
	Vector<String> voteArray;
	Vector<String> voteCountDummy=new Vector<String>();
	Vector<String> voteCount1=new Vector<String>();
	Vector<String> voteCount2=new Vector<String>();
	Vector<String> voteCount3=new Vector<String>();
	Vector<String> voteCount4=new Vector<String>();
	Vector<String> voteCount5=new Vector<String>();
	Vector<String> voteCount6=new Vector<String>();
	Vector<String> voteCount7=new Vector<String>();
	Vector<String> voteCount8=new Vector<String>();
	
	public DayThread(ServerTimer timer) {
		this.timer=timer;
		random=new Random();
		
		voteArray=new Vector<String>();
	}
	
	//�� 25��
	public synchronized void night() {
		timer.main.area.append("���� ����ִ� ������� ������ : "+timer.main.liveJobs+"�Դϴ�.\n");
		sendDayMsg("���� �Ǿ����ϴ�.");
		//nightStart
		JSONObject obj = new JSONObject();
		obj.put("Type", "nightStart"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj.toString());
		}
		
		timer.sec=25;
		//timer.sec=25;
		Thread timerThread = new Thread() {
			public void run() {
				timer.timer();
			}
		};
		timer.timerFlag=true;
		timerThread.start();
		
		//���� �� ���Ǿ�, ����, �ǻ翡�� ������ �� �ִ� ���� �ֱ�
		wakeUpMafia();
		
		try {
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		JSONObject obj2 = new JSONObject();
		obj2.put("Type", "nightEnd"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st2=timer.main.list.get(i);
			st2.send(obj2.toString());
		}
		
		youDie(killPoint);
		win();	//���Ǿƶ� �ù� ���غ���
		dayFlag=true;
	}
	//�� 02:30��
	public void morning() {
		sendDayMsg("���� ��ҽ��ϴ�.");
		//timer.sec=20;
		timer.min=2;
		timer.sec=30;
		Thread timerThread = new Thread() {
			public void run() {
				timer.timer();
			}
		};
		timer.timerFlag=true;
		timerThread.start();
		try {
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		vote();
	}
	
	//��ǥ 25��
	public void vote() {
		sendDayMsg("��ǥ�� ���۵Ǿ����ϴ�. ���ǾƸ� �������ּ���.");
		timer.sec=30;
		//timer.sec=25;
		Thread timerThread = new Thread() {
			public void run() {
				timer.timer();
			}
		};
		timer.timerFlag=true;
		timerThread.start();
		
		//��� ������ ���� ���ϰ��� ��ƾ���
		getVote();

		try {
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		voteEnd();
		lastChance();
	}
	//���Ĺݷ� 25��
	public void lastChance() {
		sendDayMsg("���� �ݷ�");
		timer.sec=15;
		//timer.sec=25;
		Thread timerThread = new Thread() {
			public void run() {
				timer.timer();
			}
		};
		timer.timerFlag=true;
		timerThread.start();
		
		try {
			System.out.println("Ÿ�̸� ���� ������ dayThread �����!");
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//�߾�� �����ֱ�
		giveVoice();
		liveOrDie();
	}
	//������ǥ
	public void liveOrDie() {
		sendDayMsg("������ǥ");
		timer.sec=15;
		//timer.sec=25;
		//������ǥ ���� - ����ǥ ������
		System.out.println("�츱�� ������ ��ǥ����.");
		JSONObject obj = new JSONObject();
		obj.put("Type", "liveOrDieVote"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj.toString());
		}
		
		Thread timerThread = new Thread() {
			public void run() {
				timer.timer();
			}
		};
		timer.timerFlag=true;
		timerThread.start();
		try {
			System.out.println("Ÿ�̸� ���� ������ dayThread �����!");
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//������ǥ ����Ǿ����� �˷���
		JSONObject obj2 = new JSONObject();
		obj2.put("Type", "liveOrDieVoteEnd"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj2.toString());
		}

		//������ǥ �ؼ� ���� ������ ���� ���̱�
		
		judgement();	//���� ��� �Ѱܾߵ�
		win();
		createVoteCount();
		dayFlag=false;	//������
	}
	public void win() {
		System.out.println("���и� üũ�մϴ�.");
		//boolean n=false;//����η��� ���� �ӽú���
		//��ȹ
		//������ �� ����
		//mafia �迭�� civilian �迭 ���� ���� ���纸�� ���� count ��
		//count ���ؼ� ���� ����
		int civilianLive=0;
		int mafiaLive=0;
		System.out.println("���� �������� �����ִ� ������ "+timer.main.liveJobs);
		for(int i=0; i<timer.main.liveJobs.size();i++) {
			String count=timer.main.liveJobs.get(i);
			if(count.equals("Mafia1")||count.equals("Mafia2")) {
				mafiaLive++;
				System.out.println("���Ǿƴ� ���� "+mafiaLive+"�� �������ֽ��ϴ�.");
			} else if (count.equals("Civilian")) {
				civilianLive++;
				System.out.println("�ù��� ���� "+civilianLive+"�� �������ֽ��ϴ�.");
			}
		}
		
		//�ο��� ���� ������ gameFlag �� false�� �ְ� ���� ��
		//���Ǿ� ��� ���
		if( mafiaLive==0 ) {
			JSONObject obj = new JSONObject();
			obj.put("Type", "winCivilian"); 
			for(int i=0;i<timer.main.list.size();i++) {
				ServerThread st=timer.main.list.get(i);
				st.send(obj.toString());
			}
			timer.main.area.append("�ù��� �¸��Ͽ����ϴ�.\n");
			System.out.println("�ù��� �¸��Ͽ����ϴ�");
			gameFlag=false;
		}
		
		//���Ǿ� ���� �ùΰ� �������ų� �� ������
		if( mafiaLive>=civilianLive ) {	
			JSONObject obj = new JSONObject();
			obj.put("Type", "winMafia"); 
			for(int i=0;i<timer.main.list.size();i++) {
				ServerThread st=timer.main.list.get(i);
				st.send(obj.toString());
			}
			timer.main.area.append("���Ǿư� �¸��Ͽ����ϴ�.\n");
			System.out.println("���Ǿư� �¸��Ͽ����ϴ�");
			gameFlag=false;
		}
	}
	//���� ����!
	public void run() {
		//�� ó���� ���� ����
		settingGame();
		while(gameFlag) {
			if(dayFlag) {
				morning();
			} else {
				night();
			}
		}
	}
	public void settingGame() {
		timer.main.settintJobs();	//�ο����� ���� ���ӿ��� ���� ���� ����
		setJobs();	//�� �������� ���� ����
	}
	public void setJobs() {
		int user=timer.main.list.size();//�������ִ� ������ ��
		
		for(int i=0;i<user;i++) {
			int ran=random.nextInt(user-i);	//������ ����
			String job=timer.main.jobsReady.get(ran);	//�غ�� ���� �� �ϳ� ��������
			if(job=="Mafia1"){
				whoIsMafia1=timer.main.jobs.size();
				System.out.println(whoIsMafia1+"�� ������ Mafia1�Դϴ�.");
			}
			
			if(job=="Mafia2"){
				whoIsMafia2=timer.main.jobs.size();
				System.out.println(whoIsMafia2+"�� ������ Mafia2�Դϴ�.");
			}
			
			if(job=="Civilian") {
				System.out.println(timer.main.jobs.size()+"�� ������ �ù��Դϴ�.");
			}
			//�ش� �������� �� ���Ǿƶ�� �˷��ֱ�
			JSONObject obj = new JSONObject();
			obj.put("Type", "yourJob"); 
			obj.put("ClientNo", Integer.toString(timer.main.jobs.size()));
			obj.put("Job", job);
			obj.put("ClientsCount", Integer.toString(timer.main.list.size()));
			ServerThread st=timer.main.list.get(timer.main.jobs.size());
			System.out.println("���� ���� �� send�ϴ� JSON�� : "+obj.toString());
			st.send(obj.toString());
			
			timer.main.jobs.add(job);	//������ ���� ����
			timer.main.liveJobs.add(job);	//�����ڵ� ��������
			timer.main.jobsReady.remove(ran);
		}
		System.out.println("setJobs�� ��� : "+timer.main.jobs);
		//���д� �����ڵ� �迭�� ������ ������ ����
	}
	
	public void wakeUpMafia() {
		int mafia1=10;	//10�� ���̰�
		int mafia2=10;
		
		for(int i=0;i<timer.main.liveJobs.size();i++) {
			String job=timer.main.liveJobs.get(i);
			if(job.equals("Mafia1")) {
				mafia1=i;
			}else if(job.equals("Mafia2")) {
				mafia2=i;
			}
		}
		if(mafia1!=10) {
			JSONObject obj = new JSONObject();
			obj.put("Type", "wakeUpMafia");
			ServerThread st=timer.main.list.get(mafia1);
			System.out.println("1�� ���Ǿ��� "+mafia1+"��° ������ ����ϴ�.");
			st.send(obj.toString());
		}
		if(mafia2!=10) {
			JSONObject obj = new JSONObject();
			obj.put("Type", "wakeUpMafia");
			ServerThread st=timer.main.list.get(mafia2);
			System.out.println("2�� ���Ǿ��� "+mafia2+"��° ������ ����ϴ�.");
			st.send(obj.toString());
		}
		
	};
	
	public void sendDayMsg(String msg) {
		JSONObject obj = new JSONObject();
		obj.put("Type", "sendDayMsg");
		obj.put("dayMsg", msg); 
		timer.main.area.append("----------------------------------------\n");
		timer.main.area.append(msg+"\n");
		timer.main.area.append("----------------------------------------\n");
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj.toString());
		}
	}
	public void getVote() {	//��ǥ���� �����޶�� ClientParser���� ��Ź��.
		JSONObject obj = new JSONObject();
		obj.put("Type", "vote"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			System.out.println("��ǥ���� �����ݴϴ�.");
			st.send(obj.toString());
		}
	}
	
	//ServerParser�� ������ ��ǥ�������� ��ǥ�Կ� �������
	public void stackVote(String select) {	
		System.out.println("stackVote���� ���ù��� �ĺ��� : "+select);
		switch(select){	
			case "0" : voteCount1.add("��ǥ");break;
			case "1" : voteCount2.add("��ǥ");break;
			case "2" : voteCount3.add("��ǥ");break;
			case "3" : voteCount4.add("��ǥ");break;
			case "4" : voteCount5.add("��ǥ");break;
			case "5" : voteCount6.add("��ǥ");break;
			case "6" : voteCount7.add("��ǥ");break;
			case "7" : voteCount8.add("��ǥ");
		}
	}
	
	public void voteEnd() {	
		//��ǥ�� ������ ��ǥ ����� �����ϰ� ��ǥ���� ���
		//��ǥ ���� ��ȹ
		//��ǥ���� 8�� ���� ���� ��ǥ�� �������
		//��ǥ�Ե��� size�� ���ؼ� ���� ū ��ǥ���� ������ ��÷
		//
		Vector total=voteTotal();
		
		whoIsWiner(total);
		
		//Client �鿡�� ��ǥ�� ����Ǿ����� �˸�
		JSONObject obj = new JSONObject();
		obj.put("Type", "voteEnd"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			System.out.println("��ǥ�� �����մϴ�.");
			st.send(obj.toString());
		}
		
		
	}
	
	//��ǥ�� ����
	public void createVoteCount() {	
		voteCount1.clear();
		voteCount2.clear();
		voteCount3.clear();
		voteCount4.clear();
		voteCount5.clear();
		voteCount6.clear();
		voteCount7.clear();
		voteCount8.clear();
		liveOrDieVoteAgree=0;
		liveOrDieVoteDisagree=0;
		hangMan=0;
	}
	
	//��� ��ǥ���� ���ؼ� ���� ���� ǥ�� ���� ��ǥ���� ��ȯ��
	public Vector voteTotal() {	
		Vector a=whoIsBigMan(voteCount1, voteCount2);	
		a=whoIsBigMan(a, voteCount3);	
		a=whoIsBigMan(a, voteCount4);	
		a=whoIsBigMan(a, voteCount5);	
		a=whoIsBigMan(a, voteCount6);	
		a=whoIsBigMan(a, voteCount7);
		a=whoIsBigMan(a, voteCount8);	
		return a;
	}
	
	//��ǥ�� size ���Ϸ��� ���� Sample �޼���
	public Vector whoIsBigMan(Vector a,Vector b) {
		if(a.size() > b.size()) {
			return a;
		} else if(a.size() < b.size()) {
			return b;
		} else if(a.size() == b.size()) {
			return voteCountDummy;
		}
		return voteCountDummy;
	}
	public void whoIsWiner(Vector total) {
		if(total==voteCountDummy){	
			System.out.println("������� ����� �����ϴ�.");
		}else if(total==voteCount1){
			System.out.println("������� ����� ù��° �����Դϴ�.");
			//��ȹ
			//����� ���� ���� ������ �������� JTextField�� ��Ȱ��ȭ�ϱ�
			//���Ĺݷ� ������ ���� Ȱ��ȭ���Ѽ� Ǯ���ֱ�
			//�̰� ���� ��û �Ұ� �����ϱ� �޼���� ����
			shutVoice(0);
			hangMan=0;
		}else if(total==voteCount2){
			System.out.println("������� ����� �ι�° �����Դϴ�.");
			shutVoice(1);
			hangMan=1;
		}else if(total==voteCount3){
			System.out.println("������� ����� ����° �����Դϴ�.");
			shutVoice(2);
			hangMan=2;
		}else if(total==voteCount4){
			System.out.println("������� ����� �׹�° �����Դϴ�.");
			shutVoice(3);
			hangMan=3;
		}else if(total==voteCount5){
			System.out.println("������� ����� �ټ���° �����Դϴ�.");
			shutVoice(4);
			hangMan=4;
		}else if(total==voteCount6){
			System.out.println("������� ����� ������° �����Դϴ�.");
			shutVoice(5);
			hangMan=5;
		}else if(total==voteCount7){
			System.out.println("������� ����� �ϰ���° �����Դϴ�.");
			shutVoice(6);
			hangMan=6;
		}else if(total==voteCount8){
			System.out.println("������� ����� ������° �����Դϴ�.");
			shutVoice(7);
			hangMan=7;
		}
	}
	public void shutVoice(int who) {	//���Ĺݷ� �� �ٸ� ��� �����ϰ� �����
		JSONObject obj = new JSONObject();
		obj.put("Type", "lastChance");
		obj.put("who",""+0+"");
		for(int i=0;i<timer.main.list.size();i++) {
			if(who!=i) {
				ServerThread st=timer.main.list.get(i);
				st.send(obj.toString());
			}
		}
	}
	
	public void liveOrDieVote(int result) {
		if (result==0) {
			liveOrDieVoteAgree++;
		} else {
			liveOrDieVoteDisagree++;
		}
	}
	
	//�����뿡 ���� ��� ���̱�
	public void judgement() {
		if(liveOrDieVoteAgree>liveOrDieVoteDisagree) {
			youDie(hangMan);
		}
		
	}
	
	public void giveVoice() {	//���Ĺݷ� ������ �ٸ� ������Ե� �߾�� �ֱ�-���� ������״� �ָ� �ȵ�;; ���?
		JSONObject obj = new JSONObject();
		obj.put("Type", "lastChanceEnd");
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj.toString());
		}
	}
	
	//�ش� ���� �����Ű�� �޼���
	public void youDie(int diePeaple) {
		System.out.println("youDie���� �Ѱܹ��� diePeaple�� : "+diePeaple);
		if(diePeaple>=0 && diePeaple<=8) {	//10�� ���̰�
			JSONObject obj = new JSONObject();
			obj.put("Type", "youDie");
			ServerThread st=timer.main.list.get(diePeaple);
			st.send(obj.toString());
			
			JSONObject obj2 = new JSONObject();
			obj2.put("Type", "youDieNotify");
			obj2.put("who", Integer.toString(diePeaple));
			obj2.put("msg", ""+(diePeaple+1)+"��° ������ �׾����ϴ�.");
			for(int i=0;i<timer.main.list.size();i++) {
				ServerThread st2=timer.main.list.get(i);
				st2.send(obj2.toString());
			}
			
			timer.main.liveJobs.set(diePeaple, "DIE");
			timer.main.area.append((diePeaple+1)+"��° ������ �׾����ϴ�.\n");
		}
	}
}

