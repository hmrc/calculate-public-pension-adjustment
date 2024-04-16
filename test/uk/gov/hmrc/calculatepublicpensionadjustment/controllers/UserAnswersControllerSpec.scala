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

package uk.gov.hmrc.calculatepublicpensionadjustment.controllers

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.{Mockito, MockitoSugar}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderNames
import uk.gov.hmrc.calculatepublicpensionadjustment.repositories.UserAnswersRepository
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, SubmissionStatusResponse, UserAnswers}
import uk.gov.hmrc.calculatepublicpensionadjustment.services.UserAnswersService
import uk.gov.hmrc.internalauth.client.test.StubBehaviour

import java.time.temporal.ChronoUnit
import java.time.{Clock, Instant, ZoneId}
import scala.concurrent.Future

class UserAnswersControllerSpec
    extends AnyFreeSpec
    with Matchers
    with MockitoSugar
    with OptionValues
    with ScalaFutures
    with BeforeAndAfterEach {

  private val mockRepo               = mock[UserAnswersRepository]
  private val mockUserAnswersService = mock[UserAnswersService]
  private val mockStubBehaviour      = mock[StubBehaviour]

  private val instant   = Instant.now.truncatedTo(ChronoUnit.MILLIS)
  private val stubClock = Clock.fixed(instant, ZoneId.systemDefault)
  private val userId    = "foo"
  private val userData  = UserAnswers(userId, Json.obj("bar" -> "baz"), "uniqueId", Instant.now(stubClock), true, true)

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset[Any](mockRepo, mockStubBehaviour, mockUserAnswersService)
  }

  private val app = new GuiceApplicationBuilder()
    .overrides(
      bind[UserAnswersRepository].toInstance(mockRepo),
      bind[UserAnswersService].toInstance(mockUserAnswersService)
    )
    .build()

  ".get" - {

    "must return OK and the data when user data can be found for this session id" in {

      when(mockRepo.get(eqTo(userId))) thenReturn Future.successful(Some(userData))

      val request =
        FakeRequest(GET, routes.UserAnswersController.get.url)
          .withHeaders(HeaderNames.xSessionId -> userId)

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.toJson(userData)
    }

    "must return Not Found when user data cannot be found for this session id" in {

      when(mockRepo.get(any())) thenReturn Future.successful(None)

      val request =
        FakeRequest(GET, routes.UserAnswersController.get.url)
          .withHeaders(HeaderNames.xSessionId -> "foo")

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND
    }

    "must return Bad Request when the request does not have a session id" in {

      val request = FakeRequest(GET, routes.UserAnswersController.get.url)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }
  }

  ".set" - {

    "must return No Content when the data is successfully saved" in {

      when(mockRepo.set(any())) thenReturn Future.successful(Done)

      val request =
        FakeRequest(POST, routes.UserAnswersController.set.url)
          .withHeaders(
            HeaderNames.xSessionId -> "foo",
            "Content-Type"         -> "application/json"
          )
          .withBody(Json.toJson(userData).toString)

      val result = route(app, request).value

      status(result) mustEqual NO_CONTENT
      verify(mockRepo, times(1)).set(eqTo(userData))
    }

    "must return Bad Request when the request does not have a session id" in {

      val request =
        FakeRequest(POST, routes.UserAnswersController.set.url)
          .withHeaders("Content-Type" -> "application/json")
          .withBody(Json.toJson(userData))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }

    "must return Bad Request when the request cannot be parsed as UserData" in {

      val badPayload = Json.obj("foo" -> "bar")

      val request =
        FakeRequest(POST, routes.UserAnswersController.set.url)
          .withHeaders(
            HeaderNames.xSessionId -> "foo",
            "Content-Type"         -> "application/json"
          )
          .withBody(badPayload)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }
  }

  ".testOnlyset" - {

    "must return No Content when the data is successfully saved" in {

      when(mockRepo.set(any())) thenReturn Future.successful(Done)

      val request =
        FakeRequest(POST, routes.UserAnswersController.testOnlyset.url)
          .withHeaders(
            "Content-Type" -> "application/json"
          )
          .withBody(Json.toJson(userData).toString)

      val result = route(app, request).value

      status(result) mustEqual NO_CONTENT
      verify(mockRepo, times(1)).set(eqTo(userData))
    }

    "must return Bad Request when the request cannot be parsed as UserData" in {

      val badPayload = Json.obj("foo" -> "bar")

      val request =
        FakeRequest(POST, routes.UserAnswersController.testOnlyset.url)
          .withHeaders(
            "Content-Type" -> "application/json"
          )
          .withBody(badPayload)

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }
  }

  ".keepAlive" - {

    "must return No Content when data is kept alive" in {

      when(mockRepo.keepAlive(eqTo(userId))) thenReturn Future.successful(Done)

      val request =
        FakeRequest(POST, routes.UserAnswersController.keepAlive.url)
          .withHeaders(HeaderNames.xSessionId -> "foo")
          .withBody(Json.toJson(userData))

      val result = route(app, request).value

      status(result) mustEqual NO_CONTENT
      verify(mockRepo, times(1)).keepAlive(eqTo(userId))
    }

    "must return Bad Request when the request does not have a session id" in {

      val request =
        FakeRequest(POST, routes.UserAnswersController.keepAlive.url)
          .withBody(Json.toJson(userData))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }
  }

  ".clear" - {

    "must return No Content when data is cleared" in {

      when(mockRepo.clear(eqTo(userId))) thenReturn Future.successful(Done)

      val request =
        FakeRequest(DELETE, routes.UserAnswersController.clear.url)
          .withHeaders(HeaderNames.xSessionId -> "foo")

      val result = route(app, request).value

      status(result) mustEqual NO_CONTENT
      verify(mockRepo, times(1)).clear(eqTo(userId))
    }

    "must return Bad Request when the request does not have a session id" in {

      val request =
        FakeRequest(DELETE, routes.UserAnswersController.clear.url)
          .withBody(Json.toJson(userData))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST
    }
  }

  ".updateSubmissionLander must return OK when user answer found with uniqueID" in {

    when(mockUserAnswersService.retrieveUserAnswers("uniqueId"))
      .thenReturn(
        Future.successful(
          Some(
            UserAnswers("uniqueId", Json.obj("foo" -> "bar"), "uniqueId", Instant.now(stubClock), true, true)
          )
        )
      )

    when(mockUserAnswersService.updateSubmissionStartedToFalse("uniqueId"))
      .thenReturn(
        Future.successful(true)
      )

    val request = FakeRequest(routes.UserAnswersController.updateSubmissionLander("uniqueId"))
      .withHeaders(HeaderNames.xSessionId -> "foo")

    val result = route(app, request).value

    status(result) mustBe OK
  }

  ".updateSubmissionLander must return bad request when user answer not found" in {

    when(mockUserAnswersService.retrieveUserAnswers("uniqueId"))
      .thenReturn(
        Future.successful(
          None
        )
      )

    when(mockUserAnswersService.updateSubmissionStartedToFalse("uniqueId"))
      .thenReturn(
        Future.successful(false)
      )

    val request = FakeRequest(routes.UserAnswersController.updateSubmissionLander("uniqueId"))
      .withHeaders(HeaderNames.xSessionId -> "foo")

    val result = route(app, request).value

    status(result) mustBe BAD_REQUEST
  }

  "checkSubmissionStarted" - {
    "must return OK when record have been found" - {

      val submissionStatusResponse = SubmissionStatusResponse("uniqueId", true)
      when(mockUserAnswersService.checkSubmissionStartedWithId(eqTo(userId))) thenReturn
        Future.successful(Some(submissionStatusResponse))

      val request =
        FakeRequest(GET, routes.UserAnswersController.checkSubmissionStartedWithId(userId).url)
          .withHeaders(HeaderNames.xSessionId -> userId)

      val result = route(app, request).value

      status(result) mustEqual OK
      contentAsJson(result) mustEqual Json.parse("{\"uniqueId\":\"uniqueId\",\"submissionStarted\":true}")
    }

    "must return Not Found when the no records have been found" - {
      when(mockUserAnswersService.checkSubmissionStartedWithId(eqTo(userId))) thenReturn Future.successful(None)

      val request =
        FakeRequest(GET, routes.UserAnswersController.checkSubmissionStartedWithId(userId).url)
          .withHeaders(HeaderNames.xSessionId -> userId)

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND
    }
  }
}
