/**
 * 
 */
package bootstrap;

import java.util.ArrayList;
import java.util.Random;

import delayDiscounting.Settings;

/**
 * @author Ringo
 * 
 */
public class BootstrapManager<T>
{
	public static final int	NUM_REPETITIONS	= Settings.getNumBootstrapRepetitions();
	private final Random	m_random		= new Random();

	private BootstrapResult buildResultObject(final DataAnalyzer<T> p_analyzer,
		final double[] meanArray, final double[] sdArray)
	{
		final BootstrapResult ret = new BootstrapResult();
		for(int i = 0; i < p_analyzer.numVariables(); i++)
		{
			final String variableName = p_analyzer.getVariableName(i);
			ret.addResult(variableName, meanArray[i], sdArray[i]);
		}

		return ret;
	}

	private double[] computeMeans(final DataAnalyzer<T> p_analyzer,
		final double[][] p_resultArray)
	{
		final double[] meanArray = new double[p_analyzer.numVariables()];
		for(final double[] bootstrapResult: p_resultArray)
		{
			for(int i = 0; i < bootstrapResult.length; i++)
			{
				meanArray[i] += bootstrapResult[i];
			}
		}

		for(int i = 0; i < meanArray.length; i++)
		{
			meanArray[i] /= NUM_REPETITIONS;
		}
		return meanArray;
	}

	private double[] computeStandardErrors(final DataAnalyzer<T> p_analyzer,
		final double[][] resultArray, final double[] meanArray)
	{
		final double[] sdArray = new double[p_analyzer.numVariables()];
		if(NUM_REPETITIONS == 1)
		{
			for(int i = 0; i < sdArray.length; i++)
			{
				sdArray[i] = Double.NaN;
			}
		}
		else
		{
			for(final double[] bootstrapResult: resultArray)
			{
				for(int i = 0; i < bootstrapResult.length; i++)
				{
					final double deviation = bootstrapResult[i] - meanArray[i];
					final double contribution = deviation * deviation
						/ (NUM_REPETITIONS - 1);
					sdArray[i] += contribution;
				}
			}

			for(int i = 0; i < sdArray.length; i++)
			{
				sdArray[i] = Math.sqrt(sdArray[i]);
			}
		}
		return sdArray;
	}

	/**
	 * @param p_dataSet
	 * @return
	 */
	private ArrayList<T> generateRandomList(final ArrayList<T> p_dataSet)
	{
		final ArrayList<T> ret = new ArrayList<T>();

		for(@SuppressWarnings("unused")
		final T element: p_dataSet)
		{
			final int randomIndex = m_random.nextInt(p_dataSet.size());
			ret.add(p_dataSet.get(randomIndex));
		}

		return ret;
	}

	private double[][] getBootstrapResults(final DataAnalyzer<T> p_analyzer,
		final ArrayList<T> p_dataSet)
	{
		final double[][] resultArray = new double[NUM_REPETITIONS][];

		if(NUM_REPETITIONS == 1)
		{
			resultArray[0] = p_analyzer.analyzeData(p_dataSet);
		}
		else
		{
			for(int i = 0; i < NUM_REPETITIONS; i++)
			{
				final ArrayList<T> randomArray = generateRandomList(p_dataSet);
				final double[] result = p_analyzer.analyzeData(randomArray);
				resultArray[i] = result;
			}
		}
		return resultArray;
	}

	public BootstrapResult runBootstrap(final DataAnalyzer<T> p_analyzer,
		final ArrayList<T> p_dataSet)
	{
		final double[][] resultArray = getBootstrapResults(p_analyzer,
			p_dataSet);
		final double[] meanArray = computeMeans(p_analyzer, resultArray);
		final double[] sdArray = computeStandardErrors(p_analyzer, resultArray,
			meanArray);

		return buildResultObject(p_analyzer, meanArray, sdArray);
	}
}
