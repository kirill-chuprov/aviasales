package com.aviasales.task.ui.destination

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aviasales.task.R
import com.aviasales.task.ui.destination.ChooseDestinationStateIntent.GoToMaps
import com.aviasales.task.ui.destination.HomeAdapter.TownViewHolder
import com.aviasales.task.ui.destination.ItemState.ItemTown
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit.MILLISECONDS

/**
 * Created by Kirill Chuprov on 5/16/19.
 */
internal class HomeAdapter(private val eventPublisher: PublishSubject<ChooseDestinationStateIntent>) :
  ListAdapter<ItemTown, TownViewHolder>(CATEGORY_COMPARATOR) {

  private lateinit var inflater: LayoutInflater
  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): TownViewHolder {
    if (!::inflater.isInitialized) inflater = LayoutInflater.from(parent.context)
    return TownViewHolder(DataBindingUtil.inflate(inflater, viewType, parent, false))
  }

  override fun getItemViewType(position: Int): Int = R.layout.item_category

  override fun onBindViewHolder(holder: TownViewHolder, position: Int) =
    holder.bind(getItem(position))

  private companion object {

    private val CATEGORY_COMPARATOR = object : DiffUtil.ItemCallback<ItemTown>() {

      override fun areItemsTheSame(oldItem: ItemTown, newItem: ItemTown): Boolean =
        oldItem.id == newItem.id

      override fun areContentsTheSame(oldItem: ItemTown, newItem: ItemTown): Boolean =
        oldItem == newItem
    }
  }

  inner class TownViewHolder(val binding: ViewDataBinding) : ViewHolder(binding.root) {

    init {
      binding.root.clicks()
        .skip(300, MILLISECONDS)
        .map {
          GoToMaps(
            (getItem(adapterPosition) as ItemTown).id,

          )
        }
        .subscribe(eventPublisher)
    }

    fun bind(data: ItemState) {
      if (binding is ItemCategoryBinding) {
        with(binding) {
          val itemCategory = getItem(adapterPosition) as ItemCategory
          when {
            itemCategory.isFree -> openLesson(this)
            itemCategory.isPaid -> openLesson(this)
            else -> closeLesson(this)
          }

          roundedCorners = 8
          imgResId = R.drawable.lock
        }

      }

      if (binding.setVariable(BR.viewState, data)) binding.executePendingBindings()
    }
  }
}