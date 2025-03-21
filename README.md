# Real-Time Chat Application 🚀

A feature-rich real-time chat application leveraging WebSocket and HTTP protocols for seamless communication.
(https://roadmap.sh/projects/realtime-leaderboard-system)
---

## 📡 API Endpoints

### WebSocket Endpoints

| Endpoint                          | Functionality                                                                 |
|-----------------------------------|-------------------------------------------------------------------------------|
| **/chat.sendMessage**             | Broadcasts a public message, updates user status to "online", and attaches an avatar. |
| **/chat.addUser**                 | Adds a user to the session and notifies the public chat of their online status. |
| **/chat.sendPrivateMessage**      | Sends a private message between two users via dedicated queues.               |
| **/chat.createPrivateRoom**       | Creates a private room, assigns the user, and returns room details.           |
| **/chat.joinRoom**                | Joins a user to an existing room and notifies members.                        |
| **/chat.leaveRoom**               | Removes a user from a room and updates remaining members.                     |
| **/chat.sendRoomMessage**         | Broadcasts a message to all users in a specific private room.                 |

### HTTP Endpoints

| Endpoint              | Method | Functionality                                                                 |
|-----------------------|--------|-------------------------------------------------------------------------------|
| **/**                 | GET    | Renders the chat page if authenticated; redirects to login otherwise.         |
| **/login**            | GET    | Displays the login page.                                                      |
| **/login**            | POST   | Authenticates users and creates a session.                                    |
| **/logout**           | GET    | Invalidates the session and redirects to login.                               |
| **/upload**           | POST   | Handles file uploads (e.g., images) and returns the file URL.                 |

---

## ✨ Key Features

- **Real-Time Messaging**  
  Instant message delivery using WebSocket for public chats, private chats, and rooms.

- **User Authentication & Sessions**  
  Secure login/logout mechanism with session management.

- **Private Rooms & Messaging**  
  Create private rooms, send one-on-one messages, and manage dynamic user lists.

- **File Upload Support**  
  Share images/files via uploads stored in a dedicated directory.

- **Dynamic Avatars**  
  Auto-generated avatar URLs based on usernames for personalized profiles.

---

## 🐳 Docker Deployment

### Run with Docker

1. **Build Docker image**:
```bash
docker build -t real-time-chat:latest .
```
2. **Run Docker image**:
```bash
docker run -d -p 8081:8081 --name chat-app real-time-chat:latest
```

## 🛠️ Contributing

Contributions are welcome! Follow these steps:
1. **Fork** the repository.
2. Create a feature branch (`git checkout -b feature/your-idea`).
3. Commit your changes (`git commit -m 'Add amazing feature'`).
4. Push to the branch (`git push origin feature/your-idea`).
5. Open a **Pull Request**.

For major changes, please open an issue first to discuss your proposal.

---

## 📬 Contact

**Author**: Do Minh Chinh  
**Email**: [chinh0726@gmail.com](mailto:chinh0726@gmail.com)
