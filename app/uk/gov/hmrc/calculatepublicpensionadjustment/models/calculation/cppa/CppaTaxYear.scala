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

package uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa

import play.api.libs.json.{Json, Reads, Writes}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{Period, TaxYear}

import scala.language.implicitConversions

trait CppaTaxYear extends TaxYear {
  def period: Period
}

object CppaTaxYear {

  implicit lazy val reads: Reads[CppaTaxYear] = {

    implicit class ReadsWithContravariantOr[A](a: Reads[A]) {

      def or[B >: A](b: Reads[B]): Reads[B] =
        a.map[B](identity).orElse(b)
    }

    implicit def convertToSupertype[A, B >: A](a: Reads[A]): Reads[B] =
      a.map(identity)

    CppaTaxYear2013To2015.reads or
      CppaTaxYear2016PreAlignment.reads or
      CppaTaxYear2016PostAlignment.reads or
      CppaTaxYear2017ToCurrent.reads
  }

  implicit lazy val writes: Writes[CppaTaxYear] = Writes {
    case year: CppaTaxYear2013To2015        => Json.toJson(year)(CppaTaxYear2013To2015.writes)
    case year: CppaTaxYear2016PreAlignment  => Json.toJson(year)(CppaTaxYear2016PreAlignment.writes)
    case year: CppaTaxYear2016PostAlignment => Json.toJson(year)(CppaTaxYear2016PostAlignment.writes)
    case year: CppaTaxYear2017ToCurrent     => Json.toJson(year)(CppaTaxYear2017ToCurrent.writes)
    case _                                  => throw new Exception("Tax year period is invalid")
  }
}
