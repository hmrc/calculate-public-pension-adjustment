package uk.gov.hmrc.calculatepublicpensionadjustment.controllers

import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import play.api.inject.bind
import org.scalatest.matchers.must.Matchers
import play.api.http.Status.{BAD_REQUEST, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{AUTHORIZATION, contentAsJson, contentAsString, defaultAwaitTimeout, route, status}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.TaxRateStatusRequest
import uk.gov.hmrc.calculatepublicpensionadjustment.services.{PaacService, UserAnswersService}

class TaxRateStatusRequestControllerSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset[Any](mockPaacService)
  }

  private val mockPaacService = mock[PaacService]

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[PaacService].toInstance(mockPaacService)
    )
    .build()

  "calculate" - {

    "must return a bool when with a valid request" in {

      when(mockPaacService.isBelowBasicOrIntermediateTaxRate(List(Period._2017), Period._2018, 1000))
        .thenReturn(true)

      val request = FakeRequest(routes.TaxRateStatusRequestController.calculate)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(TaxRateStatusRequest(Period._2018, 1000, List(Period._2017))))

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustBe Json.toJson(true)
    }

    "must return invalid request with an invalid request" in {

      val request = FakeRequest(routes.TaxRateStatusRequestController.calculate)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson("{\"invalid\":\"request\"}"))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual "Invalid TaxRateStatusRequest"
    }
  }
}
