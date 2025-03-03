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
import org.scalatest.matchers.must.Matchers.mustEqual
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

class PaacTaxYear2016PostAlignmentSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with PaacModelGenerators {

  "PaacTaxYear2016PostAlignmentNormalTaxYear" - {

    "must deserialise PaacTaxYear2016PostAlignmentNormalTaxYear with a period 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsSuccess(
          PaacTaxYear2016PostAlignment.NormalTaxYear(v.pensionInputAmount, v.period)
        )
      }

    }

    "must fail to deserialise PaacTaxYear2016PostAlignmentNormalTaxYear with a period other than 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> Period._2016PreAlignment.toString
        )

        json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsError("tax year must be `2016-post`")
      }
    }

    "must serialise a PaacTaxYear2016PostAlignmentNormalTaxYear with a period 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentNormalTaxYear) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        Json.toJson[PaacTaxYear2016PostAlignment](
          PaacTaxYear2016PostAlignment.NormalTaxYear(v.pensionInputAmount, v.period)
        ) `mustEqual` json
      }

    }

  }

  "PaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear" - {

    "must deserialise PaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear with a period 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "period"                                   -> v.period.toString
        )

        json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsSuccess(
          PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.period
          )
        )
      }

    }

    "must fail to deserialise PaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear with a period other than 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "period"                                   -> Period._2016PreAlignment.toString
        )

        json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsError("tax year must be `2016-post`")
      }
    }

    "must serialise a PaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear with a period 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentInitialFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "period"                                   -> v.period.toString
        )

        Json.toJson[PaacTaxYear2016PostAlignment](
          PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.period
          )
        ) `mustEqual` json
      }

    }

  }

  "PaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear" - {

    "must deserialise PaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear with a period 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "period"                         -> v.period.toString
        )

        json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsSuccess(
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.period
          )
        )
      }

    }

    "must fail to deserialise PaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear with a period other than 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "period"                         -> Period._2016PreAlignment.toString
        )

        json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsError("tax year must be `2016-post`")
      }
    }

    "must serialise a PaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear with a period 2016-post" in {
      forAll(genPaacTaxYear2016PostAlignmentPostFlexiblyAccessedTaxYear) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "period"                         -> v.period.toString
        )

        Json.toJson[PaacTaxYear2016PostAlignment](
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.period
          )
        ) `mustEqual` json
      }

    }

  }

  "PaacTaxYear2016PostAlignmentNoInputTaxYear" - {

    "must deserialise PaacTaxYear2016PostAlignmentNoInputTaxYear with a period 2016-post" in {
      val json = Json.obj(
        "period" -> Period._2016PostAlignment.toString
      )

      json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsSuccess(
        PaacTaxYear2016PostAlignment.NoInputTaxYear(Period._2016PostAlignment)
      )

    }

    "must fail to deserialise PaacTaxYear2016PostAlignmentNoInputTaxYear with a period other than 2016-post" in {
      val json = Json.obj(
        "period" -> Period._2016PreAlignment.toString
      )

      json.validate[PaacTaxYear2016PostAlignment] `mustEqual` JsError("tax year must be `2016-post`")

    }

    "must serialise a PaacTaxYear2016PostAlignmentNoInputTaxYear with a period 2016-post" in {
      val json = Json.obj(
        "period" -> Period._2016PostAlignment.toString
      )

      Json.toJson[PaacTaxYear2016PostAlignment](
        PaacTaxYear2016PostAlignment.NoInputTaxYear(Period._2016PostAlignment)
      ) `mustEqual` json

    }

  }

}
