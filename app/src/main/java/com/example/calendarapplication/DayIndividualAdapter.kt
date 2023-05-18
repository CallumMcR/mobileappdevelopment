import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calendarapplication.DBHandler
import com.example.calendarapplication.DayAdapter
import com.example.calendarapplication.R

class DayIndividualAdapter(
    private val context: Context,
    private val events: ArrayList<DayAdapter.Event>
) : RecyclerView.Adapter<DayIndividualAdapter.ViewHolder>() {

    private val dbHandler: DBHandler = DBHandler(context)

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.day_individual_button, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val eventText: TextView = view.findViewById(R.id.event_textview)
        val removeButton: ImageButton = view.findViewById(R.id.remove_button)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]
        holder.eventText.text = "${event.time}: ${event.description}"
        holder.removeButton.setOnClickListener {
            dbHandler.removeEvent(event.ID)
            events.removeAt(position) // Remove the item from the list
            notifyItemRemoved(position) // Notify the adapter that an item has been removed
        }
    }

}
