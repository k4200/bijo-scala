package code.snippet

import _root_.scala.xml.{ NodeSeq, Text }
import _root_.net.liftweb._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import http._
import mapper._
import util._
import Helpers._
import code.lib._

class ShowEntry {
//  def entry: CssBind = {
//	   "#time *" #> ""
//  }
  import code.model._
  import net.liftweb.mapper.By

  def render = {
    S.param("entry_id").map( entryId =>
      (for(entry <- Entry.find(By(Entry.id, entryId.toLong), PreCache(Entry.girl));
           girl <- entry.girl.obj)
        yield {
          ".title *" #> entry.title &
          ".overview *" #> entry.overview &
          ".description *" #> entry.description &
          ".girl_name *" #> girl.name &
          ".girl_comment_c *" #> girl.url &
          ".girl_image [src]" #> Text("/girl/image/" + girl.id) 
       }).getOrElse {
    	  // In case that an entry doesn't exist for the given ID
    	  // or girl is null.
          ".entry *" #> Text("No entry for the given id " + entryId)
    }).getOrElse {
      // entry_idが渡されない場合は何も表示しない
      //".entry *" #> List[NodeSeq]()
      ".entry *" #> Text("entry_id wasn't passed.")
    }
  }
  
  /**
   * Shows the list of all entries that a girl is already assigned to.
   * @return
   */
  def list = {
    S.param("entry_id") match {
      case Empty => {
        val entries = Entry.findAll(NotNullRef(Entry.girl))
        ".entry *" #> entries.map(entry => 
          ".link *" #> entry.title &
          ".link [href]" #> "/entry?entry_id=%s".format(entry.id)
        )
      }
   	  case _ => ".entry *" #> List[NodeSeq]()
   	}
  }
  
  def foo(in: NodeSeq): NodeSeq = {
    S.param("girl_id").map(
      girlId => Text("id = " + girlId)
    ).getOrElse {
      Text("girl_id must be specified.")
    }
  }
}
