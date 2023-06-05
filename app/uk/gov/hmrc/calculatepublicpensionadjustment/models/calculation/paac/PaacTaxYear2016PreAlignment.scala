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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

sealed trait PaacTaxYear2016PreAlignment extends PaacTaxYear

object PaacTaxYear2016PreAlignment {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    period: Period = Period._2016PreAlignment
  ) extends PaacTaxYear2016PreAlignment

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    period: Period = Period._2016PreAlignment
  ) extends PaacTaxYear2016PreAlignment

  case class NoInputTaxYear(period: Period) extends PaacTaxYear2016PreAlignment

  implicit lazy val reads: Reads[PaacTaxYear2016PreAlignment] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[PaacTaxYear2016PreAlignment] =
      (__ \ "pensionInputAmount")
        .read[Int]
        .map(PaacTaxYear2016PreAlignment.NormalTaxYear(_))

    val initialReads: Reads[PaacTaxYear2016PreAlignment] = ((__ \ "definedBenefitInputAmount").read[Int] and
      (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "postAccessDefinedContributionInputAmount").read[Int])(
      PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(_, _, _)
    )

    val noInputReads: Reads[PaacTaxYear2016PreAlignment] =
      (__ \ "period").read[Period].map(PaacTaxYear2016PreAlignment.NoInputTaxYear)

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p == Period._2016PreAlignment =>
          Reads(_ => JsSuccess(p))
        case _                                  =>
          Reads(_ => JsError("tax year must be `2016-pre`"))
      }
      .andKeep(normalReads orElse initialReads orElse noInputReads)
  }

  implicit lazy val writes: Writes[PaacTaxYear2016PreAlignment] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[PaacTaxYear2016PreAlignment.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.period))

    lazy val initialWrites: Writes[PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear] = (
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

    lazy val noInputWrites: Writes[PaacTaxYear2016PreAlignment.NoInputTaxYear] =
      (__ \ "period").write[Period].contramap(_.period)

    Writes {
      case year: PaacTaxYear2016PreAlignment.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)

      case year: PaacTaxYear2016PreAlignment.NoInputTaxYear =>
        Json.toJson(year)(noInputWrites)
    }
  }

}
