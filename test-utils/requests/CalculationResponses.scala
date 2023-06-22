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

package requests

import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation._

trait CalculationResponses {

  val allTaxYearsWithNormalTaxYearResponse: CalculationResponse = CalculationResponse(
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        4000.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        3000.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        5000,
        3000,
        2000,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        10000,
        8000,
        2000,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        5000,
        3000,
        2000,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        6000,
        4000,
        2000,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val allTaxYearsWithInitialFlexiblyAccessedTaxYearResponse = CalculationResponse(
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        4000.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0.0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        1560.0,
        1040,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        5000,
        1560,
        1040,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        10000,
        6032,
        1508,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        5000,
        1560,
        1040,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        6000,
        2400,
        1200,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val allTaxYearsWithPostFlexiblyAccessedTaxYearResponse = CalculationResponse(
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        1334.0,
        668,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 334),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 334)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0.0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2018,
        0.0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        0.0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        5000,
        0,
        0,
        1400,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2021,
        10000,
        2752,
        688,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        5000,
        0,
        0,
        1400,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        6000,
        0,
        0,
        400,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

  val missingTaxYearsValidResponse = CalculationResponse(
    List(
      OutOfDatesTaxYearsCalculation(
        Period._2016PreAlignment,
        0.0,
        2000,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2016PostAlignment,
        1334.0,
        668,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2017,
        0.0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 0),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 0)
        )
      ),
      OutOfDatesTaxYearsCalculation(
        Period._2019,
        0.0,
        0,
        List(
          OutOfDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          OutOfDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    ),
    List(
      InDatesTaxYearsCalculation(
        Period._2020,
        5000,
        1560,
        1040,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2022,
        5000,
        0,
        0,
        2200,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      ),
      InDatesTaxYearsCalculation(
        Period._2023,
        6000,
        2400,
        1200,
        0,
        List(
          InDatesTaxYearSchemeCalculation("Scheme 1", "pstrTest1", 1000),
          InDatesTaxYearSchemeCalculation("Scheme 2", "pstrTest2", 1000)
        )
      )
    )
  )

}
