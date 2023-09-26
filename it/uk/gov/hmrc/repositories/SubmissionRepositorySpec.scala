package uk.gov.hmrc.repositories

import org.mockito.Mockito.when
import org.mockito.MockitoSugar
import org.mongodb.scala.model.Filters
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.calculatepublicpensionadjustment.models.Done
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.SubmissionRepository
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport
import uk.gov.hmrc.calculatepublicpensionadjustment.models.calculation.{CalculationInputs, Resubmission}
import uk.gov.hmrc.calculatepublicpensionadjustment.config.AppConfig

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.ExecutionContext.Implicits.global

class SubmissionRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[Submission]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  private val submissionUniqueId = "submissionUniqueId"
  private val instant            = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock   = Clock.fixed(instant, ZoneId.systemDefault)
  private val mockAppConfig      = mock[AppConfig]

  private val calculationInputs      = CalculationInputs(Resubmission(false, None), None, None)
  private val submission: Submission = Submission("submissionUniqueId", calculationInputs, None)

  when(mockAppConfig.cacheTtl) thenReturn 900

  protected override val repository = new SubmissionRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    clock = stubClock
  )

  ".insert" - {

    "must set the last updated time to `now` and save the submission" in {

      val expectedResult = Submission(
        submissionUniqueId,
        calculationInputs,
        None,
        Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
      )

      val insertResult = repository.insert(submission).futureValue
      val dbRecord     = find(Filters.equal("uniqueId", submissionUniqueId)).futureValue.headOption.value

      insertResult mustEqual Done
      dbRecord mustEqual expectedResult
    }
  }

  ".get" - {

    "when there is a record for this submissionUniqueId" - {

      "must get the record" in {

        val submission = Submission(
          submissionUniqueId,
          calculationInputs,
          None,
          Instant.now(stubClock).truncatedTo(ChronoUnit.MILLIS)
        )

        insert(submission).futureValue

        val result = repository.get(submissionUniqueId).futureValue
        result.value mustEqual submission
      }
    }

    "when there is no record for this submissionUniqueId" - {

      "must return None" in {

        repository.get(submissionUniqueId).futureValue must not be defined
      }
    }
  }
}
