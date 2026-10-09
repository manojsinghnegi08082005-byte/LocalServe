<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.localserve.util.HtmlUtil" %>
<%@ page import="java.util.Map" %>
<%
Map<String, String> form = (Map<String, String>) request.getAttribute("form");
if (form == null) form = new java.util.HashMap<String, String>();
String savedService = form.get("service") == null ? "" : form.get("service");
String[] services = {"Plumber", "Electrician", "Carpenter", "Painter", "AC Repair", "Fridge Repair",
    "Washing Machine Repair", "TV Repair", "Mobile Repair", "Laptop Repair", "Beautician", "Hair Stylist",
    "Home Cleaning", "Pest Control", "Gardener", "Driver", "Cook", "Tutor", "Photographer"};
%>
<!DOCTYPE html>
<html>
<head>
<title>Create Profile</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
<style>
*{margin:0;padding:0;box-sizing:border-box;font-family:Poppins;}
body{min-height:100vh;display:flex;justify-content:center;align-items:center;padding:30px 0;background:linear-gradient(135deg,#0f172a,#1e293b,#2563eb);}
.card{width:420px;padding:35px;border-radius:20px;background:rgba(255,255,255,0.08);backdrop-filter:blur(18px);
    box-shadow:0 20px 40px rgba(0,0,0,0.3);color:white;}
.title{text-align:center;margin-bottom:25px;font-size:22px;font-weight:600;}
.msg{text-align:center;margin-bottom:15px;color:#ffcc00;}
.err{text-align:center;margin-bottom:15px;color:#fca5a5;}
.section{font-size:13px;font-weight:500;margin:8px 0 4px;color:#cbd5e1;}
.hint{font-size:12px;color:#cbd5e1;margin-bottom:10px;line-height:1.5;}
input, select{width:100%;padding:12px;margin-bottom:12px;border:none;border-radius:10px;outline:none;background:rgba(255,255,255,0.15);color:white;}
input::placeholder{color:#cbd5e1;}
.custom-select option{background:#1e293b;color:white;}
button{width:100%;padding:12px;border:none;border-radius:10px;background:linear-gradient(90deg,#22d3ee,#06b6d4);
    color:black;font-weight:600;margin-top:5px;cursor:pointer;}
</style>
</head>
<body>
<form method="POST" class="card" action="${pageContext.request.contextPath}/provider/create-profile">
<div class="title"><i class="fa-solid fa-user-gear"></i> Complete Profile</div>
<% if (request.getAttribute("msg") != null) { %>
<div class="msg"><%= HtmlUtil.esc((String) request.getAttribute("msg")) %></div>
<% } %>
<% if (request.getAttribute("error") != null) { %>
<div class="err"><%= HtmlUtil.esc((String) request.getAttribute("error")) %></div>
<% } %>
<input type="text" name="name" placeholder="Enter Your Name" maxlength="100" required
       value="<%= HtmlUtil.esc(form.get("name")) %>">
<select name="service" required class="custom-select">
<option value="">Select Service</option>
<% for (String s : services) { %>
<option<%= s.equals(savedService) ? " selected" : "" %>><%= s %></option>
<% } %>
</select>
<input type="number" name="price" placeholder="Price Per Hour" step="0.01" min="0.01" max="100000" required
       value="<%= HtmlUtil.esc(form.get("price")) %>">
<input type="text" name="phone" placeholder="Phone Number" maxlength="20" pattern="[0-9+\-() ]{7,20}" required
       value="<%= HtmlUtil.esc(form.get("phone")) %>">
<input type="number" name="exp" placeholder="Experience (years)" min="0" max="60" required
       value="<%= HtmlUtil.esc(form.get("exp")) %>">

<div class="section"><i class="fa-solid fa-location-dot"></i> Service Location</div>
<div class="hint">The area where you take jobs, not your home address. Customers are only shown
providers with the same city, state and PIN code as the place they need the service.</div>
<input type="text" name="city" placeholder="Service City (e.g. Dehradun)" maxlength="100" required
       value="<%= HtmlUtil.esc(form.get("city")) %>">
<input type="text" name="state" list="stateList" placeholder="State (e.g. Uttarakhand)" maxlength="100" required
       value="<%= HtmlUtil.esc(form.get("state")) %>">
<input type="text" name="pin_code" placeholder="PIN Code (6 digits, e.g. 248001)" maxlength="6"
       pattern="[0-9]{6}" inputmode="numeric" title="Enter exactly 6 digits" required
       value="<%= HtmlUtil.esc(form.get("pin_code")) %>">
<%@ include file="/WEB-INF/jspf/state_list.jspf" %>


<button type="submit"><i class="fa-solid fa-floppy-disk"></i> Save Profile</button>

<a href="${pageContext.request.contextPath}/provider/dashboard"
   style="display:block; text-align:center; padding:12px;
          margin-top:12px; border-radius:10px;
          background:#49566b; color:white;
          text-decoration:none; font-weight:600;">
    <i class="fa-solid fa-arrow-left"></i> Back to Dashboard
</a>

</form>

</body>
</html>
