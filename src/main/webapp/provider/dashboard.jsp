<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<title>Provider Dashboard</title>
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
.hero{background:white;padding:50px;border-radius:20px;text-align:center;box-shadow:0 10px 30px rgba(0,0,0,0.1);margin-bottom:40px;}
.hero h1{font-size:36px;font-weight:700;}
.hero span{color:#2563eb;}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:25px;}
.card{background:white;padding:25px;border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.08);transition:0.3s;position:relative;}
.card:hover{transform:translateY(-5px);}
.title{font-size:14px;color:#64748b;}
.value{font-size:26px;font-weight:600;margin-top:5px;}
.blue{border-top:4px solid #3b82f6;}
.yellow{border-top:4px solid #facc15;}
.green{border-top:4px solid #22c55e;}
.red{border-top:4px solid #ef4444;}
.notice{background:#fef3c7;color:#92400e;padding:16px 20px;border-radius:12px;margin-bottom:25px;font-size:14px;}
.notice a{color:#92400e;font-weight:600;}
</style>
</head>
<body>
<div class="sidebar">
<div>
<div class="logo">LocalServe (Java)</div>
<a href="${pageContext.request.contextPath}/provider/dashboard" class="active"><i class="fa-solid fa-house"></i> Dashboard</a>
<a href="${pageContext.request.contextPath}/provider/requests"><i class="fa-solid fa-bell"></i> Requests</a>
<a href="${pageContext.request.contextPath}/provider/create-profile"><i class="fa-solid fa-user"></i> Profile</a>
<a href="${pageContext.request.contextPath}/provider/earnings"><i class="fa-solid fa-wallet"></i> Earnings</a>
</div>
<a href="${pageContext.request.contextPath}/auth/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
</div>
<div class="container">
<div class="hero"><h1>Welcome to <span>LocalServe</span></h1><p>Manage your services like a professional</p></div>
<% if (Boolean.TRUE.equals(request.getAttribute("needsLocation"))) { %>
<div class="notice">Customers cannot find you yet. Please add your service city, state and PIN code in
<a href="${pageContext.request.contextPath}/provider/create-profile">Profile</a>.</div>
<% } %>
<div class="grid">
<div class="card blue"><div class="title">Total Jobs</div><div class="value"><%= request.getAttribute("total") %></div></div>
<div class="card yellow"><div class="title">Pending</div><div class="value"><%= request.getAttribute("pending") %></div></div>
<div class="card green"><div class="title">Completed</div><div class="value"><%= request.getAttribute("completed") %></div></div>
<div class="card red"><div class="title">Status</div><div class="value"><%= request.getAttribute("status") %></div></div>
</div>
</div>
</body>
</html>
