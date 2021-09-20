package com.sushi.Sushi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.sushi.Sushi.R
import com.sushi.Sushi.dialog.CountDialog
import com.sushi.Sushi.models.CatMenuModel
import com.sushi.Sushi.models.MenuModelcatMenu

class DiscountAdapter(context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mDiscountList: ArrayList<MenuModelcatMenu> = ArrayList()
    private val glide = Glide.with(context)

    fun setupDiscount(discountList: ArrayList<CatMenuModel>) {
        mDiscountList.clear()

        for (categoryModel in discountList) {

                for (i in categoryModel.Items) {
                    var menuModel = MenuModelcatMenu()
                    menuModel.Items = i.value
                    mDiscountList.add(menuModel)
                }
        }
            notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_discount_recyclerview, parent, false)

        return DiscountViewHolder(itemView = itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DiscountAdapter.DiscountViewHolder) {
            holder.bind(discountList = mDiscountList[position], position = position)
        }
    }

    override fun getItemCount(): Int {
        return mDiscountList.count()
    }

    inner class DiscountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView = itemView.findViewById(R.id.image_dish_discount_menu)
        var nameDish: TextView = itemView.findViewById(R.id.name_dish_discount_menu)
        var cost: TextView = itemView.findViewById(R.id.txt_roll_price)
        var newCost: TextView = itemView.findViewById(R.id.txt_roll_price_new_cost)
        var layout: RelativeLayout = itemView.findViewById(R.id.layout_discont)

        fun bind(discountList: MenuModelcatMenu, position: Int){

            val newCostT = discountList.Items?.NewCost

            nameDish.text = "${discountList.Items?.Name}"
            cost.text = "${discountList.Items?.Cost}" + " р."
            newCost.text = "${discountList.Items?.NewCost}" + " р."

            if (discountList.Items?.PictureForLoad == null) {
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.getReferenceFromUrl(discountList.Items?.Picture!!)
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    discountList.Items?.PictureForLoad = uri
                    val img = glide.load(uri)
                    img.diskCacheStrategy(DiskCacheStrategy.NONE)
                    img.centerCrop().into(image)
                }
            } else {
                val img = glide.load(discountList.Items?.PictureForLoad)
                img.diskCacheStrategy(DiskCacheStrategy.NONE)
                img.into(image)
            }

            if (newCostT == 9223372036854775807) {
                layout.visibility = View.GONE
                layout.layoutParams = RecyclerView.LayoutParams(0, 0)
            } else {
                layout.visibility = View.VISIBLE
                layout.layoutParams = RecyclerView.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            }

            itemView.setOnClickListener {
                CountDialog.openDialog(itemView.context, discountList, position)
            }
        }
    }
}