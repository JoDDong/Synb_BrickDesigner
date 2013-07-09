import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.sqlite.SQLiteConfig;
import org.xml.sax.SAXException;

public class DBManege {

	/******* SQLlite 쓸 떄 ***********/
	private Connection connection;
	private String dbName;
	private boolean isOpened = false;
	private Statement stmt;
	private long starttime, endtime;
	private final static String QUERY_SELECT_BY_NAME = "SELECT * FROM media WHERE FilePath=?;";
	private final static String QUERY_SELECT_BY_NAME_HASHCODE = "SELECT Thumbnail FROM media WHERE FilePath=?;";
	public final static String DATABASE = "D:/Temp/TestSQLDB.db";
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DBManege() {
		starttime = System.currentTimeMillis();
	}

	public boolean open() {
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

	public void MakeTable() {
		try {
			stmt = (Statement) connection.createStatement();

			System.out.println("테이블을 생성합니다.");

			String c_qu = "CREATE TABLE IF NOT EXISTS part( part_id INT PRIMARY KEY,part_name LONGTEXT default null,part_short_name LONGTEXT default null,part_short_desc LONGTEXT default null,part_type LONGTEXT default null,"
					+ "part_status LONGTEXT default null,part_result LONGTEXT default null,part_nickname LONGTEXT default null,part_rating INT,part_url LONGTEXT default null,"
					+ "part_entered LONGTEXT default null,part_author LONGTEXT default null,best_quality LONGTEXT default null )";
			stmt.execute(c_qu);
			c_qu = "create table IF NOT EXISTS deep_subparts ( part_id INT not null, subpart_id INT not null, part_sub_num INT not null,  PRIMARY KEY(part_id, subpart_id, part_sub_num ), "
					+ "FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (subpart_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS sequences ( part_id INT NOT NULL, seq_data LONGTEXT default null, PRIMARY KEY( part_id ), FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS specified_subparts ( part_id INT not null, subpart_id INT not null, part_sub_num INT not null, PRIMARY KEY( part_id, subpart_id, part_sub_num ), "
					+ "FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (subpart_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS specified_subscars ( part_id INT not null, part_scar_num INT not null , scar_id INT not null, scar_type LONGTEXT default null, scar_name LONGTEXT default null, "
					+ "scar_nickname LONGTEXT default null, scar_comments LONGTEXT default null, scar_seq LONGTEXT default null, PRIMARY KEY( part_id, part_scar_num ), "
					+ "FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS categories ( part_id INT not null, category VARCHAR(200) NOT NULL, PRIMARY KEY( part_id, category ), FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS features ( part_id INT not null, feature_id INT not null, feature_title LONGTEXT default null, feature_type LONGTEXT default null, feature_direction LONGTEXT default null, "
					+ "feature_startpos LONG default null, feature_endpos INT default null, PRIMARY KEY( part_id, feature_id ), FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS parameterz ( part_id INT not null, par_name LONGTEXT default null, par_value LONGTEXT default null, par_units LONGTEXT default null, par_url LONGTEXT default null, "
					+ "par_id INT not null, par_m_data LONGTEXT default null, par_user_id LONG default null, par_user_name LONGTEXT default null, PRIMARY KEY( part_id, par_id ), "
					+ "FOREIGN KEY (part_id) REFERENCES part (part_id) ON DELETE CASCADE ON UPDATE CASCADE )";
			stmt.execute(c_qu);
			c_qu = "CREATE TABLE IF NOT EXISTS userbricks ( part_name VARCHAR(250) not null, part_type LONGTEXT default null, part_desc LONGTEXT default null, part_seq LONGTEXT default null, PRIMARY KEY(part_name) )";
			stmt.execute(c_qu);

			stmt.close();

			System.out.println("테이블이 생성되었습니다.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void InsertTable_DefaultData() {
		FileReader fr;
		BufferedReader br;
		String strline = null;
		String temp_Query = null;
		Pattern pten = Pattern.compile("\"" + "*" + "\"");// [\\w\\W]
		Matcher match;
		String[] str_Part_info;
		String[] temp;
		PreparedStatement pstmt = null;

		temp_Query = "insert or replace into part (part_id,part_name,part_short_desc,part_type) values (?,?,?,?);";
		try {
			fr = new FileReader("D:/Temp/All_Parts.txt");
			br = new BufferedReader(fr);
			try {
				System.out.println("읽기 시작");
				try {
					connection.setAutoCommit(false);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				while ((strline = br.readLine()) != null) {
					if (strline.startsWith(">")) {
						str_Part_info = null;
						str_Part_info = strline.split(" ");
						// System.out.println("Insert Part_Name" +
						// str_Part_info[0]);
						// part_short_desc가 빠져있다, 3번째 들어가야함
						temp = pten.split(strline);
						try {
							pstmt = connection.prepareStatement(temp_Query);
							pstmt.setString(1, str_Part_info[2]);
							pstmt.setString(2,
									str_Part_info[0].replace(">", ""));
							pstmt.setString(3, temp[1]);
							pstmt.setString(4, str_Part_info[3]); // 이게 4번째로
																	// 들어가야함
							pstmt.executeUpdate();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				try {
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// PreparedStatement 종료
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("읽기 끝");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void InsertTable_all() throws SAXException,ParserConfigurationException {

		int check_num = 0;
		Document document;
		String Query = "SELECT part_name FROM part";
		Element root;
		ResultSet rs;
		SAXBuilder builder;
		builder = new SAXBuilder();
		builder.setIgnoringElementContentWhitespace(true);
		PreparedStatement parts_pstmt = null;
		PreparedStatement deep_subparts_pstmt = null;
		PreparedStatement specified_subparts_pstmt = null;
		PreparedStatement specified_subscars_pstmt = null;
		PreparedStatement sequences_pstmt = null;
		PreparedStatement features_pstmt = null;
		PreparedStatement parameterz_pstmt = null;
		PreparedStatement categories_pstmt = null;
		String part_query = "UPDATE part set part_short_desc = ?, part_short_name = ?, part_status = ?, "
				+ "part_result = ? , part_nickname = ?, part_rating = ?, part_url = ?, part_entered = ?, "
				+ "part_author = ? where part_name = ?";
		String deep_subparts_query = "insert into deep_subparts (part_id, subpart_id, part_sub_num) values (?,?,?)";
		String specified_subparts_query = "insert into specified_subparts (part_id, subpart_id, part_sub_num ) values (?,?,?)";
		String specified_subscars_query = "insert into specified_subscars (part_id, part_scar_num , scar_id, scar_type, scar_name, scar_nickname, scar_comments, scar_seq) values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?)";
		String sequences_query = "insert into sequences (part_id, seq_data) values (?,?)";
		String features_query = "insert into features (part_id, feature_id, feature_title, feature_type, feature_direction, feature_startpos, feature_endpos) values "
				+ "(?, ?, ?, ?, ?, ?, ?)";
		String parameterz_query = "insert into parameterz (part_id, par_name, par_value, par_units, par_url, par_id, par_m_data, par_user_id, par_user_name ) values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
		String categories_query = "insert into categories (part_id, category) values (?,?)";
		try {
			parts_pstmt = connection.prepareStatement(part_query);
			deep_subparts_pstmt = connection
					.prepareStatement(deep_subparts_query);
			specified_subparts_pstmt = connection
					.prepareStatement(specified_subparts_query);
			specified_subscars_pstmt = connection
					.prepareStatement(specified_subscars_query);
			sequences_pstmt = connection.prepareStatement(sequences_query);
			features_pstmt = connection.prepareStatement(features_query);
			parameterz_pstmt = connection.prepareStatement(parameterz_query);
			categories_pstmt = connection.prepareStatement(categories_query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			Statement checkST = connection.createStatement();
			
			rs = checkST.executeQuery(Query);
			connection.setAutoCommit(false);
			while(rs.next()){
				if(rs.getString(1).equals("BBa_Y00073"))
				break;
			}
			while (rs.next()) {
				check_num++;
				int deep_part_num = 1, specified_subpart_num = 1, specified_subscars_num = 1;
				String str_current_id = null;
				System.out.println(rs.getString(1) + "  : XML 파싱 하는 중");
				try {
					// part TABLE 업데이트문

					try {
						document = builder
								.build("http://parts.igem.org/xml/part."
										+ rs.getString(1));
						root = document.getRootElement();

						List<Element> root_element = root.getChildren(); // part
																			// insert
						if (!root_element.isEmpty()) {
							for (Element element : root_element) {
								List<Element> root_sub_element = element
										.getChildren();
								if (!root_sub_element.isEmpty()) {
									for (Element elements : root_sub_element) {
										if (elements.getContentSize() != 1) {

											str_current_id = elements.getChild(
													"part_id").getText();
/*
											parts_pstmt.setString(
													1,
													elements.getChild(
															"part_short_desc")
															.getText());
											parts_pstmt.setString(
													2,
													elements.getChild(
															"part_short_name")
															.getText());

											parts_pstmt.setString(3, elements
													.getChild("release_status")
													.getText());

											parts_pstmt.setString(4, elements
													.getChild("part_results")
													.getText());

											parts_pstmt.setString(5, elements
													.getChild("part_nickname")
													.getText());
											if (!elements
													.getChild("part_rating")
													.getText().equals("null")) {
												parts_pstmt.setString(
														6,
														elements.getChild(
																"part_rating")
																.getText());
											} else {
												parts_pstmt
														.setString(6, "null");
											}
											parts_pstmt.setString(7, elements
													.getChild("part_url")
													.getText());
											parts_pstmt.setString(8, elements
													.getChild("part_entered")
													.getText());

											parts_pstmt.setString(9, elements
													.getChild("part_author")
													.getText());

											parts_pstmt.setString(10,
													rs.getString(1));
											parts_pstmt.executeUpdate();
											parts_pstmt.clearParameters();

											Element deep_subparts_element = elements // deep_subparts
																						// insert
													.getChild("deep_subparts");
											List<Element> deep_subparts_elementlist = deep_subparts_element
													.getChildren();

											if (!deep_subparts_elementlist
													.isEmpty()) {
												for (Element deep_element : deep_subparts_elementlist) {
													deep_subparts_pstmt
															.setString(1,
																	str_current_id);
													deep_subparts_pstmt
															.setString(
																	2,
																	deep_element
																			.getChild(
																					"part_id")
																			.getText());
													deep_subparts_pstmt.setInt(
															3, deep_part_num);
													deep_part_num++;
													deep_subparts_pstmt
															.executeUpdate();
													deep_subparts_pstmt
															.clearParameters();
												}
											}
											// parts_pstmt.close();
											Element specified_subparts_element = elements // specufued_subparts
																							// insert
													.getChild("specified_subparts");
											List<Element> specified_subparts_elementlist = specified_subparts_element
													.getChildren();
											if (!specified_subparts_elementlist
													.isEmpty()) {

												for (Element specifiedp_element : specified_subparts_elementlist) {
													specified_subparts_pstmt
															.setString(1,
																	str_current_id);
													specified_subparts_pstmt
															.setString(
																	2,
																	specifiedp_element
																			.getChild(
																					"part_id")
																			.getText());
													specified_subparts_pstmt
															.setInt(3,
																	specified_subpart_num);
													specified_subpart_num++;
													specified_subparts_pstmt
															.executeUpdate();
													specified_subparts_pstmt
															.clearParameters();
												}
											}

											Element specified_subscars_element = elements // specified_subscar
																							// insert
													.getChild("specified_subscars");
											List<Element> specified_subscars_elementlist = specified_subscars_element
													.getChildren("scar");
											if (!specified_subscars_elementlist
													.isEmpty()) {

												for (Element specifieds_element : specified_subscars_elementlist) {
													specified_subscars_pstmt
															.setString(1,
																	str_current_id);
													specified_subscars_pstmt
															.setInt(2,
																	specified_subscars_num);
													specified_subscars_pstmt
															.setString(
																	3,
																	specifieds_element
																			.getChild(
																					"scar_id")
																			.getText());
													specified_subscars_pstmt
															.setString(
																	4,
																	specifieds_element
																			.getChild(
																					"scar_type")
																			.getText());
													specified_subscars_pstmt
															.setString(
																	5,
																	specifieds_element
																			.getChild(
																					"scar_name")
																			.getText());
													specified_subscars_pstmt
															.setString(
																	6,
																	specifieds_element
																			.getChild(
																					"scar_nickname")
																			.getText());
													specified_subscars_pstmt
															.setString(
																	7,
																	specifieds_element
																			.getChild(
																					"scar_comments")
																			.getText());
													specified_subscars_pstmt
															.setString(
																	8,
																	specifieds_element
																			.getChild(
																					"scar_sequence")
																			.getText());
													specified_subscars_num++;
													specified_subscars_pstmt
															.executeUpdate();
													specified_subscars_pstmt
															.clearParameters();
												}
											}

											Element sequences_element = elements // sequence
																					// insert
													.getChild("sequences");

											sequences_pstmt.setString(1,
													str_current_id);
											sequences_pstmt.setString(
													2,
													sequences_element.getChild(
															"seq_data")
															.getText());
											sequences_pstmt.executeUpdate();
											sequences_pstmt.clearParameters();

											Element features_element = elements
													.getChild("features");
											List<Element> features_elementlist = features_element
													.getChildren();
											if (!features_elementlist.isEmpty()) {

												for (Element feature_element : features_elementlist) {
													features_pstmt.setString(1,
															str_current_id);
													features_pstmt
															.setString(
																	2,
																	feature_element
																			.getChild(
																					"id")
																			.getText());
													features_pstmt
															.setString(
																	3,
																	feature_element
																			.getChild(
																					"title")
																			.getText());
													features_pstmt
															.setString(
																	4,
																	feature_element
																			.getChild(
																					"type")
																			.getText());
													features_pstmt
															.setString(
																	5,
																	feature_element
																			.getChild(
																					"direction")
																			.getText());
													features_pstmt
															.setString(
																	6,
																	feature_element
																			.getChild(
																					"startpos")
																			.getText());
													features_pstmt
															.setString(
																	7,
																	feature_element
																			.getChild(
																					"endpos")
																			.getText());
													features_pstmt.executeUpdate();
													features_pstmt
															.clearParameters();
												}
											}
											// parameters은 현재 자료가 들어있지 않다 있는 것도
											// 있으므로
											// 너야함

											Element parameter_element = elements
													.getChild("parameters");
											List<Element> parameter_elementlist = parameter_element
													.getChildren();
											if (!parameter_elementlist
													.isEmpty()) {

												for (Element parameterz_element : parameter_elementlist) {
													parameterz_pstmt.setString(
															1, str_current_id);
													parameterz_pstmt
															.setString(
																	2,
																	parameterz_element
																			.getChild(
																					"name")
																			.getText());
													parameterz_pstmt
															.setString(
																	3,
																	parameterz_element
																			.getChild(
																					"value")
																			.getText());
													parameterz_pstmt
															.setString(
																	4,
																	parameterz_element
																			.getChild(
																					"units")
																			.getText());
													parameterz_pstmt
															.setString(
																	5,
																	parameterz_element
																			.getChild(
																					"url")
																			.getText());
													parameterz_pstmt
															.setString(
																	6,
																	parameterz_element
																			.getChild(
																					"id")
																			.getText());
													parameterz_pstmt
															.setString(
																	7,
																	parameterz_element
																			.getChild(
																					"m_date")
																			.getText());
													parameterz_pstmt
															.setString(
																	8,
																	parameterz_element
																			.getChild(
																					"user_id")
																			.getText());
													parameterz_pstmt
															.setString(
																	9,
																	parameterz_element
																			.getChild(
																					"user_name")
																			.getText());
													parameterz_pstmt.executeUpdate();
													parameterz_pstmt
															.clearParameters();
												}
											}
											*/
											
											List<Element> categories_elementlist = elements // categories
																							// insert
													.getChildren("categories");
											if (!categories_elementlist.isEmpty()) {
												for (Element category_element : categories_elementlist) {
													List<Element> categorie_element = category_element.getChildren();
													if(!categorie_element.isEmpty()){
														for(Element cate_element : categorie_element){
															categories_pstmt.setString(
																	1, str_current_id);
															categories_pstmt.setString(
																	2, cate_element.getText());
															categories_pstmt.executeUpdate();
															categories_pstmt.clearParameters();
														}
														
													}
													
												}
											}
											
										}
									}
								}
							}
						}
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				document = null;
				root = null;
				if (check_num % 1000 == 0) {
					check_num = 0;
					System.out.println("DB올림");
					connection.commit();
				}
			}
			parts_pstmt.close();
			deep_subparts_pstmt.close();
			specified_subparts_pstmt.close();
			specified_subscars_pstmt.close();
			sequences_pstmt.close();
			features_pstmt.close();
			parameterz_pstmt.close();
			categories_pstmt.close();

			endtime = System.currentTimeMillis();
			System.out.println("총 걸린 시간 :" + (endtime - starttime) / 1000.0f
					+ "초");
			connection.setAutoCommit(true);
			checkST.close();
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void Check_table(){
		String query_ = "SELECT * FROM part";
		ResultSet rs = null;
		Statement checkST;
		try {
			checkST = connection.createStatement();
			rs = checkST.executeQuery(query_);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			while(rs.next()){
				
				System.out.println(rs.getString("part_url"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
