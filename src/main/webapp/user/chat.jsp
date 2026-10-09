<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.localserve.user.ChatMessage" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Chat</title>
<style>
body{font-family:Poppins,sans-serif;background:#0f172a;color:white;margin:0;padding:20px;}
.chat-box{max-width:600px;margin:0 auto 10px;height:400px;overflow-y:auto;background:rgba(255,255,255,0.1);padding:15px;border-radius:12px;}
.msg{margin:10px 0;padding:10px;border-radius:10px;max-width:70%;}
.me{background:#22d3ee;color:black;margin-left:auto;}
.other{background:#6366f1;}
form{max-width:600px;margin:0 auto;display:flex;gap:10px;}
input{padding:10px;flex:1;border-radius:8px;border:none;}
button{padding:10px 18px;border:none;border-radius:8px;background:#22d3ee;cursor:pointer;font-weight:600;}
</style>
</head>
<body>

<div style="max-width:600px; margin:0 auto 15px;">
    <a href="${pageContext.request.contextPath}/user/dashboard"
       style="display:inline-block; padding:9px 14px;
              background:#22d3ee; color:#0f172a;
              border-radius:8px; text-decoration:none;
              font-weight:600;">
        &#8592; Back to Dashboard
    </a>
    <h2 style="margin:12px 0 15px;">Chat</h2>
</div>

<%
int bookingId = (int) request.getAttribute("bookingId");
List<ChatMessage> messages = (List<ChatMessage>) request.getAttribute("messages");
Integer myId = (Integer) session.getAttribute("user_id");
%>
<div class="chat-box">
<%
if (messages != null) {
    for (ChatMessage m : messages) {
        String cls = (m.senderId == myId) ? "me" : "other";
%>
<div class="msg <%= cls %>"><%= m.message %></div>
<% } } %>
</div>
<form method="POST" action="${pageContext.request.contextPath}/user/chat?booking_id=<%= bookingId %>">
<input type="text" name="msg" placeholder="Type message" required>
<button type="submit">Send</button>
</form>
</body>
</html>
