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
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

class CppaTaxYear2016PostAlignmentSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with CppaModelGenerators {

  "CppaTaxYear2016PostAlignmentNormalTaxYear" - {

    "must deserialise CppaTaxYear2016PostAlignmentNormalTaxYear with a period 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "taxYearSchemes"     -> v.taxYearSchemes,
          "totalIncome"        -> v.totalIncome,
          "chargePaidByMember" -> v.chargePaidByMember,
          "period"             -> v.period
        )

        json.validate[CppaTaxYear2016PostAlignment] mustEqual
          JsSuccess(
            CppaTaxYear2016PostAlignment
              .NormalTaxYear(v.pensionInputAmount, v.totalIncome, v.chargePaidByMember, v.taxYearSchemes, v.period)
          )
      }
    }

    "must fail to deserialise CppaTaxYear2016PostAlignmentNormalTaxYear with a period other than 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "taxYearSchemes"     -> v.taxYearSchemes,
          "totalIncome"        -> v.totalIncome,
          "chargePaidByMember" -> v.chargePaidByMember,
          "period"             -> Period._2016PreAlignment.toString
        )

        json.validate[CppaTaxYear2016PostAlignment] mustEqual JsError("tax year must be `2016-post`")
      }
    }

    "must serialise a period with a period 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "taxYearSchemes"     -> v.taxYearSchemes,
          "totalIncome"        -> v.totalIncome,
          "chargePaidByMember" -> v.chargePaidByMember,
          "period"             -> Period._2016PostAlignment.toString
        )

        Json.toJson[CppaTaxYear2016PostAlignment](
          CppaTaxYear2016PostAlignment.NormalTaxYear(
            v.pensionInputAmount,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period
          )
        ) mustEqual json
      }
    }

  }

  "CppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear" - {

    "must deserialise CppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear with a period 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "period"                                   -> v.period
        )

        json.validate[CppaTaxYear2016PostAlignment] mustEqual
          JsSuccess(
            CppaTaxYear2016PostAlignment
              .InitialFlexiblyAccessedTaxYear(
                v.definedBenefitInputAmount,
                v.flexiAccessDate,
                v.preAccessDefinedContributionInputAmount,
                v.postAccessDefinedContributionInputAmount,
                v.totalIncome,
                v.chargePaidByMember,
                v.taxYearSchemes,
                v.period
              )
          )
      }
    }

    "must fail to deserialise CppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear with a period other than 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "period"                                   -> Period._2016PreAlignment.toString
        )

        json.validate[CppaTaxYear2016PostAlignment] mustEqual JsError("tax year must be `2016-post`")
      }
    }

    "must serialise a period with a period 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "period"                                   -> Period._2016PostAlignment.toString
        )

        Json.toJson[CppaTaxYear2016PostAlignment](
          CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.flexiAccessDate,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.totalIncome,
            v.chargePaidByMember,
            v.taxYearSchemes,
            v.period
          )
        ) mustEqual json
      }
    }

  }

  "CppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear" - {

    "must deserialise CppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear with a period 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "totalIncome"                    -> v.totalIncome,
          "chargePaidByMember"             -> v.chargePaidByMember,
          "taxYearSchemes"                 -> v.taxYearSchemes,
          "period"                         -> v.period
        )

        json.validate[CppaTaxYear2016PostAlignment] mustEqual
          JsSuccess(
            CppaTaxYear2016PostAlignment
              .PostFlexiblyAccessedTaxYear(
                v.definedBenefitInputAmount,
                v.definedContributionInputAmount,
                v.totalIncome,
                v.chargePaidByMember,
                v.taxYearSchemes,
                v.period
              )
          )
      }
    }

    "must fail to deserialise CppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear with a period other than 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "totalIncome"                    -> v.totalIncome,
          "chargePaidByMember"             -> v.chargePaidByMember,
          "taxYearSchemes"                 -> v.taxYearSchemes,
          "period"                         -> Period._2016PreAlignment.toString
        )

        json.validate[CppaTaxYear2016PostAlignment] mustEqual JsError("tax year must be `2016-post`")
      }
    }

    "must serialise a period with a period 2016-post" in {
      forAll(genCppaTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "totalIncome"                    -> v.totalIncome,
          "chargePaidByMember"             -> v.chargePaidByMember,
          "taxYearSchemes"                 -> v.taxYearSchemes,
          "period"                         -> Period._2016PostAlignment.toString
        )

        Json.toJson[CppaTaxYear2016PostAlignment](
          CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
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
