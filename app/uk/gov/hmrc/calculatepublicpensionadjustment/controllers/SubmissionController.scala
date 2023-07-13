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

import cats.data.EitherT
import play.api.Logging
import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}
import play.api.mvc._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.{RetrieveSubmissionResponse, Submission, SubmissionRequest, SubmissionResponse}
import uk.gov.hmrc.calculatepublicpensionadjustment.services.SubmissionService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionController @Inject() (
  override val controllerComponents: ControllerComponents,
  submissionService: SubmissionService
)(implicit ec: ExecutionContext)
    extends BackendBaseController
    with Logging {

  def submit: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[SubmissionRequest]("Submission") { submissionRequest =>
      val result = EitherT(submissionService.submit(submissionRequest.userAnswers, submissionRequest.calculation))

      result.fold(
        errors => BadRequest(Json.toJson(SubmissionResponse.Failure(errors))),
        uniqueId => {
          logger.info(s"request.id : ${request.id}, uniqueId : $uniqueId")

          Accepted(Json.toJson(SubmissionResponse.Success(uniqueId)))
        }
      )
    }
  }

  def retrieveSubmission(uniqueId: String): Action[AnyContent] = Action.async {
    logger.info(s"uniqueId : $uniqueId")

    val submission: Future[Option[Submission]] = submissionService.retrieve(uniqueId)

    submission.map(s =>
      s match {
        case Some(submission) =>
          Ok(Json.toJson(RetrieveSubmissionResponse(submission.userAnswers, submission.calculation)))
        case None             => BadRequest
      }
    )
  }

  private def withValidJson[T](
    errMessage: String
  )(f: T => Future[Result])(implicit request: Request[JsValue], reads: Reads[T]): Future[Result] =
    request.body.validate[T] match {
      case JsSuccess(value, _) => f(value)
      case _                   => Future.successful(BadRequest(s"Invalid $errMessage"))
    }
}
