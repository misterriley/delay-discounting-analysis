sink(NULL)
source("DDFunctions.R")
source("DDIO.R")
source("DDResponseSets.R")
source("DDVectors.R")

OPTIM_METHOD <- "L-BFGS-B" # "Nelder-Mead" "L-BFGS-B" 
OUT_OF_BOUNDS_VALUE <- -1000000
NDEP <- .001

m_functionCount <- 0
m_numDotsInRow <- 0
MAX_DOTS_IN_ROW <- 50

checkForNA <- function(p_vector)
{
	if(any(is.na(p_vector)))
	{
		stop("NA value in parameters")
	}
}

optimizeDiscountingModel <- function(p_modelCombination, 
					p_survivingResponses, 
					p_participantIDs,
					p_isGroupAnalysis,
					p_outputLineStart)
{
	assign("m_functionCount", 0, envir = .GlobalEnv)
	assign("m_numDotsInRow", 0, envir = .GlobalEnv)

	cat("\n --- new model --- \n")

	checkForNA(p_modelCombination)
	numResponses <- nrow(p_survivingResponses)
	if(numResponses == 0)
	{
		return(NULL)
	}	

	discountModel <- p_modelCombination["discountModel"]
	utilityModel <- p_modelCombination["utilityModel"]
	compModel <- p_modelCombination["comparisonModel"]

	startingVector <- getStartingVector(discountModel, 
					utilityModel, 
					compModel, 
					p_participantIDs)
	minVector <- getMinVector(discountModel, 
					utilityModel, 
					compModel, 
					p_participantIDs)
	maxVector <- getMaxVector(discountModel, 
					utilityModel, 
					compModel,
					p_participantIDs)
	numParams <- length(startingVector)

	print(p_modelCombination)
	print(paste("data points:", numResponses))
	print(paste("params:", numParams))
	print(paste("optimization method:", OPTIM_METHOD))

	minPassVector <- if(OPTIM_METHOD == "L-BFGS-B") minVector else -Inf 
	maxPassVector <- if(OPTIM_METHOD == "L-BFGS-B") maxVector else Inf

	#gradient <- fitGradient(p_responses = p_survivingResponses, 
	#			getUtilityFunction(utilityModel),
	#			getDiscountFunction(discountModel),
	#			getComparisonFunction(compModel),
	#			minVector,
	#			maxVector,
	#			p_isGroupAnalysis,
	#			p_par = startingVector)*/
	#
	#parscale <- abs(1/gradient)

	# optimization function
	results <- 
		optim(
		
			# starting point and function to optimize
			par = startingVector, 
			fn = fit,
			
			# data passed to the function
			p_responses = p_survivingResponses,
			p_utilityFunction = getUtilityFunction(utilityModel),
			p_discountFunction = getDiscountFunction(discountModel),
			p_compFunction = getComparisonFunction(compModel),
			p_minVector = minVector,
			p_maxVector = maxVector,
			p_useAlphaIs = p_isGroupAnalysis,
		
			# details about how to optimize
			method = OPTIM_METHOD,
			lower = minPassVector,
			upper = maxPassVector,
			control = list(fnscale = -1, # makes this a maximization function
						#parscale = parscale,
						trace = 6,
						REPORT = 1,
						maxit = 500
					)
		)

	BIC <- -2 * results$value + numParams * log(numResponses)

	cat("\n")
	print(paste("log likelihood of model:", results$value))
	print(paste("df:", numParams))
	print(paste("BIC:", BIC))
	print(paste("Convergence code:", results$convergence))
	cat("\n")

	lineToWrite <- paste(p_outputLineStart,
					discountModel,
					utilityModel, 
					compModel,
					BIC,
					results$value,
					numResponses,
					numParams,
					paste(results$par, collapse=","), 
					sep=",")

	if(p_isGroupAnalysis)
	{
		writeLineToFile(lineToWrite, GROUP_OUTPUT_FILE)	
	}
	else
	{
		writeLineToFile(lineToWrite, INDIV_OUTPUT_FILE)	
	}
}

# calculates the log likelihood of the given model (utility, discount and 
# comparison function) given the input parameters (par).  This number will 
# usually be somewhere around -1 * nrow(p_responses) / 2, give or take.  

