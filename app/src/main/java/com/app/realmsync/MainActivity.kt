package com.app.realmsync

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.kotlin.where
import io.realm.mongodb.Credentials
import io.realm.mongodb.User
import io.realm.mongodb.sync.SyncConfiguration

class MainActivity : AppCompatActivity(), RealmChangeListener<RealmResults<Note>> {
    private lateinit var realm: Realm
    private var user: User? = null
    private val app = AppApplication.getInstance().getRealmApp()

    private var rv: RecyclerView? = null
    private var edtNote: EditText? = null
    private var btnPost: Button? = null

    private val noteList = mutableListOf<Note>()
    private var adapter: NoteListAdapter? = null

    private var results: RealmResults<Note>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    private fun initUI() {
        rv = findViewById(R.id.rv)
        edtNote = findViewById(R.id.edtNote)
        btnPost = findViewById(R.id.btnPost)

        rv?.layoutManager = LinearLayoutManager(this)
        adapter = NoteListAdapter(noteList)
        rv?.adapter = adapter


        btnPost?.setOnClickListener {
            val text = edtNote?.text.toString()

            if (!text.isNullOrEmpty()) {
                val note = Note(text, System.currentTimeMillis(), USER_ID)
//                noteList.add(note)
//                adapter?.notifyDataSetChanged()
                //all realm writes needs to occur inside of a transaction
                if (realm != null) {
                    realm.executeTransactionAsync { realm ->
                        realm.insert(note)
                    }
                } else {
                    Toast.makeText(this, "Realm is null", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val cred = Credentials.anonymous()
        app.loginAsync(cred) {
            if (it.isSuccess) {
                Toast.makeText(this, "Successfully authenticated", Toast.LENGTH_SHORT).show()
                initRealm()
            } else {
                Toast.makeText(
                    this,
                    "Failed to Log in to MongoDB Realm: \${it.error.errorMessage",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initRealm() {
        user = app.currentUser()
        if (user != null) {
            //configure realm and use the current user and the public partition
            val config = SyncConfiguration.Builder(user, USER_ID)
                .waitForInitialRemoteData()
                .build()

            //It is recommended to get a Realm Instance asynchronously
            Realm.getInstanceAsync(config, object : Realm.Callback() {
                override fun onSuccess(_realm: Realm) {
                    // since this realm should live exactly as long as this activity, assign the realm to a member variable
                    realm = _realm
                    fetchNotes()
                }
            })
        }
    }

    private fun fetchNotes() {
        results = realm.where<Note>().sort("date").findAllAsync()
        results?.addChangeListener(this)
//        noteList.addAll(results)
//        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        results?.removeChangeListener(this)
        results = null
        realm?.close()
    }

    override fun onChange(t: RealmResults<Note>) {
        if (t.isLoaded) {
            noteList.clear()
            noteList.addAll(t)
            adapter?.notifyDataSetChanged()
        }
    }
}