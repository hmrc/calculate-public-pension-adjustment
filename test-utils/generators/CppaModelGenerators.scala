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
import org.scalacheck.Gen._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._

import java.time.LocalDate

trait CppaModelGenerators extends ModelGenerators {

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
    stringOfN(10, alphaChar)

  lazy val genSchemePstr: Gen[String] =
    stringOfN(10, alphaNumChar)

  lazy val genFlexiAccessDate: Period => Gen[LocalDate] = {
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

  lazy val genTaxYearScheme: Gen[TaxYearScheme] =
    for {
      schemeName                 <- genSchemeName
      pensionSchemeTaxReference  <- genSchemePstr
      originalPensionInputAmount <- genPensionInputAmount
      revisedPensionInputAmount  <- genPensionInputAmount
      chargePaidByScheme         <- genChargePaidBySchemeOrMember
    } yield TaxYearScheme(
      schemeName,
      pensionSchemeTaxReference,
      originalPensionInputAmount,
      revisedPensionInputAmount,
      chargePaidByScheme
    )

  lazy val genCppaTaxYear2013To2015ForPeriod: Period => Gen[CppaTaxYear2011To2015] = (period: Period) =>
    for {
      pensionInputAmount <- genPensionInputAmount
    } yield CppaTaxYear2011To2015(pensionInputAmount, period)

  lazy val genCppaTaxYear2016PreAlignmentNormalTaxYear: Gen[CppaTaxYear2016PreAlignment.NormalTaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
      noOfTaxYearSchemes <- Gen.oneOf(1, 5)
      taxYearSchemes     <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2016PreAlignment.NormalTaxYear(
      pensionInputAmount,
      taxYearSchemes,
      0,
      0,
      Period._2016PreAlignment
    )

  lazy val genCppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear
    : Gen[CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genFlexiAccessDate(Period._2016PreAlignment)
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
      noOfTaxYearSchemes                       <- Gen.oneOf(1, 5)
      taxYearSchemes                           <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      flexiAccessDate,
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      taxYearSchemes,
      0,
      0,
      Period._2016PreAlignment
    )

  lazy val genCppaTaxYear2016PostAlignmentNormalTaxYear: Gen[CppaTaxYear2016PostAlignment.NormalTaxYear] =
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

  lazy val genCppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear
    : Gen[CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genFlexiAccessDate(Period._2016PostAlignment)
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

  lazy val genCppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear
    : Gen[CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear] =
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

  lazy val genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod: Period => Gen[CppaTaxYear2017ToCurrent.NormalTaxYear] =
    (period: Period) =>
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

  lazy val genCppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod
    : Period => Gen[CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear] = (period: Period) =>
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genFlexiAccessDate(period)
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

  lazy val genCppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod
    : Period => Gen[CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear] = (period: Period) =>
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
