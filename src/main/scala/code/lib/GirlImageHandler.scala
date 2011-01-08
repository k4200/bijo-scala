package code.lib

import net.liftweb._
import common._
import http._
import mapper._

import code.model.Girl

object GirlImageHandler {
  /**
   * 
   */
  def show(id: String): Box[LiftResponse] = {
	println("GrilImageHandler.show is being called.")
	Girl.find(By(Girl.id, id.toLong)) match {
      case Full(girl) => {
        val headers = ("Content-type" -> ("image/" + girl.imageFormat.toLowerCase)) ::
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
  
import java.awt.Image 
import java.awt.image.BufferedImage
import javax.imageio._
import java.awt.Graphics2D
import java.awt.AlphaComposite
  /**
   * http://stackoverflow.com/questions/1404814/lift-image-upload-resize-store-in-database-display
   */
  def resize(is:java.io.InputStream, maxWidth:Int, maxHeight:Int):BufferedImage = {
	//TODO not used.
    val originalImage:BufferedImage = ImageIO.read(is)
    
    val height = originalImage.getHeight
    val width = originalImage.getWidth

    if (width <= maxWidth && height <= maxHeight)
      originalImage
    else {
      var scaledWidth:Int = width
      var scaledHeight:Int = height
      val ratio:Double = width/height
      if (scaledWidth > maxWidth) {
        scaledWidth = maxWidth
        scaledHeight = (scaledWidth.doubleValue/ratio).intValue
      }
      if (scaledHeight > maxHeight){
        scaledHeight = maxHeight
        scaledWidth = (scaledHeight.doubleValue*ratio).intValue
      }
      val scaledBI = new BufferedImage(scaledWidth, scaledHeight,  BufferedImage.TYPE_INT_RGB)
      val g = scaledBI.createGraphics
      g.setComposite(AlphaComposite.Src)
      g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
      g.dispose
      scaledBI
    }
  }

  def getFormatName(image: Array[Byte]): Option[String] = {
    try {
      import javax.imageio.stream.ImageInputStream
      // Create an image input stream on the image
      val iis = ImageIO.createImageInputStream(new java.io.ByteArrayInputStream(image))
      
      // Find all image readers that recognize the image format
      val iter = ImageIO.getImageReaders(iis)
      if (!iter.hasNext()) {
        // No readers found
    	println("no readers found.")
        return Empty
      }

      // Use the first reader
      val reader = iter.next().asInstanceOf[ImageReader]

      // Close stream
      iis.close()

      // Return the format name
      return Some(reader.getFormatName())
    } catch {
      case e:java.io.IOException => {
    	e.printStackTrace
    	Empty
      }
    }
    // The image could not be read
   	println("The image could not be read.")
    Empty
  }
}