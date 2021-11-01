/**
 *
 */
package delayDiscounting;

import java.util.Collection;

import types.ResponseType;

/**
 * @author Ringo
 *
 */
public class GridSearch
{
	private static final double	K_STEP				= Settings.getKStepSize();
	private static final double	SENSITIVITY_STEP	= Settings.getSensitivityStepSize();

	public static double[] getMaximumLikelihoodConstants(
		final Collection<ResponseData> p_responses,
		final double p_minKValue,
		final double p_maxKValue)
	{
		final double kLimit = p_maxKValue + K_STEP;

		final double[] ret = new double[2];

		int separableValues = 0;
		for (double k = p_minKValue; k < kLimit; k += K_STEP)
		{
			if (dataIsSeparable(p_responses, k))
			{
				separableValues++;
				ret[0] += k;
			}
		}

		if (separableValues > 0)
		{
			ret[0] /= separableValues;
			ret[1] = 1;
			return ret;
		}

		double maxLnLikelihood = Double.NEGATIVE_INFINITY;
		for (double k = p_minKValue; k < kLimit; k += K_STEP)
		{
			for (double sensitivity = Constants.MIN_SENSITIVITY; sensitivity
				<= Constants.MAX_SENSITIVITY; sensitivity += SENSITIVITY_STEP)
			{
				final double a = Functions.getA(sensitivity);
				final double lnLikelihood = Functions.lnProbOfResponses(a, k, p_responses);
				if (lnLikelihood > maxLnLikelihood)
				{
					ret[0] = k;
					ret[1] = sensitivity;
					maxLnLikelihood = lnLikelihood;
				}
			}
		}

		return ret;
	}

	private static boolean dataIsSeparable(final Collection<ResponseData> p_responses, final double p_k)
	{
		for (final ResponseData response : p_responses)
		{
			if (response.isAttentionCheck() || response.getResponseType() == ResponseType.NONE)
			{
				continue;
			}

			final double diff = Functions.hypValDifference(response, p_k);
			if (diff < 0)
			{
				return false;
			}
		}
		return true;
	}
}
