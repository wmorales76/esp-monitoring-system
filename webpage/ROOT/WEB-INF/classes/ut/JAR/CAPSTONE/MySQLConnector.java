//This class belongs to the ut.JAR.CPEN410 package

/*GRANT ALL PRIVILEGES ON capstone.* TO 'student'@'localhost';
FLUSH PRIVILEGES;
*/
package ut.JAR.CAPSTONE;

//Import the java.sql package for managing the ResulSet objects
import java.sql.* ;

/******
	This class manage a connection to the Department database and it should not be accessed from the front End. 
*/
public class MySQLConnector{

	//Database credential <jdbc:<protocol>://<hostName>/<databaseName>>
	//private String DB_URL="jdbc:mysql://localhost/capstone";
	private String DB_URL="jdbc:mysql://capstone-database.cbgkiweqag9q.us-east-2.rds.amazonaws.com/capstone";
    
	//Database authorized user information
	private String USER="admin";
	private String PASS="wmorales";
   
   //Connection objects
   private Connection conn;
   
   //Statement object to perform queries and transations on the database
   private Statement stmt;
   
	/********
		Default constructor
		@parameters:
		
	*/
	public MySQLConnector()
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
		 // Class.forName("com.mysql.jdbc.Driver").newInstance();
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
								   
		  System.out.println("Connecting to database...");
		   //Open a connection using the database credentials
		  conn = DriverManager.getConnection(DB_URL, USER, PASS);
		  
		  System.out.println("Creating statement...");
		  //Create an Statement object for performing queries and transations
		  stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
	public ResultSet doSelect(String fields, String tables, String where)
	{
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
		String selectionStatement = "Select " + fields+ " from " + tables + ";";
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
		Debugging method
			This method creates an applicationDBManager object, retrieves all departments in the database, and close the connection to the database
			@parameters:
				args[]: String array 
			@returns:
	*/
	public static void main(String[] args)
	{	
		System.out.println("TEsting");
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
			//ResultSet res=conn.doSelect(fields, tables, whereClause);
		    ResultSet res=conn.doSelect(fields, tables);
		
			//Iterate over the ResulSet containing all departments in the database, and count how many tuples were retrieved
			int count=0;
			while (res.next())
			{
				count++;
				System.out.println(res.getString(1));
				
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