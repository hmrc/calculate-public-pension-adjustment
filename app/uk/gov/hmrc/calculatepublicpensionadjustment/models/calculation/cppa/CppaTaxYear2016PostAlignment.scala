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

sealed trait CppaTaxYear2016PostAlignment extends CppaTaxYear

object CppaTaxYear2016PostAlignment {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period = Period._2016PostAlignment
  ) extends CppaTaxYear2016PostAlignment

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    flexiAccessDate: LocalDate,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period = Period._2016PostAlignment
  ) extends CppaTaxYear2016PostAlignment

  case class PostFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period = Period._2016PostAlignment
  ) extends CppaTaxYear2016PostAlignment

  implicit lazy val reads: Reads[CppaTaxYear2016PostAlignment] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[CppaTaxYear2016PostAlignment] = ((__ \ "pensionInputAmount").read[Int] and
      (__ \ "totalIncome").read[Int] and
      (__ \ "chargePaidByMember").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]])(
      CppaTaxYear2016PostAlignment.NormalTaxYear(_, _, _, _)
    )

    val initialReads: Reads[CppaTaxYear2016PostAlignment] = ((__ \ "definedBenefitInputAmount").read[Int] and
      (__ \ "flexiAccessDate").read[LocalDate] and
      (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "postAccessDefinedContributionInputAmount").read[Int] and
      (__ \ "totalIncome").read[Int] and
      (__ \ "chargePaidByMember").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]])(
      CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(_, _, _, _, _, _, _)
    )

    val postFlexiblyAccessedReads: Reads[CppaTaxYear2016PostAlignment] =
      ((__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int] and
        (__ \ "totalIncome").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "taxYearSchemes").read[List[TaxYearScheme]])(
        CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(_, _, _, _, _)
      )

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p == Period._2016PostAlignment =>
          Reads(_ => JsSuccess(p))
        case _                                   =>
          Reads(_ => JsError("tax year must be `2016-post`"))
      }
      .andKeep(normalReads orElse initialReads orElse postFlexiblyAccessedReads)

  }

  implicit lazy val writes: Writes[CppaTaxYear2016PostAlignment] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[CppaTaxYear2016PostAlignment.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.totalIncome, a.chargePaidByMember, a.taxYearSchemes, a.period))

    lazy val initialWrites: Writes[CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "flexiAccessDate").write[LocalDate] and
        (__ \ "preAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.flexiAccessDate,
        a.preAccessDefinedContributionInputAmount,
        a.postAccessDefinedContributionInputAmount,
        a.totalIncome,
        a.chargePaidByMember,
        a.taxYearSchemes,
        a.period
      )
    )

    lazy val postWrites: Writes[CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "definedContributionInputAmount").write[Int] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.definedContributionInputAmount,
        a.totalIncome,
        a.chargePaidByMember,
        a.taxYearSchemes,
        a.period
      )
    )

    Writes {
      case year: CppaTaxYear2016PostAlignment.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: CppaTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)

      case year: CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear =>
        Json.toJson(year)(postWrites)
    }
  }

}
