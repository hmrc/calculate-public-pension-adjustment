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

  def freeAllowance: Int

  def basicRateAllowance: Int

  def topRateAllowance: Int = 150000

  def basicTaxRate: Double = 0.20

  def higherTaxRate: Double = 0.40

  def topTaxRate: Double = 0.45

  def getTaxRate(income: Int): (Double, Int) =
    income match {
      case i if i <= freeAllowance      => (0.00, 0)
      case i if i <= basicRateAllowance => (basicTaxRate, freeAllowance)
      case i if i <= topRateAllowance   => (higherTaxRate, basicRateAllowance)
      case _                            => (topTaxRate, topRateAllowance)
    }
}

sealed trait NonScottishTaxRate extends TaxRate

object NonScottishTaxRate {

  case class NonScottishTaxRates(
    freeAllowance: Int = 0,
    basicRateAllowance: Int = 0
  ) extends NonScottishTaxRate

  case class _2016(
    freeAllowance: Int = 10600,
    basicRateAllowance: Int = 42385
  ) extends NonScottishTaxRate

  case class _2017(
    freeAllowance: Int = 11000,
    basicRateAllowance: Int = 43000
  ) extends NonScottishTaxRate

  case class _2018(
    freeAllowance: Int = 11500,
    basicRateAllowance: Int = 45000
  ) extends NonScottishTaxRate

  case class _2019(
    freeAllowance: Int = 11850,
    basicRateAllowance: Int = 46350
  ) extends NonScottishTaxRate

  case class _2020To2021(
    freeAllowance: Int = 12500,
    basicRateAllowance: Int = 50000
  ) extends NonScottishTaxRate

  case class _2022To2023(
    freeAllowance: Int = 12570,
    basicRateAllowance: Int = 50270
  ) extends NonScottishTaxRate

}

sealed trait ScottishTaxRate extends TaxRate

sealed trait ScottishTaxRateTill2018 extends ScottishTaxRate

object ScottishTaxRateTill2018 {

  case class ScottishTaxRatesTill2018(
    freeAllowance: Int = 0,
    basicRateAllowance: Int = 0
  ) extends ScottishTaxRateTill2018

  case class _2016(
    freeAllowance: Int = 10600,
    basicRateAllowance: Int = 42385
  ) extends ScottishTaxRateTill2018

  case class _2017(
    freeAllowance: Int = 11000,
    basicRateAllowance: Int = 42385
  ) extends ScottishTaxRateTill2018

  case class _2018(
    freeAllowance: Int = 11500,
    basicRateAllowance: Int = 43000
  ) extends ScottishTaxRateTill2018
}

sealed trait ScottishTaxRateAfter2018 extends ScottishTaxRate {

  def starterRateAllowance: Int

  def intermediateRateAllowance: Int

  def starterTaxRate: Double = 0.19

  def intermediateTaxRate: Double = 0.21

  override def higherTaxRate: Double = 0.41

  override def topTaxRate: Double = 0.46

  override def getTaxRate(income: Int): (Double, Int) =
    income match {
      case i if i <= freeAllowance             => (0.00, 0)
      case i if i <= starterRateAllowance      => (starterTaxRate, freeAllowance)
      case i if i <= basicRateAllowance        => (basicTaxRate, starterRateAllowance)
      case i if i <= intermediateRateAllowance => (intermediateTaxRate, basicRateAllowance)
      case i if i <= topRateAllowance          => (higherTaxRate, intermediateRateAllowance)
      case _                                   => (topTaxRate, topRateAllowance)
    }
}

object ScottishTaxRateAfter2018 {

  case class ScottishTaxRatesAfter2018(
    freeAllowance: Int = 0,
    starterRateAllowance: Int = 0,
    basicRateAllowance: Int = 0,
    intermediateRateAllowance: Int = 0
  ) extends ScottishTaxRateAfter2018

  case class _2019(
    freeAllowance: Int = 11850,
    starterRateAllowance: Int = 13850,
    basicRateAllowance: Int = 24000,
    intermediateRateAllowance: Int = 43430
  ) extends ScottishTaxRateAfter2018

  case class _2020(
    freeAllowance: Int = 12500,
    starterRateAllowance: Int = 14549,
    basicRateAllowance: Int = 24944,
    intermediateRateAllowance: Int = 43430
  ) extends ScottishTaxRateAfter2018

  case class _2021(
    freeAllowance: Int = 12500,
    starterRateAllowance: Int = 14585,
    basicRateAllowance: Int = 25158,
    intermediateRateAllowance: Int = 43430
  ) extends ScottishTaxRateAfter2018

  case class _2022(
    freeAllowance: Int = 12570,
    starterRateAllowance: Int = 14667,
    basicRateAllowance: Int = 25296,
    intermediateRateAllowance: Int = 43662
  ) extends ScottishTaxRateAfter2018

  case class _2023(
    freeAllowance: Int = 12570,
    starterRateAllowance: Int = 14732,
    basicRateAllowance: Int = 25688,
    intermediateRateAllowance: Int = 43662
  ) extends ScottishTaxRateAfter2018

}
