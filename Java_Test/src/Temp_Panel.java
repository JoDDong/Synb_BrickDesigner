import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class Temp_Panel extends JPanel{
	BufferedImage img;
	
	public void paint(Graphics g)
	{
		g.drawImage(img,0,0,null);
		
	}
	
	public Temp_Panel()
	{
		try
		{
			String filename="C:/Users/Administrator/Pictures/Debut/Untitled 20.jpg";
			img = ImageIO.read(new File(filename));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public Dimension getPreferredSize()
	{
		if(img == null){
			return new Dimension(100,100);
		}else{
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
}
