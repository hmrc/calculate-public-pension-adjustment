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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.submission

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Resubmission

import scala.io.Source

class SubmissionRequestParsingSpec extends AnyFreeSpec with Matchers {

  "Data model" - {

    "must parse a SubmissionRequest in alignment with calculate frontend" in {
      val request: SubmissionRequest = readSubmissionRequest("test/resources/SubmissionRequest.json")
      request.calculationInputs.resubmission mustBe (Resubmission(false, None))

      request.calculationInputs.annualAllowance.get.scottishTaxYears mustBe List.empty
      request.calculationInputs.annualAllowance.get.taxYears.size mustBe 2
    }
  }

  def readSubmissionRequest(calculationResponseFile: String): SubmissionRequest = {
    val source: String = Source.fromFile(calculationResponseFile).getLines().mkString
    val json: JsValue  = Json.parse(source)
    json.as[SubmissionRequest]
  }
}
