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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac

import play.api.libs.json._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

sealed trait PaacTaxYear2016PostAlignment extends PaacTaxYear

object PaacTaxYear2016PostAlignment {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    period: Period = Period._2016PostAlignment
  ) extends PaacTaxYear2016PostAlignment

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    period: Period = Period._2016PostAlignment
  ) extends PaacTaxYear2016PostAlignment

  case class PostFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int,
    period: Period = Period._2016PostAlignment
  ) extends PaacTaxYear2016PostAlignment

  case class NoInputTaxYear(period: Period) extends PaacTaxYear2016PostAlignment

  implicit lazy val reads: Reads[PaacTaxYear2016PostAlignment] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[PaacTaxYear2016PostAlignment] =
      (__ \ "pensionInputAmount")
        .read[Int]
        .map(NormalTaxYear(_))

    val initialReads: Reads[PaacTaxYear2016PostAlignment] = (
      (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").read[Int]
    )(PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(_, _, _))

    val postFlexiblyAccessedReads: Reads[PaacTaxYear2016PostAlignment] = (
      (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int]
    )(PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(_, _))

    val noInputReads: Reads[PaacTaxYear2016PostAlignment] =
      (__ \ "period").read[Period].map(PaacTaxYear2016PostAlignment.NoInputTaxYear)

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p == Period._2016PostAlignment =>
          Reads(_ => JsSuccess(p))
        case _                                   =>
          Reads(_ => JsError("tax year must be `2016-post`"))
      }
      .andKeep(normalReads orElse initialReads orElse postFlexiblyAccessedReads orElse noInputReads)
  }

  implicit lazy val writes: Writes[PaacTaxYear2016PostAlignment] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[PaacTaxYear2016PostAlignment.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.period))

    lazy val initialWrites: Writes[PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "preAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.preAccessDefinedContributionInputAmount,
        a.postAccessDefinedContributionInputAmount,
        a.period
      )
    )

    lazy val postWrites: Writes[PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "definedContributionInputAmount").write[Int] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.definedContributionInputAmount,
        a.period
      )
    )

    lazy val noInputWrites: Writes[PaacTaxYear2016PostAlignment.NoInputTaxYear] =
      (__ \ "period").write[Period].contramap(_.period)

    Writes {
      case year: PaacTaxYear2016PostAlignment.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)

      case year: PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear =>
        Json.toJson(year)(postWrites)

      case year: PaacTaxYear2016PostAlignment.NoInputTaxYear =>
        Json.toJson(year)(noInputWrites)
    }
  }

}
