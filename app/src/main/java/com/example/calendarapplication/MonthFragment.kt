package com.example.calendarapplication

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MonthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthFragment : Fragment() {


    interface OnMonthClickListener {
        fun onMonthClick(monthNumber: Int)
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private val months = arrayOf(
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
    )

    inner class MonthAdapter(private val months: List<String>) :
        RecyclerView.Adapter<MonthAdapter.ViewHolder>() {
        var listener: MonthFragment.OnMonthClickListener? = null


        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
            val button: Button = view.findViewById(R.id.month_button)

            init {
                button.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val month = months[adapterPosition]
                val activity = view.context as AppCompatActivity
                val fragmentManager = activity.supportFragmentManager
                val monthNumber = month.toInt()
                val daysFragment = DayFragment.newInstance(monthNumber)

                fragmentManager.beginTransaction()
                    .replace(R.id.MainBody, daysFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()

                val listener = activity as OnMonthClickListener
                Log.d("MonthFragment -Number", monthNumber.toString())
                listener.onMonthClick(monthNumber)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.month_button, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val month = months[position]
            holder.button.text = month
            holder.button.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.button_gradient)
            holder.button.background = gradients[position % gradients.size] // % is used to make sure the
            // gradient is always within range of the gradients array
            holder.button.setOnClickListener {
                val daysFragment = DayFragment()

                val bundle = Bundle()
                bundle.putInt("ARG_MONTH_NUMERIC", position+1)
                daysFragment.arguments = bundle

                val fragmentManager = requireActivity().supportFragmentManager
                val transaction = fragmentManager.beginTransaction()
                transaction.replace(R.id.MainBody, daysFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        override fun getItemCount() = months.size
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_month, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.months_list)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MonthAdapter(months.toList())


        val adapter = MonthAdapter(months.toList())
        adapter.listener = requireActivity() as OnMonthClickListener
        recyclerView.adapter = adapter
        return view

    }




    companion object {

        private val gradients = arrayOf(
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFFD4AF37.toInt(), // start color - Metallic Gold
                    0xFF8B4513.toInt() // end color - Saddle Brown
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFF6495ED.toInt(), // start color - Orchid
                    0xFF1E90FF.toInt() // end color - Dark Violet
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFFFF7F50.toInt(), // start color - Lime
                    0xFFFF4500.toInt() // end color - Green
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFF00FF7F.toInt(), // start color - Orange
                    0xFF00FA9A.toInt() // end color - Sienna
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFFFFD700.toInt(), // start color - Moccasin
                    0xFFFFA500.toInt() // end color - Light Salmon
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFFDA70D6.toInt(), // start color - Light Blue
                    0xFFBA55D3.toInt() // end color - Royal Blue
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFF9ACD32.toInt(), // start color - Fire Brick
                    0xFF6B8E23.toInt() // end color - Red
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFFDC143C.toInt(), // start color - Lavender
                    0xFFFF69B4.toInt() // end color - Dark Violet
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFF1E90FF.toInt(), // start color - Aquamarine
                    0xFF00BFFF.toInt() // end color - Teal
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFFFFA500.toInt(), // start color - Light Salmon
                    0xFFFF8C00.toInt() // end color - Dark Red
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFF8FBC8F.toInt(), // start color
                    0xFF2E8B57.toInt() // end color
                )),
            GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT, intArrayOf(
                    0xFF9932CC.toInt(), // start color
                    0xFF9400D3.toInt() // end color
                )),

            )




        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MonthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}



