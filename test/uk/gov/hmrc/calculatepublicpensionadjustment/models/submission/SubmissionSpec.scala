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
import org.scalatest.matchers.must.Matchers.{convertToAnyMustWrapper, startWith}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logging
import play.api.libs.json.{JsResult, JsSuccess, JsValue, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{CalculationInputs, Resubmission}

class SubmissionSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with Logging {

  "SubmissionRequest" - {

    "must serialise to expected Json" in {
      val calculationInputs = CalculationInputs(Resubmission(false, None), None, None)
      val submissionRequest = SubmissionRequest(calculationInputs, None, "sessionId")

      val serialised: JsValue = Json.toJson(submissionRequest)

      val expectedJson = Json.obj(
        "calculationInputs" -> Json.obj("resubmission" -> Json.obj("isResubmission" -> false)),
        "sessionId"         -> "sessionId"
      )
      serialised mustEqual expectedJson
    }

    "must de-serialise from valid Json with userAnswers" in {
      val json = Json.obj(
        "calculationInputs" -> Json.obj("resubmission" -> Json.obj("isResubmission" -> false)),
        "sessionId"         -> "sessionId"
      )

      val deserialised: JsResult[SubmissionRequest] = json.validate[SubmissionRequest]

      val calculationInputs = CalculationInputs(Resubmission(false, None), None, None)
      val submissionRequest = SubmissionRequest(calculationInputs, None, "sessionId")
      deserialised mustEqual (JsSuccess(submissionRequest))
    }

    "serialise" in {
      val calculationInputs = CalculationInputs(Resubmission(false, None), None, None)
      val submissionRequest =
        SubmissionRequest(calculationInputs, Some(SubmissionTestData.calculationResponse), "sessionId")
      val json: String      = Json.toJson(submissionRequest).toString

      json must startWith("{\"calculationInputs\":{")
    }
  }
}
