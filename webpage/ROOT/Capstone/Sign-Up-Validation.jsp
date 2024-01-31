<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html>
<html>
<head>
    <title>Registration Confirmation</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 15px;
            text-align: center;
        }
        .message {
            font-size: 1.2em;
            color: #333;
        }
        .error {
            color: #D8000C;
        }
        .success {
            color: #4F8A10;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Registration Confirmation</h1>
        <%
            //Retrieve variables
            String Username = request.getParameter("Username");
            String Hash = request.getParameter("Hash");
            String Name = request.getParameter("Name");
            String Phone = request.getParameter("Phone");
            String Email = request.getParameter("Email");
            String Role = request.getParameter("Role");

            //Check if Role is empty or null
            if (Role == null || Role.isEmpty()) {
                Role = "Normal";
            }

            //Check if Username is null
            if (Username == null) {
                session.setAttribute("currentPage", null);
                session.setAttribute("Username", null);
                response.sendRedirect("MainPage.html");
            } else {
                try{
                    applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
                    boolean res = appDBAuth.addUser(Username, Hash, Name, Phone, Email, Role);

                    //Verify if the user has been authenticated
                    if (res){
                        %>
                        <p class="message success">User Registered</p>
                        <%
                    } else {
                        session.setAttribute("Username", Username);
                        %>
                        <p class="message error">User NOT Registered. Already exists, try again.</p>
                        <%
                    }

                    //Close the connection to the database
                    appDBAuth.close();
                } catch(Exception e) {
                    %>
                    <p class="message error">An error occurred. Please try again.</p>
                    <%
                    e.printStackTrace();
                } finally {
                    session.setAttribute("Username", null);
                }
            }
        %>
    </div>
</body>
</html>
