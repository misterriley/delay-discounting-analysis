/**
 * 
 */
package delayDiscounting;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 * @author Ringo
 * 
 */
public class Settings
{
	private static String			PROPERTIES_FILE_NAME					= "DDSettings.txt";

	private static final String		TAG_K_STEP_SIZE							= "KStepSize";
	private static final String		TAG_SENSITIVITY_STEP_SIZE				= "SensitivityStepSize";
	private static final String		TAG_SENSITIVITY_MIN						= "SensitivityMin";
	private static final String		TAG_SENSITIVITY_MAX						= "SensitivityMax";
	private static final String		TAG_LOGIT_LIN_NUM_SECTIONS				= "LogitLinearizationNumSections";
	private static final String		TAG_WRITE_ALL_VECTOR_FILES				= "WriteAllVectorFiles";
	private static final String		TAG_WRITE_EASY_HARD_VECTOR_FILES		= "WriteEasyHardVectorFiles";
	private static final String		TAG_WRITE_OUTPUT_FILE					= "WriteOutputFile";
	private static final String		TAG_K_SANITY_CHECK_TOLERANCE			= "KSanityCheckTolerance";
	private static final String		TAG_RT_MINIMUM_MS						= "RTMinimum";
	private static final String		TAG_NUM_BOOTSTRAP_REPETITIONS			= "NumBootstrapRepetitions";
	private static final String		TAG_NUM_EASY_HARD_RESPONSES				= "NumEasyHardResponses";
	private static final String		TAG_USE_SANN_SEARCH						= "UseSannSearch";
	private static final String		TAG_RUN_K_ANALYSIS						= "RunKAnalysis";

	private static final double		DEFAULT_K_STEP_SIZE						= .0005;
	private static final double		DEFAULT_SENSITIVITY_STEP_SIZE			= .01;
	private static final double		DEFAULT_SENSITIVITY_MIN					= 0;
	private static final double		DEFAULT_SENSITIVITY_MAX					= 1;
	private static final int		DEFAULT_LOGIT_LIN_NUM_SECTIONS			= 1000000;
	private static final boolean	DEFAULT_WRITE_ALL_VECTOR_FILES			= false;
	private static final boolean	DEFAULT_WRITE_EASY_HARD_VECTOR_FILES	= true;
	private static final boolean	DEFAULT_WRITE_OUTPUT_FILE				= true;
	private static final double		DEFAULT_K_SANITY_CHECK_TOLERANCE		= .0001;
	private static final int		DEFAULT_RT_MINIMUM_MS					= 200;
	private static final int		DEFAULT_NUM_BOOTSTRAP_REPETITIONS		= 100;
	private static final int		DEFAULT_NUM_EASY_HARD_RESPONSES			= 16;
	private static final boolean	DEFAULT_USE_SANN_SEARCH					= false;
	private static final boolean	DEFAULT_RUN_K_ANALYSIS					= false;

	private static Properties		m_properties;

	static
	{
		tryToLoadPropertiesFromFile();
	}

	/**
	 * @return
	 */
	private static Properties buildDefaultProperties()
	{
		final Properties ret = new Properties();
		ret.setProperty(TAG_RT_MINIMUM_MS,
			String.valueOf(DEFAULT_RT_MINIMUM_MS));
		ret.setProperty(TAG_NUM_EASY_HARD_RESPONSES,
			String.valueOf(DEFAULT_NUM_EASY_HARD_RESPONSES));
		ret.setProperty(TAG_K_STEP_SIZE, String.valueOf(DEFAULT_K_STEP_SIZE));
		ret.setProperty(TAG_SENSITIVITY_STEP_SIZE,
			String.valueOf(DEFAULT_SENSITIVITY_STEP_SIZE));
		ret.setProperty(TAG_SENSITIVITY_MIN,
			String.valueOf(DEFAULT_SENSITIVITY_MIN));
		ret.setProperty(TAG_SENSITIVITY_MAX,
			String.valueOf(DEFAULT_SENSITIVITY_MAX));
		ret.setProperty(TAG_LOGIT_LIN_NUM_SECTIONS,
			String.valueOf(DEFAULT_LOGIT_LIN_NUM_SECTIONS));
		ret.setProperty(TAG_WRITE_ALL_VECTOR_FILES,
			String.valueOf(DEFAULT_WRITE_ALL_VECTOR_FILES));
		ret.setProperty(TAG_WRITE_EASY_HARD_VECTOR_FILES,
			String.valueOf(DEFAULT_WRITE_EASY_HARD_VECTOR_FILES));
		ret.setProperty(TAG_WRITE_OUTPUT_FILE,
			String.valueOf(DEFAULT_WRITE_OUTPUT_FILE));
		ret.setProperty(TAG_K_SANITY_CHECK_TOLERANCE,
			String.valueOf(DEFAULT_K_SANITY_CHECK_TOLERANCE));
		ret.setProperty(TAG_NUM_BOOTSTRAP_REPETITIONS,
			String.valueOf(DEFAULT_NUM_BOOTSTRAP_REPETITIONS));
		ret.setProperty(TAG_RUN_K_ANALYSIS,
			String.valueOf(DEFAULT_RUN_K_ANALYSIS));
		return ret;
	}

