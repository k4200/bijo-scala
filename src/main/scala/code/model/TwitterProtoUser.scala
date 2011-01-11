package code.model

import net.liftweb._
import mapper._
import common._
import util._
import util.Helpers._
import http._
import sitemap._
import proto.{ProtoUser => GenProtoUser}
import _root_.scala.xml.{ NodeSeq, Text }

trait TwitterProtoUser[T <: TwitterProtoUser[T]] extends LongKeyedMapper[T] with IdPK
with UserIdAsString {
  self: T =>

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
  lazy val superUser: MappedBoolean[T] = new MySuperUser(this)

  protected class MySuperUser(obj: T) extends MappedBoolean(obj) {
    override def defaultValue = false
  }
  
  /**
   * The unique id field for the User. This field
   * is used for validation, lost passwords, etc.
   * You can override the behavior
   * of this field:
   * <pre name="code" class="scala">
   * override lazy val uniqueId = new MyUniqueId(this, 32) {
   *   println("I am doing something different")
   * }
   * </pre>
   */
  lazy val uniqueId: MappedUniqueId[T] = new MyUniqueId(this, 32)

  protected class MyUniqueId(obj: T, size: Int) extends MappedUniqueId(obj, size) {
    override def dbIndexed_? = true
    override def writePermission_?  = true
  }
  
  /**
   * The has the user been validated.
   * You can override the behavior
   * of this field:
   * <pre name="code" class="scala">
   * override lazy val validated = new MyValidated(this, 32) {
   *   println("I am doing something different")
   * }
   * </pre>
   */
  lazy val validated: MappedBoolean[T] = new MyValidated(this)

  protected class MyValidated(obj: T) extends MappedBoolean[T](obj) {
    override def defaultValue = false
    override val fieldId = Some(Text("txtValidated"))
  }
}



/**
 * 
 */
