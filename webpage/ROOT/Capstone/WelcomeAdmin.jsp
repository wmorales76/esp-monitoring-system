<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<html>
<head>
<style>
body {
    font-family: Arial, sans-serif;
}

.container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100vh;
}

.welcome {
    font-size: 2em;
    margin-bottom: 20px;
}

form {
    margin-bottom: 10px;
}

input[type=submit] {
    background-color: #4CAF50;
    color: white;
    padding: 12px 24px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    text-decoration: none;
}

input[type=submit]:hover {
    background-color: #45a049;
}
</style>
</head>
<body>
<%
    String Username = (String) session.getAttribute("Username");
    String Role = (String) session.getAttribute("Role");

    if (Username == null || Role == null || !Role.equals("Admin")) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
        session.setAttribute("currentPage", "WelcomeAdmin.jsp");
%>
<div class="container">
    <div class="welcome">Welcome <%=Username %>!</div>
    <form action="DevicesDataAdmin.jsp">
        <input type="submit" value="View Devices Data" style="background-color: #0693E3;">
    </form>
    <form action="Manage_Users.jsp">
        <input type="submit" value="User Management">
    </form>
    <form action="Device_Location.jsp">
        <input type="submit" value="Device Location Management">
    </form>
    <form action="Sign-Out.jsp">
        <input type="submit" value="Sign out">
    </form>
</div>
<%
    }
%>
</body>
</html>
