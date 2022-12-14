package jp.techacademy.yuya.tajima.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.list_question_detail.view.*

class QuestionDetailListAdapter(context: Context, private val mQustion: Question) : BaseAdapter() {
    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }

    private var mLayoutInflater: LayoutInflater? = null

    var isFavorite: Boolean = false

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return 1 + mQustion.answers.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_QUESTION
        } else {
            TYPE_ANSWER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mQustion
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view

        if (getItemViewType(position) == TYPE_QUESTION) {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
            }
            val body = mQustion.body
            val name = mQustion.name

            val bodyTextView = convertView.bodyTextView as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView as TextView
            nameTextView.text = name

            val bytes = mQustion.imageBytes
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }

            val favoriteImageView = convertView.findViewById<View>(R.id.favoriteImageView) as ImageView

            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                favoriteImageView.visibility = View.GONE
            } else {
                favoriteImageView.visibility = View.VISIBLE
                favoriteImageView.setOnClickListener {
                    val dataBaseReference = FirebaseDatabase.getInstance().reference
                    val favoriteRef = dataBaseReference.child(UsersPATH).child(user.uid).child(FavoritePATH).child(mQustion.questionUid)

                    if (isFavorite) {
                        favoriteRef.removeValue()
                        favoriteImageView.setImageResource(R.drawable.ic_star_border)
                        isFavorite = false
                    } else {
                        favoriteRef.push().setValue(mQustion.title)
                        favoriteImageView.setImageResource(R.drawable.ic_star)
                        isFavorite = true
                    }
                }
            }

            if (isFavorite) {
                favoriteImageView.setImageResource(R.drawable.ic_star)
            } else {
                favoriteImageView.setImageResource(R.drawable.ic_star_border)
            }

        } else {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer, parent, false)!!
            }

            val answer = mQustion.answers[position - 1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.bodyTextView as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView as TextView
            nameTextView.text = name
        }

        return convertView
    }
}