// Import required java libraries
import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

import org.json.JSONArray;//TEST JSON OBJECT
import org.json.JSONObject;//TEST JSON OBJECT
import org.json.JSONException;

/*IMPORTAR el file donde esta la clase.*/
import ut.JAR.CPEN410.applicationDBAuthenticationGoodComplete;
import ut.JAR.CPEN410.applicationDBManager;


// Extend HttpServlet class
public class authenticateApplicationDummy extends HttpServlet {
 
   private String message;
   private String title;
   private applicationDBAuthenticationGoodComplete auth; // Instanciar "auth" object
   private applicationDBManager ListBooks; // Instanciar "auth" object

   public void init() throws ServletException {
      // Do required initialization
      message = "Hello World";
      title = "my first servlet";
      auth = new applicationDBAuthenticationGoodComplete();
      ListBooks = new applicationDBManager();
   }

//TEST JSON OBJECTS
public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    PrintWriter out = response.getWriter();

    ResultSet rs = ListBooks.listAllBooks();

    JSONArray jsonArray = new JSONArray();

    try {
        while (rs.next()) {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("Book ID", rs.getString("bookID"));
                jsonObject.put("Title", rs.getString("title"));
                jsonObject.put("Author", rs.getString("author"));
                jsonObject.put("Publisher", rs.getString("publisher"));
                jsonObject.put("Genre", rs.getString("description"));
                jsonObject.put("Plot", rs.getString("plot"));
                jsonObject.put("Publishing Year", rs.getString("publishingYear"));
                jsonObject.put("Price", rs.getString("price"));
                jsonObject.put("Image URL", rs.getString("imageURL"));
                jsonObject.put("Quantity", rs.getString("quantity"));
            } catch (JSONException e) {
                System.out.println("Error during creating JSON object: " + e.getMessage());
            }

            jsonArray.put(jsonObject);
        }
    } catch (SQLException e) {

        System.out.println("Error during listing books: " + e.getMessage());
    }

    out.print(jsonArray.toString());
}



/*Usar el metodo de autenticacion de las paginas */
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
     
      // Retrieve the http request parameters
      String userName = request.getParameter("userName");
      String userPass = request.getParameter("userPass");
     
      // Set response content type
      response.setContentType("text/html");

      // Actual logic goes here.
      PrintWriter out = response.getWriter();
     
      // Perform the actual authentication process
      ResultSet rs = auth.authenticateAndroid(userName, userPass);
      String msg;
      try {
          if (rs.next()) {
              msg = "Authenticated!";
          } else {
              msg = "Not authenticated!";
          }
      } catch (SQLException e) {
          msg = "Error during authentication!";
      }
      
      // Send the final response to the requester
      out.println(msg);
      System.out.println(msg);
   }

   public void destroy() {
      // Close the database connection
      auth.close();
   }
}
