# GitPeek...🔍

**Explore GitHub users with ease.**  
---

## 📌 Overview  
GitPeek is an **offline-first** Android app that allows users to browse GitHub profiles with a **list and detail view**. The app utilizes the **GitHub User API** to fetch user information and presents it in a clean, **Material Design-inspired** interface.  

---

## 🚀 Features  
- 🔍 **Explore GitHub Users**: Browse a list of GitHub users with their profile pictures and usernames.  
- 📂 **Detailed User Profiles**: View information like bio, repositories, followers, and more.  
- 🎨 **Material Design UI**: A modern, responsive design with intuitive navigation.  
- 🔗 **Direct Links**: Quickly access user GitHub profiles, blogs, or social accounts.  
- 🛠️ **Search GitHub Users**: With suggestions & filters.  
- 🚀 **Pull-to-Refresh**: Get real-time updates.  

---

## 🛠️ Tech Stack  
- **Programming Language**: Kotlin  
- **UI Framework**: Jetpack Compose 🖌️ *(Modern UI Toolkit)*  
- **Architecture**: MVVM 🏗️ *(Scalable & Maintainable)*  
- **Networking**: Retrofit + OkHttp 🌐 *(API Calls & Caching)*  
- **Dependency Injection**: Hilt  
- **Image Loading**: Coil 🖼️ *(Efficient Image Loading)*  
- **Navigation**: Jetpack Navigation Compose  
- **State Management**: StateFlow  
- **Background Sync**: WorkManager + CustomWorkerFactory ⏳  
- **Offline Persistence**: Room Database 🗄️  

---

## 📸 Screenshots  

### 🔹🔹 App Demo                                                 🔹🔹 User List Screen  🔹🔹 User Detail Screen  🔹🔹 Search Feature  🔹🔹    
<img src="https://i.imgur.com/dWWpBYN.gif" width="200">  <img src="https://i.imgur.com/GPrhFON.png" width="200">  <img src="https://i.imgur.com/MJNzE0H.png" width="200"> <img src="https://i.imgur.com/nsOhRKN.png" width="200"> 

---
## 📡 API Details  

GitPeek fetches GitHub user data using the **GitHub User API**.  

### 🔹 Endpoints Used:  
1. **List Users API**  
   - **Endpoint:** `https://api.github.com/users`  
   - **Description:** Fetches a paginated list of GitHub users.  

2. **User Details API**  
   - **Endpoint:** `https://api.github.com/users/{username}`  
   - **Description:** Fetches detailed information for a specific user, including bio, repositories, and followers.  

📌 **For more details, refer to the official [GitHub API Documentation](https://docs.github.com/en/rest/users/users).**  

📩 Contact
For questions or feedback, feel free to reach out:
📧 appntech@gmail.com

