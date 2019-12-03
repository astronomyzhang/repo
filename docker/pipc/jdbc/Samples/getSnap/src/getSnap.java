import java.sql.*;
import java.util.Properties;

public class getSnap {

	public static void main(String[] args) {
		// arguments: java getSnap url2 [mytagsearchpattern]
		// Example: java getSnap mydas mypi sin%
		Connection con = null;
		String url = "jdbc:pisql://" + args[0] + "/Data Source=" + args[1] + "; Integrated Security=SSPI";
		String driver = "com.osisoft.jdbc.Driver";
		PreparedStatement stmt; ResultSet rs;
		Properties plist = new Properties();
		
		//plist.put("LogConsole", "True");  // optionally switch on debug info
		//plist.put("LogLevel", "3");       // 3=Fine		
		String tagp = "sinusoid";
		if (args.length > 2) tagp = args[2];
		try {
		 Class.forName(driver).newInstance();
		 con = DriverManager.getConnection(url, plist);		
		 stmt = con.prepareStatement("SELECT tag, value FROM pisnapshot WHERE tag like ?");
		 
         DatabaseMetaData md = con.getMetaData();
		 System.out.println(md.getDriverName() + " " + md.getDriverVersion()); 
		 System.out.println(md.getDatabaseProductName());
		 System.out.println(md.getDatabaseProductVersion() + "\n");		 
		 
         // bind Parameter representing the Tag name
		 stmt.setString(1, tagp);
		 rs = stmt.executeQuery();

		 while (rs.next()) {
			String value, tag;
			tag = rs.getString(1);
			value = rs.getString(2);
			System.out.println(tag+" "+value);
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

