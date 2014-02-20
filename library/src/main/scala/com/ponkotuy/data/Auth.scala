package com.ponkotuy.data

import scalikejdbc.async._
import scalikejdbc.async.FutureImplicits._
import scalikejdbc.SQLInterpolation._
import scalikejdbc.WrappedResultSet
import scala.concurrent.Future
import org.json4s.{JValue, DefaultFormats}
import org.json4s.JsonAST.{JInt, JString}

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
  * @param startTime ゲーム開始時間っぽいけど暗号代わりに
 */
case class Auth(id: Long, nickname: String, startTime: Long) extends ShortenedNames {
  def save()(implicit session: AsyncDBSession = AsyncDB.sharedSession, ctx: EC = ECGlobal): Future[Auth] =
    Auth.save(this)
}

object Auth extends SQLSyntaxSupport[Auth] with ShortenedNames {
  implicit val formats = DefaultFormats

  def apply(x: SyntaxProvider[Auth])(rs: WrappedResultSet): Auth = apply(x.resultName)(rs)
  def apply(x: ResultName[Auth])(rs: WrappedResultSet): Auth = new Auth(
    rs.long(x.id),
    rs.string(x.nickname),
    rs.long(x.startTime)
  )

  def fromJSON(json: JValue): Auth = {
    val JString(id) = json \ "api_nickname_id"
    val JString(nickname) = json \ "api_nickname"
    val JInt(startTime) = json \ "api_starttime"
    Auth(id.toLong, nickname, startTime.toLong)
  }

  lazy val auth = Auth.syntax("auth")

  def find(id: Long)(
    implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Option[Auth]] = withSQL {
    select.from(Auth as auth).where.eq(auth.id, id).limit(1)
  }.map(Auth(auth))

  def save(a: Auth)(
      implicit session: AsyncDBSession = AsyncDB.sharedSession, cxt: EC = ECGlobal): Future[Auth] = withSQL {
    update(Auth).set(column.id -> a.id, column.nickname -> a.nickname, column.startTime -> a.startTime)
      .where.eq(column.id, a.id)
  }.update().future.map(_ => a)
}
