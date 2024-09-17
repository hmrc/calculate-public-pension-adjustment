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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{AnnualAllowanceSetup, CalculationInputs, LifetimeAllowanceSetup, MaybePIAIncrease, MaybePIAUnchangedOrDecreased, Resubmission, Setup}

class SubmissionSpec extends AnyFreeSpec with ScalaCheckPropertyChecks with Logging {

  "SubmissionRequest" - {

    "must serialise to expected Json" in {
      val calculationInputs = CalculationInputs(
        Resubmission(false, None),
        Setup(
          Some(
            AnnualAllowanceSetup(
              Some(true),
              Some(false),
              Some(false),
              Some(false),
              Some(false),
              Some(false),
              Some(MaybePIAIncrease.No),
              Some(MaybePIAUnchangedOrDecreased.No),
              Some(false),
              Some(false),
              Some(false),
              Some(false)
            )
          ),
          Some(
            LifetimeAllowanceSetup(
              Some(true),
              Some(false),
              Some(true),
              Some(false),
              Some(false),
              Some(false),
              Some(true)
            )
          )
        ),
        None,
        None
      )
      val submissionRequest = SubmissionRequest(calculationInputs, None, "userId", "uniqueId")

      val serialised: JsValue = Json.toJson(submissionRequest)

      val expectedJson = Json.parse(
        "{\"calculationInputs\":{\"resubmission\":{\"isResubmission\":false},\"setup\":{\"annualAllowanceSetup\":{\"savingsStatement\":true," +
          "\"pensionProtectedMember\":false,\"hadAACharge\":false,\"contributionRefunds\":false,\"netIncomeAbove100K\":false," +
          "\"netIncomeAbove190K\":false,\"maybePIAIncrease\":\"no\",\"maybePIAUnchangedOrDecreased\":\"no\",\"pIAAboveAnnualAllowanceIn2023\":false," +
          "\"netIncomeAbove190KIn2023\":false,\"flexibleAccessDcScheme\":false,\"contribution4000ToDirectContributionScheme\":false}," +
          "\"lifetimeAllowanceSetup\":{\"benefitCrystallisationEventFlag\":true,\"previousLTACharge\":false," +
          "\"changeInLifetimeAllowancePercentageInformedFlag\":true,\"increaseInLTACharge\":false,\"newLTACharge\":false," +
          "\"multipleBenefitCrystallisationEventFlag\":false,\"otherSchemeNotification\":true}}},\"userId\":\"userId\"," +
          "\"uniqueId\":\"uniqueId\"}"
      )

      serialised mustEqual expectedJson
    }

    "must de-serialise from valid Json with userAnswers" in {
      val json = Json.parse(
        "{\"calculationInputs\":{\"resubmission\":{\"isResubmission\":false},\"setup\":{\"annualAllowanceSetup\":{\"savingsStatement\":true," +
          "\"pensionProtectedMember\":false,\"hadAACharge\":false,\"contributionRefunds\":false,\"netIncomeAbove100K\":false," +
          "\"netIncomeAbove190K\":false,\"maybePIAIncrease\":\"no\",\"maybePIAUnchangedOrDecreased\":\"no\",\"pIAAboveAnnualAllowanceIn2023\":false," +
          "\"netIncomeAbove190KIn2023\":false,\"flexibleAccessDcScheme\":false,\"contribution4000ToDirectContributionScheme\":false}," +
          "\"lifetimeAllowanceSetup\":{\"benefitCrystallisationEventFlag\":true,\"previousLTACharge\":false," +
          "\"changeInLifetimeAllowancePercentageInformedFlag\":true,\"increaseInLTACharge\":false,\"newLTACharge\":false," +
          "\"multipleBenefitCrystallisationEventFlag\":false,\"otherSchemeNotification\":true}}},\"userId\":\"userId\"," +
          "\"uniqueId\":\"uniqueId\"}"
      )

      val deserialised: JsResult[SubmissionRequest] = json.validate[SubmissionRequest]

      val calculationInputs = CalculationInputs(
        Resubmission(false, None),
        Setup(
          Some(
            AnnualAllowanceSetup(
              Some(true),
              Some(false),
              Some(false),
              Some(false),
              Some(false),
              Some(false),
              Some(MaybePIAIncrease.No),
              Some(MaybePIAUnchangedOrDecreased.No),
              Some(false),
              Some(false),
              Some(false),
              Some(false)
            )
          ),
          Some(
            LifetimeAllowanceSetup(
              Some(true),
              Some(false),
              Some(true),
              Some(false),
              Some(false),
              Some(false),
              Some(true)
            )
          )
        ),
        None,
        None
      )
      val submissionRequest = SubmissionRequest(calculationInputs, None, "userId", "uniqueId")
      deserialised mustEqual (JsSuccess(submissionRequest))
    }

    "serialise" in {
      val calculationInputs = CalculationInputs(
        Resubmission(false, None),
        Setup(
          Some(
            AnnualAllowanceSetup(
              Some(true),
              Some(false),
              Some(false),
              Some(false),
              Some(false),
              Some(false),
              Some(MaybePIAIncrease.No),
              Some(MaybePIAUnchangedOrDecreased.No),
              Some(false),
              Some(false),
              Some(false),
              Some(false)
            )
          ),
          Some(
            LifetimeAllowanceSetup(
              Some(true),
              Some(false),
              Some(true),
              Some(false),
              Some(false),
              Some(false),
              Some(true)
            )
          )
        ),
        None,
        None
      )
      val submissionRequest =
        SubmissionRequest(calculationInputs, Some(SubmissionTestData.calculationResponse), "userId", "uniqueId")
      val json: String      = Json.toJson(submissionRequest).toString

      json must startWith("{\"calculationInputs\":{")
    }
  }
}
