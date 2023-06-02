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

package uk.gov.hmrc.calculatepublicpensionadjustment.services

import uk.gov.hmrc.calculatepublicpensionadjustment.logging.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{CalculationRequest, PaacRequest}

class PaacService extends Logging {

  def sendRequest(calculationRequest: CalculationRequest): Unit = {

    val paacRequest = buildPaacRequest(calculationRequest)
  }

  def buildPaacRequest(calculationRequest: CalculationRequest): PaacRequest = {
    val paacTaxYears = calculationRequest.taxYears.map {
      case CppaTaxYear2013To2015(pensionInputAmount, period) => PaacTaxYear2013To2015(pensionInputAmount, period)

      case CppaTaxYear2016PreAlignment.NormalTaxYear(pensionInputAmount, taxYearSchemes, _, _, period) =>
        PaacTaxYear2016PreAlignment.NormalTaxYear(
          pensionInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          period
        )

      case CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            taxYearSchemes,
            _,
            _,
            period
          ) =>
        PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
          definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          preAccessDefinedContributionInputAmount,
          postAccessDefinedContributionInputAmount,
          period
        )

      case CppaTaxYear2016PostAlignment.NormalTaxYear(pensionInputAmount, _, _, taxYearSchemes, period) =>
        PaacTaxYear2016PostAlignment.NormalTaxYear(
          pensionInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          period
        )

      case CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            _,
            _,
            taxYearSchemes,
            period
          ) =>
        PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
          definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          preAccessDefinedContributionInputAmount,
          postAccessDefinedContributionInputAmount,
          period
        )

      case CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            taxYearSchemes,
            period
          ) =>
        PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
          definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          definedContributionInputAmount,
          period
        )

      case CppaTaxYear2017ToCurrent.NormalTaxYear(pensionInputAmount, income, _, _, taxYearSchemes, period) =>
        PaacTaxYear2017ToCurrent.NormalTaxYear(
          pensionInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          income,
          period
        )

      case CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            income,
            _,
            _,
            taxYearSchemes,
            period
          ) =>
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
          definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          preAccessDefinedContributionInputAmount,
          postAccessDefinedContributionInputAmount,
          income,
          period
        )

      case CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            income,
            _,
            _,
            taxYearSchemes,
            period
          ) =>
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
          definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
          definedContributionInputAmount,
          income,
          period
        )

    }

    PaacRequest(paacTaxYears, paacTaxYears.map(_.period).sorted.max)
  }

}
