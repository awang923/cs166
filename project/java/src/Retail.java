/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

   // reference to physical database connection.
   private Connection _connection = null;
    private static String userID = "";
   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return name;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

   public static int[] checkUserType(Retail esql){
      try{
         int[] ans = new int[2];
         System.out.print("\tEnter user ID: ");
         int userID = Integer.parseInt(in.readLine());
         String query = String.format("SELECT U.type FROM Users U WHERE U.userID = '%d' AND U.type = 'manager'", userID);
         String query2 = String.format("SELECT U.type FROM Users U WHERE U.userID = '%d' AND U.type = 'admin'", userID);
         int managerNum = esql.executeQuery(query);
         int adminNum = esql.executeQuery(query2);
         int storeID = 0;
         if (adminNum > 0) {
            System.out.println("\tUser is an admin");
            System.out.print("\tEnter store ID: ");
            storeID = Integer.parseInt(in.readLine());
            ans[0] = userID;
            ans[1] = storeID;
            return ans;
         }
         else if (managerNum > 0) {
            System.out.println("\tUser is a manager");
            storeID = checkStoreID(esql, userID);
            if(storeID == 0) {
              System.out.println("\tInvalid storeID");
            }
            ans[0] = userID;
            ans[1] = storeID;
            return ans;
         }
         return new int[] {0,0};
      }catch(Exception e){
         System.out.println("\tUser is not an admin or manager.");
         return new int[] {0,0};
      }
    }
 
   public static int checkStoreID(Retail esql, int userID){
       try{
         System.out.print("\tEnter store ID: ");
         int storeID = Integer.parseInt(in.readLine());
         String query = String.format("SELECT S.storeID FROM Store S WHERE S.storeID = '%d' AND S.managerID = '%d'",storeID, userID);
         int num = esql.executeQuery(query);
         if (num > 0){
           return storeID;
         }
         return 0;
       }catch(Exception e){
         System.out.println("\tUserID and storeID doesn't match.");
         return 0;
       }
   } 