	private static boolean getAsBoolean(final String p_key,
		final boolean p_default)
	{
		return Boolean.parseBoolean(getAsString(p_key,
			String.valueOf(p_default)));
	}

	private static double getAsDouble(final String p_key, final double p_default)
	{
		return Double.parseDouble(getAsString(p_key, String.valueOf(p_default)));
	}

	private static int getAsInt(final String p_key, final int p_default)
	{
		return Integer.parseInt(getAsString(p_key, String.valueOf(p_default)));
	}

	private static String getAsString(final String p_key, final String p_default)
	{
		String value = m_properties.getProperty(p_key);
		if(value == null)
		{
			m_properties.setProperty(p_key, p_default);
			writeProperties(m_properties);
			value = p_default;
		}
		return value;
	}

	public static double getKSanityCheckTolerance()
	{
		return getAsDouble(TAG_K_SANITY_CHECK_TOLERANCE,
			DEFAULT_K_SANITY_CHECK_TOLERANCE);
	}

	public static double getKStepSize()
	{
		return getAsDouble(TAG_K_STEP_SIZE, DEFAULT_K_STEP_SIZE);
	}

	public static int getLogitLinNumSections()
	{
		return getAsInt(TAG_LOGIT_LIN_NUM_SECTIONS,
			DEFAULT_LOGIT_LIN_NUM_SECTIONS);
	}

	/**
	 * @return
	 */
	public static int getNumBootstrapRepetitions()
	{
		return getAsInt(TAG_NUM_BOOTSTRAP_REPETITIONS,
			DEFAULT_NUM_BOOTSTRAP_REPETITIONS);
	}

	public static int getNumEasyHardResponses()
	{
		return getAsInt(TAG_NUM_EASY_HARD_RESPONSES,
			DEFAULT_NUM_EASY_HARD_RESPONSES);
	}

	/**
	 * @return
	 */
	public static int getRTMinimumMS()
	{
		return getAsInt(TAG_RT_MINIMUM_MS, DEFAULT_RT_MINIMUM_MS);
	}

	public static double getSensitivityMax()
	{
		return getAsDouble(TAG_SENSITIVITY_MAX, DEFAULT_SENSITIVITY_MAX);
	}

	public static double getSensitivityMin()
	{
		return getAsDouble(TAG_SENSITIVITY_MIN, DEFAULT_SENSITIVITY_MIN);
	}

	public static double getSensitivityStepSize()
	{
		return getAsDouble(TAG_SENSITIVITY_STEP_SIZE,
			DEFAULT_SENSITIVITY_STEP_SIZE);
	}

	/**
	 * @return
	 */
	public static boolean getUseSannSearch()
	{
		return getAsBoolean(TAG_USE_SANN_SEARCH, DEFAULT_USE_SANN_SEARCH);
	}

	public static boolean runKAnalysis()
	{
		return getAsBoolean(TAG_RUN_K_ANALYSIS, DEFAULT_RUN_K_ANALYSIS);
	}

	private static void tryToLoadPropertiesFromFile()
	{
		m_properties = new Properties();
		FileReader reader = null;
		try
		{
			final File propertiesFile = new File(PROPERTIES_FILE_NAME);
			if(!propertiesFile.exists())
			{
				throw new IOException();
			}

			reader = new FileReader(PROPERTIES_FILE_NAME);
			m_properties.load(reader);
		}
		catch(final IOException ex)
		{
			JOptionPane.showMessageDialog(null,
				"Unable to find properties file - loading default properties");
			m_properties = buildDefaultProperties();
			writeProperties(m_properties);
		}
		finally
		{
			Utilities.close(reader);
		}

		System.out.println();
		System.out.println("Current settings:");
		m_properties.list(System.out);
		System.out.println();
	}

	public static boolean writeAllVectorFiles()
	{
		return getAsBoolean(TAG_WRITE_ALL_VECTOR_FILES,
			DEFAULT_WRITE_ALL_VECTOR_FILES);
	}

	public static boolean writeEasyHardVectorFiles()
	{
		return getAsBoolean(TAG_WRITE_EASY_HARD_VECTOR_FILES,
			DEFAULT_WRITE_EASY_HARD_VECTOR_FILES);
	}

	public static boolean writeOutputFile()
	{
		return getAsBoolean(TAG_WRITE_OUTPUT_FILE, DEFAULT_WRITE_OUTPUT_FILE);
	}

	private static void writeProperties(final Properties p_properties)
	{
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(new File(PROPERTIES_FILE_NAME));
			p_properties.store(writer, "Settings file for DDAnalyzer.jar");
		}
		catch(final IOException ex)
		{
			JOptionPane.showMessageDialog(null,
				"Unable to write properties file: " + ex.getMessage());
		}
		finally
		{
			Utilities.close(writer);
		}
	}
}
