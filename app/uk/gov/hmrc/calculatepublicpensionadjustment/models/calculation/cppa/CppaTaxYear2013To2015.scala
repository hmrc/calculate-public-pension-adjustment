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

case class CppaTaxYear2013To2015(
  period: Period,
  totalIncome: Int,
  taxYearSchemes: List[CppaTaxYearScheme2013To2015]
) extends CppaTaxYear

object CppaTaxYear2013To2015 {

  implicit lazy val reads: Reads[CppaTaxYear2013To2015] = {

    import play.api.libs.functional.syntax._

    import Ordering.Implicits._

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p >= Period._2013 && p <= Period._2015 =>
          Reads(_ => JsSuccess(p))
        case _                                           =>
          Reads(_ => JsError("taxYear must fall between `2013`-`2015`"))
      } andKeep {
      (
        (__ \ "period").read[Period] and
          (__ \ "totalIncome").read[Int] and
          (__ \ "taxYearSchemes").read[List[CppaTaxYearScheme2013To2015]]
      )(CppaTaxYear2013To2015.apply _)
    }

  }
}
