/**
 * 
 */
package types;

/**
 * @author Ringo
 * 
 */
public enum ResponseType
{
	SOONER, LATER, NONE;

	public static int convertToInteger(final ResponseType p_type)
	{
		switch(p_type)
		{
			case LATER:
				return 2;
			case NONE:
				return -1;
			case SOONER:
				return 1;
		}

		throw new IllegalArgumentException("Unexpected response type: "
			+ p_type);
	}
}
