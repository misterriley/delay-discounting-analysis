/**
 * 
 */
package delayDiscounting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import types.ResponseType;

/**
 * @author Ringo
 * 
 */
public class DataLoading
{
	// tags unique to b-type response files
	private static final String	B_STIMULUS_RESPONSE_HEADER_TAG		= "stimulus.RESP";
	private static final String	B_STIMULUS_ID_HEADER_TAG			= "stimulus";
	private static final String	B_TRIAL_TYPE_TAG					= "Type";
	private static final String	B_RT_TAG							= "stimulus.RT";

	// tags shared between a-type and b-type response files
	private static final String	RESP_INPUT_DIR						= "To Analyze";
	private static final String	RESP_STIMULUS_RESPONSE_HEADER_TAG	= "Choice.RESP";
	private static final int	RESP_LINES_TO_SKIP					= 0;
	private static final String	RESP_CHOICE_1_TAG					= "AmountNow";
	private static final String	RESP_CHOICE_2_TAG					= "AmountLater";
	private static final String	RESP_CHOICE_1_DELAY_TAG				= "D1";
	private static final String	RESP_CHOICE_2_DELAY_TAG				= "D2";
	private static final String	RESP_K_TAG							= "k";
	private static final String	RESP_TRIAL_TYPE_TAG					= "Procedure[Block]";
	private static final String	RESP_ITI_TAG						= "ITI";
	private static final String	RESP_RT_TAG							= "Choice.RT";
	private static final String	RESP_ATTENTION_CHECK_KEYWORD		= "Null";
	private static final String	RESP_DELIMITER						= ",";
	private static final String	RESP_SUBJECT_ID_HEADER_TAG			= "Subject";
	private static final String	RESP_TIME_TAG						= "SessionTime";
	private static final String	RESP_DATE_TAG						= "SessionDate";
	private static final String	RESP_EXPERIMENT_ORDER_TAG			= "ExperimentName";
	private static final String	RESP_THROWAWAY_KEYWORD				= "SampleNoScan";
	private static final int	RESP_RT_MINIMUM_MS					= Settings.getRTMinimumMS();
	private static final String	RESP_YEAR_2_INDICATOR				= "Year2";
	private static final String	RESP_TRIAL_TAG						= "Trial";

	// tags used in the stimulus file
	private static final String	STIM_DATA_FILE						= "/stimuli_corrected3.3.15.txt";
	private static final String	STIM_DELIMITER						= "\t";
	private static final int	STIM_STIM_ID_INDEX					= 0;
	private static final int	STIM_CHOICE_1_INDEX					= 1;
	private static int			STIM_CHOICE_1_DELAY_INDEX			= -1;
	private static final int	STIM_CHOICE_2_INDEX					= 2;
	private static final int	STIM_CHOICE_2_DELAY_INDEX			= 3;
	private static final int	STIM_K_INDEX						= 4;

	// tags used in the subject file
	private static final String	SUBJ_DATA_FILE						= "/AllParticipantList.csv";
	private static final String	SUBJ_DELIMITER						= ",";
	private static final String	SUBJ_SUBJ_NUMBER_TAG				= "subject_number";
	private static final String	SUBJ_SEX_TAG						= "sex";
	private static final String	SUBJ_ADHD_TD_TAG					= "adhd_td_group";
	private static final String	SUBJ_YEAR_1_AGE_TAG					= "age_yr1_mri1";
	private static final String	SUBJ_YEAR_2_AGE_TAG					= "age_yr2_v1";

	private static ResponseType convertStringToResponse(
		final String p_stimulusResponseString)
	{
		if(p_stimulusResponseString.equals("")
			|| p_stimulusResponseString.equalsIgnoreCase("null"))
		{
			return ResponseType.NONE;
		}

		if(p_stimulusResponseString.equals("1"))
		{
			return ResponseType.SOONER;
		}

		if(p_stimulusResponseString.equals("2"))
		{
			return ResponseType.LATER;
		}

		throw new RuntimeException("Invalid response string: "
			+ p_stimulusResponseString);
	}

