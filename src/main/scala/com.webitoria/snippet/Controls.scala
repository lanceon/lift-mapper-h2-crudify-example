package com.webitoria
package snippet

import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmds
import model.Country
import net.liftweb.http.js.JsCmds.SetHtml
import net.liftweb.common.Loggable
import xml.Text
import net.liftweb.http.SHtml.BasicElemAttr

class Controls extends Loggable {

  def importBtn = SHtml.ajaxButton("Start import",
    ()=> SetHtml("results", Country.importCountries().map(s => <div>{Text(s)}</div>)),
    BasicElemAttr("class", "btn")
  )

  def listCountriesLink = "a [href]" #> Country.listPathString
  def createCountryLink = "a [href]" #> Country.createPathString

}
