/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.calculatepublicpensionadjustment.models.submission

import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{CalculationResponse, InDatesTaxYearSchemeCalculation, InDatesTaxYearsCalculation, OutOfDatesTaxYearSchemeCalculation, OutOfDatesTaxYearsCalculation, Period, Resubmission, TotalAmounts}

object SubmissionTestData {

  val calculationResponse = CalculationResponse(
    Resubmission(false, None),
    TotalAmounts(1, 2, 3),
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0,
        2000,
        1,
        2,
        3,
        4,
        5,
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
        1,
        2,
        3,
        4,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

}
