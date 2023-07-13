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

package uk.gov.hmrc.calculatepublicpensionadjustment.services

import cats.data.{EitherT, NonEmptyChain}
import play.api.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.CalculationUserAnswers
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.CalculationResponse
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.{PPASubmissionEvent, Submission}
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionService @Inject() (
  auditService: AuditService,
  submissionRepository: SubmissionRepository,
  uuidService: UuidService
)(implicit ec: ExecutionContext)
    extends Logging {

  def submit(
    calculationUserAnswers: CalculationUserAnswers,
    calculationResponse: Option[CalculationResponse]
  )(implicit hc: HeaderCarrier): Future[Either[NonEmptyChain[String], String]] = {

    val uniqueId   = uuidService.random()
    val submission = buildSubmission(uniqueId, calculationUserAnswers, calculationResponse)

    val result: EitherT[Future, NonEmptyChain[String], String] = for {
      _ <- EitherT.liftF(Future.successful(auditService.auditSubmitRequest(buildAudit(submission))))
      _ <- EitherT.liftF(submissionRepository.insert(submission))
    } yield submission.uniqueId

    result.value
  }

  def retrieve(submissionUniqueId: String): Future[Option[Submission]] = submissionRepository.get(submissionUniqueId)

  private def buildSubmission(
    uniqueId: String,
    calculationUserAnswers: CalculationUserAnswers,
    calculationResponse: Option[CalculationResponse]
  ) = Submission(uniqueId, calculationUserAnswers, calculationResponse)

  private def buildAudit(submission: Submission): PPASubmissionEvent = PPASubmissionEvent(submission.uniqueId)
}
