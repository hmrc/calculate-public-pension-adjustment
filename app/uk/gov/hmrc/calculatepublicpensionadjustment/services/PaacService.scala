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

package uk.gov.hmrc.calculatepublicpensionadjustment.services

import com.google.inject.Inject
import uk.gov.hmrc.calculatepublicpensionadjustment.connectors.PaacConnector
import uk.gov.hmrc.calculatepublicpensionadjustment.logging.Logging
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Income.BelowThreshold
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac._
import uk.gov.hmrc.http.HeaderCarrier

import java.time.LocalDate
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.math.BigDecimal.RoundingMode
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

    val requestResponseMapByPeriod: Map[Period, (CppaTaxYear, Option[PaacResponseRow], Option[PaacResponseRow])] =
      calculationRequest.taxYears
        .map { cppaTaxYear =>
          if (cppaTaxYear.period == Period._2016) {
            val oPaacResponseRow = paacResponse.rows.find { paacResponseRow =>
              paacResponseRow.taxYear.period == Period._2016PreAlignment
            }

            val oPaacResponse2016PostRow = paacResponse.rows.find { paacResponseRow =>
              paacResponseRow.taxYear.period == Period._2016PostAlignment
            }

            cppaTaxYear.period -> (cppaTaxYear, oPaacResponseRow, oPaacResponse2016PostRow)
          } else {
            val oPaacResponseRow = paacResponse.rows.find { paacResponseRow =>
              paacResponseRow.taxYear.period == cppaTaxYear.period
            }

            cppaTaxYear.period -> (cppaTaxYear, oPaacResponseRow, None)
          }
        }
        .toMap
        .filterNot(v => v._1 == Period._2013 | v._1 == Period._2014 | v._1 == Period._2015)

    val outDates = requestResponseMapByPeriod.flatMap { v =>
      (v._1, v._2._1) match {
        case (
              Period._2016 | Period._2017 | Period._2018 | Period._2019,
              CppaTaxYear2016To2023.NormalTaxYear(_, taxYearSchemes, totalIncome, chargePaidByMember, _, _, _)
            ) =>
          Some(
            buildOutOfDatesTaxYearsCalculationResult(
              v._1,
              calculationRequest.scottishTaxYears,
              totalIncome,
              chargePaidByMember,
              taxYearSchemes,
              v._2._2,
              v._2._3
            )
          )

        case (
              Period._2016 | Period._2017 | Period._2018 | Period._2019,
              CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                _,
                taxYearSchemes,
                totalIncome,
                chargePaidByMember,
                _,
                _,
                _,
                _,
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
              v._2._2,
              v._2._3
            )
          )

        case (
              Period._2016 | Period._2017 | Period._2018 | Period._2019,
              CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _,
                _,
                _,
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
              v._2._2,
              v._2._3
            )
          )

        case _ => None
      }

    }.toList

    val inDates: List[InDatesTaxYearsCalculation] = requestResponseMapByPeriod.flatMap { v =>
      (v._1, v._2._1) match {
        case (
              Period._2020 | Period._2021 | Period._2022 | Period._2023,
              CppaTaxYear2016To2023.NormalTaxYear(_, taxYearSchemes, totalIncome, chargePaidByMember, _, _, _)
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
              CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
                _,
                _,
                _,
                _,
                taxYearSchemes,
                totalIncome,
                chargePaidByMember,
                _,
                _,
                _,
                _,
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
              CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
                _,
                _,
                totalIncome,
                chargePaidByMember,
                taxYearSchemes,
                _,
                _,
                _,
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
    oPaacResponseRow: Option[PaacResponseRow],
    oPaacResponse2016PostRow: Option[PaacResponseRow]
  ): OutOfDatesTaxYearsCalculation = {

    val originalCharge = calculateOriginalCharge(chargePaidByMember, taxYearSchemes)

    val chargeableAmount =
      if (period == Period._2016)
        oPaacResponseRow
          .map(_.chargeableAmount)
          .getOrElse(0) + oPaacResponse2016PostRow.map(_.chargeableAmount).getOrElse(0)
      else
        oPaacResponseRow.map(_.chargeableAmount).getOrElse(0)

    val revisedCharge = calculateRevisedCharge(scottishTaxYears, period, totalIncome, chargeableAmount)

    val totalCompensation = originalCharge - revisedCharge

    val adjustedCompensation =
      if (totalCompensation > 0)
        totalCompensation
      else
        0

    val directCompensation =
      if (chargePaidByMember != 0)
        ceil((chargePaidByMember.toDouble / originalCharge.toDouble) * adjustedCompensation).toInt
      else
        0

    val compensationToSchemes: List[OutOfDatesTaxYearSchemeCalculation] = taxYearSchemes.map { s =>
      OutOfDatesTaxYearSchemeCalculation(
        s.name,
        s.pensionSchemeTaxReference,
        if (s.chargePaidByScheme != 0)
          ceil((s.chargePaidByScheme.toDouble / originalCharge.toDouble) * adjustedCompensation).toInt
        else
          0
      )
    }

    val indirectCompensation = compensationToSchemes.map(_.compensation).sum

    val unusedAnnualAllowance: Option[Int] =
      if (period == Period._2016) {
        oPaacResponse2016PostRow
          .map(_.predictedFutureUnusedAllowance) match {
          case a @ Some(_) => a
          case None        => oPaacResponseRow.map(_.predictedFutureUnusedAllowance)
        }
      } else
        oPaacResponseRow.map(_.predictedFutureUnusedAllowance)

    OutOfDatesTaxYearsCalculation(
      period,
      directCompensation,
      indirectCompensation,
      chargePaidByMember,
      taxYearSchemes.map(_.chargePaidByScheme).sum,
      chargeableAmount,
      floor(revisedCharge).toInt,
      unusedAnnualAllowance.getOrElse(0),
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
      case (Period._2016 | Period._2017 | Period._2018 | Period._2019, true) =>
        calculateRevisedChargeHelper(scottishTaxYears, period, totalIncome, chargeableAmount)

      case (Period._2016 | Period._2017 | Period._2018 | Period._2019, false) =>
        0

      case (Period._2020 | Period._2021 | Period._2022 | Period._2023, _) =>
        calculateRevisedChargeHelper(scottishTaxYears, period, totalIncome, chargeableAmount)
    }

  @tailrec
  private def calculateRevisedChargeHelper(
    scottishTaxYears: List[Period],
    period: Period,
    totalIncome: Int,
    chargeableAmount: Int,
    revisedCharge: Double = 0.0
  ): Double =
    if (chargeableAmount == 0) {
      if (revisedCharge > 0)
        BigDecimal(revisedCharge).setScale(2, RoundingMode.DOWN).toDouble
      else
        BigDecimal(revisedCharge).setScale(2, RoundingMode.UP).toDouble
    } else {
      val taxRate = findTaxRate(scottishTaxYears, period, totalIncome + chargeableAmount)

      val maxChargeableAmount = (totalIncome + chargeableAmount) - taxRate._2

      val chargeableAmountUnderTaxRate =
        if (chargeableAmount < maxChargeableAmount) chargeableAmount
        else maxChargeableAmount

      val charge = taxRate._1 * chargeableAmountUnderTaxRate

      calculateRevisedChargeHelper(
        scottishTaxYears,
        period,
        totalIncome,
        chargeableAmount - chargeableAmountUnderTaxRate,
        revisedCharge + charge
      )
    }

  def findTaxRate(scottishTaxYears: List[Period], period: Period, totalIncome: Int): (Double, Int) =
    (scottishTaxYears.contains(period), period) match {
      case (true, Period._2016) => ScottishTaxRateTill2018._2016().getTaxRate(totalIncome)

      case (false, Period._2016) => NonScottishTaxRate._2016().getTaxRate(totalIncome)

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

  def isBelowBasicOrIntermediateTaxRate(scottishTaxYears: List[Period], period: Period, totalIncome: Int): Boolean =
    (
      scottishTaxYears.contains(period),
      period,
      findTaxRate(scottishTaxYears: List[Period], period: Period, totalIncome: Int)._1
    ) match {
      case (true, Period._2016 | Period._2017 | Period._2018, taxRate) =>
        taxRate <= ScottishTaxRateTill2018.ScottishTaxRatesTill2018().basicTaxRate

      case (true, _, taxRate) =>
        taxRate <= ScottishTaxRateAfter2018.ScottishTaxRatesAfter2018().intermediateTaxRate

      case (false, _, taxRate) =>
        taxRate <= NonScottishTaxRate.NonScottishTaxRates().basicTaxRate
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
      case CppaTaxYear2016To2023
            .InitialFlexiblyAccessedTaxYear(_, Some(flexiAccessDate), _, _, _, _, _, Period._2016, _, _, _, _) =>
        if (flexiAccessDate.isAfter(LocalDate.of(2015, 4, 5)) && flexiAccessDate.isBefore(LocalDate.of(2015, 7, 9)))
          Period._2016PreAlignment
        else
          Period._2016PostAlignment

      case CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(_, _, _, _, _, _, _, period, _, _, _, _) => period
    }

    val paacTaxYears: List[PaacTaxYear] = calculationRequest.taxYears.flatMap {
      case CppaTaxYear2011To2015(pensionInputAmount, period) =>
        List(PaacTaxYear2011To2015.NormalTaxYear(pensionInputAmount, period))

      case CppaTaxYear2016To2023
            .NormalTaxYear(pensionInputAmount, _, _, _, Period._2016, _, None) =>
        List(
          PaacTaxYear2016PreAlignment.NormalTaxYear(
            pensionInputAmount,
            Period._2016PreAlignment
          )
        )

      case CppaTaxYear2016To2023
            .NormalTaxYear(pensionInputAmount, _, _, _, Period._2016, _, Some(0)) =>
        List(
          PaacTaxYear2016PreAlignment.NormalTaxYear(
            pensionInputAmount,
            Period._2016PreAlignment
          )
        )

      case CppaTaxYear2016To2023
            .NormalTaxYear(pensionInputAmount, _, _, _, Period._2016, _, Some(pensionInput2016PostAmount)) =>
        List(
          PaacTaxYear2016PreAlignment.NormalTaxYear(
            pensionInputAmount,
            Period._2016PreAlignment
          ),
          PaacTaxYear2016PostAlignment.NormalTaxYear(
            pensionInput2016PostAmount,
            Period._2016PostAlignment
          )
        )

      case CppaTaxYear2016To2023.NormalTaxYear(pensionInputAmount, _, _, _, period, income, _) =>
        List(
          PaacTaxYear2017ToCurrent.NormalTaxYear(
            pensionInputAmount,
            income.getOrElse(BelowThreshold),
            period
          )
        )

      case CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            taxYearSchemes,
            _,
            _,
            Period._2016,
            _,
            definedBenefitInput2016PostAmount,
            definedContributionInput2016PostAmount,
            _
          ) if oFlexiblyAccessedTaxYear.contains(Period._2016PreAlignment) =>
        List(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            Period._2016PreAlignment
          ),
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            definedBenefitInput2016PostAmount
              .getOrElse(0) + taxYearSchemes.map(_.revisedPensionInput2016PostAmount.getOrElse(0)).sum,
            definedContributionInput2016PostAmount.getOrElse(0),
            Period._2016PostAlignment
          )
        )

      case CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            taxYearSchemes,
            _,
            _,
            Period._2016,
            _,
            definedBenefitInput2016PostAmount,
            definedContributionInput2016PostAmount,
            postAccessDefinedContributionInput2016PostAmount
          ) if oFlexiblyAccessedTaxYear.contains(Period._2016PostAlignment) =>
        List(
          PaacTaxYear2016PreAlignment.NormalTaxYear(
            definedBenefitInputAmount + preAccessDefinedContributionInputAmount + postAccessDefinedContributionInputAmount + taxYearSchemes
              .map(_.revisedPensionInputAmount)
              .sum,
            Period._2016PreAlignment
          ),
          PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(
            definedBenefitInput2016PostAmount.getOrElse(0) + taxYearSchemes
              .map(_.revisedPensionInput2016PostAmount.getOrElse(0))
              .sum,
            definedContributionInput2016PostAmount.getOrElse(0),
            postAccessDefinedContributionInput2016PostAmount.getOrElse(0),
            Period._2016PostAlignment
          )
        )

      case CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            _,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            taxYearSchemes,
            _,
            _,
            period,
            income,
            _,
            _,
            _
          ) =>
        List(
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
            definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
            preAccessDefinedContributionInputAmount,
            postAccessDefinedContributionInputAmount,
            income.getOrElse(BelowThreshold),
            period
          )
        )

      case CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            taxYearSchemes,
            Period._2016,
            _,
            definedBenefitInput2016PostAmount,
            definedContributionInput2016PostAmount
          ) =>
        List(
          PaacTaxYear2016PreAlignment.NormalTaxYear(
            definedBenefitInputAmount + definedContributionInputAmount + taxYearSchemes
              .map(_.revisedPensionInputAmount)
              .sum,
            Period._2016PreAlignment
          ),
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
            definedBenefitInput2016PostAmount
              .getOrElse(0) + taxYearSchemes.map(_.revisedPensionInput2016PostAmount.getOrElse(0)).sum,
            definedContributionInput2016PostAmount.getOrElse(0),
            Period._2016PostAlignment
          )
        )

      case CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            taxYearSchemes,
            period,
            income,
            _,
            _
          ) if oFlexiblyAccessedTaxYear.forall(p => List(period, p) == List(period, p).sorted) =>
        List(
          PaacTaxYear2017ToCurrent.NormalTaxYear(
            definedBenefitInputAmount + definedContributionInputAmount + taxYearSchemes
              .map(_.revisedPensionInputAmount)
              .sum,
            income.getOrElse(BelowThreshold),
            period
          )
        )

      case CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount,
            definedContributionInputAmount,
            _,
            _,
            taxYearSchemes,
            period,
            income,
            _,
            _
          ) =>
        List(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
            definedBenefitInputAmount + taxYearSchemes.map(_.revisedPensionInputAmount).sum,
            definedContributionInputAmount,
            income.getOrElse(BelowThreshold),
            period
          )
        )

    }
    PaacRequest(paacTaxYears, paacTaxYears.map(_.period).sorted.max)
  }

}
