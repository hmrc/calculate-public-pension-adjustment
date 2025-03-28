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
import play.api.libs.json.{Format, Reads, __}

case class AnnualAllowance(scottishTaxYears: List[Period], taxYears: List[TaxYear])

object AnnualAllowance {

  implicit lazy val reads: Reads[AnnualAllowance] =
    ((__ \ "scottishTaxYears").read[List[Period]] and
      (__ \ "taxYears").read[List[TaxYear]])(AnnualAllowance(_, _))

  implicit lazy val formats: Format[AnnualAllowance] = (
    (__ \ "scottishTaxYears").format[List[Period]] and
      (__ \ "taxYears").format[List[TaxYear]]
  )(AnnualAllowance.apply, o => Tuple.fromProductTyped(o))
}