	private static boolean decideIfLieTrial(final int kIndex,
		final String[] bits)
	{
		final boolean isAttentionCheck = kIndex >= bits.length ? false : bits[kIndex].equals(RESP_ATTENTION_CHECK_KEYWORD);
		return isAttentionCheck;
	}

	private static int findIndex(final String[] p_array,
		final String p_searchItem)
	{
		final String searchItemInQuotes = "\"" + p_searchItem + "\"";

		for(int i = 0; i < p_array.length; i++)
		{
			if(p_array[i].equals(p_searchItem) ||
				p_array[i].equals(searchItemInQuotes))
			{
				return i;
			}
		}

		return -1;
	}

	private static Map<String, StimulusData> loadStimulusData()
	{
		final Map<String, StimulusData> ret = new HashMap<String, StimulusData>();
		BufferedReader reader = null;
		try
		{
			final URL toDisplay = MainController.class.getResource(STIM_DATA_FILE);

			reader = new BufferedReader(
				new InputStreamReader(toDisplay.openStream()));

			while(true)
			{
				final String line = reader.readLine();
				if(line == null)
				{
					break;
				}

				final String[] bits = line.split(STIM_DELIMITER);

				final String id = bits[STIM_STIM_ID_INDEX];
				final double choice1 = Utilities.parseDouble(bits[STIM_CHOICE_1_INDEX]);
				int choice1Delay = 0;
				if(STIM_CHOICE_1_DELAY_INDEX != -1)
				{
					choice1Delay = Utilities.parseInt(bits[STIM_CHOICE_1_DELAY_INDEX]);
				}

				final double choice2 = Utilities.parseDouble(bits[STIM_CHOICE_2_INDEX]);
				final int choice2Delay = Utilities.parseInt(bits[STIM_CHOICE_2_DELAY_INDEX]);

				final double k = Utilities.parseDouble(bits[STIM_K_INDEX]);

				final StimulusData sd = new StimulusData(id, choice1,
					choice1Delay, choice2, choice2Delay, k);
				ret.put(id, sd);
			}
		}
		catch(final IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			Utilities.close(reader);
		}

		return ret;
	}

	public static SubjectData[] loadSubjectAndResponseData()
	{
		final Map<Integer, SubjectData> subjectsMap = loadSubjectData();
		final Map<String, StimulusData> stimulusMap = loadStimulusData();

		final File dataDir = new File(RESP_INPUT_DIR);

		final JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(dataDir);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		final int retVal = chooser.showOpenDialog(null);

		if(retVal != JFileChooser.APPROVE_OPTION)
		{
			JOptionPane.showMessageDialog(null, "No files selected.  Quitting.");
			System.exit(0);
		}

		for(final File toRead: chooser.getSelectedFiles())
		{
			System.out.println();
			System.out.println("reading " + toRead.getName());
			int linesRead = readOneSubjectFile(subjectsMap, stimulusMap,
				toRead, "ASCII", false);
			if(linesRead == 0)
			{
				System.out.println("reading in Unicode");
				linesRead = readOneSubjectFile(subjectsMap, stimulusMap,
					toRead, "Unicode", true);
			}
			System.out.println(linesRead + " lines read");
		}

		final SubjectData[] ret = new SubjectData[subjectsMap.size()];
		subjectsMap.values().toArray(ret);
		Arrays.sort(ret);

		return ret;
	}

