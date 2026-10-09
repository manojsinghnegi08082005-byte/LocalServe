<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.localserve.provider.EarningEntry" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Earnings</title>
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
.container{margin-left:270px;padding:50px;width:100%;}
.header{background:white;padding:25px;border-radius:15px;box-shadow:0 5px 20px rgba(0,0,0,0.08);margin-bottom:25px;}
.total-card{background:linear-gradient(135deg,#22c55e,#16a34a);color:white;padding:25px;border-radius:18px;
    box-shadow:0 10px 25px rgba(0,0,0,0.15);margin-bottom:30px;display:flex;justify-content:space-between;align-items:center;}
.withdraw{padding:10px 18px;border:none;border-radius:10px;background:white;color:#16a34a;font-weight:600;cursor:pointer;}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(320px,1fr));gap:25px;}
.card{background:white;border-radius:18px;padding:20px;box-shadow:0 10px 25px rgba(0,0,0,0.1);transition:0.3s;}
.card:hover{transform:translateY(-5px);}
.info{display:flex;align-items:center;gap:10px;margin:8px 0;color:#475569;}
.amount{margin-top:10px;font-size:18px;font-weight:600;color:#16a34a;}
.divider{height:1px;background:#e2e8f0;margin:10px 0;}
.msg{color:#2563eb;font-weight:600;margin-bottom:10px;}
</style>
</head>
<body>
<div class="sidebar">
<div>
<div class="logo">LocalServe (Java)</div>
<a href="${pageContext.request.contextPath}/provider/dashboard"><i class="fa-solid fa-house"></i> Dashboard</a>
<a href="${pageContext.request.contextPath}/provider/requests"><i class="fa-solid fa-bell"></i> Requests</a>
<a href="${pageContext.request.contextPath}/provider/create-profile"><i class="fa-solid fa-user"></i> Profile</a>
<a href="${pageContext.request.contextPath}/provider/earnings" class="active"><i class="fa-solid fa-wallet"></i> Earnings</a>
</div>
<a href="${pageContext.request.contextPath}/auth/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
</div>
<div class="container">
<div class="header"><h2><i class="fa-solid fa-money-bill-trend-up"></i> Earnings Overview</h2></div>
<% if (request.getAttribute("msg") != null) { %>
<div class="msg"><%= request.getAttribute("msg") %></div>
<% } %>
<form method="POST" action="${pageContext.request.contextPath}/provider/earnings">
<div class="total-card">
<h3><i class="fa-solid fa-wallet"></i> Rs. <%= request.getAttribute("total") %></h3>
<button class="withdraw" type="submit">Withdraw</button>
</div>
</form>
<div class="grid">
<%
List<EarningEntry> entries = (List<EarningEntry>) request.getAttribute("entries");
if (entries != null) {
    for (EarningEntry e : entries) {
%>
<div class="card">
<div class="info"><i class="fa-solid fa-id-badge"></i> Booking ID: <%= e.bookingId %></div>
<div class="info"><i class="fa-solid fa-calendar-days"></i> <%= e.bookingDate %></div>
<div class="info"><i class="fa-solid fa-clock"></i> <%= e.timeSlot %></div>
<div class="divider"></div>
<div class="amount">Rs. <%= e.amount %></div>
</div>
<% } } %>
</div>
</div>
</body>
</html>
