/**
 * 
 */
package delayDiscounting;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * @author Ringo
 * 
 */
public class Utilities
{
	public static void close(final Reader p_reader)
	{
		if(p_reader == null)
		{
			return;
		}

		try
		{
			p_reader.close();
		}
		catch(final IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static void close(final Writer p_writer)
	{
		if(p_writer == null)
		{
			return;
		}

		try
		{
			p_writer.close();
		}
		catch(final IOException ex)
		{
			ex.printStackTrace();
		}
	}

	public static double parseDouble(final String p_string)
	{
		try
		{
			return Double.parseDouble(p_string);
		}
		catch(final Exception p_ex)
		{
			return Double.NaN;
		}
	}

	public static int parseInt(final String p_string)
	{
		try
		{
			return Integer.parseInt(p_string);
		}
		catch(final Exception p_ex)
		{
			return -1;
		}
	}
}
