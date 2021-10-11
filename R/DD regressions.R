makeDiffColumns <- function(p_data)
{
  uniqueIDs <- unique(p_data$Subject)
  diffColumns <- grep("X.LL..All.", colnames(p_data), value = TRUE)
  collateralColumns <-
    colnames(p_data)[!colnames(p_data) %in% diffColumns]
  
  diffData <-
    do.call(rbind,
            lapply(uniqueIDs, function(subject_id,
                                       p_data,
                                       p_diffColumns,
                                       p_collateralColumns)
            {
              subjectData <- p_data[which(p_data$Subject == subject_id), ]
              year1Data <- subjectData[which(subjectData$Year == 1), ]
              year2Data <- subjectData[which(subjectData$Year == 2), ]
              if (nrow(year1Data) == 1 & nrow(year2Data) == 1)
              {
                collateralData <- year2Data[, p_collateralColumns]
                year1DiffData <- year1Data[, p_diffColumns]
                year2DiffData <- year2Data[, p_diffColumns]
                X.LL..All. <- year2DiffData - year1DiffData
                
                ret <- cbind(X.LL..All., collateralData)
                
                return (ret)
              }
              else
              {
                return (NULL)
              }
            }, p_data, diffColumns, collateralColumns))
  
  #print(diffData)
  
  return (diffData)
}

lmAndPrintFit <- function(p_data, p_formula, p_printAtStart)
{
	print(p_printAtStart)
	print(p_formula)
	fit <- lm(p_formula, data = p_data)
	print(coef(summary(fit)))
}

ddData <- read.csv("K Analysis 1_22_16.csv")

ddYear1Data <- ddData[which(ddData$Year == 1),]
ddYear1DataTD <- ddYear1Data[which(ddYear1Data$ADHD.TD.Group == "TD"),]
ddYear1DataADHD <- ddYear1Data[which(ddYear1Data$ADHD.TD.Group == "ADHD"),]

foAll <- as.formula("X.LL..All. ~ Age + Sex + ADHD.TD.Group + ADHD.TD.Group*Age + ADHD.TD.Group*Sex + Age*Sex")
lmAndPrintFit(ddYear1Data, foAll, "Year 1 All")

foSmall <- as.formula("X.LL..All. ~ Age + Sex + Age*Sex")
lmAndPrintFit(ddYear1DataTD, foSmall, "Year 1 TD")
lmAndPrintFit(ddYear1DataADHD, foSmall, "Year 1 ADHD")

ddDiffData <- makeDiffColumns(ddData)
ddDiffDataTD <- ddDiffData[which(ddDiffData$ADHD.TD.Group == "TD"),]
ddDiffDataADHD <- ddDiffData[which(ddDiffData$ADHD.TD.Group == "ADHD"),]

lmAndPrintFit(ddDiffData, foAll, "Diff All")
lmAndPrintFit(ddDiffDataTD, foSmall, "Diff TD")
lmAndPrintFit(ddDiffDataADHD, foSmall, "Diff ADHD")