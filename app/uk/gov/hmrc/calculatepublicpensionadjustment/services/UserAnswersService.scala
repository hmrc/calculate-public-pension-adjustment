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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, RetrieveSubmissionInfo, SubmissionStatusResponse, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.UserAnswersRepository

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

  def updateSubmissionStartedToTrue(retrieveSubmissionInfo: RetrieveSubmissionInfo): Future[Boolean] =
    submissionService.retrieve(retrieveSubmissionInfo.submissionUniqueId.value).flatMap {
      case Some(submission) =>
        retrieveUserAnswers(submission.sessionId).flatMap {
          case Some(rUserAnswers) =>
            for {
              _ <- userAnswers.clear(retrieveSubmissionInfo.internalId)
              _ <-
                updateUserAnswers(
                  rUserAnswers
                    .copy(id = retrieveSubmissionInfo.internalId, submissionStarted = true, authenticated = true)
                )
              _ <- submissionService.clearBySessionId(retrieveSubmissionInfo.internalId)
              _ <- submissionService.updateSubmission(submission.copy(sessionId = retrieveSubmissionInfo.internalId))
              r <- userAnswers.clearByUniqueIdAndNotId(
                     retrieveSubmissionInfo.submissionUniqueId.value,
                     retrieveSubmissionInfo.internalId
                   )
            } yield r match {
              case Done => true
              case _    => false
            }

          case None =>
            Future.successful(false)
        }
      case None             =>
        Future.successful(false)
    }

  def updateSubmissionStartedToFalse(uniqueId: String): Future[Boolean] =
    retrieveUserAnswers(uniqueId).flatMap {
      case Some(userAnswers) =>
        updateUserAnswers(userAnswers.copy(submissionStarted = false))
      case None              =>
        Future.successful(false)
    }

  def checkSubmissionStartedWithId(id: String): Future[Option[SubmissionStatusResponse]] =
    retrieveUserAnswers(id).flatMap {
      case Some(userAnswers) =>
        Future.successful(Some(SubmissionStatusResponse(userAnswers.uniqueId, userAnswers.submissionStarted)))
      case None              =>
        Future.successful(None)
    }
}
