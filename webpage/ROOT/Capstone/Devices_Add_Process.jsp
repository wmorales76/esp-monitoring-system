<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<html>
	<head>
		<title>Adding Device!</title> <!-- Title of the web page -->
<style>
.popupGreen {
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
.popupRed {
    width: 50%;
    margin: 0 auto;
    padding: 20px;
    background-color: #F44336;
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
	//int User_ID = Integer.parseInt(request.getParameter("User_ID"));
	
		//Retrieve variables
int UserID = (Integer) session.getAttribute("User_ID");
String User_ID_Str = request.getParameter("User_ID");
int User_ID;

if (User_ID_Str == null) {
    User_ID = UserID;
} else {
    User_ID = Integer.parseInt(User_ID_Str);
}

	
	String Device_UID = request.getParameter("Device_UID"); // Retrieving 'Device_UID' parameter from the request
	String Device_Name = request.getParameter("Device_Name"); // Retrieving 'Device_Name' parameter from the request
	
	//Check if User_ID, Device_UID or Device_Name is null
	if (Device_UID == null || Device_Name == null) { // If 'User_ID', 'Device_UID' or 'Device_Name' is null
		session.setAttribute("currentPage", null); // Setting 'currentPage' attribute of the session to null
		session.setAttribute("username", null); // Setting 'User_ID' attribute of the session to null
		response.sendRedirect("Home.html"); // Redirecting to 'MainPage.html'
	} else { // If 'User_ID', 'Device_UID' and 'Device_Name' are not null
		//Try to connect the database using the applicationDBManager class
		try{
				//Create the appDBMnger object
				applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood(); // Creating an instance of 'applicationDBAuthenticationGood' class
				System.out.println("Connecting..."); // Printing 'Connecting...' to the console
				System.out.println(appDBAuth.toString()); // Printing the string representation of 'appDBAuth' object to the console
				
				//Call the assignDevice. This method assigns a device to a user in the database.
				boolean res = appDBAuth.assignDevice(User_ID, Device_UID, Device_Name); // Calling 'assignDevice' method of 'appDBAuth' object and storing the result in 'res' variable
				
				//Verify if the device has been assigned
				if (res){ // If 'res' is true
					%>
					<div class='popupGreen'>Device UID Successfully Assigned.</div> <br> <!-- Displaying 'Device Assigned' on the web page -->
				<%}
				else // If 'res' is false
				{
					%>
					<div class='popupRed'>Device UID is already registered to an existing user.</div><!-- Displaying 'Device NOT Assigned' on the web page -->
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
