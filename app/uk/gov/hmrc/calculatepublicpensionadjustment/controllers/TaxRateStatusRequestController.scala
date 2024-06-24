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

import play.api.Logging
import play.api.libs.json.{JsSuccess, JsValue, Json, Reads}
import play.api.mvc.{Action, ControllerComponents, Request, Result}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.TaxRateStatusRequest
import uk.gov.hmrc.calculatepublicpensionadjustment.services.PaacService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaxRateStatusRequestController @Inject() (
  override val controllerComponents: ControllerComponents,
  paacService: PaacService
)(implicit ec: ExecutionContext)
    extends BackendBaseController
    with Logging {

  def calculate: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[TaxRateStatusRequest]("TaxRateStatusRequest") { TaxRateStatusRequest =>
      paacService.isBelowBasicOrIntermediateTaxRate(
        TaxRateStatusRequest.scottishTaxYears,
        TaxRateStatusRequest.period,
        TaxRateStatusRequest.income
      ) match {
        case true  => Future.successful(Ok(Json.toJson(true)))
        case false => Future.successful(Ok(Json.toJson(false)))
        case _     => Future.successful(BadRequest)
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
}
