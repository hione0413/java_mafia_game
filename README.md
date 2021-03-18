# 마피아게임

<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfNDAg/MDAxNTU2NTQxNTQyMTIz.oCklGzBnvpDO6sOYPvba1nLr8eQYAtJN0zUJznLZwKcg.IYvUe6dIeS19G8vI8gqkwEQfxrFCMsramdgkLMSIh9gg.PNG.ppossing/01.png?type=w2" width="450px">
<br/>
<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfMTg4/MDAxNTU2NTQxNTQzNDEw.jjM_6OM-np-PnYrWw-lYdIjTMY71UxKTh15lZ6U8mc8g.CbhuFxQtiQ_WhzaqkgwnmmnxCL-lLYZVQRYJPpRKb_Eg.PNG.ppossing/04.png?type=w2" width="450px">
<h3>0. 주제선정 이유</h3>
  <li>Java의 소켓통신과 Thread에 대해 공부하고 싶어 진행한 개인프로젝트입니다.</li>  
<br>
<h3>1. 소개</h3>
  <ul>
    <li>Java의 소켓통신과 Thread, JSON을 활용해 만든 마피아 게임입니다.</li>
    <li>멀티 Thread를 이용해 다대다 통신을 구현하였습니다.</li>
    <li>서버 클라이언트에 내장된 TimeThread들을 이용해 턴이 진행됩니다.</li>
    <li>게임 진행은 기본적인 마피아게임의 룰을 채용하고 있습니다.</li>
  </ul>
<br/>
<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfMTYx/MDAxNTU2NTQxNTQzODgx.zU_T9ik9NgUj7wfH-p4iLfYqeNVCeVFGYbNsOkyh2uQg.cZBEpnVgUjNZRTb2hcdrYec3Nxxu7LNMTgNa3GTqw2Ug.PNG.ppossing/05.png?type=w2" width="450px">
<br/>
<p>서버에서는 하나의 Game Thread 안에서 다수의 Time Thread가 돌아가며 게임을 주관합니다.</p>
<p>Time Thread가 트리거가 되어 턴에 맞는 명령을 Client 들에게 Socket을 통해 JSONObject 형태로 전송합니다.</p>
<br/>
<p>
Server는 Server의 소켓으로 접속하는 Client들의 정보를 저장하고 있으며, 또한 게임 시작시 Client들에게 랜덤하게 직업을 부여합니다.
직업부여 방식은 제비뽑기 알고리즘을 체택했습니다.
간단하게 
1. 게임 참여인원에 맞춰 직업들이 준비된 배열을 준비하고<br/>
2. 랜덤하게 배열의 값을 추출한 뒤<br/>
3. 해당 게임에서의 직업 정보를 저장할 배열에 넣는 방식입니다.<br/>
</p>
<br/>
<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfODcg/MDAxNTU2NTQxNTQ0NDkz.v1qWgaruQa2SvSBS6aJpOJczLFxnFTU-cXrhOJKZz8Ig.Q08aRhdVPgQTSBpLs1BFF8qXsBBEvjV0llSsBkzHPn8g.PNG.ppossing/06.png?type=w2" width="450px">
<br/>
<p>
  Client는 Server로부터 받은 명령에 따라 채팅 기능을 포함한 다양한 기능이 on/off 됩니다.</p>
<p>기능의 on 되었을 때, Client는 자신의 직업이나 상황에 맞는 선택을 할 수 있고, 그 선택을 Server 에게 JSONObject 형태로 전송하여 처리하도록 합니다.
</p>
<br/>

<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfMjAz/MDAxNTU2NTQxNTQ1MDM2.KZLXaJT7ImkiqjCKFpHjvVZO1w85bS6h7A8wJq7f8csg.j41PjmeNhEGRc9EPm5PU2MFVo03emFaCMrWV_FKROewg.PNG.ppossing/07.png?type=w2" width="450px">
<br>
<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfMjgx/MDAxNTU2NTQxNTQ1NTU2.o5h4jDrHyFnxJpqsRglY7uD0tEdIfFtN9eRf5In5ot8g.2kDSmAlx6tMICDmlCGc0k477BNWPQ5xzwz8z4LFRBpAg.PNG.ppossing/08.png?type=w2" width="450px">
<br/>
<p>
마피아 직업을 배정받은 Client 들은 밤이 되면 마피아채팅이 활성화됩니다. 
제한 시간 동안 의견을 나눠 생존자 중 살해할 Client를 선택할 수 있습니다.
마피아 채팅은 아침이 되면 비활성화 됩니다.
</p>
<br/>
<br/>
<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfMTc1/MDAxNTU2NTQxNTQ1OTQx.dxwolfOKrmdxI_JNgZQVAy_EcbcBEHLkFUO9xTJx7jwg.44goCQKOaQwZe3bWTtikcwSTHsn1OFVEQpouP0zxue4g.PNG.ppossing/09.png?type=w2" width="450px">
<br>
<p>
모든 Client들은 하루에 1개의 투표권을 가지며, 자신의 투표 정보를 Server에게 전송합니다.
Server는 표를 voteBox라는 배열에 넣어 보관하다 개표 시간이 되면 정렬 알고리즘을 이용해 투표 결과를 출력합니다.
</p>
<br/>
<br/>
<img src="https://blogfiles.pstatic.net/MjAxOTA0MjlfMjg0/MDAxNTU2NTQxNTQ3MDE5.Y7ATvQWMx7tAl951r7TCR7EdffoS8-1JJ0RC-7TAjPIg.HueM5biGuHLVY5xXLYdW-FpZHRZ-iL76K_7fJRHiCYQg.PNG.ppossing/10.png?type=w2" width="450px">
<br/>
<p>
마침이 밝거나 밤이 되는 순간마다 생존자들과 마피아들의 인원수를 비교해 승패를 판정합니다.
</p>
<p>
제작 기간이 짧다보니 업그레이드 해야 할 사항과 코드가 많습니다. 고민과 보람도 많았지만 아쉬움도 많았던 프로젝트였습니다.
</p>
