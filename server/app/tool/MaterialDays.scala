package tool

import com.github.nscala_time.time.Imports._
import models.db.Material

/**
 * Date: 14/06/21.
 */
case class MaterialDays(
    day: LocalDate,
    fuel: MatDiff,
    ammo: MatDiff,
    steel: MatDiff,
    bauxite: MatDiff,
    instant: MatDiff,
    bucket: MatDiff,
    develop: MatDiff,
    revamping: MatDiff) {
  def prettyDay: String = day.toString("M月d日")
}

object MaterialDays {
  def fromMaterials(day: LocalDate, x: Material, y: Material): MaterialDays = {
    MaterialDays(day,
      f(x.fuel, y.fuel),
      f(x.ammo, y.ammo),
      f(x.steel, y.steel),
      f(x.bauxite, y.bauxite),
      f(x.instant, y.instant),
      f(x.bucket, y.bucket),
      f(x.develop, y.develop),
      f(x.revamping, y.revamping)
    )
  }

  private def f(now: Int, prev: Int) = MatDiff(now, now - prev)
}
