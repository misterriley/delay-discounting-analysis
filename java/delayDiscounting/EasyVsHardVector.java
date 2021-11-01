/**
 *
 */
package delayDiscounting;

import java.util.Arrays;

/**
 * @author Ringo
 *
 */
public class EasyVsHardVector
{
	private final SubjectData m_subjectData;

	public EasyVsHardVector(final SubjectData p_subjectData)
	{
		if (!p_subjectData.getYear1Data().isTypeA())
		{
			throw new RuntimeException("Only type A subjects allowed");
		}

		m_subjectData = p_subjectData;
	}

	public void writeVectorFiles()
	{
		final ResponseData[] easyResponses = m_subjectData.getYear1Data().getEasyResponses();
		final ResponseData[] hardResponses = m_subjectData.getYear1Data().getHardResponses();
		final ResponseData[] midResponses = m_subjectData.getYear1Data().getMidResponses();
		final ResponseData[] invalidResponses = m_subjectData.getYear1Data().getInvalidResponses();

		final int[] easyVector = getSortedTimeIndices(easyResponses);
		final int[] hardVector = getSortedTimeIndices(hardResponses);
		final int[] midVector = getSortedTimeIndices(midResponses);
		final int[] invalidVector = getSortedTimeIndices(invalidResponses);

		DataWriting.writeEasyVector(easyVector, m_subjectData);
		DataWriting.writeHardVector(hardVector, m_subjectData);
		DataWriting.writeMidVector(midVector, m_subjectData);
		DataWriting.writeInvalidVector(invalidVector, m_subjectData);
	}

	private int[] getSortedTimeIndices(final ResponseData[] p_responses)
	{
		final int[] ret = new int[p_responses.length];
		for (int i = 0; i < p_responses.length; i++)
		{
			ret[i] = p_responses[i].getTimeIndex();
		}
		Arrays.sort(ret);
		return ret;
	}
}
