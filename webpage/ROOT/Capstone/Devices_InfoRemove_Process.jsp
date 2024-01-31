<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.CAPSTONE package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
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
<%
    String Username = (String) session.getAttribute("Username");
    String Role = (String) session.getAttribute("Role");

    if (Username == null || Role == null || !Role.equals("Admin")) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
        session.setAttribute("currentPage", "Devices_InfoRemove_Process.jsp");

	try{
			//Create the appDBMnger object
			applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
			System.out.println("Connecting...");
			System.out.println(appDBAuth.toString());
			
			//Get the Device_ID from the form
			String Device_ID = request.getParameter("device");
			
			//Call the removeDeviceInformation method. This method removes a record from the Device_Information table
			appDBAuth.removeDeviceInformation(Device_ID);
			
			out.println("<div class='popup'>The device information has been removed successfully.</div>");
			
		} catch(Exception e)
		{%>
			Error: <%=e.getMessage()%>.
		<%}finally
		{
			System.out.println("Finally");
		}	
    }
%>
</html>
