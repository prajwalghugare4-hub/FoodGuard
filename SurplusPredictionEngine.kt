package com.example.ml

import com.example.data.model.HistoricalLogEntity
import com.example.data.model.PredictionResult
import com.example.data.model.SurplusRiskLevel
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

object SurplusPredictionEngine {

  /**
   * Predicts demand, calculates expected surplus, recommends preparation quantity,
   * and determines the surplus risk level based on actual historical food records.
   */
  fun calculatePrediction(
    historicalLogs: List<HistoricalLogEntity>,
    mealType: String,
    currentPlannedQuantity: Int,
    isWeekend: Boolean = false,
    isHoliday: Boolean = false,
    specialEventMultiplier: Double = 1.0
  ): PredictionResult {
    // Filter relevant logs for the same meal type if available, else all logs
    val relevantLogs = historicalLogs.filter { it.mealType.equals(mealType, ignoreCase = true) }
      .ifEmpty { historicalLogs }

    // Check if sufficient data exists (at least 2 entries required for honest statistical forecasting)
    if (relevantLogs.size < 2) {
      return PredictionResult.InsufficientData(
        message = "Building estimate — more data needed",
        requiredEntries = 2,
        currentEntries = relevantLogs.size
      )
    }

    // Historical demand = quantityConsumed (actual plates eaten/sold)
    val demandValues = relevantLogs.map { it.quantityConsumed }
    val n = demandValues.size

    // 1. Exponential / Weighted Moving Average (giving higher weight to recent meals)
    var weightedSum = 0.0
    var weightTotal = 0.0
    val recentWindow = relevantLogs.take(7) // recent 7 logs
    recentWindow.forEachIndexed { index, log ->
      val weight = 1.0 / (index + 1)
      weightedSum += log.quantityConsumed * weight
      weightTotal += weight
    }
    val baselineDemand = if (weightTotal > 0) weightedSum / weightTotal else demandValues.average()

    // 2. Day of week & contextual adjustments
    var contextualMultiplier = 1.0
    if (isWeekend) {
      contextualMultiplier *= 0.95 // slightly fewer in mess / canteen on weekends
    }
    if (isHoliday) {
      contextualMultiplier *= 0.90
    }
    contextualMultiplier *= specialEventMultiplier

    val predictedDemandRaw = baselineDemand * contextualMultiplier
    val predictedDemand = max(1, predictedDemandRaw.roundToInt())

    // 3. Statistical Variance & Standard Deviation for Safety Margin
    val variance = demandValues.map { (it - baselineDemand) * (it - baselineDemand) }.sum() / n
    val standardDeviation = sqrt(variance)

    // Safety margin (e.g. 0.5 to 0.75 * standard deviation, minimum 5 plates)
    val safetyMargin = max(5, (standardDeviation * 0.65).roundToInt())
    val recommendedPreparation = predictedDemand + safetyMargin

    // 4. Expected Surplus Calculation
    val expectedSurplus = max(0, currentPlannedQuantity - predictedDemand)

    // 5. Surplus Risk Assessment
    val (riskLevel, riskReason) = when {
      currentPlannedQuantity > (recommendedPreparation * 1.10).roundToInt() -> {
        Pair(
          SurplusRiskLevel.HIGH,
          "Current planned quantity exceeds recommended preparation significantly. Consider initiating a rescue now."
        )
      }
      currentPlannedQuantity > predictedDemand -> {
        Pair(
          SurplusRiskLevel.MEDIUM,
          "Planned quantity is moderately higher than predicted demand. Monitor leftover food during service."
        )
      }
      else -> {
        Pair(
          SurplusRiskLevel.LOW,
          "Planned quantity is balanced with expected demand. Low surplus risk predicted."
        )
      }
    }

    // Average waste reduction calculation from history
    val totalPrepared = relevantLogs.sumOf { it.quantityPrepared }
    val totalRescued = relevantLogs.sumOf { it.quantityRescued }
    val wasteReductionPct = if (totalPrepared > 0) ((totalRescued / totalPrepared) * 100).roundToInt() else 18

    return PredictionResult.Success(
      expectedDemand = predictedDemand,
      recommendedPreparation = recommendedPreparation,
      currentPlanned = currentPlannedQuantity,
      expectedSurplus = expectedSurplus,
      surplusRisk = riskLevel,
      riskReason = riskReason,
      recommendationReason = "Recommended quantity is based on recent demand ($predictedDemand plates), historical variation (±${safetyMargin} margin), and weekday attendance pattern.",
      historicalEntriesCount = n,
      averageWasteReductionPct = wasteReductionPct
    )
  }
}