fit <- function(p_responses, 
		p_utilityFunction, 
		p_discountFunction, 
		p_compFunction,
		p_minVector,
		p_maxVector, 
		p_useAlphaIs,
		par)
{	
	checkForNA(par)
	if(OPTIM_METHOD != "L-BFGS-B")
	{
		# L-BFGS-B is a bounded search.  If we are using another method,
		# out of bounds values should return something indicating this 
		# point shouldn't work

		if(any(par < p_minVector | par > p_maxVector))
		{
			cat("O")
			checkForNewRow()
			return (OUT_OF_BOUNDS_VALUE)
		}
	}
	
	logLikelihoods <- apply(p_responses,
					1, 
					logLikelihood, 
					p_par = par, 
					p_utilityFunction,
					p_discountFunction,
					p_compFunction = p_compFunction,
					p_useAlphaIs = p_useAlphaIs)
	
	ret <- sum(logLikelihoods)

	if(is.infinite(ret) | is.nan(ret))
	{
		ret <- OUT_OF_BOUNDS_VALUE
	}

	assign("m_functionCount", (m_functionCount + 1), envir = .GlobalEnv)

	cat(".")
	checkForNewRow()
	print(paste(ret, m_functionCount))
	#print(par)
	
	return (ret)
}

checkForNewRow <- function()
{
	assign("m_numDotsInRow", (m_numDotsInRow + 1), envir = .GlobalEnv)

	if(m_numDotsInRow == MAX_DOTS_IN_ROW)
	{
		cat("\n")
		assign("m_numDotsInRow", 0, envir = .GlobalEnv)	
	}	
}

fitGradient <- function(p_responses, 
		p_utilityFunction, 
		p_discountFunction, 
		p_compFunction,
		p_minVector,
		p_maxVector, 
		p_useAlphaIs,
		p_par)
{
	value1 <- fit(p_responses,
			p_utilityFunction,
			p_discountFunction,
			p_compFunction,
			p_minVector,
			p_maxVector,
			p_useAlphaIs,
			par = p_par)

	ret <- sapply(1:length(p_par), 
			ddVar,
			p_responses = p_responses, 
			p_utilityFunction, 
			p_discountFunction, 
			p_compFunction,
			p_minVector,
			p_maxVector,
			p_useAlphaIs,
			p_par,
			p_pointValue = value1)

	return (ret)
}

ddVar <- function(p_index,
			p_responses,
			p_utilityFunction,
			p_discountFunction,
			p_compFunction,
			p_minVector,
			p_maxVector,
			p_useAlphaIs,
			p_par,
			p_pointValue)
{
	p_par[p_index] <- (p_par[p_index] + NDEP)

	value <- fit(p_responses,
			p_utilityFunction,
			p_discountFunction,
			p_compFunction,
			p_minVector,
			p_maxVector,
			p_useAlphaIs,
			p_par)

	ret <- (value - p_pointValue)/NDEP
	return (ret)
}

