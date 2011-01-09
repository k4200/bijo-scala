package code.model
import net.liftweb._;
import sitemap._;
class Entry extends LongKeyedMapper[Entry] with IdPK {
  object overview extends MappedString(this,100);
  object link extends MappedString(this, 100);