package controllers

import play.api.mvc.Controller
import scalikejdbc.SQLInterpolation._
import org.json4s._
import org.json4s.JsonDSL._

/**
 *
 * @author ponkotuy
 * Date: 14/02/23
 */
object Rest extends Controller {
  import Common._

  def searchUser(q: String) = returnJson(models.Admiral.findAllByLike(s"%$q%", limit = 20))

  /** Createされた記録のあるMasterShipとMasterSlotItemを検索 */
  def searchMaster(q: String) = returnJson {
    val ships = models.CreateShip.findAllShipByNameLike(s"%$q%") ++
      models.BattleResult.findAllShipByNameLike(s"%$q%")
    val items = models.CreateItem.findAllItemByNameLike(sqls"mi.name like ${s"%$q%"}")
    Map("ships" -> ships.distinct, "items" -> items)
  }

  /** Createされた記録のあるMasterShipを検索 */
  def searchMasterShip(q: String) = returnJson {
    models.CreateShip.findAllShipByNameLike(s"%$q%") ++
      models.BattleResult.findAllShipByNameLike(s"%$q%")
  }

  def recipeFromShip(shipId: Int) = returnJson {
    val allCounts = models.CreateShip.materialCount().toMap
    val counts = models.CreateShip.materialCount(sqls"result_ship = ${shipId}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def dropFromShip(shipId: Int) = returnJson {
    val allCounts = models.BattleResult.countAllGroupByCells().toMap
    val dropCounts = models.BattleResult.countAllGroupByCells(sqls"get_ship_id = ${shipId}")
    dropCounts.map { case (cell, count) =>
      Map("cell" -> cell, "count" -> count, "sum" -> allCounts(cell))
    }
  }

  def recipeFromItem(itemId: Int) = returnJson {
    val allCounts = models.CreateItem.materialCount().toMap
    val counts = models.CreateItem.materialCount(sqls"slotitem_id = ${itemId}")
    counts.map { case (mat, count) =>
      Map("mat" -> mat, "count" -> count, "sum" -> allCounts(mat))
    }
  }

  def dropCell(area: Int, info: Int, cell: Int, rank: String) = returnJson {
    val drops = models.BattleResult.countCellsGroupByDrop(area, info, cell, rank)
    val sum = drops.map(_._2).sum.toDouble
    drops.map { case (drop, count) =>
      Extraction.decompose(drop).asInstanceOf[JObject] ~
        ("count" -> count) ~
        ("rate" -> f"${count / sum * 100}%.1f%%")
    }
  }

  def route(area: Int, info: Int) = returnJson {
    val routes = models.MapRoute.countCellsGroupByDest(area, info)
    routes.map { case (route, count) =>
      Extraction.decompose(route).asInstanceOf[JObject] ~ ("count" -> count)
    }
  }

  def cellInfo(area: Int, info: Int) = returnJson {
    val where = sqls"true"
      .append(if(area != -1) sqls" and area_id = ${area}" else sqls"")
      .append(if(info != -1) sqls" and info_no = ${info}" else sqls"")
    models.CellInfo.findAllBy(where)
  }

  def maps() = returnJson {
    models.MapRoute.findStageUnique()
  }

  def materials(userId: Long) = returnJson(models.Material.findAllByUser(userId))

  def basics(userId: Long) = returnJson(models.Basic.findAllByUser(userId))

  def ndocks(memberId: Long) = returnJson(models.NDock.findAllByUserWithName(memberId))

  def kdocks(memberId: Long) = returnJson(models.KDock.findAllByUserWithName(memberId))

  def missions(memberId: Long) = returnJson(models.Mission.findByUserWithFlagship(memberId))

  def createShips(memberId: Long, limit: Int, offset: Int, large: Boolean) = returnJson {
    models.CreateShip.findAllByUserWithName(memberId, large, limit, offset)
  }

  def createShipCount(memberId: Long, large: Boolean) =
    returnString(models.CreateShip.countByUser(memberId, large))

  def createItems(memberId: Long, limit: Int, offset: Int) = returnJson {
    models.CreateItem.findAllByWithName(sqls"ci.member_id = ${memberId}", limit, offset)
  }

  def createItemCount(memberId: Long) = returnString(models.CreateItem.countBy(sqls"member_id = ${memberId}"))

  def battleResult(memberId: Long, limit: Int, offset: Int, boss: Boolean, drop: Boolean, rank: String) = returnJson {
    val where = battleResultWhere(memberId, boss, drop, rank)
    val result = models.BattleResult.findAllByWithCell(where, limit, offset)
    JArray(result.map(_.toJson))
  }

  def battleResultCount(memberId: Long, boss: Boolean, drop: Boolean, rank: String) = returnString {
    val where = battleResultWhere(memberId, boss, drop, rank)
    models.BattleResult.countBy(where)
  }

  private def battleResultWhere(memberId: Long, boss: Boolean, drop: Boolean, rank: String) =
    sqls"member_id = ${memberId}"
      .append(if(rank.nonEmpty) sqls" and win_rank in (${rank.map(_.toString)})" else sqls"")
      .append(if(boss) sqls" and boss = true" else sqls"")
      .append(if(drop) sqls" and get_ship_id is not null" else sqls"")

  def routeLog(memberId: Long, limit: Int, offset: Int, area: Int, info: Int) = returnJson {
    models.MapRoute.findAllBy(routeLogWhere(memberId, area, info), limit, offset)
  }

  def routeLogCount(memberId: Long, area: Int, info: Int) = returnString {
    models.MapRoute.countBy(routeLogWhere(memberId, area, info))
  }

  private def routeLogWhere(memberId: Long, area: Int, info: Int) = {
    sqls"member_id = ${memberId}"
      .append(if(area != -1) sqls" and area_id = ${area}" else sqls"")
      .append(if(info != -1) sqls" and info_no = ${info}" else sqls"")
  }

  def quest(memberId: Long) = returnJson { models.Quest.findAllBy(sqls"member_id = ${memberId}") }
}
