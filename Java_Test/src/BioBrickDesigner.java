import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class BioBrickDesigner implements ActionListener {
	JPanel UDPane;
	JPanel UPane;
	JScrollPane Psimul;
	JList Ilist;
	JPanel DPane;
	JPanel InfoPane;

	private int changecode = 0;

	/**
	 * Create the application.
	 */
	DBManege DBD;

	public BioBrickDesigner() {
		initialize();
	}

	private JFrame frame;
	ImageIcon icon;
	ImageIcon icon1;
	final static int size = 1024;
	private JButton DownBT;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BioBrickDesigner window = new BioBrickDesigner();
					window.frame.setState(Frame.NORMAL);
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		frame = new JFrame();
		frame.setBounds(100, 100, 1024, 692);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.getContentPane().setLayout(null);

		UDPane = new JPanel();
		this.UDPane.setBounds(0, 0, 730, 655);
		frame.getContentPane().add(UDPane);
		UDPane.setLayout(new BoxLayout(UDPane, BoxLayout.X_AXIS));

		UPane = new JPanel();
		this.UDPane.add(this.UPane);
		FlowLayout flowLayout = (FlowLayout) UPane.getLayout();
		flowLayout.setAlignment(FlowLayout.LEADING);

		JScrollPane Psimul_1 = new JScrollPane();
		UPane.add(Psimul_1);

		Ilist = new JList();
		UPane.add(Ilist);

		DPane = new JPanel() {
			public void paintComponent(Graphics g) {
				if (changecode == 0) {
					g.drawImage(icon.getImage(), 0, 0, 200, 200, this);
				} else {
					g.drawImage(icon1.getImage(), 0, 0, 200, 200, this);
				}
			}
		};
		this.UDPane.add(this.DPane);
		DPane.setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 5));

		InfoPane = new JPanel();
		this.InfoPane.setBounds(731, 0, 277, 654);
		this.frame.getContentPane().add(this.InfoPane);
		FlowLayout flowLayout_1 = (FlowLayout) this.InfoPane.getLayout();
		flowLayout_1.setVgap(10);
		flowLayout_1.setHgap(10);
		InfoPane.setBorder(new EmptyBorder(0, 4, 4, 4));

		DownBT = new JButton("Down");
		this.DownBT.setPreferredSize(new Dimension(100, 36));
		this.DownBT.setMargin(new Insets(2, 20, 2, 20));
		this.DownBT.setForeground(new Color(0, 0, 0));
		this.DownBT.setMaximumSize(new Dimension(100, 100));
		this.DownBT.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		this.DownBT.setSize(new Dimension(35, 29));
		DownBT.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		DownBT.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
		DownBT.addActionListener((ActionListener) this);
		InfoPane.add(DownBT);

		icon = new ImageIcon(
				"C:/Users/Administrator/Pictures/Debut/Untitled 20.jpg");
		icon1 = new ImageIcon(
				"C:/Users/Administrator/Pictures/Debut/Untitled 16.jpg");
		Temp_Panel temp = new Temp_Panel();
		InfoPane.add(temp);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// DB매니저 객체 생성
			// DBD = new DBManege();
			// DBD.open();
			// 다운로드 호출
			/*
			  if(DBD != null) { DBD.MakeTable();
			  	DBD.InsertTable_DefaultData(); }
			  	DBD.Check_table();
			  DBD.InsertTable_all();
			 */

			String url = "http://parts.igem.org/fasta/parts/All_Parts";
			String downDir = "D:/Temp";
			// fileUrlDownload(url, downDir);

			changecode = (changecode == 1) ? 0 : 1;
			DPane.repaint();

			
			DB_connect DC = new DB_connect();
			
			DC.open_part();
			ResultSet rs = DC.get_table_rs("BBa_A340620", "deep_subparts");
			ResultSetMetaData rsmd = rs.getMetaData();
			
			while(rs.next()){
				for(int i = 0; i < rsmd.getColumnCount(); i++)
				System.out.println(rs.getString(i+1));
			}
			
		} catch (Exception exc) {

		}
	}

	public static void fileUrlReadAndDownload(String fileAddress,
			String localFileName, String downloadDir) {
		OutputStream outStream = null;
		URLConnection uCon = null;
		InputStream is = null;
		try {
			System.out.println("-------Download Start------");
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			Url = new URL(fileAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(
					downloadDir + "\\" + localFileName + ".txt"));
			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}
			System.out.println("Download Successfully.");
			System.out.println("File name : " + localFileName);
			System.out.println("of bytes  : " + byteWritten);
			System.out.println("-------Download End--------");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param fileAddress
	 * @param downloadDir
	 */
	public static void fileUrlDownload(String fileAddress, String downloadDir) {
		int slashIndex = fileAddress.lastIndexOf('/'); // http:까지 글자수 5
		int periodIndex = fileAddress.lastIndexOf('.'); // 12
		// 파일 어드레스에서 마지막에 있는 파일이름을 취득
		String fileName = fileAddress.substring(slashIndex + 1); // 주소가 정상적일 경우에
																	// 다운 시작
		if (periodIndex >= 1 && slashIndex >= 0
				&& slashIndex < fileAddress.length() - 1) {
			fileUrlReadAndDownload(fileAddress, fileName, downloadDir);
		} else {
			System.err.println("path or file name NG.");
		}
	}

}
