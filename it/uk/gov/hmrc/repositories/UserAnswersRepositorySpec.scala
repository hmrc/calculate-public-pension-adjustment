package uk.gov.hmrc.repositories

import org.mockito.MockitoSugar
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Configuration
import play.api.libs.json.Json
import uk.gov.hmrc.calculatepublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.{SubmissionRepository, UserAnswersRepository}
import uk.gov.hmrc.crypto.{Crypted, Decrypter, Encrypter, SymmetricCryptoFactory}
import uk.gov.hmrc.mongo.test.DefaultPlayMongoRepositorySupport

import java.security.SecureRandom
import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import java.util.Base64
import scala.concurrent.ExecutionContext.Implicits.global

class UserAnswersRepositorySpec
    extends AnyFreeSpec
    with Matchers
    with DefaultPlayMongoRepositorySupport[UserAnswers]
    with ScalaFutures
    with IntegrationPatience
    with OptionValues
    with MockitoSugar {

  private val userAnswersUniqueId = "userAnswersUniqueId"
  private val instant             = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock: Clock    = Clock.fixed(instant, ZoneId.systemDefault)
  private val mockAppConfig       = mock[AppConfig]

  private val aesKey = {
    val aesKey = new Array[Byte](32)
    new SecureRandom().nextBytes(aesKey)
    Base64.getEncoder.encodeToString(aesKey)
  }

  private val configuration = Configuration("crypto.key" -> aesKey)

  private implicit val crypto: Encrypter with Decrypter =
    SymmetricCryptoFactory.aesGcmCryptoFromConfig("crypto", configuration.underlying)

  when(mockAppConfig.cacheTtl) thenReturn 900

  protected override val repository = new UserAnswersRepository(
    mongoComponent = mongoComponent,
    appConfig = mockAppConfig,
    clock = stubClock
  )

  val userAnswers = UserAnswers(userAnswersUniqueId, Json.obj("foo" -> "bar"), Instant.now(stubClock), true, true)

  ".get" - {

    "when a userAnswer exists, must get the record with the uniqueId" in {

      insert(userAnswers).futureValue

      val result = repository.get(userAnswersUniqueId).futureValue
      result.value mustEqual userAnswers
    }

    "when no userAnswer exists, return None" in {

      repository.get(userAnswersUniqueId).futureValue must not be defined
    }
  }

  ".clear" - {

    "must clear user answers" in {

      insert(userAnswers).futureValue

      repository.clear(userAnswersUniqueId).futureValue
      repository.get(userAnswersUniqueId).futureValue must not be defined
    }
  }

  ".set" - {

    "must set user answers" in {

      repository.set(userAnswers).futureValue
      repository.get(userAnswersUniqueId).futureValue.value mustBe userAnswers
    }
  }

  ".keepAlive" - {

    "must return done when last updated time kept alive" in {

      insert(userAnswers).futureValue

      repository.keepAlive(userAnswersUniqueId).futureValue mustBe Done
    }
  }
}
