/*
 * Copyright 2024 HM Revenue & Customs
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
import org.scalacheck.Gen.*
import uk.gov.hmrc.calculatepublicpensionadjustment.models.IncomeSubJourney
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.*
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa.*

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
      schemeName                <- genSchemeName
      pensionSchemeTaxReference <- genSchemePstr
      revisedPensionInputAmount <- genPensionInputAmount
      chargePaidByScheme        <- genChargePaidBySchemeOrMember
    } yield TaxYearScheme(
      schemeName,
      pensionSchemeTaxReference,
      revisedPensionInputAmount,
      chargePaidByScheme,
      None
    )

  lazy val genIncomeSubJourneyFor2016: Gen[IncomeSubJourney] =
    for {
      taxReliefAmount             <- genIncomeSubJourneyAmounts
      blindPersonsAllowanceAmount <- genIncomeSubJourneyAmounts
    } yield IncomeSubJourney(
      None,
      None,
      None,
      None,
      None,
      taxReliefAmount,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      blindPersonsAllowanceAmount,
      None,
      None
    )

  lazy val genIncomeSubJourney: Gen[IncomeSubJourney] =
    for {
      salarySacrificeAmount                 <- genIncomeSubJourneyAmounts
      flexibleRemunerationAmount            <- genIncomeSubJourneyAmounts
      rASContributionsAmount                <- genIncomeSubJourneyAmounts
      lumpsumDeathBenefitsAmount            <- genIncomeSubJourneyAmounts
      isAboveThreshold                      <- genIsAboveThreshold
      taxReliefAmount                       <- genIncomeSubJourneyAmounts
      adjustedIncomeAmount                  <- genIncomeSubJourneyAmounts
      taxReliefPensionAmount                <- genIncomeSubJourneyAmounts
      personalContributionsAmount           <- genIncomeSubJourneyAmounts
      reliefClaimedOnOverseasPensionsAmount <- genIncomeSubJourneyAmounts
      giftAidAmount                         <- genIncomeSubJourneyAmounts
      personalAllowanceAmount               <- genIncomeSubJourneyAmounts
      tradeUnionOrPoliceReliefAmount        <- genIncomeSubJourneyAmountsLessThan100
      blindPersonsAllowanceAmount           <- genIncomeSubJourneyAmounts
      thresholdAmount                       <- genIncomeSubJourneyAmounts
      reducedNetIncomeAmount                <- genIncomeSubJourneyAmounts
    } yield IncomeSubJourney(
      salarySacrificeAmount,
      flexibleRemunerationAmount,
      rASContributionsAmount,
      lumpsumDeathBenefitsAmount,
      isAboveThreshold,
      taxReliefAmount,
      adjustedIncomeAmount,
      taxReliefPensionAmount,
      personalContributionsAmount,
      reliefClaimedOnOverseasPensionsAmount,
      giftAidAmount,
      personalAllowanceAmount,
      tradeUnionOrPoliceReliefAmount,
      blindPersonsAllowanceAmount,
      thresholdAmount,
      reducedNetIncomeAmount
    )

  lazy val genCppaTaxYear2011To2015ForPeriod: Period => Gen[CppaTaxYear2011To2015] = (period: Period) =>
    for {
      pensionInputAmount <- genPensionInputAmount
    } yield CppaTaxYear2011To2015(pensionInputAmount, period)

  lazy val genCppaTaxYear2016With2016PIA: Gen[CppaTaxYear2016To2023.NormalTaxYear] =
    for {
      pensionInputAmount         <- genPensionInputAmount
      income                     <- genIncome
      totalIncome                <- genTotalIncome
      chargePaidByMember         <- genChargePaidBySchemeOrMember
      incomeSubJourney           <- genIncomeSubJourneyFor2016
      noOfTaxYearSchemes         <- Gen.oneOf(1, 5)
      taxYearSchemes             <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      pensionInput2016PostAmount <- genPensionInputAmount
    } yield CppaTaxYear2016To2023.NormalTaxYear(
      pensionInputAmount,
      taxYearSchemes,
      totalIncome,
      chargePaidByMember,
      Period._2016,
      incomeSubJourney,
      Some(income),
      Some(pensionInput2016PostAmount)
    )

  lazy val genCppaTaxYear2016WithNo2016PIA: Gen[CppaTaxYear2016To2023.NormalTaxYear] =
    for {
      pensionInputAmount <- genPensionInputAmount
      income             <- genIncome
      totalIncome        <- genTotalIncome
      chargePaidByMember <- genChargePaidBySchemeOrMember
      incomeSubJourney   <- genIncomeSubJourneyFor2016
      noOfTaxYearSchemes <- Gen.oneOf(1, 5)
      taxYearSchemes     <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
    } yield CppaTaxYear2016To2023.NormalTaxYear(
      pensionInputAmount,
      taxYearSchemes,
      totalIncome,
      chargePaidByMember,
      Period._2016,
      incomeSubJourney,
      Some(income),
      None
    )

  lazy val genCppaTaxYear2016InitialFlexiblyAccessedWithOptionalAmounts
    : Gen[CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount                        <- genDefinedBenefitInputAmount
      flexiAccessDate                                  <- genFlexiAccessDate(Period._2016PreAlignment)
      preAccessDefinedContributionInputAmount          <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount         <- genPreAndPostAccessDefinedContributionInputAmount
      noOfTaxYearSchemes                               <- Gen.oneOf(1, 5)
      taxYearSchemes                                   <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      incomeSubJourney                                 <- genIncomeSubJourneyFor2016
      definedBenefitInput2016PostAmount                <- genPensionInputAmount
      definedContributionInput2016PostAmount           <- genPensionInputAmount
      postAccessDefinedContributionInput2016PostAmount <- genPensionInputAmount
    } yield CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      Some(flexiAccessDate),
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      taxYearSchemes,
      0,
      0,
      Period._2016,
      incomeSubJourney,
      None,
      Some(definedBenefitInput2016PostAmount),
      Some(definedContributionInput2016PostAmount),
      Some(postAccessDefinedContributionInput2016PostAmount)
    )

  lazy val genCppaTaxYear2016InitialFlexiblyAccessedWithNoOptionalAmounts
    : Gen[CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount                <- genDefinedBenefitInputAmount
      flexiAccessDate                          <- genFlexiAccessDate(Period._2016PreAlignment)
      preAccessDefinedContributionInputAmount  <- genPreAndPostAccessDefinedContributionInputAmount
      postAccessDefinedContributionInputAmount <- genPreAndPostAccessDefinedContributionInputAmount
      noOfTaxYearSchemes                       <- Gen.oneOf(1, 5)
      taxYearSchemes                           <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      incomeSubJourney                         <- genIncomeSubJourneyFor2016
    } yield CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      Some(flexiAccessDate),
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      taxYearSchemes,
      0,
      0,
      Period._2016,
      incomeSubJourney
    )

  lazy val genCppaTaxYear2016PostFlexiblyAccessedWithNoOptionalAmounts
    : Gen[CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount      <- genDefinedBenefitInputAmount
      definedContributionInputAmount <- genDefinedContributionInputAmount
      totalIncome                    <- genTotalIncome
      chargePaidByMember             <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes             <- Gen.oneOf(1, 5)
      taxYearSchemes                 <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      incomeSubJourney               <- genIncomeSubJourneyFor2016
    } yield CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      definedContributionInputAmount,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      Period._2016,
      incomeSubJourney
    )

  lazy val genCppaTaxYear2016PostFlexiblyAccessedWithOptionalAmounts
    : Gen[CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear] =
    for {
      definedBenefitInputAmount              <- genDefinedBenefitInputAmount
      definedContributionInputAmount         <- genDefinedContributionInputAmount
      totalIncome                            <- genTotalIncome
      chargePaidByMember                     <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes                     <- Gen.oneOf(1, 5)
      taxYearSchemes                         <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      incomeSubJourney                       <- genIncomeSubJourneyFor2016
      definedBenefitInput2016PostAmount      <- genDefinedBenefitInputAmount
      definedContributionInput2016PostAmount <- genDefinedContributionInputAmount
    } yield CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      definedContributionInputAmount,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      Period._2016,
      incomeSubJourney,
      None,
      Some(definedBenefitInput2016PostAmount),
      Some(definedContributionInput2016PostAmount)
    )

  lazy val genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod: Period => Gen[CppaTaxYear2016To2023.NormalTaxYear] =
    (period: Period) =>
      for {
        pensionInputAmount <- genPensionInputAmount
        income             <- genIncome
        totalIncome        <- genTotalIncome
        chargePaidByMember <- genChargePaidBySchemeOrMember
        noOfTaxYearSchemes <- Gen.oneOf(1, 5)
        taxYearSchemes     <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
        incomeSubJourney   <- genIncomeSubJourney
      } yield CppaTaxYear2016To2023.NormalTaxYear(
        pensionInputAmount,
        taxYearSchemes,
        totalIncome,
        chargePaidByMember,
        period,
        incomeSubJourney,
        Some(income)
      )

  lazy val genCppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod
    : Period => Gen[CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear] = (period: Period) =>
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
      incomeSubJourney                         <- genIncomeSubJourney
    } yield CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      Some(flexiAccessDate),
      preAccessDefinedContributionInputAmount,
      postAccessDefinedContributionInputAmount,
      taxYearSchemes,
      totalIncome,
      chargePaidByMember,
      period,
      incomeSubJourney,
      Some(income)
    )

  lazy val genCppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod
    : Period => Gen[CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear] = (period: Period) =>
    for {
      definedBenefitInputAmount      <- genDefinedBenefitInputAmount
      definedContributionInputAmount <- genDefinedContributionInputAmount
      income                         <- genIncome
      totalIncome                    <- genTotalIncome
      chargePaidByMember             <- genChargePaidBySchemeOrMember
      noOfTaxYearSchemes             <- Gen.oneOf(1, 5)
      taxYearSchemes                 <- Gen.listOfN(noOfTaxYearSchemes, genTaxYearScheme)
      incomeSubJourney               <- genIncomeSubJourney
    } yield CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
      definedBenefitInputAmount,
      definedContributionInputAmount,
      totalIncome,
      chargePaidByMember,
      taxYearSchemes,
      period,
      incomeSubJourney,
      Some(income)
    )
}
