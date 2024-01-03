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

import cats.data.NonEmptyChain
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchersSugar.eqTo
import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import requests.CalculationResponses
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.CalculationInputs
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Resubmission
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.calculatepublicpensionadjustment.models.Done
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex

class SubmissionServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach
    with CalculationResponses {

  private val mockAuditService         = mock[AuditService]
  private val mockSubmissionRepository = mock[SubmissionRepository]
  private val mockUuidService          = mock[UuidService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditService)
    reset(mockSubmissionRepository)
    reset(mockUuidService)
  }

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new SubmissionService(mockAuditService, mockSubmissionRepository, mockUuidService)

  "SubmissionService" - {

    "submit" - {

      "must succeed and return a uniqueId when submission is inserted" in {

        when(mockUuidService.random()).thenReturn("uniqueId")
        when(mockSubmissionRepository.insert(any())).thenReturn(Future.successful(Done))

        val result: Future[Either[NonEmptyChain[String], String]] =
          service.submit(CalculationInputs(Resubmission(false, None), None, None), None)(hc)

        result.futureValue mustBe Right("uniqueId")
      }

      "must fail when insert fails" in {

        when(mockUuidService.random()).thenReturn("uniqueId")
        when(mockSubmissionRepository.insert(any())).thenReturn(Future.failed(new RuntimeException("exception")))

        val result: Future[Either[NonEmptyChain[String], String]] =
          service.submit(CalculationInputs(Resubmission(false, None), None, None), None)(hc)

        an[RuntimeException] mustBe thrownBy(result.futureValue)
      }
    }

    "retrieve" - {

      "must return a submission when it exists in the repository" in {
        val submission = Submission("uniqueId", CalculationInputs(Resubmission(false, None), None, None), None)

        when(mockSubmissionRepository.get(any())).thenReturn(Future.successful(Some(submission)))

        service.retrieve("uniqueId").futureValue mustBe Some(submission)
        verify(mockSubmissionRepository, times(1)).get(eqTo("uniqueId"))
      }

      "must return None when it does not exist in the repository" in {
        when(mockSubmissionRepository.get("unknownId")).thenReturn(Future.successful(None))

        service.retrieve("unknownId").futureValue mustBe None
        verify(mockSubmissionRepository, times(1)).get(eqTo("unknownId"))
      }
    }
  }

  "UuidService" in {
    val uuidService = new UuidService

    val uniqueId: String = uuidService.random()

    uniqueId.length mustBe 36
    val uuidRegex: Regex = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$".r
    uuidRegex.matches(uniqueId) mustBe true
  }

}
