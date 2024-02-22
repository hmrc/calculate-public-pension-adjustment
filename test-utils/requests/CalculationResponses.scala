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

package requests

import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._

trait CalculationResponses {

  val allTaxYearsWithNormalTaxYearResponse: CalculationResponse = CalculationResponse(
    Resubmission(false, None),
    TotalAmounts(11002, 0, 26000),
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016,
        1334,
        668,
        4000,
        2000,
        10000,
        4000,
        10000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 334),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 334)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0,
        2000,
        0,
        2000,
        0,
        0,
        10000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0,
        2000,
        0,
        2000,
        0,
        0,
        10000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        3000,
        2000,
        3000,
        2000,
        0,
        0,
        10000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        3000,
        2000,
        0,
        3000,
        2000,
        0,
        0,
        10000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        8000,
        2000,
        0,
        8000,
        2000,
        0,
        0,
        10000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        3000,
        2000,
        0,
        3000,
        2000,
        0,
        0,
        10000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        4000,
        2000,
        0,
        4000,
        2000,
        0,
        0,
        10000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val allTaxYearsWithInitialFlexiblyAccessedTaxYearResponse = CalculationResponse(
    Resubmission(false, None),
    TotalAmounts(12600, 0, 16340),
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0,
        2000,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        4000,
        2000,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0,
        2000,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        1560,
        1040,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        1560,
        1040,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        6032,
        1508,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        1560,
        1040,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        2400,
        1200,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val allTaxYearsWithPostFlexiblyAccessedTaxYearResponse = CalculationResponse(
    Resubmission(true, Some("Change in amounts")),
    TotalAmounts(4002, 3200, 3440),
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0,
        2000,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        1334,
        668,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 334),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 334)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        0,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        0,
        0,
        1400,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        2752,
        688,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        0,
        0,
        1400,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        0,
        0,
        400,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val missingTaxYearsValidResponse = CalculationResponse(
    Resubmission(false, None),
    TotalAmounts(4002, 2200, 6200),
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0,
        2000,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        1334,
        668,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        0,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        1560,
        1040,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        0,
        0,
        2200,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        2400,
        1200,
        0,
        200,
        200,
        1000,
        600,
        20000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val withAllYearsResponse = CalculationResponse(
    Resubmission(true, Some("Change in amounts")),
    TotalAmounts(34400, 0, 23002),
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016,
        0,
        2000,
        0,
        2000,
        10000,
        0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0,
        14400,
        0,
        18000,
        9000,
        3600,
        40000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 7200),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 7200)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0,
        18000,
        0,
        18000,
        0,
        0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 9000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 9000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        0,
        0,
        3000,
        3000,
        18000,
        7380,
        30000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        867,
        1734,
        0,
        3000,
        6000,
        16000,
        6400,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 6000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        8000,
        3000,
        0,
        8000,
        3000,
        0,
        0,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 2000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        1200,
        3600,
        0,
        3000,
        9000,
        18000,
        7200,
        60000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 3000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 6000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        1673,
        2928,
        0,
        4000,
        7000,
        16000,
        6400,
        40000,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 6000)
        )
      )
    )
  )

}
