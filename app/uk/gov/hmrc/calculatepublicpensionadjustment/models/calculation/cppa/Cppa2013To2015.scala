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

import play.api.libs.json._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

case class Cppa2013To2015(
  name: String,
  pstr: String,
  chargePaidByScheme: Int,
  chargePaidByMember: Int,
  oPensionInputAmount: Int,
  pensionInputAmount: Int
) extends TaxYearScheme

object Cppa2013To2015 {

  implicit lazy val reads: Reads[Cppa2013To2015] = {

    import play.api.libs.functional.syntax._

    import Ordering.Implicits._

    val normalReads: Reads[Cppa2013To2015] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "pensionInputAmount").read[Int]
    )(Cppa2013To2015.apply _)

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p >= Period._2013 && p <= Period._2015 =>
          Reads(_ => JsSuccess(p))
        case _                                           =>
          Reads(_ => JsError("taxYear must fall between `2013`-`2015`"))
      }
      .andKeep(normalReads)
  }

}
