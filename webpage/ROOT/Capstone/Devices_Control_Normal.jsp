<%@ page import="java.lang.*"%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%@ page import="java.sql.*"%>
<html>
<head>
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

.container {
    width: 100%;
    display: flex;
    justify-content: space-between;
}

.form-container {
    border-radius: 15px;
    border: 1px solid #ccc;
    padding: 20px;
    margin: 10px;
    width: 45%;
}

input[type=text], select {
    width: 100%;
    padding: 12px 20px;
    margin: 8px 0;
    display: inline-block;
    border: 1px solid #ccc;
    border-radius: 4px;
    box-sizing: border-box;
}

input[type=submit] {
    width: 100%;
    background-color: #4CAF50;
    color: white;
    padding: 14px 20px;
    margin: 8px 0;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}
.scrollable-table {
    height: 500px; /* Adjust as needed */
    overflow-y: auto;
}

input[type=submit]:hover {
    background-color: #45a049;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-bottom: 20px;
}

table, th, td {
    border: 1px solid #ddd;
}

th, td {
    text-align: left;
    padding: 15px;
}

th {
    background-color: #4CAF50;
    color: white;
}
</style>
</head>
<body>
<%
    String Username = (String) session.getAttribute("Username");
    String Role = (String) session.getAttribute("Role");
	int UserID = (Integer) session.getAttribute("User_ID");

    if (Username == null || Role == null) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
        session.setAttribute("currentPage", "Devices_Control_Normal.jsp");

	try{
			//Create the appDBMnger object
			applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
			System.out.println("Connecting...");
			System.out.println(appDBAuth.toString());
			
			//Call the showAssignedDevice method. This method returns a ResultSet containing all the tuples in the table Assigned_Devices
			ResultSet result = appDBAuth.showAssignedDevice(UserID);
			
%>
<p>Welcome, <%= UserID %>!</p>
<div class="scrollable-table">
<table border="1" id="refreshTable">
				<tr>
					<th>Device Name</th>
					<th>Device UID</th>
				</tr>
				<%
				while (result.next())
				{
					String Device_ID = result.getString("Device_ID");
					String Device_Name = result.getString("Device_Name");
					String User_ID = result.getString("User_ID");
					String Device_UID = result.getString("Device_UID");
					String RegisteredUsername = result.getString("Username");	
				%>
				<tr>
					<td><%=Device_Name%></td>
					<td><%=Device_UID%></td>
				</tr>
				<%
				}
				%>
</table>
</div>
<div class="container">
<%
    // Fetch all usernames from Users table
    ResultSet UserAdd = appDBAuth.showUsers();
%>

<div class="form-container">
<form action="Devices_Remove_Process.jsp">
    <input type="hidden" name="UserID" value="<%=UserID%>">
    Select a Device_ID:
    <select name="Device_ID">
    <% 
        ResultSet DeviceRemove = appDBAuth.DISTINCTDeviceIDNormal(UserID);
        while (DeviceRemove.next()) { %>
            <option value="<%=DeviceRemove.getString("Device_ID")%>"><%=DeviceRemove.getString("Device_UID")%></option>
    <% } %>
    </select>
    <input type="submit" value="Unregister Device">
</form>

</div>
<div class="form-container">
    <p>Write the unique Device UID and the name that will be displayed for it:</p>
    <form action="Devices_Add_Process.jsp">
        <input type="hidden" name="UserID" value="<%=UserID%>">
        Device UID: <input type="text" name="Device_UID"><br>
        Device Name: <input type="text" name="Device_Name"><br>
        <input type="submit" value="Register Device">
    </form>
</div>

</div>
<div style="margin-top: 20px;">
        <form action="Sign-Out.jsp"> <!-- Form starts here, it sends data to 'SignOutAdmin.jsp' -->
        <input type="submit" value="Sign out"> <!-- Submit button with value 'Sign out' -->
        </form> <!-- Form ends here -->
</div>
<%
				result.close();
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
