package br.com.fomezero.joaofood.activities.ong

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import br.com.fomezero.joaofood.R
import br.com.fomezero.joaofood.activities.ActiveUserData
import br.com.fomezero.joaofood.model.OngData
import br.com.fomezero.joaofood.util.loadImage
import br.com.fomezero.joaofood.model.Product
import com.google.android.material.button.MaterialButton
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDate
import java.time.format.DateTimeFormatter

class ProductListAdapter(
    private val context: Context,
    private val productList: List<Product>,
    private val loadFragment: (Fragment) -> Unit
) : RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {


    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val product = productList[position]
        val dateorg = LocalDate.parse(product.postDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        val curdate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")).toString()
        val curdat1 = LocalDate.parse(curdate, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
        Log.d("AshDate", "Curdate: "+curdat1+"  "+dateorg.plusDays(2))
        holder.name.text = product.name
        holder.postDate.text = product.postDate
        if(product.price == "0.0"){
            holder.price.text ="Free"
        }
        else{
            holder.price.text = context.getString(R.string.price_template, product.price)
        }

        holder.amount.text = product.amount
        holder.image.loadImage(product.imageUrl, CircularProgressDrawable(context))
        if(dateorg.plusDays(2) >= curdat1){
            if(ActiveUserData.data?.getBoolean("isApproved") == true){
                holder.button.setOnClickListener {
                    loadFragment(MerchantInfoFragment(productList[position]))
                }
            }
            else{
                holder.button.setOnClickListener {
                    Toast.makeText(context, "Your Approval is Pending", Toast.LENGTH_LONG).show()
                }
            }
        }
        else{
            holder.button.setOnClickListener {
                Toast.makeText(context, "Is has Expired or Soldout", Toast.LENGTH_LONG).show()
            }
            holder.button.text = "Expired"
            holder.button.textSize = 10F
            holder.button.setBackgroundColor(Color.parseColor("#ff0000"))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.food_card_list, parent, false)
        return MyViewHolder(itemView)
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var price: TextView = view.findViewById(R.id.price)
        var amount: TextView = view.findViewById(R.id.amount)
        var image: ImageView = view.findViewById(R.id.image)
        var postDate: TextView = view.findViewById(R.id.postDate)
        var button: MaterialButton = view.findViewById(R.id.submitProductButton)
    }
}
