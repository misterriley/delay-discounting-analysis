/**
 *
 */
package delayDiscounting;

import types.AnalysisType;
import types.EasyHardDataType;
import types.KAnalysisValueType;
import types.SubjectInfoType;

/**
 * @author Ringo
 *
 */
public class SubjectOutputHeaderRow extends SubjectOutputRow
{
	@Override
	public String getLLPString(final AnalysisType p_type)
	{
		return "%LL (" + p_type + ")";
	}

	@Override
	public String getOutputString(final AnalysisType p_analysisType, final KAnalysisValueType p_analysisValueType)
	{
		String intro = null;
		switch (p_analysisValueType)
		{
			case K:
				intro = "K";
				break;
			case K_SE:
				intro = "K Std Err";
				break;
			case S:
				intro = "Sensitivity";
				break;
			case S_SE:
				intro = "Sens Std Err";
				break;
			default:
				throw new IllegalArgumentException("Unexpected type: " + p_analysisValueType);
		}

		return intro + " (" + p_analysisType + ")";
	}

	@Override
	public String getOutputString(final EasyHardDataType p_type)
	{
		switch (p_type)
		{
			case EASY_RT_MEAN:
				return "Easy RT Mean";
			case EASY_RT_SD:
				return "Easy RT SD";
			case HARD_RT_MEAN:
				return "Hard RT Mean";
			case HARD_RT_SD:
				return "Hard RT SD";
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public String getOutputString(final SubjectInfoType p_type)
	{
		switch (p_type)
		{
			case AGE:
				return "Age";
			case ATTENTION_PERCENT:
				return "Attention %";
			case DATE:
				return "Date";
			case ORDER:
				return "Order";
			case SOURCE_FILE:
				return "Source File";
			case SUBJECT_ID:
				return "Subject";
			case TIME:
				return "Time";
			case ADHD_TD_GROUP:
				return "ADHD/TD Group";
			case SEX:
				return "Sex";
			case YEAR:
				return "Year";
		}

		throw new RuntimeException("Invalid output type: " + p_type);
	}
}