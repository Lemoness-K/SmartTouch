var menu=0,
	submenu=0
	depth=0;

//아이콘 경로 설정
var title_menu = ["res/img/sedan2.png",			// 메인
                  "res/img/map30.png",			// Tmap 아이콘
                  "res/img/musical.png",		// 노래 아이콘
                  "res/img/call5.png",			// 전화 아이콘
                  "res/img/machine2.png"];		// 설정 아이콘
var subtitle_menu = new Array(5);
	subtitle_menu[0] = [];
	subtitle_menu[1] = ["res/img/dwelling1.png",			// Tmap - 집
	                    "res/img/buildings5.png",			// Tmap - 회사
	                    "res/img/favourites7.png"];			// Tmap - 즐겨찾기
	subtitle_menu[2] = ["res/img/musicalnp.png",
	                    "res/img/volume26.png"];			// 노래 !!!!!!!!!!!!사용법으로 바꿀것
	subtitle_menu[3] = ["res/img/user62.png",
	                    "res/img/user62.png",
	                    "res/img/user62.png",
	                    "res/img/user62.png"];				// 전화 - 즐겨찾기
	subtitle_menu[4] = ["res/img/bluetooth.png"];

(function() {
	//화면 꺼짐 방지
	tizen.power.request("SCREEN", "SCREEN_DIM");
	
	var page = document.getElementById( "main" );
	var text = document.getElementById("maintext");
	var changer = document.getElementById( "hsectionchanger" );
	var s_pointX, s_pointY, e_pointX, e_pointY, lengthX, lengthY;
	var idx=1;
	console.log("javascript loaded");

	page.addEventListener( "touchstart", function(e){
		s_pointX = e.touches[0].clientX;
		s_pointY = e.touches[0].clientY;
	});

	page.addEventListener( "touchmove", function(e){
		e_pointX = e.touches[0].clientX;
		e_pointY = e.touches[0].clientY;
	});

	page.addEventListener( "touchend", function(){
		lengthX = (s_pointX > e_pointX)?s_pointX-e_pointX:e_pointX-s_pointX;
		lengthY = (s_pointY > e_pointY)?s_pointY-e_pointY:e_pointY-s_pointY;

		// SAP 연결 확인
		if(!SASocket){
			//SAP 연결 안되어있으면 연결
			connect();
			console.log("reconnect");
			dispPopup("핸드폰과 연결되지 않았습니다."+'</br>'+"연결을 진행합니다.");
		}
		
		//터치 분석
		else if(lengthX + lengthY <= 60){
			//touch
			console.log("touched");
			controlPage("touch");
		}
		else if(lengthX > lengthY){
			if(s_pointX > e_pointX){
				//touch left
				console.log("touch left");
				if(!depth){							//메인메뉴일때 상황
					menu++;
					if(menu >= title_menu.length){	//메뉴 범위 오버
						menu = title_menu.length-1;
					}else{
						controlPage("left");
					}
				}
				else{								//서브메뉴일때 상황
					submenu++;
					if(menu === 2 && submenu){		// 노래일때
						submenu = 0;
						controlPage("left");
					}
					if(submenu >= subtitle_menu[menu].length){	//메뉴 범위 오버
						submenu = subtitle_menu[menu].length-1;
					}else{
						controlPage("left");
					}
				}
				


			}else{
				//touch right
				console.log("touch right");
				if(!depth){				//메인메뉴일때 상황
					if(menu > 0){
						menu--;
						controlPage("right");
					}else{		//메뉴 범위 오버
					}
				}else{					//서브메뉴일때 상황
					if(menu === 2){		// 노래일때
						submenu = 0;
						controlPage("right");
					}
					else if(submenu > 0){
						submenu--;
						controlPage("right");
					}
				}
			}
		}
		else{
			if(s_pointY > e_pointY){
				//touch up
				console.log("touch up");
				depth++;
				if(depth && menu === 0){				// 홈 화면에서 deth 늘릴때
					depth--;
				}else if(depth > 1 && menu !== 2){	// 노래가 아닌데 depth늘릴때
					depth--;
				}else if(depth > 2){			// depth 범위 오버
					depth--;
				}else
					controlPage("up");				
				
			}else{
				//touch down
				console.log("touch down");
				depth--;
				controlPage("down");
			}
		}
		
		//종료후 length 초기화
		s_pointX=0, s_pointY=0, e_pointX=0, e_pointY=0, lengthX=0, lengthY=0;
	});

	page.addEventListener( "pagehide", function() {
		// release object
		//화면 꺼짐 방지 해제
		tizen.power.release("SCREEN");
	});
})();

