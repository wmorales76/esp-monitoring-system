<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.CAPSTONE package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
<%@ page import="java.sql.*"%>
<html>
<Head>
<style>
    body {
        font-family: Arial, sans-serif;
        margin: 0;
        padding: 0;
        background-color: #f4f4f4;
    }

    table {
        width: 100%;
        margin: 20px 0;
        border-collapse: collapse;
    }

    table, th, td {
        border: 1px solid #ddd;
        text-align: left;
    }

    th, td {
        padding: 15px;
    }

    th {
        background-color: #4CAF50;
        color: white;
    }

    tr:nth-child(even) {
        background-color: #f2f2f2;
    }

    a {
        color: #333;
        text-decoration: none;
    }

    a:hover {
        color: #4CAF50;
    }

    .sign-out {
        display: block;
        width: 100px;
        height: 35px;
        margin: 20px auto;
        background: #4CAF50;
        padding: 10px;
        text-align: center;
        border-radius: 5px;
        color: white;
        font-weight: bold;
        line-height: 25px;
    }

    .sign-out:hover {
        background: #45a049;
    }
	
.lowDanger {
    background-color: #90EE90; /* light green */
}
.mediumDanger {
    background-color: #FFE666; /* orange */
}
.highDanger {
    background-color: #FF8066; /* red */
}
a {
    text-decoration: none; /* removes underline */
    color: #884DFF; /* changes color to bright blue */
}
</style>

</Head>
<%
    String Username = (String) session.getAttribute("Username"); // Retrieving 'Username' attribute from the session
    String Role = (String) session.getAttribute("Role"); // Retrieving 'Role' attribute from the session

    if (Username == null || Role == null) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
		session.setAttribute("currentPage", "SearchResult.jsp"); // Setting 'currentPage' attribute of the session to 'SearchResult.jsp'
		
		// Retrieve the Device_ID and Danger values from the request
		String Danger = request.getParameter("Danger");
		//String Device_ID = request.getParameter("Device_ID");
		String Device_Name = request.getParameter("Device_Name");
		
		// Create the appDBMnger object
		applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
		System.out.println("Connecting...");
		System.out.println(appDBAuth.toString());
		
		// Call the showDeviceMonitor method. This method returns a ResultSet containing the tuples in the Device_Monitor table that match the search inputs
		//ResultSet result = appDBAuth.showDeviceMonitorSearchJOIN(Username, Device_ID, Danger);
		ResultSet result = appDBAuth.SearchAdmin(Device_Name, Danger);
%>
<table border="1">
	<tr>
		<th>Device_ID</th>
		<th>Parts Per Million</th>
		<th>Temperature</th>
		<th>Relative Humidity</th>
		<th>Date / Time</th>
		<th>Danger</th>
	</tr>
	<%
	while (result.next())
	{
		int Monitor_ID = result.getInt("Monitor_ID");
		String Parts_Per_Million = result.getString("Parts_Per_Million");
		String Temperature = result.getString("Temperature");
		String Relative_Humidity = result.getString("Relative_Humidity");
		String Danger_Result = result.getString("Danger");
		Timestamp Timestamp = result.getTimestamp("Timestamp");
		String Device_ID = result.getString("Device_ID");
		
		String rowClass = "";
					
					switch (Danger_Result) {
					case "Low":
					rowClass = "lowDanger";
					break;
					case "Medium":
					rowClass = "mediumDanger";
					break;
					case "High":
					rowClass = "highDanger";
					break;}
	%>
	<tr>
	<td><a href="DeviceDetails.jsp?Device_ID=<%=Device_ID%>"><%=Device_Name%></a></td>
		<td><%=Parts_Per_Million%></td>
		<td><%=Temperature%></td>
		<td><%=Relative_Humidity%></td>
		<td><%=Timestamp%></td>
		<td class="<%=rowClass%>"><%=Danger_Result%></td>
	</tr>
	<%
	}
	result.close();
	}
%>
</table>
<div style="margin-top: 20px;">
        <form action="Sign-Out.jsp"> <!-- Form starts here, it sends data to 'SignOutAdmin.jsp' -->
        <input type="submit" value="Sign out"> <!-- Submit button with value 'Sign out' -->
        </form> <!-- Form ends here -->
</div>
</html>
