'use strict';


var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');

var stompClient = null;
var username = null;


function connect() {
    username = document.querySelector('#username').innerText.trim();

    //Tạo kết nối WebSocket đến /ws bằng SockJS.
    var socket = new SockJS('/ws');

    //Sau đó, nó sử dụng Stomp (Simple Text Oriented Messaging Protocol) để quản lý giao tiếp WebSocket.
    stompClient = Stomp.over(socket);

    //Khi kết nối xong, gọi onConnected(). Nếu có lỗi, gọi onError().
    stompClient.connect({}, onConnected, onError);
}

// Connect to WebSocket Server.
connect();

function onConnected() {
    // Subscribe (đăng ký) vào topic công khai
    //Đăng ký (subscribe) vào kênh /topic/publicChatRoom: Lắng nghe tin nhắn gửi đến từ server.
    stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);

    // Gửi thông báo "đã tham gia" đến server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

//Gửi tin nhắn
function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };

        //Gửi tin nhắn đến /app/chat.sendMessage.
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

// Xử lý tin nhắn nhận được
function onMessageReceived(payload) {

    //Parse JSON tin nhắn nhận được từ WebSocket.
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
        var usernameElement = document.createElement('strong');
        usernameElement.classList.add('nickname');
        var usernameText = document.createTextNode(message.sender);
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('span');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


messageForm.addEventListener('submit', sendMessage, true);
