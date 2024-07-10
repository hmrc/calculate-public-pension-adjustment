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

import play.api.libs.json._

trait CppaRequests {

  val emptyRequest = Json.parse("""{
      |
      |}""".stripMargin)

  val outOfTaxYearsRequest: JsValue = Json.parse("""
      |{
      |    "scottishTaxYears": [
      |        "2017",
      |        "2021"
      |    ],
      |    "taxYears": [
      |        {
      |            "period": "2011",
      |            "pensionInputAmount": 10000
      |        },
      |        {
      |            "period": "2015",
      |            "pensionInputAmount": 12000
      |        },
      |        {
      |            "period": "2016",
      |            "totalIncome": 0,
      |            "definedBenefitInputAmount": 16000,
      |            "flexiAccessDate": "2015-05-25",
      |            "preAccessDefinedContributionInputAmount": 6000,
      |            "postAccessDefinedContributionInputAmount": 10000,
      |            "chargePaidByMember": 0,
      |            "taxYearSchemes": [
      |                {
      |                    "name": "Scheme 1",
      |                    "pensionSchemeTaxReference": "pstrTest1",
      |                    "revisedPensionInputAmount": 10000,
      |                    "chargePaidByScheme": 1000
      |                },
      |                {
      |                    "name": "Scheme 2",
      |                    "pensionSchemeTaxReference": "pstrTest2",
      |                    "revisedPensionInputAmount": 10000,
      |                    "chargePaidByScheme": 1000
      |                }
      |            ],
      |            "incomeSubJourney" : {
      |              "isAboveThreshold" : false,
      |              "taxReliefAmount" : 111,
      |              "giftAidAmount" : 222,
      |              "payeCodeAdjustment" : "increase",
      |              "codeAdjustmentAmount" : 333,
      |              "blindPersonsAllowanceAmount" : 2291
      |           }
      |        }
      |    ]
      |}
      |""".stripMargin)

  val invalidDataTypeRequest: JsValue = Json.parse("""
     |{
     |    "scottishTaxYears": [
     |        "2017",
     |        "2021"
     |    ],
     |    "taxYears": [
     |        {
     |            "period": "2013",
     |            "pensionInputAmount": 10000
     |        },
     |        {
     |            "period": "2015",
     |            "pensionInputAmount": 12000
     |        },
     |        {
     |            "period": "2016",
     |            "totalIncome": 0,
     |            "definedBenefitInputAmount": "16000",
     |            "flexiAccessDate": "2015-05-25",
     |            "preAccessDefinedContributionInputAmount": 6000,
     |            "postAccessDefinedContributionInputAmount": 10000,
     |            "chargePaidByMember": 0,
     |            "taxYearSchemes": [
     |                {
     |                    "name": "Scheme 1",
     |                    "pensionSchemeTaxReference": "pstrTest1",
     |                    "revisedPensionInputAmount": 10000,
     |                    "chargePaidByScheme": 1000
     |                },
     |                {
     |                    "name": "Scheme 2",
     |                    "pensionSchemeTaxReference": 123,
     |                    "revisedPensionInputAmount": 10000,
     |                    "chargePaidByScheme": 1000
     |                }
     |            ],
     |            "incomeSubJourney" : {
     |                "isAboveThreshold" : false,
     |                "taxReliefAmount" : 111,
     |                "giftAidAmount" : 222,
     |                "payeCodeAdjustment" : "increase",
     |                "codeAdjustmentAmount" : 333,
     |                "blindPersonsAllowanceAmount" : 2291
     |           }
     |        }
     |    ]
     |}
     |""".stripMargin)

  val missingDataRequest: JsValue = Json.parse("""
     |{
     |    "scottishTaxYears": [
     |        "2017",
     |        "2021"
     |    ],
     |    "taxYears": [
     |        {
     |            "period": "2013",
     |            "pensionInputAmount": 10000
     |        },
     |        {
     |            "period": "2015",
     |            "pensionInputAmount": 12000
     |        },
     |        {
     |            "period": "2016",
     |            "totalIncome": 0,
     |            "flexiAccessDate": "2015-05-25",
     |            "postAccessDefinedContributionInputAmount": 10000,
     |            "chargePaidByMember": 0,
     |            "taxYearSchemes": [
     |                {
     |                    "name": "Scheme 1",
     |                    "pensionSchemeTaxReference": "pstrTest1",
     |                    "revisedPensionInputAmount": 10000,
     |                    "chargePaidByScheme": 1000
     |                },
     |                {
     |                    "name": "Scheme 2",
     |                    "pensionSchemeTaxReference": "pstrTest2",
     |                    "revisedPensionInputAmount": 10000,
     |                    "chargePaidByScheme": 1000
     |                }
     |            ],
     |            "incomeSubJourney" : {
     |              "isAboveThreshold" : false,
     |              "taxReliefAmount" : 111,
     |              "giftAidAmount" : 222,
     |              "payeCodeAdjustment" : "increase",
     |              "codeAdjustmentAmount" : 333,
     |              "blindPersonsAllowanceAmount" : 2291
     |           }
     |        }
     |    ]
     |}
     |""".stripMargin)

