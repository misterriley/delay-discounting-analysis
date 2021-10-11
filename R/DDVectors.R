SENS_NAME <- "SENSITIVITY MULTIPLIER"
SENS_MIN <- 0
SENS_MAX <- Inf
SENS_START <- .2

BETA_NAME <- "BETA"
BETA_MIN <- 0
BETA_MAX <- 1
BETA_START <- .7

DELTA_NAME <- "DELTA"
DELTA_MIN <- 0
DELTA_MAX <- 1
DELTA_START <- .9

SENS_EXP_NAME <- "SENSITIVITY EXPONENT"
SENS_EXP_MIN <- 0
SENS_EXP_MAX <- 1
SENS_EXP_START <- .8

DISC_EXP_NAME <- "DISCOUNT EXPONENT"
DISC_EXP_MIN <- 0
DISC_EXP_MAX <- Inf
DISC_EXP_START <- 1

SUB_ADD_EXP_NAME <- "SUBADDITIVE EXPONENT"
SUB_ADD_EXP_MIN <- 0
SUB_ADD_EXP_MAX <- 1
SUB_ADD_EXP_START <- .8

SUP_ADD_EXP_NAME <- "SUPERADDITIVE EXPONENT"
SUP_ADD_EXP_MIN <- 1
SUP_ADD_EXP_MAX <- Inf
SUP_ADD_EXP_START <- 2

K_NAME <- "K"
K_MIN <- 0
K_MAX <- Inf
K_START <- .03

COMB_CONST_NAME <- "COMBINED COMPARISON RATIO"
COMB_CONST_MIN <- 0
COMB_CONST_MAX <- 1
COMB_CONST_START <- .5

TAU_NAME <- "TAU"
TAU_MIN <- 0
TAU_MAX <- Inf
TAU_START <- .02

CONCAVE_EXP_NAME <- "CONCAVE EXPONENT"
CONCAVE_EXP_MIN <- 0
CONCAVE_EXP_MAX <- 1
CONCAVE_EXP_START <- .7

ALPHA_I_MIN <- -Inf
ALPHA_I_MAX <- Inf
ALPHA_I_START <- 0

addToVector <- function(p_vector, p_name, p_value)
{
	newVector <- c(p_value)
	names(newVector) <- c(p_name)
	return (c(p_vector, newVector))
}

# lower bounds on all variables
getMinVector <- function(p_discountModel, 
			p_utilityModel, 
			p_compModel, 
			p_participantIDs)
{
	ret <- c(SENS_MIN)
	names(ret) <- c(SENS_NAME)
	
	if(p_discountModel == EXP_DM_NAME | 
		p_discountModel == GEN_HYP_DM_NAME |
		p_discountModel == SUBADDITIVE_DM_NAME |
		p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, DISC_EXP_NAME, DISC_EXP_MIN)
	}

	if(p_discountModel == HYP_DM_NAME | 
		p_discountModel == GEN_HYP_DM_NAME |
		p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, K_NAME, K_MIN)
	}

	if(p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, SUP_ADD_EXP_NAME, SUP_ADD_EXP_MIN)
		ret <- addToVector(ret, SENS_EXP_NAME, SENS_EXP_MIN)
	}

	if(p_discountModel == SUBADDITIVE_DM_NAME)
	{
		ret <- addToVector(ret, SUB_ADD_EXP_NAME, SUB_ADD_EXP_MIN)
	}
	
	if(p_discountModel == Q_HYP_DM_NAME)
	{
		ret <- addToVector(ret, BETA_NAME, BETA_MIN)
		ret <- addToVector(ret, DELTA_NAME, DELTA_MIN)
	}

	if(p_utilityModel == LOG_UM_NAME)
	{
		ret <- addToVector(ret, TAU_NAME, TAU_MIN)
	}

	if(p_utilityModel == CONCAVE_UM_NAME)
	{
		ret <- addToVector(ret, CONCAVE_EXP_NAME, CONCAVE_EXP_MIN)
	}

	if(p_compModel == COMBINED_CM_NAME)
	{
		ret <- addToVector(ret, COMB_CONST_NAME, COMB_CONST_MIN)
	}

	alphaIVector <- c()
	if(length(p_participantIDs) > 1)
	{
		alphaIVector <- rep(ALPHA_I_MIN, length(p_participantIDs))	
		names(alphaIVector) <- p_participantIDs
	}

	return(c(ret, alphaIVector))
}

