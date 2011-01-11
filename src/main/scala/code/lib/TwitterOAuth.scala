package code.lib

import dispatch._
import twitter.{Auth,Status}
import oauth._
import oauth.OAuth._
//import net.liftweb.util.Log
//import net.liftweb.common.Logger

// Box, Full, Empty
import net.liftweb.common._

import code.model._

// CONSUMER_KEY and CONSUMER_SECRET
import code.lib.TwitterAppSettings._

object TwitterOAuth {
  val CONSUMER = Consumer(CONSUMER_KEY, CONSUMER_SECRET)


  /**
   * 
   */
  def authorizeUrl() = {
    val http = new Http
    val reqToken = http(Auth.request_token(CONSUMER, REDIRECT_URL))
    println("req_token =" + reqToken)
    Auth.authorize_url(reqToken).to_uri
  }

  /**
   * Sends the given request token to Twitter, gets an access token,
   * and saves it in the database.
   * @param oauth_token_s
   * @param oauth_verifier
   */
  def getAndSaveToken(oauthToken:String, oauthVerifier: String):Unit = {
    val (accessToken: Token, userId: String, screenName: String)
      = getAccessToken(oauthToken, oauthVerifier)
    println("ACCESS_TOKEN value: "  + accessToken.value)
    println("ACCESS_TOKEN secret: " + accessToken.secret)
    println("user_id: "     + userId)
    println("screen_name: " + screenName)

    User.currentUser match {
      case Full(user) => {
        //Save the token.
        user.accessToken.set(accessToken.value)
        user.accessTokenSecret.set(accessToken.secret)
	    user.save()
      }
      case _ => println("User must be logged in.")
    }
  }
  
  /**
   * @param
   * @return (AccessToken, User ID, Screen Name)
   */
  def getAccessToken(oauthToken:String, oauthVerifier: String): Tuple3[Token, String, String] = {
    val http = new Http
    val oauthTokenObj = new Token(oauthToken, oauthVerifier)
    http(Auth.access_token(CONSUMER, oauthTokenObj, oauthVerifier))
  }

  /**
   * Tweets (updates the "status") using the credential of
   * the given user.
   *
   */
  def tweet(status: String, user: User): Unit = {
    if (user.accessToken.get.length() == 0) {
      //TODO redirect to the authorize page.
      println("The user doesn't have an access token.")
      return
    }
    
    // Construct a token
    val accessToken = new Token(user.accessToken, user.accessTokenSecret)
    tweet(status, accessToken)
  }

  /**
   * Tweets using the given access_token.
   */
  def tweet(status: String, accessToken: Token): Unit = {
    val http = new Http

    //TODO Move these to somewhere
    val to = "@shibuyascala"
    val hashtag = "#rpscala009"

    var out_status = to + " " + hashtag + " " + status
    if (out_status.length > 140) out_status = out_status.substring(0, 139)

    val req = Status.update(out_status, CONSUMER, accessToken)
    // Dispatch is difficult to read... What's >| ??
    // http://www.scala-lang.org/node/1981
    val res = http(req >|)
    //println(res)
  }
}
