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
import org.scalacheck.Gen.{alphaNumStr, alphaStr}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._

import java.time.LocalDate

trait ModelGenerators extends Generators {

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

  lazy val genDefinedContributionInputAmount: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 20000
    )

  lazy val genPreAndPostAccessDefinedContributionInputAmount: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 20000
    )

  lazy val genTotalIncome: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 160000
    )

  lazy val genChargePaidBySchemeOrMember: Gen[Int] =
    Gen.chooseNum(
      minT = 0,
      maxT = 10000
    )

  lazy val genSchemeName: Gen[String] =
    alphaStr.map(_.substring(0, 10))

  lazy val genSchemePstr: Gen[String] =
    alphaNumStr.map(_.substring(0, 10))

  def genLocalDate(period: Period): Gen[LocalDate] =
    period match {
      case Period._2013              => Gen.choose(LocalDate.parse("2012-04-06"), LocalDate.parse("2013-04-05"))
      case Period._2014              => Gen.choose(LocalDate.parse("2013-04-06"), LocalDate.parse("2014-04-05"))
      case Period._2015              => Gen.choose(LocalDate.parse("2014-04-06"), LocalDate.parse("2015-04-05"))
      case Period._2016PreAlignment  => Gen.choose(LocalDate.parse("2015-04-06"), LocalDate.parse("2015-07-08"))
      case Period._2016PostAlignment => Gen.choose(LocalDate.parse("2015-07-09"), LocalDate.parse("2016-04-05"))
      case Period._2017              => Gen.choose(LocalDate.parse("2016-04-06"), LocalDate.parse("2017-04-05"))
      case Period._2018              => Gen.choose(LocalDate.parse("2017-04-06"), LocalDate.parse("2018-04-05"))
      case Period._2019              => Gen.choose(LocalDate.parse("2018-04-06"), LocalDate.parse("2019-04-05"))
      case Period._2020              => Gen.choose(LocalDate.parse("2019-04-06"), LocalDate.parse("2020-04-05"))
      case Period._2021              => Gen.choose(LocalDate.parse("2020-04-06"), LocalDate.parse("2021-04-05"))
      case Period._2022              => Gen.choose(LocalDate.parse("2021-04-06"), LocalDate.parse("2022-04-05"))
      case Period._2023              => Gen.choose(LocalDate.parse("2022-04-06"), LocalDate.parse("2023-04-05"))

    }

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

  val genPeriod: Gen[Period] =
    Gen.oneOf(
      Period._2016PreAlignment,
      Period._2016PostAlignment,
      Period._2017,
      Period._2018,
      Period._2019,
      Period._2020,
      Period._2021,
      Period._2022,
      Period._2023
    )

  def genCppaTaxYear2013To2015ForPeriod(period: Period): Gen[TaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
    } yield CppaTaxYear2013To2015(pensionInputAmount, period)

  def genTaxYearScheme: Gen[TaxYearScheme] =
    for {
      schemeName          <- genSchemeName
      schemePstr          <- genSchemePstr
      oPensionInputAmount <- genPensionInputAmount
      rPensionInputAmount <- genPensionInputAmount
      chargePaidByScheme  <- genChargePaidBySchemeOrMember
    } yield TaxYearScheme(schemeName, schemePstr, oPensionInputAmount, rPensionInputAmount, chargePaidByScheme)

  def genCppaTaxYear2016PreAlignmentNormalTaxYear: Gen[TaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
      noOfTaxYearSchemes <- Gen.oneOf(1, 5)
      taxYearSchemes     <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      totalIncome        <- genTotalIncome
      chargePaidByMember <- genChargePaidBySchemeOrMember
    } yield CppaTaxYear2016PreAlignment.NormalTaxYear(
      pensionInputAmount,
      taxYearSchemes,
      totalIncome,
      chargePaidByMember,
      Period._2016PreAlignment
    )

  def genCppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear: Gen[TaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genLocalDate(Period._2016PreAlignment)
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
      noOfTaxYearSchemes                       <- Gen.oneOf(1, 5)
      taxYearSchemes                           <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      totalIncome                              <- genTotalIncome
      chargePaidByMember                       <- genChargePaidBySchemeOrMember
    } yield CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      flexiAccessDate,
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      taxYearSchemes,
      totalIncome,
      chargePaidByMember,
      Period._2016PreAlignment
    )

  def genCppaTaxYear2016PostAlignmentNormalTaxYear: Gen[TaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
      totalIncome        <- genTotalIncome
      chargePaidByMember <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes <- Gen.oneOf(1, 5)
      taxYearSchemes     <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2016PostAlignment.NormalTaxYear(
      pensionInputAmount,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      Period._2016PostAlignment
    )

  def genCppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear: Gen[TaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genLocalDate(Period._2016PostAlignment)
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
      totalIncome                              <- genTotalIncome
      chargePaidByMember                       <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes                       <- Gen.oneOf(1, 5)
      taxYearSchemes                           <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      flexiAccessDate,
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      Period._2016PostAlignment
    )

  def genCppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear: Gen[TaxYear] =
    for {
      definedBenefitInputAmount      <- genDefinedBenefitInputAmount
      definedContributionInputAmount <- genDefinedContributionInputAmount
      totalIncome                    <- genTotalIncome
      chargePaidByMember             <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes             <- Gen.oneOf(1, 5)
      taxYearSchemes                 <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      definedContributionInputAmount,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      Period._2016PostAlignment
    )

  def genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod(period: Period): Gen[TaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
      income             <- genIncome
      totalIncome        <- genTotalIncome
      chargePaidByMember <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes <- Gen.oneOf(1, 5)
      taxYearSchemes     <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2017ToCurrent.NormalTaxYear(
      pensionInputAmount,
      income,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      period
    )

  def genCppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(period: Period): Gen[TaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genLocalDate(period)
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
      income                                   <- genIncome
      totalIncome                              <- genTotalIncome
      chargePaidByMember                       <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes                       <- Gen.oneOf(1, 5)
      taxYearSchemes                           <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      flexiAccessDate,
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      income,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      period
    )

  def genCppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(period: Period): Gen[TaxYear] =
    for {
      definedBenefitInputAmount      <- genDefinedBenefitInputAmount
      definedContributionInputAmount <- genDefinedContributionInputAmount
      income                         <- genIncome
      totalIncome                    <- genTotalIncome
      chargePaidByMember             <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes             <- Gen.oneOf(1, 5)
      taxYearSchemes                 <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      definedContributionInputAmount,
      income,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      period
    )
}
