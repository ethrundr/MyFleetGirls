package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/10/22.
 */
case class SlotItemWithMaster(item: SlotItem, master: MasterSlotItem) {
  def slotitemId = master.id
  def name = master.name
  def category = master.category
  def iconType = master.iconType
  def alv = item.alv

  def colorClass: String = {
    import tool.EquipIconType._
    iconType.map {
      case MainGunS | MainGunM | MainGunL | AntiShipBullet | Bomber => "danger"
      case CaliberGun | AntiAirBullet | AntiAirGun | Fighter | ScoutSeaplane | Autogiro => "success"
      case SubGun | Scout | Radar | EngineChamber | Searchlight | Flare => "warning"
      case Torpedo | TorpedoBomber | Sonar | DepthBomb => "info"
      case LandingCraft => "success"
      case MaritimePartrolAircraft => "info"
      case Armor => "purple"
      case DamageControl | SimplifiedPackage | RepairFacility | Pilot | Command => ""
    }
  }.getOrElse("")

  /** withLevelと言っているがついでに熟練度もオマケしちゃうぞ */
  def nameWithLevel = master.name + item.withLevel + alvStr.getOrElse("")

  def alvStr: Option[String] = alv.map { alv => s"(${alv})" }

  def airSuperiority: Int = alv.map { alv => (alv - 1) * 25 / 6 }.getOrElse(0)
}
