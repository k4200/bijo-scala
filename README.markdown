日本語テキストは後ろの方

This small web application is "inspired" by [Bijo-Linux](http://bijo-linux.com/),
which  shows a girl's photo with a Linux command, which (I believe)
changes every day.

I started it just for fun and my learning of Scala and Lift.

Although it's small, it has some features that not many people
have done in Scala and Lift, such as image upload, CRUD, and so on.
It also uses some new features introduced in Lift 2.2, such as CSS
selectors and ... what else? So, it might be of some use for newbies.

This is a sbt project, which you should be used to if you're a
Scala programmer, and also an Eclipse project. .project was made
by a cool program called SbtEclipsify.  
https://github.com/musk/SbtEclipsify

TODO

* i18n
* Fix the relationship between girls and entries tables.

If you find any bugs, have questions/comments, feel free to conatact
me at:  
kashima [@t m@rk] shibuya [d0t] scala-users [d0t] org

# 日本語

Liftの勉強のために、[美女Linux](http://bijo-linux.com/)みたいなのを作ってみました。

Lift 2.2の新機能とかもちょこちょこ使ってます。その他、以下のような事もやってるので、
Lift初心者の方には参考になるかも。

* CRUD
* ファイルアップロード
* Twitter認証

sbtプロジェクトを[SbtEclipsify](https://github.com/musk/SbtEclipsify)
というプラグインでEclipseのプロジェクトに変換しているので、Eclipseな人にも安心。

TODO

* i18n
* girlsとentriesのリレーションの変更

以下のURLで動かしてます（落ちてる場合もあります）。  
[http://bijo.shibuya.scala-users.org:8080/](http://bijo.shibuya.scala-users.org:8080/)

ご意見等はこちらのメールアドレスまで。  
kashima [あっとまーく] shibuya [ドット] scala-users [。] org