  val allTaxYearsWithNormalTaxYearValidRequest: JsValue = Json.parse("""
     |{
     |  "resubmission": {
     |    "isResubmission": true,
     |    "reason": "Change in amounts"
     |  },
     |  "annualAllowance": {
     |    "scottishTaxYears": [
     |      "2017",
     |      "2018",
     |      "2021"
     |    ],
     |    "taxYears": [
     |      {
     |        "period": "2013",
     |        "pensionInputAmount": 10000
     |      },
     |      {
     |        "period": "2014",
     |        "pensionInputAmount": 11000
     |      },
     |      {
     |        "period": "2015",
     |        "pensionInputAmount": 12000
     |      },
     |      {
     |        "period": "2016",
     |        "pensionInputAmount": 18000,
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 4000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "isAboveThreshold" : false,
     |          "taxReliefAmount" : 111,
     |          "giftAidAmount" : 222,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 333,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2017",
     |        "pensionInputAmount": 18000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 21000
     |        },
     |        "totalIncome": 100000,
     |        "chargePaidByMember": 0,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2018",
     |        "pensionInputAmount": 10000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 100000,
     |        "chargePaidByMember": 0,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2019",
     |        "pensionInputAmount": 10000,
     |        "income": {
     |          "incomeAboveThreshold": false
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 3000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2020",
     |        "pensionInputAmount": 10000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 3000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2021",
     |        "pensionInputAmount": 10000,
     |        "income": {
     |          "incomeAboveThreshold": false
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 8000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2022",
     |        "pensionInputAmount": 10000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 3000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2023",
     |        "pensionInputAmount": 10000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 4000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      }
     |    ]
     |  }
     |}""".stripMargin)

  val allTaxYearsWithInitialFlexiblyAccessedTaxYearValidRequest: JsValue = Json.parse("""
      |{
      |  "resubmission": {
      |    "isResubmission": false
      |  },
      |  "annualAllowance": {
      |    "scottishTaxYears": [
      |      "2017",
      |      "2018",
      |      "2021"
      |    ],
      |    "taxYears": [
      |      {
      |        "period": "2013",
      |        "pensionInputAmount": 10000
      |      },
      |      {
      |        "period": "2014",
      |        "pensionInputAmount": 11000
      |      },
      |      {
      |        "period": "2015",
      |        "pensionInputAmount": 12000
      |      },
      |      {
      |        "period": "2016",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "totalIncome": 0,
      |        "chargePaidByMember": 0,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "isAboveThreshold" : false,
      |          "taxReliefAmount" : 111,
      |          "giftAidAmount" : 222,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 333,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2017",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 21000
      |        },
      |        "totalIncome": 100000,
      |        "chargePaidByMember": 0,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2018",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "totalIncome": 100000,
      |        "chargePaidByMember": 0,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2019",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": false
      |        },
      |        "totalIncome": 90000,
      |        "chargePaidByMember": 3000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2020",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "totalIncome": 90000,
      |        "chargePaidByMember": 3000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2021",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": false
      |        },
      |        "totalIncome": 90000,
      |        "chargePaidByMember": 8000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2022",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "totalIncome": 90000,
      |        "chargePaidByMember": 3000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2023",
      |        "definedBenefitInputAmount": 16000,
      |        "flexiAccessDate": "2015-05-25",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "totalIncome": 90000,
      |        "chargePaidByMember": 4000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      }
      |    ]
      |  }
      |}""".stripMargin)

