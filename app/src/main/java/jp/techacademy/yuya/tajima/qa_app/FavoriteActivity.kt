package jp.techacademy.yuya.tajima.qa_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.content_main.*

class FavoriteActivity : AppCompatActivity() {

    private lateinit var mFavoriteArrayList: ArrayList<Favorite>
    private lateinit var mAdapter: FavoriteAdapter

    private var mFavoriteRef: DatabaseReference? = null

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            for (key in map.keys) {
                val temp = map[key] as Map<String, String>
                val title = temp["title"] ?: ""
                val name = temp["name"] ?: ""
                val imageString = temp["image"] ?: ""
                val bytes =
                    if (imageString.isNotEmpty()) {
                        Base64.decode(imageString, Base64.DEFAULT)
                    } else {
                        byteArrayOf()
                    }
                val favorite = Favorite(title, name, bytes)
                mFavoriteArrayList.add(favorite)
            }

            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }

    override fun onResume() {
        super.onResume()
        mFavoriteArrayList.clear()
        mAdapter.setFavoriteArrayList(mFavoriteArrayList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        title = getString(R.string.menu_favorite_label)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val dataBaseReference = FirebaseDatabase.getInstance().reference
            mFavoriteRef = dataBaseReference.child(UsersPATH).child(user.uid).child(FavoritePATH)
            mFavoriteRef!!.addChildEventListener(mEventListener)
        }

        mAdapter = FavoriteAdapter(this)
        listView.adapter = mAdapter
        mFavoriteArrayList = ArrayList<Favorite>()
    }
}