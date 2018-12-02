import java.util.*;
import javax.swing.*;
public class Closer implements Runnable
{
	private JButton firstBtn = null;
	private JButton secondBtn = null;
	private ImageIcon tempImg = null;
	public Closer(JButton fBtn, JButton sBtn, ImageIcon cImage)
	{
		firstBtn = fBtn;
		secondBtn = sBtn;
		tempImg = cImage;
	}
	public Closer()
	{
	}
	public void run()
	{
		try
		{
			Thread.sleep(300);

					firstBtn.setIcon(tempImg);
					secondBtn.setIcon(tempImg);
		}
		catch(Exception e)
		{
			System.out.println("ThreadError : " + e.getMessage());
		}
	}

}