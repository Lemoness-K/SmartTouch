var SAAgent = null;
var SASocket = null;
var CHANNELID = 124;
var tag = "SIMPLE SAP ";
var ProviderAppName = "SimpleSAPProvider";
SAP_MUSIC_REMOTE_CONTROL_REQ = 'music-remotecontrol-req',
SAP_MUSIC_REMOTE_CONTROL_REQ_STATUS_PRESSED = 'pressed',
SAP_MUSIC_REMOTE_CONTROL_REQ_STATUS_RELEASED = 'released',
SAP_MUSIC_REMOTE_CONTROL_REQ_STATUS_BOTH = 'both',
SAP_MUSIC_SET_ATTR_REQ = 'music-setattribute-req',
SAP_MUSIC_SET_ATTR_REQ_OBJECT = {
    msgId: SAP_MUSIC_SET_ATTR_REQ,
    volume: 'nochange',
    repeat: 'nochange',
    shuffle: 'nochange',
    mute: 'nochange'
},
SAP_MUSIC_GET_ATTR_REQ = 'music-getattribute-req',
SAP_MUSIC_MEDIA_CHANGED_REQ = 'music-mediachanged-req',
SAP_RESULT_FAILURE = 'failure';
var nowvol=0;			//받아온 볼륨 저장
var nowmusic="";		//받아온 노래제목 저장

function createHTML(log_string)
{
	var log = document.getElementById('resultBoard');
	log.innerHTML = log.innerHTML + "<br> : " + log_string;
	console.log("CREATEHTML: " + log_string);
}

function onerror(err) {
	console.log("ONERROR: err [" + err.name + "] msg[" + err.message + "]");
}

function disconnect() {
	try {
		if (SASocket != null) {
			console.log(" DISCONNECT SASOCKET NOT NULL");
			SASocket.close();
			SASocket = null;
			createHTML("closeConnection");
		}
	} catch(err) {
		console.log(" DISCONNECT ERROR: exception [" + err.name + "] msg[" + err.message + "]");
	}
}

var agentCallback = {
		onconnect : function(socket) {
			console.log( "agentCallback onconnect" + socket);
			SASocket = socket;
			dispPopup("폰과의 연결에 성공하였습니다!");
			createHTML("startConnection");
			SASocket.setSocketStatusListener(function(reason){
				console.log("Service connection lost, Reason : [" + reason + "]");
				disconnect();
			});
		},
		onerror : onerror
};

var peerAgentFindCallback = {
		onpeeragentfound : function(peerAgent) {
			try {
				if (peerAgent.appName === ProviderAppName) {
					console.log(" peerAgentFindCallback::onpeeragentfound " + peerAgent.appname + " || " + ProviderAppName);
					SAAgent.setServiceConnectionListener(agentCallback);
					SAAgent.requestServiceConnection(peerAgent);
				} else {
					console.log(" peerAgentFindCallback::onpeeragentfound else");
					alert("Not expected app!! : " + peerAgent.appName);
				}
			} catch(err) {
				console.log(" peerAgentFindCallback::onpeeragentfound exception [" + err.name + "] msg[" + err.message + "]");
			}
		},
		onerror : onerror
}

function onsuccess(agents) {
	try {
		if (agents.length > 0) {
			SAAgent = agents[0];

			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();
			console.log(" onsuccess " + SAAgent.name);
		} else {
			alert("Not found SAAgent!!");
			console.log(" onsuccess else");
		}
		SASocket.setDataReceiveListener(onreceive);
	} catch(err) {
		console.log("onsuccess exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function connect() {
	if (SASocket) {
		return false;
	}
	try {
		webapis.sa.requestSAAgent(onsuccess, onerror);
		console.log(SASocket);
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function onreceive(channelId, data) {
	//createHTML(data);
	console.log("receiver start");
	console.log("receive : " + data);
	if(data.substring(0,4) == "music"){
		nowmusic = data;
	}else if(data.substring(0,4) == "volum"){
		nowvol = data;
	}
}

function fetch() {
	try {
		SASocket.setDataReceiveListener(onreceive);
		SASocket.sendData(CHANNELID, "");
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function sendSAPmsg(str) {
	try {
		SASocket.sendData(CHANNELID, str);
		SASocket.setDataReceiveListener(onreceive);
		console.log("sendSAPmessage : " + str);
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
		disconnect();
		delay(2000,connect);
	}
}
