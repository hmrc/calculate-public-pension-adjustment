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

import cats.data.EitherT
import play.api.Logging
import play.api.libs.json.{JsSuccess, JsValue, Json, Reads, __}
import play.api.mvc._
import uk.gov.hmrc.calculatepublicpensionadjustment.controllers.actions.IdentifierAction
import uk.gov.hmrc.calculatepublicpensionadjustment.models.RetrieveSubmissionInfo
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.{RetrieveSubmissionResponse, Submission, SubmissionRequest, SubmissionResponse}
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.calculatepublicpensionadjustment.services.{SubmissionService, UserAnswersService}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionController @Inject() (
  override val controllerComponents: ControllerComponents,
  submissionService: SubmissionService,
  identify: IdentifierAction,
  repository: SubmissionRepository,
  userAnswersService: UserAnswersService
)(implicit ec: ExecutionContext)
    extends BackendBaseController
    with Logging {

  def submit: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[SubmissionRequest]("Submission") { submissionRequest =>
      EitherT(
        submissionService
          .submit(
            submissionRequest.calculationInputs,
            submissionRequest.calculation,
            submissionRequest.userId,
            submissionRequest.uniqueId
          )
      ).fold(
        errors => BadRequest(Json.toJson(SubmissionResponse.Failure(errors))),
        uniqueId => Accepted(Json.toJson(SubmissionResponse.Success(uniqueId)))
      )
    }
  }

  def retrieveSubmission: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[RetrieveSubmissionInfo]("RetrieveSubmissionInfo") { retrieveSubmissionInfo =>
      submissionService.retrieve(retrieveSubmissionInfo.submissionUniqueId.value) flatMap {
        case Some(submission) =>
          userAnswersService.updateSubmissionStartedToTrue(retrieveSubmissionInfo).map {
            case true  =>
              Ok(Json.toJson(RetrieveSubmissionResponse(submission.calculationInputs, submission.calculation)))
            case false =>
              BadRequest
          }
        case None             =>
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

  def clear: Action[AnyContent] = identify.async { request =>
    repository
      .clear(request.userId)
      .map(_ => NoContent)
  }
}
