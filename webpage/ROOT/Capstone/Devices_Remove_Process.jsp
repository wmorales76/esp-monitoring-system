<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.CAPSTONE package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
<%@ page import="java.sql.*"%>
<html>
	<head>
		<title>Removing Device!</title> <!-- Title of the web page -->
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
	color:white;
}
</style>
	</head>
	<body>

<%
	//Retrieve variables
	//String User_ID = request.getParameter("User_ID"); // Retrieving 'User_ID' parameter from the request
	
	//Retrieve variables
int UserID = (Integer) session.getAttribute("User_ID");
String User_ID_Str = request.getParameter("User_ID");
String User_ID;

if (User_ID_Str == null) {
    User_ID = Integer.toString(UserID);
} else {
    User_ID = User_ID_Str;
}


	String Username = (String) session.getAttribute("Username");
	String Device_ID = request.getParameter("Device_ID"); // Retrieving 'device' parameter from the request
	
	//Check if User_ID or Device_ID is null
	if (Username == null || Device_ID == null) { // If 'User_ID' or 'Device_ID' is null
		session.setAttribute("currentPage", null); // Setting 'currentPage' attribute of the session to null
		session.setAttribute("username", null); // Setting 'User_ID' attribute of the session to null
		response.sendRedirect("Home.html"); // Redirecting to 'MainPage.html'
	} else { // If 'User_ID' and 'Device_ID' are not null
		//Try to connect the database using the applicationDBManager class
		try{
				//Create the appDBMnger object
				applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood(); // Creating an instance of 'applicationDBAuthenticationGood' class
				System.out.println("Connecting..."); // Printing 'Connecting...' to the console
				System.out.println(appDBAuth.toString()); // Printing the string representation of 'appDBAuth' object to the console
				
				//Call the removeDevice. This method assigns a device to a user in the database.
				boolean res = appDBAuth.removeDevice(User_ID, Device_ID); // Calling 'removeDevice' method of 'appDBAuth' object and storing the result in 'res' variable
				
				//Verify if the device has been assigned
				if (res){ // If 'res' is true
					%>
					<div class='popup'>Device NOT Removed</div> <!-- Displaying 'Device NOT Assigned' on the web page -->
				<%}
				else // If 'res' is false
				{
					%>
					<div class='popup'>Device Removed</div> <br> <!-- Displaying 'Device Assigned' on the web page -->
				<%}
					
					//Close the connection to the database
					appDBAuth.close(); // Closing the connection to the database
				} catch(Exception e) // Catching any exceptions
				{%>
					Nothing to show! <!-- Displaying 'Nothing to show!' on the web page -->
					<%e.printStackTrace(); // Printing the stack trace of the exception to the console
				}finally{
					System.out.println("Finally"); // Printing 'Finally' to the console
	}}
				%>		
	</body>
</html>
