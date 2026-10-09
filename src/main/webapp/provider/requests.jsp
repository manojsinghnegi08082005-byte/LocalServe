<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.localserve.provider.BookingRequest" %>
<%@ page import="com.localserve.util.HtmlUtil" %>
<%@ page import="com.localserve.util.LocationUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Requests</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
<style>
*{margin:0;padding:0;box-sizing:border-box;font-family:Poppins;}
body{display:flex;background:#f1f5f9;}
.sidebar{width:250px;height:100vh;background:linear-gradient(180deg,#0f172a,#1e293b);padding:20px;position:fixed;
    display:flex;flex-direction:column;justify-content:space-between;}
.logo{color:white;font-size:22px;margin-bottom:30px;}
.sidebar a{display:flex;gap:12px;align-items:center;padding:12px;margin:10px 0;border-radius:10px;text-decoration:none;color:#cbd5e1;}
.sidebar a:hover,.active{background:linear-gradient(90deg,#2563eb,#3b82f6);color:white;}
.container{margin-left:270px;padding:40px;width:100%;}
.header{background:white;padding:25px;border-radius:18px;margin-bottom:30px;box-shadow:0 10px 30px rgba(0,0,0,0.08);}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));gap:20px;}
.card{background:white;padding:22px;border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.08);transition:0.3s;position:relative;}
.card:hover{transform:translateY(-6px);}
.user{font-size:16px;font-weight:600;margin-bottom:10px;}
.info{font-size:14px;color:#64748b;margin:5px 0;}
.status{display:inline-block;margin-top:8px;padding:6px 12px;border-radius:20px;font-size:12px;font-weight:500;}
.pending{background:#fef3c7;color:#92400e;}
.accepted{background:#dcfce7;color:#166534;}
.rejected{background:#fee2e2;color:#991b1b;}
.actions{margin-top:15px;display:flex;gap:10px;}
.btn{flex:1;text-align:center;padding:10px;border-radius:10px;text-decoration:none;font-size:13px;font-weight:500;}
.accept{background:#22c55e;color:white;}
.reject{background:#ef4444;color:white;}
.chat{background:#3b82f6;color:white;}
.err{color:#dc2626;font-weight:600;margin-bottom:15px;}
.old{font-style:italic;}
</style>
</head>
<body>
<div class="sidebar">
<div>
<div class="logo">LocalServe (Java)</div>
<a href="${pageContext.request.contextPath}/provider/dashboard"><i class="fa-solid fa-house"></i> Dashboard</a>
<a href="${pageContext.request.contextPath}/provider/requests" class="active"><i class="fa-solid fa-bell"></i> Requests</a>
<a href="${pageContext.request.contextPath}/provider/create-profile"><i class="fa-solid fa-user"></i> Profile</a>
<a href="${pageContext.request.contextPath}/provider/earnings"><i class="fa-solid fa-wallet"></i> Earnings</a>
</div>
<a href="${pageContext.request.contextPath}/auth/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
</div>
<div class="container">
<div class="header"><h2>Service Requests</h2><p>Manage and respond to customer requests</p></div>
<% if (request.getAttribute("error") != null) { %>
<div class="err"><%= HtmlUtil.esc((String) request.getAttribute("error")) %></div>
<% } %>
<div class="grid">
<%
List<BookingRequest> requests = (List<BookingRequest>) request.getAttribute("requests");
if (requests != null) {
    for (BookingRequest r : requests) {
%>
<div class="card">
<%
String where = LocationUtil.format(r.serviceCity, r.serviceState, r.servicePinCode);
String fullAddress = r.serviceAddress == null ? "" : r.serviceAddress.trim();
%>
<div class="user"><%= HtmlUtil.esc(r.userName) %></div>
<div class="info"><i class="fa-solid fa-calendar"></i> <%= r.bookingDate %></div>
<div class="info"><i class="fa-solid fa-clock"></i> <%= HtmlUtil.esc(r.timeSlot) %></div>
<% if (!where.isEmpty()) { %>
<div class="info"><i class="fa-solid fa-location-dot"></i> Service at: <%= HtmlUtil.esc(where) %></div>
<% } else { %>
<div class="info old"><i class="fa-solid fa-location-dot"></i> Location not recorded</div>
<% } %>
<% if (!fullAddress.isEmpty()) { %>
<div class="info"><i class="fa-solid fa-house"></i> <%= HtmlUtil.esc(fullAddress) %></div>
<% } %>
<div class="status <%= r.status %>"><%= r.status.substring(0,1).toUpperCase() + r.status.substring(1) %></div>
<div class="actions">
<a href="${pageContext.request.contextPath}/provider/requests?action=accepted&id=<%= r.id %>" class="btn accept">Accept</a>
<a href="${pageContext.request.contextPath}/provider/requests?action=rejected&id=<%= r.id %>" class="btn reject">Reject</a>
<a href="${pageContext.request.contextPath}/provider/chat?booking_id=<%= r.id %>" class="btn chat">Chat</a>
</div>
</div>
<% } } %>
</div>
</div>
</body>
</html>
