#discounting models
EXP_DM_NAME <- "EXPONENTIAL"
Q_HYP_DM_NAME <- "QUASI HYPERBOLIC"
HYP_DM_NAME <- "HYPERBOLIC"
GEN_HYP_DM_NAME <- "GENERALIZED_HYPERBOLIC"
SUBADDITIVE_DM_NAME <- "SUBADDITIVE"
DBI_DM_NAME <- "DBI"
DISCOUNTING_MODELS <- c(EXP_DM_NAME, 
				#Q_HYP_DM_NAME, 
				HYP_DM_NAME#, 
				#GEN_HYP_DM_NAME, 
				#SUBADDITIVE_DM_NAME, 
				#DBI_DM_NAME
				)

#utility models
LINEAR_UM_NAME <- "LINEAR"
LOG_UM_NAME <- "LOGARITHMIC"
CONCAVE_UM_NAME <- "CONCAVE"
UTILITY_MODELS <- c(#LINEAR_UM_NAME, 
				#LOG_UM_NAME, 
				CONCAVE_UM_NAME
				)

#comparison models
DOLLAR_DIFF_CM_NAME <- "DOLLAR DIFFERENCE"
COMBINED_CM_NAME <- "COMBINED"
COMPARISON_MODELS <- c(DOLLAR_DIFF_CM_NAME#, 
				#COMBINED_CM_NAME
				)


#custom models from literature
TRADEOFF_COMBM_NAME <- "TRADEOFF"
HEURISTIC_COMBM_NAME <- "HEURISTIC"

MODEL_COMBINATIONS <- expand.grid(discountModel = DISCOUNTING_MODELS,
						utilityModel = UTILITY_MODELS,
						comparisonModel = COMPARISON_MODELS)
COMBINED_MODELS <- c(TRADEOFF_COMBM_NAME, HEURISTIC_COMBM_NAME)


isTwoFactorDiscount <- function(p_discountFunction)
{
	return (identical(p_discountFunction, dbiDiscount) | 
		identical(p_discountFunction, subadditiveDiscount))
}

quasiHypDiscount <- function(p_t, p_par)
{
	if(p_t == 0)
	{
		return (1)
	}

	beta <- p_par[BETA_NAME]
	delta <- p_par[DELTA_NAME]

	ret <- beta * (delta ^ p_t)
	return (ret)
}

dbiDiscount <- function(p_ts, p_tl, p_par)
{
	sensitivityExp <- p_par[SENS_EXP_NAME]
	discountExp <- p_par[DISC_EXP_NAME]
	additivityExp <- p_par[SUP_ADD_EXP_NAME]
	k <- p_par[K_NAME]

	ts.alt = p_ts^sensitivityExp
	tl.alt = p_tl^sensitivityExp
	timeDiff.alt = (tl.alt - ts.alt)^additivityExp
	
	if(k == 0)
	{
		denom <- exp(timeDiff.alt)
		return ((1/denom)^discountExp)
	}
	else
	{
		denom <- 1 + k * timeDiff.alt

		exp <- discountExp / k
		return ((1/denom)^exp)
	}
}

subadditiveDiscount <- function(p_ts, p_tl, p_par)
{
	discountExp <- p_par[DISC_EXP_NAME]
	additivityExp <- p_par[SUB_ADD_EXP_NAME]

	timeDiff <- p_tl - p_ts
	exponent <- timeDiff^additivityExp
	denom <- exp(exponent)
	ret <- 1/denom^discountExp

	return (ret)
}

genHypDiscount <- function(p_t, p_par)
{
	k <- p_par[K_NAME]
	discountExp <- p_par[DISC_EXP_NAME]

	if(k == 0)
	{
		denom <- exp(p_t)
		frac <- 1/denom
		return (frac^discountExp)		
	}
	else
	{
		denom <- 1 + k * p_t
		frac <- 1/denom
		exp <- discountExp/k
	
		return (frac^exp)	
	}
}

hyperbolicDiscount <- function(p_t, p_par)
{
	k <- p_par[K_NAME]

	denom <- 1 + k * p_t
	return (1/denom)
}

exponentialDiscount <- function(p_t, p_par)
{
	discountExp <- p_par[DISC_EXP_NAME]

	exponent <- discountExp * p_t * -1
	ret = exp(exponent)
	return (ret)
}

combinedComparison <- function(p_choiceVal, p_alternativeVal, p_par)
{
	combConst <- p_par[COMB_CONST_NAME]	

	percentDiff <- percentDiffComparison(p_choiceVal, p_alternativeVal, p_par)
	dollarDiff <- dollarDiffComparison(p_choiceVal, p_alternativeVal, p_par)
	ret <- percentDiff * combConst + dollarDiff * (1 - combConst)

	return(ret)
}

percentDiffComparison <- function(p_choiceVal, p_alternativeVal, p_par)
{
	if(p_choiceVal == p_alternativeVal)
	{
		return (0)
	}

	diff <- p_choiceVal - p_alternativeVal
	minVal <- min(p_choiceVal, p_alternativeVal)
	ret <- as.double(diff)/minVal

	return (ret) 
}

dollarDiffComparison <- function(p_choiceVal, p_alternativeVal, p_par)
{
	return (p_choiceVal - p_alternativeVal)
}

logarithmicUtility <- function(p_x, p_par)
{
	tau <- p_par[TAU_NAME]

	if(tau == 0)
	{
		return (p_x)
	}

	ret <- log(1 + tau * p_x) / tau
	return (ret)
}

concaveUtility <- function(p_x, p_par)
{
	concaveExp <- p_par[CONCAVE_EXP_NAME]

	return (p_x ^ concaveExp)
}

linearUtility <- function(p_x, p_par)
{
	return (p_x)
}

getComparisonFunction <- function(p_compModel)
{
	if(p_compModel == COMBINED_CM_NAME)
	{
		return (combinedComparison)	
	}
	else if(p_compModel == DOLLAR_DIFF_CM_NAME)
	{
		return (dollarDiffComparison)
	}
	else if(p_compModel == "PERCENT_DIFF")
	{
		return (percentDiffComparison)
	}
	else
	{
		stop(paste("Unexpected comparison model: ", p_compModel))
	}
}

getUtilityFunction <- function(p_utilityModel)
{
	if(p_utilityModel == LINEAR_UM_NAME)
	{
		return (linearUtility)	
	}
	else if(p_utilityModel == CONCAVE_UM_NAME)
	{
		return (concaveUtility)
	}
	else if(p_utilityModel == LOG_UM_NAME)
	{
		return (logarithmicUtility)
	}
	else
	{
		stop(paste("Unexpected utility model: ", p_utilityModel))
	}
}

getDiscountFunction <- function(p_discountModel)
{
	if(p_discountModel == EXP_DM_NAME)
	{
		return (exponentialDiscount)
	}
	else if(p_discountModel == HYP_DM_NAME)
	{
		return (hyperbolicDiscount)
	}
	else if(p_discountModel == GEN_HYP_DM_NAME)
	{
		return (genHypDiscount)
	}
	else if(p_discountModel == SUBADDITIVE_DM_NAME)
	{
		return (subadditiveDiscount)
	}
	else if(p_discountModel == DBI_DM_NAME)
	{
		return (dbiDiscount)
	}
	else if(p_discountModel == Q_HYP_DM_NAME)
	{
		return (quasiHypDiscount)
	}
	else
	{
		stop(paste("Unexpected discount model: ", p_discountModel))
	}
}