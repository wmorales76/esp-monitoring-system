<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.PROJECT package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
<%@ page import="java.sql.*"%>
<html>
<%
    String Username = (String) session.getAttribute("Username");
    String Role = (String) session.getAttribute("Role");

    // Clearing the session attributes
    session.setAttribute("Username", null);
    session.setAttribute("Role", null);
    session.setAttribute("currentPage", "Home.html");
    // Redirecting to the main page
    response.sendRedirect("Home.html");
%>
</html>
