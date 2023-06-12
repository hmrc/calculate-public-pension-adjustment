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

class PaacTaxYear2011To2015Spec extends AnyFreeSpec with ScalaCheckPropertyChecks with PaacModelGenerators {

  private val genValidPaacTaxYear2011To2015Period   = Gen.choose(2011, 2015).map(Period.Year)
  private val genInvalidPaacTaxYear2011To2015Period = Gen.choose(2017, 2023).map(Period.Year)

  "PaacTaxYear2011To2015NormalTaxYear" - {

    "must deserialise a period fall within 2011 - 2015" in {
      forAll(genValidPaacTaxYear2011To2015Period.flatMap(genPaacTaxYear2011To2015NormalTaxYearForPeriod(_))) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        json.validate[PaacTaxYear2011To2015] mustEqual JsSuccess(
          PaacTaxYear2011To2015.NormalTaxYear(v.pensionInputAmount, v.period)
        )
      }

    }

    "must fail to deserialise a period fall outside 2011 - 2015" in {
      forAll(genInvalidPaacTaxYear2011To2015Period.flatMap(genPaacTaxYear2011To2015NormalTaxYearForPeriod(_))) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        json.validate[PaacTaxYear2011To2015] mustEqual JsError("taxYear must fall between `2011`-`2015`")
      }

    }

    "must serialise a period fall within 2011 - 2015" in {
      forAll(genValidPaacTaxYear2011To2015Period.flatMap(genPaacTaxYear2011To2015NormalTaxYearForPeriod(_))) { v =>
        val json = Json.obj(
          "pensionInputAmount" -> v.pensionInputAmount,
          "period"             -> v.period.toString
        )

        Json.toJson[PaacTaxYear2011To2015](
          PaacTaxYear2011To2015.NormalTaxYear(v.pensionInputAmount, v.period)
        ) mustEqual json
      }

    }

  }

  "PaacTaxYear2011To2015NoInputTaxYear" - {

    "must deserialise a period fall within 2011 - 2015" in {
      forAll(genValidPaacTaxYear2011To2015Period.flatMap(PaacTaxYear2011To2015.NoInputTaxYear(_))) { v =>
        val json = Json.obj(
          "period" -> v.period.toString
        )

        json.validate[PaacTaxYear2011To2015] mustEqual JsSuccess(
          PaacTaxYear2011To2015.NoInputTaxYear(v.period)
        )
      }

    }

    "must fail to deserialise a period fall outside 2011 - 2015" in {
      forAll(genInvalidPaacTaxYear2011To2015Period.flatMap(PaacTaxYear2011To2015.NoInputTaxYear(_))) { v =>
        val json = Json.obj(
          "period" -> v.period.toString
        )

        json.validate[PaacTaxYear2011To2015] mustEqual JsError("taxYear must fall between `2011`-`2015`")
      }

    }

    "must serialise a period fall within 2011 - 2015" in {
      forAll(genValidPaacTaxYear2011To2015Period.flatMap(PaacTaxYear2011To2015.NoInputTaxYear(_))) { v =>
        val json = Json.obj(
          "period" -> v.period.toString
        )

        Json.toJson[PaacTaxYear2011To2015](
          PaacTaxYear2011To2015.NoInputTaxYear(v.period)
        ) mustEqual json
      }

    }

  }

}
