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
import play.api.Logging
import play.api.http.Status.OK
import play.api.libs.json.Json
import uk.gov.hmrc.calculatepublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{UniqueId, UserAnswers}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}

import scala.concurrent.{ExecutionContext, Future}

class SubmitBackendConnector @Inject() (
  config: AppConfig,
  httpClient2: HttpClientV2
)(implicit
  ec: ExecutionContext
) extends Logging {
  def retrieveCalcUserAnswersFromSubmitBE(uniqueId: String)(implicit hc: HeaderCarrier): Future[UserAnswers] = {
    val submissionsSessionUrl =
      url"${config.sppaBaseUrl}/submit-public-pension-adjustment/calc-user-answers/$uniqueId"
    httpClient2
      .get(submissionsSessionUrl)
      .execute
      .flatMap { response =>
        response.status match {
          case OK =>
            Future.successful(response.json.as[UserAnswers])
          case _  =>
            logger.error(
              s"Unexpected response from /submit-public-pension-adjustment/calc-user-answers/$uniqueId with status : ${response.status}"
            )
            Future.failed(
              UpstreamErrorResponse(
                "Unexpected response from /submit-public-pension-adjustment/calc-user-answers",
                response.status
              )
            )
        }
      }
  }
}
