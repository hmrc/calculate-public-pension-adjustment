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

import play.api.libs.json.{JsError, JsSuccess, Reads, __}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{Income, Period}

import java.util.Date

trait Cppa2017ToCurrent extends TaxYearScheme

object Cppa2017ToCurrent {

  case class NormalTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    pensionInputAmount: Int,
    income: Income
  ) extends Cppa2017ToCurrent

  case class InitialFlexiblyAccessedTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    definedBenefitInputAmount: Int,
    flexiAccessDate: Date,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int,
    income: Income
  ) extends Cppa2017ToCurrent

  case class PostFlexiblyAccessedTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int,
    income: Income
  ) extends Cppa2017ToCurrent

  implicit lazy val reads: Reads[Cppa2017ToCurrent] = {

    import play.api.libs.functional.syntax._
    import Ordering.Implicits._

    val normalReads: Reads[Cppa2017ToCurrent] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "pensionInputAmount").read[Int] and
        (__ \ "income").read[Income]
    )(Cppa2017ToCurrent.NormalTaxYearScheme)

    val initialFlexiblyAccessedReads: Reads[Cppa2017ToCurrent] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "flexiAccessDate").read[Date] and
        (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "income").read[Income]
    )(Cppa2017ToCurrent.InitialFlexiblyAccessedTaxYearScheme)

    val postFlexiblyAccessedReads: Reads[Cppa2017ToCurrent] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int] and
        (__ \ "income").read[Income]
    )(Cppa2017ToCurrent.PostFlexiblyAccessedTaxYearScheme)

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

}
