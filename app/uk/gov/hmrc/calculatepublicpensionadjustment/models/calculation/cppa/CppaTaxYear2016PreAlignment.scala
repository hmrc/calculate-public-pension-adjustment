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

import play.api.libs.json._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

import java.time.LocalDate

sealed trait CppaTaxYear2016PreAlignment extends CppaTaxYear

object CppaTaxYear2016PreAlignment {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    taxYearSchemes: List[TaxYearScheme],
    totalIncome: Int = 0,
    chargePaidByMember: Int = 0,
    period: Period = Period._2016PreAlignment
  ) extends CppaTaxYear2016PreAlignment

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    flexiAccessDate: LocalDate,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    taxYearSchemes: List[TaxYearScheme],
    totalIncome: Int = 0,
    chargePaidByMember: Int = 0,
    period: Period = Period._2016PreAlignment
  ) extends CppaTaxYear2016PreAlignment

  implicit lazy val reads: Reads[CppaTaxYear2016PreAlignment] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[CppaTaxYear2016PreAlignment] = ((__ \ "pensionInputAmount").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]])(
      CppaTaxYear2016PreAlignment.NormalTaxYear(_, _)
    )

    val initialReads: Reads[CppaTaxYear2016PreAlignment] = ((__ \ "definedBenefitInputAmount").read[Int] and
      (__ \ "flexiAccessDate").read[LocalDate] and
      (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "postAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]])(
      CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(_, _, _, _, _)
    )

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p == Period._2016PreAlignment =>
          Reads(_ => JsSuccess(p))
        case _                                  =>
          Reads(_ => JsError("tax year must be `2016-pre`"))
      }
      .andKeep(normalReads orElse initialReads)
  }

  lazy val writes: Writes[CppaTaxYear2016PreAlignment] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[CppaTaxYear2016PreAlignment.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.taxYearSchemes, a.totalIncome, a.chargePaidByMember, a.period))

    lazy val initialWrites: Writes[CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "flexiAccessDate").write[LocalDate] and
        (__ \ "preAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.flexiAccessDate,
        a.preAccessDefinedContributionInputAmount,
        a.postAccessDefinedContributionInputAmount,
        a.taxYearSchemes,
        a.totalIncome,
        a.chargePaidByMember,
        a.period
      )
    )

    Writes {
      case year: CppaTaxYear2016PreAlignment.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)
    }
  }
}
