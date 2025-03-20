'use strict';

var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');
var recipientInput = document.querySelector('#recipient');
var createRoomButton = document.querySelector('#createRoomButton');
var roomIdInput = document.querySelector('#roomId');
var uploadImageButton = document.querySelector('#uploadImageButton');
var imageInput = document.querySelector('#imageInput');
var leaveRoomButton = document.querySelector('#leaveRoomButton');
var roomUsersList = document.querySelector('#roomUsersList');

var stompClient = null;
var username = null;
var currentRoomId = null;

function connect() {
    username = document.querySelector('#username').innerText.trim();
    if (!username) {
        window.location.href = '/login';
        return;
    }

    console.log("Attempting to connect to WebSocket at /ws...");
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    console.log("Successfully connected to WebSocket!");
    stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);
    stompClient.subscribe('/user/' + username + '/queue/private', onPrivateMessageReceived);

    stompClient.send("/app/chat.addUser", {}, JSON.stringify({sender: username, type: 'JOIN'}));
    connectingElement.classList.add('hidden');

    if (Notification.permission !== "granted") {
        Notification.requestPermission();
    }
}

function onError(error) {
    console.error("WebSocket connection error:", error);
    connectingElement.textContent = 'Could not connect to WebSocket server. Please check the server and refresh this page to try again!';
    connectingElement.style.color = 'red';
    connectingElement.classList.remove('hidden');
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    var recipient = recipientInput ? recipientInput.value.trim() : '';

    if ((messageContent || imageInput.files.length > 0) && stompClient) {
        if (imageInput.files.length > 0) {
            uploadImage(imageInput.files[0], function(imageUrl) {
                sendChatMessage(messageContent, recipient, imageUrl);
                imageInput.value = '';
            });
        } else {
            sendChatMessage(messageContent, recipient, null);
        }
        messageInput.value = '';
    }
    event.preventDefault();
}

function sendChatMessage(messageContent, recipient, imageUrl) {
    var chatMessage = {
        sender: username,
        content: messageContent,
        type: 'CHAT',
        imageUrl: imageUrl,
        roomId: currentRoomId,
        recipient: recipient
    };
    if (currentRoomId) {
        stompClient.send("/app/chat.sendRoomMessage", {}, JSON.stringify(chatMessage));
    } else if (recipient) {
        chatMessage.type = 'PRIVATE';
        stompClient.send("/app/chat.sendPrivateMessage", {}, JSON.stringify(chatMessage));
    } else {
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
    }
}

function uploadImage(file, callback) {
    var formData = new FormData();
    formData.append('file', file);

    fetch('/upload', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Upload failed with status: ' + response.status);
            }
            return response.text();
        })
        .then(imageUrl => {
            console.log('Uploaded image URL:', imageUrl);
            if (imageUrl) {
                callback(imageUrl);
            } else {
                console.error('No image URL returned from server');
            }
        })
        .catch(error => {
            console.error('Error uploading image:', error);
        });
}
function createPrivateRoom() {
    if (stompClient) {
        stompClient.send("/app/chat.createPrivateRoom", {}, JSON.stringify({sender: username}));
    }
}

function joinRoom() {
    var roomId = roomIdInput.value.trim();
    if (roomId && stompClient) {
        if (currentRoomId) {
            leaveRoom();
        }
        currentRoomId = roomId;
        stompClient.subscribe('/room/' + currentRoomId, onRoomMessageReceived);
        messageArea.innerHTML = '';
        stompClient.send("/app/chat.joinRoom", {}, JSON.stringify({sender: username, roomId: currentRoomId}));
    }
}

function leaveRoom() {
    if (currentRoomId && stompClient) {
        stompClient.send("/app/chat.leaveRoom", {}, JSON.stringify({sender: username}));
        stompClient.unsubscribe('/room/' + currentRoomId);
        currentRoomId = null;
        roomIdInput.value = '';
        messageArea.innerHTML = '';
        roomUsersList.innerHTML = '';
        leaveRoomButton.style.display = 'none';
    }
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    console.log("Received message with imageUrl:", message.imageUrl);
    if (!currentRoomId) {
        displayMessage(message, 'public');
    }
}

function onPrivateMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    if (message.content.includes("Private room created with ID:")) {
        currentRoomId = message.content.split("ID: ")[1];
        stompClient.subscribe('/room/' + currentRoomId, onRoomMessageReceived);
        messageArea.innerHTML = '';
        roomIdInput.value = currentRoomId;
        leaveRoomButton.style.display = 'inline-block';
        updateRoomUsers(message.roomUsers);
    }
    displayMessage(message, 'private');
}

function onRoomMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    displayMessage(message, 'room');
    if (message.roomUsers) {
        console.log("Received room users:", message.roomUsers);
        updateRoomUsers(message.roomUsers);
    }
}

function updateRoomUsers(users) {
    roomUsersList.innerHTML = '';
    if (users && users.length > 0) {
        users.forEach(user => {
            var userElement = document.createElement('li');
            userElement.classList.add('user-item');
            userElement.innerHTML = `
                <span>${user}</span>
                <span class="status">(online)</span>
            `;
            roomUsersList.appendChild(userElement);
        });
    } else {
        roomUsersList.innerHTML = '<li style="color: #666; font-style: italic;">No users in room</li>';
    }
}

function displayMessage(message, type) {
    var messageElement = document.createElement('li');

    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        messageElement.textContent = message.content
            || (message.type === 'JOIN'
                ? message.sender + ' joined the room!'
                : message.sender + ' left the room!');
    } else {
        messageElement.classList.add(
            type === 'private' ? 'private-message'
                : type === 'room' ? 'room-message'
                    : 'chat-message'
        );

        if (message.sender !== username) {
            messageElement.classList.add('other');
        }

        var contentWrapper = document.createElement('div');

        var usernameElement = document.createElement('strong');
        usernameElement.classList.add('nickname');
        usernameElement.textContent = message.sender;
        contentWrapper.appendChild(usernameElement);

        var statusElement = document.createElement('span');
        statusElement.classList.add('status');
        statusElement.textContent = message.status ? (' (' + message.status + ')') : '';
        contentWrapper.appendChild(statusElement);

        if (type === 'private') {
            var privateLabel = document.createElement('span');
            privateLabel.textContent = ' [Private] ';
            contentWrapper.appendChild(privateLabel);
        } else if (type === 'room' && currentRoomId) {
            var roomLabel = document.createElement('span');
            roomLabel.textContent = ' [Room: ' + currentRoomId + '] ';
            contentWrapper.appendChild(roomLabel);
        }

        if (message.content) {
            var textElement = document.createElement('span');
            textElement.classList.add('message-content');
            textElement.textContent = message.content;
            contentWrapper.appendChild(textElement);
        }

        if (message.imageUrl) {
            console.log('Displaying image with URL:', message.imageUrl);
            var imageElement = document.createElement('img');
            imageElement.src = message.imageUrl;
            imageElement.classList.add('message-image');
            imageElement.onerror = () => {
                console.error('Failed to load image:', message.imageUrl);
                imageElement.style.display = 'none'; // Ẩn hình ảnh nếu không tải được
                var errorText = document.createElement('span');
                errorText.classList.add('message-content');
                errorText.style.color = 'red';
                errorText.textContent = '[Failed to load image]';
                contentWrapper.appendChild(errorText);
            };
            contentWrapper.appendChild(imageElement);
        }

        var timestampElement = document.createElement('div');
        timestampElement.classList.add('timestamp');
        timestampElement.textContent = new Date().toLocaleTimeString();
        contentWrapper.appendChild(timestampElement);

        messageElement.appendChild(contentWrapper);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTo({ top: messageArea.scrollHeight, behavior: 'smooth' });

    if (document.hidden && Notification.permission === "granted" && message.type !== 'JOIN' && message.type !== 'LEAVE') {
        new Notification("New message from " + message.sender, {
            body: message.content || "Sent an image"
        });
    }
}


window.onload = function() {
    messageArea.innerHTML = '';
    connect();
};

messageForm.addEventListener('submit', sendMessage, true);
createRoomButton.addEventListener('click', createPrivateRoom);
roomIdInput.addEventListener('change', joinRoom);
uploadImageButton.addEventListener('click', function() {
    imageInput.click();
});
leaveRoomButton.addEventListener('click', leaveRoom);
