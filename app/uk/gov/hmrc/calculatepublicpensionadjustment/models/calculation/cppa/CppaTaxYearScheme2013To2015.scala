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

case class CppaTaxYearScheme2013To2015(
  name: String,
  pstr: String,
  chargePaidByScheme: Int,
  chargePaidByMember: Int,
  oPensionInputAmount: Int,
  pensionInputAmount: Int
) extends TaxYearScheme

object CppaTaxYearScheme2013To2015 {

  implicit lazy val reads: Reads[CppaTaxYearScheme2013To2015] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[CppaTaxYearScheme2013To2015] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "pensionInputAmount").read[Int]
    )(CppaTaxYearScheme2013To2015.apply _)

    normalReads
  }

}
