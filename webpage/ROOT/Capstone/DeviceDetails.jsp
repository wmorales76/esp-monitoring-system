<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<html>
<head>
    <title>Device Information</title>
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
        .device-info {
            text-align: center;
        }
        .device-info h1 {
            font-size: 3em;
        }
        .device-info p {
            font-size: 1.5em;
        }
        .error {
            color: red;
            font-size: 1.5em;
        }
    </style>
</head>
<body>
<%
    String Username = (String) session.getAttribute("Username");
    String Role = (String) session.getAttribute("Role");

    if (Username == null || Role == null) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
        session.setAttribute("currentPage", "DeviceDetails.jsp");

    try{
            //Create the appDBMnger object
            applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
            System.out.println("Connecting...");
            System.out.println(appDBAuth.toString());
            
            //Get the Device_ID from the URL
            String Device_ID = request.getParameter("Device_ID");
            
            //Call the getDeviceInformation method. This method returns a ResultSet containing the tuple in the Device_Information table with the given Device_ID
            ResultSet result = appDBAuth.getDeviceInformationTest(Device_ID);
            
            if (result.next())
            {
                String Building = result.getString("Building");
                String Room = result.getString("Room");
                String Area = result.getString("Area");
                String Purpose = result.getString("Purpose");
                String Device_UID = result.getString("Device_UID");
    %>
    <div class="device-info">
        <h1>Device UID: <%=Device_UID%></h1>
        <p><strong>Building:</strong> <%=Building%></p>
        <p><strong>Room:</strong> <%=Room%></p>
        <p><strong>Area:</strong> <%=Area%></p>
        <p><strong>Purpose:</strong> <%=Purpose%></p>
    </div>
    <%
            }
            else
            {
                out.println("<div class='error'>No information found for this device.</div>");
            }
            
            result.close();
        } catch(Exception e)
        {%>
            <div class='error'>Error: <%=e.getMessage()%>.</div>
        <%}finally
        {
            System.out.println("Finally");
        }   
    }
%>
</body>
</html>
