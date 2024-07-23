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

package uk.gov.hmrc.calculatepublicpensionadjustment.services

import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import requests.CalculationResponses
import uk.gov.hmrc.calculatepublicpensionadjustment.connectors.PaacConnector
import uk.gov.hmrc.calculatepublicpensionadjustment.models.IncomeSubJourney
import uk.gov.hmrc.calculatepublicpensionadjustment.models.PayeCodeAdjustment.Increase
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Income.{AboveThreshold, BelowThreshold}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac._
import uk.gov.hmrc.http.{HeaderCarrier, ServiceUnavailableException}

import java.time.LocalDate
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PaacServiceSpec
    extends AnyFreeSpec
    with Matchers
    with ScalaFutures
    with OptionValues
    with MockitoSugar
    with BeforeAndAfterEach
    with CalculationResponses {

  private val mockPaacConnector = mock[PaacConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockPaacConnector)
  }

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new PaacService(mockPaacConnector)

  "PaacService" - {

    val validCalculationRequestWithAllYears = CalculationRequest(
      Resubmission(isResubmission = true, Some("Change in amounts")),
      List(Period._2017, Period._2019, Period._2021),
      List(
        CppaTaxYear2011To2015(9000, Period._2011),
        CppaTaxYear2011To2015(10000, Period._2013),
        CppaTaxYear2011To2015(11000, Period._2014),
        CppaTaxYear2011To2015(12000, Period._2015),
        CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
          16000,
          Some(LocalDate.parse("2015-05-25")),
          6000,
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
          ),
          80000,
          0,
          Period._2016,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            Some(888),
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            Some(2291)
          ),
          None,
          Some(16000),
          Some(20000),
          None
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          12000,
          19000,
          100000,
          0,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 9000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 9000, None)
          ),
          Period._2017,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(21000))
        ),
        CppaTaxYear2016To2023.NormalTaxYear(
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 9000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 9000, None)
          ),
          100000,
          0,
          Period._2018,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          23000,
          22000,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 3000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 0, None)
          ),
          Period._2019,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        ),
        CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
          23000,
          Some(LocalDate.parse("2019-12-22")),
          6000,
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 6000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 0, None)
          ),
          90000,
          3000,
          Period._2020,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(BelowThreshold)
        ),
        CppaTaxYear2016To2023.NormalTaxYear(
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 2000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
          ),
          90000,
          8000,
          Period._2021,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          23000,
          22000,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 3000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 6000, None)
          ),
          Period._2022,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        ),
        CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
          23000,
          Some(LocalDate.parse("2023-02-21")),
          6000,
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 6000, None)
          ),
          90000,
          4000,
          Period._2023,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        )
      )
    )

    val validCalculationRequestWith2020InitialFlexiblyAccessedTaxYear = {

      val uTaxYears = validCalculationRequestWithAllYears.taxYears
        .filterNot(v => v.period == Period._2016) ++ List(
        CppaTaxYear2016To2023.NormalTaxYear(
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 9000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 9000, None)
          ),
          100000,
          0,
          Period._2016,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            Some(888),
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            Some(2291)
          ),
          None,
          Some(10000)
        )
      )

      validCalculationRequestWithAllYears.copy(taxYears = uTaxYears.sortBy(_.period))
    }

    val validCalculationRequestWith2016PostAlignmentInitialFlexiblyAccessedTaxYear = {

      val uTaxYears = validCalculationRequestWithAllYears.taxYears
        .filterNot(v => v.period == Period._2016) ++ List(
        CppaTaxYear2016To2023.InitialFlexiblyAccessedTaxYear(
          10000,
          Some(LocalDate.parse("2016-02-21")),
          0,
          0,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 9000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 9000, None)
          ),
          100000,
          0,
          Period._2016,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            Some(888),
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            Some(2291)
          ),
          None,
          Some(23000)
        )
      )

      validCalculationRequestWithAllYears.copy(taxYears = uTaxYears.sortBy(_.period))
    }

    val validCalculationRequestWithNoInitialFlexiblyAccessedTaxYear = {

      val uTaxYears = validCalculationRequestWithAllYears.taxYears.filterNot(v =>
        v.period == Period._2016 | v.period == Period._2020 | v.period == Period._2023
      ) ++ List(
        CppaTaxYear2016To2023.NormalTaxYear(
          6000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 9000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 9000, None)
          ),
          100000,
          4000,
          Period._2016,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            Some(888),
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            Some(2291)
          )
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          12000,
          10000,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 3000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 6000, None)
          ),
          Period._2020,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(BelowThreshold)
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          8000,
          6000,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 3000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 6000, None)
          ),
          Period._2023,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        )
      )

      validCalculationRequestWithAllYears.copy(taxYears = uTaxYears.sortBy(_.period))
    }

    val validCalculationRequestWithNoInitialFlexiblyAccessedTaxYear2 = {

      val uTaxYears = validCalculationRequestWithAllYears.taxYears.filterNot(v =>
        v.period == Period._2016 | v.period == Period._2020 | v.period == Period._2023
      ) ++ List(
        CppaTaxYear2016To2023.NormalTaxYear(
          6000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 9000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 9000, None)
          ),
          100000,
          4000,
          Period._2016,
          IncomeSubJourney(
            None,
            None,
            None,
            None,
            None,
            Some(888),
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            None,
            Some(2291)
          ),
          None,
          Some(0)
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          12000,
          10000,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 3000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 6000, None)
          ),
          Period._2020,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(BelowThreshold)
        ),
        CppaTaxYear2016To2023.PostFlexiblyAccessedTaxYear(
          8000,
          6000,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 10000, 3000, None),
            TaxYearScheme("Scheme 2", "pstrTest2", 10000, 6000, None)
          ),
          Period._2023,
          IncomeSubJourney(
            Some(444),
            Some(666),
            Some(712),
            Some(777),
            Some(true),
            Some(888),
            None,
            Some(1111),
            Some(1212),
            Some(1414),
            Some(842),
            Some(Increase),
            Some(2740),
            None,
            Some(2291)
          ),
          Some(AboveThreshold(24000))
        )
      )

      validCalculationRequestWithAllYears.copy(taxYears = uTaxYears.sortBy(_.period))
    }

    val validPaacRequestWithAllYears = PaacRequest(
      List(
        PaacTaxYear2011To2015.NormalTaxYear(9000, Period._2011),
        PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013),
        PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014),
        PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015),
        PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
        PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(16000, 20000, Period._2016PostAlignment),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2021),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023)
      ),
      Period._2023
    )

    val validPaacRequestWith2020InitialFlexiblyAccessedTaxYear = PaacRequest(
      List(
        PaacTaxYear2011To2015.NormalTaxYear(9000, Period._2011),
        PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013),
        PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014),
        PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015),
        PaacTaxYear2016PreAlignment.NormalTaxYear(10000, Period._2016PreAlignment),
        PaacTaxYear2016PostAlignment.NormalTaxYear(10000, Period._2016PostAlignment),
        PaacTaxYear2017ToCurrent.NormalTaxYear(51000, AboveThreshold(21000), Period._2017),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018),
        PaacTaxYear2017ToCurrent.NormalTaxYear(65000, AboveThreshold(24000), Period._2019),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2021),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023)
      ),
      Period._2023
    )

    val validPaacRequestWith2016PostAlignmentInitialFlexiblyAccessedTaxYear = PaacRequest(
      List(
        PaacTaxYear2011To2015.NormalTaxYear(9000, Period._2011),
        PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013),
        PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014),
        PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015),
        PaacTaxYear2016PreAlignment.NormalTaxYear(30000, Period._2016PreAlignment),
        PaacTaxYear2016PostAlignment.InitialFlexiblyAccessedTaxYear(23000, 0, 0, Period._2016PostAlignment),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2021),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023)
      ),
      Period._2023
    )

    val validPaacRequestWithNoInitialFlexiblyAccessedTaxYear = PaacRequest(
      List(
        PaacTaxYear2011To2015.NormalTaxYear(9000, Period._2011),
        PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013),
        PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014),
        PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015),
        PaacTaxYear2016PreAlignment.NormalTaxYear(6000, Period._2016PreAlignment),
        PaacTaxYear2017ToCurrent.NormalTaxYear(51000, AboveThreshold(21000), Period._2017),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018),
        PaacTaxYear2017ToCurrent.NormalTaxYear(65000, AboveThreshold(24000), Period._2019),
        PaacTaxYear2017ToCurrent.NormalTaxYear(42000, BelowThreshold, Period._2020),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2021),
        PaacTaxYear2017ToCurrent.NormalTaxYear(65000, AboveThreshold(24000), Period._2022),
        PaacTaxYear2017ToCurrent.NormalTaxYear(34000, AboveThreshold(24000), Period._2023)
      ),
      Period._2023
    )

    val validPaacResponseWithAllYears = PaacResponse(
      List(
        PaacResponseRow(
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
          16000,
          40000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          18000,
          60000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0, 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
          16000,
          0
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          18000,
          30000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2018), 0, 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          9000,
          40000
        ),
        PaacResponseRow(
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(36000, 20000, Period._2016PostAlignment),
          10000,
          0
        ),
        PaacResponseRow(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
          0,
          20000
        ),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014), 0, 20000),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0, 30000),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2012), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2011), 0, 0)
      )
    )

    val validPaacResponseWithAllYearsAfterFilter = PaacResponse(
      List(
        PaacResponseRow(
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
          16000,
          40000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          18000,
          60000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0, 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
          16000,
          0
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          18000,
          30000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2018), 0, 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          9000,
          40000
        ),
        PaacResponseRow(
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(36000, 20000, Period._2016PostAlignment),
          10000,
          0
        ),
        PaacResponseRow(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
          0,
          20000
        ),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014), 0, 20000),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0, 30000)
      )
    )

    val validCalculationRequestWithMissingYears =
      CalculationRequest(
        validCalculationRequestWithAllYears.resubmission,
        validCalculationRequestWithAllYears.scottishTaxYears,
        validCalculationRequestWithAllYears.taxYears.filterNot(p =>
          p.period == Period._2014 | p.period == Period._2018 | p.period == Period._2020
        )
      )

    val validPaacRequestWithMissingYears = PaacRequest(
      validPaacRequestWithAllYears.taxYears.filterNot(p =>
        p.period == Period._2014 | p.period == Period._2018 | p.period == Period._2020
      ),
      validPaacRequestWithAllYears.until
    )

    val validPaacResponseWithMissingYears = PaacResponse(
      List(
        PaacResponseRow(
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
          16000,
          20000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          18000,
          30000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0, 20000),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NoInputTaxYear(Period._2020), 0, 10000),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          18000,
          10000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NoInputTaxYear(Period._2018), 0, 20000),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          9000,
          0
        ),
        PaacResponseRow(PaacTaxYear2016PostAlignment.NoInputTaxYear(Period._2016PostAlignment), 0, 10000),
        PaacResponseRow(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(16000, 6000, 10000, Period._2016PreAlignment),
          0,
          10000
        ),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2014), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2012), 0, 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2011), 0, 0)
      )
    )

    val validPaacResponseWithMissingYearsAfterFilter = PaacResponse(
      validPaacResponseWithMissingYears.rows.filterNot(r =>
        r.taxYear.period == Period._2011 | r.taxYear.period == Period._2012 | r.taxYear.period == Period._2014
          | r.taxYear.period == Period._2016PostAlignment | r.taxYear.period == Period._2018 | r.taxYear.period == Period._2020
      )
    )

    "sendRequest" - {

      "must receive a valid PaacResponse from PAAC for all taxyears and filter out unwanted years successfully" in {

        when(mockPaacConnector.sendRequest(validPaacRequestWithAllYears)(hc))
          .thenReturn(Future.successful(validPaacResponseWithAllYears))

        val result = service.sendRequest(validCalculationRequestWithAllYears)(hc).futureValue

        result mustEqual validPaacResponseWithAllYearsAfterFilter
      }

      "must receive a valid PaacResponse from PAAC for some missing taxyears and filter out unwanted years successfully" in {

        when(mockPaacConnector.sendRequest(validPaacRequestWithMissingYears)(hc))
          .thenReturn(Future.successful(validPaacResponseWithMissingYears))
        val result = service.sendRequest(validCalculationRequestWithMissingYears)(hc).futureValue

        result mustEqual validPaacResponseWithMissingYearsAfterFilter
      }

      "must fail if the PAAC service is unavailable" in {

        when(mockPaacConnector.sendRequest(validPaacRequestWithAllYears)(hc))
          .thenReturn(Future.failed(new ServiceUnavailableException("")))

        service.sendRequest(validCalculationRequestWithAllYears)(hc).failed.futureValue
      }

      "must fail if the PAAC service returns RuntimeException" in {

        when(mockPaacConnector.sendRequest(validPaacRequestWithAllYears)(hc))
          .thenReturn(Future.failed(new RuntimeException()))

        service.sendRequest(validCalculationRequestWithAllYears)(hc).failed.futureValue
      }

    }

    "buildPaacRequest" - {

      "must return valid PaacRequest for given valid CalculationRequest with all tax years" in {

        val result = service.buildPaacRequest(validCalculationRequestWithAllYears)

        result mustEqual validPaacRequestWithAllYears
      }

      "must return valid PaacRequest for given valid CalculationRequest with missing tax years" in {

        val result = service.buildPaacRequest(validCalculationRequestWithMissingYears)

        result mustEqual validPaacRequestWithMissingYears
      }

      "must return valid PaacRequest for given valid CalculationRequest with 2020 InitialFlexiblyAccessedTaxYear" in {

        val result = service.buildPaacRequest(validCalculationRequestWith2020InitialFlexiblyAccessedTaxYear)

        result mustEqual validPaacRequestWith2020InitialFlexiblyAccessedTaxYear
      }

      "must return valid PaacRequest for given valid CalculationRequest with 2016PostAlignment InitialFlexiblyAccessedTaxYear" in {

        val result =
          service.buildPaacRequest(validCalculationRequestWith2016PostAlignmentInitialFlexiblyAccessedTaxYear)

        result mustEqual validPaacRequestWith2016PostAlignmentInitialFlexiblyAccessedTaxYear
      }

      "must return valid PaacRequest for given valid CalculationRequest with no InitialFlexiblyAccessedTaxYear" in {

        val result = service.buildPaacRequest(validCalculationRequestWithNoInitialFlexiblyAccessedTaxYear)

        result mustEqual validPaacRequestWithNoInitialFlexiblyAccessedTaxYear
      }

      "must return valid PaacRequest for given valid CalculationRequest with no InitialFlexiblyAccessedTaxYear and pensionInput2016PostAmount = Some(0) for CppaTaxYear2016To2023.NormalTaxYear for period 2016" in {

        val result = service.buildPaacRequest(validCalculationRequestWithNoInitialFlexiblyAccessedTaxYear2)

        result mustEqual validPaacRequestWithNoInitialFlexiblyAccessedTaxYear
      }

    }

    "findTaxRate" - {

      "must return correct TaxRate for NonScottishTaxRate 2016 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2016, 12385, 28000, 2250)

        result mustEqual (0.2, 12385)
      }

      "must return correct TaxRate for ScottishTaxRate 2016 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2016, Period._2017), Period._2016, 12384, 28000, 2250)

        result mustEqual (0.2, 12384)
      }

      "must return correct TaxRate for NonScottishTaxRate 2016 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2016, 16000, 150000, 2250)

        result mustEqual (0.4, 34035)
      }

      "must return correct TaxRate for ScottishTaxRate 2016 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2016, Period._2017), Period._2016, 14250, 149999, 2250)

        result mustEqual (0.4, 34035)
      }

      "must return correct TaxRate for NonScottishTaxRate 2016 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2016, 12000, 154000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2016 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2016, Period._2017), Period._2016, 12000, 154000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for NonScottishTaxRate 2017 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2018), Period._2017, 11000, 28000, 2250)

        result mustEqual (0.2, 11000)
      }

      "must return correct TaxRate for ScottishTaxRate 2017 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2017), Period._2017, 12000, 28000, 2250)

        result mustEqual (0.2, 12000)
      }

      "must return correct TaxRate for NonScottishTaxRate 2017 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2018), Period._2017, 10000, 150000, 2250)

        result mustEqual (0.4, 34250)
      }

      "must return correct TaxRate for ScottishTaxRate 2017 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2017), Period._2017, 14999, 150100, 2250)

        result mustEqual (0.4, 33635)
      }

      "must return correct TaxRate for NonScottishTaxRate 2017 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2018), Period._2017, 14001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2017 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2017), Period._2017, 14001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for NonScottishTaxRate 2018 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2018, 8000, 18000, 2250)

        result mustEqual (0.2, 8000)
      }

      "must return correct TaxRate for ScottishTaxRate 2018 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2018), Period._2018, 12000, 28000, 2250)

        result mustEqual (0.2, 12000)
      }

      "must return correct TaxRate for NonScottishTaxRate 2018 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2018, 15000, 148000, 2250)

        result mustEqual (0.4, 35750)
      }

      "must return correct TaxRate for ScottishTaxRate 2018 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2018), Period._2018, 14999, 148000, 2250)

        result mustEqual (0.4, 33750)
      }

      "must return correct TaxRate for NonScottishTaxRate 2018 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2018, 15001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2018 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2018), Period._2018, 15001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2019 under StarterRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2019), Period._2019, 13850, 1000, 500)

        result mustEqual (0.19, 13850)
      }

      "must return correct TaxRate for NonScottishTaxRate 2019 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2019, 16350, 28000, 2250)

        result mustEqual (0.2, 16350)
      }

      "must return correct TaxRate for ScottishTaxRate 2019 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2019), Period._2019, 14000, 8000, 2250)

        result mustEqual (0.2, 2000)
      }

      "must return correct TaxRate for ScottishTaxRate 2019 under IntermediateRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2019), Period._2019, 13430, 28000, 2250)

        result mustEqual (0.21, 14400)
      }

      "must return correct TaxRate for NonScottishTaxRate 2019 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2019, 15000, 148000, 2250)

        result mustEqual (0.4, 36750)
      }

      "must return correct TaxRate for ScottishTaxRate 2019 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2019), Period._2019, 14999, 148000, 2250)

        result mustEqual (0.41, 33830)
      }

      "must return correct TaxRate for NonScottishTaxRate 2019 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2019, 15001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2019 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2019), Period._2019, 15001, 188000, 2250)

        result mustEqual (0.46, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2020 under StarterRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2020), Period._2020, 14549, 1000, 750)

        result mustEqual (0.19, 14549)
      }

      "must return correct TaxRate for NonScottishTaxRate 2020 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2020, 5000, 31000, 1250)

        result mustEqual (0.2, 5000)
      }

      "must return correct TaxRate for ScottishTaxRate 2020 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2020), Period._2020, 24944, 11000, 1250)

        result mustEqual (0.2, 2049)
      }

      "must return correct TaxRate for ScottishTaxRate 2020 under IntermediateRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2020), Period._2020, 43430, 31000, 1250)

        result mustEqual (0.21, 13694)
      }

      "must return correct TaxRate for NonScottishTaxRate 2020 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2020, 150000, 148000, 2250)

        result mustEqual (0.4, 39750)
      }

      "must return correct TaxRate for ScottishTaxRate 2020 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2020), Period._2020, 149999, 148000, 2250)

        result mustEqual (0.41, 33180)
      }

      "must return correct TaxRate for NonScottishTaxRate 2020 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2020, 150001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2020 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2020), Period._2020, 150001, 188000, 2250)

        result mustEqual (0.46, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2021 under StarterRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2021), Period._2021, 9585, 1000, 750)

        result mustEqual (0.19, 9585)
      }

      "must return correct TaxRate for NonScottishTaxRate 2021 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2021, 10160, 31000, 1250)

        result mustEqual (0.2, 10160)
      }

      "must return correct TaxRate for ScottishTaxRate 2021 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2021), Period._2021, 9158, 11000, 1250)

        result mustEqual (0.2, 2085)
      }

      "must return correct TaxRate for ScottishTaxRate 2021 under IntermediateRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2021), Period._2021, 12430, 28000, 1250)

        result mustEqual (0.21, 13908)
      }

      "must return correct TaxRate for NonScottishTaxRate 2021 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2021, 12000, 148000, 2250)

        result mustEqual (0.4, 39750)
      }

      "must return correct TaxRate for ScottishTaxRate 2021 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2021), Period._2021, 149999, 148000, 1250)

        result mustEqual (0.41, 32180)
      }

      "must return correct TaxRate for NonScottishTaxRate 2021 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2021, 150001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2021 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2021), Period._2021, 150001, 188000, 2250)

        result mustEqual (0.46, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2022 under StarterRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2022), Period._2022, 14667, 1000, 750)

        result mustEqual (0.19, 14667)
      }

      "must return correct TaxRate for NonScottishTaxRate 2022 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2022, 14270, 27000, 2250)

        result mustEqual (0.2, 14270)
      }

      "must return correct TaxRate for ScottishTaxRate 2022 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2022), Period._2022, 25296, 8000, 2250)

        result mustEqual (0.2, 2097)
      }

      "must return correct TaxRate for ScottishTaxRate 2022 under IntermediateRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2022), Period._2022, 43662, 28000, 2250)

        result mustEqual (0.21, 14976)
      }

      "must return correct TaxRate for NonScottishTaxRate 2022 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2022, 12000, 148000, 2250)

        result mustEqual (0.4, 39950)
      }

      "must return correct TaxRate for ScottishTaxRate 2022 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2022), Period._2022, 149999, 148000, 2250)

        result mustEqual (0.41, 33342)
      }

      "must return correct TaxRate for NonScottishTaxRate 2022 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2022, 150001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2022 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2022), Period._2022, 150001, 188000, 2250)

        result mustEqual (0.46, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2023 under StarterRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2023), Period._2023, 14732, 1000, 750)

        result mustEqual (0.19, 14732)
      }

      "must return correct TaxRate for NonScottishTaxRate 2023 under BasicRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2023, 15270, 28000, 2250)

        result mustEqual (0.2, 15270)
      }

      "must return correct TaxRate for ScottishTaxRate 2023 under BasicRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2023), Period._2023, 15688, 8000, 2250)

        result mustEqual (0.2, 2162)
      }

      "must return correct TaxRate for ScottishTaxRate 2023 under IntermediateRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2023), Period._2023, 43662, 28000, 2250)

        result mustEqual (0.21, 15368)
      }

      "must return correct TaxRate for NonScottishTaxRate 2023 under TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2023, 150000, 148000, 2250)

        result mustEqual (0.4, 39950)
      }

      "must return correct TaxRate for ScottishTaxRate 2023 under TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2023), Period._2023, 149999, 148000, 2250)

        result mustEqual (0.41, 33342)
      }

      "must return correct TaxRate for NonScottishTaxRate 2023 above TopRateAllowance" in {

        val result = service.findTaxRate(List(Period._2017), Period._2023, 150001, 188000, 2250)

        result mustEqual (0.45, 152250)
      }

      "must return correct TaxRate for ScottishTaxRate 2023 above TopRateAllowance" in {

        val result =
          service.findTaxRate(List(Period._2016, Period._2023), Period._2023, 150001, 188000, 2250)

        result mustEqual (0.46, 152250)
      }

    }

    "calculateOriginalCharge" - {

      val nonZeroTaxYearSchemes = List(
        TaxYearScheme("Scheme 1", "pstrTest1", 10000, 2000, None),
        TaxYearScheme("Scheme 2", "pstrTest2", 10000, 3000, None)
      )

      val zeroTaxYearSchemes = List(
        TaxYearScheme("Scheme 1", "pstrTest1", 10000, 0, None),
        TaxYearScheme("Scheme 2", "pstrTest2", 10000, 0, None)
      )

      "must return correct OriginalCharge for non-zero chargePaidByMember and chargePaidBySchemes" in {

        val result =
          service.calculateOriginalCharge(1000, nonZeroTaxYearSchemes)

        result mustEqual 6000
      }

      "must return correct OriginalCharge for zero chargePaidByMember and non-zero chargePaidBySchemes" in {

        val result =
          service.calculateOriginalCharge(0, nonZeroTaxYearSchemes)

        result mustEqual 5000
      }

      "must return correct OriginalCharge for non-zero chargePaidByMember and zero chargePaidBySchemes" in {

        val result =
          service.calculateOriginalCharge(1000, zeroTaxYearSchemes)

        result mustEqual 1000
      }

      "must return correct OriginalCharge for non-zero chargePaidByMember and Nil chargePaidBySchemes" in {

        val result =
          service.calculateOriginalCharge(1000, Nil)

        result mustEqual 1000
      }

      "must return correct OriginalCharge for zero chargePaidByMember and chargePaidBySchemes" in {

        val result =
          service.calculateOriginalCharge(0, zeroTaxYearSchemes)

        result mustEqual 0
      }

      "must return correct OriginalCharge for zero chargePaidByMember and Nil chargePaidBySchemes" in {

        val result =
          service.calculateOriginalCharge(0, Nil)

        result mustEqual 0
      }

    }

    "calculateRevisedCharge" - {

      "must return correct RevisedCharge for non-zero chargeableAmount and non scottishTaxYear 2017 with TaxRate 0.4" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2023),
            Period._2017,
            18000,
            60000,
            18000,
            2250
          )

        result mustEqual 7200.0
      }

      "must return correct RevisedCharge for non-zero chargeableAmount and scottishTaxYear 2019 with TaxRate 0.21" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2019),
            Period._2019,
            18000,
            20000,
            62000,
            2250
          )

        result mustEqual 22653.99
      }

      "must return correct RevisedCharge for non-zero chargeableAmount and scottishTaxYear 2019 with TaxRate 0.41" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2019),
            Period._2019,
            0,
            40000,
            22000,
            2250
          )

        result mustEqual 9020.0
      }

      "must return correct RevisedCharge for negative chargeableAmount and scottishTaxYear 2019 with TaxRate 0.21" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2019),
            Period._2019,
            18000,
            40000,
            -18000,
            2250
          )

        result mustEqual 0.0
      }

      "must return correct RevisedCharge for non-zero chargeableAmount and scottishTaxYear 2022 with TaxRate 0.19" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2022),
            Period._2022,
            14000,
            28000,
            22000,
            2250
          )

        result mustEqual 7951.59
      }

      "must return correct RevisedCharge for zero chargeableAmount and scottishTaxYear 2022 with TaxRate 0.19" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2022),
            Period._2022,
            0,
            14000,
            22000,
            2250
          )

        result mustEqual 5141.83
      }

      "must return correct RevisedCharge for negative chargeableAmount and scottishTaxYear 2022 with TaxRate 0.41" in {

        val result =
          service.calculateRevisedCharge(
            List(Period._2016PostAlignment, Period._2022),
            Period._2022,
            0,
            80000,
            22000,
            2250
          )

        result mustEqual 9020.0
      }

    }

    "buildOutOfDatesTaxYearsCalculationResult" - {

      val nonZeroTaxYearSchemes = List(
        TaxYearScheme("Scheme 1", "pstrTest1", 10000, 2000, None),
        TaxYearScheme("Scheme 2", "pstrTest2", 10000, 3000, None)
      )

      val zeroTaxYearSchemes = List(
        TaxYearScheme("Scheme 1", "pstrTest1", 10000, 0, None),
        TaxYearScheme("Scheme 2", "pstrTest2", 10000, 0, None)
      )

      "must return correct OutOfDatesTaxYearsCalculation for non-zero chargeableAmount, chargePaidByMember and chargePaidBySchemes for nonScottishTaxYear" in {

        val result =
          service.buildOutOfDatesTaxYearsCalculationResult(
            Period._2017,
            List(Period._2016PostAlignment, Period._2018),
            60000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            1000,
            nonZeroTaxYearSchemes,
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2017),
                3000,
                10000
              )
            ),
            None
          )

        result mustEqual OutOfDatesTaxYearsCalculation(
          Period._2017,
          800,
          4000,
          1000,
          5000,
          3000,
          1200,
          10000,
          List(
            OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1600),
            OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 2400)
          )
        )
      }

      "must return correct OutOfDatesTaxYearsCalculation for non-zero chargeableAmount, chargePaidByMember and chargePaidBySchemes for scottishTaxYear" in {

        val result =
          service.buildOutOfDatesTaxYearsCalculationResult(
            Period._2019,
            List(Period._2016PostAlignment, Period._2019),
            30000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            1000,
            nonZeroTaxYearSchemes,
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
                3000,
                20000
              )
            ),
            None
          )

        result mustEqual OutOfDatesTaxYearsCalculation(
          Period._2019,
          895,
          4475,
          1000,
          5000,
          3000,
          630,
          20000,
          List(
            OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1790),
            OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 2685)
          )
        )
      }

      "must return correct OutOfDatesTaxYearsCalculation for non-zero chargeableAmount, chargePaidByMember and chargePaidBySchemes for nonScottishTaxYear with negative charge" in {

        val result =
          service.buildOutOfDatesTaxYearsCalculationResult(
            Period._2017,
            List(Period._2016PostAlignment, Period._2018),
            60000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            100,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 200, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 300, None)
            ),
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2017),
                3000,
                10000
              )
            ),
            None
          )

        result mustEqual OutOfDatesTaxYearsCalculation(
          Period._2017,
          0,
          0,
          100,
          500,
          3000,
          1200,
          10000,
          List(
            OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
            OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
          )
        )
      }

      "must return correct OutOfDatesTaxYearsCalculation for zero chargePaidByMember and chargePaidBySchemes for nonScottishTaxYear with non zero charge" in {

        val result =
          service.buildOutOfDatesTaxYearsCalculationResult(
            Period._2017,
            List(Period._2016PostAlignment, Period._2018),
            60000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            0,
            zeroTaxYearSchemes,
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2017),
                3000,
                10000
              )
            ),
            None
          )

        result mustEqual OutOfDatesTaxYearsCalculation(
          Period._2017,
          0,
          0,
          0,
          0,
          3000,
          1200,
          10000,
          List(
            OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
            OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
          )
        )
      }

    }

    "buildInDatesTaxYearsCalculationResult" - {

      val nonZeroTaxYearSchemes = List(
        TaxYearScheme("Scheme 1", "pstrTest1", 10000, 2000, None),
        TaxYearScheme("Scheme 2", "pstrTest2", 10000, 3000, None)
      )

      val zeroTaxYearSchemes = List(
        TaxYearScheme("Scheme 1", "pstrTest1", 10000, 0, None),
        TaxYearScheme("Scheme 2", "pstrTest2", 10000, 0, None)
      )

      "must return correct InDatesTaxYearsCalculation for non-zero chargeableAmount, chargePaidByMember and chargePaidBySchemes for nonScottishTaxYear" in {

        val result =
          service.buildInDatesTaxYearsCalculationResult(
            Period._2021,
            List(Period._2016PostAlignment, Period._2018),
            60000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            1000,
            nonZeroTaxYearSchemes,
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2021),
                3000,
                10000
              )
            )
          )

        result mustEqual InDatesTaxYearsCalculation(
          Period._2021,
          800,
          4000,
          0,
          1000,
          5000,
          3000,
          1200,
          10000,
          List(
            InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 2000),
            InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 3000)
          )
        )
      }

      "must return correct InDatesTaxYearsCalculation for non-zero chargeableAmount, chargePaidByMember and chargePaidBySchemes for scottishTaxYear" in {

        val result =
          service.buildInDatesTaxYearsCalculationResult(
            Period._2021,
            List(Period._2016PostAlignment, Period._2021),
            30000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            1000,
            nonZeroTaxYearSchemes,
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2021),
                3000,
                20000
              )
            )
          )

        result mustEqual InDatesTaxYearsCalculation(
          Period._2021,
          895,
          4475,
          0,
          1000,
          5000,
          3000,
          630,
          20000,
          List(
            InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 2000),
            InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 3000)
          )
        )
      }

      "must return correct InDatesTaxYearsCalculation for non-zero chargeableAmount, chargePaidByMember and chargePaidBySchemes for nonScottishTaxYear with negative charge" in {

        val result =
          service.buildInDatesTaxYearsCalculationResult(
            Period._2021,
            List(Period._2016PostAlignment, Period._2018),
            60000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            100,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 200, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 300, None)
            ),
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2021),
                3000,
                10000
              )
            )
          )

        result mustEqual InDatesTaxYearsCalculation(
          Period._2021,
          0,
          0,
          600,
          100,
          500,
          3000,
          1200,
          10000,
          List(
            InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 200),
            InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 300)
          )
        )
      }

      "must return correct InDatesTaxYearsCalculation for zero chargePaidByMember and chargePaidBySchemes for nonScottishTaxYear with non zero charge" in {

        val result =
          service.buildInDatesTaxYearsCalculationResult(
            Period._2021,
            List(Period._2016PostAlignment, Period._2018),
            60000,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None
            ),
            0,
            zeroTaxYearSchemes,
            Some(
              PaacResponseRow(
                PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2021),
                3000,
                10000
              )
            )
          )

        result mustEqual InDatesTaxYearsCalculation(
          Period._2021,
          0,
          0,
          1200,
          0,
          0,
          3000,
          1200,
          10000,
          List(
            InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
            InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
          )
        )
      }

    }

    "calculateCompensation" - {

      val validCalculationRequestWithNormalTaxYears = CalculationRequest(
        Resubmission(isResubmission = false, None),
        List(Period._2017, Period._2018, Period._2021),
        List(
          CppaTaxYear2011To2015(10000, Period._2013),
          CppaTaxYear2011To2015(11000, Period._2014),
          CppaTaxYear2011To2015(12000, Period._2015),
          CppaTaxYear2016To2023.NormalTaxYear(
            18000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, Some(123)),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, Some(123))
            ),
            90000,
            4000,
            Period._2016,
            IncomeSubJourney(
              None,
              None,
              None,
              None,
              None,
              Some(888),
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              None,
              Some(2291)
            )
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            18000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            100000,
            0,
            Period._2017,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(AboveThreshold(21000))
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            10000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            100000,
            0,
            Period._2018,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(AboveThreshold(24000))
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            10000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            90000,
            3000,
            Period._2019,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(BelowThreshold)
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            10000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            90000,
            3000,
            Period._2020,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(AboveThreshold(24000))
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            10000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            90000,
            8000,
            Period._2021,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(BelowThreshold)
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            10000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            90000,
            3000,
            Period._2022,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(AboveThreshold(24000))
          ),
          CppaTaxYear2016To2023.NormalTaxYear(
            10000,
            List(
              TaxYearScheme("Scheme 1", "pstrTest1", 10000, 1000, None),
              TaxYearScheme("Scheme 2", "pstrTest2", 10000, 1000, None)
            ),
            90000,
            4000,
            Period._2023,
            IncomeSubJourney(
              Some(444),
              Some(666),
              Some(712),
              Some(777),
              Some(true),
              Some(888),
              None,
              Some(1111),
              Some(1212),
              Some(1414),
              Some(842),
              Some(Increase),
              Some(2740),
              None,
              Some(2291)
            ),
            Some(AboveThreshold(24000))
          )
        )
      )

      val validPaacResponseWithNormalTaxYears = PaacResponse(
        List(
          PaacResponseRow(
            PaacTaxYear2017ToCurrent
              .NormalTaxYear(10000, AboveThreshold(24000), Period._2023),
            0,
            10000
          ),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2022),
            0,
            10000
          ),
          PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(10000, BelowThreshold, Period._2021), 0, 10000),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2020),
            0,
            10000
          ),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.NormalTaxYear(10000, BelowThreshold, Period._2019),
            0,
            10000
          ),
          PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018), 0, 10000),
          PaacResponseRow(
            PaacTaxYear2017ToCurrent.NormalTaxYear(18000, AboveThreshold(21000), Period._2017),
            0,
            10000
          ),
          PaacResponseRow(
            PaacTaxYear2016PostAlignment.NormalTaxYear(18000, Period._2016PostAlignment),
            10000,
            10000
          ),
          PaacResponseRow(
            PaacTaxYear2016PreAlignment.NormalTaxYear(18000, Period._2016PreAlignment),
            0,
            10000
          ),
          PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0, 10000),
          PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014), 0, 10000),
          PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0, 10000),
          PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2012), 0, 10000),
          PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2011), 0, 10000)
        )
      )

      "must return correct CalculationResponse for a valid CalculationRequest with all years" in {

        val result = service.calculateCompensation(validCalculationRequestWithAllYears, validPaacResponseWithAllYears)

        result mustEqual withAllYearsResponse
      }

      "must return correct CalculationResponse for a valid CalculationRequest with all NormalTaxYears" in {

        val result =
          service.calculateCompensation(validCalculationRequestWithNormalTaxYears, validPaacResponseWithNormalTaxYears)

        result mustEqual allTaxYearsWithNormalTaxYearResponse
      }

    }

    "calculateTotalAmounts" - {

      "must return correct TotalAmounts for a valid outDates and inDates calculations for all years" in {

        val result =
          service.calculateTotalAmounts(withAllYearsResponse.outDates, withAllYearsResponse.inDates)

        result mustEqual TotalAmounts(32400, 0, 23002)
      }

      "must return correct TotalAmounts for a valid outDates and inDates calculations for missing years" in {

        val result =
          service.calculateTotalAmounts(missingTaxYearsValidResponse.outDates, missingTaxYearsValidResponse.inDates)

        result mustEqual TotalAmounts(4002, 2200, 6200)
      }

      "must return correct TotalAmounts for a valid outDates and inDates calculations for all TaxYears with all NormalTaxYear" in {

        val result =
          service.calculateTotalAmounts(
            allTaxYearsWithNormalTaxYearResponse.outDates,
            allTaxYearsWithNormalTaxYearResponse.inDates
          )

        result mustEqual TotalAmounts(11002, 0, 26000)
      }

      "must return correct TotalAmounts for a valid outDates and inDates calculations for all TaxYears with all InitialFlexiblyAccessedTaxYear" in {

        val result =
          service.calculateTotalAmounts(
            allTaxYearsWithInitialFlexiblyAccessedTaxYearResponse.outDates,
            allTaxYearsWithInitialFlexiblyAccessedTaxYearResponse.inDates
          )

        result mustEqual TotalAmounts(12600, 0, 16340)
      }

      "must return correct TotalAmounts for a valid outDates and inDates calculations for all TaxYears with PostFlexiblyAccessedTaxYear" in {

        val result =
          service.calculateTotalAmounts(
            allTaxYearsWithPostFlexiblyAccessedTaxYearResponse.outDates,
            allTaxYearsWithPostFlexiblyAccessedTaxYearResponse.inDates
          )

        result mustEqual TotalAmounts(4002, 3200, 3440)
      }

    }

  }

}
