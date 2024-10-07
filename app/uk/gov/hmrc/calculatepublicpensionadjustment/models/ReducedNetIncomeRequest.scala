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

package uk.gov.hmrc.calculatepublicpensionadjustment.models
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

import play.api.libs.json.{Format, Json}

case class ReducedNetIncomeRequest(
                                    period: Period,
                                    scottishTaxYears: List[Period],
                                    totalIncome: Int,
                                    incomeSubJourney: IncomeSubJourney
                            )

object ReducedNetIncomeRequest {

  implicit lazy val format: Format[ReducedNetIncomeRequest] = Json.format
}
