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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.useranswers

import play.api.libs.json.{Format, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.Enumerable

sealed trait LTAChargeType

object LTAChargeType extends Enumerable.Implicits {

  case object New extends LTAChargeType
  case object Increased extends LTAChargeType
  case object Decreased extends LTAChargeType
  case object None extends LTAChargeType

  val values: Seq[LTAChargeType] = Seq(
    New,
    Increased,
    Decreased,
    None
  )

  implicit lazy val enumerable: Enumerable[LTAChargeType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
