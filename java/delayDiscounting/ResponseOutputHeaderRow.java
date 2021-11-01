/**
 *
 */
package delayDiscounting;

import types.ResponseOutputDataType;

/**
 * @author Ringo
 *
 */
public class ResponseOutputHeaderRow extends ResponseData
{
	@Override
	public String getData(final ResponseOutputDataType p_type)
	{
		switch (p_type)
		{
			case ADHD_TD_GROUP:
				return "ADHD/TD Group";
			case AGE:
				return "Age";
			case IS_DELAYED:
				return "Delayed?";
			case IS_MESSY:
				return "Messy?";
			case K:
				return "K";
			case OPTION_1:
				return "Option 1";
			case OPTION_1_DELAY:
				return "Option 1 Delay";
			case OPTION_2:
				return "Option 2";
			case OPTION_2_DELAY:
				return "Option 2 Delay";
			case SEX:
				return "Sex";
			case SUBJECT_CHOICE:
				return "Choice";
			case SUBJECT_ID:
				return "ID";
			case YEAR:
				return "Year";
		}

		throw new RuntimeException("Invalid data type: " + p_type);
	}
}
