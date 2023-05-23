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

import play.api.libs.json.{Reads, __}

case class TaxYearScheme(
  name: String,
  pstr: String,
  oPensionInputAmount: Int,
  rPensionInputAmount: Int,
  chargePaidByScheme: Int
)

object TaxYearScheme {

  implicit lazy val reads: Reads[TaxYearScheme] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "rPensionInputAmount").read[Int] and
        (__ \ "chargePaidByScheme").read[Int]
    )(TaxYearScheme.apply _)

  }
}
