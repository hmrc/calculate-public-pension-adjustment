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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac

import play.api.libs.json._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{Income, Period}

sealed trait PaacTaxYear2017ToCurrent extends PaacTaxYear

object PaacTaxYear2017ToCurrent {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    income: Income,
    period: Period
  ) extends PaacTaxYear2017ToCurrent

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    income: Income,
    period: Period
  ) extends PaacTaxYear2017ToCurrent

  case class PostFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int,
    income: Income,
    period: Period
  ) extends PaacTaxYear2017ToCurrent

  case class NoInputTaxYear(period: Period) extends PaacTaxYear2017ToCurrent

  implicit lazy val reads: Reads[PaacTaxYear2017ToCurrent] = {

    import play.api.libs.functional.syntax._

    import Ordering.Implicits._

    val normalReads: Reads[PaacTaxYear2017ToCurrent] = (
      (__ \ "pensionInputAmount").read[Int] and
        (__ \ "income").read[Income] and
        (__ \ "period").read[Period]
    )(PaacTaxYear2017ToCurrent.NormalTaxYear)

    val initialReads: Reads[PaacTaxYear2017ToCurrent] = (
      (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "income").read[Income] and
        (__ \ "period").read[Period]
    )(PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear)

    val postReads: Reads[PaacTaxYear2017ToCurrent] = (
      (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int] and
        (__ \ "income").read[Income] and
        (__ \ "period").read[Period]
    )(PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear)

    val noInputReads: Reads[PaacTaxYear2017ToCurrent] =
      (__ \ "period").read[Period].map(PaacTaxYear2017ToCurrent.NoInputTaxYear)

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p >= Period._2017 =>
          Reads(_ => JsSuccess(p))
        case _                      =>
          Reads(_ => JsError("tax year must be `2017` or later"))
      }
      .andKeep(normalReads orElse initialReads orElse postReads orElse noInputReads)
  }

  implicit lazy val writes: Writes[PaacTaxYear2017ToCurrent] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[PaacTaxYear2017ToCurrent.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "income").write[Income] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.income, a.period))

    lazy val initialWrites: Writes[PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "preAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "income").write[Income] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.preAccessDefinedContributionInputAmount,
        a.postAccessDefinedContributionInputAmount,
        a.income,
        a.period
      )
    )

    lazy val postWrites: Writes[PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "definedContributionInputAmount").write[Int] and
        (__ \ "income").write[Income] and
        (__ \ "period").write[Period]
    )(a => (a.definedBenefitInputAmount, a.definedContributionInputAmount, a.income, a.period))

    lazy val noInputWrites: Writes[PaacTaxYear2017ToCurrent.NoInputTaxYear] =
      (__ \ "period").write[Period].contramap(_.period)

    Writes {
      case year: PaacTaxYear2017ToCurrent.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)

      case year: PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear =>
        Json.toJson(year)(postWrites)

      case year: PaacTaxYear2017ToCurrent.NoInputTaxYear =>
        Json.toJson(year)(noInputWrites)
    }
  }

}
