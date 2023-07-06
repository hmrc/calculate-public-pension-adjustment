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

import uk.gov.hmrc.calculatepublicpensionadjustment.models.Enumerable

sealed trait HowPaidLTACharge

object HowPaidLTACharge extends Enumerable.Implicits {

  case object AnnualPayment extends HowPaidLTACharge
  case object LumpSum extends HowPaidLTACharge

  val values: Seq[HowPaidLTACharge] = Seq(
    AnnualPayment,
    LumpSum
  )

  implicit val enumerable: Enumerable[HowPaidLTACharge] =
    Enumerable(values.map(v => v.toString -> v): _*)
}
