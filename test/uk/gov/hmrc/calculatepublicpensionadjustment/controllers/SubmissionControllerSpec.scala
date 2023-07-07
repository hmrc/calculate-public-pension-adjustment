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

import cats.data.NonEmptyChain
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.CalculationResponse
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.{SubmissionRequest, SubmissionResponse}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.useranswers.lta.{LTACharge, LTAChargeHowPaid, LTAChargePaidByScheme, LTAChargeType, LTAChargeWhoPays, LTAProtection, LTAProtectionType, LifetimeAllowance}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.useranswers.{CalculationUserAnswers, Resubmission}
import uk.gov.hmrc.calculatepublicpensionadjustment.services.SubmissionService
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SubmissionControllerSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach {

  override def beforeEach(): Unit                          = {
    super.beforeEach()
    Mockito.reset[Any](mockSubmissionService, mockStubBehaviour)
  }
  private val mockSubmissionService                        = mock[SubmissionService]
  private val mockStubBehaviour                            = mock[StubBehaviour]
  private val backendAuthComponents: BackendAuthComponents =
    BackendAuthComponentsStub(mockStubBehaviour)(Helpers.stubControllerComponents(), global)

  private val permission = Predicate.Permission(
    resource = Resource(
      resourceType = ResourceType("calculate-public-pension-adjustment"),
      resourceLocation = ResourceLocation("submission")
    ),
    action = IAAction("WRITE")
  )

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[SubmissionService].toInstance(mockSubmissionService),
      bind[BackendAuthComponents].toInstance(backendAuthComponents)
    )
    .build()

  "submit" - {

    "must return ACCEPTED when a submission is successful" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.submit(any(), any())(any()))
        .thenReturn(Future.successful(Right("uniqueId")))

      val calculationResponse = Some(CalculationResponse(List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationUserAnswers, calculationResponse)))

      val result = route(app, request).value

      status(result) mustEqual ACCEPTED
      contentAsJson(result) mustEqual Json.obj("uniqueId" -> "uniqueId")

      verify(mockSubmissionService, times(1)).submit(eqTo(calculationUserAnswers), eqTo(calculationResponse))(any())
    }

    "must fail when the submission fails" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.submit(any(), any())(any()))
        .thenReturn(Future.failed(new RuntimeException()))

      val calculationResponse = Some(CalculationResponse(List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationUserAnswers, calculationResponse)))

      intercept[Exception](route(app, request).value.futureValue)
    }

    "must return BAD_REQUEST when the submission service returns errors" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.submit(any(), any())(any()))
        .thenReturn(Future.successful(Left(NonEmptyChain.one("some error"))))

      val calculationResponse = Some(CalculationResponse(List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationUserAnswers, calculationResponse)))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsJson(result).as[SubmissionResponse.Failure] mustEqual (SubmissionResponse.Failure(List("some error")))
    }

    "must return BAD_REQUEST when the user provides an invalid request" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson("{\"invalid\":\"request\"}"))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual "Invalid Submission"
    }
  }

  private def calculationUserAnswers = {
    val ltaCharge =
      LTACharge(
        1234,
        LTAChargeWhoPays.PensionScheme,
        Some(LTAChargePaidByScheme("scheme1", "pstr1", LTAChargeHowPaid.LumpSum))
      )

    val lifetimeAllowance = Some(
      LifetimeAllowance(
        LocalDate.now(),
        LTAChargeType.New,
        List(LTAProtection(LTAProtectionType.FixedProtection, "123")),
        List.empty,
        ltaCharge
      )
    )

    CalculationUserAnswers(Resubmission(false, None), None, lifetimeAllowance)
  }
}
