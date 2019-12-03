import java.sql.*;
import java.util.Properties;

public class getEASnap {

	public static void main(String[] args) {
		// arguments: java getSnap url2 [myElementAttribute]
		// Example: java getSnap mydas myaf NuGreen B-210 (an Element out of the NuGreen sample database)
		Connection con = null;
		String url = "jdbc:pi://" + args[0] + "/Data Source=" + args[1] + "; Integrated Security=SSPI";
		String driver = "com.osisoft.jdbc.Driver";
		PreparedStatement stmt; ResultSet rs;
		Properties plist = new Properties();
		
		//plist.put("LogConsole", "True");  // optionally switch on debug info
		//plist.put("LogLevel", "3");       // 3=Fine		
		String catalogp = "NuGreen";
		if (args.length > 2) catalogp = args[2];
		String elementp = "B-210";
		if (args.length > 3) elementp = args[3];
		try {
		 Class.forName(driver).newInstance();
		 con = DriverManager.getConnection(url, plist);	
		 // specify AF Database name (catalog), so the query does not need to be catalog specific
		 con.setCatalog(catalogp);
		 
		 stmt = con.prepareStatement("SELECT ea.Name, s.ValueStr " +
	                "FROM [Asset].[ElementHierarchy] eh " +
	                "INNER JOIN [Asset].[ElementAttribute] ea ON ea.ElementID = eh.ElementID " +
	                "INNER JOIN [Data].[Snapshot] s ON s.ElementAttributeID = ea.ID " +
	                "WHERE eh.Name = ? " +
	                "ORDER BY ea.Name " +
	                "OPTION (FORCE ORDER, EMBED ERRORS)");
		 
		 DatabaseMetaData md = con.getMetaData();
		 System.out.println(md.getDriverName() + " " + md.getDriverVersion()); 
		 System.out.println(md.getDatabaseProductName());
		 System.out.println(md.getDatabaseProductVersion() + "\n");	
		 
         // bind Parameter representing the Element name
		 stmt.setString(1, elementp);
		 rs = stmt.executeQuery();
		 
		 while (rs.next()) {
			String value, element;
			element = rs.getString(1);
			value = rs.getString(2);
			System.out.format("%20s  %s%n", element, value);
		 }
		 rs.close();
		 stmt.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (con != null) {
				try { con.close (); }
				catch (SQLException e) {e.printStackTrace();}
			}
		}	
	}
}

