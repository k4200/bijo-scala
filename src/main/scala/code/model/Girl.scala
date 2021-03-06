package code.model

import net.liftweb._
import common._
import mapper._
import http._;

object Girl extends Girl with LongKeyedMetaMapper[Girl] with AdminEditable[Long, Girl] {
  override def fieldOrder = List(name, dateOfBirth, url)
  override def dbTableName = "girls"; // define the DB table name
  import xml.{ NodeSeq, Text, UnprefixedAttribute, Null }
  // http://groups.google.com/group/liftweb/browse_thread/thread/5c44aabd86200448
  override def _createTemplate = super._createTemplate %
    new UnprefixedAttribute("multipart", Text("true"), Null) 
  override def _editTemplate = super._editTemplate %
    new UnprefixedAttribute("multipart", Text("true"), Null) 
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
  
  object dateOfBirth extends MappedDate(this) {
    import java.text.{SimpleDateFormat, ParseException}
    import java.util.Date
    val dateFormat = new SimpleDateFormat("yyyy/MM/dd")
    override def format(d: Date): String = dateFormat.format(is)
    override def parse(s: String): Box[Date] = {
      try {
        Full(dateFormat.parse(s))
      } catch {
        case e: ParseException => Empty 
      }
    }
  } 
  
  // 以下をコメントアウトするとEntry側にエラーが出る
//  protected object entries
//  extends MappedOneToMany(Entry, Entry.girl, OrderBy(Entry.id, Ascending));

  
  object image extends MappedBinary(this) {
    override def dbDisplay_? = false
    override def writePermission_? = true
  } 

  object imagePath extends MappedBinary(this.asInstanceOf[MapperType]) {
    def notEmpty(input: String) = {
      println("input=" + input)
      if (input.isEmpty) {
        //List(FieldError(this, "Choose a file!"))
      }
      else Nil
    }
    
    override def _toForm = {
      Full(SHtml.fileUpload(saveFile _))
    }
    override def displayName = "BILD"
    //override def validations = notEmpty _ :: Nil
  }

  private def saveFile(fp: FileParamHolder): Unit = {
	//TODO ugly code.
    println("*** saveFile ***")
    fp.file match {
      case null => {
        println("saveFile: null")
      }
      case x if x.length == 0 => {
        println("saveFile: empty file")
      }
      case _ => {
        println("saveFile: default")
        code.lib.GirlImageHandler.getFormatName(fp.file) match {
          case Some(format) => {
            this.image(fp.file)
            this.imageFormat(format)
          }
          case _ => println("invalid file format?")
        }
      }
    }
  }

  object imageFormat extends MappedString(this, 4) {
    override def dbDisplay_? = false
  } 
  
  // Optional
  object url extends MappedString(this, 150)
  object comment extends MappedTextarea(this, 1000)
  
}
