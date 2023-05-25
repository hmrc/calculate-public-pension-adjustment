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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{Income, Period}

import java.time.LocalDate

sealed trait CppaTaxYear2017ToCurrent extends CppaTaxYear

object CppaTaxYear2017ToCurrent {

  case class NormalTaxYear(
    pensionInputAmount: Int,
    income: Income,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period
  ) extends CppaTaxYear2017ToCurrent

  case class InitialFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    flexiAccessDate: LocalDate,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    income: Income,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period
  ) extends CppaTaxYear2017ToCurrent

  case class PostFlexiblyAccessedTaxYear(
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int,
    income: Income,
    totalIncome: Int,
    chargePaidByMember: Int,
    taxYearSchemes: List[TaxYearScheme],
    period: Period
  ) extends CppaTaxYear2017ToCurrent

  implicit lazy val reads: Reads[CppaTaxYear2017ToCurrent] = {

    import play.api.libs.functional.syntax._

    import Ordering.Implicits._

    val normalReads: Reads[CppaTaxYear2017ToCurrent] = ((__ \ "pensionInputAmount").read[Int] and
      (__ \ "income").read[Income] and
      (__ \ "totalIncome").read[Int] and
      (__ \ "chargePaidByMember").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]] and
      (__ \ "period").read[Period])(
      CppaTaxYear2017ToCurrent.NormalTaxYear
    )

    val initialFlexiblyAccessedReads: Reads[CppaTaxYear2017ToCurrent] =
      ((__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "flexiAccessDate").read[LocalDate] and
        (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "income").read[Income] and
        (__ \ "totalIncome").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "taxYearSchemes").read[List[TaxYearScheme]] and
        (__ \ "period").read[Period])(
        CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear
      )

    val postFlexiblyAccessedReads: Reads[CppaTaxYear2017ToCurrent] = ((__ \ "definedBenefitInputAmount").read[Int] and
      (__ \ "definedContributionInputAmount").read[Int] and
      (__ \ "income").read[Income] and
      (__ \ "totalIncome").read[Int] and
      (__ \ "chargePaidByMember").read[Int] and
      (__ \ "taxYearSchemes").read[List[TaxYearScheme]] and
      (__ \ "period").read[Period])(
      CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear
    )

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p >= Period._2017 =>
          Reads(_ => JsSuccess(p))
        case _                      =>
          Reads(_ => JsError("tax year must be `2017` or later"))
      }
      .andKeep(normalReads orElse initialFlexiblyAccessedReads orElse postFlexiblyAccessedReads)

  }

  lazy val writes: Writes[CppaTaxYear2017ToCurrent] = {

    import play.api.libs.functional.syntax._

    lazy val normalWrites: Writes[CppaTaxYear2017ToCurrent.NormalTaxYear] = (
      (__ \ "pensionInputAmount").write[Int] and
        (__ \ "income").write[Income] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "period").write[Period]
    )(a => (a.pensionInputAmount, a.income, a.totalIncome, a.chargePaidByMember, a.taxYearSchemes, a.period))

    lazy val initialWrites: Writes[CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "flexiAccessDate").write[LocalDate] and
        (__ \ "preAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").write[Int] and
        (__ \ "income").write[Income] and
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
        a.income,
        a.totalIncome,
        a.chargePaidByMember,
        a.taxYearSchemes,
        a.period
      )
    )

    lazy val postWrites: Writes[CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear] = (
      (__ \ "definedBenefitInputAmount").write[Int] and
        (__ \ "definedContributionInputAmount").write[Int] and
        (__ \ "income").write[Income] and
        (__ \ "totalIncome").write[Int] and
        (__ \ "chargePaidByMember").write[Int] and
        (__ \ "taxYearSchemes").write[List[TaxYearScheme]] and
        (__ \ "period").write[Period]
    )(a =>
      (
        a.definedBenefitInputAmount,
        a.definedContributionInputAmount,
        a.income,
        a.totalIncome,
        a.chargePaidByMember,
        a.taxYearSchemes,
        a.period
      )
    )

    Writes {
      case year: CppaTaxYear2017ToCurrent.NormalTaxYear =>
        Json.toJson(year)(normalWrites)

      case year: CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear =>
        Json.toJson(year)(initialWrites)

      case year: CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear =>
        Json.toJson(year)(postWrites)
    }

  }
}
