<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.localserve.user.Provider" %>
<%@ page import="com.localserve.util.HtmlUtil" %>
<%@ page import="com.localserve.util.LocationUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Book Service</title>
<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
<style>
*{margin:0;padding:0;box-sizing:border-box;font-family:Poppins;}
body{display:flex;background:#f1f5f9;}
.sidebar{width:250px;height:100vh;background:linear-gradient(180deg,#0f172a,#1e293b);padding:20px;position:fixed;
    display:flex;flex-direction:column;justify-content:space-between;}
.logo{font-size:22px;font-weight:600;color:white;margin-bottom:30px;}
.sidebar a{display:flex;align-items:center;gap:10px;padding:12px;margin:10px 0;border-radius:10px;text-decoration:none;color:#cbd5e1;}
.sidebar a:hover,.active{background:linear-gradient(90deg,#2563eb,#3b82f6);color:white;}
.container{margin-left:270px;padding:50px;width:100%;}
.header{background:white;padding:25px;border-radius:15px;box-shadow:0 5px 20px rgba(0,0,0,0.08);margin-bottom:40px;}
.grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(320px,1fr));gap:30px;}
.card{background:white;padding:25px;border-radius:18px;box-shadow:0 8px 25px rgba(0,0,0,0.08);transition:0.3s;}
.card:hover{transform:translateY(-6px);}
.price{color:#2563eb;font-weight:600;}
input, select{width:100%;padding:10px;margin-top:10px;border-radius:10px;border:1px solid #e2e8f0;}
.btn-group{display:flex;justify-content:space-between;margin-top:15px;}
.btn{padding:10px 18px;border:none;border-radius:10px;cursor:pointer;font-weight:500;}
.book{background:linear-gradient(90deg,#2563eb,#3b82f6);color:white;}
.msg{color:#16a34a;font-weight:600;margin-bottom:15px;}
.err{color:#dc2626;font-weight:600;margin-bottom:15px;}
.loc-box{background:white;padding:25px;border-radius:15px;box-shadow:0 5px 20px rgba(0,0,0,0.08);margin-bottom:30px;}
.loc-box p{color:#64748b;font-size:14px;margin-top:5px;}
.loc-row{display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:15px;}
.label{font-size:13px;font-weight:500;margin-top:12px;display:block;}
.note{background:#eff6ff;color:#1e40af;padding:18px;border-radius:12px;font-size:14px;}
.area{font-size:13px;color:#64748b;margin-top:5px;}
</style>
</head>
<body>
<div class="sidebar">
<div>
<div class="logo">LocalServe (Java)</div>
<a href="${pageContext.request.contextPath}/user/dashboard"><i class="fa-solid fa-house"></i> Dashboard</a>
<a href="${pageContext.request.contextPath}/user/book" class="active"><i class="fa-solid fa-plus"></i> Book Service</a>
<a href="${pageContext.request.contextPath}/user/bookings"><i class="fa-solid fa-list-check"></i> My Bookings</a>
</div>
<a href="${pageContext.request.contextPath}/auth/logout"><i class="fa-solid fa-right-from-bracket"></i> Logout</a>
</div>
<div class="container">
<div class="header"><h2>Book Service</h2><p>Select provider and book easily</p></div>
<% if (request.getAttribute("msg") != null) { %>
<div class="msg"><%= HtmlUtil.esc((String) request.getAttribute("msg")) %></div>
<% } %>
<% if (request.getAttribute("error") != null) { %>
<div class="err"><%= HtmlUtil.esc((String) request.getAttribute("error")) %></div>
<% } %>

<div class="loc-box">
<h3><i class="fa-solid fa-location-dot"></i> Where do you need the service?</h3>
<p>Enter the location where the work is required. You will only see providers who serve the same
city, state and PIN code.</p>
<% if (request.getAttribute("locationError") != null) { %>
<div class="err" style="margin-top:12px;"><%= HtmlUtil.esc((String) request.getAttribute("locationError")) %></div>
<% } %>
<form method="GET" action="${pageContext.request.contextPath}/user/book">
<div class="loc-row">
<div><label class="label" for="city">Service City</label>
<input type="text" id="city" name="city" maxlength="100" placeholder="e.g. Dehradun" required
       value="<%= HtmlUtil.esc((String) request.getAttribute("city")) %>"></div>
<div><label class="label" for="state">State</label>
<input type="text" id="state" name="state" list="stateList" maxlength="100" placeholder="e.g. Uttarakhand" required
       value="<%= HtmlUtil.esc((String) request.getAttribute("state")) %>"></div>
<div><label class="label" for="pin_code">PIN Code</label>
<input type="text" id="pin_code" name="pin_code" maxlength="6" pattern="[0-9]{6}" inputmode="numeric"
       title="Enter exactly 6 digits" placeholder="e.g. 248001" required
       value="<%= HtmlUtil.esc((String) request.getAttribute("pinCode")) %>"></div>
</div>
<label class="label" for="address">Full Service Address (optional)</label>
<input type="text" id="address" name="address" maxlength="255" placeholder="e.g. Rajpur Road, Dehradun"
       value="<%= HtmlUtil.esc((String) request.getAttribute("address")) %>">
<%@ include file="/WEB-INF/jspf/state_list.jspf" %>
<div class="btn-group">
<button class="btn book" type="submit"><i class="fa-solid fa-magnifying-glass"></i> Find Providers</button>
</div>
</form>
</div>

<%
List<Provider> providers = (List<Provider>) request.getAttribute("providers");
List<String> timeSlots = (List<String>) request.getAttribute("timeSlots");
Boolean searched = (Boolean) request.getAttribute("searched");
String today = (String) request.getAttribute("today");
String pinCode = (String) request.getAttribute("pinCode");

if (searched == null || !searched) {
    if (request.getAttribute("locationError") == null && request.getAttribute("error") == null) {
%>
<div class="note">Please enter your service location (city, state and PIN code) above to see the
service providers available there.</div>
<%
    }
} else if (providers == null || providers.isEmpty()) {
%>
<div class="note">No service providers are currently available for this PIN code. Try another location or check back later.</div>
<% } else { %>
<div class="grid">
<%
    for (Provider p : providers) {
%>
<div class="card">
<h3><%= HtmlUtil.esc(p.service) %></h3>
<p class="price">Rs. <%= p.pricePerHour %>/hr</p>
<p>Experience: <%= p.experience %> yrs | Phone: <%= HtmlUtil.esc(p.phone) %></p>
<p class="area"><i class="fa-solid fa-location-dot"></i> Serves: <%= HtmlUtil.esc(LocationUtil.format(p.serviceCity, p.serviceState, p.servicePinCode)) %></p>
<form method="POST" action="${pageContext.request.contextPath}/user/book">
<input type="hidden" name="provider_id" value="<%= p.userId %>">
<input type="hidden" name="city" value="<%= HtmlUtil.esc((String) request.getAttribute("city")) %>">
<input type="hidden" name="state" value="<%= HtmlUtil.esc((String) request.getAttribute("state")) %>">
<input type="hidden" name="pin_code" value="<%= HtmlUtil.esc(pinCode) %>">
<input type="hidden" name="address" value="<%= HtmlUtil.esc((String) request.getAttribute("address")) %>">
<input type="date" name="date" min="<%= HtmlUtil.esc(today) %>" required>
<select name="time" required>
<option value="">Select Time</option>
<% for (String slot : timeSlots) { %>
<option><%= HtmlUtil.esc(slot) %></option>
<% } %>
</select>
<div class="btn-group">
<button class="btn book" type="submit">Book Now</button>
</div>
</form>
</div>
<% } %>
</div>
<% } %>
</div>
</body>
</html>
