package bootstrap.liftweb

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.sitemap._
import Loc._
import net.liftweb.db.{DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.mapper.{Schemifier, DB}
import com.webitoria.model.{Color, Country}

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot extends Loggable {

  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.webitoria")

    // Build SiteMap
    val sitemap = List(
      Menu.i("Home") / "index", // the simple way to declare a menu
      Menu.i("Test") / "test"
    ) ::: Country.menus

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(sitemap:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))


    // init database connection
    object DBVendor extends StandardDBVendor(
        Props.get("db.driver", "org.h2.Driver"),
        Props.get("db.url", "jdbc:h2:file:database/countries;AUTO_SERVER=TRUE"),
        Empty, Empty)
    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    LiftRules.unloadHooks.append( () => DBVendor.closeAllConnections_!() )
    S.addAround(DB.buildLoanWrapper())
    Schemifier.schemify(true, Schemifier.infoF _, DefaultConnectionIdentifier, // todo: check run mode
      Country, Color)

    logger.info("APPLICATION STARTED: run.mode = %s".format(Props.mode))
  }
}
