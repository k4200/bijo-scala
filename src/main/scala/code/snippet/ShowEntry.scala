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

  def render: CssBindFunc = {
    S.param("entry_id").map( entryId => {
      try {
        showEntry(entryId.toLong)
      } catch {
    	case e:NumberFormatException => showNoEntry
      }
    }).getOrElse {
      // entry_idが渡されない場合は何も表示しない
      showNoEntry
      //".entry *" #> Text("entry_id wasn't passed.") //Debug
    }
  }
  
  private def showEntry(entryId: Long): CssBindFunc = {
    Entry.find(By(Entry.id, entryId), PreCache(Entry.girl)) match {
      case Full(entry) => showEntry(entry)
      case _ => ".entry *" #> Text("No entry for the given id " + entryId)
    }
  }
  
  private def showEntry(entry: Entry): CssBindFunc = {
      (for(girl <- entry.girl.obj)
        yield {
          ".title *" #> entry.title &
          ".overview *" #> entry.overview &
          ".description *" #> entry.description &
          ".girl_name *" #> girl.name &
          ".girl_comment_c *" #> girl.url &
          ".girl_image [src]" #> Text("/girl/image/" + girl.id) 
       }).getOrElse {
          ".entry *" #> Text("The entry doesn't have a girl's info yet. Entry ID = " + entry.id)
      }
  }
  
  private def showNoEntry: CssBindFunc = {
    ".entry *" #> List[NodeSeq]() &
    ".tolist *" #> List[NodeSeq]()
  }
  
  //--------------------------------------------------------
  /**
   * Shows the list of all entries that a girl is already assigned to.
   * @return
   */
  def list: CssBindFunc = {
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
  
  //--------------------------------------------------------
  import scala.util.Random
  def random = {
	val entries = Entry.findAll(NotNullRef(Entry.girl))
	val entry = entries(Random.nextInt(entries.length))
	showEntry(entry) &
    ".link [href]" #> "/entry?entry_id=%s".format(entry.id)
  }
}
