DATA_FOLDER <- "C:/Users/Ringo/Documents/Eclipse projects/MIND/"

RESPONSE_FILE <- "DDResponses.csv"
MATCHED_DATA_FILE <- "qry_match_included_adhd_td.csv"

GROUP_OUTPUT_FILE <- "DDGroupAnalysis.csv"
INDIV_OUTPUT_FILE <- "DDIndividualAnalysis.csv"

HEADER_END <- "Discount Model,Utility Model,Comparison Model,BIC,Log Likelihood,# Data Points,# Parameters,Parameters"
GROUP_HEADER_START <- "Study Group,Analysis Type,ADHD/TD Group,Sex"
INDIV_HEADER_START <- "ID,ADHD/TD Group,Sex,Age"

responses <- read.table(paste(DATA_FOLDER, RESPONSE_FILE, sep = ""),
			header=TRUE, 
			sep=",")

matchedData <- read.table(paste(DATA_FOLDER, MATCHED_DATA_FILE, sep = ""), 
			header=TRUE, 
			sep=",")
matchedIDs <- matchedData[,1]

writeHeaderToFile <- function(p_headerStart, p_file)
{
	sink(p_file, append = FALSE, split = TRUE)
	toWrite <- paste(paste(p_headerStart, HEADER_END, sep=","), "\n", sep="")
	cat(toWrite)
	sink(NULL)
}

writeIndivHeaderToFile <- function(p_file)
{
	writeHeaderToFile(INDIV_HEADER_START, p_file)
}

writeGroupHeaderToFile <- function(p_file)
{
	writeHeaderToFile(GROUP_HEADER_START, p_file)
}

writeOutputToFile <- function(p_output, p_file)
{
	sink(p_file, append = TRUE, split = TRUE)
	cat(p_output)
	sink(NULL)
}

writeLineToFile <- function(p_output, p_file)
{
	line <- paste(p_output, "\n")
	writeOutputToFile(line, p_file)
}