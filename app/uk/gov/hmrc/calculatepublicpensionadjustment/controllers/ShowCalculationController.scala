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

import com.google.inject.{Inject, Singleton}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.calculatepublicpensionadjustment.logging.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.CalculationRequest

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class ShowCalculationController @Inject() (
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends CppaBaseController(cc)
    with Logging {

  def showCalculation: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withValidJson[CalculationRequest]("Calculation request") { calculationRequest =>
      logger.info(s"Received a calculation request : $calculationRequest")
      Future.successful(Ok("Calculation request accepted"))
    }

  }
}
