#Code translated from MATLAB to do simulated annealing in estimating k values
#Very loop-y hence runs slow in R
#Will vectorize and get rid of loops at some point in life
#AZE 05.12.15 

#Vectorized
#SJR 06.25.15

# CONSTANTS

dRt <- 0.85 # Temperature reduction factor. The value suggested by Corana et al. is .85. See Goffe et al. for more advice.
iN_S <- 20 # Number of iterations in which only step size changes
iN_T <- 20 # Number of iterations before temperature decreases
iN_Z <- 50 # Number of temperature decreases before quitting

rUpdate_min <- .4 # Minimum update ratio
rUpdate_max <- .6	# Maximum update ratio
step_length_adj <- 2 # Step length adjustment constant
step_size_max <- 5 # Limit on step size

r_o <- 0.1 # K (parameter 1, hyperbolic discount factor) initialization 
mu_o <- 1.1 # mu (parameter 2, GLM slope) initialization
vP_0 <- c(r_o, mu_o) # Initial parameters

vL <- c(0.0001, 0) # Lower bounds on parameters
vU <- c(0.999, 20) # Upper bounds on parameters 

mle.env <- new.env() # environment for storing variables between functions

# END CONSTANTS

# Calculates the log of the logistic function
# of the differences between values in options
# given the current parameters.  The sum of these
# gives the negative of the log-likelihood of the model 
# given the current parameters, and thus is what we 
# are looking to minimize.

multi_logit2 <- function(params, data)
{
	k <- params[1]
	m <- params[2]

	values <- apply(data, 1, function(data_row)
	{
		v1 <- data_row["ss.amt"]
		v2 <- data_row["ll.amt"]
		t1 <- data_row["ss.del"]
		t2 <- data_row["ll.del"]
		c <- 1 - data_row["ll1_ss0"]

		V1 <- v1 * 1/(1+ k * t1) # current value of v1 based on parameters
		V2 <- v2 * 1/(1+ k * t2) # current value of v2
		P1 <- 1 / (1 + exp(-1*m*(V1-V2))) # logistic function of difference
		
		if (c == 1) # user chose option 1
		{
      		return(log(P1))
    		}
    		else # user chose option 2
		{
      		return(log(1-P1))
    		}
  	})
	
	# multiplication by -1 in order to turn this into a minimization problem  
  	return(-1*sum(values))
}

anneal_once <- function(temp_index)
{
	z <- temp_index
	replicate(iN_T, run_one_search_iteration()) # do all iterations for this temp
    		
    	mle.env$dT <- dRt * mle.env$dT # Adjust temperature
    	
	mle.env$vX <- mle.env$vX_opt # Start from the best point thus far
    	mle.env$df <- mle.env$df_opt

    	print(z)
    		
	# record values for the best point this loop
	mle.env$vf[z+1] <- mle.env$df
    	mle.env$mX <- rbind(mle.env$mX, mle.env$vX)
    	mle.env$mM <- rbind(mle.env$mM, mle.env$vM)
}

run_one_search_iteration <- function()
{
	mle.env$vnAcp <- rep(0, mle.env$dsize_param) # counter for number of times that we update along each dimension
      replicate(iN_S, lapply(1:mle.env$dsize_param, adjust_one_var)) # go through search procedure    
      		
	# Adjust step length (vM)
      mle.env$vRatio  <- mle.env$vnAcp / iN_S # How often we updated along each dimension in the last loop
      lapply(1:mle.env$dsize_param, adjust_one_step_length) # adjust each variable
			
      vselect <- mle.env$vM > step_size_max # vector of booleans indicating which step sizes are too big
      mle.env$vM <- mle.env$vM * (1-vselect) + step_size_max * vselect # limit too big step sizes to step_size_max
}

adjust_one_step_length <- function(var_index)
{
	i <- var_index
	if(mle.env$vRatio[i] > rUpdate_max)
	{
		# We updated this dimension more that 60% of the time.  
		# This is too much.  We are likely near a local minimum.  
		# Increase the step size to get us out of here.
      	mle.env$vM[i] <- mle.env$vM[i]*(1 + step_length_adj * (mle.env$vRatio[i] - rUpdate_max) / (1 - rUpdate_max))
      }
      else if(mle.env$vRatio[i] < rUpdate_min)
	{
		# We updated this dimension less than 40% of the time.
		# This is too little.  Decrease the step size to 
            # increase the likelihood that a new point will be better.  
      	mle.env$vM[i] <- mle.env$vM[i]/(1 + step_length_adj * (rUpdate_min - mle.env$vRatio[i]) / rUpdate_min)          
      }
}

