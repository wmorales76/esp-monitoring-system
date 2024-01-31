<%@ page import="java.lang.*"%>
<html>
<head>
    <title>Administrator Actions</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
            margin: 0;
            background-color: #f5f5f5;
        }
        .welcome {
            font-size: 2em;
            margin-bottom: 20px;
        }
        form {
            margin: 10px;
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
        session.setAttribute("currentPage", "Manage-Users.jsp");
%>
    <div class="welcome">Administrator Actions!</div>
    <form action="Remove_Users.jsp">
        <input type="submit" value="Remove Users in the Database" style="background-color: #B80000;">
    </form>
    <form action="Devices_Control.jsp">
        <input type="submit" value="Manage Devices for Users">
    </form>
    <form action="Sign-Out.jsp">
        <input type="submit" value="Sign out">
    </form>
<%
    }
%>
</body>
</html>
