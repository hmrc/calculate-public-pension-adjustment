package uk.gov.hmrc.calculatepublicpensionadjustment.models.submission

import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period}

object SubmissionTestData {

  val calculationResponse = CalculationResponse(
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        5000,
        3000,
        2000,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

}
