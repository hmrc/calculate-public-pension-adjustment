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

import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import requests.{CalculationResponses, CppaRequests}
import uk.gov.hmrc.calculatepublicpensionadjustment.services.PaacService

import scala.concurrent.Future

class ShowCalculationControllerSpec
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with OptionValues
    with ScalaCheckPropertyChecks
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterEach
    with CppaRequests
    with CalculationResponses {

  private val mockPaacService = mock[PaacService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockPaacService)
  }

  override lazy val app = GuiceApplicationBuilder()
    .overrides(
      bind[PaacService].toInstance(mockPaacService)
    )
    .build()

  "ShowCalculationController" - {

    "must return Status 200 - Ok" - {
      "when the valid request contains all tax years 2013 - 2023 with NormalTaxYear" in {

        when(mockPaacService.calculate(any())(any()))
          .thenReturn(Future.successful(allTaxYearsWithNormalTaxYearResponse))

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

        when(mockPaacService.calculate(any())(any()))
          .thenReturn(Future.successful(allTaxYearsWithInitialFlexiblyAccessedTaxYearResponse))

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

        when(mockPaacService.calculate(any())(any()))
          .thenReturn(Future.successful(allTaxYearsWithPostFlexiblyAccessedTaxYearResponse))

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

        when(mockPaacService.calculate(any())(any()))
          .thenReturn(Future.successful(missingTaxYearsValidResponse))

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
