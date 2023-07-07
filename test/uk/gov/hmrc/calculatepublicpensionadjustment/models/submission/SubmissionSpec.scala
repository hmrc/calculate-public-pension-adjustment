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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.submission

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Logging
import play.api.libs.json.{JsResult, JsSuccess, JsValue, Json}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.useranswers.lta.{LTACharge, LTAChargeHowPaid, LTAChargePaidByScheme, LTAChargeType, LTAChargeWhoPays, LTAProtection, LTAProtectionType, LifetimeAllowance}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.useranswers.{CalculationUserAnswers, Resubmission}

import java.time.LocalDate

class SubmissionSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with Logging {

  "SubmissionRequest" - {

    "must serialise to expected Json" in {
      val calculationUserAnswers = CalculationUserAnswers(Resubmission(false, None), None, None)
      val submissionRequest      = SubmissionRequest(calculationUserAnswers, None)

      val serialised: JsValue = Json.toJson(submissionRequest)

      val expectedJson = Json.obj(
        "userAnswers" -> Json.obj("resubmission" -> Json.obj("isResubmission" -> false))
      )
      serialised mustEqual expectedJson
    }

    "must de-serialise from valid Json with userAnswers" in {
      val json = Json.obj(
        "userAnswers" -> Json.obj("resubmission" -> Json.obj("isResubmission" -> false))
      )

      val deserialised: JsResult[SubmissionRequest] = json.validate[SubmissionRequest]

      val calculationUserAnswers = CalculationUserAnswers(Resubmission(false, None), None, None)
      val submissionRequest      = SubmissionRequest(calculationUserAnswers, None)
      deserialised mustEqual (JsSuccess(submissionRequest))
    }

    "serialise" in {

      val ltaCharge =
        LTACharge(
          1234,
          LTAChargeWhoPays.PensionScheme,
          Some(LTAChargePaidByScheme("scheme1", "pstr1", LTAChargeHowPaid.LumpSum))
        )

      val lifetimeAllowance = Some(
        LifetimeAllowance(
          LocalDate.now(),
          LTAChargeType.New,
          List(LTAProtection(LTAProtectionType.FixedProtection, "123")),
          List.empty,
          ltaCharge
        )
      )

      val calculationUserAnswers = CalculationUserAnswers(Resubmission(false, None), None, lifetimeAllowance)
      val submissionRequest      = SubmissionRequest(calculationUserAnswers, Some(SubmissionTestData.calculationResponse))
      val json                   = Json.toJson(submissionRequest)

      logger.info(s"\n ${json.toString()} \n")
    }
  }
}
