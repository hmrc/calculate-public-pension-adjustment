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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac

import generators.PaacModelGenerators
import org.scalacheck.Gen
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsSuccess, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

class PaacTaxYear2017ToCurrentSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with PaacModelGenerators {

  private val genValidPaacTaxYear2017ToCurrentPeriod   = Gen.choose(2017, 2023).map(Period.Year)
  private val genInvalidPaacTaxYear2017ToCurrentPeriod = Gen.choose(2011, 2015).map(Period.Year)

  "PaacTaxYear2017ToCurrentNormalTaxYear" - {

    "must deserialise a PaacTaxYear2017ToCurrentNormalTaxYear fall within 2017 - 2023" in {
      forAll(genValidPaacTaxYear2017ToCurrentPeriod.flatMap(genPaacTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "period"             -> v.period
          )

          json.validate[PaacTaxYear2017ToCurrent] mustEqual JsSuccess(
            PaacTaxYear2017ToCurrent.NormalTaxYear(
              v.pensionInputAmount,
              v.income,
              v.period
            )
          )
      }

    }

    "must fail to deserialise a PaacTaxYear2017ToCurrentNormalTaxYear fall outside 2017 - 2023" in {
      forAll(genInvalidPaacTaxYear2017ToCurrentPeriod.flatMap(genPaacTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "period"             -> v.period
          )

          json.validate[PaacTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")
      }

    }

    "must serialise a PaacTaxYear2017ToCurrentNormalTaxYear fall within 2017 - 2023" in {
      forAll(genValidPaacTaxYear2017ToCurrentPeriod.flatMap(genPaacTaxYear2017ToCurrentNormalTaxYearForPeriod(_))) {
        v =>
          val json = Json.obj(
            "pensionInputAmount" -> v.pensionInputAmount,
            "income"             -> v.income,
            "period"             -> v.period
          )

          Json.toJson[PaacTaxYear2017ToCurrent](
            PaacTaxYear2017ToCurrent.NormalTaxYear(
              v.pensionInputAmount,
              v.income,
              v.period
            )
          ) mustEqual json
      }
    }

  }

  "PaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYear" - {

    "must deserialise a PaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYear fall within 2017 - 2023" in {
      forAll(
        genValidPaacTaxYear2017ToCurrentPeriod.flatMap(
          genPaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "income"                                   -> v.income,
          "period"                                   -> v.period
        )

        json.validate[PaacTaxYear2017ToCurrent] mustEqual JsSuccess(
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.income,
            v.period
          )
        )

      }

    }

    "must fail to deserialise a PaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYear fall outside 2017 - 2023" in {
      forAll(
        genInvalidPaacTaxYear2017ToCurrentPeriod.flatMap(
          genPaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "income"                                   -> v.income,
          "period"                                   -> v.period
        )

        json.validate[PaacTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")

      }

    }

    "must serialise a PaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYear fall within 2017 - 2023" in {
      forAll(
        genValidPaacTaxYear2017ToCurrentPeriod.flatMap(
          genPaacTaxYear2017ToCurrentInitialFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"                -> v.definedBenefitInputAmount,
          "preAccessDefinedContributionInputAmount"  -> v.preAccessDefinedContributionInputAmount,
          "postAccessDefinedContributionInputAmount" -> v.postAccessDefinedContributionInputAmount,
          "income"                                   -> v.income,
          "period"                                   -> v.period
        )

        Json.toJson[PaacTaxYear2017ToCurrent](
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.preAccessDefinedContributionInputAmount,
            v.postAccessDefinedContributionInputAmount,
            v.income,
            v.period
          )
        ) mustEqual json

      }

    }

  }

  "PaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYear" - {

    "must deserialise a PaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYear fall within 2017 - 2023" in {
      forAll(
        genValidPaacTaxYear2017ToCurrentPeriod.flatMap(
          genPaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "income"                         -> v.income,
          "period"                         -> v.period
        )

        json.validate[PaacTaxYear2017ToCurrent] mustEqual JsSuccess(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.income,
            v.period
          )
        )

      }

    }

    "must fail to deserialise a PaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYear fall outside 2017 - 2023" in {
      forAll(
        genInvalidPaacTaxYear2017ToCurrentPeriod.flatMap(
          genPaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "income"                         -> v.income,
          "period"                         -> v.period
        )

        json.validate[PaacTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")

      }

    }

    "must serialise a PaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYear fall within 2017 - 2023" in {
      forAll(
        genValidPaacTaxYear2017ToCurrentPeriod.flatMap(
          genPaacTaxYear2017ToCurrentPostFlexiblyAccessedTaxYearForPeriod(_)
        )
      ) { v =>
        val json = Json.obj(
          "definedBenefitInputAmount"      -> v.definedBenefitInputAmount,
          "definedContributionInputAmount" -> v.definedContributionInputAmount,
          "income"                         -> v.income,
          "period"                         -> v.period
        )

        Json.toJson[PaacTaxYear2017ToCurrent](
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            v.definedBenefitInputAmount,
            v.definedContributionInputAmount,
            v.income,
            v.period
          )
        ) mustEqual json

      }

    }

  }

  "PaacTaxYear2017ToCurrentNoInputTaxYear" - {

    "must deserialise a PaacTaxYear2017ToCurrentNoInputTaxYear fall within 2017 - 2023" in {
      forAll(genValidPaacTaxYear2017ToCurrentPeriod.flatMap(PaacTaxYear2017ToCurrent.NoInputTaxYear(_))) { v =>
        val json = Json.obj(
          "period" -> v.period.toString
        )

        json.validate[PaacTaxYear2017ToCurrent] mustEqual JsSuccess(
          PaacTaxYear2017ToCurrent.NoInputTaxYear(v.period)
        )

      }

    }

    "must fail to deserialise a PaacTaxYear2017ToCurrentNoInputTaxYear fall outside 2017 - 2023" in {
      forAll(genInvalidPaacTaxYear2017ToCurrentPeriod.flatMap(PaacTaxYear2017ToCurrent.NoInputTaxYear(_))) { v =>
        val json = Json.obj(
          "period" -> v.period.toString
        )

        json.validate[PaacTaxYear2017ToCurrent] mustEqual JsError("tax year must be `2017` or later")

      }

    }

    "must serialise a PaacTaxYear2017ToCurrentNoInputTaxYear fall within 2017 - 2023" in {
      forAll(genValidPaacTaxYear2017ToCurrentPeriod.flatMap(PaacTaxYear2017ToCurrent.NoInputTaxYear(_))) { v =>
        val json = Json.obj(
          "period" -> v.period.toString
        )

        Json.toJson[PaacTaxYear2017ToCurrent](
          PaacTaxYear2017ToCurrent.NoInputTaxYear(v.period)
        ) mustEqual json

      }

    }

  }

}
