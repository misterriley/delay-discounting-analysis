/**
 * 
 */
package delayDiscounting;

import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import types.AnalysisType;

/**
 * @author Ringo
 * 
 */
public class MainController
{
	private static final boolean	WRITE_RESPONSES_FILE	= true;

	public static void main(final String[] p_args)
	{
		if(System.console() == null)
		{
			JOptionPane.showMessageDialog(null,
				"Console unavailable - no text will be shown");
		}

		final SubjectData[] subjects = DataLoading.loadSubjectAndResponseData();
		writeOutputFile(subjects);
		writeVectorFiles(subjects);
		writeResponseFile(subjects);
		System.out.println();
		System.out.println("Finished analysis - close command prompt when ready");
		if(Settings.runKAnalysis())
		{
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private static void writeOutputFile(final SubjectData[] p_subjects)
	{
		if(!Settings.writeOutputFile())
		{
			return;
		}

		int workUnits = 0;

		final SubjectOutputFile ret = new SubjectOutputFile();
		for(final SubjectData subject: p_subjects)
		{
			for(int year = 1; year <= 2; year++)
			{
				final int responses = subject.getYearData(year).getAllValidResponses().size();
				if(responses == 0)
				{
					continue;
				}

				workUnits += responses * 3;

				if(subject.getYearData(year).kResultShouldExist(
					AnalysisType.ALL_DELAYED))
				{
					workUnits += responses;
				}

				final SubjectOutputRow row = new SubjectOutputRow(
					subject.getYearData(year));
				ret.addRow(row);
			}
		}

		final long startTime = System.currentTimeMillis();

		int completedWorkUnits = 0;
		for(final SubjectData subject: p_subjects)
		{
			for(final AnalysisType type: AnalysisType.values())
			{
				for(int year = 1; year <= 2; year++)
				{
					if(subject.getYearData(year).kResultShouldExist(type)
						&& !subject.getYearData(year).getResponses(type, true).isEmpty())
					{
						subject.getYearData(year).ensureKValuesArePresent(type);
						final ArrayList<ResponseData> values = subject.getYearData(
							year).getResponses(
							type, true);
						completedWorkUnits += values.size();
						final long passedMillis = System.currentTimeMillis()
							- startTime;

						final double percentComplete = ((double)completedWorkUnits)
							/ workUnits;
						final long expectedTotalMillis = (long)(passedMillis / percentComplete);
						final long expectedRemainingMillis = expectedTotalMillis
							- passedMillis;

						System.out.println(percentComplete * 100 + "% complete");
						System.out.println(TimeDisplay.getTimeDisplay(expectedTotalMillis)
							+ " expected total run time");
						System.out.println(TimeDisplay.getTimeDisplay(passedMillis)
							+ " elapsed");
						System.out.println(TimeDisplay.getTimeDisplay(expectedRemainingMillis)
							+ " remaining");
						System.out.println();
					}
				}
			}
		}

		DataWriting.writeOutputFile(ret);
	}

	/**
	 * @param p_subjects
	 */
	private static void writeResponseFile(final SubjectData[] p_subjects)
	{
		if(WRITE_RESPONSES_FILE)
		{
			DataWriting.writeResponsesFile(p_subjects);
		}
	}

	private static void writeVectorFiles(final SubjectData[] p_subjects)
	{
		if(!Settings.writeEasyHardVectorFiles()
			&& !Settings.writeAllVectorFiles())
		{
			return;
		}

		for(final SubjectData subject: p_subjects)
		{
			if(subject.getYear1Data().isTypeA())
			{
				if(Settings.runKAnalysis()
					&& Settings.writeEasyHardVectorFiles())
				{
					final EasyVsHardVector vector = new EasyVsHardVector(
						subject);
					vector.writeVectorFiles();
				}

				if(Settings.writeAllVectorFiles())
				{
					final AllVector vector = new AllVector(subject);
					vector.writeVectorFile();
				}
			}
		}
	}
}
