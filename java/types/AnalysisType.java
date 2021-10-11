/**
 * 
 */
package types;

/**
 * @author Ringo
 * 
 */
public enum AnalysisType
{
	ALL_ALL, ROUND_ALL, MESSY_ALL, ALL_IMMEDIATE, ALL_DELAYED, ROUND_IMMEDIATE,
	ROUND_DELAYED, MESSY_IMMEDIATE, MESSY_DELAYED;

	@Override
	public String toString()
	{
		switch(this)
		{
			case ALL_ALL:
				return "All";
			case ALL_DELAYED:
				return "Delayed";
			case ALL_IMMEDIATE:
				return "Immediate";
			case MESSY_ALL:
				return "Messy";
			case MESSY_DELAYED:
				return "Messy/Delayed";
			case MESSY_IMMEDIATE:
				return "Messy/Immediate";
			case ROUND_ALL:
				return "Round";
			case ROUND_DELAYED:
				return "Round/Delayed";
			case ROUND_IMMEDIATE:
				return "Round/Immediate";
			default:
				throw new IllegalArgumentException("");
		}
	}
}
