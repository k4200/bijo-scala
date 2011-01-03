package code.model

import net.liftweb._
import mapper._

object Girl extends Girl with LongKeyedMetaMapper[Girl] with AdminEditable[Long, Girl] {
  override def fieldOrder = List(name, dateOfBirth, url)
  override def dbTableName = "girls"; // define the DB table name
}

/**
 * 
 */
class Girl extends LongKeyedMapper[Girl] with IdPK 
// OneToOneというのは存在しないので、OneToManyを使用し、Many側に制限をかける 
// TODO エラーが出るので、後で調べる。
// with OneToMany[Long, Girl]
{
  def getSingleton = Girl

  object name extends MappedString(this, 20)
  object dateOfBirth extends MappedDateTime(this)
  
  // 以下をコメントアウトするとEntry側にエラーが出る
//  protected object entries
//  extends MappedOneToMany(Entry, Entry.girl, OrderBy(Entry.id, Ascending));

  
  // Optional
  object url extends MappedString(this, 20)
  object comment extends MappedTextarea(this, 1000)
  
}
