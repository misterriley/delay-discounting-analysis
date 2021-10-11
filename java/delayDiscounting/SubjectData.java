/**
 * 
 */
package delayDiscounting;

/**
 * @author Ringo
 * 
 */
public class SubjectData
	implements Comparable<SubjectData>
{
	private final Integer			m_subjectID;

	private final SubjectYearData	m_year1Data;
	private final SubjectYearData	m_year2Data;

	private String					m_sex;
	private String					m_adhdTdGroup;

	public SubjectData(final Integer p_subjectID)
	{
		m_subjectID = p_subjectID;
		m_year1Data = new SubjectYearData(this, 1);
		m_year2Data = new SubjectYearData(this, 2);
	}

	public SubjectData(final Integer p_subjectID, final String p_sex,
		final String p_adhdTdGroup)
	{
		this(p_subjectID);
		m_sex = p_sex;
		m_adhdTdGroup = p_adhdTdGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final SubjectData p_that)
	{
		return this.m_subjectID - p_that.getSubjectID();
	}

	public String getAdhdTdGroup()
	{
		return m_adhdTdGroup;
	}

	public String getSex()
	{
		return m_sex;
	}

	public int getSubjectID()
	{
		return m_subjectID;
	}

	public SubjectYearData getYear1Data()
	{
		return m_year1Data;
	}

	public SubjectYearData getYear2Data()
	{
		return m_year2Data;
	}

	public SubjectYearData getYearData(final int p_year)
	{
		switch(p_year)
		{
			case 1:
				return getYear1Data();
			case 2:
				return getYear2Data();
			default:
				throw new RuntimeException("Invalid year: " + p_year);
		}
	}
}
