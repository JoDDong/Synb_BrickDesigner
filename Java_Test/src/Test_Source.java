import java.awt.*;
import javax.swing.*;

public class Test_Source {
	public static void main(String args[]) {
		JFrame jf = new JFrame("Text Frame");
		jf.setSize(400,500);
		Container ct = jf.getContentPane();
		JButton jb = new JButton("Text");
		jb.setSize(100,100);
		jb.setLocation(new Point(100,100));
		ct.add(jb);
		
		
		jf.setVisible(true);
	}
}