<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<html>
<head>
<style>
.popup {
    width: 50%;
    margin: 0 auto;
    padding: 20px;
    background-color: #4CAF50;
    border: 1px solid #ddd;
    border-radius: 15px;
    text-align: center;
    font-size: 2em;
	 color: white;
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
        session.setAttribute("currentPage", "Devices_InfoAdd_Process.jsp");
	try{
			//Create the appDBMnger object
			applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
			System.out.println("Connecting...");
			System.out.println(appDBAuth.toString());
			
			//Get the values from the form
			String Building = request.getParameter("building");
			String Room = request.getParameter("room");
			String Area = request.getParameter("area");
			String Purpose = request.getParameter("purpose");
			String Device_ID = request.getParameter("device");
			
			//Call the addDeviceInformation method. This method adds a new record to the Device_Information table
			appDBAuth.Upsert(Building, Room, Area, Purpose, Device_ID);
			
			out.println("<div class='popup'>The information of the device has been updated/added successfully.</div>");
			
		} catch(Exception e)
		{%>
			Error: <%=e.getMessage()%>.
		<%}finally
		{
			System.out.println("Finally");
		}	
    }
%>
</body>
</html>

