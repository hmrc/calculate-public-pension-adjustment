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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa

import play.api.libs.json.{JsError, JsSuccess, Reads, __}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

case class CppaTaxYear2016PreAlignment(
  period: Period,
  totalIncome: Int,
  taxYearSchemes: List[CppaTaxYearScheme2016PreAlignment]
) extends CppaTaxYear

object CppaTaxYear2016PreAlignment {

  implicit lazy val reads: Reads[CppaTaxYear2016PreAlignment] = {

    import play.api.libs.functional.syntax._

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p == Period._2016PreAlignment =>
          Reads(_ => JsSuccess(p))
        case _                                  =>
          Reads(_ => JsError("tax year must be `2016-pre`"))
      }
      .andKeep {
        (
          (__ \ "period").read[Period] and
            (__ \ "totalIncome").read[Int] and
            (__ \ "taxYearSchemes").read[List[CppaTaxYearScheme2016PreAlignment]]
        )(CppaTaxYear2016PreAlignment.apply _)
      }
  }

}
