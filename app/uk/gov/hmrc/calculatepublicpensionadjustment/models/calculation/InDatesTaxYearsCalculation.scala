/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation

import play.api.libs.json.{Reads, Writes, __}

case class InDatesTaxYearsCalculation(
  period: Period,
  originalCharge: Int,
  memberCredit: Int,
  schemeCredit: Int,
  Debit: Int,
  taxYearSchemes: List[InDatesTaxYearSchemeCalculation]
)

object InDatesTaxYearsCalculation {

  implicit lazy val reads: Reads[InDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").read[Period] and
        (__ \ "originalCharge").read[Int] and
        (__ \ "memberCredit").read[Int] and
        (__ \ "schemeCredit").read[Int] and
        (__ \ "Debit").read[Int] and
        (__ \ "taxYearSchemes").read[List[InDatesTaxYearSchemeCalculation]]
    )(InDatesTaxYearsCalculation.apply _)
  }

  implicit lazy val writes: Writes[InDatesTaxYearsCalculation] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "period").write[Period] and
        (__ \ "originalCharge").write[Int] and
        (__ \ "memberCredit").write[Int] and
        (__ \ "schemeCredit").write[Int] and
        (__ \ "Debit").write[Int] and
        (__ \ "taxYearSchemes").write[List[InDatesTaxYearSchemeCalculation]]
    )(a =>
      (
        a.period,
        a.originalCharge,
        a.memberCredit,
        a.schemeCredit,
        a.Debit,
        a.taxYearSchemes
      )
    )
  }

}
