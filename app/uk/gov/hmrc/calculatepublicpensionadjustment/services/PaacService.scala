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

import com.google.inject.Inject
import uk.gov.hmrc.calculatepublicpensionadjustment.connectors.PaacConnector
import uk.gov.hmrc.calculatepublicpensionadjustment.logging.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac._
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}
import scala.math._

class PaacService @Inject() (connector: PaacConnector)(implicit ec: ExecutionContext) extends Logging {

  def calculate(
    calculationRequest: CalculationRequest
  )(implicit hc: HeaderCarrier): Future[CalculationResponse] =
    for {
      paacResponse       <- sendRequest(calculationRequest)
      calculationResponse = calculateCompensation(calculationRequest, paacResponse)
    } yield calculationResponse

  def calculateCompensation(calculationRequest: CalculationRequest, paacResponse: PaacResponse): CalculationResponse = {

    val requestResponseMapByPeriod: Map[Period, (CppaTaxYear, Option[PaacResponseRow])] = calculationRequest.taxYears
      .map { cppaTaxYear =>
        val oPaacResponseRow = paacResponse.rows.find { paacResponseRow =>
          paacResponseRow.taxYear.period == cppaTaxYear.period
        }
        cppaTaxYear.period -> (cppaTaxYear, oPaacResponseRow)
      }
      .toMap
      .filterNot(v => v._1 == Period._2013 | v._1 == Period._2014 | v._1 == Period._2015)

    val outDates = requestResponseMapByPeriod.flatMap { v =>
      (v._1, v._2._1) match {
        case (
              Period._2016PreAlignment,
              CppaTaxYear2016PreAlignment.NormalTaxYear(_, taxYearSchemes, totalIncome, chargePaidByMember, _)
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2016PreAlignment,
              CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                _,
                taxYearSchemes,
                totalIncome,
                chargePaidByMember,
                _
              )
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2016PreAlignment,
              CppaTaxYear2016PreAlignment.PostFlexiblyAccessedTaxYear(
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2016PostAlignment,
              CppaTaxYear2016PostAlignment.NormalTaxYear(_, totalIncome, chargePaidByMember, taxYearSchemes, _)
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2016PostAlignment,
              CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2016PostAlignment,
              CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2017 | Period._2018 | Period._2019,
              CppaTaxYear2017ToCurrent.NormalTaxYear(_, _, totalIncome, chargePaidByMember, taxYearSchemes, _)
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2017 | Period._2018 | Period._2019,
              CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2017 | Period._2018 | Period._2019,
              CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case _ => None
      }

    }.toList

    val inDates: List[InDatesTaxYearsCalculation] = requestResponseMapByPeriod.flatMap { v =>
      (v._1, v._2._1) match {
        case (
              Period._2020 | Period._2021 | Period._2022 | Period._2023,
              CppaTaxYear2017ToCurrent.NormalTaxYear(_, _, totalIncome, chargePaidByMember, taxYearSchemes, _)
            ) =>
          Some(
            buildInDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2020 | Period._2021 | Period._2022 | Period._2023,
              CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildInDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case (
              Period._2020 | Period._2021 | Period._2022 | Period._2023,
              CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _
              )
            ) =>
          Some(
            buildInDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2
            )
          )

        case _ => None
      }

    }.toList

    CalculationResponse(
      calculationRequest.resubmission,
      calculateTotalAmounts(outDates, inDates),
      outDates.sortBy(_.period),
      inDates.sortBy(_.period)
    )

  }

  def buildOutOfDatesTaxYearsCalculationResult(
    period: Period,
    scottishTaxYears: List[Period],
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    oPaacResponseRow: Option[PaacResponseRow]
  ): OutOfDatesTaxYearsCalculation = {

    val originalCharge = calculateOriginalCharge(chargePaidByMember, taxYearSchemes)

    val chargeableAmount = oPaacResponseRow.map(_.chargeableAmount).getOrElse(0)

    val revisedCharge = calculateRevisedCharge(scottishTaxYears, period, totalIncome, chargeableAmount)

    val totalCompensation = originalCharge - revisedCharge

    val adjustedCompensation =
      if (totalCompensation > 0)
        totalCompensation
      else
        0

    val directCompensation =
      if (chargePaidByMember != 0) {
        ceil((chargePaidByMember.toDouble / originalCharge.toDouble) * adjustedCompensation).toInt
      } else {
        0
      }

    val compensationToSchemes: List[OutOfDatesTaxYearSchemeCalculation] = taxYearSchemes.map { s =>
      OutOfDatesTaxYearSchemeCalculation(
        s.name,
        s.pensionSchemeTaxReference,
        if (s.chargePaidByScheme != 0) {
          ceil((s.chargePaidByScheme.toDouble / originalCharge.toDouble) * adjustedCompensation).toInt
        } else {
          0
        }
      )
    }

    val indirectCompensation = compensationToSchemes.map(_.compensation).sum

    OutOfDatesTaxYearsCalculation(
      period,
      directCompensation,
      indirectCompensation,
      chargePaidByMember,
      taxYearSchemes.map(_.chargePaidByScheme).sum,
      chargeableAmount,
      floor(revisedCharge).toInt,
      oPaacResponseRow.map(_.predictedFutureUnusedAllowance).getOrElse(0),
      compensationToSchemes
    )
  }

