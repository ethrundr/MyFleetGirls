package models.db

import scalikejdbc._
import com.ponkotuy.data

/** このツール内でログイン代わりに使うパラメータ
  *
  * @param id nick name id
  */
@deprecated("Use Admiral", "0.1-SNAPSHOT")
case class Auth(id: Long, nickname: String, created: Long) {
  def save()(implicit session: DBSession = Auth.autoSession): Auth =
    Auth.save(this)
}

@deprecated("Use Admiral", "0.1-SNAPSHOT")
object Auth extends SQLSyntaxSupport[Auth] {
  def apply(x: SyntaxProvider[Auth])(rs: WrappedResultSet): Auth = apply(x.resultName)(rs)
  def apply(x: ResultName[Auth])(rs: WrappedResultSet): Auth = autoConstruct(rs, x)

  lazy val auth = Auth.syntax("auth")

  def find(id: Long)(implicit session: DBSession = Auth.autoSession): Option[Auth] = {
    withSQL {
      select.from(Auth as auth).where.eq(auth.id, id)
    }.map(Auth(auth)).toOption().apply()
  }

  def findByName(name: String)(implicit session: DBSession = Auth.autoSession): Option[Auth] = {
    withSQL {
      select.from(Auth as auth).where.eq(auth.nickname, name).limit(1)
    }.map(Auth(auth)).toOption().apply()
  }

  def findAllByUser(userId: Long)(implicit session: DBSession = Auth.autoSession): List[Auth] = {
    withSQL {
      select.from(Auth as auth)
        .where.eq(auth.id, userId)
        .orderBy(auth.created).desc
    }.map(Auth(auth)).list().apply()
  }

  def save(a: Auth)(implicit session: DBSession = Auth.autoSession): Auth = {
    withSQL {
      update(Auth).set(column.id -> a.id, column.nickname -> a.nickname)
        .where.eq(column.id, a.id)
    }.update()
    a
  }

  def create(a: data.Auth)(implicit session: DBSession = Auth.autoSession): Auth = {
    val created = System.currentTimeMillis()
    withSQL {
      insert.into(Auth).namedValues(
        column.id -> a.id, column.nickname -> a.nickname, column.created -> created
      )
    }.update().apply()
    Auth(a.id, a.nickname, created)
  }
}
