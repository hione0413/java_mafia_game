package server.main;

import java.util.Random;
import java.util.Vector;

import org.json.simple.JSONObject;

/*
 * 모든 client (main.list[i] 들에게 min과 sec 세팅해주기)
 * server 자신도 main.timer에 시간 체크
 * */
public class DayThread extends Thread{
	ServerTimer timer;
	Random random;
	
	boolean gameFlag=false;	//게임 시작 시 true 값으로, 끝나면 false로
	boolean dayFlag=false;	//false면 밤으로, true 면 아침으로
	
	int whoIsMafia1;
	int whoIsMafia2;
	
	int hangMan=10;	//투표로 지목된 사람. 10은 더미값
	int killPoint=10;	//마피아가 밤에 지목한 사람.
	
	//찬반투표 찬성 반대표 모으기
	int liveOrDieVoteAgree;
	int liveOrDieVoteDisagree;
	
	//투표함
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
	
	//밤 25초
	public synchronized void night() {
		timer.main.area.append("현재 살아있는 사람들의 직업은 : "+timer.main.liveJobs+"입니다.\n");
		sendDayMsg("밤이 되었습니다.");
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
		
		//직업 중 마피아, 경찰, 의사에게 선택할 수 있는 권한 주기
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
		win();	//마피아랑 시민 비교해보기
		dayFlag=true;
	}
	//낮 02:30초
	public void morning() {
		sendDayMsg("날이 밝았습니다.");
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
	
	//투표 25초
	public void vote() {
		sendDayMsg("투표가 시작되었습니다. 마피아를 지목해주세요.");
		timer.sec=30;
		//timer.sec=25;
		Thread timerThread = new Thread() {
			public void run() {
				timer.timer();
			}
		};
		timer.timerFlag=true;
		timerThread.start();
		
		//모든 유저의 유저 리턴값을 모아야함
		getVote();

		try {
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		voteEnd();
		lastChance();
	}
	//최후반론 25초
	public void lastChance() {
		sendDayMsg("최후 반론");
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
			System.out.println("타이머 끝날 때까지 dayThread 대기중!");
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//발언권 돌려주기
		giveVoice();
		liveOrDie();
	}
	//찬반투표
	public void liveOrDie() {
		sendDayMsg("찬반투표");
		timer.sec=15;
		//timer.sec=25;
		//찬반투표 시작 - 찬반표 얻어오기
		System.out.println("살릴지 죽일지 투표시작.");
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
			System.out.println("타이머 끝날 때까지 dayThread 대기중!");
			timerThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//찬반투표 종료되었음을 알려줌
		JSONObject obj2 = new JSONObject();
		obj2.put("Type", "liveOrDieVoteEnd"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj2.toString());
		}

		//찬반투표 해서 찬성 많으면 유저 죽이기
		
		judgement();	//죽일 사람 넘겨야됨
		win();
		createVoteCount();
		dayFlag=false;	//밤으로
	}
	public void win() {
		System.out.println("승패를 체크합니다.");
		//boolean n=false;//멈춰두려고 넣은 임시변수
		//계획
		//직업들 값 빼옴
		//mafia 배열과 civilian 배열 만들어서 조건 맞춰보고 각각 count 함
		//count 비교해서 승패 결정
		int civilianLive=0;
		int mafiaLive=0;
		System.out.println("승패 판정에서 남아있는 직업은 "+timer.main.liveJobs);
		for(int i=0; i<timer.main.liveJobs.size();i++) {
			String count=timer.main.liveJobs.get(i);
			if(count.equals("Mafia1")||count.equals("Mafia2")) {
				mafiaLive++;
				System.out.println("마피아는 현재 "+mafiaLive+"명 생존해있습니다.");
			} else if (count.equals("Civilian")) {
				civilianLive++;
				System.out.println("시민은 현재 "+civilianLive+"명 생존해있습니다.");
			}
		}
		
		//인원수 조건 맞으면 gameFlag 값 false로 주고 게임 끝
		//마피아 모두 사망
		if( mafiaLive==0 ) {
			JSONObject obj = new JSONObject();
			obj.put("Type", "winCivilian"); 
			for(int i=0;i<timer.main.list.size();i++) {
				ServerThread st=timer.main.list.get(i);
				st.send(obj.toString());
			}
			timer.main.area.append("시민이 승리하였습니다.\n");
			System.out.println("시민이 승리하였습니다");
			gameFlag=false;
		}
		
		//마피아 수가 시민과 같아지거나 더 많아짐
		if( mafiaLive>=civilianLive ) {	
			JSONObject obj = new JSONObject();
			obj.put("Type", "winMafia"); 
			for(int i=0;i<timer.main.list.size();i++) {
				ServerThread st=timer.main.list.get(i);
				st.send(obj.toString());
			}
			timer.main.area.append("마피아가 승리하였습니다.\n");
			System.out.println("마피아가 승리하였습니다");
			gameFlag=false;
		}
	}
	//게임 시작!
	public void run() {
		//맨 처음에 직업 배정
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
		timer.main.settintJobs();	//인원수에 따른 게임에서 쓰일 직업 세팅
		setJobs();	//각 유저에게 직업 세팅
	}
	public void setJobs() {
		int user=timer.main.list.size();//접속해있는 유저의 수
		
		for(int i=0;i<user;i++) {
			int ran=random.nextInt(user-i);	//랜덤값 추출
			String job=timer.main.jobsReady.get(ran);	//준비된 직업 중 하나 랜덤추출
			if(job=="Mafia1"){
				whoIsMafia1=timer.main.jobs.size();
				System.out.println(whoIsMafia1+"번 유저는 Mafia1입니다.");
			}
			
			if(job=="Mafia2"){
				whoIsMafia2=timer.main.jobs.size();
				System.out.println(whoIsMafia2+"번 유저는 Mafia2입니다.");
			}
			
			if(job=="Civilian") {
				System.out.println(timer.main.jobs.size()+"번 유저는 시민입니다.");
			}
			//해당 유저에게 너 마피아라고 알려주기
			JSONObject obj = new JSONObject();
			obj.put("Type", "yourJob"); 
			obj.put("ClientNo", Integer.toString(timer.main.jobs.size()));
			obj.put("Job", job);
			obj.put("ClientsCount", Integer.toString(timer.main.list.size()));
			ServerThread st=timer.main.list.get(timer.main.jobs.size());
			System.out.println("직업 배정 시 send하는 JSON은 : "+obj.toString());
			st.send(obj.toString());
			
			timer.main.jobs.add(job);	//유저들 직업 세팅
			timer.main.liveJobs.add(job);	//생존자들 직업세팅
			timer.main.jobsReady.remove(ran);
		}
		System.out.println("setJobs의 결과 : "+timer.main.jobs);
		//승패는 생존자들 배열에 직업들 지워서 판정
	}
	
	public void wakeUpMafia() {
		int mafia1=10;	//10은 더미값
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
			System.out.println("1번 마피아인 "+mafia1+"번째 유저를 깨웁니다.");
			st.send(obj.toString());
		}
		if(mafia2!=10) {
			JSONObject obj = new JSONObject();
			obj.put("Type", "wakeUpMafia");
			ServerThread st=timer.main.list.get(mafia2);
			System.out.println("2번 마피아인 "+mafia2+"번째 유저를 깨웁니다.");
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
	public void getVote() {	//투표권을 나눠달라고 ClientParser에게 부탁함.
		JSONObject obj = new JSONObject();
		obj.put("Type", "vote"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			System.out.println("투표권을 나눠줍니다.");
			st.send(obj.toString());
		}
	}
	
	//ServerParser가 수집한 투표용지들을 투표함에 집어넣음
	public void stackVote(String select) {	
		System.out.println("stackVote에서 선택받은 후보는 : "+select);
		switch(select){	
			case "0" : voteCount1.add("한표");break;
			case "1" : voteCount2.add("한표");break;
			case "2" : voteCount3.add("한표");break;
			case "3" : voteCount4.add("한표");break;
			case "4" : voteCount5.add("한표");break;
			case "5" : voteCount6.add("한표");break;
			case "6" : voteCount7.add("한표");break;
			case "7" : voteCount8.add("한표");
		}
	}
	
	public void voteEnd() {	
		//투표가 끝나면 투표 결과를 집계하고 투표함을 비움
		//투표 집계 계획
		//투표함을 8개 만들어서 각각 한표씩 집어넣음
		//투표함들의 size를 비교해서 가장 큰 투표함의 주인이 당첨
		//
		Vector total=voteTotal();
		
		whoIsWiner(total);
		
		//Client 들에게 투표가 종료되었음을 알림
		JSONObject obj = new JSONObject();
		obj.put("Type", "voteEnd"); 
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			System.out.println("투표를 종료합니다.");
			st.send(obj.toString());
		}
		
		
	}
	
	//투표함 비우기
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
	
	//모든 투표함을 비교해서 가장 많은 표를 받은 투표함을 반환함
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
	
	//투표함 size 비교하려고 만든 Sample 메서드
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
			System.out.println("지목받은 사람이 없습니다.");
		}else if(total==voteCount1){
			System.out.println("지목받은 사람은 첫번째 유저입니다.");
			//계획
			//지목된 유저 외의 나머지 유저들의 JTextField를 비활성화하기
			//최후반론 끝나면 전부 활성화시켜서 풀어주기
			//이거 재사용 엄청 할거 같으니까 메서드로 빼자
			shutVoice(0);
			hangMan=0;
		}else if(total==voteCount2){
			System.out.println("지목받은 사람은 두번째 유저입니다.");
			shutVoice(1);
			hangMan=1;
		}else if(total==voteCount3){
			System.out.println("지목받은 사람은 세번째 유저입니다.");
			shutVoice(2);
			hangMan=2;
		}else if(total==voteCount4){
			System.out.println("지목받은 사람은 네번째 유저입니다.");
			shutVoice(3);
			hangMan=3;
		}else if(total==voteCount5){
			System.out.println("지목받은 사람은 다섯번째 유저입니다.");
			shutVoice(4);
			hangMan=4;
		}else if(total==voteCount6){
			System.out.println("지목받은 사람은 여섯번째 유저입니다.");
			shutVoice(5);
			hangMan=5;
		}else if(total==voteCount7){
			System.out.println("지목받은 사람은 일곱번째 유저입니다.");
			shutVoice(6);
			hangMan=6;
		}else if(total==voteCount8){
			System.out.println("지목받은 사람은 여덟번째 유저입니다.");
			shutVoice(7);
			hangMan=7;
		}
	}
	public void shutVoice(int who) {	//최후반론 중 다른 사람 조용하게 만들기
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
	
	//사형대에 오른 사람 죽이기
	public void judgement() {
		if(liveOrDieVoteAgree>liveOrDieVoteDisagree) {
			youDie(hangMan);
		}
		
	}
	
	public void giveVoice() {	//최후반론 끝나고 다른 사람에게도 발언권 주기-죽은 사람한테는 주면 안됨;; 어떻게?
		JSONObject obj = new JSONObject();
		obj.put("Type", "lastChanceEnd");
		for(int i=0;i<timer.main.list.size();i++) {
			ServerThread st=timer.main.list.get(i);
			st.send(obj.toString());
		}
	}
	
	//해당 유저 사망시키는 메서드
	public void youDie(int diePeaple) {
		System.out.println("youDie에서 넘겨받은 diePeaple은 : "+diePeaple);
		if(diePeaple>=0 && diePeaple<=8) {	//10은 더미값
			JSONObject obj = new JSONObject();
			obj.put("Type", "youDie");
			ServerThread st=timer.main.list.get(diePeaple);
			st.send(obj.toString());
			
			JSONObject obj2 = new JSONObject();
			obj2.put("Type", "youDieNotify");
			obj2.put("who", Integer.toString(diePeaple));
			obj2.put("msg", ""+(diePeaple+1)+"번째 유저가 죽었습니다.");
			for(int i=0;i<timer.main.list.size();i++) {
				ServerThread st2=timer.main.list.get(i);
				st2.send(obj2.toString());
			}
			
			timer.main.liveJobs.set(diePeaple, "DIE");
			timer.main.area.append((diePeaple+1)+"번째 유저는 죽었습니다.\n");
		}
	}
}

