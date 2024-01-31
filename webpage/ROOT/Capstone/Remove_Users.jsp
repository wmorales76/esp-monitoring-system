<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<html>
<head>
    <title>You are removing a user administrator!</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
$(document).ready(function(){
    setInterval(function(){
        $("#refreshTable").load(location.href + " #refreshTable");
    }, 5000); // Refresh every 5 seconds
});
</script>
    <style>
    
        body {
            font-family: Arial, sans-serif;
        }
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            text-align: left;
            padding: 8px;
        }
        tr:nth-child(even) {background-color: #f2f2f2;}
        th {
            background-color: #4CAF50;
            color: white;
        }
        .form-container {
            margin-top: 20px;
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
    <script>
        function checkSelected() {
            var checkboxes = document.getElementsByName('userCheckbox');
            var isChecked = false;
            for (var i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].checked) {
                    isChecked = true;
                    break;
                }
            }
            if (!isChecked) {
                alert('Please select at least one user to remove.');
                return false;
            }
            return true;
        }
    </script>
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
        out.println("<div style='text-align: center; font-size: 24px;'>Welcome "+Username +"</div>");

    try{
            //Create the appDBMnger object
            applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
            System.out.println("Connecting...");
            System.out.println(appDBAuth.toString());
            
            //Call the showUsers method. This method returns a ResultSet containing all the tuples in the table Users
            ResultSet result = appDBAuth.showUsers();
            
%>
<form onsubmit="return checkSelected();" method="post" action="RemoveUsers.jsp">
    <table id="refreshTable">
        <tr>
            <th>User_ID</th>
            <th>Username</th>
            <th>Name</th>
            <th>Phone</th>
            <th>Email</th>
            <th>Role</th>
            <th>Timestamp</th>
            <th>Select</th>
        </tr>
        <%
        while (result.next())
        {
            int user_id = result.getInt("User_ID");
            String username = result.getString("Username");
            String name = result.getString("Name");
            String phone = result.getString("Phone");
            String email = result.getString("Email");
            String role = result.getString("Role");
            Timestamp timestamp = result.getTimestamp("Timestamp");
        %>
        <tr>
            <td><%=user_id%></td>
            <td><%=username%></td>
            <td><%=name%></td>
            <td><%=phone%></td>
            <td><%=email%></td>
            <td><%=role%></td>
            <td><%=timestamp%></td>
            <td><input type="checkbox" name="userCheckbox" value="<%=username%>"></td>
        </tr>
        <%
        }
%>
    </table>
    <div class="form-container">
    <input type="submit" value="Remove User(s)">
    </div>
</form>
<div class="form-container">
    <form action="Sign-Out.jsp"> <!-- Form starts here, it sends data to 'SignOutAdmin.jsp' -->
    <input type="submit" value="Sign out"> <!-- Submit button with value 'Sign out' -->
    </form> <!-- Form ends here -->
</div>
<%}catch(Exception e)
{%>
            Error: <%=e.getMessage()%>.
    <%}finally
    {
        System.out.println("Finally");
    }   %>

<%
    }
%>
</body>
</html>