trait MetaTwitterProtoUser[ModelType <: TwitterProtoUser[ModelType]]
extends LongKeyedMetaMapper[ModelType] with GenProtoUser {
  self: ModelType =>
  
//  override def dbTableName = "twitter_users"; // define the DB table name

  // The code below is taken from
  // net.liftweb.proto.ProtoUser and net.liftweb.mapper.MegaProtoUser
  type TheUserType = ModelType
  /**
   * What's a field pointer for the underlying CRUDify
   */
  type FieldPointerType = MappedField[_, TheUserType]
  /**
   * Based on a FieldPointer, build a FieldPointerBridge
   */
  protected implicit def buildFieldBridge(from: FieldPointerType): FieldPointerBridge = new MyPointer(from)

  protected class MyPointer(from: FieldPointerType) extends FieldPointerBridge {
    /**
     * What is the display name of this field?
     */
    def displayHtml: NodeSeq = from.displayHtml

    /**
     * Does this represent a pointer to a Password field
     */
    def isPasswordField_? : Boolean = from match {
      case a: MappedPassword[_] => true
      case _ => false
    }
  }

  /**
   * Convert an instance of TheUserType to the Bridge trait
   */
  protected implicit def typeToBridge(in: TheUserType): UserBridge = 
    new MyUserBridge(in)
  /**
   * Bridges from TheUserType to methods used in this class
   */
  protected class MyUserBridge(in: TheUserType) extends UserBridge {
    /**
     * Convert the user's primary key to a String
     */
    def userIdAsString: String = in.id.toString

    /**
     * Return the user's first name
     */
    def getFirstName: String = ""

    /**
     * Return the user's last name
     */
    def getLastName: String = ""

    /**
     * Get the user's email
     */
    def getEmail: String = ""

    /**
     * Is the user a superuser
     */
    def superUser_? : Boolean = in.superUser

    /**
     * Has the user been validated?
     */
    def validated_? : Boolean = in.validated

    /**
     * Does the supplied password match the actual password?
     */
    def testPassword(toTest: Box[String]): Boolean = false

    /**
     * Set the validation flag on the user and return the user
     */
    def setValidated(validation: Boolean): TheUserType =
      in.validated(validation)

    /**
     * Set the unique ID for this user to a new value
     */
    def resetUniqueId(): TheUserType = {
      in.uniqueId.reset()
    }

    /**
     * Return the unique ID for the user
     */
    def getUniqueId(): String = in.uniqueId

    /**
     * Validate the user
     */
    def validate: List[FieldError] = in.validate

    /**
     * Given a list of string, set the password
     */
    def setPasswordFromListString(pwd: List[String]): TheUserType = {
      //in.password.setList(pwd)
      in
    }

    /**
     * Save the user to backing store
     */
    def save(): Boolean = in.save
  }

  /**
   * Given a field pointer and an instance, get the field on that instance
   */
  protected def computeFieldFromPointer(instance: TheUserType, pointer: FieldPointerType): Box[BaseField] = Full(getActualField(instance, pointer))


  /**
   * Given an twitter account, find the user
   */
  protected def findUserByUserName(twitterAccount: String): Box[TheUserType] =
    find(By(this.twitterAccount, twitterAccount))

  /**
   * Given a unique id, find the user
   */
  protected def findUserByUniqueId(id: String): Box[TheUserType] =
    find(By(uniqueId, id))

  /**
   * Create a new instance of the User
   */
  protected def createNewUserInstance(): TheUserType = self.create

  /**
   * Given a String representing the User ID, find the user
   */
  protected def userFromStringId(id: String): Box[TheUserType] = find(id)

  /**
   * The list of fields presented to the user at sign-up
   */
  def signupFields: List[FieldPointerType] = List(twitterAccount)
  /**
   * The list of fields presented to the user for editing
   */
  def editFields: List[FieldPointerType] = List(twitterAccount) 
//  def editFields: List[FieldPointerType] = List(firstName, 
//                                                lastName, 
//                                                email, 
//                                                locale, 
//                                                timezone)

  // ----------------- menus 
  override val basePath: List[String] = "user_mgt" :: Nil
  
  // Only login functionality is needed.
  override def logoutMenuLoc: Box[Menu] = Empty
  override def createUserMenuLoc: Box[Menu] = Empty
  override def lostPasswordMenuLoc: Box[Menu] = Empty
  override def resetPasswordMenuLoc: Box[Menu] = Empty
  override def editUserMenuLoc: Box[Menu] = Empty
  override def changePasswordMenuLoc: Box[Menu] = Empty
  override def validateUserMenuLoc: Box[Menu] = Empty

//  override lazy val ItemList: List[MenuItem] =
//    List(MenuItem(S.??("log.in"), loginPath, false))

  
  // ----------------- Methods related to authentication
//  def loggedIn_? = {
//    if(!currentUserId.isDefined)
//      for(f <- autologinFunc) f()
//    currentUserId.isDefined
//  }
  
  /**
   * How do we prompt the user for the username.  By default,
   * it's S.??("email.address"), you can can change it to something else
   * TODO Add a text to the property files.
   */
  override def userNameFieldString: String = S.??("twitter.account")
  
  override def loginXhtml = {
    (<form method="post" action={S.uri}><table><tr><td
              colspan="2">{S.??("log.in")}</td></tr>
          <tr><td>{userNameFieldString}</td><td><user:email /></td></tr>
          <tr><td colspan="2"><user:submit /></td></tr></table>
     </form>)
  }

  import code.lib.TwitterOAuth
  override def login = {
    if (S.post_?) {
      val url = TwitterOAuth.authorizeUrl
      S.redirectTo(url.toString())
      
//      S.param("username").
//      flatMap(username => findUserByUserName(username)) match {
//        case Full(user) if user.validated_? &&
//          user.testPassword(S.param("password")) => {
//            logUserIn(user, () => {
//              S.notice(S.??("logged.in"))
//
//              val redir = loginRedirect.is match {
//                case Full(url) =>
//                  loginRedirect(Empty)
//                url
//                case _ =>
//                  homePage
//              }
//              S.redirectTo(redir)
//            })
//          }
//
//        case Full(user) if !user.validated_? =>
//          S.error(S.??("account.validation.error"))
//
//        case _ => S.error(S.??("invalid.credentials"))
//      }
    } //if
    
    
    bind("user", loginXhtml,
         "email" -> (<input type="text" name="username"/>),
         "submit" -> (<input type="submit" value={S.??("log.in")}/>))
  }


}