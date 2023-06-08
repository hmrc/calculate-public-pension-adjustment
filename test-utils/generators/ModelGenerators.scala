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

package generators

import org.scalacheck.Gen
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Income

trait ModelGenerators {

  lazy val genPensionInputAmount: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 100000
    )

  lazy val genDefinedBenefitInputAmount: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 40000
    )

  lazy val genPreAndPostAccessDefinedContributionInputAmount: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 20000
    )

  lazy val genDefinedContributionInputAmount: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 20000
    )

  lazy val genAdjustedIncome: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 100000
    )

  lazy val genIncome: Gen[Income] =
    Gen.oneOf(
      Gen.const(Income.BelowThreshold),
      genAdjustedIncome.map(Income.AboveThreshold)
    )

}
