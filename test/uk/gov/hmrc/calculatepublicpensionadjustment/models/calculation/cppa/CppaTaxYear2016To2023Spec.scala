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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa

import generators.CppaModelGenerators
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

class CppaTaxYear2016To2023Spec extends AnyFreeSpec with ScalaCheckPropertyChecks with CppaModelGenerators {
  private val genValidCppaTaxYear2017ToCurrentPeriod   = Gen.choose(2017, 2023).map(Period.Year)
  private val genInvalidCppaTaxYear2017ToCurrentPeriod = Gen.choose(2013, 2015).map(Period.Year)

  "CppaTaxYear2016ToCurrentNormalTaxYear" - {

    "must deserialise a period fall within 2017 - 2023" in {
      forAll(genValidCppaTaxYear2017ToCurrentPeriod.flatMap(genCppaTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "totalIncome"        -> v.totalIncome,
            "chargePaidByMember" -> v.chargePaidByMember,
            "taxYearSchemes"     -> v.taxYearSchemes,
            "period"             -> v.period,
            "incomeSubJourney"   -> v.incomeSubJourney
          )

          json.validate[CppaTaxYear2016To2023] mustEqual JsSuccess(
            CppaTaxYear2016To2023.NormalTaxYear(
              v.pensionInputAmount,
              v.taxYearSchemes,
              v.totalIncome,
              v.chargePaidByMember,
              v.period,
              v.incomeSubJourney,
              v.income
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
            "period"             -> v.period,
            "incomeSubJourney"   -> v.incomeSubJourney
          )

          json.validate[CppaTaxYear2016To2023] mustEqual JsError("tax year must be `between 2016 and 2023`")
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
            "period"             -> v.period,
            "incomeSubJourney"   -> v.incomeSubJourney
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.NormalTaxYear(
              v.pensionInputAmount,
              v.taxYearSchemes,
              v.totalIncome,
              v.chargePaidByMember,
              v.period,
              v.incomeSubJourney,
              v.income
            )
          ) mustEqual json
      }
    }

    "when period is 2016" - {

      "must deserliase when pensionInput2016PostAmount is none" in {

        forAll(genCppaTaxYear2016WithNo2016PIA) { v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "totalIncome"        -> v.totalIncome,
            "chargePaidByMember" -> v.chargePaidByMember,
            "taxYearSchemes"     -> v.taxYearSchemes,
            "period"             -> v.period,
            "incomeSubJourney"   -> v.incomeSubJourney
          )

          json.validate[CppaTaxYear2016To2023] mustEqual
            JsSuccess(
              CppaTaxYear2016To2023
                .NormalTaxYear(
                  v.pensionInputAmount,
                  v.taxYearSchemes,
                  v.totalIncome,
                  v.chargePaidByMember,
                  v.period,
                  v.incomeSubJourney,
                  v.income
                )
            )
        }

      }

      "must deserialise when pensionInput2016PostAmount is not none" in {

        forAll(genCppaTaxYear2016With2016PIA) { v =>
          val json = Json.obj(
            "pensionInputAmount"         -> v.pensionInputAmount,
            "income"                     -> v.income,
            "totalIncome"                -> v.totalIncome,
            "chargePaidByMember"         -> v.chargePaidByMember,
            "taxYearSchemes"             -> v.taxYearSchemes,
            "period"                     -> v.period,
            "incomeSubJourney"           -> v.incomeSubJourney,
            "pensionInput2016PostAmount" -> v.pensionInput2016PostAmount
          )

          json.validate[CppaTaxYear2016To2023] mustEqual
            JsSuccess(
              CppaTaxYear2016To2023
                .NormalTaxYear(
                  v.pensionInputAmount,
                  v.taxYearSchemes,
                  v.totalIncome,
                  v.chargePaidByMember,
                  v.period,
                  v.incomeSubJourney,
                  v.income,
                  v.pensionInput2016PostAmount
                )
            )
        }

      }

      "must serialise when pensionInput2016PostAmount is none" in {

        forAll(genCppaTaxYear2016WithNo2016PIA) { v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "totalIncome"        -> v.totalIncome,
            "chargePaidByMember" -> v.chargePaidByMember,
            "taxYearSchemes"     -> v.taxYearSchemes,
            "period"             -> v.period,
            "incomeSubJourney"   -> v.incomeSubJourney
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.NormalTaxYear(
              v.pensionInputAmount,
              v.taxYearSchemes,
              v.totalIncome,
              v.chargePaidByMember,
              v.period,
              v.incomeSubJourney,
              v.income
            )
          ) mustEqual json
        }

      }

