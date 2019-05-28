package com.aviasales.task.ui.destinationsearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aviasales.task.BR
import com.aviasales.task.R
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.SelectCityFrom
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.SelectCityTo
import com.aviasales.task.ui.destination.ItemState
import com.aviasales.task.ui.destination.ItemState.ItemCity
import com.aviasales.task.ui.destinationsearch.DestinationAdapter.TownViewHolder
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Created by Kirill Chuprov on 5/16/19.
 */

internal class DestinationAdapter(
  private val eventPublisher: PublishSubject<ChooseDestinationStateIntent>,
  val type: Int, val destinationBottomSheetDialogFragment: DestinationBottomSheetDialogFragment
) :
  ListAdapter<ItemCity, TownViewHolder>(CATEGORY_COMPARATOR) {

  private lateinit var inflater: LayoutInflater
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): TownViewHolder {
    if (!::inflater.isInitialized) inflater = LayoutInflater.from(parent.context)
    return TownViewHolder(DataBindingUtil.inflate(inflater, viewType, parent, false))
  }

  override fun getItemViewType(position: Int): Int = R.layout.item_city

  override fun onBindViewHolder(holder: TownViewHolder, position: Int) =
    holder.bind(getItem(position))

  private companion object {

    private val CATEGORY_COMPARATOR = object : DiffUtil.ItemCallback<ItemCity>() {

      override fun areItemsTheSame(oldItem: ItemCity, newItem: ItemCity): Boolean =
        oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: ItemCity, newItem: ItemCity): Boolean =
        oldItem == newItem
    }
  }

  inner class TownViewHolder(private val binding: ViewDataBinding) : ViewHolder(binding.root) {

    init {
      binding.root.clicks()
        .skip(300, MILLISECONDS)
        .map {
          when (type) {
            TYPE_FROM -> SelectCityFrom((getItem(adapterPosition) as ItemCity))
            else -> SelectCityTo((getItem(adapterPosition) as ItemCity))
          }.also { destinationBottomSheetDialogFragment.dismiss() }
        }
        .subscribe(eventPublisher)
    }

    fun bind(data: ItemState) {
      if (binding.setVariable(BR.viewState, data)) binding.executePendingBindings()
    }
  }
}