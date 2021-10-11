/**
 * 
 */
package bootstrap;

import java.util.HashMap;

/**
 * @author Ringo
 * 
 */
public class BootstrapResult
{
	private final HashMap<String, double[]>	m_resultsMap;

	public BootstrapResult()
	{
		m_resultsMap = new HashMap<String, double[]>();
	}

	public void addResult(final String p_variableName, final double p_mean,
		final double p_standardError)
	{
		final double[] value = new double[]{p_mean, p_standardError};

		m_resultsMap.put(p_variableName, value);
	}

	public double getMean(final String p_variableName)
	{
		return m_resultsMap.get(p_variableName)[0];
	}

	public double getStandardError(final String p_variableName)
	{
		return m_resultsMap.get(p_variableName)[1];
	}
}
