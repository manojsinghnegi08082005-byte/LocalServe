<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.localserve.provider.ChatServlet.Msg" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Chat</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
<style>
body{margin:0;font-family:Poppins,sans-serif;background:linear-gradient(135deg,#0f172a,#1e3a8a,#2563eb);color:white;}
.header{display:flex;justify-content:space-between;align-items:center;padding:15px 25px;background:rgba(0,0,0,0.5);}
.back{background:#22d3ee;padding:8px 15px;border-radius:10px;text-decoration:none;color:black;}
.chat-container{max-width:650px;margin:20px auto;background:rgba(255,255,255,0.1);border-radius:20px;
    backdrop-filter:blur(15px);padding:20px;}
.chat-box{height:420px;overflow-y:auto;padding:10px;display:flex;flex-direction:column;}
.msg{margin:8px 0;padding:12px 15px;border-radius:15px;max-width:70%;font-size:14px;}
.me{background:linear-gradient(135deg,#22d3ee,#06b6d4);color:black;align-self:flex-end;}
.other{background:linear-gradient(135deg,#6366f1,#8b5cf6);}
form{display:flex;gap:10px;margin-top:15px;}
input{flex:1;padding:12px;border:none;border-radius:12px;outline:none;}
button{padding:12px 18px;border:none;border-radius:12px;background:linear-gradient(135deg,#22d3ee,#06b6d4);cursor:pointer;font-weight:500;}
</style>
</head>
<body>
<div class="header"><h3>Chat</h3>

<a href="${pageContext.request.contextPath}/provider/dashboard" class="back">
    <i class="fa-solid fa-arrow-left"></i> Back to Dashboard
</a>
</div>
<%
int bookingId = (int) request.getAttribute("bookingId");
List<Msg> messages = (List<Msg>) request.getAttribute("messages");
Integer myId = (Integer) session.getAttribute("user_id");
%>
<div class="chat-container">
<div class="chat-box">
<%
if (messages != null) {
    for (Msg m : messages) {
        String cls = (m.senderId == myId) ? "me" : "other";
%>
<div class="msg <%= cls %>"><%= m.text %></div>
<% } } %>
</div>
<form method="POST" action="${pageContext.request.contextPath}/provider/chat?booking_id=<%= bookingId %>">
<input type="text" name="msg" placeholder="Type your message..." required>
<button type="submit">Send</button>
</form>
</div>
</body>
</html>
