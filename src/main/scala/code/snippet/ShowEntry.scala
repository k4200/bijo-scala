package code.snippet

import _root_.scala.xml.{ NodeSeq, Text }
import _root_.net.liftweb._
import _root_.net.liftweb.common._
import _root_.java.util.Date
import http._
import util._
import Helpers._
import code.lib._

class ShowEntry {
//  def entry: CssBind = {
//	   "#time *" #> ""
//  }
  import code.model._
  import net.liftweb.mapper.By

  def render(in: NodeSeq): NodeSeq = {
    S.param("entry_id").map( entry_id =>
      Entry.find(By(Entry.id, entry_id.toLong)).map(entry =>
        bind("entry", in,
          //TODO method strToBPAssoc in trait BindHelpers is deprecated
          "field1" --> "1", 
          "field2" --> "2"
        )
      ).getOrElse {
        Text("No entry for the given id " + entry_id)
      }
    ).getOrElse {
      // entry_idが渡されない場合は何も表示しない
      Text("")
    }
  }
  
  def list(in: NodeSeq): NodeSeq = {
  
    Entry.findAll.flatMap(entry => {
      println(new Date() + "," + entry.title + "," + entry.overview)
      bind("line", in,
        //TODO method strToBPAssoc in trait BindHelpers is deprecated
        // -> and --> 
        "title" -> Text(entry.title), 
        "overview" -> Text(entry.overview)
      )
    })
  
//      bind("line", in,
//        //TODO method strToBPAssoc in trait BindHelpers is deprecated
//        // -> and --> 
//        "title" -> Text("aaaaa"), 
//        "overview" -> Text("bbbbbbbbbbb")
//      )
  }
  
  def foo(in: NodeSeq): NodeSeq = {
    S.param("girl_id").map(
      girl_id => Text("id = " + girl_id)
    ).getOrElse {
      Text("girl_id must be specified.")
    }
  }
}
