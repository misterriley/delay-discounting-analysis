/**
 * 
 */
package delayDiscounting;

import types.ResponseOutputDataType;
import types.ResponseType;

/**
 * @author Ringo
 * 
 */
public class ResponseData
{
	private static final double	K_CALC_TOLERANCE	= Settings.getKSanityCheckTolerance();

	private ResponseType		m_responseType;
	private double				m_choice1;
	private int					m_choice1Delay;
	private double				m_choice2;
	private int					m_choice2Delay;
	private boolean				m_isAttentionCheck;
	private boolean				m_isMessyTrial;
	private int					m_timeIndex;
	private double				m_likelihood;
	private double				m_k;
	private SubjectYearData		m_subjectYearData;
	private double				m_rt;

	public ResponseData()
	{
	// exists so that ResponseOutputHeader can extend without
	// needing a bunch of inputs
	}

	public ResponseData(final SubjectYearData p_subjectYearData,
		final double p_choice1, final int p_choice1Delay,
		final double p_choice2, final int p_choice2Delay,
		final ResponseType p_responseType, final boolean p_isAttentionCheck,
		final boolean p_isMessyTrial, final int p_timeIndex, final double p_k,
		final boolean p_checkForKValue, final double p_rt)
	{
		setSubjectYearData(p_subjectYearData);
		setChoice1(p_choice1);
		setChoice1Delay(p_choice1Delay);
		setChoice2(p_choice2);
		setChoice2Delay(p_choice2Delay);
		setResponseType(p_responseType);
		setAttentionCheck(p_isAttentionCheck);
		setMessyTrial(p_isMessyTrial);
		setTimeIndex(p_timeIndex);
		setK(p_k);
		setRT(p_rt);
		final double testK = Functions.calculateBreakEvenKValue(this);
		if(p_checkForKValue && Math.abs(testK - m_k) > K_CALC_TOLERANCE)
		{
			throw new RuntimeException("K Value is wrong : " + testK + " vs. "
				+ m_k);
		}
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

	public String getData(final ResponseOutputDataType p_type)
	{
		switch(p_type)
		{
			case ADHD_TD_GROUP:
				return getSubjectYearData().getSubjectData().getAdhdTdGroup();
			case AGE:
				return String.valueOf(getSubjectYearData().getAge());
			case IS_DELAYED:
				return String.valueOf(isDelayedTrial());
			case IS_MESSY:
				return String.valueOf(isMessyTrial());
			case K:
				return String.valueOf(m_k);
			case OPTION_1:
				return String.valueOf(m_choice1);
			case OPTION_1_DELAY:
				return String.valueOf(m_choice1Delay);
			case OPTION_2:
				return String.valueOf(m_choice2);
			case OPTION_2_DELAY:
				return String.valueOf(m_choice2Delay);
			case SEX:
				return getSubjectYearData().getSubjectData().getSex();
			case SUBJECT_CHOICE:
				return String.valueOf(ResponseType.convertToInteger(getResponseType()));
			case SUBJECT_ID:
				return String.valueOf(getSubjectYearData().getSubjectData().getSubjectID());
			case YEAR:
				return String.valueOf(getSubjectYearData().getYear());
		}

		throw new RuntimeException("Invalid data type: " + p_type);
	}

	public double getK()
	{
		return m_k;
	}

	public double getLikelihood()
	{
		return m_likelihood;
	}

	public ResponseType getResponseType()
	{
		return m_responseType;
	}

	public double getRT()
	{
		return m_rt;
	}

	public SubjectYearData getSubjectYearData()
	{
		return m_subjectYearData;
	}

	public int getTimeIndex()
	{
		return m_timeIndex;
	}

	public boolean isAttentionCheck()
	{
		return m_isAttentionCheck;
	}

	/**
	 * @return
	 */
	public boolean isDelayedTrial()
	{
		return m_choice1Delay > 0;
	}

	public boolean isMessyTrial()
	{
		return m_isMessyTrial;
	}

	public void setAttentionCheck(final boolean isAttentionCheck)
	{
		m_isAttentionCheck = isAttentionCheck;
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

	public void setK(final double p_k)
	{
		m_k = p_k;
	}

	public void setLikelihood(final double likelihood)
	{
		m_likelihood = likelihood;
	}

	public void setMessyTrial(final boolean isMessyTrial)
	{
		m_isMessyTrial = isMessyTrial;
	}

	public void setResponseType(final ResponseType p_responseType)
	{
		m_responseType = p_responseType;
	}

	public void setRT(final double rt)
	{
		m_rt = rt;
	}

	public void setSubjectYearData(final SubjectYearData subjectYearData)
	{
		m_subjectYearData = subjectYearData;
	}

	public void setTimeIndex(final int timeIndex)
	{
		m_timeIndex = timeIndex;
	}
}
