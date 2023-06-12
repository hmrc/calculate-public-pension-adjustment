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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac.{PaacTaxYear2011To2015, PaacTaxYear2016PostAlignment, PaacTaxYear2016PreAlignment, PaacTaxYear2017ToCurrent}

trait PaacModelGenerators extends ModelGenerators {

  lazy val genPaacTaxYear2011To2015NormalTaxYearForPeriod: Period => Gen[PaacTaxYear2011To2015.NormalTaxYear] =
    (period: Period) =>
      for {
        pensionInputAmount <- genPensionInputAmount
      } yield PaacTaxYear2011To2015.NormalTaxYear(pensionInputAmount, period)

  lazy val genPaacTaxYear2016PreAlignmentNormalTaxYear: Gen[PaacTaxYear2016PreAlignment.NormalTaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
    } yield PaacTaxYear2016PreAlignment.NormalTaxYear(
      pensionInputAmount
    )

  lazy val genPaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear
    : Gen[PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
    } yield PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      Period._2016PreAlignment
    )

  lazy val genPaacTaxYear2016PostAlignmentNormalTaxYear: Gen[PaacTaxYear2016PostAlignment.NormalTaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
    } yield PaacTaxYear2016PostAlignment.NormalTaxYear(
      pensionInputAmount
    )

  lazy val genPaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear
    : Gen[PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
    } yield PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      Period._2016PostAlignment
    )

  lazy val genPaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear
    : Gen[PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount      <- genDefinedBenefitInputAmount
      definedContributionInputAmount <- genDefinedContributionInputAmount
    } yield PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      definedContributionInputAmount,
      Period._2016PostAlignment
    )

  lazy val genPaacTaxYear2017ToCurrentNormalTaxYearForPeriod: Period => Gen[PaacTaxYear2017ToCurrent.NormalTaxYear] =
    (period: Period) =>
      for {
        pensionInputAmount <- genPensionInputAmount
        income             <- genIncome
      } yield PaacTaxYear2017ToCurrent.NormalTaxYear(
        pensionInputAmount,
        income,
        period
      )

  lazy val genPaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod
    : Period => Gen[PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear] =
    (period: Period) =>
      for {
        definedBenefitInputAmount                <- genDefinedBenefitInputAmount
        preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
        postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
        income                                   <- genIncome
      } yield PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
        definedBenefitInputAmount,
        preAccessDefinedContributionInputAmount,
        postAccessDefinedContributionInputAmount,
        income,
        period
      )

  lazy val genPaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod
    : Period => Gen[PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear] =
    (period: Period) =>
      for {
        definedBenefitInputAmount      <- genDefinedBenefitInputAmount
        definedContributionInputAmount <- genDefinedContributionInputAmount
        income                         <- genIncome
      } yield PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
        definedBenefitInputAmount,
        definedContributionInputAmount,
        income,
        period
      )

}