  def buildInDatesTaxYearsCalculationResult(
    period: Period,
    scottishTaxYears: List[Period],
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    oPaacResponseRow: Option[PaacResponseRow]
  ): InDatesTaxYearsCalculation = {

    val originalCharge = calculateOriginalCharge(chargePaidByMember, taxYearSchemes)

    val chargeableAmount = oPaacResponseRow.map(_.chargeableAmount).getOrElse(0)

    val revisedCharge = calculateRevisedCharge(scottishTaxYears, period, totalIncome, chargeableAmount)

    val totalCompensation = originalCharge - revisedCharge

    val (memberCredit, schemeCredit, debit): (Int, Int, Int) =
      if (totalCompensation > 0) {
        val iMemberCredit = (chargePaidByMember.toDouble / originalCharge.toDouble) * totalCompensation
        (ceil(iMemberCredit).toInt, ceil(totalCompensation - iMemberCredit).toInt, 0)
      } else (0, 0, floor(-totalCompensation).toInt)

    val inDatesTaxYearSchemeCalculation: List[InDatesTaxYearSchemeCalculation] = taxYearSchemes.map { s =>
      InDatesTaxYearSchemeCalculation(
        s.name,
        s.pensionSchemeTaxReference,
        s.chargePaidByScheme
      )
    }

    InDatesTaxYearsCalculation(
      period,
      memberCredit,
      schemeCredit,
      debit,
      chargePaidByMember,
      taxYearSchemes.map(_.chargePaidByScheme).sum,
      chargeableAmount,
      floor(revisedCharge).toInt,
      oPaacResponseRow.map(_.predictedFutureUnusedAllowance).getOrElse(0),
      inDatesTaxYearSchemeCalculation
    )

  }

  def calculateOriginalCharge(chargePaidByMember: Int, taxYearSchemes: List[TaxYearScheme]): Int =
    chargePaidByMember + taxYearSchemes.map(_.chargePaidByScheme).sum

  def calculateRevisedCharge(
    scottishTaxYears: List[Period],
    period: Period,
    totalIncome: Int,
    chargeableAmount: Int
  ): Double =
    (period, chargeableAmount > 0) match {
      case (Period._2016PreAlignment | Period._2016PostAlignment | Period._2017 | Period._2018 | Period._2019, true) =>
        chargeableAmount * findTaxRate(scottishTaxYears, period, totalIncome)

      case (Period._2016PreAlignment | Period._2016PostAlignment | Period._2017 | Period._2018 | Period._2019, false) =>
        0

      case (Period._2020 | Period._2021 | Period._2022 | Period._2023, _) =>
        chargeableAmount * findTaxRate(scottishTaxYears, period, totalIncome)
    }

  def findTaxRate(scottishTaxYears: List[Period], period: Period, totalIncome: Int): Double =
    (scottishTaxYears.contains(period), period) match {
      case (true, Period._2016PreAlignment | Period._2016PostAlignment) =>
        ScottishTaxRateTill2018._2016().getTaxRate(totalIncome)

      case (false, Period._2016PreAlignment | Period._2016PostAlignment) =>
        NonScottishTaxRate._2016().getTaxRate(totalIncome)

      case (true, Period._2017) => ScottishTaxRateTill2018._2017().getTaxRate(totalIncome)

      case (false, Period._2017) => NonScottishTaxRate._2017().getTaxRate(totalIncome)

      case (true, Period._2018) => ScottishTaxRateTill2018._2018().getTaxRate(totalIncome)

      case (false, Period._2018) => NonScottishTaxRate._2018().getTaxRate(totalIncome)

      case (true, Period._2019) => ScottishTaxRateAfter2018._2019().getTaxRate(totalIncome)

      case (false, Period._2019) => NonScottishTaxRate._2019().getTaxRate(totalIncome)

      case (true, Period._2020) => ScottishTaxRateAfter2018._2020().getTaxRate(totalIncome)

      case (false, Period._2020 | Period._2021) => NonScottishTaxRate._2020To2021().getTaxRate(totalIncome)

      case (true, Period._2021) => ScottishTaxRateAfter2018._2021().getTaxRate(totalIncome)

      case (true, Period._2022) => ScottishTaxRateAfter2018._2022().getTaxRate(totalIncome)

      case (false, Period._2022 | Period._2023) => NonScottishTaxRate._2022To2023().getTaxRate(totalIncome)

      case (true, Period._2023) => ScottishTaxRateAfter2018._2023().getTaxRate(totalIncome)
    }