      "must serialise when pensionInput2016PostAmount is not none" in {

        forAll(genCppaTaxYear2016With2016PIA) { v =>
          val json = Json.obj(
            "pensionInputAmount"         -> v.pensionInputAmount,
            "income"                     -> v.income,
            "totalIncome"                -> v.totalIncome,
            "chargePaidByMember"         -> v.chargePaidByMember,
            "taxYearSchemes"             -> v.taxYearSchemes,
            "period"                     -> v.period,
            "incomeSubJourney"           -> v.incomeSubJourney,
            "pensionInput2016PostAmount" -> v.pensionInput2016PostAmount
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.NormalTaxYear(
              v.pensionInputAmount,
              v.taxYearSchemes,
              v.totalIncome,
              v.chargePaidByMember,
              v.period,
              v.incomeSubJourney,
              v.income,
              v.pensionInput2016PostAmount
            )
          ) mustEqual json
        }
      }
    }
  }

  "CppaTaxYear2016ToCurrentInitialFlexiblyAccessedTaxYear" - {

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
          "period"                                   -> v.period,
          "incomeSubJourney"                         -> v.incomeSubJourney
        )

        json.validate[CppaTaxYear2016To2023] mustEqual JsSuccess(
          CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.flexiAccessDate,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.taxYearSchemes,
            v.totalIncome,
            v.chargePaidByMember,
            v.period,
            v.incomeSubJourney,
            v.income
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
          "period"                                   -> v.period,
          "incomeSubJourney"                         -> v.incomeSubJourney
        )

        json.validate[CppaTaxYear2016To2023] mustEqual JsError("tax year must be `between 2016 and 2023`")
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
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "period"                                   -> v.period,
          "incomeSubJourney"                         -> v.incomeSubJourney,
          "income"                                   -> v.income
        )

        Json.toJson[CppaTaxYear2016To2023](
          CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.flexiAccessDate,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.taxYearSchemes,
            v.totalIncome,
            v.chargePaidByMember,
            v.period,
            v.incomeSubJourney,
            v.income
          )
        ) mustEqual json
      }
    }

    "when period is 2016" - {

      "must deserialise when optional flexibly accessed tax year amounts are none" in {

        forAll(genCppaTaxYear2016InitialFlexiblyAccessedWithNoOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
            "flexiAccessDate"                          -> v.flexiAccessDate,
            "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
            "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
            "taxYearSchemes"                           -> v.taxYearSchemes,
            "totalIncome"                              -> v.totalIncome,
            "chargePaidByMember"                       -> v.chargePaidByMember,
            "period"                                   -> v.period,
            "incomeSubJourney"                         -> v.incomeSubJourney
          )

          json.validate[CppaTaxYear2016To2023] mustEqual
            JsSuccess(
              CppaTaxYear2016To2023
                .InitialFlexiblyAccessedTaxYear(
                  v.definedBenefitInputAmount,
                  v.flexiAccessDate,
                  v.preAccessDefinedContributionInputAmount,
                  v.postAccessDefinedContributionInputAmount,
                  v.taxYearSchemes,
                  v.totalIncome,
                  v.chargePaidByMember,
                  v.period,
                  v.incomeSubJourney
                )
            )
        }
      }

      "must deserialise when optional flexibly accessed tax year amounts are not none" in {

        forAll(genCppaTaxYear2016InitialFlexiblyAccessedWithOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"                        -> v.definedBenefitInputAmount,
            "flexiAccessDate"                                  -> v.flexiAccessDate,
            "preAccessDefinedContributionInputAmount"          -> v.preAccessDefinedContributionInputAmount,
            "postAccessDefinedContributionInputAmount"         -> v.postAccessDefinedContributionInputAmount,
            "taxYearSchemes"                                   -> v.taxYearSchemes,
            "totalIncome"                                      -> v.totalIncome,
            "chargePaidByMember"                               -> v.chargePaidByMember,
            "period"                                           -> v.period,
            "incomeSubJourney"                                 -> v.incomeSubJourney,
            "definedBenefitInput2016PostAmount"                -> v.definedBenefitInput2016PostAmount,
            "definedContributionInput2016PostAmount"           -> v.definedContributionInput2016PostAmount,
            "postAccessDefinedContributionInput2016PostAmount" -> v.postAccessDefinedContributionInput2016PostAmount
          )

          json.validate[CppaTaxYear2016To2023] mustEqual
            JsSuccess(
              CppaTaxYear2016To2023
                .InitialFlexiblyAccessedTaxYear(
                  v.definedBenefitInputAmount,
                  v.flexiAccessDate,
                  v.preAccessDefinedContributionInputAmount,
                  v.postAccessDefinedContributionInputAmount,
                  v.taxYearSchemes,
                  v.totalIncome,
                  v.chargePaidByMember,
                  v.period,
                  v.incomeSubJourney,
                  None,
                  v.definedBenefitInput2016PostAmount,
                  v.definedContributionInput2016PostAmount,
                  v.postAccessDefinedContributionInput2016PostAmount
                )
            )
        }

      }

      "must serialise when optional flexibly accessed tax year amounts are none" in {

        forAll(genCppaTaxYear2016InitialFlexiblyAccessedWithNoOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
            "flexiAccessDate"                          -> v.flexiAccessDate,
            "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
            "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
            "taxYearSchemes"                           -> v.taxYearSchemes,
            "totalIncome"                              -> v.totalIncome,
            "chargePaidByMember"                       -> v.chargePaidByMember,
            "period"                                   -> v.period,
            "incomeSubJourney"                         -> v.incomeSubJourney
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
              v.definedBenefitInputAmount,
              v.flexiAccessDate,
              v.preAccessDefinedContributionInputAmount,
              v.postAccessDefinedContributionInputAmount,
              v.taxYearSchemes,
              v.totalIncome,
              v.chargePaidByMember,
              v.period,
              v.incomeSubJourney
            )
          ) mustEqual json
        }

      }

      "must serialise when optional flexibly accessed tax year amounts are not none" in {

        forAll(genCppaTaxYear2016InitialFlexiblyAccessedWithOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"                        -> v.definedBenefitInputAmount,
            "flexiAccessDate"                                  -> v.flexiAccessDate,
            "preAccessDefinedContributionInputAmount"          -> v.preAccessDefinedContributionInputAmount,
            "postAccessDefinedContributionInputAmount"         -> v.postAccessDefinedContributionInputAmount,
            "taxYearSchemes"                                   -> v.taxYearSchemes,
            "totalIncome"                                      -> v.totalIncome,
            "chargePaidByMember"                               -> v.chargePaidByMember,
            "period"                                           -> v.period,
            "incomeSubJourney"                                 -> v.incomeSubJourney,
            "definedBenefitInput2016PostAmount"                -> v.definedBenefitInput2016PostAmount,
            "definedContributionInput2016PostAmount"           -> v.definedContributionInput2016PostAmount,
            "postAccessDefinedContributionInput2016PostAmount" -> v.postAccessDefinedContributionInput2016PostAmount
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
              v.definedBenefitInputAmount,
              v.flexiAccessDate,
              v.preAccessDefinedContributionInputAmount,
              v.postAccessDefinedContributionInputAmount,
              v.taxYearSchemes,
              v.totalIncome,
              v.chargePaidByMember,
              v.period,
              v.incomeSubJourney,
              None,
              v.definedBenefitInput2016PostAmount,
              v.definedContributionInput2016PostAmount,
              v.postAccessDefinedContributionInput2016PostAmount
            )
          ) mustEqual json
        }
      }
    }
  }

  "CppaTaxYear2016ToCurrentPostFlexiblyAccessedTaxYear" - {

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
          "period"                         -> v.period,
          "incomeSubJourney"               -> v.incomeSubJourney
        )

        json.validate[CppaTaxYear2016To2023] mustEqual JsSuccess(
          CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period,
            v.incomeSubJourney,
            v.income
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
          "period"                         -> v.period,
          "incomeSubJourney"               -> v.incomeSubJourney
        )

        json.validate[CppaTaxYear2016To2023] mustEqual JsError("tax year must be `between 2016 and 2023`")
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
          "period"                         -> v.period,
          "incomeSubJourney"               -> v.incomeSubJourney
        )

        Json.toJson[CppaTaxYear2016To2023](
          CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period,
            v.incomeSubJourney,
            v.income
          )
        ) mustEqual json
      }
    }

    "when period is 2016" - {

      "must deserialise when optional flexibly accessed tax year amounts are none" in {

        forAll(genCppaTaxYear2016PostFlexiblyAccessedWithNoOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
            "definedContributionInputAmount" -> v.definedContributionInputAmount,
            "totalIncome"                    -> v.totalIncome,
            "chargePaidByMember"             -> v.chargePaidByMember,
            "taxYearSchemes"                 -> v.taxYearSchemes,
            "period"                         -> v.period,
            "incomeSubJourney"               -> v.incomeSubJourney
          )

          json.validate[CppaTaxYear2016To2023] mustEqual
            JsSuccess(
              CppaTaxYear2016To2023
                .PostFlexiblyAccessedTaxYear(
                  v.definedBenefitInputAmount,
                  v.definedContributionInputAmount,
                  v.totalIncome,
                  v.chargePaidByMember,
                  v.taxYearSchemes,
                  v.period,
                  v.incomeSubJourney
                )
            )
        }
      }

      "must deserialise when optional flexibly accessed tax year amounts are not none" in {

        forAll(genCppaTaxYear2016PostFlexiblyAccessedWithOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"              -> v.definedBenefitInputAmount,
            "definedContributionInputAmount"         -> v.definedContributionInputAmount,
            "totalIncome"                            -> v.totalIncome,
            "chargePaidByMember"                     -> v.chargePaidByMember,
            "taxYearSchemes"                         -> v.taxYearSchemes,
            "period"                                 -> v.period,
            "incomeSubJourney"                       -> v.incomeSubJourney,
            "definedBenefitInput2016PostAmount"      -> v.definedBenefitInput2016PostAmount,
            "definedContributionInput2016PostAmount" -> v.definedContributionInput2016PostAmount
          )

          json.validate[CppaTaxYear2016To2023] mustEqual
            JsSuccess(
              CppaTaxYear2016To2023
                .PostFlexiblyAccessedTaxYear(
                  v.definedBenefitInputAmount,
                  v.definedContributionInputAmount,
                  v.totalIncome,
                  v.chargePaidByMember,
                  v.taxYearSchemes,
                  v.period,
                  v.incomeSubJourney,
                  None,
                  v.definedBenefitInput2016PostAmount,
                  v.definedContributionInput2016PostAmount
                )
            )
        }
      }

      "must serialise when optional flexibly accessed tax year amounts are none" in {

        forAll(genCppaTaxYear2016PostFlexiblyAccessedWithNoOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
            "definedContributionInputAmount" -> v.definedContributionInputAmount,
            "totalIncome"                    -> v.totalIncome,
            "chargePaidByMember"             -> v.chargePaidByMember,
            "taxYearSchemes"                 -> v.taxYearSchemes,
            "period"                         -> v.period,
            "incomeSubJourney"               -> v.incomeSubJourney
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
              v.definedBenefitInputAmount,
              v.definedContributionInputAmount,
              v.totalIncome,
              v.chargePaidByMember,
              v.taxYearSchemes,
              v.period,
              v.incomeSubJourney
            )
          ) mustEqual json
        }

      }

      "must serialise when optional flexibly accessed tax year amounts are not none" in {

        forAll(genCppaTaxYear2016PostFlexiblyAccessedWithOptionalAmounts) { v =>
          val json = Json.obj(
            "definedBenefitInputAmount"              -> v.definedBenefitInputAmount,
            "definedContributionInputAmount"         -> v.definedContributionInputAmount,
            "totalIncome"                            -> v.totalIncome,
            "chargePaidByMember"                     -> v.chargePaidByMember,
            "taxYearSchemes"                         -> v.taxYearSchemes,
            "period"                                 -> v.period,
            "incomeSubJourney"                       -> v.incomeSubJourney,
            "definedBenefitInput2016PostAmount"      -> v.definedBenefitInput2016PostAmount,
            "definedContributionInput2016PostAmount" -> v.definedContributionInput2016PostAmount
          )

          Json.toJson[CppaTaxYear2016To2023](
            CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
              v.definedBenefitInputAmount,
              v.definedContributionInputAmount,
              v.totalIncome,
              v.chargePaidByMember,
              v.taxYearSchemes,
              v.period,
              v.incomeSubJourney,
              None,
              v.definedBenefitInput2016PostAmount,
              v.definedContributionInput2016PostAmount
            )
          ) mustEqual json
        }
      }
    }

  }
}
