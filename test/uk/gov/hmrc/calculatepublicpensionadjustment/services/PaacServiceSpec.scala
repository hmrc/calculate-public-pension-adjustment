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

package uk.gov.hmrc.calculatepublicpensionadjustment.services

import org.mockito.MockitoSugar
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import uk.gov.hmrc.calculatepublicpensionadjustment.connectors.PaacConnector
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.Income.{AboveThreshold, BelowThreshold}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.cppa._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.paac._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{CalculationRequest, Period}
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
    with BeforeAndAfterEach {

  private val mockPaacConnector = mock[PaacConnector]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockPaacConnector)
  }

  private val hc: HeaderCarrier = HeaderCarrier()

  private val service = new PaacService(mockPaacConnector)

  "PaacService" - {

    val validCalculationRequestWithAllYears = CalculationRequest(
      List(Period._2017, Period._2019, Period._2021),
      List(
        CppaTaxYear2013To2015(10000, Period._2013),
        CppaTaxYear2013To2015(11000, Period._2014),
        CppaTaxYear2013To2015(12000, Period._2015),
        CppaTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(
          16000,
          LocalDate.parse("2015-05-25"),
          6000,
          10000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 1000),
            TaxYearScheme("Scheme 2", "pstrTest2", 12000, 10000, 1000)
          ),
          0,
          0,
          Period._2016PreAlignment
        ),
        CppaTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(
          16000,
          20000,
          90000,
          4000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 2000),
            TaxYearScheme("Scheme 2", "pstrTest2", 12000, 10000, 6000)
          ),
          Period._2016PostAlignment
        ),
        CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
          12000,
          19000,
          AboveThreshold(21000),
          100000,
          0,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 9000),
            TaxYearScheme("Scheme 2", "pstrTest2", 12000, 10000, 9000)
          ),
          Period._2017
        ),
        CppaTaxYear2017ToCurrent.NormalTaxYear(
          10000,
          AboveThreshold(24000),
          100000,
          0,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 9000),
            TaxYearScheme("Scheme 2", "pstrTest2", 12000, 10000, 9000)
          ),
          Period._2018
        ),
        CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
          23000,
          22000,
          AboveThreshold(24000),
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 3000),
            TaxYearScheme("Scheme 2", "pstrTest2", 18000, 10000, 0)
          ),
          Period._2019
        ),
        CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
          23000,
          LocalDate.parse("2019-12-22"),
          6000,
          10000,
          BelowThreshold,
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 6000),
            TaxYearScheme("Scheme 2", "pstrTest2", 18000, 10000, 0)
          ),
          Period._2020
        ),
        CppaTaxYear2017ToCurrent.NormalTaxYear(
          10000,
          AboveThreshold(24000),
          90000,
          8000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 2000),
            TaxYearScheme("Scheme 2", "pstrTest2", 18000, 10000, 1000)
          ),
          Period._2021
        ),
        CppaTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(
          23000,
          22000,
          AboveThreshold(24000),
          90000,
          3000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 3000),
            TaxYearScheme("Scheme 2", "pstrTest2", 18000, 10000, 6000)
          ),
          Period._2022
        ),
        CppaTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(
          23000,
          LocalDate.parse("2023-02-21"),
          6000,
          10000,
          AboveThreshold(24000),
          90000,
          4000,
          List(
            TaxYearScheme("Scheme 1", "pstrTest1", 12000, 10000, 1000),
            TaxYearScheme("Scheme 2", "pstrTest2", 18000, 10000, 6000)
          ),
          Period._2023
        )
      )
    )

    val validPaacRequestWithAllYears = PaacRequest(
      List(
        PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013),
        PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014),
        PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015),
        PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(16000, 6000, 10000, Period._2016PreAlignment),
        PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(16000, 20000, Period._2016PostAlignment),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(12000, 19000, AboveThreshold(21000), Period._2017),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2018),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(23000, 22000, AboveThreshold(24000), Period._2019),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(23000, 6000, 10000, BelowThreshold, Period._2020),
        PaacTaxYear2017ToCurrent.NormalTaxYear(10000, AboveThreshold(24000), Period._2021),
        PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(23000, 22000, AboveThreshold(24000), Period._2022),
        PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(23000, 6000, 10000, AboveThreshold(24000), Period._2023)
      ),
      Period._2023
    )

    val validPaacResponseWithAllYears = PaacResponse(
      List(
        PaacResponseRow(
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
          16000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          18000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
          16000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          18000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2018), 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          9000
        ),
        PaacResponseRow(
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(36000, 20000, Period._2016PostAlignment),
          10000
        ),
        PaacResponseRow(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
          0
        ),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2012), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2011), 0)
      )
    )

    val validPaacResponseWithAllYearsAfterFilter = PaacResponse(
      List(
        PaacResponseRow(
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
          16000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          18000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, BelowThreshold, Period._2020),
          16000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          18000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2018), 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          9000
        ),
        PaacResponseRow(
          PaacTaxYear2016PostAlignment.PostFlexiblyAccessedTaxYear(36000, 20000, Period._2016PostAlignment),
          10000
        ),
        PaacResponseRow(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(36000, 6000, 10000, Period._2016PreAlignment),
          0
        ),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(11000, Period._2014), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0)
      )
    )

    val validCalculationRequestWithMissingYears =
      CalculationRequest(
        validCalculationRequestWithAllYears.scottishTaxYears,
        validCalculationRequestWithAllYears.taxYears.filterNot(p =>
          p.period == Period._2014 | p.period == Period._2016PostAlignment | p.period == Period._2018 | p.period == Period._2020
        )
      )

    val validPaacRequestWithMissingYears = PaacRequest(
      validPaacRequestWithAllYears.taxYears.filterNot(p =>
        p.period == Period._2014 | p.period == Period._2016PostAlignment | p.period == Period._2018 | p.period == Period._2020
      ),
      validPaacRequestWithAllYears.until
    )

    val validPaacResponseWithMissingYears = PaacResponse(
      List(
        PaacResponseRow(
          PaacTaxYear2017ToCurrent
            .InitialFlexiblyAccessedTaxYear(43000, 6000, 10000, AboveThreshold(24000), Period._2023),
          16000
        ),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2022),
          18000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NormalTaxYear(30000, AboveThreshold(24000), Period._2021), 0),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NoInputTaxYear(Period._2020), 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(43000, 22000, AboveThreshold(24000), Period._2019),
          18000
        ),
        PaacResponseRow(PaacTaxYear2017ToCurrent.NoInputTaxYear(Period._2018), 0),
        PaacResponseRow(
          PaacTaxYear2017ToCurrent.PostFlexiblyAccessedTaxYear(32000, 19000, AboveThreshold(21000), Period._2017),
          9000
        ),
        PaacResponseRow(PaacTaxYear2016PostAlignment.NoInputTaxYear(Period._2016PostAlignment), 0),
        PaacResponseRow(
          PaacTaxYear2016PreAlignment.InitialFlexiblyAccessedTaxYear(16000, 6000, 10000, Period._2016PreAlignment),
          0
        ),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(12000, Period._2015), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2014), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NormalTaxYear(10000, Period._2013), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2012), 0),
        PaacResponseRow(PaacTaxYear2011To2015.NoInputTaxYear(Period._2011), 0)
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

    /*   "buildPaacRequest" - {

      "must return valid PaacRequest for given valid CalculationRequest with all tax years" in {

        val result = service.buildPaacRequest(validCalculationRequestWithAllYears)

        result mustEqual validPaacRequestWithAllYears
      }

      "must return valid PaacRequest for given valid CalculationRequest with missing tax years" in {

        val result = service.buildPaacRequest(validCalculationRequestWithMissingYears)

        result mustEqual validPaacRequestWithMissingYears
      }

    }*/

  }

}
