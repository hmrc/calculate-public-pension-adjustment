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

import play.api.libs.json.{Json, Reads, Writes}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{Period, TaxYear}
import scala.language.implicitConversions

trait PaacTaxYear extends TaxYear {
  def period: Period
}

object PaacTaxYear {

  implicit lazy val reads: Reads[PaacTaxYear] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    PaacTaxYear2011To2015.reads or
      PaacTaxYear2016PreAlignment.reads or
      PaacTaxYear2016PostAlignment.reads or
      PaacTaxYear2017ToCurrent.reads
  }

  implicit lazy val writes: Writes[PaacTaxYear] = Writes {
    case year: PaacTaxYear2011To2015        => Json.toJson(year)(PaacTaxYear2011To2015.writes)
    case year: PaacTaxYear2016PreAlignment  => Json.toJson(year)(PaacTaxYear2016PreAlignment.writes)
    case year: PaacTaxYear2016PostAlignment => Json.toJson(year)(PaacTaxYear2016PostAlignment.writes)
    case year: PaacTaxYear2017ToCurrent     => Json.toJson(year)(PaacTaxYear2017ToCurrent.writes)
    case _                                  => throw new Exception("Tax year period is invalid")
  }
}