  def calculateTotalAmounts(
    outDates: List[OutOfDatesTaxYearsCalculation],
    inDates: List[InDatesTaxYearsCalculation]
  ): TotalAmounts =
    TotalAmounts(
      outDates.map(v => v.directCompensation + v.indirectCompensation).sum,
      inDates.map(_.debit).sum,
      inDates.map(v => v.memberCredit + v.schemeCredit).sum
    )

  def sendRequest(
    calculationRequest: CalculationRequest
  )(implicit hc: HeaderCarrier): Future[PaacResponse] =
    connector
      .sendRequest(buildPaacRequest(calculationRequest))
      .map { response =>
        PaacResponse(response.rows flatMap { row =>
          row.taxYear match {
            case _: PaacTaxYear2011To2015.NoInputTaxYear | _: PaacTaxYear2016PreAlignment.NoInputTaxYear |
                _: PaacTaxYear2016PostAlignment.NoInputTaxYear | _: PaacTaxYear2017ToCurrent.NoInputTaxYear =>
              None
            case _ => Some(row)
          }
        })
      }

  def buildPaacRequest(calculationRequest: CalculationRequest): PaacRequest = {

    val oFlexiblyAccessedTaxYear: Option[Period] = calculationRequest.taxYears collectFirst {
      case CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(_, _, _, _, _, _, _, _)      =>
        Period._2016PreAlignment
      case CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(_, _, _, _, _, _, _, _)     =>
        Period._2016PostAlignment
      case CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(_, _, _, _, _, _, _, _, period) => period
    }

    val paacTaxYears: List[PaacTaxYear] = calculationRequest.taxYears.map {
      case CppaTaxYear2013To2015(pensionInputAmount, period) =>
        PaacTaxYear2011To2015.NormalTaxYear(pensionInputAmount, period)

      case CppaTaxYear2016PreAlignment.NormalTaxYear(pensionInputAmount, _, _, _, period) =>
        PaacTaxYear2016PreAlignment.NormalTaxYear(
          pensionInputAmount,
          period
        )

      case CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            _,
            _,
            _,
            period
          ) =>
        PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
          definedBenefitInputAmount,
          preAccessDefinedContributionInputAmount,
          postAccessDefinedContributionInputAmount,
          period
        )

      case CppaTaxYear2016PreAlignment.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            _,
            period
          ) =>
        PaacTaxYear2016PreAlignment.NormalTaxYear(
          definedBenefitInputAmount + definedContributionInputAmount,
          period
        )

      case CppaTaxYear2016PostAlignment.NormalTaxYear(pensionInputAmount, _, _, _, period) =>
        PaacTaxYear2016PostAlignment.NormalTaxYear(
          pensionInputAmount,
          period
        )

      case CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            _,
            _,
            _,
            period
          ) =>
        PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
          definedBenefitInputAmount,
          preAccessDefinedContributionInputAmount,
          postAccessDefinedContributionInputAmount,
          period
        )

      case CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            _,
            period
          ) if oFlexiblyAccessedTaxYear.forall(p => List(period, p) == List(period, p).sorted) =>
        PaacTaxYear2016PostAlignment.NormalTaxYear(
          definedBenefitInputAmount + definedContributionInputAmount,
          period
        )

      case CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            _,
            period
          ) =>
        PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
          definedBenefitInputAmount,
          definedContributionInputAmount,
          period
        )

      case CppaTaxYear2017ToCurrent.NormalTaxYear(pensionInputAmount, income, _, _, _, period) =>
        PaacTaxYear2017ToCurrent.NormalTaxYear(
          pensionInputAmount,
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
            _,
            period
          ) =>
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
          definedBenefitInputAmount,
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
            _,
            period
          ) if oFlexiblyAccessedTaxYear.forall(p => List(period, p) == List(period, p).sorted) =>
        PaacTaxYear2017ToCurrent.NormalTaxYear(
          definedBenefitInputAmount + definedContributionInputAmount,
          income,
          period
        )

      case CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            income,
            _,
            _,
            _,
            period
          ) =>
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
          definedBenefitInputAmount,
          definedContributionInputAmount,
          income,
          period
        )

    }

    PaacRequest(paacTaxYears, paacTaxYears.map(_.period).sorted.max)
  }

}
