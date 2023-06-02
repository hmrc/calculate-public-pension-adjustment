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

import play.api.libs.json._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac.PaacTaxYear
import uk.gov.hmrc.time

case class PaacRequest(taxYears: List[PaacTaxYear], until: Period = Period.Year(time.TaxYear.current.finishYear))

object PaacRequest {
  implicit lazy val reads: Reads[PaacRequest] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "taxYears").read[List[PaacTaxYear]] and
        (__ \ "until").readNullable[Period].map(_.getOrElse(Period.Year(time.TaxYear.current.finishYear)))
    )(PaacRequest(_, _))

  }

  implicit lazy val writes: Writes[PaacRequest] =
    Json.writes[PaacRequest]

}
