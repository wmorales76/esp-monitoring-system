import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import ut.JAR.CAPSTONE.applicationDBAuthenticationGood;
import ut.JAR.CAPSTONE.applicationDBManager;
public class Capstone extends HttpServlet {
   private applicationDBAuthenticationGood auth; 

   public void init() throws ServletException {
      auth = new applicationDBAuthenticationGood();
   }
public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    // Get the username from the URL
    String Username = request.getParameter("Username");

    if (Username != null) {
        System.out.println("Username retrieved from URL: " + Username);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        ResultSet rs = auth.showDeviceMonitorAndroid(Username);

        JSONArray jsonArray = new JSONArray();

        try {
            while (rs.next()) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Monitor ID", rs.getString("Monitor_ID"));
                    jsonObject.put("Parts Per Million", rs.getString("Parts_Per_Million"));
                    jsonObject.put("Temperature", rs.getString("Temperature"));
                    jsonObject.put("Relative Humidity", rs.getString("Relative_Humidity"));
                    jsonObject.put("Device ID", rs.getString("Device_ID"));
                    jsonObject.put("Danger", rs.getString("Danger"));
                    jsonObject.put("Timestamp", rs.getString("Timestamp"));
                } catch (JSONException e) {
                    System.out.println("Error during creating JSON object: " + e.getMessage());
                }

                jsonArray.put(jsonObject);
            }
        } catch (SQLException e) {
            System.out.println("Error during listing devices: " + e.getMessage());
        }

        out.print(jsonArray.toString());
    } else {
        System.out.println("No username found in URL.");
    }
}
public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String Username = request.getParameter("Username");
    String Hash = request.getParameter("Hash");
    String Role = request.getParameter("Role");

    response.setContentType("text/html");

    PrintWriter out = response.getWriter();

    ResultSet rs = auth.authenticate(Username, Hash, Role);
    String msg;
    try {
        if (rs.next()) {
            msg = "Authenticated!";

            // Create a new session or use existing one
            HttpSession session = request.getSession(true);
            System.out.println("Session ID: " + session.getId());

            // Set the Username attribute in the session
            session.setAttribute("Username", Username);
            System.out.println("Username set in session: " + session.getAttribute("Username"));
        } else {
            msg = "Not authenticated!";
        }
    } catch (SQLException e) {
        msg = "Error during authentication!";
    }

    out.println(msg);
    System.out.println(msg);
}


   public void destroy() {

      auth.close();
   }
}
