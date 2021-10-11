/**
 * 
 */
package delayDiscounting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import types.AnalysisType;
import types.EasyHardDataType;
import types.KAnalysisValueType;
import types.ResponseType;
import types.SubjectInfoType;
import bootstrap.BootstrapManager;
import bootstrap.BootstrapResult;

/**
 * @author Ringo
 * 
 */
public class SubjectYearData
{
	private static void addResponseToMap(
		final Map<AnalysisType, ArrayList<ResponseData>> p_map,
		final ResponseData p_response)
	{
		p_map.get(AnalysisType.ALL_ALL).add(p_response);
		if(p_response.isDelayedTrial())
		{
			p_map.get(AnalysisType.ALL_DELAYED).add(p_response);
		}
		else
		{
			p_map.get(AnalysisType.ALL_IMMEDIATE).add(p_response);
		}

		if(p_response.isMessyTrial())
		{
			p_map.get(AnalysisType.MESSY_ALL).add(p_response);
			if(p_response.isDelayedTrial())
			{
				p_map.get(AnalysisType.MESSY_DELAYED).add(p_response);
			}
			else
			{
				p_map.get(AnalysisType.MESSY_IMMEDIATE).add(p_response);
			}
		}
		else
		{
			p_map.get(AnalysisType.ROUND_ALL).add(p_response);
			if(p_response.isDelayedTrial())
			{
				p_map.get(AnalysisType.ROUND_DELAYED).add(p_response);
			}
			else
			{
				p_map.get(AnalysisType.ROUND_IMMEDIATE).add(p_response);
			}
		}
	}

	private static double[] getMeanSDRT(final ResponseData[] p_responses)
	{
		final double[] ret = new double[2];

		double sum = 0;
		for(final ResponseData response: p_responses)
		{
			sum += response.getRT();
		}

		final double mean = sum / p_responses.length;
		ret[0] = mean;

		double sumDev = 0;
		for(final ResponseData response: p_responses)
		{
			final double thisDev = Math.pow((response.getRT() - mean), 2);
			sumDev += thisDev;
		}

		final double variance = sumDev / p_responses.length;
		final double sd = Math.pow(variance, .5);
		ret[1] = sd;

		return ret;
	}

	private final SubjectData									m_subject;
	private String												m_timeString;
	private String												m_dateString;
	private String												m_orderIndicator;
	private boolean												m_isTypeA;
	private final Map<AnalysisType, ArrayList<ResponseData>>	m_allResponses;
	private final Map<AnalysisType, ArrayList<ResponseData>>	m_validResponses;
	private final ArrayList<ResponseData>						m_invalidResponses;
	private String												m_sourceFile;
	private final int											m_year;
	private final ResponseSetAnalyzer							m_analyzer;

	private final BootstrapManager<ResponseData>				m_bootstrapManager;
	private final Map<AnalysisType, BootstrapResult>			m_bootstrapResults;

	private double												m_age;

	private ResponseData[]										m_easyResponses;
	private ResponseData[]										m_hardResponses;
	private ResponseData[]										m_midResponses;

	public SubjectYearData(final SubjectData p_subject,
		final int p_year)
	{
		m_allResponses = new HashMap<AnalysisType, ArrayList<ResponseData>>();
		m_validResponses = new HashMap<AnalysisType, ArrayList<ResponseData>>();
		m_invalidResponses = new ArrayList<ResponseData>();
		for(final AnalysisType type: AnalysisType.values())
		{
			m_allResponses.put(type, new ArrayList<ResponseData>());
			m_validResponses.put(type, new ArrayList<ResponseData>());
		}
		m_bootstrapResults = new HashMap<AnalysisType, BootstrapResult>();

		m_analyzer = new ResponseSetAnalyzer();
		m_bootstrapManager = new BootstrapManager<ResponseData>();
		m_subject = p_subject;
		m_year = p_year;
	}

	public void addResponse(final double p_choice1, final int p_choice1Delay,
		final double p_choice2, final int p_choice2Delay,
		final ResponseType p_responseType, final boolean p_isAttentionCheck,
		final boolean p_isMessyTrial, final int p_timeIndex, final double p_k,
		final boolean p_isValid, final double p_rt)
	{
		final ResponseData response = new ResponseData(this, p_choice1,
			p_choice1Delay, p_choice2, p_choice2Delay, p_responseType,
			p_isAttentionCheck, p_isMessyTrial, p_timeIndex, p_k, p_isValid,
			p_rt);

		addResponseToMap(m_allResponses, response);

		if(p_isValid)
		{
			addResponseToMap(m_validResponses, response);
		}
		else
		{
			m_invalidResponses.add(response);
		}
	}

	private boolean analysisIsValidForSubject(final AnalysisType p_type)
	{
		if(m_isTypeA)
		{
			return true;
		}

		if(p_type == AnalysisType.ALL_DELAYED
			|| p_type == AnalysisType.ROUND_DELAYED
			|| p_type == AnalysisType.MESSY_DELAYED
			|| p_type == AnalysisType.ALL_IMMEDIATE
			|| p_type == AnalysisType.ROUND_IMMEDIATE
			|| p_type == AnalysisType.MESSY_IMMEDIATE)
		{
			return false;
		}

		return true;
	}

