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

package uk.gov.hmrc.calculatepublicpensionadjustment.services

import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.libs.json.Json
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.CalculationInputs
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Resubmission
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, RetrieveSubmissionInfo, UniqueId, UserAnswers}

import java.time.Instant
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.{SubmissionRepository, UserAnswersRepository}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json.JsObject
import requests.CalculationResponses

class UserAnswersServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach
    with CalculationResponses {

  private val mockSubmissionService     = mock[SubmissionService]
  private val mockUserAnswersRepository = mock[UserAnswersRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSubmissionService)
    reset(mockUserAnswersRepository)
  }

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new UserAnswersService(mockSubmissionService, mockUserAnswersRepository)

  "SubmissionService" - {

    "retrieveUserAnswers" - {

      "must return a submission when it exists in the userAnswersRepository" in {

        val userAnswers = UserAnswers("uniqueId", Json.obj(), "uniqueId", Instant.now)

        when(mockUserAnswersRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

        service.retrieveUserAnswers("uniqueId").futureValue mustBe Some(userAnswers)
        verify(mockUserAnswersRepository, times(1)).get(eqTo("uniqueId"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockUserAnswersRepository.get("unknownId")).thenReturn(Future.successful(None))

        service.retrieveUserAnswers("unknownId").futureValue mustBe None
        verify(mockUserAnswersRepository, times(1)).get(eqTo("unknownId"))
      }
    }

    "retrieveUserAnswersByUniqueId" - {

      "must return a submission when it exists in the userAnswersRepository" in {

        val userAnswers = UserAnswers("uniqueId", Json.obj(), "uniqueId", Instant.now)

        when(mockUserAnswersRepository.getByUniqueId(any())).thenReturn(Future.successful(Some(userAnswers)))

        service.retrieveUserAnswersByUniqueId("uniqueId").futureValue mustBe Some(userAnswers)
        verify(mockUserAnswersRepository, times(1)).getByUniqueId(eqTo("uniqueId"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockUserAnswersRepository.getByUniqueId("unknownId")).thenReturn(Future.successful(None))

        service.retrieveUserAnswersByUniqueId("unknownId").futureValue mustBe None
        verify(mockUserAnswersRepository, times(1)).getByUniqueId(eqTo("unknownId"))
      }
    }

    "updateSubmissionStartedToTrue" - {

      "must return a submission when it exists in the userAnswersRepository" in {

        val retrieveSubmissionInfo = RetrieveSubmissionInfo("uniqueId", UniqueId("1234"))

        val submission =
          Submission("id", "uniqueId", "sessionId", CalculationInputs(Resubmission(false, None), None, None), None)

        val userAnswers = UserAnswers("uniqueId", Json.obj(), "uniqueId", Instant.now)

        when(mockSubmissionService.retrieve(any())).thenReturn(Future.successful(Some(submission)))
        when(mockSubmissionService.updateSubmission(any())).thenReturn(Future.successful(Done))
        when(mockSubmissionService.clearBySessionId(any())).thenReturn(Future.successful(Done))
        when(mockSubmissionService.clearByUniqueIdAndNotId(any(), any())).thenReturn(Future.successful(Done))
        when(mockUserAnswersRepository.clear(any())).thenReturn(Future.successful(Done))
        when(mockUserAnswersRepository.get("sessionId")).thenReturn(Future.successful(Some(userAnswers)))
        when(mockUserAnswersRepository.set(any())) thenReturn (Future.successful(Done))
        when(mockUserAnswersRepository.clearByUniqueIdAndNotId(any(), any())) thenReturn (Future.successful(Done))

        service.updateSubmissionStartedToTrue(retrieveSubmissionInfo).futureValue mustBe true
      }

      "must return a false if no userAnswer exist for the session id" in {

        val retrieveSubmissionInfo = RetrieveSubmissionInfo("uniqueId", UniqueId("1234"))

        val submission =
          Submission("id", "uniqueId", "sessionId", CalculationInputs(Resubmission(false, None), None, None), None)

        when(mockSubmissionService.retrieve(any())).thenReturn(Future.successful(Some(submission)))
        when(mockUserAnswersRepository.get("sessionId")).thenReturn(Future.successful(None))
        when(mockUserAnswersRepository.set(any())) thenReturn Future.successful(Done)

        service.updateSubmissionStartedToTrue(retrieveSubmissionInfo).futureValue mustBe false
      }

      "must return a false if no submission exist for the unique id" in {

        val retrieveSubmissionInfo = RetrieveSubmissionInfo("uniqueId", UniqueId("1234"))

        when(mockSubmissionService.retrieve(any())).thenReturn(Future.successful(None))

        service.updateSubmissionStartedToTrue(retrieveSubmissionInfo).futureValue mustBe false
      }

    }

    "updateSubmissionStartedToFalse" - {

      "must return a submission when it exists in the userAnswersRepository" in {
        val userAnswers = UserAnswers("uniqueId", Json.obj(), "uniqueId", Instant.now)

        when(mockUserAnswersRepository.get("uniqueId")).thenReturn(Future.successful(Some(userAnswers)))
        when(mockUserAnswersRepository.set(any())) thenReturn (Future.successful(Done))

        service.updateSubmissionStartedToFalse("uniqueId").futureValue mustBe true
      }

      "must return a false if no userAnswer exist for the session id" in {
        when(mockUserAnswersRepository.get("uniqueId")).thenReturn(Future.successful(None))

        service.updateSubmissionStartedToFalse("uniqueId").futureValue mustBe false
      }

    }
  }

  "UserAnswersService" - {

    "checkSubmissionStarted" - {

      "must return submission started when it exists in repository" - {
        val userAnswers = new UserAnswers("ID", new JsObject(Map.empty), "uniqueId", Instant.now(), true, true)

        when(mockUserAnswersRepository.get(any())).thenReturn(Future.successful(Some(userAnswers)))

        val result = service.checkSubmissionStartedWithId("ID")

        result.futureValue.get.uniqueId mustBe "uniqueId"
        result.futureValue.get.submissionStarted mustBe true
      }

      "must return submission started empty when it does not exists in repository" - {
        when(mockUserAnswersRepository.get(any())).thenReturn(Future.successful(None))

        val result = service.checkSubmissionStartedWithId("ID")

        result.futureValue mustBe None
      }
    }
  }
}
