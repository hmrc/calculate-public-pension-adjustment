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

package uk.gov.hmrc.calculatepublicpensionadjustment.models

import play.api.libs.functional.syntax.*
import play.api.libs.json.{Format, __}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

case class ReducedNetIncomeRequest(
  period: Period,
  scottishTaxYears: List[Period],
  totalIncome: Int,
  incomeSubJourney: IncomeSubJourney
)

object ReducedNetIncomeRequest {

  implicit lazy val format: Format[ReducedNetIncomeRequest] = (
    (__ \ "period").format[Period] and
      (__ \ "scottishTaxYears").format[List[Period]] and
      (__ \ "totalIncome").format[Int] and
      (__ \ "incomeSubJourney").format[IncomeSubJourney]
  )(ReducedNetIncomeRequest.apply, o => Tuple.fromProductTyped(o))
}