	private void calculateEasyHardResponses()
	{
		if(m_easyResponses == null || m_hardResponses == null
			|| m_midResponses == null)
		{
			final ArrayList<ResponseData> responses = getAllValidResponses();
			final ResponseData[] responseArray = new ResponseData[responses.size()];
			responses.toArray(responseArray);

			final DifficultyComparator comp = new DifficultyComparator(
				getKValue(AnalysisType.ALL_ALL),
				getSensitivity(AnalysisType.ALL_ALL));
			Arrays.sort(responseArray, comp);

			final int numMidResponses = responses.size() - 2
				* Settings.getNumEasyHardResponses();

			m_easyResponses = new ResponseData[Settings.getNumEasyHardResponses()];
			m_hardResponses = new ResponseData[Settings.getNumEasyHardResponses()];
			m_midResponses = new ResponseData[numMidResponses];

			for(int i = 0; i < Settings.getNumEasyHardResponses(); i++)
			{
				m_hardResponses[i] = responseArray[i];
				m_easyResponses[i] = responseArray[responseArray.length - 1 - i];
			}

			for(int i = 0; i < numMidResponses; i++)
			{
				m_midResponses[i] = responseArray[i
					+ Settings.getNumEasyHardResponses()];
			}
		}
	}

	/**
	 * @param p_responses
	 * @return
	 */
	private double calculateMaxBreakEvenK(
		final ArrayList<ResponseData> p_responses)
	{
		double max = 0;
		for(final ResponseData response: p_responses)
		{
			final double breakEvenK = Functions.calculateBreakEvenKValue(response);
			if(breakEvenK > max)
			{
				max = breakEvenK;
			}
		}
		return max;
	}

	private double calculateMinBreakEvenK(
		final ArrayList<ResponseData> p_responses)
	{
		double min = Double.POSITIVE_INFINITY;
		for(final ResponseData response: p_responses)
		{
			final double breakEvenK = Functions.calculateBreakEvenKValue(response);
			if(breakEvenK < min)
			{
				min = breakEvenK;
			}
		}
		return min;
	}

	public void ensureKValuesArePresent(final AnalysisType p_analysisType)
	{
		if(kResultShouldExist(p_analysisType)
			&& getBootstrapResult(p_analysisType, false) == null)
		{
			final double maxK = calculateMaxBreakEvenK(getResponses(
				p_analysisType,
				true));
			m_analyzer.setMaxKValue(maxK);
			final double minK = calculateMinBreakEvenK(getResponses(
				p_analysisType,
				true));
			m_analyzer.setMinKValue(minK);

			System.out.println("Analyzing " + m_subject.getSubjectID() + " "
				+ p_analysisType
				+ " year " + m_year);
			final BootstrapResult result = m_bootstrapManager.runBootstrap(
				m_analyzer, getResponses(p_analysisType, true));

			m_bootstrapResults.put(p_analysisType, result);

			if(result.getMean(ResponseSetAnalyzer.SENSITIVITY) == 1)
			{
				System.out.println("Separable");
				System.out.println("k = "
					+ result.getMean(ResponseSetAnalyzer.K) +
					"\nk(se) = "
					+ result.getStandardError(ResponseSetAnalyzer.K));
			}
			else
			{
				System.out.println("k = "
					+ result.getMean(ResponseSetAnalyzer.K) +
					"\nk(se) = "
					+ result.getStandardError(ResponseSetAnalyzer.K) +
					"\nsens = "
					+ result.getMean(ResponseSetAnalyzer.SENSITIVITY) +
					"\nsens(se) = "
					+ result.getStandardError(ResponseSetAnalyzer.K));
			}
		}
	}

	public double getAge()
	{
		return m_age;
	}

	public ArrayList<ResponseData> getAllValidResponses()
	{
		return m_validResponses.get(AnalysisType.ALL_ALL);
	}

	public BootstrapResult getBootstrapResult(final AnalysisType p_type,
		final boolean p_ensureResultsExist)
	{
		if(p_ensureResultsExist)
		{
			ensureKValuesArePresent(p_type);
		}

		return m_bootstrapResults.get(p_type);
	}

	public String getDateString()
	{
		return m_dateString;
	}

	public ResponseData[] getEasyResponses()
	{
		calculateEasyHardResponses();
		return m_easyResponses;
	}

	public ResponseData[] getHardResponses()
	{
		calculateEasyHardResponses();
		return m_hardResponses;
	}

	public ResponseData[] getInvalidResponses()
	{
		return m_invalidResponses.toArray(new ResponseData[0]);
	}

