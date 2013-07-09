import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;


public class DB_connect {
	private Connection connection;
	private String dbName;
	private boolean isOpened = false;
	private Statement stmt;
	public final static String DATABASE = "D:/Temp/TestSQLDB.db";
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean open_part() {
		try {
			SQLiteConfig config = new SQLiteConfig();
			config.setReadOnly(true);
			this.connection = DriverManager.getConnection("jdbc:sqlite:"
					+ DATABASE);
			System.out.println("연결 성공");

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		isOpened = true;
		return false;
	}

	public boolean close() {
		if (this.isOpened = false) {
			return true;
		}
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//Part_id 받아오는 부분
	private String get_part_id(String part_name, String table_name){
		String table_connect_query = "SELECT part_id from " + table_name + " where part_name = '" + part_name + "'";
		String part_id;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery(table_connect_query);
			rs.next();
			part_id =  rs.getString(1);
			return part_id;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("not found parts");
			return null;
		}
	}
	
	// 테이블 선택하는 부분
	private void select_table(String part_name, String table_name){
		String part_id = null;
		String part_table_connect_query = "SELECT * from " + table_name + " where part_name = '" + part_name + "'";
		String other_table_connect_query;
		try {
			stmt = connection.createStatement();
			
			if(table_name.equals("parts")){
				stmt.execute(part_table_connect_query);
			}else{
				part_id = get_part_id(part_name, "part");
				other_table_connect_query = "SELECT * from " + table_name + " where part_id = '" + part_id + "'";
				stmt.execute(other_table_connect_query);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("not found parts");
		}
	}
	
	// Result 받기 -> select_table -> get_part_id
	public ResultSet get_table_rs(String part_name, String table_name){
		ResultSet table_rs = null;
		select_table(part_name,table_name);
		try {
			table_rs = stmt.getResultSet();
			return table_rs;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
