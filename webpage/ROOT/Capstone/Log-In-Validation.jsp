<%-- Importing all classes from java.lang package--%>
<%@ page import="java.lang.*"%>
<%-- Importing all classes from ut.JAR.PROJECT package--%>
<%@ page import="ut.JAR.CAPSTONE.*"%>
<%-- Import the java.sql package to use the ResultSet class--%>
<%@ page import="java.sql.*"%>
<%-- Import the java.util package to use the List and ArrayList classes--%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<html>
    <head>
        <title>Your first web form!</title> 
    </head>
    <body>

<%
    String Username = request.getParameter("Username"); 
    String Hash = request.getParameter("Hash"); 
    String Role = "";
    int UserID;
    List<String> Device_UIDs = new ArrayList<String>();

    try{
        applicationDBAuthenticationGood appDBAuth = new applicationDBAuthenticationGood(); 
        System.out.println("Connecting..."); 
        System.out.println(appDBAuth.toString());

        ResultSet res=appDBAuth.authenticate(Username, Hash, Role);

        if (res.next()){
            session.setAttribute("currentPage", "Log-In-Validation.jsp");
            session.setAttribute("Username", Username); 

            Role = res.getString("Role"); 
            session.setAttribute("Role", Role); 

            UserID = res.getInt("User_ID"); // Retrieve User_ID from the ResultSet
            session.setAttribute("User_ID", UserID); // Store User_ID in the session 

            if ("Admin".equals(Role)) {
                response.sendRedirect("WelcomeAdmin.jsp"); 
            } else {
                // Retrieve Device_UIDs from the devices table
                ResultSet deviceRes = appDBAuth.showAssignedDevice();
                while(deviceRes.next()){
                    if(deviceRes.getInt("User_ID") == UserID){
                        Device_UIDs.add(deviceRes.getString("Device_UID"));
                    }
                }
                session.setAttribute("Device_UIDs", Device_UIDs); // Store Device_UIDs in the session
                response.sendRedirect("DevicesDataNormal.jsp"); 
            }

        }else{
            session.setAttribute("Username", null);
            session.setAttribute("User_ID", null);
            session.setAttribute("Role", null); 
            response.sendRedirect("Home.html");
        }

        res.close(); 
        appDBAuth.close();

    } catch(Exception e) {
        response.sendRedirect("Home.html");
        e.printStackTrace();

    } finally {
        System.out.println("Finally");
        System.out.println(Role);
    }
%>      
sessionName=<%=session.getAttribute("Username")%>
DeviceUIDs=<%=session.getAttribute("Device_UIDs")%>
</body>
</html>
