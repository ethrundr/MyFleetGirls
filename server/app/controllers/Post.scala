package controllers

import scala.Throwable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits._
import play.api.mvc._
import com.ponkotuy.data._
import Common._

/**
 *
 * @author ponkotuy
 * Date: 14/02/21.
 */
object Post extends Controller {
  def basic = authAndParse[Basic] { case (auth, basic) =>
    val isChange = !models.Basic.findByUser(auth.id).exists(_.diff(basic) < 0.01)
    if(isChange) {
      models.Basic.create(basic)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def material = authAndParse[Material] { case (auth, material) =>
    val isChange = !models.Material.findByUser(auth.id).exists(_.diff(material) < 0.01)
    if(isChange) {
      models.Material.create(material)
      Ok("Success")
    } else {
      Ok("No Change")
    }
  }

  def ship = Action.async {
    Future { Gone("Obsolete this version API. Your client need updated.") }
  }

  def ship2 = authAndParse[List[Ship]] { case (auth, ships) =>
    models.Ship.deleteAllByUser(auth.id)
    models.Ship.bulkInsert(ships, auth.id)
    Ok("Success")
  }

  def updateShip() = authAndParse[List[Ship]] { case (auth, ships) =>
    models.Ship.bulkUpsert(ships, auth.id)
    Ok("Success")
  }

  def ndock = authAndParse[List[NDock]] { case (auth, docks) =>
    models.NDock.deleteAllByUser(auth.id)
    docks.foreach(dock => models.NDock.create(dock))
    Ok("Success")
  }

  def createShip = authAndParse[CreateShipAndDock] { case (auth, CreateShipAndDock(ship, dock)) =>
    try {
      models.CreateShip.createFromKDock(ship, dock)
    } catch {
      case e: Throwable =>
        Ok("Duplicate Entry")
    }
    Ok("Success")
  }

  def createShip2 = authAndParse[CreateShipWithId] { case (auth, CreateShipWithId(ship, id)) =>
    models.CreateShip.create(ship, auth.id, id)
    Ok("Success")
  }

  def createItem = authAndParse[CreateItem] { (auth, item) =>
    models.CreateItem.create(item, auth.id)
    for {
      id <- item.id
      slotitemId <- item.slotitemId
      master <- models.MasterSlotItem.find(slotitemId)
    } {
      models.SlotItem.create(auth.id, id, slotitemId, master.name)
    }
    Ok("Success")
  }

  def kdock = authAndParse[List[KDock]] { case (auth, docks) =>
    models.KDock.deleteByUser(auth.id)
    models.KDock.bulkInsert(docks.filterNot(_.completeTime == 0))
    Ok("Success")
  }

  def deckPort = authAndParse[List[DeckPort]] { case (auth, decks) =>
    try {
      models.DeckPort.deleteByUser(auth.id)
      models.DeckPort.bulkInsert(decks)
    } catch {
      case e: Exception => e.printStackTrace()
    }
    Ok("Success")
  }

  def shipBook = authAndParse[List[ShipBook]] { case (auth, ships) =>
    models.ShipBook.bulkUpsert(ships, auth.id)
    Ok("Success")
  }

  def itemBook = authAndParse[List[ItemBook]] { case (auth, items) =>
    models.ItemBook.bulkUpsert(items, auth.id)
    Ok("Success")
  }

  def mapInfo = authAndParse[List[MapInfo]] { case (auth, maps) =>
    models.MapInfo.deleteAllByUser(auth.id)
    models.MapInfo.bulkInsert(maps, auth.id)
    Ok("Success")
  }

  def slotItem = authAndParse[List[SlotItem]] { case (auth, items) =>
    models.SlotItem.deleteAllByUser(auth.id)
    models.SlotItem.bulkInsert(items, auth.id)
    Ok("Success")
  }

  def battleResult = authAndParse[(BattleResult, MapStart)] { case (auth, (result, map)) =>
    models.BattleResult.create(result, map, auth.id)
    Ok("Success")
  }

  def mapRoute = authAndParse[MapRoute] { case (auth, mapRoute) =>
    models.MapRoute.create(mapRoute, auth.id)
    Ok("Success")
  }

  def questlist = authAndParse[List[Quest]] { case (auth, quests) =>
    println(quests)
    models.Quest.bulkUpsert(quests, auth.id)
    Ok("Success")
  }
}
