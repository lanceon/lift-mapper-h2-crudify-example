package com.webitoria
package model

import net.liftweb.mapper.{MappedString, IdPK, LongKeyedMetaMapper, LongKeyedMapper}
import net.liftweb.common.Loggable
import collection.mutable.ListBuffer

class Color extends LongKeyedMapper[Color] with IdPK {

  def getSingleton = Color

  object name extends MappedString(this, 32)

}

object Color extends Color with LongKeyedMetaMapper[Color]
  with Loggable
{
  def initColors() : List[String] = {
    Color.bulkDelete_!!()
    List("Red", "Orange", "Yellow", "Green", "Blue").foldLeft(ListBuffer[String]())( (acc, name)=>{
      Color.create.name(name).save()
      acc += "Color added: %s".format(name)
    }).toList
  }

}