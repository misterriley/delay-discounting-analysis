/**
 * 
 */
package delayDiscounting;

import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import types.AnalysisType;
import types.ResponseOutputDataType;

public class DataWriting
{
	private static final String	VECTOR_DIR				= "Easy vs. hard vectors";
	private static final String	VECTOR_EASY_LABEL		= "easy";
	private static final String	VECTOR_HARD_LABEL		= "hard";
	private static final String	VECTOR_ALL_LABEL		= "all";
	private static final String	VECTOR_INVALID_LABEL	= "invalid";
	private static final String	VECTOR_MID_LABEL		= "mid";

	private static final String	RESP_OUT_FILE			= "DDResponses.csv";
	private static final String	RESP_OUT_FILE_DELIMITER	= ",";

	private static Set<String>	writtenVectorFiles		= new HashSet<String>();

	/**
	 * @param p_response
	 * @param p_year
	 * @param p_subject
	 * @return
	 */
	private static String buildOutputString(final ResponseData p_response)
	{
		String ret = "";

		boolean firstItem = true;
		for(final ResponseOutputDataType type: ResponseOutputDataType.values())
		{
			final String data = p_response.getData(type);
			if(!firstItem)
			{
				ret += RESP_OUT_FILE_DELIMITER;
			}

			ret += data;

			firstItem = false;
		}

		return ret;
	}

	public static void writeAllVector(final int[] p_vector,
		final SubjectData p_subjectData)
	{
		writeVector(p_vector, p_subjectData, VECTOR_ALL_LABEL);
	}

	public static void writeEasyVector(final int[] p_easyVector,
		final SubjectData p_subjectData)
	{
		writeVector(p_easyVector, p_subjectData, VECTOR_EASY_LABEL);
	}

	public static void writeHardVector(final int[] p_hardVector,
		final SubjectData p_subjectData)
	{
		writeVector(p_hardVector, p_subjectData, VECTOR_HARD_LABEL);
	}

	public static void writeInvalidVector(final int[] p_omittedVector,
		final SubjectData p_subjectData)
	{
		writeVector(p_omittedVector, p_subjectData, VECTOR_INVALID_LABEL);
	}

	public static void writeMidVector(final int[] p_midVector,
		final SubjectData p_subjectData)
	{
		writeVector(p_midVector, p_subjectData, VECTOR_MID_LABEL);
	}

	public static void writeOutputFile(final SubjectOutputFile p_outputFile)
	{
		p_outputFile.prepareToWrite();

		if(Settings.runKAnalysis())
		{
			Toolkit.getDefaultToolkit().beep();
		}

		BufferedWriter writer = null;
		final String outputFileName = "output_"
			+ System.currentTimeMillis() + ".csv";
		try
		{
			writer = new BufferedWriter(new FileWriter(outputFileName));
			p_outputFile.writeOutput(writer);

			final String absoluteFilePath = new File(outputFileName).getAbsolutePath();
			System.out.println("Successfully wrote analysis file: "
				+ absoluteFilePath);
		}
		catch(final IOException ioex)
		{
			JOptionPane.showMessageDialog(null, "Unable to write to "
				+ outputFileName);
		}
		finally
		{
			Utilities.close(writer);
		}
	}

	public static void writeResponsesFile(final SubjectData[] p_subjects)
	{
		BufferedWriter writer = null;

		try
		{
			writer = new BufferedWriter(new FileWriter(new File(RESP_OUT_FILE)));

			final String header = buildOutputString(new ResponseOutputHeaderRow());
			writer.write(header + "\r\n");

			for(final SubjectData subject: p_subjects)
			{
				for(int year = 1; year <= 2; year++)
				{
					final ArrayList<ResponseData> validResponses = subject.getYearData(
						year).getResponses(
						AnalysisType.ALL_ALL, true);

					for(final ResponseData response: validResponses)
					{
						final String output = buildOutputString(response);
						writer.write(output + "\r\n");
					}
				}
			}
		}
		catch(final IOException ioex)
		{
			ioex.printStackTrace();
		}
		finally
		{
			Utilities.close(writer);
		}
	}

	private static void writeVector(final int[] p_vector,
		final SubjectData p_subjectData, final String p_vectorLabel)
	{
		final File vectorDir = new File(VECTOR_DIR);
		if(!vectorDir.exists())
		{
			vectorDir.mkdir();
		}
		final String vectorFileName = p_subjectData.getSubjectID() + "_"
			+ p_vectorLabel + ".txt";
		final File vectorFile = new File(vectorDir, vectorFileName);

		if(writtenVectorFiles.contains(vectorFile))
		{
			throw new RuntimeException("Collision on ID "
				+ p_subjectData.getSubjectID());
		}

		System.out.println("Writing " + vectorFileName);
		writtenVectorFiles.add(vectorFileName);

		String toWrite = "";
		for(final int timeIndex: p_vector)
		{
			toWrite += (timeIndex + " ");
		}

		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(vectorFile));
			writer.write(toWrite);
		}
		catch(final IOException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			Utilities.close(writer);
		}
	}
}
