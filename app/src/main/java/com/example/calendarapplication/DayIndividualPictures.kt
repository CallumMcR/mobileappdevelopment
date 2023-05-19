import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.calendarapplication.DBHandler
import com.example.calendarapplication.DayAdapter
import com.example.calendarapplication.MonthFragment
import com.example.calendarapplication.R

class DayIndividualPicturesAdapter(
        private val context: Context,
        private val photos: ArrayList<DayAdapter.Photo>
) : RecyclerView.Adapter<DayIndividualPicturesAdapter.ViewHolder>() {

        private val dbHandler: DBHandler = DBHandler(context)

        override fun getItemCount(): Int {
                return photos.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.day_individual_picture, parent, false)
                return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val photo = photos[position]
                // Load the image into the ImageView using the photo data
                // Assuming you have the actual image data (e.g., URI, file path, etc.) in `photo.pictureData`
                // Use an appropriate method to load the image into the ImageView (e.g., Glide, Picasso, etc.)
                Glide.with(context)
                        .load(photo.pictureData) // Replace `pictureData` with the appropriate field containing the image data
                        .into(holder.imageView)

                holder.deleteButton.setOnClickListener {
                        dbHandler.deletePhoto(photos[position].ID)
                        photos.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, photos.size)
                }
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
                val imageView: ImageView = view.findViewById(R.id.image_view)
                val deleteButton: ImageView = view.findViewById(R.id.delete_photo)
        }
}

