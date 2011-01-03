package code.model

import net.liftweb._
import common._;
import http._;
import mapper._
import sitemap._;
import Loc._;

trait AdminEditable [KeyType, CrudType <: KeyedMapper[KeyType, CrudType]]
extends CRUDify [KeyType, CrudType] {
  self: CrudType with KeyedMetaMapper[KeyType, CrudType] =>  

  // super userかどうかを判別する
  val superUser_? = If(() => User.currentUser match {
                               case Full(user) => user.superUser;
                               case _ => false} ,
                       () => RedirectResponse("/user_mgt/login"));

  // super userだけしかCURDは使えなくする（viewは除く）
  override def showAllMenuLocParams: List[Loc.AnyLocParam] = List(superUser_?);
//  override def viewMenuLocParams: List[Loc.AnyLocParam] = List(superUser_?)
  override def createMenuLocParams: List[Loc.AnyLocParam] = List(superUser_?);
  override def editMenuLocParams: List[Loc.AnyLocParam] = List(superUser_?);
  // deleteは機能まるごと廃止
  override def deleteMenuLoc: Box[Menu] = Empty;
//  override def deleteMenuLocParams: List[Loc.AnyLocParam] = List(superUser_?);

}