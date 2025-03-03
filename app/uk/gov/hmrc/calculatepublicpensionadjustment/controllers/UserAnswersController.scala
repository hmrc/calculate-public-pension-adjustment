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

import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}
import play.api.mvc.*
import uk.gov.hmrc.calculatepublicpensionadjustment.controllers.actions.IdentifierAction
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{RetrieveSubmissionInfo, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.UserAnswersRepository
import uk.gov.hmrc.calculatepublicpensionadjustment.services.UserAnswersService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersController @Inject() (
  cc: ControllerComponents,
  identify: IdentifierAction,
  repository: UserAnswersRepository,
  userAnswersService: UserAnswersService
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def get: Action[AnyContent] = identify.async { request =>
    repository
      .get(request.userId)
      .map {
        _.map(userAnswers => Ok(Json.toJson(userAnswers)))
          .getOrElse(NotFound)
      }
  }

  def set: Action[UserAnswers] = identify(parse.json[UserAnswers]).async { request =>
    repository
      .set(request.body)
      .map(_ => NoContent)
  }

  def testOnlyset: Action[UserAnswers] = Action.async(parse.json[UserAnswers]) { request =>
    repository
      .set(request.body)
      .map(_ => NoContent)
  }

  def keepAlive: Action[AnyContent] = identify.async { request =>
    repository
      .keepAlive(request.userId)
      .map(_ => NoContent)
  }

  def clear: Action[AnyContent] = identify.async { request =>
    repository
      .clear(request.userId)
      .map(_ => NoContent)
  }

  def retrieveUserAnswersByUniqueId: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[RetrieveSubmissionInfo]("RetrieveSubmissionInfo") { retrieveSubmissionInfo =>
      userAnswersService.retrieveUserAnswersByUniqueId(retrieveSubmissionInfo.submissionUniqueId.value) flatMap {
        case Some(answers) =>
          Future.successful(Ok(Json.toJson(answers)))
        case None          =>
          Future.successful(BadRequest)
      }
    }
  }

  def withValidJson[T](
    errMessage: String
  )(f: T => Future[Result])(implicit request: Request[JsValue], reads: Reads[T]): Future[Result] =
    request.body.validate[T] match {
      case JsSuccess(value, _) => f(value)
      case _                   => Future.successful(BadRequest(s"Invalid $errMessage"))
    }

  def updateSubmissionLander(uniqueId: String): Action[AnyContent] = Action.async {
    userAnswersService.updateSubmissionStartedToFalse(uniqueId).map {
      case true  =>
        Ok
      case false =>
        BadRequest
    }
  }

  def checkSubmissionStartedWithId(id: String): Action[AnyContent] = Action.async {
    userAnswersService
      .checkSubmissionStartedWithId(id)
      .map(submissionStarted =>
        if (submissionStarted.isEmpty) {
          NotFound
        } else {
          Ok(Json.toJson(submissionStarted))
        }
      )
  }

  def checkAndRetrieveCalcUserAnswersWithUniqueId(uniqueId: String): Action[AnyContent] = Action.async {
    implicit request =>
      userAnswersService.checkAndRetrieveCalcUserAnswersWithUniqueId(uniqueId).map { _ =>
        Ok
      }
  }

  def checkAndRetrieveCalcUserAnswersWithId(id: String): Action[AnyContent] = Action.async { implicit request =>
    userAnswersService.checkAndRetrieveCalcUserAnswersWithId(id).map { _ =>
      Ok
    }
  }
}
