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

class CppaTaxYear2016PreAlignmentSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with CppaModelGenerators {

  "CppaTaxYear2016PreAlignmentNormalTaxYear" - {

    "must deserialise CppaTaxYear2016PreAlignmentNormalTaxYear with a period 2016-pre" in {
      forAll(genCppaTaxYear2016PreAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "taxYearSchemes"     -> v.taxYearSchemes,
          "totalIncome"        -> v.totalIncome,
          "chargePaidByMember" -> v.chargePaidByMember,
          "period"             -> v.period
        )

        json.validate[CppaTaxYear2016PreAlignment] mustEqual
          JsSuccess(
            CppaTaxYear2016PreAlignment
              .NormalTaxYear(v.pensionInputAmount, v.taxYearSchemes, v.totalIncome, v.chargePaidByMember, v.period)
          )
      }
    }

    "must fail to deserialise CppaTaxYear2016PreAlignmentNormalTaxYear with a period other than 2016-pre" in {
      forAll(genCppaTaxYear2016PreAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "taxYearSchemes"     -> v.taxYearSchemes,
          "totalIncome"        -> v.totalIncome,
          "chargePaidByMember" -> v.chargePaidByMember,
          "period"             -> Period._2016PostAlignment.toString
        )

        json.validate[CppaTaxYear2016PreAlignment] mustEqual JsError("tax year must be `2016-pre`")
      }
    }

    "must serialise a period with a period 2016-pre" in {
      forAll(genCppaTaxYear2016PreAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "taxYearSchemes"     -> v.taxYearSchemes,
          "totalIncome"        -> v.totalIncome,
          "chargePaidByMember" -> v.chargePaidByMember,
          "period"             -> v.period.toString
        )

        Json.toJson[CppaTaxYear2016PreAlignment](
          CppaTaxYear2016PreAlignment.NormalTaxYear(
            v.pensionInputAmount,
            v.taxYearSchemes,
            v.totalIncome,
            v.chargePaidByMember,
            v.period
          )
        ) mustEqual json
      }
    }

  }

  "CppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear" - {

    "must deserialise CppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear with a period 2016-pre" in {
      forAll(genCppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear) { v =>
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

        json.validate[CppaTaxYear2016PreAlignment] mustEqual
          JsSuccess(
            CppaTaxYear2016PreAlignment
              .InitialFlexiblyAccessedTaxYear(
                v.definedBenefitInputAmount,
                v.flexiAccessDate,
                v.preAccessDefinedContributionInputAmount,
                v.postAccessDefinedContributionInputAmount,
                v.taxYearSchemes,
                v.totalIncome,
                v.chargePaidByMember,
                v.period
              )
          )
      }
    }

    "must fail to deserialise CppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear with a period other than 2016-pre" in {
      forAll(genCppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear) { v =>
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

        json.validate[CppaTaxYear2016PreAlignment] mustEqual JsError("tax year must be `2016-pre`")
      }
    }

    "must serialise a period with a period 2016-pre" in {
      forAll(genCppaTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "flexiAccessDate"                          -> v.flexiAccessDate,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "taxYearSchemes"                           -> v.taxYearSchemes,
          "totalIncome"                              -> v.totalIncome,
          "chargePaidByMember"                       -> v.chargePaidByMember,
          "period"                                   -> v.period.toString
        )

        Json.toJson[CppaTaxYear2016PreAlignment](
          CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.flexiAccessDate,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.taxYearSchemes,
            v.totalIncome,
            v.chargePaidByMember,
            v.period
          )
        ) mustEqual json
      }
    }

  }

}
