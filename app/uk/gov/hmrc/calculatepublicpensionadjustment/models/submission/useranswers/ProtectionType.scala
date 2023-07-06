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

sealed trait ProtectionType

object ProtectionType extends Enumerable.Implicits {

  case object EnhancedProtection extends ProtectionType
  case object PrimaryProtection extends ProtectionType
  case object FixedProtection extends ProtectionType
  case object FixedProtection2014 extends ProtectionType
  case object FixedProtection2016 extends ProtectionType
  case object IndividualProtection2014 extends ProtectionType
  case object IndividualProtection2016 extends ProtectionType
  case object InternationalEnhancement extends ProtectionType
  case object PensionCredit extends ProtectionType

  val values: Seq[ProtectionType] = Seq(
    EnhancedProtection,
    PrimaryProtection,
    FixedProtection,
    FixedProtection2014,
    FixedProtection2016,
    IndividualProtection2014,
    IndividualProtection2016,
    InternationalEnhancement,
    PensionCredit
  )

  implicit val enumerable: Enumerable[ProtectionType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