logLikelihood <- function(p_response, 
			p_par, 
			p_utilityFunction, 
			p_discountFunction, 
			p_compFunction, 
			p_useAlphaIs)
{
	value1 <- as.numeric(p_response["Option.1"])
	value2 <- as.numeric(p_response["Option.2"])

	utility1 <- do.call(p_utilityFunction, list(p_x = value1, p_par = p_par))
	utility2 <- do.call(p_utilityFunction, list(p_x = value2, p_par = p_par))

	time1 <- as.numeric(p_response["Option.1.Delay"])
	time2 <- as.numeric(p_response["Option.2.Delay"])

	discount1 <- 0
	discount2 <- 0
	
	if(isTwoFactorDiscount(p_discountFunction))
	{
		discount1 <- do.call(p_discountFunction, 
					list(p_ts = 0, p_tl = time1, p_par))
		discount2 <- discount1 * do.call(p_discountFunction, 
							list(p_ts = time1, 
								p_tl = time2, 
								p_par))
	}
	else
	{
		discount1 <- do.call(p_discountFunction, list(time1, p_par))
		discount2 <- do.call(p_discountFunction, list(time2, p_par))
	}	

	ssValue = utility1 * discount1 
	llValue = utility2 * discount2
	choice = p_response["Choice"]
	id = p_response["ID"]

	maxVal <- max(llValue, ssValue)
	minVal <- min(llValue, ssValue)
	
	choiceVal <- 0
	alternativeVal <- 0
	if(choice == "1")
	{
		# user chose ss for this trial
		choiceVal <- ssValue
		alternativeVal <- llValue
	}
	else if(choice == "2")
	{
		# user chose ll for this trial
		choiceVal <- llValue
		alternativeVal <- ssValue
	}
	else
	{
		stop(paste("Unexpected choice: ", p_values.Choice))
	}

	sens <- p_par[SENS_NAME]

	# use the passed in comparison function to measure the difference between what was chosen and the 
	# alternative
	diff <- do.call(p_compFunction, list(p_choiceVal = choiceVal, 
							p_alternativeVal = alternativeVal, 
							p_par = p_par))
	alphaI <- 0
	if(p_useAlphaIs)
	{
		alphaI <- p_par[id]
	}

	# this is the logarithm of a standard logistic calculation
	xVal <- diff * sens + alphaI
	logLogistic <- xVal - log(exp(xVal) + 1)

	return(logLogistic)
}

responseValues <- function(p_response, 
				p_utilityFunction, 
				p_discountFunction, 
				p_par)
{
	value1 <- as.numeric(p_response["Option.1"])
	value2 <- as.numeric(p_response["Option.2"])

	utility1 <- do.call(p_utilityFunction, list(p_x = value1, p_par = p_par))
	utility2 <- do.call(p_utilityFunction, list(p_x = value2, p_par = p_par))

	time1 <- as.numeric(p_response["Option.1.Delay"])
	time2 <- as.numeric(p_response["Option.2.Delay"])

	discount1 <- 0
	discount2 <- 0
	
	if(isTwoFactorDiscount(p_discountFunction))
	{
		discount1 <- do.call(p_discountFunction, 
					list(p_ts = 0, p_tl = time1, p_par))
		discount2 <- discount1 * do.call(p_discountFunction, 
							list(p_ts = time1, 
								p_tl = time2, 
								p_par))
	}
	else
	{
		discount1 <- do.call(p_discountFunction, list(time1, p_par))
		discount2 <- do.call(p_discountFunction, list(time2, p_par))
	}	

	ret <- list(ssVal = utility1 * discount1, 
			llVal = utility2 * discount2, 
			choice = as.numeric(p_response["Choice"]),
			id = p_response["ID"])

	return (ret)
}

analyzeOneID <- function(p_id)
{
	print(paste("ID:", p_id))
	responseSet <- getResponseSetFromIDAndYear(responses, p_id, 1)

	row <- matchedData[which(matchedData$subject_number == p_id),]
	diagnosisGroup <- row$adhd_td_group
	sex <- row$sex
	age <- row$age_yr1_pomp
	outputLineStart <- paste(p_id, diagnosisGroup, sex, age, sep=",") 

	apply(MODEL_COMBINATIONS, 
		1, 
		optimizeDiscountingModel, 
		responseSet,
		NULL,
		FALSE,
		outputLineStart)
}

analyzeOneParticipantSet <- function(p_participantSet)
{
	cat(" --- analyzing new participant set --- \n")
	print(p_participantSet)

	responseSet <- getResponseSet(responses, p_participantSet)
	idsInSet <- apply(responseSet, 1, getID)
	uniqueIDs <- unique(idsInSet)
	outputLineStart <- paste(p_participantSet, collapse = ",")

	apply(MODEL_COMBINATIONS,
		1,
		optimizeDiscountingModel,
		responseSet,
		uniqueIDs,
		TRUE,
		outputLineStart)
}

getID <- function(p_response)
{
	return (p_response["ID"])
}

#writeIndivHeaderToFile(INDIV_OUTPUT_FILE)
#sapply(matchedIDs, analyzeOneID)

writeGroupHeaderToFile(GROUP_OUTPUT_FILE)
apply(PARTICIPANT_SETS, 1, analyzeOneParticipantSet)

sink(NULL)