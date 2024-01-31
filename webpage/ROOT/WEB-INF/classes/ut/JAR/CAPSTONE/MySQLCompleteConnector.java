//This class belongs to the ut.JAR.CPEN410 package
package ut.JAR.CAPSTONE;

//Import the java.sql package for managing the ResulSet objects
import java.sql.* ;

/******
	This class manage a connection to the Department database and it should not be accessed from the front End. 
*/
public class MySQLCompleteConnector{

	//Database credential <jdbc:<protocol>://<hostName>/<databaseName>>
	//private String DB_URL="jdbc:mysql://localhost/capstone";
	private String DB_URL="jdbc:mysql://capstone-database.cbgkiweqag9q.us-east-2.rds.amazonaws.com/capstone";
	//Database authorized user information
	private String USER="admin"; //student
	private String PASS="wmorales";//password
   
   //Connection objects
   private Connection conn;
   
   //Statement object to perform queries and transations on the database
   private Statement stmt;
   public Statement getStatement() {
        return stmt;
    }
	/********
		Default constructor
		@parameters:
		
	*/
	public MySQLCompleteConnector()
	{
		//define connections ojects null
		conn = null;
		stmt = null;}
		
	/********
		doConnection method
			It creates a new connection object and open a connection to the database
			@parameters:

	*/		
	public void doConnection(){
		try{
		  //Register JDBC the driver
		  Class.forName("com.mysql.jdbc.Driver").newInstance();

								   
		  System.out.println("Connecting to database...");
		   //Open a connection using the database credentials
		  conn = DriverManager.getConnection(DB_URL, USER, PASS);
		  
		  System.out.println("Creating statement...");
		  //Create an Statement object for performing queries and transations
		  stmt = conn.createStatement();
		  System.out.println("Statement Ok...");
		} catch(SQLException sqlex){
			sqlex.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	/********
		closeConnection method
			Close the connection to the database
			@parameters:

	*/		
	public void closeConnection()
	{
		try{
			//close the statement object
			stmt.close();
			//close the connection to the database
			conn.close();
			}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/***********
		doSelect method
			This method performs a query to the database
			@parameters:
				fields: list of fields to be projected from the tables
				tables: list of tables to be selected
				where: where clause
			@returns:
				ResulSet result containing the project tuples resulting from the query
	*/
	public ResultSet doSelectLogIn(String fields, String tables, String where){
		//Create a ResulSet
		ResultSet result=null;
		
		//Create the selection statement 
		String selectionStatement = "Select " + fields+ " from " + tables + " where " + where + " ;";
		System.out.println(selectionStatement);
		
		try{
			//perform the query and catch results in the result object
			result = stmt.executeQuery(selectionStatement);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//return results
			return result;
		}
	}
	
	public ResultSet doSelect(String fields, String tables, String where){
		//Create a ResulSet
		ResultSet result=null;
		
		//Create the selection statement 
		String selectionStatement = "Select " + fields+ " from " + tables + " where " + where + " ;";
		System.out.println(selectionStatement);
		
		try{
			//perform the query and catch results in the result object
			result = stmt.executeQuery(selectionStatement);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//return results
			return result;
		}
	}
	/***********
		doSelect method
			This method performs a query to the database
			@parameters:
				fields: list of fields to be projected from the tables
				tables: list of tables to be selected
			@returns:
				ResulSet result containing the project tuples resulting from the query
	*/
	
	public ResultSet doSelect(String fields, String tables){
		//Create a ResulSet
		ResultSet result=null;
		
		//Create the selection statement 
		String selectionStatement = "Select " + fields + " from " + tables + ";";
		System.out.println(selectionStatement);
		
		try{
			//perform the query and catch results in the result object
			result = stmt.executeQuery(selectionStatement);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//return results
			return result;
		}
	}
	
	/***********
		doSelect method
			This method performs a query to the database
			@parameters:
				fields: list of fields to be projected from the tables
				tables: list of tables to be selected
				where: where clause
				orderBy: order by condition
			@returns:
				ResulSet result containing the project tuples resulting from the query
	*/
	public ResultSet doSelect(String fields, String tables, String where, String orderBy){
		
		//Create a ResulSet
		ResultSet result=null;
		
		//Create the selection statement 
		String selectionStatement = "Select" + fields+ " from " + tables + " where " + where + " order by " + orderBy + ";";
		
		try{
			//perform the query and catch results in the result object
			result = stmt.executeQuery(selectionStatement);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//return results
			return result;
		}
	}
	
	/***********
		doInsertion method
			This method performs an insertion to the database
			@parameters:
				values: values to be inserted 
				table: table to be updated
				
			@returns:
				boolean value: true: the insertion was ok, an error was generated
	*/
	
	public boolean doInsert(String table, String values)
	{
		boolean res=false;
		String charString ="INSERT INTO "+ table + " values (" + values +");";
		System.out.println(charString);
		//try to insert a record to the selected table
		try{
			 res=stmt.execute(charString);
			 System.out.println("MySQLCompleteConnector insertion: " + res);
			 
		}
		catch(Exception e)
		{
			
			e.printStackTrace();
		}
		finally{
			
		}
			return res;
	}
	
	/*public boolean doInsertDevice(String table, String columns, String values)
{
    boolean res=false;
    String charString ="INSERT INTO "+ table + " (" + columns + ") values (" + values +");";
    System.out.println(charString);
    //try to insert a record to the selected table
    try{
         res=stmt.execute(charString);
         System.out.println("MySQLCompleteConnector insertion: " + res);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    finally{
        
    }
    return res;
}*/

public boolean doInsertDevice(String table, String columns, String values)
{
    boolean res=false;
    String charString ="INSERT INTO "+ table + " (" + columns + ") values (" + values +");";
    System.out.println(charString);
    //try to insert a record to the selected table
    try{
         int rowsAffected = stmt.executeUpdate(charString);
         res = rowsAffected > 0;
         System.out.println("MySQLCompleteConnector insertion: " + res);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    finally{
        
    }
    return res;
}


	
	public boolean doInsertInfo(String table, String columns, String values)
{
    boolean res = false;
    String charString = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ");";
    System.out.println(charString);
    // try to insert a record into the selected table
    try {
        res = stmt.execute(charString);
        System.out.println("MySQLCompleteConnector insertion: " + res);
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        // You can put any cleanup code here if needed
    }
    return res;
}


public boolean doInsertUsers(String table, String values) {
    boolean res = false;
    String charString = "INSERT INTO " + table + " (Username, Hash, Name, Phone, Email, Role) VALUES (" + values + ");";
    System.out.println(charString);
    //try to insert a record to the selected table
    try {
        stmt.execute(charString);
        res = true; // Change this line
        System.out.println("MySQLCompleteConnector insertion: " + res);
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        return res;
    }
}


public boolean userExists(String Username) {
    boolean res = false;
    String query = "SELECT * FROM users WHERE Username = '" + Username + "'";
    try {
        ResultSet rs = stmt.executeQuery(query);
        res = rs.next();
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        return res;
    }
}

	
	public boolean doDeleteDeviceInfo(String table, String Device_ID)
{
    boolean res = false;
    String charString = "DELETE FROM " + table + " WHERE Device_ID = '" + Device_ID + "';";
    System.out.println(charString);
    // try to delete a record from the selected table
    try {
        res = stmt.execute(charString);
        System.out.println("MySQLCompleteConnector deletion: " + res);
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        // You can put any cleanup code here if needed
    }
    return res;
}

	public boolean doDeleteDeviceMonitor(String table, String Device_ID)
{
    boolean res = false;
    String charString = "DELETE FROM " + table + " WHERE Device_ID = '" + Device_ID + "';";
    System.out.println(charString);
    // try to delete a record from the selected table
    try {
        res = stmt.execute(charString);
        System.out.println("MySQLCompleteConnector deletion: " + res);
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        // You can put any cleanup code here if needed
    }
    return res;
}
	
	//Method to delete a device assignment from the table
public boolean doDeleteDevice(String table, String User_ID, String Device_ID)
{
    boolean res = false;
    String charString = "DELETE FROM " + table + " WHERE User_ID = '" + User_ID + "' AND Device_ID = '" + Device_ID + "';";
    System.out.println(charString);
    //try to delete a record from the selected table
    try{
        res = stmt.execute(charString);
        System.out.println("MySQLCompleteConnector deletion: " + res);
    }
    catch(Exception e)
    {
        e.printStackTrace();
    }
    finally{
        // You can add any cleanup code here if needed
    }
    return res;
}

//Method to delete users://CAPSTONE
/*public boolean doDeleteUsers(String table, String[] usernames)
{
    boolean res = false;
    for (String Username : usernames) {
        String charString = "DELETE FROM " + table + " WHERE Username = '" + Username + "';";
        System.out.println(charString);
        // try to delete a record from the selected table
        try {
            res = stmt.execute(charString);
            System.out.println("MySQLCompleteConnector deletion: " + res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return res;
}*///                     JANUARY 18, 2024 

public String doDeleteUsers(String table, String[] usernames)
{
    String res = "Success";
    for (String Username : usernames) {
        String charString = "DELETE FROM " + table + " WHERE Username = '" + Username + "';";
        System.out.println(charString);
        // try to delete a record from the selected table
        try {
            boolean result = stmt.execute(charString);
            System.out.println("MySQLCompleteConnector deletion: " + result);
        } catch (Exception e) {
            if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
                res = "Failed";
            }
            e.printStackTrace();
        }
    }
    return res;
}



//Method to modify a row based on title
public boolean doUpdate(String table, String OldTitle, String Title, String Genre, String Plot, String Author, String Editor, int Publishing_Year, double Price, int Quantity, String ImageURL) 
{
    boolean res = false;
	String charString = "UPDATE " + table + " SET title = '" + Title + "', genre = '" + Genre + "', plot = '" + Plot + "', author = '" + Author + "', editor = '" + Editor + "', publishing_year = " + Publishing_Year + ", price = " + Price + ", quantity = " + Quantity + ", imageURL = '" + ImageURL + "' WHERE title = '" + OldTitle + "'";
    System.out.println(charString);
    try {
        res = stmt.execute(charString);
        System.out.println("MySQLCompleteConnector insertion: " + res);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
    }
    return res;
}


//Method to show everything
	public ResultSet doShowAll(String fields, String tables)
	{
		//Create a ResulSet
		ResultSet result=null;
		
		//Create the selection statement 
		String selectionStatement = "Select " + fields+ " from " + tables +";";
		System.out.println(selectionStatement);
		
		try{
			//perform the query and catch results in the result object
			result = stmt.executeQuery(selectionStatement);
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			//return results
			return result;
		}
	}
	
	public ResultSet doShowAllByOrder(String fields, String tables, String orderBy)
{
    //Create a ResultSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    // Add the ORDER BY clause
    if (orderBy != null && !orderBy.isEmpty()) {
        selectionStatement += " ORDER BY " + orderBy;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}

public ResultSet doShowDistinct(String fields, String tables)
{
    //Create a ResulSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT DISTINCT " + fields + " FROM " + tables + ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}

public ResultSet doShowAllJOIN(String fields, String tables, String condition)
{
    //Create a ResulSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables + " WHERE " + condition + ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}


// Method to show device monitors by Device_ID and Danger.//CAPSTONE
public ResultSet doShowDeviceMonitorSearch(String fields, String tables, String condition)
{
    // Create a ResultSet
    ResultSet result = null;
    
    // Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    if (condition != null && !condition.isEmpty()) {
        selectionStatement += " WHERE " + condition;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try {
        // Perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        // Return results
        return result;
    }
}


//Method to show by selected danger//CAPSTONE
public ResultSet doShowByDanger(String fields, String tables, String condition)
{
    //Create a ResultSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    if (condition != null && !condition.isEmpty()) {
        selectionStatement += " WHERE " + condition;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}

public ResultSet doShowByDangerOrdered(String fields, String tables, String condition, String orderBy)
{
    //Create a ResultSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    if (condition != null && !condition.isEmpty()) {
        selectionStatement += " WHERE " + condition;
    }
    // Add the ORDER BY clause
    if (orderBy != null && !orderBy.isEmpty()) {
        selectionStatement += " ORDER BY " + orderBy;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}


//Method to show by selected danger//CAPSTONE
public ResultSet doShowByCondition(String fields, String tables, String condition)
{
    //Create a ResultSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    if (condition != null && !condition.isEmpty()) {
        selectionStatement += " WHERE " + condition;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}

public ResultSet doShowByOrder(String fields, String tables, String condition, String orderBy)
{
    //Create a ResultSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    if (condition != null && !condition.isEmpty()) {
        selectionStatement += " WHERE " + condition;
    }
    // Add the ORDER BY clause
    if (orderBy != null && !orderBy.isEmpty()) {
        selectionStatement += " ORDER BY " + orderBy;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}




//Method to show the selected items on the cart
	public ResultSet doShowCart(String fields, String tables, String condition)
{
    //Create a ResultSet
    ResultSet result=null;
    
    //Create the selection statement 
    String selectionStatement = "SELECT " + fields + " FROM " + tables;
    if (condition != null && !condition.isEmpty()) {
        selectionStatement += " WHERE " + condition;
    }
    selectionStatement += ";";
    System.out.println(selectionStatement);
    
    try{
        //perform the query and catch results in the result object
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        //return results
        return result;
    }
}

//Method to update by condition
/*	public void doUpdate(String set, String table, String condition)
{
    //Create the update statement 
    String updateStatement = "UPDATE " + table + " SET " + set + " WHERE " + condition + ";";
    System.out.println(updateStatement);
    
    try{
        //execute the update statement
        stmt.executeUpdate(updateStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
}*/

public boolean doUpdate(String set, String table, String condition)
{
    boolean res = false;
    //Create the update statement 
    String updateStatement = "UPDATE " + table + " SET " + set + " WHERE " + condition + ";";
    System.out.println(updateStatement);
    
    try{
        //execute the update statement
        int rowsAffected = stmt.executeUpdate(updateStatement);
        res = (rowsAffected > 0); // if any row affected, update is successful
    } catch(Exception e){
        e.printStackTrace();
    }
    return res;
}


public boolean recordExists(String table, String condition) {
    boolean exists = false;
    String query = "SELECT 1 FROM " + table + " WHERE " + condition + " LIMIT 1;";
    System.out.println(query);

    try {
        ResultSet resultSet = stmt.executeQuery(query);
        if (resultSet.next()) {
            exists = true;
        }
        resultSet.close();
    } catch(Exception e) {
        e.printStackTrace();
    }

    return exists;
}


//Method to show past orders where it is filtered by username
public ResultSet doShowOrder(String fields, String tables, String condition)
{
    ResultSet result=null;
    String selectionStatement = "SELECT " + fields + " FROM " + tables + " WHERE " + condition + ";";
    System.out.println(selectionStatement);
    
    try{
        result = stmt.executeQuery(selectionStatement);
    } catch(Exception e){
        e.printStackTrace();
    }
    finally{
        return result;
    }
}




	/***********
		Debugging method
			This method creates an applicationDBManager object, retrieves all departments in the database, and close the connection to the database
			@parameters:
				args[]: String array 
			@returns:
	*/
	public static void main(String[] args)
	{	
		System.out.println("Testing");
		//Create a MySQLConnector
		MySQLConnector conn = new MySQLConnector();
		//Declare tthe fiels, tables and whereClause string objects
		String fields, tables, whereClause;
		//Define the projected fields
		fields ="userName, userPass";
		//Define the selected tables
		tables="users";
		//Establish the where clause
		whereClause="userName=null";		
		
			
		try{
			System.out.println("Connecting...");
			//Establish the database connection
			conn.doConnection();
			//perform the query using the doSelect methods with 3 parameters
			ResultSet res=conn.doSelect(fields, tables, whereClause);
		
			//Iterate over the ResulSet containing all departments in the database, and count how many tuples were retrieved
			int count=0;
			while (res.next())
			{
				count++;
				
			}
			//Print the results count
			System.out.println("Count: " + count);
			
			//Close the ResulSet
			res.close();
			//Close the database connection
			conn.closeConnection();
			
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}