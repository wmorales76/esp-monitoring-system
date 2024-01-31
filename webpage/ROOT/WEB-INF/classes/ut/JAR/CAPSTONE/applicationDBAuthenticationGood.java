
//This class belongs to the ut.JAR.CPEN410 package
package ut.JAR.CAPSTONE;

//Import the java.sql package for managing the ResulSet objects
import java.sql.* ;

//Import Hash functions
import org.apache.commons.codec.*;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/******
	This class authenticate users using Username and passwords

*/
public class applicationDBAuthenticationGood{

	//myDBConn is an MySQLConnector object for accessing to the database
	private MySQLCompleteConnector myDBConn;
	
	/********
		Default constructor
		It creates a new MySQLConnector object and open a connection to the database
		@parameters:
		
	*/
	public applicationDBAuthenticationGood(){
		//Create the MySQLConnector object
		myDBConn = new MySQLCompleteConnector();
		
		//Open the connection to the database
		myDBConn.doConnection();
	}
	
	
	/*******
		authenticate method
			Authentication method
			@parameters:
			@returns:
				A ResultSet containing the Username and all roles assigned to her.
	*/
/*public ResultSet authenticate(String Username, String Hash, String Role) 
{
    //Declare function variables
    String fields, tables, whereClause, HashVal;
    
    //Define the table where the selection is performed
    tables="users";
    HashVal = HashSha256(Username + Hash);
    whereClause="Username='" +Username +"' and Hash='" + HashVal + "'";
    
    //Define the list fields list to retrieve assigned roles to the user
    fields ="Role";
    
    System.out.println("listing DATA...");
    
    //Return the ResultSet containing all roles assigned to the user
    return myDBConn.doSelectLogIn(fields, tables, whereClause);
}*/

public ResultSet authenticate(String Username, String Hash, String Role) 
{
    //Declare function variables
    String fields, tables, whereClause, HashVal;
    
    //Define the table where the selection is performed
    tables="users";
    HashVal = HashSha256(Username + Hash);
    whereClause="Username='" +Username +"' and Hash='" + HashVal + "'";
    
    //Define the list fields list to retrieve assigned roles to the user
    fields ="Role, User_ID"; // Add User_ID to the fields to select
    
    System.out.println("listing DATA...");
    
    //Return the ResultSet containing all roles assigned to the user
    return myDBConn.doSelectLogIn(fields, tables, whereClause);
}

	
	public ResultSet verifyUser(String Username, String Hash, String currentPage, String previousPage) {
    //Declare function variables
    String fields, tables, whereClause, HashVal;
    
    //Define the table where the selection is performed
    tables="users";
    HashVal = HashSha256(Username + Hash);
    whereClause="Username='" +Username +"' and Hash='" + HashVal + "'";
    
    //Define the list fields list to retrieve assigned roles to the user
    fields ="Username";
    
    System.out.println("listing...");
    
    //Return the ResultSet containing all roles assigned to the user
    return myDBConn.doSelect(fields, tables, whereClause);
}


//Method to register users on the database	
public boolean addUser(String Username, String Hash, String Name, String Phone, String Email, String Role) 
{
    boolean res = false;
    String table, values, HashValue;
    HashValue = HashSha256(Username + Hash);
    table = "users";
    values = "'" + Username + "', '" + HashValue + "', '" + Name + "', '" + Phone + "', '" + Email + "', '" + Role + "'";
    if (!myDBConn.userExists(Username)) {
        res = myDBConn.doInsertUsers(table, values);
    }
    System.out.println("Insertion result: " + res);
    return res;
}


//Method to add device information to the database
public boolean addDeviceInformation(String Building, String Room, String Area, String Purpose, String Device_ID)
{
    boolean res;
    String table, columns, values;
    table = "device_information";
    columns = "Building, Room, Area, Purpose, Device_ID";
    values = "'" + Building + "', '" + Room + "','" + Area + "', '" + Purpose + "', '" + Device_ID + "'";
    res = myDBConn.doInsertInfo(table, columns, values);
    System.out.println("Insertion result: " + res);
    return res;
}

//Upsert - Update + Insert
public boolean Upsert(String Building, String Room, String Area, String Purpose, String Device_ID)
{
    boolean res;
    String table, columns, values, condition, set;
    table = "device_information";
    columns = "Building, Room, Area, Purpose, Device_ID";
    values = "'" + Building + "', '" + Room + "','" + Area + "', '" + Purpose + "', '" + Device_ID + "'";
    condition = "Device_ID = '" + Device_ID + "'";
    set = "Building = '" + Building + "', Room = '" + Room + "', Area = '" + Area + "', Purpose = '" + Purpose + "'";

    // Check if Device_ID exists
    if (myDBConn.recordExists(table, condition)) {
        // If it exists, update the record
        res = myDBConn.doUpdate(set, table, condition);
    } else {
        // If it doesn't exist, insert a new record
        res = myDBConn.doInsertInfo(table, columns, values);
    }

    System.out.println("Operation result: " + res);
    return res;
}




// Method to assign a device to a user// Capstone
/*public boolean assignDevice(String User_ID, String Device_ID)
{
    boolean res;
    String table, values;
    table = "devices";
    values = "'" + User_ID + "', '" + Device_ID + "'";
    res = myDBConn.doInsert(table, values);
    System.out.println("Insertion result: " + res);
    return res;
}*/

/*public boolean assignDevice(int User_ID, String Device_UID, String Device_Name)
{
    boolean res;
    String table, columns, values;
    table = "devices";
    columns = "User_ID, Device_UID, Device_Name";
    values = User_ID + ", '" + Device_UID + "', '" + Device_Name + "'"; // Removed single quotes around User_ID
    res = myDBConn.doInsertDevice(table, columns, values);
    System.out.println("Insertion result: " + res);
    return res;
}*///LATEST

public boolean assignDevice(int User_ID, String Device_UID, String Device_Name)
{
    boolean res = false;
    String table, columns, values;
    table = "devices";
    columns = "User_ID, Device_UID, Device_Name";
    values = User_ID + ", '" + Device_UID + "', '" + Device_Name + "'"; // Removed single quotes around User_ID

    // Check if Device_UID already exists
    String condition = "Device_UID = '" + Device_UID + "'";
    if (!myDBConn.recordExists(table, condition)) { // If the Device_UID does not exist
        res = myDBConn.doInsertDevice(table, columns, values);
        System.out.println("Insertion result: " + res);
    } else {
        System.out.println("Device_UID already exists. No insertion performed.");
    }
    return res;
}


/*public boolean removeDevice(String User_ID, String Device_ID)
{
    boolean res;
    String table = "devices";
    res = myDBConn.doDeleteDevice(table, User_ID, Device_ID);
    System.out.println("Deletion result: " + res);
    return res;
}*/



public boolean removeDevice(String User_ID, String Device_ID)
{
    boolean res = false;
    String devicesTable = "devices";
    String deviceInfoTable = "device_information";
    String deviceMonitorTable = "device_monitor";
    String condition = "Device_ID = '" + Device_ID + "'";

    // Check if Device_ID exists in the device_monitor table
    if (myDBConn.recordExists(deviceMonitorTable, condition)) {
        // If it exists, delete the record from device_monitor
        res = myDBConn.doDeleteDeviceMonitor(deviceMonitorTable, Device_ID);
        System.out.println("Deletion result from device_monitor: " + res);
    }

    // Check if Device_ID exists in the device_information table
    if (myDBConn.recordExists(deviceInfoTable, condition)) {
        // If it exists, delete the record from device_information
        res = myDBConn.doDeleteDeviceInfo(deviceInfoTable, Device_ID);
        System.out.println("Deletion result from device_information: " + res);
    }

    // Check if User_ID and Device_ID exist in the devices table
    condition = "User_ID = '" + User_ID + "' AND Device_ID = '" + Device_ID + "'";
    if (myDBConn.recordExists(devicesTable, condition)) {
        // If they exist, delete the record from devices
        res = myDBConn.doDeleteDevice(devicesTable, User_ID, Device_ID);
        System.out.println("Deletion result from devices: " + res);
    }

    return res;
}


public boolean removeDeviceInformation(String Device_ID)
{
    boolean res;
    String table = "device_information";
    res = myDBConn.doDeleteDeviceInfo(table, Device_ID);
    System.out.println("Deletion result: " + res);
    return res;
}

// Method to show all device monitors.//CAPSTONE
/*ublic ResultSet showDeviceMonitor() {
    String table = "device_monitor";
    String fields = "*";
    String condition = "";
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}*/

public ResultSet showDeviceMonitor() {
    String table = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name";
    String condition = "";
    // Order the results by Monitor_ID
    String orderBy = "DM.Monitor_ID";
    ResultSet result = myDBConn.doShowAllByOrder(fields, table, orderBy);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet showDeviceMonitorNormal(int User_ID, List<String> Device_UIDs) {
    // Convert the list of Device_UIDs to a comma-separated string enclosed in quotes
    String Device_UIDs_String = Device_UIDs.stream()
                                           .map(uid -> "'" + uid + "'")
                                           .collect(Collectors.joining(", "));
    // Define the tables for the query
    String tables = "users u JOIN devices d ON u.User_ID = d.User_ID JOIN device_monitor dm ON d.Device_ID = dm.Device_ID";

    // Select all columns from the joined tables
    String fields = "*";

    // Only select records where Device_UID is in the list of Device_UIDs
    String condition = "d.Device_UID IN (" + Device_UIDs_String + ")"; 

    ResultSet result = myDBConn.doShowByCondition(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}

/*public ResultSet showDeviceMonitorNormalOrdered(String Username) {
    // Define the tables for the query
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";

    // Select all columns from device_monitor
    String fields = "DM.*";

    // Only select records where the username matches
    String condition = "U.Username = '" + Username + "'"; 

    // Order the results by Monitor_ID
    String orderBy = "DM.Monitor_ID";

    ResultSet result = myDBConn.doShowByOrder(fields, tables, condition, orderBy);
    System.out.println("Selection result: " + result);
    return result;
}*/

public ResultSet showDeviceMonitorNormalOrdered(String Username) {
    // Define the tables for the query
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";

    // Select all columns from device_monitor and Device_Name from devices
    String fields = "DM.*, D.Device_Name";

    // Only select records where the username matches
    String condition = "U.Username = '" + Username + "'"; 

    // Order the results by Monitor_ID
    String orderBy = "DM.Monitor_ID";

    ResultSet result = myDBConn.doShowByOrder(fields, tables, condition, orderBy);
    System.out.println("Selection result: " + result);
    return result;
}


public ResultSet showDeviceMonitorAndroid(String Username) {
    String tables;
    String fields = "DM.*"; // Select all columns from device_monitor
    String condition;
    ResultSet result = null;

    try {
        // Get a Statement object
        Statement stmt = myDBConn.getStatement();
        
        // First, check the role of the user
        String roleCheck = "SELECT Role FROM users WHERE Username = '" + Username + "'";
        ResultSet rs = stmt.executeQuery(roleCheck);
        if (rs.next()) {
            String role = rs.getString("Role");
            if (role.equals("Admin")) {
                // If the user is an Admin, select all records from device_monitor
                tables = "device_monitor DM";
                condition = "";
            } else {
                // If the user is not an Admin, select only the records assigned to the user
                tables = "device_monitor DM INNER JOIN devices AD ON DM.Device_ID = AD.Device_ID INNER JOIN users U ON U.Username = AD.Username";
                condition = "U.Username = '" + Username + "'";
            }
            result = myDBConn.doShowByCondition(fields, tables, condition);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    System.out.println("Selection result: " + result);
    return result;
}

// Method to show all device information.//CAPSTONE
public ResultSet showDeviceInformation() {
    String table = "device_information";
    String fields = "*";
    String condition = "";
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet ViewDeviceInformation() {
    String table = "device_information DI LEFT JOIN devices D ON DI.Device_ID = D.Device_ID";
    String fields = "DI.*, D.Device_UID"; // Select all columns from device_information and Device_UID from devices
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}


public ResultSet RemoveInfoFromDevice() {
    String table = "device_information DI LEFT JOIN devices D ON DI.Device_ID = D.Device_ID";
    String fields = "DI.*, D.Device_UID"; // Select all columns from device_information and Device_UID from devices
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show all distinct device monitors..//CAPSTONE
public ResultSet DISTINCTDeviceMonitor() {
    String table = "device_monitor";
    String fields = "Device_ID"; // Select Device_IDs
    ResultSet result = myDBConn.doShowDistinct(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show all distinct device monitors..//CAPSTONE
public ResultSet DISTINCTDeviceID() {
    String table = "devices";
    String fields = "Device_ID, Device_UID"; // Select Device_IDs
    ResultSet result = myDBConn.doShowDistinct(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet DISTINCTDeviceIDNormal(int UserID) {
    String table = "devices";
    String fields = "*"; // Select Device_UIDs
    String condition = "User_ID = " + UserID; // Only select rows where User_ID equals UserID
    ResultSet result = myDBConn.doShowByCondition(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}


public ResultSet DISTINCTDeviceMonitorJOIN() {
    String table = "device_monitor DM LEFT JOIN device_information DI ON DM.Device_ID = DI.Device_ID";
    String fields = "DISTINCT DM.Device_ID"; // Select distinct Device_IDs
    String condition = "DI.Device_ID IS NULL"; // Only select Device_IDs that are not in device_information
    ResultSet result = myDBConn.doShowAllJOIN(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

/*public ResultSet AddInfoToDevice() {
    String table = "device_monitor DM LEFT JOIN device_information DI ON DM.Device_ID = DI.Device_ID LEFT JOIN devices D ON DM.Device_ID = D.Device_ID";
    String fields = "DISTINCT DM.Device_ID, D.Device_UID"; // Select distinct Device_IDs and Device_UIDs
    String condition = "DI.Device_ID IS NULL"; // Only select Device_IDs that are not in device_information
    ResultSet result = myDBConn.doShowAllJOIN(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}*/

public ResultSet AddInfoToDevice() {
    String table = "device_monitor DM LEFT JOIN device_information DI ON DM.Device_ID = DI.Device_ID LEFT JOIN devices D ON DM.Device_ID = D.Device_ID";
    String fields = "DISTINCT DM.Device_ID, D.Device_UID"; // Select distinct Device_IDs and Device_UIDs
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet DeviceUID() {
    String table = "devices D LEFT JOIN device_monitor DM ON D.Device_ID = DM.Device_ID LEFT JOIN device_information DI ON D.Device_ID = DI.Device_ID";
    String fields = "DISTINCT D.Device_ID, D.Device_UID"; // Select Device_UIDs
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}



// Method to show device monitors with 'Low' danger.//CAPSTONE
public ResultSet showDeviceMonitorLow() {
    String table = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name";
    String condition = "Danger = 'Low'";
    ResultSet result = myDBConn.doShowByDanger(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show device monitors with 'Medium' danger.//CAPSTONE
public ResultSet showDeviceMonitorMedium() {
    String table = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name";
    String condition = "Danger = 'Medium'";
    ResultSet result = myDBConn.doShowByDanger(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show device monitors with 'High' danger.//CAPSTONE
public ResultSet showDeviceMonitorHigh() {
    String table = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name";
    String condition = "Danger = 'High'";
    ResultSet result = myDBConn.doShowByDanger(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show device monitors with 'Low' danger for a specific user.
/*public ResultSet showDeviceMonitorLowNormal(String Username) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*"; // Select all columns from device_monitor
    String condition = "U.Username = '" + Username + "' AND DM.Danger = 'Low'";
    ResultSet result = myDBConn.doShowByDanger(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}*/

public ResultSet showDeviceMonitorLowNormal(String Username) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name"; // Select all columns from device_monitor
    String condition = "U.Username = '" + Username + "' AND DM.Danger = 'Low'";

    // Order the results by Monitor_ID
    String orderBy = "DM.Monitor_ID";

    ResultSet result = myDBConn.doShowByDangerOrdered(fields, tables, condition, orderBy);
    System.out.println("Selection result: " + result);
    return result;
}


// Method to show device monitors with 'Medium' danger for a specific user.
public ResultSet showDeviceMonitorMediumNormal(String Username) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name"; // Select all columns from device_monitor
    String condition = "U.Username = '" + Username + "' AND DM.Danger = 'Medium'";
    ResultSet result = myDBConn.doShowByDanger(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show device monitors with 'High' danger for a specific user.
public ResultSet showDeviceMonitorHighNormal(String Username) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*, D.Device_Name"; // Select all columns from device_monitor
    String condition = "U.Username = '" + Username + "' AND DM.Danger = 'High'";
    ResultSet result = myDBConn.doShowByDanger(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}


public ResultSet showUsers() {
    String table = "users";
    String fields = "*";
    String condition = "Role = 'Normal'";
    ResultSet result = myDBConn.doShowByCondition(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show all assigned devices.//CAPSTONE
/*public ResultSet showAssignedDevice() {
    String table = "devices";
    String fields = "*";
    String condition = "";
    ResultSet result = myDBConn.doShowAll(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}*/

public ResultSet showAssignedDevice() {
    String tables = "devices D INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "D.*, U.Username, U.Name";
    ResultSet result = myDBConn.doShowAll(fields, tables);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet showAssignedDevice(int UserID) {
    String tables = "devices D INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "D.*, U.Username, U.User_ID";
    String condition = "D.User_ID = '" + UserID + "'";
    ResultSet result = myDBConn.doShowByCondition(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}



// Method to show all distinct usernames from devices
/*public ResultSet showAssignedDeviceDISTINCT() {
    String table = "devices";
    String fields = "User_ID"; // Select distinct Usernames
    ResultSet result = myDBConn.doShowDistinct(fields, table);
    System.out.println("Selection result: " + result);
    return result;
}*/

// Method to show all distinct usernames from devices
public ResultSet showAssignedDeviceDISTINCT() {
    String tables = "devices D INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DISTINCT U.Username, U.User_ID"; // Select distinct Usernames
    ResultSet result = myDBConn.doShowDistinct(fields, tables);
    System.out.println("Selection result: " + result);
    return result;
}





// Method to get device information by Device_ID
public ResultSet getDeviceInformation(String Device_ID) {
    String table = "device_information";
    String fields = "*";
    String condition = "Device_ID = '" + Device_ID + "'";
    ResultSet result = myDBConn.doShowByCondition(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet getDeviceInformationTest(String Device_ID) {
    // Define the tables for the query
    String tables = "device_information DI INNER JOIN devices D ON DI.Device_ID = D.Device_ID";

    // Select all columns from device_information and Device_UID from devices
    String fields = "DI.*, D.Device_UID";

    // Only select records where the Device_ID matches
    String condition = "DI.Device_ID = '" + Device_ID + "'"; 

    ResultSet result = myDBConn.doShowByCondition(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}

// Method to show device monitors by Device_ID and Danger.//CAPSTONE
public ResultSet showDeviceMonitorSearch(String Device_ID, String Danger) {
    String table = "device_monitor";
    String fields = "*";
    String condition = "Device_ID = '" + Device_ID + "'";
    if (Danger != null && !Danger.isEmpty()) {
        condition += " AND Danger = '" + Danger + "'";
    }
    ResultSet result = myDBConn.doShowDeviceMonitorSearch(fields, table, condition);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet showDeviceMonitorSearchJOIN(String Username, String Device_ID, String Danger) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*"; // Select all columns from device_monitor
    String condition = "U.Username = '" + Username + "' AND DM.Device_ID = '" + Device_ID + "'";
    if (Danger != null && !Danger.isEmpty()) {
        condition += " AND DM.Danger = '" + Danger + "'";
    }
    ResultSet result = myDBConn.doShowDeviceMonitorSearch(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}

/*public ResultSet showDeviceMonitorSearchName(String Username, String Device_Name, String Danger) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    String fields = "DM.*"; // Select all columns from device_monitor
    String condition = "U.Username = '" + Username + "' AND D.Device_Name = '" + Device_Name + "'";
    if (Danger != null && !Danger.isEmpty()) {
        condition += " AND DM.Danger = '" + Danger + "'";
    }
    ResultSet result = myDBConn.doShowDeviceMonitorSearch(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}*/

public ResultSet SearchNormal(String Username, String Device_Name, String Danger) {
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID INNER JOIN users U ON D.User_ID = U.User_ID";
    // Select all columns from device_monitor and Device_ID from devices
    String fields = "DM.*, D.Device_ID"; 
    String condition = "U.Username = '" + Username + "' AND D.Device_Name = '" + Device_Name + "'";
    if (Danger != null && !Danger.isEmpty()) {
        condition += " AND DM.Danger = '" + Danger + "'";
    }
    ResultSet result = myDBConn.doShowDeviceMonitorSearch(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}

public ResultSet SearchAdmin(String Device_Name, String Danger) {
    // Join device_monitor, devices, and device_information tables
    String tables = "device_monitor DM INNER JOIN devices D ON DM.Device_ID = D.Device_ID LEFT JOIN device_information DI ON D.Device_ID = DI.Device_ID";
    // Select all columns from device_monitor, devices, and device_information
    String fields = "DM.*, D.*, DI.*"; 
    String condition = "D.Device_Name = '" + Device_Name + "'";
    if (Danger != null && !Danger.isEmpty()) {
        condition += " AND DM.Danger = '" + Danger + "'";
    }
    ResultSet result = myDBConn.doShowDeviceMonitorSearch(fields, tables, condition);
    System.out.println("Selection result: " + result);
    return result;
}


//Method used to delete a user: //CAPSTONE
/*public boolean deleteUser(String[] usernames)
{
    boolean res;
    String table = "users";
    res = myDBConn.doDeleteUsers(table, usernames);
    System.out.println("Deletion result: " + res);
    return res;
}*/

//Method used to delete a user: //CAPSTONE
public String deleteUser(String[] usernames)
{
    String res;
    String table = "users";
    res = myDBConn.doDeleteUsers(table, usernames);
    System.out.println("Deletion result: " + res);
    return res;
}



	/*********
		HashSha256 method
			Generates a hash value using the sha256 algorithm.
			@parameters: Plain text
			@returns: the hash string based on the plainText
	*/
	private String HashSha256(String plainText)
	{
			String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(plainText); 
			return sha256hex;
	}
	
	
	
	
	/*********
		close method
			Close the connection to the database.
			This method must be called at the end of each page/object that instatiates a applicationDBManager object
			@parameters:
			@returns:
	*/
	public void close()
	{
		//Close the connection
		myDBConn.closeConnection();
	}

}