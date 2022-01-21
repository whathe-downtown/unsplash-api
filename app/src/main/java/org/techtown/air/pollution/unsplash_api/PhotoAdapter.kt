package org.techtown.air.pollution.unsplash_api

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.techtown.air.pollution.unsplash_api.data.models.PhotoResponse
import org.techtown.air.pollution.unsplash_api.databinding.ItemPhotoBinding
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.ViewHolder>() {

    var photos: List<PhotoResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size


    class ViewHolder(
        private val binding: ItemPhotoBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(photo: PhotoResponse){
            val dimensionRation= photo.height/photo.width.toFloat()  // height width nullable wprj
            val targetWidth = binding.root.resources.displayMetrics.widthPixels -
                    (binding.root.paddingStart + binding.root.paddingEnd)

            val targetHeight = (targetWidth * dimensionRation).toInt()

            binding.contentsContainer.layoutParams =
                binding.contentsContainer.layoutParams.apply {
                    height = targetHeight
                }

            Glide.with(binding.root)
                .load(photo.urls?.regular)
                .thumbnail(
                    Glide.with(binding.root)
                        .load(photo.urls?.thumb)
                        .transition(DrawableTransitionOptions.withCrossFade())
                )
                .override(targetWidth, targetHeight)
                .into(binding.photoImageView)

            Glide.with(binding.root)
                .load(photo.user?.profileImageUrls?.small)
                .placeholder(R.drawable.shape_profile_placeholder)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.profileImageView)

            if(photo.user?.name.isNullOrBlank()){
                binding.authorTextView.visibility =  View.GONE

            }else{
                binding.authorTextView.visibility =  View.VISIBLE
                binding.authorTextView.text= photo.user?.name

            }
            if(photo.description.isNullOrBlank()){
                binding.descriptionTextView.visibility = View.GONE
            } else{
                binding.descriptionTextView.visibility = View.VISIBLE
                binding.descriptionTextView.text =photo.description
            }
            }


    }
}