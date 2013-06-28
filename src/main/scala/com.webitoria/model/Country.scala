package com.webitoria
package model

import net.liftweb.mapper._
import net.liftweb.common.{Full, Loggable, Logger}
import net.liftweb.util.Props
import xml.{Elem, NodeSeq, XML}
import util.Random
import net.liftweb.util.ControlHelpers._
import net.liftweb.http.LiftRules
import collection.mutable.ListBuffer
import net.liftweb.common.Full
import net.liftweb.mapper.MappedField
import net.liftweb.mapper

class Country extends LongKeyedMapper[Country]
  with IdPK
{
  def getSingleton = Country

  override def toString() = "Country(%s, %s, %s)".format(name, code2, color.obj.map(_.name).getOrElse("-"))

  object code2 extends MappedString(this, 2)
  object name extends MappedString(this, 128)
  object color extends MappedLongForeignKey(this, Color) {
    override def dbIndexed_? = true
    override def validSelectValues = Full(
      (defaultValue, "-") :: Color.findMap( OrderBy(Color.name, Ascending) ){ (c:Color) => Full(c.id.get -> c.name.get) }
    )

    // todo: refact
    override def toString() = if (defined_?) obj.map(_.name.toString).getOrElse("") else "-"
  }

}

object Country extends Country with LongKeyedMetaMapper[Country]
  with LongCRUDify[Country]
  with Loggable
{
  override def calcPrefix = List("countries")
  override def displayName = "Countries"
  override def showAllClass = super.showAllClass + " table table-hover table-bordered table-condensed"
  override def viewClass = super.viewClass + " table table-bordered"
  override def editClass = super.editClass + " table table-bordered"
  override def createClass = super.createClass + " table table-bordered"
  override def deleteClass = super.deleteClass + " table table-bordered"

  override def fieldsForList : List[FieldPointerType] = List(name, code2,  color)

  def importCountries() : List[String] = {

    Country.bulkDelete_!!()

    val xml = Props.get("xml_data_url").map(url => Full(XML.load(url)))
      .getOrElse(LiftRules.loadResourceAsXml("/country_names_and_code_elements.xml"))
      .openOrThrowException("Data URL and resource xml not found")

    lazy val colors = Color.findAll().toArray

    Color.initColors ++ (xml \\ "ISO_3166-1_Entry").foldLeft(ListBuffer[String]())((acc, n) => {
      val country = Country.create
        .name((n \ "ISO_3166-1_Country_name").text)
        .code2((n \ "ISO_3166-1_Alpha-2_Code_element").text)
        .color(tryo(colors(Random.nextInt(colors.length * 2))))
        .saveMe()
      acc += "Country added: %s".format(country)
    })

  }

}