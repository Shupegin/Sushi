package com.sushi.Sushi

import android.content.Context
import android.media.effect.EffectContext
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sushi.Sushi.adapters.BasketAdapter
import com.sushi.Sushi.fragment.RegistrationFragment
import com.sushi.Sushi.listener.EventListenerss
import com.sushi.Sushi.models.MenuModelcatMenu
import com.sushi.Sushi.singleton.BasketSingleton


class BasketFragment : Fragment(), EventListenerss{

    private lateinit var basketAdapter: BasketAdapter
    private lateinit var rvBasket : RecyclerView
    private  var listmodel : ArrayList<MenuModelcatMenu> = ArrayList()
    lateinit var btnRegistr : Button
    var registrationFragment: RegistrationFragment = RegistrationFragment()
    lateinit var menuFragment: MenuFragment
    lateinit var txtHelloBasket : TextView
    lateinit var txtHelloBasket2 : TextView
    lateinit var txtPriseTotal : TextView
    lateinit var txtNewPriseTotal : TextView
    lateinit var layoutPriseTotal : LinearLayout
    lateinit var txtHeader : TextView
    lateinit var imgHello: ImageView
    lateinit var clearBasket : TextView
    private var delivery = String()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_basket, container, false)

        txtHelloBasket = root.findViewById(R.id.txt_hello_basket)
        txtHelloBasket2 = root.findViewById(R.id.txt_hello_basket2)
        imgHello = root.findViewById(R.id.img_logo_hello_basket)
        clearBasket = root.findViewById(R.id.txt_clear_basket)
        txtPriseTotal = root.findViewById(R.id.txt_prise_total_basket)
        txtNewPriseTotal = root.findViewById(R.id.txt_new_prise_total_basket)
        layoutPriseTotal = root.findViewById(R.id.layout_prise_total)
        btnRegistr = root.findViewById(R.id.btn_registratoin)
        txtHeader = root.findViewById(R.id.txt_header_basket)
        rvBasket = root.findViewById(R.id.basket_recyclerview)

        BasketSingleton.subscribe(this)

        basketAdapter = BasketAdapter()
        rvBasket.adapter = basketAdapter
        rvBasket.layoutManager = LinearLayoutManager(root.context, RecyclerView.VERTICAL, false)
        rvBasket.setHasFixedSize(true)
        listmodel = BasketSingleton.basketItem

        txtNewPriseTotal.visibility = View.GONE

        setupAdapter(listmodel)
        btnReg(root.context)
        visible()
        updateTEXT()
        clearBasket(root.context)

        return root
    }

    companion object {


        fun newInstance() = BasketFragment()

    }


    private fun setupAdapter(modelList: ArrayList<MenuModelcatMenu>) {
        rvBasket.visibility = View.VISIBLE
        basketAdapter.setupBasket(basketList = modelList)

    }

    fun btnReg (context: Context) {
            btnRegistr.setOnClickListener {
                var sum = Long.MAX_VALUE
                val costNewCostt = BasketSingleton.loadCost()
                //здесь костыль: при пустых значаниях приходят странные цифры, но они не больше 10000)
                if (costNewCostt?.toLong()!! in 1..9999) {
                    sum = BasketSingleton.countNew()
                } else{
                    sum = BasketSingleton.count()
                }

                if (sum < 1000) {
                    Toast.makeText(context, "Сумма заказа должна быть не менее 1000 рублей", Toast.LENGTH_LONG)
                        .show()


//                    openDialogDelivery(context)
//                    //передаём стоимость доставки и переходим к регистрационному фрагменту
//                    delivery = "0"
//                    val args = Bundle()
//                    args.putString("delivery", delivery)
//                    registrationFragment.arguments = args
//
//                    goRegistrationFragment(args.toString())
                }
                else {
                    //передаём стоимость доставки и переходим к регистрационному фрагменту
                    delivery = "0"
                    val args = Bundle()
                    args.putString("delivery", delivery)
                    registrationFragment.arguments = args

                    goRegistrationFragment(args.toString())
            }
        }
    }

    private fun visible() {
        if (listmodel.isNotEmpty()) {
            txtHelloBasket.visibility = View.GONE
            txtHelloBasket2.visibility = View.GONE
            imgHello.visibility = View.GONE
            txtHeader.visibility = View.VISIBLE
            layoutPriseTotal.visibility = View.VISIBLE
            btnRegistr.visibility = View.VISIBLE
            clearBasket.visibility = View.VISIBLE}

        else {
            txtHelloBasket.visibility = View.VISIBLE
            txtHelloBasket2.visibility = View.VISIBLE
            imgHello.visibility = View.VISIBLE
            layoutPriseTotal.visibility = View.INVISIBLE
            txtHeader.visibility = View.GONE
            btnRegistr.visibility = View.GONE
            clearBasket.visibility = View.GONE
        }
    }

     fun updateTEXT() {

         //итого по обычной цене
        val sum1 = BasketSingleton.count()
         //итого со скидкой
         val sum2 = BasketSingleton.countNew()
         //скидка с чека
         val newCostSum = sum1 - sum2
         //новая цена
         val newCost = BasketSingleton.loadCost()
         //если новая цена есть
         if(sum1 != sum2){
             //то показываем размер скидки
             txtNewPriseTotal.text = "Скидка составляет: $newCostSum р."
             txtPriseTotal.text = "Итого: $sum2 р."
             txtNewPriseTotal.visibility = View.VISIBLE

         } else {
             txtNewPriseTotal.visibility = View.GONE
             txtPriseTotal.text = "Итого: $sum1 р."
         }





    }

    override fun updateRR() {
        var menufile = BasketSingleton.basketItem
        basketAdapter.setupBasket(basketList = menufile)
        rvBasket.visibility = View.VISIBLE

       updateTEXT()
    }

    private fun clearBasket(context: Context){
        clearBasket.setOnClickListener {

                val clearDialog = AlertDialog.Builder(context
                )
                clearDialog.setTitle("Аннигилирование")
                clearDialog.setMessage("Очистить корзину?")
                clearDialog.setPositiveButton(
                    "Да"
                ) { _, _ ->

                    BasketSingleton.del()
                    BasketSingleton.notifyTwo()
                    visible()
                    goMenuFragment()

                }
                clearDialog.setNegativeButton(
                    "Ой, нет!"
                ) { _, _ -> }
                clearDialog.show()
        }
    }

    private fun openDialogDelivery(context: Context){
        val deliveryDialog = AlertDialog.Builder(context)
        deliveryDialog.setTitle("Доставка")
        deliveryDialog.setMessage("Бесплатная доставка при заказе на сумму от 1000 рублей.")
        deliveryDialog.setPositiveButton(
            "Дополнить заказ"
        ) { _, _ ->
            goMenuFragment()
        }
        deliveryDialog.setNegativeButton(
            "Оплатить 250 рублей за доставку"
        ) { _, _ ->

            //передаём стоимость доставки и переходим в регистрационный фрагмент
            delivery = 250.toString()
            val args = Bundle()
            args.putString("delivery", delivery)
            registrationFragment.arguments = args

            Log.d("delivery", "delivery = $delivery")

            goRegistrationFragment(args.toString())
        }
        deliveryDialog.show()
    }

    private fun goMenuFragment(){

//        val manager = (activity as AppCompatActivity).supportFragmentManager
//        menuFragment = MenuFragment()
//        manager.beginTransaction()
//            .replace(R.id.frame_layout, menuFragment)
//            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            .commit()
    }

    private fun goRegistrationFragment(args: String){
        val manager = (activity as AppCompatActivity).supportFragmentManager
        manager.beginTransaction()
            .replace(R.id.frame_layout, registrationFragment, args)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}