	public Object getKOutput(final AnalysisType p_analysisType,
		final KAnalysisValueType p_analysisValueType)
	{
		final BootstrapResult result = getBootstrapResult(p_analysisType, true);
		if(result == null)
		{
			return null;
		}

		switch(p_analysisValueType)
		{
			case K:
				return result.getMean(ResponseSetAnalyzer.K);
			case K_SE:
				return result.getStandardError(ResponseSetAnalyzer.K);
			case S:
				return result.getMean(ResponseSetAnalyzer.SENSITIVITY);
			case S_SE:
				return result.getStandardError(ResponseSetAnalyzer.SENSITIVITY);
			default:
				throw new IllegalArgumentException("Unexpected type: "
					+ p_analysisValueType);
		}
	}

	/**
	 * @param p_all
	 * @return
	 */
	public Double getKValue(final AnalysisType p_type)
	{
		final BootstrapResult result = getBootstrapResult(p_type, true);
		if(result == null)
		{
			return null;
		}

		return result.getMean(ResponseSetAnalyzer.K);
	}

	public Double getLLP(final AnalysisType p_type)
	{
		if(!analysisIsValidForSubject(p_type))
		{
			return null;
		}

		int llResponses = 0;

		final ArrayList<ResponseData> validResponses = getResponses(p_type,
			true);
		for(final ResponseData response: validResponses)
		{
			if(response.getResponseType() == ResponseType.LATER)
			{
				llResponses++;
			}
		}

		return new Double(((double)llResponses * 100) / validResponses.size());
	}

	public ResponseData[] getMidResponses()
	{
		calculateEasyHardResponses();
		return m_midResponses;
	}

	public String getOrderIndicator()
	{
		return m_orderIndicator;
	}

	public Double getOutput(final EasyHardDataType p_type)
	{
		switch(p_type)
		{
			case EASY_RT_MEAN:
				return getMeanSDRT(getEasyResponses())[0];
			case EASY_RT_SD:
				return getMeanSDRT(getEasyResponses())[1];
			case HARD_RT_MEAN:
				return getMeanSDRT(getHardResponses())[0];
			case HARD_RT_SD:
				return getMeanSDRT(getHardResponses())[1];
			default:
				throw new IllegalArgumentException();
		}
	}

	public Object getOutput(final SubjectInfoType p_type)
	{
		switch(p_type)
		{
			case AGE:
				return Double.toString(m_age);
			case ATTENTION_PERCENT:
				return Functions.percentAttentionChecksCorrect(m_allResponses.get(AnalysisType.ALL_ALL));
			case DATE:
				return m_dateString;
			case ORDER:
				return m_orderIndicator;
			case SOURCE_FILE:
				return m_sourceFile;
			case SUBJECT_ID:
				return Integer.toString(m_subject.getSubjectID());
			case YEAR:
				return String.valueOf(m_year);
			case ADHD_TD_GROUP:
				return m_subject.getAdhdTdGroup();
			case SEX:
				return m_subject.getSex();
			case TIME:
				return getTimeString();
		}

		throw new RuntimeException("Invalid Data Type: "
			+ p_type);
	}

	public ArrayList<ResponseData> getResponses(final AnalysisType p_type,
		final boolean p_onlyValidResponses)
	{
		if(p_onlyValidResponses)
		{
			return m_validResponses.get(p_type);
		}

		return m_allResponses.get(p_type);
	}

	public Double getSensitivity(final AnalysisType p_type)
	{
		final BootstrapResult result = getBootstrapResult(p_type, true);
		if(result == null)
		{
			return null;
		}

		return result.getMean(ResponseSetAnalyzer.SENSITIVITY);
	}

	public String getSourceFile()
	{
		return m_sourceFile;
	}

	public SubjectData getSubjectData()
	{
		return m_subject;
	}

	public String getTimeString()
	{
		return m_timeString;
	}

	public String getUniqueTag(final AnalysisType p_type)
	{
		return m_subject.getSubjectID() + " year " + m_year + " ("
			+ p_type.toString() + ")";
	}

	public int getYear()
	{
		return m_year;
	}

	public boolean isTypeA()
	{
		return m_isTypeA;
	}

	/**
	 * @param p_type
	 * @return
	 */
	public boolean kResultShouldExist(final AnalysisType p_analysisType)
	{
		if(!Settings.runKAnalysis())
		{
			return false;
		}

		if(!analysisIsValidForSubject(p_analysisType))
		{
			return false;
		}

		return true;
	}

	/**
	 * @param p_year1Age
	 */
	public void setAge(final double p_age)
	{
		m_age = p_age;
	}

	public void setDateString(final String dateString)
	{
		m_dateString = dateString;
	}

	public void setOrderIndicator(final String orderIndicator)
	{
		m_orderIndicator = orderIndicator;
	}

	public void setSourceFile(final String p_sourceFile)
	{
		if(m_sourceFile != null && !m_sourceFile.equals(p_sourceFile))
		{
			System.out.println(m_subject.getSubjectID());
		}

		m_sourceFile = p_sourceFile;
	}

	public void setTimeString(final String timeString)
	{
		m_timeString = timeString;
	}

	public void setTypeA(final boolean isTypeA)
	{
		m_isTypeA = isTypeA;
	}
}
