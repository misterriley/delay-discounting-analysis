/**
 *
 */
package delayDiscounting;

import java.util.ArrayList;
import java.util.Collection;

import types.ResponseType;

/**
 * @author Ringo
 *
 */
public class Functions
{
	private static LnLogitLinearization m_logitLin = new LnLogitLinearization();

	public static double calculateBreakEvenKValue(
		final double p_choice1,
		final int p_choice1Delay,
		final double p_choice2,
		final int p_choice2Delay)
	{
		final double numerator = p_choice2 - p_choice1;
		final double denominator = p_choice1 * p_choice2Delay - p_choice2 * p_choice1Delay;
		return numerator / denominator;
	}

	public static double calculateBreakEvenKValue(final ResponseData p_data)
	{
		return calculateBreakEvenKValue(
			p_data.getChoice1(),
			p_data.getChoice1Delay(),
			p_data.getChoice2(),
			p_data.getChoice2Delay());
	}

	public static double getA(final double p_sensitivity)
	{
		final double radians = p_sensitivity * Math.PI / 2;
		return Math.tan(radians);
	}

	public static double[] getMaximumLikelihoodConstants(
		final Collection<ResponseData> p_responses,
		final double p_minKValue,
		final double p_maxKValue)
	{
		if (p_minKValue <= 0)
		{
			throw new RuntimeException("Out of bounds minimum k value");
		}

		if (p_responses.size() == 0)
		{
			System.out.println("0 responses");
			return null;
		}

		return GridSearch.getMaximumLikelihoodConstants(p_responses, p_minKValue, p_maxKValue);
	}

	public static double hypValDifference(final ResponseData p_response, final double p_k)
	{
		if (p_response.getResponseType() == ResponseType.NONE)
		{
			throw new RuntimeException("Unable to calculate difference for null response");
		}

		final double value1 = hyperbolicPresentValue(p_response.getChoice1(), p_k, p_response.getChoice1Delay());
		final double value2 = hyperbolicPresentValue(p_response.getChoice2(), p_k, p_response.getChoice2Delay());

		double diff = value2 - value1;
		if (p_response.getResponseType() == ResponseType.SOONER)
		{
			diff *= -1;
		}
		return diff;
	}

	public static double lnLogitFast(final double p_x)
	{
		return m_logitLin.logitApprox(p_x);
	}

	public static double lnProbOfResponse(final double p_a, final double p_k, final ResponseData p_response)
	{
		final double difference = hypValDifference(p_response, p_k);
		return lnLogit(p_a, difference);
	}

	public static double lnProbOfResponses(
		final double p_a,
		final double p_k,
		final Collection<ResponseData> p_responses)
	{
		double ret = 0;
		for (final ResponseData response : p_responses)
		{
			if (response.getResponseType() == ResponseType.NONE || response.isAttentionCheck())
			{
				continue;
			}

			ret += lnProbOfResponse(p_a, p_k, response);
		}
		return ret;
	}

	public static double logitExact(final double p_x)
	{
		final double exponential = Math.exp(p_x);
		return exponential / (1 + exponential);
	}

	public static Double percentAttentionChecksCorrect(final ArrayList<ResponseData> p_responses)
	{
		int numChecks = 0;
		int numCorrect = 0;

		for (final ResponseData response : p_responses)
		{
			if (!response.isAttentionCheck())
			{
				continue;
			}

			if (response.getResponseType() == ResponseType.NONE)
			{
				continue;
			}

			numChecks++;
			final int oldNumCorrect = numCorrect;

			if (response.getChoice1() >= response.getChoice2())
			{
				if (response.getResponseType() == ResponseType.SOONER)
				{
					numCorrect++;
				}
			}
			else
				if (response.getChoice1() == 0)
				{
					if (response.getResponseType() == ResponseType.LATER)
					{
						numCorrect++;
					}
				}
				else
					if (response.getChoice2() == 0)
					{
						if (response.getResponseType() == ResponseType.SOONER)
						{
							numCorrect++;
						}
					}
					else
						if (response.getChoice2() / response.getChoice1() > 30)
						{
							if (response.getResponseType() == ResponseType.LATER)
							{
								numCorrect++;
							}
						}

			if (numCorrect == oldNumCorrect)
			{
				System.out
					.println(
						"Wrong: "
							+ response.getChoice1()
							+ ":"
							+ response.getChoice1Delay()
							+ " vs "
							+ response.getChoice2()
							+ ":"
							+ response.getChoice2Delay()
							+ " choice "
							+ response.getResponseType());
			}
		}

		if (numChecks == 0)
		{
			return null;
		}

		return (double) numCorrect / numChecks;
	}

	public static int signum(final double p_dbl)
	{
		if (p_dbl == 0)
		{
			return 0;
		}

		if (p_dbl > 0)
		{
			return 1;
		}

		return -1;
	}

	private static double hyperbolicPresentValue(final double p_futureValue, final double p_k, final double p_time)
	{
		return p_futureValue / (1 + p_k * p_time);
	}

	private static double lnLogit(final double p_a, final double p_x)
	{
		final double exponent = p_a * p_x;
		return lnLogitFast(exponent);
	}
}
