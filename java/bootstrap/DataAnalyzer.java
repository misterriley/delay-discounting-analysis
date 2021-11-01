/**
 *
 */
package bootstrap;

import java.util.Collection;

/**
 * @author Ringo
 *
 */
public abstract class DataAnalyzer<T>
{
	public abstract double[] analyzeData(Collection<T> p_dataSet);

	public abstract String getVariableName(int p_variableIndex);

	public abstract int numVariables();
}
