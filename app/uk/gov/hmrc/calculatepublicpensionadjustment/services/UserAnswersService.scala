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

import play.api.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.{SubmissionRepository, UserAnswersRepository}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersService @Inject() (
  submissionService: SubmissionService,
  userAnswers: UserAnswersRepository
)(implicit ec: ExecutionContext)
    extends Logging {

  def retrieveUserAnswers(uniqueId: String): Future[Option[UserAnswers]] = userAnswers.get(uniqueId)

  def updateUserAnswers(updatedUserAnswers: UserAnswers): Future[Boolean] =
    userAnswers.set(updatedUserAnswers) map {
      case Done => true
      case _    => false
    }

  def updateSubmissionStartedToTrue(uniqueId: String): Future[Boolean] = {
    val submission: Future[Option[Submission]] = submissionService.retrieve(uniqueId)
    submission.flatMap {
      case Some(submission) =>
        val userAnswers = retrieveUserAnswers(submission.sessionId)
        userAnswers.flatMap {
          case Some(userAnswers) =>
            val updatedUserAnswers = userAnswers.copy(submissionStarted = true)
            updateUserAnswers(updatedUserAnswers)
          case None              =>
            Future.successful(false)
        }
      case None             =>
        Future.successful(false)
    }
  }

  def updateSubmissionStartedToFalse(uniqueId: String): Future[Boolean] = {
    val submission: Future[Option[Submission]] = submissionService.retrieve(uniqueId)
    submission.flatMap {
      case Some(submission) =>
        val userAnswers = retrieveUserAnswers(submission.sessionId)
        userAnswers.flatMap {
          case Some(userAnswers) =>
            val updatedUserAnswers = userAnswers.copy(submissionStarted = false)
            updateUserAnswers(updatedUserAnswers)
          case None              =>
            Future.successful(false)
        }
      case None             =>
        Future.successful(false)
    }
  }
}
