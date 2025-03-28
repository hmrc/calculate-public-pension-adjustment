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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation

import play.api.libs.json.{Format, Json, Reads, __}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa.CppaTaxYear

case class CalculationRequest(resubmission: Resubmission, scottishTaxYears: List[Period], taxYears: List[CppaTaxYear])

object CalculationRequest {

  implicit lazy val reads: Reads[CalculationRequest] = {

    import play.api.libs.functional.syntax.*

    ((__ \ "resubmission").read[Resubmission] and
      (__ \ "scottishTaxYears").read[List[Period]] and
      (__ \ "taxYears").read[List[CppaTaxYear]])(CalculationRequest(_, _, _))
  }

  implicit lazy val formats: Format[CalculationRequest] = Json.format
}
