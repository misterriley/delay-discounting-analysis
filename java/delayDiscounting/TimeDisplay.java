/**
 * 
 */
package delayDiscounting;

/**
 * @author Ringo
 * 
 */
public class TimeDisplay
{
	public static String getTimeDisplay(long p_numMillis)
	{
		long numSeconds = p_numMillis / 1000;
		p_numMillis %= 1000;

		long numMinutes = numSeconds / 60;
		numSeconds %= 60;

		long numHours = numMinutes / 60;
		numMinutes %= 60;

		final long numDays = numHours / 24;
		numHours %= 24;

		String ret = "";

		int itemsDisplayed = 0;
		if(numDays > 0)
		{
			ret += (numDays + " day" + (numDays != 1 ? "s " : " "));
			itemsDisplayed++;
		}

		if(itemsDisplayed == 1 || numHours > 0)
		{
			ret += (numHours + " hour" + (numHours != 1 ? "s " : " "));
			itemsDisplayed++;
		}

		if(itemsDisplayed == 1 || (numMinutes > 0 && itemsDisplayed == 0))
		{
			ret += (numMinutes + " minute" + (numMinutes != 1 ? "s " : " "));
			itemsDisplayed++;
		}

		if(itemsDisplayed == 1 || itemsDisplayed == 0)
		{
			ret += (numSeconds + " second" + (numSeconds != 1 ? "s " : " "));
			itemsDisplayed++;
		}

		return ret.trim();
	}
}
