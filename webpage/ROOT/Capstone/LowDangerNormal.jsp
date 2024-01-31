<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.CAPSTONE package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
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
    margin: 0;
    padding: 0;
    background-color: #f0f0f0;
	
}
ul {
        list-style-type: none;
    }
.container {
    width: 100%; /* Reduce the width of the container */
    margin: auto;
    overflow: hidden;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin: 10px 0;
}

th, td {
    text-align: left;
    padding: 8px;
    border-bottom: 1px solid #ddd;
}

tr:hover {background-color: #f5f5f5;}

button {
    background-color: #4CAF50;
    color: white;
    padding: 1px 1px;
    margin: 20px 2px; /* Add horizontal margin */
    border: none;
    cursor: pointer;
    width: auto; /* Change the width to auto */
    display: inline-block; /* Add this to display the buttons side by side */
    justify-content: center;
    align-items: center;
    min-height: 5vh; /* vh stands for viewport height */
}

button:hover {
    opacity: 0.8;
}

form {
    margin: 20px 0;
}

input[type=text] {
    width: 15%; /* Reduce the width of the text inputs */
    padding: 10px 20px;
    margin: 8px 0;
    box-sizing: border-box;
}

.sign-out {
    position: absolute;
    top: 1px;
    right: 10px;
}
 #sortingButtons {
        text-align: center;
		 width: 75%;
		 position: Center;
    }
.scrollable-table {
    display: flex;
    justify-content: center; /* aligns items horizontally in the center */
    align-items: center; /* aligns items vertically in the center */
    height: 75%; /* adjust this value as needed */
    overflow-y: auto;
    width: 100%; /* adjust this value as needed */
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
</head>
<%
    String Username = (String) session.getAttribute("Username");
    String Role = (String) session.getAttribute("Role");

    if (Username == null || Role == null) { 
        session.setAttribute("Username", null);
        response.sendRedirect("Home.html"); 
    } else {
        session.setAttribute("currentPage", "LowDangerNormal.jsp");
        out.println("Welcome '"+Username +"'");

	try{
			//Create the appDBMnger object
			applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood();
			System.out.println("Connecting...");
			System.out.println(appDBAuth.toString());
			
			//Call the showDeviceMonitorLow method. This method returns a ResultSet containing all the tuples in the table Device_Monitor where Danger is 'Low'
			ResultSet result = appDBAuth.showDeviceMonitorLowNormal(Username);
			
%>
<div>
<table border="1" id="refreshTable">
				<tr>
					<th>Device_ID</th>
					<th>Parts_Per_Million</th>
					<th>Temperature</th>
					<th>Relative_Humidity</th>
					<th>Timestamp</th>
					<th>Danger</th>
				</tr>
				<%
				while (result.next())
				{
					int Monitor_ID = result.getInt("Monitor_ID");
					String Parts_Per_Million = result.getString("Parts_Per_Million");
					String Temperature = result.getString("Temperature");
					String Relative_Humidity = result.getString("Relative_Humidity");
					String Device_ID = result.getString("Device_ID");
					String Device_Name = result.getString("Device_Name");
					String Danger = result.getString("Danger");
					String rowClass = "";
					
					switch (Danger) {
					case "Low":
					rowClass = "lowDanger";
					break;
					case "Medium":
					rowClass = "mediumDanger";
					break;
					case "High":
					rowClass = "highDanger";
					break;}

					Timestamp Timestamp = result.getTimestamp("Timestamp");
				%>
				<tr>
					<td><a href="DeviceDetails.jsp?Device_ID=<%=Device_ID%>"><%=Device_Name%></a></td>
					<td><%=Parts_Per_Million%></td>
					<td><%=Temperature%></td>
					<td><%=Relative_Humidity%></td>
					<td><%=Timestamp%></td>
					<td class="<%=rowClass%>"><%=Danger%></td>
				</tr>
				<%
				}
				%>
</table>
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
</html>
