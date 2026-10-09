<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
<title>User Dashboard</title>
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
.hero p{margin-top:10px;color:#64748b;}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:25px;}
.card{background:white;padding:25px;border-radius:18px;box-shadow:0 10px 25px rgba(0,0,0,0.08);transition:0.3s;position:relative;}
.card:hover{transform:translateY(-5px);}
.icon{position:absolute;top:15px;right:15px;font-size:20px;color:#94a3b8;}
.title{font-size:14px;color:#64748b;}
.value{font-size:26px;font-weight:600;margin-top:5px;}
.blue{border-top:4px solid #3b82f6;}
.yellow{border-top:4px solid #facc15;}
.green{border-top:4px solid #22c55e;}
</style>
</head>
<body>
<div class="sidebar">
<div>
<div class="logo">LocalServe (Java)</div>
<a href="${pageContext.request.contextPath}/user/dashboard" class="active"><i class="fa-solid fa-house"></i> Dashboard</a>
<a href="${pageContext.request.contextPath}/user/book"><i class="fa-solid fa-plus"></i> Book Service</a>
<a href="${pageContext.request.contextPath}/user/bookings"><i class="fa-solid fa-list-check"></i> My Bookings</a>
</div>
<a href="${pageContext.request.contextPath}/auth/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
</div>
<div class="container">
<div class="hero">
<h1>Welcome to <span>LocalServe</span></h1>
<p>Book and manage your services easily</p>
</div>
<div class="grid">
<div class="card blue"><i class="fa-solid fa-list icon"></i><div class="title">Total Bookings</div>
<div class="value"><%= request.getAttribute("total") %></div></div>
<div class="card yellow"><i class="fa-solid fa-clock icon"></i><div class="title">Pending</div>
<div class="value"><%= request.getAttribute("pending") %></div></div>
<div class="card green"><i class="fa-solid fa-circle-check icon"></i><div class="title">Completed</div>
<div class="value"><%= request.getAttribute("completed") %></div></div>
</div>
</div>
</body>
</html>