# upper bounds on all variables
getMaxVector <- function(p_discountModel, 
			p_utilityModel, 
			p_compModel, 
			p_participantIDs)
{
	ret <- c(SENS_MAX)
	names(ret) <- c(SENS_NAME)
	
	if(p_discountModel == EXP_DM_NAME | 
		p_discountModel == GEN_HYP_DM_NAME |
		p_discountModel == SUBADDITIVE_DM_NAME |
		p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, DISC_EXP_NAME, DISC_EXP_MAX)
	}

	if(p_discountModel == HYP_DM_NAME | 
		p_discountModel == GEN_HYP_DM_NAME |
		p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, K_NAME, K_MAX)
	}

	if(p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, SUP_ADD_EXP_NAME, SUP_ADD_EXP_MAX)
		ret <- addToVector(ret, SENS_EXP_NAME, SENS_EXP_MAX)
	}

	if(p_discountModel == SUBADDITIVE_DM_NAME)
	{
		ret <- addToVector(ret, SUB_ADD_EXP_NAME, SUB_ADD_EXP_MAX)
	}
	
	if(p_discountModel == Q_HYP_DM_NAME)
	{
		ret <- addToVector(ret, BETA_NAME, BETA_MAX)
		ret <- addToVector(ret, DELTA_NAME, DELTA_MAX)
	}

	if(p_utilityModel == LOG_UM_NAME)
	{
		ret <- addToVector(ret, TAU_NAME, TAU_MAX)
	}

	if(p_utilityModel == CONCAVE_UM_NAME)
	{
		ret <- addToVector(ret, CONCAVE_EXP_NAME, CONCAVE_EXP_MAX)
	}

	if(p_compModel == COMBINED_CM_NAME)
	{
		ret <- addToVector(ret, COMB_CONST_NAME, COMB_CONST_MAX)
	}

	alphaIVector <- c()
	if(length(p_participantIDs) > 1)
	{
		alphaIVector <- rep(ALPHA_I_MAX, length(p_participantIDs))	
		names(alphaIVector) <- p_participantIDs
	}

	return(c(ret, alphaIVector))
}

# initial vector for the search with values that are around where we 
# would expect to find them on convergence - deviations from these
# initial values can product suboptimal results, especially on the 
# lower df models where a corner of the box constraints is a local maximum

getStartingVector <- function(p_discountModel, 
				p_utilityModel, 
				p_compModel,
				p_participantIDs)
{
	ret <- c(SENS_START)
	names(ret) <- c(SENS_NAME)
	
	if(p_discountModel == EXP_DM_NAME | 
		p_discountModel == GEN_HYP_DM_NAME |
		p_discountModel == SUBADDITIVE_DM_NAME |
		p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, DISC_EXP_NAME, DISC_EXP_START)
	}

	if(p_discountModel == HYP_DM_NAME | 
		p_discountModel == GEN_HYP_DM_NAME |
		p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, K_NAME, K_START)
	}

	if(p_discountModel == DBI_DM_NAME)
	{
		ret <- addToVector(ret, SUP_ADD_EXP_NAME, SUP_ADD_EXP_START)
		ret <- addToVector(ret, SENS_EXP_NAME, SENS_EXP_START)
	}

	if(p_discountModel == SUBADDITIVE_DM_NAME)
	{
		ret <- addToVector(ret, SUB_ADD_EXP_NAME, SUB_ADD_EXP_START)
	}
	
	if(p_discountModel == Q_HYP_DM_NAME)
	{
		ret <- addToVector(ret, BETA_NAME, BETA_START)
		ret <- addToVector(ret, DELTA_NAME, DELTA_START)
	}

	if(p_utilityModel == LOG_UM_NAME)
	{
		ret <- addToVector(ret, TAU_NAME, TAU_START)
	}

	if(p_utilityModel == CONCAVE_UM_NAME)
	{
		ret <- addToVector(ret, CONCAVE_EXP_NAME, CONCAVE_EXP_START)
	}

	if(p_compModel == COMBINED_CM_NAME)
	{
		ret <- addToVector(ret, COMB_CONST_NAME, COMB_CONST_START)
	}

	alphaIVector <- c()
	if(length(p_participantIDs) > 1)
	{
		alphaIVector <- rep(ALPHA_I_START, length(p_participantIDs))	
		names(alphaIVector) <- p_participantIDs
	}

	return(c(ret, alphaIVector))
}


