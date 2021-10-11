STUDY_GROUPS <- c("MINT_1") # "ALL", "MINT", "COMP", "MINT_2"
ANALYSIS_TYPES <- c("ALL") # "DELAYED", "IMMEDIATE", "MESSY", "ROUND"
DIAGNOSIS_GROUPS <- c("ADHD_MATCHED", "TD_MATCHED", "ALL_MATCHED") #, "ALL", "ADHD", "TD") 
GENDERS <- c("ALL") # "MALE", "FEMALE"

PARTICIPANT_SETS <- expand.grid(studyGroup = STUDY_GROUPS,
				analysisType = ANALYSIS_TYPES,
				diagnosisGroup = DIAGNOSIS_GROUPS,
				gender = GENDERS)

getResponseSetFromIDAndYear <- function(p_allResponses, p_id, p_year)
{
	return(p_allResponses[which(responses$ID == p_id &
					responses$Year == p_year),])
}

getResponseSet <- function(p_allResponses, p_participantSet)
{
	survivingResponses <- p_allResponses
	studyGroup <- p_participantSet["studyGroup"]
	analysisType <- p_participantSet["analysisType"]
	diagnosisGroup <- p_participantSet["diagnosisGroup"]
	gender <- p_participantSet["gender"]

	if(studyGroup == "ALL")
	{
		# do nothing
	}
	else if(studyGroup == "COMP")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ID < 1000),]	
	}
	else if(studyGroup == "MINT")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ID >= 1000),]
	}
	else if(studyGroup == "MINT_1")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ID >= 1000 & 
							survivingResponses$Year == 1),]		
	}
	else if(studyGroup == "MINT_2")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ID >= 1000 & 
							survivingResponses$Year == 2),]
	}
	else
	{
		stop(paste("unexpected study group type ", studyGroup))
	}

	if(analysisType == "ALL")
	{
		# do nothing
	}
	else if(analysisType == "DELAYED")
	{
		if(studyGroup == "COMP")
		{
			return(NULL)
		}
	
		survivingResponses <- 
			survivingResponses[which(survivingResponses$Delayed. == "true"),]	
	}
	else if(analysisType == "IMMEDIATE")
	{
		if(studyGroup == "COMP")
		{
			return(NULL)
		}
		survivingResponses <- 
			survivingResponses[which(survivingResponses$Delayed. == "false"),]
	}
	else if(analysisType == "MESSY")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$Messy. == "true"),]		
	}
	else if(analysisType == "ROUND")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$Messy. == "false"),]
	}
	else
	{
		stop(paste("unexpected analysis type:", analysisType))
	}
	
	if(gender == "ALL")
	{
		# do nothing
	}
	else if(gender == "MALE")
	{
		survivingResponses <- 
			survivingResponses[which(p_survivingResponses$Sex == "Male"),]	
	}
	else if(gender == "FEMALE")
	{
		survivingResponses <- 
			survivingResponses[which(p_survivingResponses$Sex == "Female"),]
	}	
	else
	{
		stop(paste("unexpected gender:", gender))
	}

	if(diagnosisGroup == "ALL")
	{
		# do nothing
	}
	else if(diagnosisGroup == "ALL_MATCHED")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ID %in% matchedIDs),]	
	}
	else if(diagnosisGroup == "ADHD")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ADHD.TD.Group == "ADHD"),]	
	}
	else if(diagnosisGroup == "TD")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ADHD.TD.Group == "TD"),]
	}
	else if(diagnosisGroup == "ADHD_MATCHED")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ADHD.TD.Group == "ADHD" & 
							survivingResponses$ID %in% matchedIDs),]	
	}
	else if(diagnosisGroup == "TD_MATCHED")
	{
		survivingResponses <- 
			survivingResponses[which(survivingResponses$ADHD.TD.Group == "TD" & 
							survivingResponses$ID %in% matchedIDs),]
	}
	else
	{
		stop(paste("unexpected diagnosis group:", diagnosisGroup))
	}

	return (survivingResponses)
}