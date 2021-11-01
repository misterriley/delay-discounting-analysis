/**
 *
 */
package delayDiscounting;

/**
 * @author Ringo
 *
 */
public class LnLogitLinearization
{
	private static final int NUM_SECTIONS = Settings.getLogitLinNumSections();

	private static final int	NUM_POINTS		= NUM_SECTIONS + 1;
	private static final double	SUPPORT_MIN		= -40;
	private static final double	SUPPORT_MAX		= 40;
	private static final double	SUPPORT_SPAN	= SUPPORT_MAX - SUPPORT_MIN;
	private static final double	DELTA			= SUPPORT_SPAN / NUM_SECTIONS;

	public static void main(final String[] p_args)
	{
		for (double testPoint = SUPPORT_MIN; testPoint < SUPPORT_MAX; testPoint += .00011)
		{
			final double logitVal = Math.log(Functions.logitExact(testPoint));
			final double logitApprox = Functions.lnLogitFast(testPoint);

			final double diff = Math.abs(logitVal - logitApprox);
			System.out.println(diff);
		}
	}

	private final double[] m_cachedValues;

	public LnLogitLinearization()
	{
		m_cachedValues = new double[NUM_POINTS];

		for (int i = 0; i < NUM_POINTS; i++)
		{
			final double x = SUPPORT_MIN + DELTA * i;
			final double lnLogit = Math.log(Functions.logitExact(x));
			m_cachedValues[i] = lnLogit;
		}
	}

	public double logitApprox(final double p_x)
	{
		if (p_x < SUPPORT_MIN)
		{
			return Double.NEGATIVE_INFINITY;
		}

		if (p_x > SUPPORT_MAX)
		{
			return 0;
		}

		final double pointApprox = (p_x - SUPPORT_MIN) / SUPPORT_SPAN * NUM_SECTIONS;
		final int lowerEst = (int) pointApprox;
		final int higherEst = lowerEst + 1;
		final double fraction = pointApprox - lowerEst;

		if (higherEst >= m_cachedValues.length)
		{
			return m_cachedValues[lowerEst];
		}

		if (lowerEst < 0)
		{
			return m_cachedValues[higherEst];
		}

		final double ret = m_cachedValues[lowerEst] * (1 - fraction) + m_cachedValues[higherEst] * fraction;

		return ret;
	}
}
