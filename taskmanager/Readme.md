🧠 FocusBoard – Stay On Task, One Pomodoro at a Time
FocusBoard is a productivity-focused web application designed to help users manage their tasks efficiently using a visual task board and time management tools. Whether you're juggling multiple projects or trying to focus on deep work, FocusBoard keeps your workflow organised and your mind clear.

🚀 Features
✅ Draggable Task Cards
Move your tasks effortlessly across the board between columns such as To Do, In Progress, and Done using a simple and intuitive drag-and-drop interface.

⏱️ Built-In Pomodoro Timer
Boost your productivity with a Pomodoro timer integrated right into the interface. Work in focused 25-minute sessions with regular breaks to maintain energy and avoid burnout.

🔒 User Authentication
Secure login and registration system built with Thymeleaf:

Login Page: Users can sign in with their credentials to access the task board.

Registration Page: New users can create an account to get started with their own workspace.

🎨 Responsive Design
The app is styled with clean and responsive CSS, ensuring a smooth user experience across devices – from desktops to smartphones.

📸 UI Previews
🟦 Login Page
Simple and clean UI

Required fields for username and password

html
Copy
Edit
<form th:action="@{/login}" method="post" class="login-form">
  <!-- Username and Password Inputs -->
</form>
🟩 Register Page
Allows new users to sign up quickly

Linked to the login page for convenience

html
Copy
Edit
<form th:action="@{/register}" method="post" class="login-form">
  <!-- Username and Password Inputs -->
</form>
🟨 Task Board
Each task is a card that can be dragged between columns

Tasks are color-coded by priority (High, Medium, Low)

List view toggle for simplified navigation

🟥 Pomodoro Timer
Start, pause, and reset your focus sessions

Visually integrated into the task board for seamless use

🛠️ Tech Stack
Java + Spring Boot (Backend)

Thymeleaf (Templating engine)

HTML5, CSS3, JavaScript

Spring Security (Authentication)

H2 / MySQL (Data persistence)

📂 Project Structure
cpp
Copy
Edit
src/
├── main/
│   ├── java/
│   │   └── com.example.focusboard/
│   ├── resources/
│   │   ├── templates/
│   │   │   ├── login.html
│   │   │   ├── register.html
│   │   │   └── taskboard.html
│   │   └── static/
│   │       └── login.css
👥 User Flow
Register → Create an account via /register

Login → Sign in with your credentials at /login

Access the Task Board → View, add, and drag tasks

Use the Pomodoro Timer → Start focus sessions as you work

Log Out when done (if implemented)

📌 Future Improvements
User profile customization (avatars, themes)

Collaboration features (share boards with others)

Task deadlines and reminders

Dark mode toggle

💡 Inspiration
FocusBoard was inspired by productivity tools like Trello, Notion, and the Pomodoro technique, combining visual task management with proven timeboxing methods to supercharge your workflow.

🧪 How to Run the App Locally
Clone the repo:

bash
Copy
Edit
git clone https://github.com/your-username/focusboard.git
cd focusboard
Build and run with Maven:

bash
Copy
Edit
./mvnw spring-boot:run
Navigate to:

bash
Copy
Edit
http://localhost:8080/login
📬 Feedback
If you have ideas for improvement or spot a bug, feel free to open an issue or submit a pull request!