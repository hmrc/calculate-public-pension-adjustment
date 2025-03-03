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

package uk.gov.hmrc.repositories

import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.{verify, when}
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.calculatepublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.calculatepublicpensionadjustment.models.Done
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.*
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.crypto.{Crypted, Decrypter, Encrypter, SymmetricCryptoFactory}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.security.SecureRandom
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global
import org.mongodb.scala.{ObservableFuture, SingleObservableFuture}

class SubmissionRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[Submission]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  private val id               = "id"
  private val id2              = "id2"
  private val uniqueId         = "uniqueId"
  private val uniqueId2        = "uniqueId2"
  private val instant          = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock = Clock.fixed(instant, ZoneId.systemDefault)
  private val mockAppConfig    = mock[AppConfig]

  private val aesKey = {
    val aesKey = new Array[Byte](32)
    new SecureRandom().nextBytes(aesKey)
    Base64.getEncoder.encodeToString(aesKey)
  }

  private val configuration = Configuration("crypto.key" -> aesKey)

  private implicit val crypto: Encrypter with Decrypter =
    SymmetricCryptoFactory.aesGcmCryptoFromConfig("crypto", configuration.underlying)

  private val calculationInputs = CalculationInputs(
    Resubmission(false, None),
    Setup(
      Some(
        AnnualAllowanceSetup(
          Some(true),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(false),
          Some(MaybePIAIncrease.No),
          Some(MaybePIAUnchangedOrDecreased.No),
          Some(false),
          Some(false),
          Some(false),
          Some(false)
        )
      ),
      Some(
        LifetimeAllowanceSetup(Some(true), Some(false), Some(true), Some(false), Some(false), Some(false), Some(true))
      )
    ),
    None,
    None
  )

  val calculation = Some(
    CalculationResponse(
      Resubmission(false, None),
      TotalAmounts(10470, 1620, 5500),
      List(
        OutOfDatesTaxYearsCalculation(
          Period._2016PreAlignment,
          0,
          0,
          0,
          0,
          0,
          0,
          40000,
          List(OutOfDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 0)),
          Some(0)
        )
      ),
      List(
        InDatesTaxYearsCalculation(
          Period._2020,
          0,
          4500,
          0,
          0,
          9000,
          10000,
          4500,
          0,
          List(InDatesTaxYearSchemeCalculation("Scheme 1", "00348916RT", 9000)),
          Some(0)
        )
      )
    )
  )

  private val submission: Submission = Submission("id", "uniqueId", calculationInputs, calculation)

  when(mockAppConfig.ttlInDays) `thenReturn` 900.toLong

  protected override val repository: SubmissionRepository = new SubmissionRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    clock = stubClock
  )

  ".insert" - {

    "must set the last updated time to `now` and save the submission" in {

      val expectedResult = Submission(
        id,
        "uniqueId",
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      val insertResult = repository.insert(submission).futureValue
      val dbRecord     = find(Filters.equal("_id", "id")).futureValue.headOption.value

      insertResult `mustEqual` Done
      dbRecord `mustEqual` expectedResult
    }

    "must store the data section as encrypted bytes" in {

      val submission = Submission(
        id,
        "uniqueId",
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      repository.insert(submission).futureValue

      val record = repository.collection
        .find[BsonDocument](Filters.equal("uniqueId", submission.uniqueId))
        .headOption()
        .futureValue
        .value

      checkDocumentIsEncryptedAndCanBeDecrypted(record)
    }
  }

  private def checkDocumentIsEncryptedAndCanBeDecrypted(record: BsonDocument) = {
    val json = Json.parse(record.toJson)

    val encryptedInputs: String = (json \ "calculationInputs").get.as[String]
    encryptedInputs mustNot include("resubmission")

    val plainInputs: String = decrypt(encryptedInputs)
    plainInputs must include("resubmission")

    val decryptedInputObject: CalculationInputs = Json.parse(plainInputs).as[CalculationInputs]
    decryptedInputObject.resubmission.isResubmission `mustBe` false

    val encryptedCalculation: String = (json \ "calculation").get.as[String]
    encryptedCalculation mustNot include("totalAmounts")

    val plainCalculation: String = decrypt(encryptedCalculation)
    plainCalculation `must` include("totalAmounts")

    val decryptedCalculationObject: CalculationResponse = Json.parse(plainCalculation).as[CalculationResponse]
    decryptedCalculationObject.totalAmounts.inDatesDebit `mustBe` 1620
  }

  private def decrypt(encryptedString: String): String = {
    val decryptedString: String = crypto.decrypt(Crypted(encryptedString)).value
    decryptedString.drop(1).dropRight(1).replaceAll("\\\\", "")
  }

  ".get" - {

    "when there is a record for this submissionUniqueId" - {

      "must get the record" in {

        val submission = Submission(
          id,
          "uniqueId",
          calculationInputs,
          None,
          Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
        )

        insert(submission).futureValue

        val result = repository.get("uniqueId").futureValue
        result.value `mustEqual` submission
      }
    }

    "when there is no record for this submissionUniqueId" - {

      "must return None" in {

        repository.get(id).futureValue `must` `not` `be` defined
      }
    }
  }

  ".set" - {

    "must set submission" in {
      val submission = Submission(
        id,
        uniqueId,
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      repository.set(submission).futureValue
      repository.get(uniqueId).futureValue.value `mustBe` submission
    }

    "must delete previous submission with the same id regardless of uniqueId" in {

      val firstSubmission = Submission(
        id,
        uniqueId,
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      val secondSubmissionWithSameId = Submission(
        id,
        uniqueId2,
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      repository.set(firstSubmission).futureValue
      repository.set(secondSubmissionWithSameId).futureValue

      repository.get(uniqueId).futureValue mustBe None
      repository.get(uniqueId2).futureValue.value mustBe secondSubmissionWithSameId
    }
  }

  ".clear" - {

    "must clear user answers" in {

      val submission = Submission(
        id,
        "uniqueId",
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      insert(submission).futureValue
      repository.clear("userId").futureValue
      repository.get(id).futureValue `must` `not` `be` defined
    }
  }

  ".clearByUniqueIdAndNotId" - {

    "must clear submission with UniqueId with different Id" in {

      val submission = Submission(
        id,
        uniqueId,
        calculationInputs,
        calculation,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      insert(submission).futureValue
      insert(submission.copy(id = id2, uniqueId = uniqueId2)).futureValue

      repository.clearByUniqueIdAndNotId(uniqueId, id2).futureValue
      repository.get(uniqueId2).futureValue `mustBe` defined
      repository.get(uniqueId).futureValue `must` `not` `be` defined
    }
  }
}
