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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, post, urlEqualTo}
import org.apache.pekko.actor.ActorSystem
import org.mockito.MockitoSugar
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.Application
import play.api.http.Status.OK
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Income.{AboveThreshold, BelowThreshold}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac._
import uk.gov.hmrc.calculatepublicpensionadjustment.utils.WireMockHelper
import uk.gov.hmrc.http.HeaderCarrier

class PaacConnectorSpec
    extends AnyFreeSpec
    with WireMockHelper
    with MockitoSugar
    with ScalaFutures
    with Matchers
    with IntegrationPatience
    with BeforeAndAfterEach
    with BeforeAndAfterAll {

  implicit private lazy val as: ActorSystem = ActorSystem()

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    as.terminate().futureValue
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWireMock()
  }

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  private lazy val app: Application =
    new GuiceApplicationBuilder()
      .configure(
        "microservice.services.pension-annual-allowance-calculator.port" -> wireMockServer.port
      )
      .build()

  private lazy val connector: PaacConnector = app.injector.instanceOf[PaacConnector]

  "Paac" - {

    "when response from PAAC is OK return successful response" in {

      val validPaacResponseWithAllYears = PaacResponse(
        List(
          PaacResponseRow(
            PaacTaxYear2017ToCurrent
              .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
            16000,
            40000
          ),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
            18000,
            60000
          ),
          PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0, 0),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
            16000,
            0
          ),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
            18000,
            30000
          ),
          PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2018), 0, 0),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
            9000,
            40000
          ),
          PaacResponseRow(
            PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(36000, 20000, Period._2016PostAlignment),
            10000,
            0
          ),
          PaacResponseRow(
            PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
            0,
            20000
          ),
          PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0, 0),
          PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014), 0, 20000),
          PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0, 30000),
          PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2012), 0, 0),
          PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2011), 0, 0)
        )
      )

      val validPaacRequestWithAllYears = PaacRequest(
        List(
          PaacTaxYear2011To2015.NormalTaxYear(9000, Period._2011),
          PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013),
          PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014),
          PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015),
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(36000, 20000, Period._2016PostAlignment),
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018),
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
          PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2021),
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023)
        ),
        Period._2023
      )

      wireMockServer.stubFor(
        post(urlEqualTo("/pension-annual-allowance-calculator/calculate"))
          .willReturn(
            aResponse()
              .withStatus(OK)
              .withBody(Json.stringify(Json.toJson(validPaacResponseWithAllYears)))
          )
      )

      val result = connector.sendRequest(validPaacRequestWithAllYears)(hc).futureValue

      result mustBe validPaacResponseWithAllYears
    }
  }
}
