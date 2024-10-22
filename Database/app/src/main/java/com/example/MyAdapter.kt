package com.example

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.database.store.Person

class MyAdapter(context: Context, resource: Int, private val persons: List<Person>) :
    ArrayAdapter<Person>(context, resource, persons) {
    override fun getCount(): Int {
        return persons.size
    }

    override fun getItem(position: Int): Person {
        return persons[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
        val label = super.getView(position, convertView, parent) as android.widget.TextView
        label.text = persons[position].name
        return label
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val label = super.getView(position, convertView, parent) as android.widget.TextView
        label.text = persons[position].name
        return label
    }



}