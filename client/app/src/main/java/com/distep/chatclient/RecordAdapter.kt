package com.distep.chatclient

import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.distep.chatclient.data.entity.Message
import java.time.format.DateTimeFormatter


class RecordAdapter() :
    PagedListAdapter<Message, RecordAdapter.CouponViewHolder>(
        COUPON_COMPARATOR
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponViewHolder {
        val context = ContextThemeWrapper(
            parent.context,
            viewType
        )

        val view: View = LayoutInflater.from(context)
            .inflate(R.layout.message_item_layout, parent, false)

        return CouponViewHolder(view, context)
    }

    override fun onBindViewHolder(holder: CouponViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    public override fun getItem(position: Int): Message? {
        return super.getItem(position)
    }

    companion object {
        val COUPON_COMPARATOR = object : DiffUtil.ItemCallback<Message>() {
            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
                oldItem.id == newItem.id
        }
    }

    inner class CouponViewHolder(itemView: View, val context: ContextThemeWrapper) :
        RecyclerView.ViewHolder(
            itemView
        ) {

        private val dateView: TextView = itemView.findViewById(R.id.datetime)
        private val textView: TextView = itemView.findViewById(R.id.message_text)
        private val authorView: TextView = itemView.findViewById(R.id.author)
        private val formLayout: ConstraintLayout = itemView.findViewById(R.id.form_layout)

        fun bind(record: Message?) {
            if (record != null) {
                textView.text = record.text
                authorView.text = record.author
                dateView.text = record.datetime.format(DateTimeFormatter.ofPattern("HH:mm"))
                if(record.author == "Me") {
                    formLayout.background = getDrawable(context, R.drawable.background_my_message)

                    formLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        startToStart = ConstraintLayout.LayoutParams.UNSET
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }
        }
    }
}