	private static Map<Integer, SubjectData> loadSubjectData()
	{
		final Map<Integer, SubjectData> ret = new HashMap<Integer, SubjectData>();

		BufferedReader reader = null;
		try
		{
			final URL dataFileURL = MainController.class.getResource(SUBJ_DATA_FILE);

			if(dataFileURL == null)
			{
				return ret;
			}

			reader = new BufferedReader(
				new InputStreamReader(dataFileURL.openStream()));

			final String header = reader.readLine();
			final String[] headerBits = header.split(SUBJ_DELIMITER);

			final int subjectIDIndex = findIndex(headerBits,
				SUBJ_SUBJ_NUMBER_TAG);
			final int sexIndex = findIndex(headerBits, SUBJ_SEX_TAG);
			final int adhdTdIndex = findIndex(headerBits, SUBJ_ADHD_TD_TAG);
			final int year1AgeIndex = findIndex(headerBits, SUBJ_YEAR_1_AGE_TAG);
			final int year2AgeIndex = findIndex(headerBits, SUBJ_YEAR_2_AGE_TAG);

			String line = null;
			while(true)
			{
				line = reader.readLine();
				if(line == null)
				{
					break;
				}

				final String[] bits = line.split(SUBJ_DELIMITER);
				if(bits.length <= 1)
				{
					continue;
				}

				final Integer subjectID = new Integer(bits[subjectIDIndex]);
				final String sex = bits[sexIndex];
				final String adhdTdGroup = bits[adhdTdIndex];
				double year1Age = -1;
				if(bits.length > year1AgeIndex)
				{
					final String year1AgeString = bits[year1AgeIndex];
					if(!year1AgeString.equals(""))
					{
						year1Age = Utilities.parseDouble(year1AgeString);
					}
				}

				double year2Age = -1;
				if(bits.length > year2AgeIndex)
				{
					final String year2AgeString = bits[year2AgeIndex];
					if(!year2AgeString.equals(""))
					{
						year2Age = Utilities.parseDouble(year2AgeString);
					}
				}

				final SubjectData subject = new SubjectData(subjectID, sex,
					adhdTdGroup);
				subject.getYear1Data().setAge(year1Age);
				subject.getYear2Data().setAge(year2Age);

				ret.put(subjectID, subject);
			}
		}
		catch(final IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			Utilities.close(reader);
		}

		return ret;
	}

	/**
	 * @param p_choice1
	 * @param p_choice2
	 * @return
	 */
	private static boolean messySanityCheck(final double p_choice1,
		final double p_choice2)
	{
		final int testInt1 = (int)(p_choice1 * 100);
		final int testInt2 = (int)(p_choice2 * 100);

		final boolean isMessy1 = ((testInt1 % 100) != 0);
		final boolean isMessy2 = ((testInt2 % 100) != 0);

		if(isMessy1 != isMessy2)
		{
			throw new RuntimeException("Sanity check failed: " + p_choice1
				+ " " + p_choice2);
		}

		return isMessy1;
	}

	private static int parseNumDays(final String p_string)
	{
		if(p_string.equalsIgnoreCase("NULL"))
		{
			return -1;
		}

		if(p_string.equals("Today"))
		{
			return 0;
		}

		final int weekIndex = p_string.indexOf("week");
		if(weekIndex == -1)
		{
			throw new RuntimeException(p_string
				+ ": expecting format 'x weeks'");
		}

		final String numberString = p_string.substring(0, weekIndex - 1);
		return Utilities.parseInt(numberString) * 7;
	}