function controlPage(action){

	console.log("menu:"+menu+"submenu:"+submenu+"depth:"+depth);
	console.log("action:"+action);
	// 위치에 따른 행동 설정
	switch(depth){
		case -1 :
			//menu[1]로 돌아가기
			depth = 0;
			menu = 1;
			submenu = 0;
			dispchange(action, title_menu[menu]);
			break;
		case 0 :
			//메인메뉴 화면 변경
			if(action === "down"){
				if(submenu){			// submenu - submenu[0]으로 돌아가기
					depth++;
					submenu = 0;
					dispchange(action, subtitle_menu[menu][submenu]);
				}else{					// submenu - menu 화면 변경
					dispchange(action, title_menu[menu]);
				}
			}
			else if(action !== "up" && action !== "touch")
				dispchange(action, title_menu[menu]);		// 화면 변경
			break;
		case 1 :
			//서브메뉴 컨트롤
			switch(menu){
				case 1 :
					//네비게이션 메뉴
					if(action !== "touch"){	// 상하좌우 조작시
						if(action === "up"){		//메뉴 진입
							sendSAPmsg("Play_Navi");
							dispchange(action, subtitle_menu[menu][submenu]);	// 화면 변경
						}
						else{
							dispchange(action, subtitle_menu[menu][submenu]);
						}
					}
					else					// 터치시
						switch(submenu){
							case 0 :
								sendSAPmsg("Navi_Home");
								dispPopup("집으로 안내를 시작합니다.");
								break;
							case 1 :
								sendSAPmsg("Navi_Company");
								dispPopup("회사로 안내를 시작합니다.");
								break;
							case 2 :
								sendSAPmsg("Navi_Fav");
								dispPopup("즐겨찾기 안내를 시작합니다.");
								break;
							default :
								break;
						}
					break;
				case 2 :
					// 음악 메뉴
					if(action === "up"){		// 메뉴 진입
						//music player start
						sendSAPmsg("Play_Music");
						dispchange(action, subtitle_menu[menu][submenu]);	// 화면 변경
					}else if(action === "down"){		//메뉴 진입
						dispchange(action, subtitle_menu[menu][submenu]);	// 화면 변경
					}
					else{					// 음악 변경
						if(action === "touch"){
							//playpause
							console.log("music playpause");
							sendSAPmsg("Music_Playpause");
							setTextnCenter("노래제목("+ nowmusic +")",$("#marqueetext"),$("#marqueecontainer"),$("#pagecontainer"));
						}
						else if(action === "left"){
							//next
							console.log("music next");
							sendSAPmsg("Music_Next");
							setTextnCenter("노래제목("+ nowmusic +")",$("#marqueetext"),$("#marqueecontainer"),$("#pagecontainer"));
						}else if(action === "right"){
							//prev
							console.log("music left");
							sendSAPmsg("Music_Prev");
							setTextnCenter("노래제목("+ nowmusic +")",$("#marqueetext"),$("#marqueecontainer"),$("#pagecontainer"));
						}
					}
					break;
				case 3 :
					// 전화 메뉴
					if(action !== "touch"){
						dispchange(action, subtitle_menu[menu][submenu]);	// 화면 변경
					}
					else{
						switch(submenu){
						case 0 :
							sendSAPmsg("Call_Fav1");
							dispPopup("즐겨찾기 1번으로 전화를 걸게요.");
							break;
						case 1 :
							sendSAPmsg("Call_Fav2");
							dispPopup("즐겨찾기 2번으로 전화를 걸게요.");
							break;
						case 2 :
							sendSAPmsg("Call_Fav3");
							dispPopup("즐겨찾기 3번으로 전화를 걸게요.");
							break;
						case 3 :
							sendSAPmsg("Call_Fav4");
							dispPopup("즐겨찾기 4번으로 전화를 걸게요.");
							break;
						default :
							break;
						}
					}
					break;
				case 4 :
					// 설정 메뉴
					if(action !== "touch")
						dispchange(action, subtitle_menu[menu][submenu]);	// 화면 변경
					else{
						if(!SASocket){
							//연결
							dispPopup("핸드폰과 연결되지 않았습니다.  연결을 진행합니다.");
							connect();
						}
						else{
							//재연결
							//dispPopup("기존 연결을 끊고 다시 연결을 진행합니다.");
							sendSAPmsg("Reset_Blue");
							//disconnect();
							//delay(3000,connect);
						}
					}
					break;
				default :
					break;
			}
			break;
		case 2 :
			//볼륨 컨트롤
			if(action === "up"){			// 메뉴 진입
				dispchange(action, subtitle_menu[menu][1]);	// 화면 변경
			}
			if(action === "left"){
				//voldown
				console.log("vol -");
				sendSAPmsg("Vol_Down");
				setTextnCenter("볼륨("+ nowvol +")-",$("#maintext"),$("#textcontainer"),$("#pagecontainer"));
			}else if(action === "right"){
				//volup
				console.log("vol +");
				sendSAPmsg("Vol_Up");
				setTextnCenter("볼륨("+ nowvol +")+",$("#maintext"),$("#textcontainer"),$("#pagecontainer"));
			}
			break;
		default :
			break;
	}
}

