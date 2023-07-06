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

package uk.gov.hmrc.calculatepublicpensionadjustment.repositories

import org.mongodb.scala.model._
import uk.gov.hmrc.calculatepublicpensionadjustment.models.Done
import uk.gov.hmrc.calculatepublicpensionadjustment.models.submission.Submission
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import java.time.Clock
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SubmissionRepository @Inject() (
  mongoComponent: MongoComponent,
  clock: Clock
)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[Submission](
      collectionName = "submissions",
      mongoComponent = mongoComponent,
      domainFormat = Submission.format,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("created"),
          IndexOptions()
            .name("createdIdx")
            .expireAfter(30, TimeUnit.DAYS)
        )
      )
    ) {

  def insert(item: Submission): Future[Done] =
    collection
      .insertOne(item.copy(lastUpdated = clock.instant()))
      .toFuture()
      .map(_ => Done)
}
