package test4;

public class Card
{
	public int cNumber;
	private int mark;
	private boolean bool;

	public Card()
	{
		cNumber = 0;
		mark = 0 ;
		bool = false;
	}

	public int getcNumber() 	{
		return cNumber;
	}

	public void setcNumber(int num) {
		cNumber = num;
	}

	public int getmark() 	{
		return mark;
	}

	public void setmark(int m) {
		mark = m;
	}

	public boolean getbool()	{
		return bool;
	}

	public void setbool(boolean b) {
		bool = b;
	}
}