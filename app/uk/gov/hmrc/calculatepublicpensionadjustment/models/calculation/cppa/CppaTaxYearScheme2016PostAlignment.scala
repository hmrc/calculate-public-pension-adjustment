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

trait CppaTaxYearScheme2016PostAlignment extends TaxYearScheme

object CppaTaxYearScheme2016PostAlignment {

  case class NormalTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    pensionInputAmount: Int
  ) extends CppaTaxYearScheme2016PostAlignment

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
  ) extends CppaTaxYearScheme2016PostAlignment

  case class PostFlexiblyAccessedTaxYearScheme(
    name: String,
    pstr: String,
    chargePaidByScheme: Int,
    chargePaidByMember: Int,
    oPensionInputAmount: Int,
    definedBenefitInputAmount: Int,
    definedContributionInputAmount: Int
  ) extends CppaTaxYearScheme2016PostAlignment

  implicit lazy val reads: Reads[CppaTaxYearScheme2016PostAlignment] = {

    import play.api.libs.functional.syntax._

    val normalReads: Reads[CppaTaxYearScheme2016PostAlignment] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "pensionInputAmount").read[Int]
    )(CppaTaxYearScheme2016PostAlignment.NormalTaxYearScheme)

    val initialFlexiblyAccessedReads: Reads[CppaTaxYearScheme2016PostAlignment] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "flexiAccessDate").read[Date] and
        (__ \ "preAccessDefinedContributionInputAmount").read[Int] and
        (__ \ "postAccessDefinedContributionInputAmount").read[Int]
    )(CppaTaxYearScheme2016PostAlignment.InitialFlexiblyAccessedTaxYearScheme)

    val postFlexiblyAccessedReads: Reads[CppaTaxYearScheme2016PostAlignment] = (
      (__ \ "name").read[String] and
        (__ \ "pstr").read[String] and
        (__ \ "chargePaidByScheme").read[Int] and
        (__ \ "chargePaidByMember").read[Int] and
        (__ \ "oPensionInputAmount").read[Int] and
        (__ \ "definedBenefitInputAmount").read[Int] and
        (__ \ "definedContributionInputAmount").read[Int]
    )(CppaTaxYearScheme2016PostAlignment.PostFlexiblyAccessedTaxYearScheme)

    normalReads orElse initialFlexiblyAccessedReads orElse postFlexiblyAccessedReads
  }
}
