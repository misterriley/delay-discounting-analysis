/**
 * 
 */
package delayDiscounting;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Ringo
 * 
 */
public class SubjectOutputFile
{
	private final ArrayList<SubjectOutputRow>	m_rows;

	public SubjectOutputFile()
	{
		m_rows = new ArrayList<SubjectOutputRow>();
		m_rows.add(new SubjectOutputHeaderRow());
	}

	public void addRow(final SubjectOutputRow p_row)
	{
		m_rows.add(p_row);
	}

	public void prepareToWrite()
	{
		for(final SubjectOutputRow row: m_rows)
		{
			row.toString();
		}
	}

	public void writeOutput(final BufferedWriter p_writer)
		throws IOException
	{
		for(final SubjectOutputRow row: m_rows)
		{
			final String toWrite = row.toString();
			p_writer.write(toWrite);
			p_writer.write("\r\n");
		}
	}
}