function dispchange(direction, str){
	var imgsrc = $("#mainimage");
	var dur = 150;
	$("#marqueetext").html("");
	$("#maintext").html("");
	if(direction == "up"){
		$( "#pagecontainer" ).animate({
			top:"-=250px"
		}, dur, "swing", function(){
			imgsrc.attr("src",str);
			$("#pagecontainer").animate({top:"+=500px"},0,"swing",function(){
				$("#pagecontainer").animate({top:"0"},dur,"swing");
			});
		});
	}
	else if(direction == "down"){
		$( "#pagecontainer" ).animate({
			top:"+=250px"
		}, dur, "swing", function(){
			imgsrc.attr("src",str);
			$("#pagecontainer").animate({top:"-=500px"},0,"swing",function(){
				$("#pagecontainer").animate({top:"0"},dur,"swing");
			});
		});
	}
	else if(direction == "right"){
		$( "#pagecontainer" ).animate({
			left:"+=250px"
		}, dur, "swing", function(){
			imgsrc.attr("src",str);
			$("#pagecontainer").animate({left:"-=500px"},0,"swing",function(){
				$("#pagecontainer").animate({left:"0"},dur,"swing");
			});
		});
	}else if(direction == "left"){
		$( "#pagecontainer" ).animate({
			left:"-=250px"
		}, dur, "swing", function(){
			imgsrc.attr("src",str);
			$("#pagecontainer").animate({left:"+=500px"},0,"swing",function(){
				$("#pagecontainer").animate({left:"0"},dur,"swing");
			});
		});
	}
	else if(direction == "vol"){
		$( "#pagecontainer" ).animate({
			top:"+=250px"
		}, dur, "swing", function(){
			imgsrc.attr("src",str);
			$("#pagecontainer").animate({top:"-=500px"},0,"swing",function(){
				$("#pagecontainer").animate({top:"0"},dur,"swing");
			});
		});
	}else{
		console.log("func dispchange err");
	}
	if(str == "res/img/user62.png"){
		setTextnCenter("즐겨찾기"+(submenu+1),$("#maintext"),$("#textcontainer"),$("#pagecontainer"));
	}else{
		setTextnCenter("",$("#maintext"),$("#textcontainer"),$("#pagecontainer"));
	}
}

// 음악, 전화에서 텍스트 깔아주기
function setTextnCenter(text,maintext,textdiv, masterdiv){
//	//텍스트 변경
//	maintext.html(text);
//	//텍스트 위치 결정
//	textdiv.css({"left":(masterdiv.width()-textdiv.width())/2, "bottom":0});
}

//팝업 안내창
function dispPopup(text){
	var speed=300;
	var delaytime=300;
	
	console.log("dispPopup:" + text);
	//팝업에 글씨 쓰기
	$('#popupcontent').html(text);
	//팝업 띄워주기 애니메이션
	$('#popupcontent').animate({
	    width: ['100%', 'swing'],
	    height: ['150px', 'swing'],
	    position: 'absolute',
	    opacity: 'toggle'
	  }, speed, 'swing', function(){
		  //delaytime 만큼 대기후 토글
			$('#popupcontent').delay(delaytime).animate({
			    width: ['0%', 'swing'],
			    height: ['0%', 'swing'],
			    opacity: 'toggle'
			  }, speed);
	  });
}
