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
public class SubjectOutputRow
{
	/**
	 *
	 */
	private static final String CSV_DELIMITER = ",";

	private static String appendCSVCell(final String p_stem, final String p_newCell)
	{
		if (p_stem == null || p_stem.equals(""))
		{
			return p_newCell;
		}
		return p_stem + CSV_DELIMITER + p_newCell;
	}

	private SubjectYearData m_subjectYearData;

	private String m_toWrite;

	public SubjectOutputRow()
	{

	}

	public SubjectOutputRow(final SubjectYearData p_subjectYearData)
	{
		setSubjectYearData(p_subjectYearData);
	}

	public String getLLPString(final AnalysisType p_type)
	{
		return getDisplayString(m_subjectYearData.getLLP(p_type));
	}

	public String getOutputString(final AnalysisType p_analysisType, final KAnalysisValueType p_analysisValueType)
	{
		final Object outputObject = m_subjectYearData.getKOutput(p_analysisType, p_analysisValueType);
		return getDisplayString(outputObject);
	}

	public String getOutputString(final EasyHardDataType p_type)
	{
		final Object outputObject = m_subjectYearData.getOutput(p_type);
		return getDisplayString(outputObject);
	}

	public String getOutputString(final SubjectInfoType p_type)
	{
		final Object outputObject = m_subjectYearData.getOutput(p_type);
		return getDisplayString(outputObject);
	}

	public SubjectYearData getSubject()
	{
		return m_subjectYearData;
	}

	public void setSubjectYearData(final SubjectYearData p_subjectYearData)
	{
		m_subjectYearData = p_subjectYearData;
	}

	@Override
	public String toString()
	{
		if (m_toWrite == null)
		{
			m_toWrite = "";

			for (final SubjectInfoType type : SubjectInfoType.values())
			{
				m_toWrite = appendCSVCell(m_toWrite, getOutputString(type));
			}

			if (Settings.runKAnalysis())
			{
				for (final EasyHardDataType type : EasyHardDataType.values())
				{
					m_toWrite = appendCSVCell(m_toWrite, getOutputString(type));
				}
			}

			for (final AnalysisType analysisType : AnalysisType.values())
			{
				if (Settings.runKAnalysis())
				{
					for (final KAnalysisValueType analysisValueType : KAnalysisValueType.values())
					{
						m_toWrite = appendCSVCell(m_toWrite, getOutputString(analysisType, analysisValueType));
					}
				}

				m_toWrite = appendCSVCell(m_toWrite, getLLPString(analysisType));
			}
		}
		return m_toWrite;
	}

	private String getDisplayString(final Object outputObject)
	{
		if (outputObject == null)
		{
			return "N/A";
		}

		if (outputObject instanceof String)
		{
			return (String) outputObject;
		}

		if (outputObject instanceof Double)
		{
			return outputObject.toString();
		}

		throw new RuntimeException("Unexpected object type: " + outputObject.getClass());
	}
}