// Rest of the functions definition go in here

   public static void viewStores(Retail esql) {
      try {
         System.out.print("\tEnter userID: ");
         int userID = Integer.parseInt(in.readLine());
         String query = String.format("SELECT S.storeId, S.name, calculate_distance(S.latitude, S.longitude, U.latitude, U.longitude) as dist FROM Users U, Store S WHERE U.userId = '%d' AND calculate_distance(S.latitude, S.longitude, U.latitude, U.longitude) < 30", userID);
         esql.executeQueryAndPrintResult(query);
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void viewProducts(Retail esql) {
      try{
         System.out.print("\tEnter storeID: ");
         int storeID = Integer.parseInt(in.readLine());
         String query = String.format("SELECT * FROM PRODUCT WHERE storeID = ");
         query += storeID;
         esql.executeQueryAndPrintResult(query);
         int rowCount = esql.executeQuery(query);
         System.out.println ("total row(s): " + rowCount);
      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void placeOrder(Retail esql) {
      try {
         System.out.print("\tEnter product name: ");
         String productName = in.readLine();
         System.out.print("\tEnter storeId: ");
         String storeId = in.readLine();
         System.out.print("\nEnter number of units: ");
         Integer numberOfUnits = in.read();
         boolean correctStore = true;
            //check store validity
         String query1 = String.format("SELECT * FROM Store where storeID = %s", storeId);
         int result = esql.executeQuery(query1);
         if(result == 0){
            System.out.println("Store not found");
         }
         //check distance
         // String query2 = String.format("SELECT latitude, longitude FROM Store WHERE storeID = %s", storeId);
         // List<List<String>> stores = esql.executeQueryAndReturnResult(query2);
         // double distance = esql.calculateDistance(euser, esql.getUserLongitude(), stores.get(0), stores.get(1));
         // if(distance >= 30 || distance <= 0){
         //    System.out.println("Store not found in desired radius!");
         // }
         //check stock
         String query3 = String.format("SELECT numberOfUnits FROM Product WHERE storeID = %s and productName = '%s'", storeId, productName);
         List<List<String>> units = esql.executeQueryAndReturnResult(query3);
         if(units.size() != 0){
            System.out.println("Item out of stock");
         }else if(numberOfUnits < units.size()){
            System.out.println("Not enough stock of item");
         }else{
            query3 = String.format("INSERT INTO Orders (storeID, productName, unitsOrdered) VALUES (%s, %s, 2)", storeId, productName);
            esql.executeUpdate(query3);
         }
      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void viewRecentOrders(Retail esql) {
      try{
         System.out.print("\tEnter userId: ");
         userID = in.readLine();
         String query = String.format("SELECT * FROM Orders WHERE customerID = %s ORDER BY orderTime DESC", userID);
         List<List<String>> res = esql.executeQueryAndReturnResult(query);
         System.out.println("\nFive of your most recent orders: ");
         System.out.println("Store ID\tStore Name\t\t\tProduct Name\t\t\tNumber of Units\t\tOrder Time");
         for(int i = 0; i < 5; i++) {
            String storeName = esql.executeQueryAndReturnResult(String.format("SELECT name FROM STORE WHERE storeID = '%s'", res.get(i).get(2))).get(0).get(0);
            System.out.println(res.get(i).get(2) + "\t\t" + storeName + "\t" + res.get(i).get(3) + "\t" + res.get(i).get(4) + "\t\t\t" + res.get(i).get(5));
         }
         System.out.println("\n");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void updateProduct(Retail esql) {
      try {
         int[] user = checkUserType(esql);
         int userID = user[0];
         if(userID == 0) {
           System.out.println("\tUser is not an admin or manager");
           return;
         }
         int storeID = user[1];
         if(storeID == 0){
           return;
         }
         System.out.print("\tEnter product name: ");
         String productName = in.readLine();
         System.out.print("\tEnter number of units: ");
         int numberOfUnits = Integer.parseInt(in.readLine());
         System.out.print("\tEnter price per unit: ");
         int pricerPerUnit = Integer.parseInt(in.readLine());

         String query = String.format("UPDATE Product SET numberOfUnits = '%d', pricePerUnit = '%d' WHERE productName = '%s' AND storeID = '%d'", numberOfUnits, pricerPerUnit, productName, storeID);
         esql.executeUpdate(query);

         String insert_query = String.format("INSERT INTO ProductUpdates(managerID, storeID, productName, updatedOn) VALUES ('%d', '%d', '%s', CURRENT_TIMESTAMP)", userID, storeID, productName);
         esql.executeUpdate(insert_query);

         String query2 = String.format("SELECT * FROM Product WHERE storeID = '%d'", storeID);
         esql.executeQueryAndPrintResult(query2);

      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void viewRecentUpdates(Retail esql) {
      try{
         int[] user = checkUserType(esql);
         int userID = user[0];
         if(userID == 0) {
           System.out.println("\tUser is not an admin or manager");
           return;
         }
         String query = String.format("SELECT P.productName, P.storeID, P.managerID, P.updateNumber, P.updatedOn FROM ProductUpdates P  WHERE P.storeID IN ( SELECT S.storeID FROM Store S WHERE S.managerID = %s) ORDER BY P.updatedOn DESC", userID);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         System.out.println("Product Name\n\tStore ID\t\tManager ID\tUpdate Number\tUpdated ON");
         for(int i = 0; i < result.size(); i++) {
            System.out.println(result.get(i).get(0) + "\t" + result.get(i).get(1) + "\t\t" + result.get(i).get(2)  + "\t\t" + result.get(i).get(3)  + "\t\t" + result.get(i).get(4));
         }
         System.out.println("\n");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void viewPopularProducts(Retail esql) {
      try {
         int[] user = checkUserType(esql);
         int userID = user[0];
         if(userID == 0) {
           System.out.println("\tUser is not an admin or manager");
           return;
         }
         int storeID = user[1];
         if(storeID == 0){
           return;
         }
         String query = String.format("SELECT productName, COUNT(*) FROM Orders WHERE storeID = '%d' GROUP BY productName ORDER BY COUNT(*) DESC LIMIT 5", storeID);
         esql.executeQueryAndPrintResult(query);

      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }
   public static void viewPopularCustomers(Retail esql) {
      try {
         int[] user = checkUserType(esql);
         int userID = user[0];
         if(userID == 0) {
           System.out.println("\tUser is not an admin or manager");
           return;
         }
         int storeID = user[1];
         if(storeID == 0){
           return;
         }
         String query = String.format("SELECT customerID, COUNT(*) FROM Orders WHERE storeID = '%d' GROUP BY customerID ORDER BY COUNT(*) DESC LIMIT 5", storeID);
         esql.executeQueryAndPrintResult(query);
      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   public static void placeProductSupplyRequests(Retail esql) {
      try {
         int[] user = checkUserType(esql);
         int userID = user[0];
         if(userID == 0) {
           System.out.println("\tUser is not an admin or manager");
           return;
         }
         int storeID = user[1];
         if(storeID == 0){
           return;
         }
         System.out.print("\nInput Product Name: ");
         String productName = in.readLine();
         System.out.print("\nInput Number of Products Needed: ");
         String numberProductsNeeded = in.readLine();
         System.out.print("\n Input Warehouse ID: ");
         String warehouseID = in.readLine();
         
         String query = String.format("SELECT * FROM Product P WHERE P.productName = '%s'", productName);
         List<List<String>> res = esql.executeQueryAndReturnResult(query);
         if(res.size() <= 0){
            System.out.println("Product is not at given store");
         }
         query = String.format(
            "INSERT INTO ProductSupplyRequests (managerID, warehouseID, storeID, productName, unitsRequested) VALUES (%d, %s, %s, '%s', %s)",
            userID, warehouseID, storeID, productName, numberProductsNeeded
         );
         esql.executeUpdate(query);
      }catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

}//end Retail

