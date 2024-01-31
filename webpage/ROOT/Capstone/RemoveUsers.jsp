<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>

<html>
<head>
    <title>Remove Users</title>
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
        .messageBad {
            font-size: 1.5em;
            color: red;
        }
                .messageGood {
            font-size: 1.5em;
            color: green;
        }
                        .messageNeutral {
            font-size: 1.5em;
            color: yellow;
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
String[] usernames = request.getParameterValues("userCheckbox");
if (usernames != null && usernames.length > 0) {
    applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
    List<String> failedUsers = new ArrayList<>();
    for (String username : usernames) {
        String res = appDBAuth.deleteUser(new String[]{username});
        if ("Failed".equals(res)) {
            failedUsers.add(username);
        }
    }
    if (!failedUsers.isEmpty()) {
        out.println("<div class='messageBad'>Failed to remove the users: " + String.join(", ", failedUsers) + ".</div>");
        out.println("<div class='messageBad'>This users have registered devices. To remove them, you must unregister their devices first.");
    } else {
        out.println("<div class='messageGood'>User(s) successfully removed.</div>");
        //response.sendRedirect("Remove_Users.jsp"); 
    }
} else {
    out.println("<div class='messageNeutral'>No users selected.</div>");
}

    }
%>
</body>
</html>
