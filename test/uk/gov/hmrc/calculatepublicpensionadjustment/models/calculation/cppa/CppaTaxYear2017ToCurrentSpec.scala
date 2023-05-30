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

import generators.CppaModelGenerators
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

class CppaTaxYear2017ToCurrentSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with CppaModelGenerators {

  private val genValidCppaTaxYear2017ToCurrentPeriod   = Gen.choose(2017, 2023).map(Period.Year)
  private val genInvalidCppaTaxYear2017ToCurrentPeriod = Gen.choose(2013, 2015).map(Period.Year)

  "CppaTaxYear2017ToCurrentNormalTaxYear" - {

    "must deserialise a period fall within 2017 - 2023" in {
      forAll(genValidCppaTaxYear2017ToCurrentPeriod.flatMap(genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "totalIncome"        -> v.totalIncome,
            "chargePaidByMember" -> v.chargePaidByMember,
            "taxYearSchemes"     -> v.taxYearSchemes,
            "period"             -> v.period
          )

          json.validate[CppaTaxYear2017ToCurrent] mustEqual JsSuccess(
            CppaTaxYear2017ToCurrent.NormalTaxYear(
              v.pensionInputAmount,
              v.income,
              v.totalIncome,
              v.chargePaidByMember,
              v.taxYearSchemes,
              v.period
            )
          )
      }

    }

    "must fail to deserialise a period fall outside 2017 - 2023" in {
      forAll(genInvalidCppaTaxYear2017ToCurrentPeriod.flatMap(genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "totalIncome"        -> v.totalIncome,
            "chargePaidByMember" -> v.chargePaidByMember,
            "taxYearSchemes"     -> v.taxYearSchemes,
            "period"             -> v.period
          )

          json.validate[CppaTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")
      }

    }

    "must serialise a period fall within 2017 - 2023" in {
      forAll(genValidCppaTaxYear2017ToCurrentPeriod.flatMap(genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "totalIncome"        -> v.totalIncome,
            "chargePaidByMember" -> v.chargePaidByMember,
            "taxYearSchemes"     -> v.taxYearSchemes,
            "period"             -> v.period
          )

          Json.toJson[CppaTaxYear2017ToCurrent](
            CppaTaxYear2017ToCurrent.NormalTaxYear(
              v.pensionInputAmount,
              v.income,
              v.totalIncome,
              v.chargePaidByMember,
              v.taxYearSchemes,
              v.period
            )
          ) mustEqual json
      }
    }

  }

  "CppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYear" - {

    "must deserialise a period fall within 2017 - 2023" in {
      forAll(
        genValidCppaTaxYear2017ToCurrentPeriod.flatMap(
          genCppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "income"                                   -> v.income,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "period"                                   -> v.period
        )

        json.validate[CppaTaxYear2017ToCurrent] mustEqual JsSuccess(
          CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.flexiAccessDate,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.income,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period
          )
        )
      }

    }

    "must fail to deserialise a period fall outside 2017 - 2023" in {
      forAll(
        genInvalidCppaTaxYear2017ToCurrentPeriod.flatMap(
          genCppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "income"                                   -> v.income,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "period"                                   -> v.period
        )

        json.validate[CppaTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")
      }

    }

    "must serialise a period fall within 2017 - 2023" in {
      forAll(
        genValidCppaTaxYear2017ToCurrentPeriod.flatMap(
          genCppaTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "income"                                   -> v.income,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "period"                                   -> v.period
        )

        Json.toJson[CppaTaxYear2017ToCurrent](
          CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.flexiAccessDate,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.income,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period
          )
        ) mustEqual json
      }
    }

  }

  "CppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYear" - {

    "must deserialise a period fall within 2017 - 2023" in {
      forAll(
        genValidCppaTaxYear2017ToCurrentPeriod.flatMap(
          genCppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "income"                         -> v.income,
          "totalIncome"                    -> v.totalIncome,
          "chargePaidByMember"             -> v.chargePaidByMember,
          "taxYearSchemes"                 -> v.taxYearSchemes,
          "period"                         -> v.period
        )

        json.validate[CppaTaxYear2017ToCurrent] mustEqual JsSuccess(
          CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.income,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period
          )
        )
      }

    }

    "must fail to deserialise a period fall outside 2017 - 2023" in {
      forAll(
        genInvalidCppaTaxYear2017ToCurrentPeriod.flatMap(
          genCppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "income"                         -> v.income,
          "totalIncome"                    -> v.totalIncome,
          "chargePaidByMember"             -> v.chargePaidByMember,
          "taxYearSchemes"                 -> v.taxYearSchemes,
          "period"                         -> v.period
        )

        json.validate[CppaTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")
      }

    }

    "must serialise a period fall within 2017 - 2023" in {
      forAll(
        genValidCppaTaxYear2017ToCurrentPeriod.flatMap(
          genCppaTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "income"                         -> v.income,
          "totalIncome"                    -> v.totalIncome,
          "chargePaidByMember"             -> v.chargePaidByMember,
          "taxYearSchemes"                 -> v.taxYearSchemes,
          "period"                         -> v.period
        )

        Json.toJson[CppaTaxYear2017ToCurrent](
          CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.income,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period
          )
        ) mustEqual json
      }
    }

  }

}
