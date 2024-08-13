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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation

sealed trait TaxRate {

  def personalAllowance: Int

  def freeAllowance: Int

  def basicRateAllowance: Int

  def topRateAllowance: Int = 150000

  def basicTaxRate: Double = 0.20

  def higherTaxRate: Double = 0.40

  def topTaxRate: Double = 0.45

  def getTaxRate(income: Int, grossGiftAidAmount: Int, rASContributionsAmount: Int): (Double, Int) =
    income match {
      case i if i <= basicRateAllowance + grossGiftAidAmount + rASContributionsAmount =>
        (basicTaxRate, personalAllowance)
      case i if i <= topRateAllowance + grossGiftAidAmount + rASContributionsAmount   =>
        (higherTaxRate, basicRateAllowance + grossGiftAidAmount + rASContributionsAmount)
      case _                                                                          => (topTaxRate, topRateAllowance + grossGiftAidAmount + rASContributionsAmount)
    }

  def getGrossGiftAidAmount(netIncome: Int, giftAidAmount: Int): Double =
    if (netIncome > freeAllowance + basicRateAllowance)
      1.25 * giftAidAmount
    else giftAidAmount
}

sealed trait NonScottishTaxRate extends TaxRate

object NonScottishTaxRate {

  case class NonScottishTaxRates(
    personalAllowance: Int = 0,
    freeAllowance: Int = 0,
    basicRateAllowance: Int = 0
  ) extends NonScottishTaxRate

  case class _2016(
    personalAllowance: Int,
    freeAllowance: Int = 10600,
    basicRateAllowance: Int = 31785
  ) extends NonScottishTaxRate

  case class _2017(
    personalAllowance: Int,
    freeAllowance: Int = 11000,
    basicRateAllowance: Int = 32000
  ) extends NonScottishTaxRate

  case class _2018(
    personalAllowance: Int,
    freeAllowance: Int = 11500,
    basicRateAllowance: Int = 33500
  ) extends NonScottishTaxRate

  case class _2019(
    personalAllowance: Int,
    freeAllowance: Int = 11850,
    basicRateAllowance: Int = 34500
  ) extends NonScottishTaxRate

  case class _2020(
    personalAllowance: Int,
    freeAllowance: Int = 12500,
    basicRateAllowance: Int = 37500
  ) extends NonScottishTaxRate

  case class _2021(
    personalAllowance: Int,
    freeAllowance: Int = 12500,
    basicRateAllowance: Int = 37500
  ) extends NonScottishTaxRate

  case class _2022(
    personalAllowance: Int,
    freeAllowance: Int = 12570,
    basicRateAllowance: Int = 37700
  ) extends NonScottishTaxRate

  case class _2023(
    personalAllowance: Int,
    freeAllowance: Int = 12570,
    basicRateAllowance: Int = 37700
  ) extends NonScottishTaxRate

}

sealed trait ScottishTaxRate extends TaxRate

sealed trait ScottishTaxRateTill2018 extends ScottishTaxRate

object ScottishTaxRateTill2018 {

  case class ScottishTaxRatesTill2018(
    personalAllowance: Int = 0,
    freeAllowance: Int = 0,
    basicRateAllowance: Int = 0
  ) extends ScottishTaxRateTill2018

  case class _2016(
    personalAllowance: Int,
    freeAllowance: Int = 10600,
    basicRateAllowance: Int = 31785
  ) extends ScottishTaxRateTill2018

  case class _2017(
    personalAllowance: Int,
    freeAllowance: Int = 11000,
    basicRateAllowance: Int = 31385
  ) extends ScottishTaxRateTill2018

  case class _2018(
    personalAllowance: Int,
    freeAllowance: Int = 11500,
    basicRateAllowance: Int = 31500
  ) extends ScottishTaxRateTill2018
}

sealed trait ScottishTaxRateAfter2018 extends ScottishTaxRate {

  def starterRateAllowance: Int

  def intermediateRateAllowance: Int

  def starterTaxRate: Double = 0.19

  def intermediateTaxRate: Double = 0.21

  override def higherTaxRate: Double = 0.41

  override def topTaxRate: Double = 0.46

  override def getTaxRate(income: Int, grossGiftAidAmount: Int, rASContributionsAmount: Int): (Double, Int) =
    income match {
      case i if i <= starterRateAllowance                                                    => (starterTaxRate, personalAllowance)
      case i if i <= basicRateAllowance + grossGiftAidAmount + rASContributionsAmount        =>
        (basicTaxRate, starterRateAllowance)
      case i if i <= intermediateRateAllowance + grossGiftAidAmount + rASContributionsAmount =>
        (intermediateTaxRate, basicRateAllowance + grossGiftAidAmount + rASContributionsAmount)
      case i if i <= topRateAllowance + grossGiftAidAmount + rASContributionsAmount          =>
        (higherTaxRate, intermediateRateAllowance + grossGiftAidAmount + rASContributionsAmount)
      case _                                                                                 => (topTaxRate, topRateAllowance + grossGiftAidAmount + rASContributionsAmount)
    }
}

object ScottishTaxRateAfter2018 {

  case class ScottishTaxRatesAfter2018(
    personalAllowance: Int,
    freeAllowance: Int = 0,
    starterRateAllowance: Int = 0,
    basicRateAllowance: Int = 0,
    intermediateRateAllowance: Int = 0
  ) extends ScottishTaxRateAfter2018

  case class _2019(
    personalAllowance: Int,
    freeAllowance: Int = 11850,
    starterRateAllowance: Int = 2000,
    basicRateAllowance: Int = 12150,
    intermediateRateAllowance: Int = 31580
  ) extends ScottishTaxRateAfter2018

  case class _2020(
    personalAllowance: Int,
    freeAllowance: Int = 12500,
    starterRateAllowance: Int = 2049,
    basicRateAllowance: Int = 12444,
    intermediateRateAllowance: Int = 30930
  ) extends ScottishTaxRateAfter2018

  case class _2021(
    personalAllowance: Int,
    freeAllowance: Int = 12500,
    starterRateAllowance: Int = 2085,
    basicRateAllowance: Int = 12658,
    intermediateRateAllowance: Int = 30930
  ) extends ScottishTaxRateAfter2018

  case class _2022(
    personalAllowance: Int,
    freeAllowance: Int = 12570,
    starterRateAllowance: Int = 2097,
    basicRateAllowance: Int = 12726,
    intermediateRateAllowance: Int = 31092
  ) extends ScottishTaxRateAfter2018

  case class _2023(
    personalAllowance: Int,
    freeAllowance: Int = 12570,
    starterRateAllowance: Int = 2162,
    basicRateAllowance: Int = 13118,
    intermediateRateAllowance: Int = 31092
  ) extends ScottishTaxRateAfter2018

}
