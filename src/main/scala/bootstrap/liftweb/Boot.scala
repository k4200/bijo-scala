package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import mapper._

import code.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
//    Schemifier.schemify(true, Schemifier.infoF _, User)
    Schemifier.schemify(true, Schemifier.infoF _, Entry)
    Schemifier.schemify(true, Schemifier.infoF _, Girl)
    Schemifier.schemify(true, Schemifier.infoF _, TwitterUser)
//    Schemifier.schemify(true, Schemifier.infoF _, TestMOAuthConsumer)
//    Schemifier.schemify(true, Schemifier.infoF _, TestMOAuthToken)

    // allow /console to fall thru for H2 console servlet
    LiftRules.liftRequest.append({case r if (r.path.partPath match {
      case "console" :: _ => true
      case _ => false}
    ) => false})

    // Register the module for handling girls' images.
    import code.lib._
    LiftRules.statelessDispatchTable.append{
      case Req( "girl" :: "image" :: id :: Nil, _, _ ) => 
        () => GirlImageHandler.show(id)
    }


    // where to search snippet
    LiftRules.addToPackages("code")

    // super userかどうかを判別する
//    val superUser_? = If(() => User.currentUser match {
//                                 case Full(user) => user.superUser;
//                                 case _ => false} ,
//                         () => RedirectResponse("/login"))
    
    // Build SiteMap
    def sitemap = SiteMap(
      List(
//        Menu.i("Home") / "index" >> User.AddUserMenusUnder,
        Menu.i("Home") / "index" >> TwitterUser.AddUserMenusUnder,
//        Menu.i("Home") / "index",
//        Menu.i("Account") / "dummy" >> User.AddUserMenusUnder,
        Menu.i("Entry") / "entry"
      ) :::
      Entry.menus :::
      Girl.menus : _*
      
      // more complex because this menu allows anything in the
      // /static path to be visible
//      List(
//        Menu(Loc("Static", Link(List("static"), true, "/static/index"), 
//	       "Static Content"))) : _*
	)

	
	
//    def sitemapMutators = User.sitemapMutator
    def sitemapMutators = TwitterUser.sitemapMutator

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
//    LiftRules.loggedInTest = Full(() => User.loggedIn_?)
    LiftRules.loggedInTest = Full(() => TwitterUser.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))    

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
  }
}