	private static int parseTypeAFile(
		final Map<Integer, SubjectData> p_subjectsMap,
		final BufferedReader reader,
		final String[] headerBits, final int stimulusResponseIndex,
		final String p_sourceFile)
	{
		final int subjectIDIndex = findIndex(headerBits,
			RESP_SUBJECT_ID_HEADER_TAG);
		final int choice1DelayIndex = findIndex(headerBits,
			RESP_CHOICE_1_DELAY_TAG);
		final int choice1Index = findIndex(headerBits, RESP_CHOICE_1_TAG);
		final int choice2DelayIndex = findIndex(headerBits,
			RESP_CHOICE_2_DELAY_TAG);
		final int choice2Index = findIndex(headerBits, RESP_CHOICE_2_TAG);
		final int timeIndex = findIndex(headerBits, RESP_TIME_TAG);
		final int dateIndex = findIndex(headerBits, RESP_DATE_TAG);
		final int kIndex = findIndex(headerBits, RESP_K_TAG);
		final int trialTypeIndex = findIndex(headerBits,
			RESP_TRIAL_TYPE_TAG);
		final int orderIndex = findIndex(headerBits,
			RESP_EXPERIMENT_ORDER_TAG);
		final int itiIndex = findIndex(headerBits, RESP_ITI_TAG);
		final int rtIndex = findIndex(headerBits, RESP_RT_TAG);
		final int trialIndex = findIndex(headerBits, RESP_TRIAL_TAG);

		int lineCounter = 0;
		int timeCounter = 0;
		SubjectData currentSubject = null;

		try
		{

			while(true)
			{
				String line = null;
				try
				{
					line = reader.readLine();
				}
				catch(final IOException ex)
				{
					ex.printStackTrace();
				}

				if(line == null)
				{
					break;
				}

				final String[] bits = line.split(RESP_DELIMITER);

				final String orderString = bits[orderIndex];
				final int orderStringUnderscoreIndex = orderString.indexOf("_");
				final String orderIndicator = orderString.substring(orderStringUnderscoreIndex + 1);

				final int year = orderString.contains(RESP_YEAR_2_INDICATOR) ? 2 : 1;
				if(orderString.contains("DelayDiscountingPractice"))
				{
					continue;
				}
				final String throwawayIndicator = bits[trialTypeIndex];
				final boolean isThrowawayTrial = throwawayIndicator.equals(RESP_THROWAWAY_KEYWORD);
				final Integer subjectID = new Integer(bits[subjectIDIndex]);
				final int reactionTime = Utilities.parseInt(bits[rtIndex]);
				final String stimulusResponseString = bits[stimulusResponseIndex];
				final ResponseType response = convertStringToResponse(stimulusResponseString);
				final double choice1 = Utilities.parseDouble(bits[choice1Index]);
				final int choice1Delay = parseNumDays(bits[choice1DelayIndex]);
				final double choice2 = Utilities.parseDouble(bits[choice2Index]);
				final int choice2Delay = parseNumDays(bits[choice2DelayIndex]);
				final boolean isAttentionCheck = decideIfLieTrial(kIndex, bits);
				final int trialNumber = Utilities.parseInt(bits[trialIndex]);

				double k = -1;
				if(!isAttentionCheck && !bits[kIndex].equals(""))
				{
					k = Utilities.parseDouble(bits[kIndex]);
				}

				final boolean isMessyTrial = (bits[trialTypeIndex].startsWith("Messy"));
				final boolean messySanityCheck = messySanityCheck(choice1,
					choice2);

				final String timeString = bits[timeIndex];
				final String dateString = bits[dateIndex];
				final int itiMillis = Utilities.parseInt(bits[itiIndex]);
				final int iti = itiMillis / 1000;

				SubjectData subjectData = p_subjectsMap.get(subjectID);
				if(subjectData == null)
				{
					subjectData = new SubjectData(subjectID);
					p_subjectsMap.put(subjectID, subjectData);
				}

				if(trialNumber == 1)
				{
					if(subjectData != currentSubject)
					{
						currentSubject = subjectData;
						timeCounter = 0;
					}

					timeCounter += 4;
				}

				final SubjectYearData syd = subjectData.getYearData(year);
				syd.setTimeString(timeString);
				syd.setDateString(dateString);
				syd.setSourceFile(p_sourceFile);
				syd.setTypeA(true);

				syd.setOrderIndicator(orderIndicator);

				final boolean isValidTrial = (!isAttentionCheck
					&& response != ResponseType.NONE
					&& reactionTime >= RESP_RT_MINIMUM_MS && !isThrowawayTrial);

				if(!isThrowawayTrial && isValidTrial
					&& isMessyTrial != messySanityCheck)
				{
					throw new RuntimeException("Sanity check failed: "
						+ choice1
						+ " " + choice2);
				}

				syd.addResponse(choice1, choice1Delay, choice2,
					choice2Delay, response, isAttentionCheck,
					isMessyTrial, timeCounter, k, isValidTrial, reactionTime);

				lineCounter++;
				System.out.println(lineCounter);
				timeCounter += (10 + iti);
			}

		}
		catch(final NumberFormatException ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return lineCounter;
	}

	private static int parseTypeBFile(
		final Map<Integer, SubjectData> p_subjectsMap,
		final Map<String, StimulusData> stimulusMap,
		final BufferedReader reader, final String[] headerBits,
		final String p_sourceFile)
		throws IOException
	{
		final int stimulusResponseIndex = findIndex(headerBits,
			B_STIMULUS_RESPONSE_HEADER_TAG);
		final int subjectIDIndex = findIndex(headerBits,
			RESP_SUBJECT_ID_HEADER_TAG);
		final int timeIndex = findIndex(headerBits, RESP_TIME_TAG);
		final int dateIndex = findIndex(headerBits, RESP_DATE_TAG);
		final int trialTypeIndex = findIndex(headerBits,
			B_TRIAL_TYPE_TAG);
		final int orderIndex = findIndex(headerBits,
			RESP_EXPERIMENT_ORDER_TAG);
		final int stimulusIndex = findIndex(headerBits,
			B_STIMULUS_ID_HEADER_TAG);
		final int rtIndex = findIndex(headerBits, B_RT_TAG);
		final int year = p_sourceFile.contains(RESP_YEAR_2_INDICATOR) ? 2 : 1;

		int lineCounter = 0;
		while(true)
		{
			final String line = reader.readLine();
			if(line == null)
			{
				break;
			}

			final String[] bits = line.split(RESP_DELIMITER);

			final String throwawayIndicator = bits[trialTypeIndex];
			final Integer subjectID = new Integer(bits[subjectIDIndex]);
			final int reactionTime = Utilities.parseInt(bits[rtIndex]);
			final String stimulusID = bits[stimulusIndex];
			final StimulusData stimulus = stimulusMap.get(stimulusID);
			final double choice1 = stimulus.getChoice1();
			final int choice1Delay = stimulus.getChoice1Delay();
			final double choice2 = stimulus.getChoice2();
			final int choice2Delay = stimulus.getChoice2Delay();
			final double k = stimulus.getK();
			final ResponseType response = convertStringToResponse(bits[stimulusResponseIndex]);
			final boolean isMessyTrial = (bits[trialTypeIndex].startsWith("Messy"));
			final String timeString = bits[timeIndex];
			final String dateString = bits[dateIndex];
			final String orderString = bits[orderIndex];

			SubjectData subjectData = p_subjectsMap.get(subjectID);
			if(subjectData == null)
			{
				subjectData = new SubjectData(subjectID);
				p_subjectsMap.put(subjectID, subjectData);
			}

			final SubjectYearData syd = subjectData.getYearData(year);
			syd.setTimeString(timeString);
			syd.setDateString(dateString);
			syd.setSourceFile(p_sourceFile);
			syd.setTypeA(false);

			final int orderStringUnderscoreIndex = orderString.lastIndexOf("_");
			final String orderIndicator = orderString.substring(orderStringUnderscoreIndex + 1);
			syd.setOrderIndicator(orderIndicator);

			final boolean isValidTrial = (response != ResponseType.NONE
				&& reactionTime >= RESP_RT_MINIMUM_MS && !throwawayIndicator.equals(RESP_THROWAWAY_KEYWORD));

			syd.addResponse(choice1, choice1Delay, choice2,
				choice2Delay, response, false, isMessyTrial, -1, k,
				isValidTrial, reactionTime);
			lineCounter++;
		}

		return lineCounter;
	}

	private static int readOneSubjectFile(
		final Map<Integer, SubjectData> p_subjectsMap,
		final Map<String, StimulusData> p_stimulusMap, final File p_sourceFile,
		final String p_encoding, final boolean p_showException)
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(p_sourceFile), p_encoding));
			skipLines(reader, RESP_LINES_TO_SKIP);
			final String header = reader.readLine();
			final String[] headerBits = header.split(RESP_DELIMITER);

			final int stimulusResponseIndex = findIndex(headerBits,
				RESP_STIMULUS_RESPONSE_HEADER_TAG);
			if(stimulusResponseIndex != -1)
			{
				return parseTypeAFile(p_subjectsMap, reader, headerBits,
					stimulusResponseIndex, p_sourceFile.getName());
			}

			return parseTypeBFile(p_subjectsMap, p_stimulusMap, reader,
				headerBits,
				p_sourceFile.getName());
		}
		catch(final Exception ex)
		{
			if(p_showException)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error: unable to read "
					+ p_sourceFile.getName());
			}
		}
		finally
		{
			Utilities.close(reader);
		}

		return 0;
	}

	private static void skipLines(final BufferedReader p_reader,
		final int p_linesToSkip)
	{
		for(int i = 0; i < p_linesToSkip; i++)
		{
			try
			{
				p_reader.readLine();
			}
			catch(final IOException ioex)
			{
				ioex.printStackTrace();
			}
		}
	}
}
