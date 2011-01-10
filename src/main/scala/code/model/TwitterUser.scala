package code.model

import net.liftweb._
import mapper._
import common._
import oauth._
import oauth.mapper._

class TwitterUser extends LongKeyedMapper[TwitterUser] with IdPK
with UserIdAsString with OAuthUser {
  /**
   * 
   */
  def getSingleton = TwitterUser
  /**
   * Convert the id to a String
   */
  def userIdAsString: String = id.is.toString
  
  object twitterAccount extends MappedString(this, 20) {
    override def dbColumnName = "twitter_account"
  }
  object accessToken extends MappedString(this, 100) { //50?
    override def dbColumnName = "access_token"
  }
  object accessTokenSecret extends MappedString(this, 100) { //43?
	override def dbColumnName = "access_token_secret"
  }
  
  /**
   * The superuser field for the User.  You can override the behavior
   * of this field:
   * <pre name="code" class="scala">
   * override lazy val superUser = new MySuperUser(this) {
   *   println("I am doing something different")
   * }
   * </pre>
   * Borrowed from ProtoUser.
   */  
  lazy val superUser: MappedBoolean[TwitterUser] = new MySuperUser(this)

  protected class MySuperUser(obj: TwitterUser) extends MappedBoolean(obj) {
    override def defaultValue = false
  }
}

object TwitterUser extends TwitterUser with LongKeyedMetaMapper[TwitterUser] {
  override def dbTableName = "twitter_users"; // define the DB table name
}

class TestMOAuthConsumer extends MOAuthConsumer[TestMOAuthConsumer] {

  def getSingleton = TestMOAuthConsumer
  type UserType = TwitterUser
  def getUserMeta = TwitterUser
  type MOAuthTokenType = TestMOAuthToken
  def getMOAuthTokenMeta = TestMOAuthToken

}

object TestMOAuthConsumer extends TestMOAuthConsumer with MOAuthConsumerMeta[TestMOAuthConsumer]

class TestMOAuthToken extends MOAuthToken[TestMOAuthToken] {
  def getSingleton = TestMOAuthToken

  type UserType = TwitterUser

  def getUserMeta = TwitterUser

  type MOAuthConsumerType = TestMOAuthConsumer
  def getMOAuthConsumerMeta = TestMOAuthConsumer
}

object TestMOAuthToken extends TestMOAuthToken with MOAuthTokenMeta[TestMOAuthToken]