package code.model


import net.liftweb._;
import common._;
import http._;
import mapper._

import sitemap._;
import Loc._;

/**
 * The companion object of the class Entry
 * @author kashima
 *
 */
object Entry extends Entry with LongKeyedMetaMapper[Entry] with AdminEditable[Long, Entry] {
  override def fieldOrder = List(title, overview, description, girl, link);
  override def dbTableName: String = "entries";
}

/**
 * An entry. It can be used to explain a command, class, trait,
 * or any feature.
 * @author kashima
 */

class Entry extends LongKeyedMapper[Entry] with IdPK {
  def getSingleton = Entry;
  
  object title extends MappedString(this, 20)

  object overview extends MappedString(this,100);
  
  object girl extends MappedLongForeignKey(this, Girl) {
    //override def dbDisplay_? = false;
    override def validSelectValues = Full(
      (0L, "Not Selected") ::
      // このエントリの女の子
      Girl.findAll(
        In(Girl.id, Entry.girl, By(Entry.id, id))).map{
          x => (x.id.is, x.name.is)} :::
      // まだどのエントリにも使われていない女の子
      Girl.findAll(
        NotIn(Girl.id, Entry.girl, NotNullRef(Entry.girl))).map{
          x => (x.id.is, x.name.is)}
    ) 
  };
  
  object description extends MappedTextarea(this, 1000)
  
  // Optional

  object link extends MappedString(this, 100);
}

