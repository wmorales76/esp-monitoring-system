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
.scrollable-table {
    height: 300px; /* Adjust as needed */
    overflow-y: auto;
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

    if (Username == null || Role == null || !Role.equals("Admin")) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
        session.setAttribute("currentPage", "LowDanger.jsp");
        out.println("<div style='text-align: center; font-size: 24px;'>Welcome "+Username +". In this page, you can insert or remove the data for registered devices.</div>");


	try{
			//Create the appDBMnger object
			applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
			System.out.println("Connecting...");
			System.out.println(appDBAuth.toString());
			
			//Call the showDeviceInformation method. This method returns a ResultSet containing all the tuples in the table Device_Information
			ResultSet result = appDBAuth.ViewDeviceInformation();
			
%>
<div class="scrollable-table">
<table border="1" id="refreshTable">
				<tr>
					<th>Info_ID</th>
					<th>Building</th>
                    <th>Room</th>
                    <th>Area</th>
                    <th>Purpose</th>
                    <th>Device_UID</th>
				</tr>
				<%
				while (result.next())
				{
					String Info_ID = result.getString("Info_ID");
					String Building = result.getString("Building");
                    String Room = result.getString("Room");
                    String Area = result.getString("Area");
                    String Purpose = result.getString("Purpose");
                    String Device_ID = result.getString("Device_ID");
					String Device_UID = result.getString("Device_UID");
				%>
				<tr>
					<td><%=Info_ID%></td>
					<td><%=Building%></td>
                    <td><%=Room%></td>
                    <td><%=Area%></td>
                    <td><%=Purpose%></td>
                    <td><%=Device_UID%></td>
				</tr>
				<%
				}
				%>
</table>
</div>
<div class="container">
<%
    // Fetch all Device_IDs from Device_Information table
    ResultSet DeviceAddInfo = appDBAuth.DeviceUID();
%>
<div class="form-container">
<form action="Devices_InfoAdd_Process.jsp">
    <label for="building">Building:</label>
    <input type="text" id="building" name="building">
    <label for="room">Room:</label>
    <input type="text" id="room" name="room">
    <label for="area">Area:</label>
    <input type="text" id="area" name="area">
    <label for="purpose">Purpose:</label>
    <input type="text" id="purpose" name="purpose">
    <label for="device">Select a Device UID to insert or update its information:</label>
    <select id="device" name="device">
    <% while (DeviceAddInfo.next()) { %>
        <option value="<%=DeviceAddInfo.getString("Device_ID")%>"><%=DeviceAddInfo.getString("Device_UID")%></option>
    <% } %>
    </select>
    <input type="submit" value="Update/Insert Info">
</form>
</div>
<%
    // Fetch all Device_IDs from Device_Information table for removal
    ResultSet DeviceRemoveInfo = appDBAuth.RemoveInfoFromDevice();
%>
<div class="form-container">
<form action="Devices_InfoRemove_Process.jsp">
    <label for="device">Select a Device UID to remove its information:</label>
    <select id="device" name="device">
    <% while (DeviceRemoveInfo.next()) { %>
        <option value="<%=DeviceRemoveInfo.getString("Device_ID")%>"><%=DeviceRemoveInfo.getString("Device_UID")%></option>
    <% } %>
    </select>
    <input type="submit" value="Remove the Device">
</form>
</div>
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
