/**
 * 
 */
package delayDiscounting;

import java.util.Collection;

import bootstrap.BootstrapResult;
import bootstrap.DataAnalyzer;

/**
 * @author Ringo
 * 
 */
public class ResponseSetAnalyzer extends DataAnalyzer<ResponseData>
{
	public static final String		K			= "K";
	public static final String		SENSITIVITY	= "Sensitivity";

	private static final String[]	VAR_NAMES	= {K, SENSITIVITY};

	public static double getK(final BootstrapResult p_result)
	{
		return p_result.getMean(K);
	}

	public static double getKSE(final BootstrapResult p_result)
	{
		return p_result.getStandardError(K);
	}

	public static double getSensitivity(final BootstrapResult p_result)
	{
		return p_result.getMean(SENSITIVITY);
	}

	public static double getSensitivitySE(final BootstrapResult p_result)
	{
		return p_result.getStandardError(SENSITIVITY);
	}

	private double	m_maxKValue;
	private double	m_minKValue;

	@Override
	public double[] analyzeData(final Collection<ResponseData> p_dataSet)
	{
		return Functions.getMaximumLikelihoodConstants(p_dataSet, m_minKValue,
			m_maxKValue);
	}

	public double getMaxKValue()
	{
		return m_maxKValue;
	}

	@Override
	public String getVariableName(final int p_variableIndex)
	{
		return VAR_NAMES[p_variableIndex];
	}

	@Override
	public int numVariables()
	{
		return 2;
	}

	public void setMaxKValue(final double maxKValue)
	{
		m_maxKValue = maxKValue;
	}

	public void setMinKValue(final double p_minKValue)
	{
		m_minKValue = p_minKValue;
	}
}
