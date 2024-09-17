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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{AnnualAllowanceSetup, CalculationInputs, CalculationResponse, LifetimeAllowanceSetup, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, Resubmission, Setup, TotalAmounts}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.{Submission, SubmissionRequest, SubmissionResponse}
import uk.gov.hmrc.calculatepublicpensionadjustment.models._
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.calculatepublicpensionadjustment.services.{SubmissionService, UserAnswersService}
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.internalauth.client.test.{BackendAuthComponentsStub, StubBehaviour}

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, LocalDate, ZoneId}
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
    Mockito.reset[Any](mockSubmissionService, mockStubBehaviour, mockUserAnswersService, mockSubmissionRepository)
  }
  private val mockSubmissionService                        = mock[SubmissionService]
  private val mockUserAnswersService                       = mock[UserAnswersService]
  private val mockStubBehaviour                            = mock[StubBehaviour]
  private val mockSubmissionRepository                     = mock[SubmissionRepository]
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
      bind[UserAnswersService].toInstance(mockUserAnswersService),
      bind[SubmissionRepository].toInstance(mockSubmissionRepository),
      bind[BackendAuthComponents].toInstance(backendAuthComponents)
    )
    .build()

  private val instant   = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock = Clock.fixed(instant, ZoneId.systemDefault)

  "submit" - {

    "must return ACCEPTED when a submission is successful" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.submit(any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Right("uniqueId")))

      val calculationResponse =
        Some(CalculationResponse(Resubmission(false, None), TotalAmounts(1, 2, 3), List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationInputs, calculationResponse, "uniqueId", "uniqueId")))

      val result = route(app, request).value

      status(result) mustEqual ACCEPTED
      contentAsJson(result) mustEqual Json.obj("uniqueId" -> "uniqueId")

      verify(mockSubmissionService, times(1))
        .submit(eqTo(calculationInputs), eqTo(calculationResponse), eqTo("uniqueId"), any())(any())
    }

    "must return Submission when a valid uniqueId is specified" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.retrieve("1234"))
        .thenReturn(
          Future.successful(
            Some(
              Submission(
                "ninoId",
                "uniqueId",
                CalculationInputs(
                  Resubmission(false, None),
                  Setup(
                    Some(
                      AnnualAllowanceSetup(
                        Some(true),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(MaybePIAIncrease.No),
                        Some(MaybePIAUnchangedOrDecreased.No),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(false)
                      )
                    ),
                    Some(
                      LifetimeAllowanceSetup(
                        Some(true),
                        Some(false),
                        Some(true),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(true)
                      )
                    )
                  ),
                  None,
                  Some(
                    LifeTimeAllowance(
                      LocalDate.parse("2018-11-28"),
                      LtaProtectionOrEnhancements.Protection,
                      Some(ProtectionType.FixedProtection2014),
                      Some("R41AB678TR23355"),
                      ProtectionEnhancedChanged.Protection,
                      Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
                      Some("2134567801"),
                      true,
                      Some(ExcessLifetimeAllowancePaid.Annualpayment),
                      Some(WhoPaidLTACharge.PensionScheme),
                      Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
                      Some(WhoPayingExtraLtaCharge.You),
                      None,
                      NewLifeTimeAllowanceAdditions(
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None
                      )
                    )
                  )
                ),
                None
              )
            )
          )
        )

      when(mockUserAnswersService.retrieveUserAnswers("1234"))
        .thenReturn(
          Future.successful(
            Some(
              UserAnswers("uniqueId", Json.obj("foo" -> "bar"), "dbUniqueId", Instant.now(stubClock), true, true)
            )
          )
        )

      val retrieveSubmissionInfo = RetrieveSubmissionInfo("nino", UniqueId("1234"))

      when(mockUserAnswersService.updateSubmissionStartedToTrue(retrieveSubmissionInfo))
        .thenReturn(
          Future.successful(true)
        )

      val calculationResponse =
        Some(CalculationResponse(Resubmission(false, None), TotalAmounts(1, 2, 3), List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.retrieveSubmission)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(retrieveSubmissionInfo))

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.parse(
        "{\"calculationInputs\":{\"resubmission\":{\"isResubmission\":false},\"setup\":{\"annualAllowanceSetup\":{\"savingsStatement\":true," +
          "\"pensionProtectedMember\":false,\"hadAACharge\":false,\"contributionRefunds\":false,\"netIncomeAbove100K\":false,\"netIncomeAbove190K\":false," +
          "\"maybePIAIncrease\":\"no\",\"maybePIAUnchangedOrDecreased\":\"no\",\"pIAAboveAnnualAllowanceIn2023\":false,\"netIncomeAbove190KIn2023\":false," +
          "\"flexibleAccessDcScheme\":false,\"contribution4000ToDirectContributionScheme\":false},\"lifetimeAllowanceSetup\":{\"benefitCrystallisationEventFlag\":true," +
          "\"previousLTACharge\":false,\"changeInLifetimeAllowancePercentageInformedFlag\":true,\"increaseInLTACharge\":false,\"newLTACharge\":false," +
          "\"multipleBenefitCrystallisationEventFlag\":false,\"otherSchemeNotification\":true}}," +
          "\"lifeTimeAllowance\":{\"benefitCrystallisationEventDate\":\"2018-11-28\"," +
          "\"lifetimeAllowanceProtectionOrEnhancements\":\"protection\"," +
          "\"protectionType\":\"fixedProtection2014\",\"protectionReference\":\"R41AB678TR23355\",\"protectionTypeEnhancementChanged\":\"protection\"," +
          "\"newProtectionTypeOrEnhancement\":\"individualProtection2016\",\"newProtectionTypeOrEnhancementReference\":\"2134567801\",\"previousLifetimeAllowanceChargeFlag\":true," +
          "\"previousLifetimeAllowanceChargePaymentMethod\":\"annualPayment\",\"previousLifetimeAllowanceChargePaidBy\":\"pensionScheme\"," +
          "\"previousLifetimeAllowanceChargeSchemeNameAndTaxRef\":{\"name\":\"Scheme 1\",\"taxRef\":\"00348916RT\"}," +
          "\"newLifetimeAllowanceChargeWillBePaidBy\":\"you\",\"newLifeTimeAllowanceAdditions\":{}}}} "
      )

      verify(mockSubmissionService, times(1)).retrieve(eqTo("1234"))
    }

    "must return BadRequest when an unknown uniqueId is specified" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.retrieve("1234"))
        .thenReturn(Future.successful(None))

      val calculationResponse =
        Some(CalculationResponse(Resubmission(false, None), TotalAmounts(1, 2, 3), List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.retrieveSubmission)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(RetrieveSubmissionInfo("uniqueId", UniqueId("1234"))))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual ""

      verify(mockSubmissionService, times(1)).retrieve(eqTo("1234"))
    }

    "must return a bad request when a submission but no user answers exists" in {

      val retrieveSubmissionInfo = RetrieveSubmissionInfo("uniqueId", UniqueId("1234"))

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.retrieve("uniqueId"))
        .thenReturn(
          Future.successful(
            Some(
              Submission(
                "ninoId",
                "uniqueId",
                CalculationInputs(
                  Resubmission(false, None),
                  Setup(
                    Some(
                      AnnualAllowanceSetup(
                        Some(true),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(MaybePIAIncrease.No),
                        Some(MaybePIAUnchangedOrDecreased.No),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(false)
                      )
                    ),
                    Some(
                      LifetimeAllowanceSetup(
                        Some(true),
                        Some(false),
                        Some(true),
                        Some(false),
                        Some(false),
                        Some(false),
                        Some(true)
                      )
                    )
                  ),
                  None,
                  Some(
                    LifeTimeAllowance(
                      LocalDate.parse("2018-11-28"),
                      LtaProtectionOrEnhancements.Protection,
                      Some(ProtectionType.FixedProtection2014),
                      Some("R41AB678TR23355"),
                      ProtectionEnhancedChanged.Protection,
                      Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
                      Some("2134567801"),
                      true,
                      Some(ExcessLifetimeAllowancePaid.Annualpayment),
                      Some(WhoPaidLTACharge.PensionScheme),
                      Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
                      Some(WhoPayingExtraLtaCharge.You),
                      None,
                      NewLifeTimeAllowanceAdditions(
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None,
                        None
                      )
                    )
                  )
                ),
                None
              )
            )
          )
        )

      when(mockUserAnswersService.retrieveUserAnswers("uniqueId"))
        .thenReturn(
          Future.successful(
            None
          )
        )

      when(mockUserAnswersService.updateSubmissionStartedToTrue(retrieveSubmissionInfo))
        .thenReturn(
          Future.successful(false)
        )

      val calculationResponse =
        Some(CalculationResponse(Resubmission(false, None), TotalAmounts(1, 2, 3), List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.retrieveSubmission)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationInputs, calculationResponse, "sessionId", "uniqueId")))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

    }

    "must fail when the submission fails" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.submit(any(), any(), any(), any())(any()))
        .thenReturn(Future.failed(new RuntimeException()))

      val calculationResponse =
        Some(CalculationResponse(Resubmission(false, None), TotalAmounts(1, 2, 3), List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationInputs, calculationResponse, "sessionId", "uniqueId")))

      intercept[Exception](route(app, request).value.futureValue)
    }

    "must return BAD_REQUEST when the submission service returns errors" in {

      when(mockStubBehaviour.stubAuth(Some(permission), Retrieval.username))
        .thenReturn(Future.successful(Retrieval.Username("test-service")))

      when(mockSubmissionService.submit(any(), any(), any(), any())(any()))
        .thenReturn(Future.successful(Left(NonEmptyChain.one("some error"))))

      val calculationResponse =
        Some(CalculationResponse(Resubmission(false, None), TotalAmounts(1, 2, 3), List.empty, List.empty))

      val request = FakeRequest(routes.SubmissionController.submit)
        .withHeaders(AUTHORIZATION -> "my-token")
        .withBody(Json.toJson(SubmissionRequest(calculationInputs, calculationResponse, "sessionId", "uniqueId")))

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

  ".clear" - {

    "must return No Content when data is cleared" in {

      when(mockSubmissionRepository.clear(eqTo("sessionId"))) thenReturn Future.successful(Done)

      val request =
        FakeRequest(DELETE, routes.SubmissionController.clear.url)
          .withHeaders(HeaderNames.xSessionId -> "sessionId")

      val result = route(app, request).value

      status(result) mustEqual NO_CONTENT
      verify(mockSubmissionRepository, times(1)).clear(eqTo("sessionId"))
    }

    "must return Bad Request when the request does not have a session id" in {

      val request =
        FakeRequest(DELETE, routes.SubmissionController.clear.url)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }
  }

  private def calculationInputs =
    CalculationInputs(
      Resubmission(false, None),
      Setup(
        Some(
          AnnualAllowanceSetup(
            Some(true),
            Some(false),
            Some(false),
            Some(false),
            Some(false),
            Some(false),
            Some(MaybePIAIncrease.No),
            Some(MaybePIAUnchangedOrDecreased.No),
            Some(false),
            Some(false),
            Some(false),
            Some(false)
          )
        ),
        Some(
          LifetimeAllowanceSetup(Some(true), Some(false), Some(true), Some(false), Some(false), Some(false), Some(true))
        )
      ),
      None,
      Some(
        LifeTimeAllowance(
          LocalDate.parse("2018-11-28"),
          LtaProtectionOrEnhancements.Protection,
          Some(ProtectionType.FixedProtection2014),
          Some("R41AB678TR23355"),
          ProtectionEnhancedChanged.Protection,
          Some(WhatNewProtectionTypeEnhancement.IndividualProtection2016),
          Some("2134567801"),
          true,
          Some(ExcessLifetimeAllowancePaid.Annualpayment),
          Some(WhoPaidLTACharge.PensionScheme),
          Some(SchemeNameAndTaxRef("Scheme 1", "00348916RT")),
          Some(WhoPayingExtraLtaCharge.You),
          None,
          NewLifeTimeAllowanceAdditions(
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None
          )
        )
      )
    )
}
