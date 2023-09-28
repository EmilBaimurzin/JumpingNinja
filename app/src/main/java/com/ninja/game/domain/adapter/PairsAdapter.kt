package com.ninja.game.domain.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.ninja.game.R
import com.ninja.game.databinding.ItemPairBinding
import com.ninja.game.domain.ninja_pairs.NinjaPairsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PairsAdapter(private var onItemClick: ((Int) -> Unit)? = null) :
    RecyclerView.Adapter<PairsViewHolder>() {
    var items = mutableListOf<NinjaPairsItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        return PairsViewHolder(
            ItemPairBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        holder.bind(items[position])
        holder.onItemClick = onItemClick
    }
}

class PairsViewHolder(private val binding: ItemPairBinding) :
    RecyclerView.ViewHolder(binding.root) {
    var onItemClick: ((Int) -> Unit)? = null
    fun bind(item: NinjaPairsItem) {
        binding.apply {
            val img = when (item.value) {
                1 -> R.drawable.symbol_01
                2 -> R.drawable.symbol_02
                3 -> R.drawable.symbol_03
                4 -> R.drawable.symbol_04
                5 -> R.drawable.symbol_05
                6 -> R.drawable.symbol_06
                7 -> R.drawable.symbol_07
                8 -> R.drawable.symbol_08
                9 -> R.drawable.symbol_09
                10 -> R.drawable.symbol_10
                11 -> R.drawable.symbol_11
                12 -> R.drawable.symbol_12
                13 -> R.drawable.symbol_13
                14 -> R.drawable.symbol_14
                else -> R.drawable.symbol_15
            }
            if (item.isOpened) {
                itemImg.setImageResource(img)
            } else {
                itemImg.setImageResource(R.drawable.symbol_back)
            }
            if (item.openAnimation) {
                flipImage(binding.root, img, R.drawable.symbol_back)
            }

            if (item.closeAnimation) {
                flipImage(binding.root, null, R.drawable.symbol_back)
            }

            binding.root.setOnClickListener {
                if (!item.openAnimation && !item.closeAnimation && !item.isOpened) {
                    onItemClick?.invoke(adapterPosition)
                }
            }
        }
    }

    private fun flipImage(
        view: View,
        @DrawableRes img: Int?,
        @DrawableRes imgBox: Int,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            if (img != null) {
                binding.itemImg.setImageResource(img)
            } else {
                binding.itemImg.setImageResource(imgBox)
            }
        }
        val animatorSet = AnimatorSet()
        val rotateAnimator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f)
        rotateAnimator.duration = 400

        val scaleXAnimator = ValueAnimator.ofFloat(1f, -1f)
        scaleXAnimator.addUpdateListener { animator ->
            val scale = animator.animatedValue as Float
            view.scaleX = scale
        }
        scaleXAnimator.duration = 400

        animatorSet.playTogether(rotateAnimator, scaleXAnimator)
        animatorSet.start()
    }

}