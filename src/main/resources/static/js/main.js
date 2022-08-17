'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var stompClient2 = null;
var username = null;
var userId = null;
 
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();
	
    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
		
        // 연결할 소켓 EndPoint Path 선언
       	const socket = new SockJS('http://localhost:8080/ws');
        
        // STOMP 브로커 소켓 서버 정보 초기화
        stompClient = Stomp.over(socket);
		// 웹소켓 서버 연결
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    userId = document.querySelector('#userId').value.trim();
    if (userId) {
		// userId로 채팅 구독
    	stompClient.subscribe('/sub/chat/'+userId, onMessageReceived);
		// userId로 알림 구독
    	stompClient.subscribe('/sub/alert/'+userId, onAlertReceived);
	}
	
    // 채팅 구독
    stompClient.subscribe('/sub/chat', onMessageReceived);
    // 알림 구독
    stompClient.subscribe('/sub/alert', onAlertReceived);

    // 채팅 구독 후 서버에 사용자 이름 전달
    // stompClient.send("ws://localhost:8080/app/chat.addUser",
    
    stompClient.send("/pub/chat/user",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
	// 연결중 div hidden 처리
    connectingElement.classList.add('hidden');
    
}

// 소켓 서버 연결 실패시 
function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    userId = document.querySelector('#userId').value.trim();
    
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        // stompClient.send("ws://localhost:8080/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        stompClient.send("/pub/chat", {}, JSON.stringify(chatMessage));
        if (userId) {
			stompClient.send("/pub/chat/"+userId, {}, JSON.stringify(chatMessage));
		}
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function sendEvent(event) { 
    let type = "Ring";
    let peer_number = "01012345678";
    let local_number = "553";
    if(type && stompClient) {
        var chatMessage = {
            sender: username,
            event: type,
            peer_number: peer_number,
            local_number: local_number,
            type: 'EVENT'
        };
        // stompClient.send("ws://localhost:8080/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        stompClient.send("/pub/alert", {}, JSON.stringify(chatMessage));
    }
}

function onAlertReceived(payload) {
	console.log(payload);
	let message = JSON.parse(payload.body);
	let popup = document.querySelector('.event-popup');
	let textElement = document.createElement('p');
    let messageText = "";
    
    if(message.event == "Ring") {
		messageText = "내선번호 ["+message.local_number+"]에게 "+ message.peer_number + "으로부터 전화가 왔습니다.";	
	}
	textElement.innerHTML=messageText;
    popup.appendChild(textElement);
    popup.style.display = "block";
    
    setTimeout(() => {
		popup.style.display="none";
		popup.remove();
	}, 2000);
}

function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
