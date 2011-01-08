package code.lib

import net.liftweb._
import common._
import http._
import mapper._

import code.model.Girl

object GirlImageHandler {
  def show(id: String): Box[LiftResponse] = {
	println("GrilImageHandler.show is being called.")
	Girl.find(By(Girl.id, id.toLong)) match {
      case Full(girl) => {
        //TODO content-type は決めうち・・・
        val headers = ("Content-type" -> "image/jpeg") ::
          ("Cache-Control" -> "no-store") ::
          ("Pragma", "no-cache") ::
          ("Expires", "0" ) ::
          ("Content-length" -> girl.image.length.toString) :: Nil
        Full(StreamingResponse( new java.io.ByteArrayInputStream(girl.image.get),
          () => {}, girl.image.length, headers, Nil, 200) )
      }
      case _ => Empty
    }
  }
}