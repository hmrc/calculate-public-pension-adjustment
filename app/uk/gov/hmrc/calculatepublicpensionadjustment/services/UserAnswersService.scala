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
import uk.gov.hmrc.calculatepublicpensionadjustment.connectors.SubmitBackendConnector
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, RetrieveSubmissionInfo, SubmissionStatusResponse, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.UserAnswersRepository
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersService @Inject() (
  submissionService: SubmissionService,
  userAnswers: UserAnswersRepository,
  submitBackendConnector: SubmitBackendConnector
)(implicit ec: ExecutionContext)
    extends Logging {

  def retrieveUserAnswers(id: String): Future[Option[UserAnswers]] = userAnswers.get(id)

  def retrieveUserAnswersByUniqueId(uniqueId: String): Future[Option[UserAnswers]] = userAnswers.getByUniqueId(uniqueId)

  def updateUserAnswers(updatedUserAnswers: UserAnswers): Future[Boolean] =
    userAnswers.set(updatedUserAnswers) map {
      case Done => true
      case _    => false
    }

  def updateSubmissionStartedToTrue(retrieveSubmissionInfo: RetrieveSubmissionInfo): Future[Boolean] =
    submissionService.retrieve(retrieveSubmissionInfo.submissionUniqueId.value).flatMap {
      case Some(submission) =>
        retrieveUserAnswers(submission.id).flatMap {
          case Some(rUserAnswers) =>
            for {
              _ <- userAnswers.clear(retrieveSubmissionInfo.userId)
              _ <-
                updateUserAnswers(
                  rUserAnswers
                    .copy(id = retrieveSubmissionInfo.userId, submissionStarted = true, authenticated = true)
                )
              _ <- submissionService.retrieve(retrieveSubmissionInfo.submissionUniqueId.value).flatMap {
                     case Some(rSubmission) =>
                       for {
                         _ <- submissionService.clearByUniqueIdAndNotId(
                                retrieveSubmissionInfo.submissionUniqueId.value,
                                retrieveSubmissionInfo.userId
                              )
                         r <- submissionService.updateSubmission(
                                rSubmission
                                  .copy(id = retrieveSubmissionInfo.userId)
                              )
                       } yield r
                   }
              r <- userAnswers.clearByUniqueIdAndNotId(
                     retrieveSubmissionInfo.submissionUniqueId.value,
                     retrieveSubmissionInfo.userId
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

  def checkCalculationExistsWithUniqueId(uniqueId: String): Future[Boolean] =
    retrieveUserAnswersByUniqueId(uniqueId).flatMap {
      case Some(_) =>
        Future.successful(true)
      case None    =>
        Future.successful(false)
    }

  def checkAndRetrieveCalcUserAnswers(uniqueId: String)(implicit hc: HeaderCarrier): Future[Done] =
    for {
      calcExists <- checkCalculationExistsWithUniqueId(uniqueId)
      _          <- if (!calcExists) {
                      submitBackendConnector.retrieveCalcUserAnswersFromSubmitBE(uniqueId).flatMap { ua =>
                        userAnswers.set(ua)
                      }
                    } else {
                      Future.successful(Done)
                    }
    } yield Done

}
