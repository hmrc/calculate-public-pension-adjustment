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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac

import generators.PaacModelGenerators
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

class PaacTaxYear2016PreAlignmentSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with PaacModelGenerators {

  "PaacTaxYear2016PreAlignmentNormalTaxYear" - {

    "must deserialise PaacTaxYear2016PreAlignmentNormalTaxYear with a period 2016-pre" in {
      forAll(genPaacTaxYear2016PreAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        json.validate[PaacTaxYear2016PreAlignment] mustEqual JsSuccess(
          PaacTaxYear2016PreAlignment.NormalTaxYear(v.pensionInputAmount, v.period)
        )
      }

    }

    "must fail to deserialise PaacTaxYear2016PreAlignmentNormalTaxYear with a period other than 2016-pre" in {
      forAll(genPaacTaxYear2016PreAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> Period._2016PostAlignment.toString
        )

        json.validate[PaacTaxYear2016PreAlignment] mustEqual JsError("tax year must be `2016-pre`")
      }

    }

    "must serialise a PaacTaxYear2016PreAlignmentNormalTaxYear with a period 2016-pre" in {
      forAll(genPaacTaxYear2016PreAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        Json.toJson[PaacTaxYear2016PreAlignment](
          PaacTaxYear2016PreAlignment.NormalTaxYear(v.pensionInputAmount, v.period)
        ) mustEqual json
      }

    }

  }

  "PaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear" - {

    "must deserialise PaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear with a period 2016-pre" in {
      forAll(genPaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "period"                                   -> v.period.toString
        )

        json.validate[PaacTaxYear2016PreAlignment] mustEqual JsSuccess(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.period
          )
        )
      }

    }

    "must fail to deserialise PaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear with a period other than 2016-pre" in {
      forAll(genPaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "period"                                   -> Period._2016PostAlignment.toString
        )

        json.validate[PaacTaxYear2016PreAlignment] mustEqual JsError("tax year must be `2016-pre`")
      }

    }

    "must serialise a PaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear with a period 2016-pre" in {
      forAll(genPaacTaxYear2016PreAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "period"                                   -> v.period.toString
        )

        Json.toJson[PaacTaxYear2016PreAlignment](
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.period
          )
        ) mustEqual json

      }

    }

  }

  "PaacTaxYear2016PreAlignmentNoInputTaxYear" - {

    "must deserialise PaacTaxYear2016PreAlignmentNoInputTaxYear with a period 2016-pre" in {
      val json = Json.obj(
        "period" -> Period._2016PreAlignment.toString
      )

      json.validate[PaacTaxYear2016PreAlignment] mustEqual JsSuccess(
        PaacTaxYear2016PreAlignment.NoInputTaxYear(Period._2016PreAlignment)
      )

    }

    "must fail to deserialise PaacTaxYear2016PreAlignmentNoInputTaxYear with a period other than 2016-pre" in {
      val json = Json.obj(
        "period" -> Period._2016PostAlignment.toString
      )

      json.validate[PaacTaxYear2016PreAlignment] mustEqual JsError("tax year must be `2016-pre`")

    }

    "must serialise a PaacTaxYear2016PreAlignmentNoInputTaxYear with a period 2016-pre" in {
      val json = Json.obj(
        "period" -> Period._2016PreAlignment.toString
      )

      Json.toJson[PaacTaxYear2016PreAlignment](
        PaacTaxYear2016PreAlignment.NoInputTaxYear(Period._2016PreAlignment)
      ) mustEqual json

    }

  }
}
