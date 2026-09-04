package com.example.data.model

enum class SurplusRiskLevel {
  LOW,
  MEDIUM,
  HIGH
}

sealed class PredictionResult {
  data class Success(
    val expectedDemand: Int,
    val recommendedPreparation: Int,
    val currentPlanned: Int,
    val expectedSurplus: Int,
    val surplusRisk: SurplusRiskLevel,
    val riskReason: String,
    val recommendationReason: String,
    val historicalEntriesCount: Int,
    val averageWasteReductionPct: Int
  ) : PredictionResult()

  data class InsufficientData(
    val message: String = "Building estimate — more data needed",
    val requiredEntries: Int = 2,
    val currentEntries: Int = 0
  ) : PredictionResult()
}