  val allTaxYearsWithPostFlexiblyAccessedTaxYearValidRequest: JsValue = Json.parse("""
     |{
     |  "resubmission": {
     |    "isResubmission": true,
     |    "reason": "Change in amounts"
     |  },
     |  "annualAllowance": {
     |    "scottishTaxYears": [
     |      "2017",
     |      "2018",
     |      "2021"
     |    ],
     |    "taxYears": [
     |      {
     |        "period": "2013",
     |        "pensionInputAmount": 10000
     |      },
     |      {
     |        "period": "2014",
     |        "pensionInputAmount": 11000
     |      },
     |      {
     |        "period": "2015",
     |        "pensionInputAmount": 12000
     |      },
     |      {
     |        "period": "2016",
     |        "definedBenefitInputAmount": 16000,
     |        "flexiAccessDate": "2015-05-25",
     |        "preAccessDefinedContributionInputAmount": 6000,
     |        "postAccessDefinedContributionInputAmount": 10000,
     |        "totalIncome": 0,
     |        "chargePaidByMember": 0,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "isAboveThreshold" : false,
     |          "taxReliefAmount" : 111,
     |          "giftAidAmount" : 222,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 333,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2017",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 21000
     |        },
     |        "totalIncome": 100000,
     |        "chargePaidByMember": 0,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2018",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 100000,
     |        "chargePaidByMember": 0,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2019",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": false
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 3000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2020",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 3000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2021",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": false
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 8000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2022",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 3000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      },
     |      {
     |        "period": "2023",
     |        "definedBenefitInputAmount": 16000,
     |        "definedContributionInputAmount": 20000,
     |        "income": {
     |          "incomeAboveThreshold": true,
     |          "adjustedIncome": 24000
     |        },
     |        "totalIncome": 90000,
     |        "chargePaidByMember": 4000,
     |        "taxYearSchemes": [
     |          {
     |            "name": "Scheme 1",
     |            "pensionSchemeTaxReference": "pstrTest1",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          },
     |          {
     |            "name": "Scheme 2",
     |            "pensionSchemeTaxReference": "pstrTest2",
     |            "revisedPensionInputAmount": 10000,
     |            "chargePaidByScheme": 1000
     |          }
     |        ],
     |        "incomeSubJourney" : {
     |          "salarySacrificeAmount" : 1211,
     |          "flexibleRemunerationAmount" : 1618,
     |          "rASContributionsAmount" : 3345,
     |          "lumpsumDeathBenefitsAmount" : 948,
     |          "isAboveThreshold" : true,
     |          "taxReliefAmount" : 519,
     |          "taxReliefPensionAmount" : 8181,
     |          "personalContributionsAmount" : 4999,
     |          "giftAidAmount" : 589,
     |          "payeCodeAdjustment" : "increase",
     |          "codeAdjustmentAmount" : 697,
     |          "blindPersonsAllowanceAmount" : 2291
     |        }
     |      }
     |    ]
     |  }
     |}""".stripMargin)

  val missingTaxYearsValidRequest: JsValue = Json.parse("""
      |{
      |  "resubmission": {
      |    "isResubmission": false
      |  },
      |  "annualAllowance": {
      |    "scottishTaxYears": [
      |      "2017",
      |      "2019",
      |      "2021"
      |    ],
      |    "taxYears": [
      |      {
      |        "period": "2013",
      |        "pensionInputAmount": 10000
      |      },
      |      {
      |        "period": "2015",
      |        "pensionInputAmount": 12000
      |      },
      |      {
      |        "period": "2016",
      |        "totalIncome": 90000,
      |        "definedBenefitInputAmount": 16000,
      |        "definedContributionInputAmount": 20000,
      |        "chargePaidByMember": 4000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "isAboveThreshold" : false,
      |          "taxReliefAmount" : 111,
      |          "giftAidAmount" : 222,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 333,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2017",
      |        "totalIncome": 100000,
      |        "definedBenefitInputAmount": 12000,
      |        "definedContributionInputAmount": 19000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 21000
      |        },
      |        "chargePaidByMember": 0,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2019",
      |        "totalIncome": 90000,
      |        "definedBenefitInputAmount": 23000,
      |        "definedContributionInputAmount": 22000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "chargePaidByMember": 3000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2020",
      |        "totalIncome": 90000,
      |        "definedBenefitInputAmount": 23000,
      |        "flexiAccessDate": "2019-12-22",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": false
      |        },
      |        "chargePaidByMember": 3000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2022",
      |        "totalIncome": 90000,
      |        "definedBenefitInputAmount": 23000,
      |        "definedContributionInputAmount": 22000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "chargePaidByMember": 3000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      },
      |      {
      |        "period": "2023",
      |        "totalIncome": 90000,
      |        "definedBenefitInputAmount": 23000,
      |        "flexiAccessDate": "2023-02-21",
      |        "preAccessDefinedContributionInputAmount": 6000,
      |        "postAccessDefinedContributionInputAmount": 10000,
      |        "income": {
      |          "incomeAboveThreshold": true,
      |          "adjustedIncome": 24000
      |        },
      |        "chargePaidByMember": 4000,
      |        "taxYearSchemes": [
      |          {
      |            "name": "Scheme 1",
      |            "pensionSchemeTaxReference": "pstrTest1",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          },
      |          {
      |            "name": "Scheme 2",
      |            "pensionSchemeTaxReference": "pstrTest2",
      |            "revisedPensionInputAmount": 10000,
      |            "chargePaidByScheme": 1000
      |          }
      |        ],
      |        "incomeSubJourney" : {
      |          "salarySacrificeAmount" : 1211,
      |          "flexibleRemunerationAmount" : 1618,
      |          "rASContributionsAmount" : 3345,
      |          "lumpsumDeathBenefitsAmount" : 948,
      |          "isAboveThreshold" : true,
      |          "taxReliefAmount" : 519,
      |          "taxReliefPensionAmount" : 8181,
      |          "personalContributionsAmount" : 4999,
      |          "giftAidAmount" : 589,
      |          "payeCodeAdjustment" : "increase",
      |          "codeAdjustmentAmount" : 697,
      |          "blindPersonsAllowanceAmount" : 2291
      |        }
      |      }
      |    ]
      |  }
      |}""".stripMargin)

}
