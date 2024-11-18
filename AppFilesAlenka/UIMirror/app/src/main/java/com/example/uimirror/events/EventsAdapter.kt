package com.example.uimirror.events

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uimirror.R
import com.example.uimirror.database.models.Event
import java.text.SimpleDateFormat

class EventsAdapter(private val mList: MutableList<Event>, private val eventsClickInterface: EventsClickInterface)
    : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {


    interface EventsClickInterface {
        fun onDeleteEventClicked(event: Event)
    }


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)

        return ViewHolder(view)
    }

    fun updateList(list: List<Event>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = mList[position]
        holder.tvEventName.text = event.eventName
        holder.tvTime.text = readDate(event.dateTime)
        holder.tvDeleteEvent.setOnClickListener {
            eventsClickInterface.onDeleteEventClicked(event)
        }
    }

    private fun readDate(time: Long): String? {
        val formatter = SimpleDateFormat("dd MMM yyyy\nhh:mm a")
        return formatter.format(time)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDeleteEvent: TextView = itemView.findViewById(R.id.tvDeleteEvent)
        val tvEventName: TextView = itemView.findViewById(R.id.tvEventName)
    }
}