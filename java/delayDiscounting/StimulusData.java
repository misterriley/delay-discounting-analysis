/**
 * 
 */
package delayDiscounting;

/**
 * @author Ringo
 * 
 */
public class StimulusData
{
	private String	m_ID;
	private double	m_choice1;
	private int		m_choice1Delay;
	private double	m_choice2;
	private int		m_choice2Delay;
	private double	m_k;

	public StimulusData(final String p_ID, final double p_choice1,
		final int p_choice1Delay, final double p_choice2,
		final int p_choice2Delay, final double p_k)
	{
		setID(p_ID);
		setChoice1(p_choice1);
		setChoice1Delay(p_choice1Delay);
		setChoice2(p_choice2);
		setChoice2Delay(p_choice2Delay);
		setK(p_k);
	}

	public double getChoice1()
	{
		return m_choice1;
	}

	public int getChoice1Delay()
	{
		return m_choice1Delay;
	}

	public double getChoice2()
	{
		return m_choice2;
	}

	public int getChoice2Delay()
	{
		return m_choice2Delay;
	}

	public String getID()
	{
		return m_ID;
	}

	public double getK()
	{
		return m_k;
	}

	public void setChoice1(final double choice1)
	{
		m_choice1 = choice1;
	}

	public void setChoice1Delay(final int choice1Delay)
	{
		m_choice1Delay = choice1Delay;
	}

	public void setChoice2(final double choice2)
	{
		m_choice2 = choice2;
	}

	public void setChoice2Delay(final int choice2Delay)
	{
		m_choice2Delay = choice2Delay;
	}

	public void setID(final String iD)
	{
		m_ID = iD;
	}

	public void setK(final double k)
	{
		m_k = k;
	}
}