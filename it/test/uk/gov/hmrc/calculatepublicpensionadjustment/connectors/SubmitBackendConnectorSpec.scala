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

package uk.gov.hmrc.calculatepublicpensionadjustment.connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.scalatestplus.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.Application
import play.api.http.Status.{BAD_REQUEST, NO_CONTENT, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.utils.WireMockHelper
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}

import java.time.Instant

class SubmitBackendConnectorSpec
    extends AnyFreeSpec
    with MockitoSugar
    with ScalaFutures
    with Matchers
    with WireMockHelper
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.submit-public-pension-adjustment.port" -> wireMockServer.port
    )
    .build()

  private lazy val connector: SubmitBackendConnector = app.injector.instanceOf[SubmitBackendConnector]

  "SubmitBackendConnector" - {
    "retrieveCalcUserAnswersFromSubmitBE" - {
      "should return CalcUserAnswers successfully when calc backend responds with OK" in {
        val uniqueId         = "uniqueId"
        val expectedResponse = UserAnswers(
          "id",
          JsObject(Seq()),
          uniqueId,
          Instant.ofEpochSecond(1),
          authenticated = true,
          submissionStarted = true
        )
        val url              = s"/submit-public-pension-adjustment/calc-user-answers/$uniqueId"

        wireMockServer.stubFor(
          get(url)
            .willReturn(aResponse().withStatus(OK).withBody(Json.toJson(expectedResponse).toString()))
        )

        connector.retrieveCalcUserAnswersFromSubmitBE(uniqueId).futureValue shouldBe Some(expectedResponse)
      }

      "should return None successfully when calc backend responds with NO_CONTENT" in {
        val id = "id"

        val url = s"/submit-public-pension-adjustment/calc-user-answers/$id"

        wireMockServer.stubFor(
          get(url)
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        connector.retrieveCalcUserAnswersFromSubmitBE(id).futureValue shouldBe None
      }

      "should throw UpstreamErrorResponse when calc backend responds with an error" in {
        val uniqueId = "uniqueId"
        val url      = s"/submit-public-pension-adjustment/calc-user-answers/$uniqueId"

        wireMockServer.stubFor(
          get(url)
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        val response = connector.retrieveCalcUserAnswersFromSubmitBE(uniqueId)

        ScalaFutures.whenReady(response.failed) { response =>
          response shouldBe a[UpstreamErrorResponse]
        }
      }
    }

    "retrieveCalcUserAnswersFromSubmitBEWithId" - {
      "should return CalcUserAnswers successfully when calc backend responds with OK" in {
        val id               = "id"
        val expectedResponse = UserAnswers(
          id,
          JsObject(Seq()),
          "uniqueId",
          Instant.ofEpochSecond(1),
          authenticated = true,
          submissionStarted = true
        )
        val url              = s"/submit-public-pension-adjustment/calc-user-answers-with-id/$id"

        wireMockServer.stubFor(
          get(url)
            .willReturn(aResponse().withStatus(OK).withBody(Json.toJson(expectedResponse).toString()))
        )

        connector.retrieveCalcUserAnswersFromSubmitBEWithId(id).futureValue shouldBe Some(expectedResponse)
      }

      "should return None successfully when calc backend responds with NO_CONTENT" in {
        val id = "id"

        val url = s"/submit-public-pension-adjustment/calc-user-answers-with-id/$id"

        wireMockServer.stubFor(
          get(url)
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        connector.retrieveCalcUserAnswersFromSubmitBEWithId(id).futureValue shouldBe None
      }

      "should throw UpstreamErrorResponse when calc backend responds with an error" in {
        val id  = "id"
        val url = s"/submit-public-pension-adjustment/calc-user-answers-with-id/$id"

        wireMockServer.stubFor(
          get(url)
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        val response = connector.retrieveCalcUserAnswersFromSubmitBEWithId(id)

        ScalaFutures.whenReady(response.failed) { response =>
          response shouldBe a[UpstreamErrorResponse]
        }
      }
    }

    "clearCalcUserAnswersSubmitBE" - {
      "should return Done successfully when calc backend responds with NO_CONTENT" in {
        val url = s"/submit-public-pension-adjustment/calc-user-answers"

        wireMockServer.stubFor(
          delete(url)
            .willReturn(aResponse().withStatus(NO_CONTENT))
        )

        connector.clearCalcUserAnswersSubmitBE().futureValue shouldBe Done
      }

      "should throw UpstreamErrorResponse when calc backend responds with a bad request" in {
        val url = s"/submit-public-pension-adjustment/calc-user-answers"

        wireMockServer.stubFor(
          delete(url)
            .willReturn(aResponse().withStatus(BAD_REQUEST))
        )

        val response = connector.clearCalcUserAnswersSubmitBE()

        ScalaFutures.whenReady(response.failed) { response =>
          response shouldBe a[UpstreamErrorResponse]
        }
      }
    }
  }
}
