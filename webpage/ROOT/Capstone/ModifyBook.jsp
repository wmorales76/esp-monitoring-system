<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.PROJECT package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
<%@ page import="java.sql.*"%>
<html>
	<head>
		<title>Adding Book!</title> <!-- Title of the web page -->
	</head>
	<body>

<%
	//Retrieve variables
	String userName = request.getParameter("userName"); // Retrieving 'userName' parameter from the request
	String OldTitle = request.getParameter("OldTitle"); // Retrieving 'OldTitle' parameter from the request
	
	//Check if OldTitle is null
	if (OldTitle == null) { // If 'OldTitle' is null
		session.setAttribute("currentPage", null); // Setting 'currentPage' attribute of the session to null
		session.setAttribute("userName", null); // Setting 'userName' attribute of the session to null
		response.sendRedirect("MainPage.html"); // Redirecting to 'loginHashing.html'
	} else { // If 'OldTitle' is not null
		String Title = request.getParameter("Title"); // Retrieving 'Title' parameter from the request
		String Genre = request.getParameter("Genre"); // Retrieving 'Genre' parameter from the request
		String Plot = request.getParameter("Plot"); // Retrieving 'Plot' parameter from the request
		String Author = request.getParameter("Author"); // Retrieving 'Author' parameter from the request
		String Editor = request.getParameter("Editor"); // Retrieving 'Editor' parameter from the request
		int Publishing_Year = Integer.parseInt(request.getParameter("Publishing_Year")); // Retrieving 'Publishing_Year' parameter from the request and converting it to integer
		double Price = Double.parseDouble(request.getParameter("Price")); // Retrieving 'Price' parameter from the request and converting it to double
		int Quantity = Integer.parseInt(request.getParameter("Quantity")); // Retrieving 'Quantity' parameter from the request and converting it to integer
		String ImageURL = request.getParameter("ImageURL"); // Retrieving 'ImageURL' parameter from the request
		
		//Try to connect the database using the applicationDBManager class
		try{
				//Create the appDBMnger object
				applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood(); // Creating an instance of 'applicationDBAuthenticationGood' class
				System.out.println("Connecting..."); // Printing 'Connecting...' to the console
				System.out.println(appDBAuth.toString()); // Printing the string representation of 'appDBAuth' object to the console
				
				//Call the updateBook method. This method overwrites all the tuples in the table books by user inputted information.
				boolean res=appDBAuth.updateBook(OldTitle, Title, Genre, Plot, Author, Editor, Publishing_Year, Price, Quantity, ImageURL); // Calling 'updateBook' method of 'appDBAuth' object and storing the result in 'res' variable
				
				//Verify if the book has been updated
				if (res){ // If 'res' is true
					%>
					Book NOT Modified <!-- Displaying 'Book NOT Modified' on the web page -->
				<%}
				else // If 'res' is false
				{
					%>
					Book Modified <br> <!-- Displaying 'Book Modified' on the web page -->
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
	sessionName=<%=session.getAttribute("userName")%> <!-- Retrieving 'userName' attribute from the session and assigning it to 'sessionName' variable -->
	</body>
</html>
