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

sealed trait PaacTaxYear2011To2015 extends PaacTaxYear

object PaacTaxYear2011To2015 {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    period: Period
  ) extends PaacTaxYear2011To2015

  case class NoInputTaxYear(period: Period) extends PaacTaxYear2011To2015

  implicit lazy val reads: Reads[PaacTaxYear2011To2015] = {

    import play.api.libs.functional.syntax._

    import Ordering.Implicits._

    val normalReads: Reads[PaacTaxYear2011To2015] = (
      (__ \ "pensionInputAmount").read[Int] and
        (__ \ "period").read[Period]
    )(PaacTaxYear2011To2015.NormalTaxYear)

    val noInputReads: Reads[PaacTaxYear2011To2015] =
      (__ \ "period").read[Period].map(PaacTaxYear2011To2015.NoInputTaxYear)

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p <= Period._2015 =>
          Reads(_ => JsSuccess(p))
        case _                      =>
          Reads(_ => JsError("taxYear must fall between `2011`-`2015`"))
      } andKeep (normalReads orElse noInputReads)
  }

  implicit lazy val writes: Writes[PaacTaxYear2011To2015] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[PaacTaxYear2011To2015.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.period))

    lazy val noInputWrites: Writes[PaacTaxYear2011To2015.NoInputTaxYear] =
      (__ \ "period").write[Period].contramap(_.period)

    Writes {
      case year: PaacTaxYear2011To2015.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: PaacTaxYear2011To2015.NoInputTaxYear =>
        Json.toJson(year)(noInputWrites)
    }
  }

}
