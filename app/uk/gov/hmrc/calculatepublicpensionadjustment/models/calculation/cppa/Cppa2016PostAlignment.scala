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
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Period

import java.util.Date

trait Cppa2016PostAlignment extends TaxYearScheme

object Cppa2016PostAlignment {

  case class NormalTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    pensionInputAmount: Int
  ) extends Cppa2016PostAlignment

  case class InitialFlexiblyAccessedTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    definedBenefitInputAmount: Int,
    flexiAccessDate: Date,
    preAccessDefinedContributionInputAmount: Int,
    postAccessDefinedContributionInputAmount: Int
  ) extends Cppa2016PostAlignment

  case class PostFlexiblyAccessedTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int
  ) extends Cppa2016PostAlignment

  implicit lazy val reads: Reads[Cppa2016PostAlignment] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[Cppa2016PostAlignment] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "pensionInputAmount").read[Int]
    )(Cppa2016PostAlignment.NormalTaxYearScheme)

    val initialFlexiblyAccessedReads: Reads[Cppa2016PostAlignment] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "flexiAccessDate").read[Date] and
        (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").read[Int]
    )(Cppa2016PostAlignment.InitialFlexiblyAccessedTaxYearScheme)

    val postFlexiblyAccessedReads: Reads[Cppa2016PostAlignment] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int]
    )(Cppa2016PostAlignment.PostFlexiblyAccessedTaxYearScheme)

    (__ \ "period")
      .read[Period]
      .flatMap[Period] {
        case p if p == Period._2016PostAlignment =>
          Reads(_ => JsSuccess(p))
        case _                                   =>
          Reads(_ => JsError("tax year must be `2016-post`"))
      }
      .andKeep(normalReads orElse initialFlexiblyAccessedReads orElse postFlexiblyAccessedReads)

  }
}
