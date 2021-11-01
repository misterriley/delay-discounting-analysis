/**
 *
 */
package delayDiscounting;

import java.util.Comparator;

/**
 * @author Ringo
 *
 */
public class DifficultyComparator implements Comparator<ResponseData>
{
	private final double	m_k;
	private final double	m_sensitivity;
	private final double	m_a;

	public DifficultyComparator(final double p_k, final double p_sensitivity)
	{
		m_k = p_k;
		m_sensitivity = p_sensitivity;
		m_a = Functions.getA(m_sensitivity);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final ResponseData p_r1, final ResponseData p_r2)
	{
		if (m_sensitivity == 1)
		{
			// for separable data sets
			final double diff1 = Functions.hypValDifference(p_r1, m_k);
			final double diff2 = Functions.hypValDifference(p_r2, m_k);

			final double easinessRating1 = Math.abs(diff1);
			final double easinessRating2 = Math.abs(diff2);

			return Functions.signum(easinessRating1 - easinessRating2);
		}

		double likelihood1 = p_r1.getLikelihood();
		if (likelihood1 == 0)
		{
			likelihood1 = Math.exp(Functions.lnProbOfResponse(m_a, m_k, p_r1));
			p_r1.setLikelihood(likelihood1);
		}

		double likelihood2 = p_r2.getLikelihood();
		if (likelihood2 == 0)
		{
			likelihood2 = Math.exp(Functions.lnProbOfResponse(m_a, m_k, p_r2));
			p_r2.setLikelihood(likelihood2);
		}

		final double easinessRating1 = Math.abs(likelihood1 - .5);
		final double easinessRating2 = Math.abs(likelihood2 - .5);

		return Functions.signum(easinessRating1 - easinessRating2);
	}
}
