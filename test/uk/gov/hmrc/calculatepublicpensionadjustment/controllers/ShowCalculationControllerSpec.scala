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

package uk.gov.hmrc.calculatepublicpensionadjustment.controllers

import requests.CppaRequests
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ShowCalculationControllerSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with OptionValues
    with ScalaCheckPropertyChecks
    with CppaRequests {

  "ShowCalculationController" - {

    "must return Status 200 - Ok" - {
      "when the valid request contains all tax years 2013 - 2023 with NormalTaxYear" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(allTaxYearsWithNormalTaxYearValidRequest)

        val result = route(app, request).value
        status(result) mustEqual OK
      }

      "when the valid request contains all tax years 2013 - 2023 with InitialFlexiblyAccessedTaxYear" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(allTaxYearsWithInitialFlexiblyAccessedTaxYearValidRequest)

        val result = route(app, request).value
        status(result) mustEqual OK
      }

      "when the valid request contains all tax years 2013 - 2023 with PostFlexiblyAccessedTax" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(allTaxYearsWithPostFlexiblyAccessedTaxYearValidRequest)

        val result = route(app, request).value
        status(result) mustEqual OK
      }

      "when the valid request missing few tax years" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(missingTaxYearsValidRequest)

        val result = route(app, request).value
        status(result) mustEqual OK
      }

    }

    "must return Status 400 - Bad Request" - {
      "when the request is empty" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(emptyRequest)

        val result = route(app, request).value
        status(result) mustEqual BAD_REQUEST
      }

      "when the request contains tax year out of valid 2013 - 2023 tax years" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(outOfTaxYearsRequest)

        val result = route(app, request).value
        status(result) mustEqual BAD_REQUEST
      }

      "when the request contains invalid data type" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(invalidDataTypeRequest)

        val result = route(app, request).value
        status(result) mustEqual BAD_REQUEST
      }

      "when the request is missing mandatory data" in {

        val request =
          FakeRequest(POST, "/calculate-public-pension-adjustment/show-calculation")
            .withHeaders(
              CONTENT_TYPE -> "application/json"
            )
            .withJsonBody(missingDataRequest)

        val result = route(app, request).value
        status(result) mustEqual BAD_REQUEST
      }
    }

  }

}
