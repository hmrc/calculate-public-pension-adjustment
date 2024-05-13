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

package uk.gov.hmrc.calculatepublicpensionadjustment.connectors

import com.google.inject.Inject
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.calculatepublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.calculatepublicpensionadjustment.logging.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac.{PaacRequest, PaacResponse}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class PaacConnector @Inject() (config: AppConfig, httpClient2: HttpClientV2)(implicit
  ec: ExecutionContext
) extends Logging {

  def sendRequest(paacRequest: PaacRequest)(implicit hc: HeaderCarrier): Future[PaacResponse] =
    httpClient2
      .post(url"${config.paacServiceUrl}/pension-annual-allowance-calculator/calculate")
      .withBody(Json.toJson(paacRequest))
      .execute[HttpResponse]
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[PaacResponse])
          case _  =>
            logger.error(
              s"Unexpected response from /pension-annual-allowance-calculator/calculate with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /pension-annual-allowance-calculator/calculate",
                response.status
              )
            )
        }
      }

}
