package models.join

import models.db._

/**
 *
 * @author ponkotuy
 * Date: 14/12/16.
 */
case class ShipWithSpecs(ship: Ship, master: MasterShipBase, stype: MasterStype, spec: MasterShipSpecs) extends ShipParameter
