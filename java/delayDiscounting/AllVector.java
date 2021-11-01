/**
 *
 */
package delayDiscounting;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Ringo
 *
 */
public class AllVector
{
	private final SubjectData m_subjectData;

	public AllVector(final SubjectData p_subjectData)
	{
		m_subjectData = p_subjectData;
	}

	public void writeVectorFile()
	{
		final ArrayList<ResponseData> responses = m_subjectData.getYear1Data().getAllValidResponses();
		final int[] vector = new int[responses.size()];
		for (int i = 0; i < responses.size(); i++)
		{
			vector[i] = responses.get(i).getTimeIndex();
		}

		Arrays.sort(vector);
		DataWriting.writeAllVector(vector, m_subjectData);
	}
}