adjust_one_var <- function(var_index)
{
	i <- var_index	

	# pick a test point based on our current point 
	# that is varied along a single dimension
      vX_test <- mle.env$vX
	randStep <- 2 * (runif(1) - 0.5) * mle.env$vM[i] # random uniform number in [-1, 1] times the step size 
      vX_test[i] <- vX_test[i] + randStep
        				
	# check if the test point is in bounds
      if(vX_test[i] > vU[i] || vX_test[i] < vL[i])
	{
		# not in bounds, pick random value that's in bounds
      	vX_test[i] <- vL[i] + (vU[i] - vL[i])*runif(1)
      }
          
	# check the value at the test point
      df_test <- multi_logit2(vX_test, mle.env$data_mle)
      if(df_test <= mle.env$df && is.numeric(df_test))
	{
		# if the test point is better than our current point,
		# update the current point
            mle.env$vX <- vX_test
            mle.env$df <- df_test
            mle.env$vnAcp[i] <- mle.env$vnAcp[i] + 1

		# if the test point is the best so far,
		# update the optimal point
            if(df_test <= mle.env$df_opt)
		{
              	mle.env$vX_opt <- vX_test
              	mle.env$df_opt <- df_test

			# print(paste(mle.env$df, paste(mle.env$vX, collapse = ","), sep=","))
            }
	}
      else if(is.numeric(df_test))
	{
		# If the test point isn't better than the 
		# current point, there's still a chance
		# we'll take it over our current point. 
		# The chance of doing that depends on how
		# bad the point is and the current temperature. 
            				
		# The chance of taking the test point over 
		# our current point exponentially declines with
		# the difference in outputs at these points
		# divided by the temperature.  
		dp <- exp((mle.env$df-df_test)/mle.env$dT)
            if(dp > runif(1))
		{
			# The RNG decided this point is good enough.
			# Update the current point.  
              	mle.env$vnAcp[i] <- mle.env$vnAcp[i]+1;
              	mle.env$vX <- vX_test
              	mle.env$df <- df_test				
		}
	}
}

MLE_estimation <- function(data)
{
	# we must add these variables to the mle.env so that
	# they may be accessed and altered in the called functions
	mle.env$data_mle <- data

  	mle.env$dsize_param <- length(vP_0) # Number of parameters in model
	mle.env$vM <- rep(1, mle.env$dsize_param) # Step length vector used in initial step. Algorithm adjusts vM automatically, such that an incorrect initial value gets adapted.
  	
	mle.env$dT <- 1 # Current temperature, initialized at 1
  	mle.env$vX <- vP_0 # Current parameter set, initialized at vP_0
  	mle.env$df <- multi_logit2(mle.env$vX,mle.env$data_mle) # Current output value
  	
	mle.env$vX_opt <- mle.env$vX # Best parameter set found so far
  	mle.env$df_opt <- multi_logit2(mle.env$vX_opt, mle.env$data_mle) # Output value of the best parameter set so far
  	
	mle.env$mX <- mle.env$vX_opt # Matrix of recorded best parameter values
  	mle.env$vf <- mle.env$df_opt # Vector of recorded outputs from best parameter values 
  	mle.env$mM <- mle.env$vM # Matrix of recorded step lengths

	lapply(1:iN_Z, anneal_once) # run simulated annealing

	temp <- multi_logit2(mle.env$vX_opt, mle.env$data_mle)
  	output <- data.frame(mle.env$vX_opt[1], mle.env$vX_opt[2], temp)
  	print(output)
  	return(output)
}  

library(plyr)

titration.data.clean <- read.table("titration.data.deidentified.csv", 
					header=TRUE,
					sep=",")

titration.data.k <- plyr::ddply(titration.data.clean, c("subid"), MLE_estimation, .parallel = FALSE)
write.csv(titration.data.k, "titration.data.k")