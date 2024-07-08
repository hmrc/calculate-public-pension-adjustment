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

package uk.gov.hmrc.calculatepublicpensionadjustment.repositories

import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model._
import play.api.libs.json.Format
import uk.gov.hmrc.calculatepublicpensionadjustment.config.AppConfig
import uk.gov.hmrc.calculatepublicpensionadjustment.models.{Done, UserAnswers}
import uk.gov.hmrc.crypto.{Decrypter, Encrypter}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats

import java.time.{Clock, Instant}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAnswersRepository @Inject() (
  mongoComponent: MongoComponent,
  appConfig: AppConfig,
  clock: Clock
)(implicit ec: ExecutionContext, crypto: Encrypter with Decrypter)
    extends PlayMongoRepository[UserAnswers](
      collectionName = "user-answers",
      mongoComponent = mongoComponent,
      domainFormat = UserAnswers.format,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("lastUpdated"),
          IndexOptions()
            .name("last-updated-index")
            .expireAfter(appConfig.userAnswerTtlInDays, TimeUnit.DAYS)
        )
      )
    ) {

  implicit val instantFormat: Format[Instant] = MongoJavatimeFormats.instantFormat

  private def byId(id: String): Bson = Filters.equal("_id", id)

  private def byUniqueId(uniqueId: String): Bson = Filters.equal("uniqueId", uniqueId)

  private def byUniqueIdAndNotId(uniqueId: String, id: String): Bson =
    Filters.and(Filters.equal("uniqueId", uniqueId), Filters.ne("_id", id))

  def keepAlive(id: String): Future[Done] =
    collection
      .updateOne(
        filter = byId(id),
        update = Updates.set("lastUpdated", Instant.now(clock))
      )
      .toFuture()
      .map(_ => Done)

  def keepAliveByUniqueId(uniqueId: String): Future[Done] =
    collection
      .updateOne(
        filter = byUniqueId(uniqueId),
        update = Updates.set("lastUpdated", Instant.now(clock))
      )
      .toFuture()
      .map(_ => Done)

  def get(id: String): Future[Option[UserAnswers]] =
    keepAlive(id).flatMap { _ =>
      collection
        .find(byId(id))
        .headOption()
    }

  def getByUniqueId(uniqueId: String): Future[Option[UserAnswers]] =
    keepAliveByUniqueId(uniqueId).flatMap { _ =>
      collection
        .find(byUniqueId(uniqueId))
        .headOption()
    }

  def set(userAnswers: UserAnswers): Future[Done] = {

    val updatedUserAnswers = userAnswers copy (lastUpdated = Instant.now(clock))

    collection
      .replaceOne(
        filter = byId(updatedUserAnswers.id),
        replacement = updatedUserAnswers,
        options = ReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => Done)
  }

  def clear(id: String): Future[Done] =
    collection
      .deleteOne(byId(id))
      .toFuture()
      .map(_ => Done)

  def clearByUniqueIdAndNotId(uniqueId: String, id: String): Future[Done] =
    collection
      .deleteOne(byUniqueIdAndNotId(uniqueId, id))
      .toFuture()
      .map(_ => Done)
}
