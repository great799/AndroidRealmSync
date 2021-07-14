package com.app.realmsync

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class Note(
    var noteName: String= "",
    var date: Long = 0L,
    var _userId: String = USER_ID//"Public"
): RealmObject(){

    @PrimaryKey
    var _id: ObjectId = ObjectId()

}