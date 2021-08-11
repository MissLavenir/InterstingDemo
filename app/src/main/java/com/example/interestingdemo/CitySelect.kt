package com.example.interestingdemo

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.interestingdemo.model.PlaceModel
import kotlinx.android.synthetic.main.dialog_sure_btn.view.*
import kotlinx.android.synthetic.main.fragment_city_select.*
import kotlinx.android.synthetic.main.item_single_textview.view.*


class CitySelect : Fragment() {
    private val allCity = getCity()
    private var cityModels = ArrayList<PlaceModel>()
    private val adapter = CityAdapter(cityModels)
    private val areaList = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_city_select, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        cityRecyclerView.adapter = adapter
        cityRecyclerView.layoutManager = layoutManager

        resetSelect()

        selectFinish.setOnClickListener {
            var nameString = ""
            if (areaList.isNotEmpty()){
                areaList.forEach {
                    nameString += "${it}-"
                }
            }else {
                nameString = "未选择。"
            }
            val dialog = LayoutInflater.from(context).inflate(R.layout.dialog_sure_btn, null, false)
            val alert = AlertDialog.Builder(context).setView(dialog).create()
            dialog.sureTitle.text = "选择结果"
            dialog.sureMessage.text = "您选择的地区为：\n  ${nameString.dropLast(1)}"
            dialog.sureBtn.setOnClickListener {
                alert.dismiss()
            }
            dialog.cancelBtn.visibility = View.GONE
            alert.show()
        }

    }


    fun selectNext(parent : Int) {
        cityModels.clear()
        allCity.filter { it.parentId == parent }.forEach { model ->
            cityModels.add(model)
        }
        adapter.notifyDataSetChanged()
    }

    private fun resetSelect(){
        areaList.clear()
        selectNext(0)
    }

    /**
     * @param status 状态0添加，1是修改
     */
    fun navigationTab(model : PlaceModel, status : Int){
        when(status){
            0 -> {
                val textTab = LayoutInflater.from(context).inflate(R.layout.item_single_textview, selectTab, false)
                textTab.textViewName.text = model.name
                textTab.textViewName.setTextColor(ResourcesCompat.getColor(resources, R.color.blue_400, context?.theme))
                textTab.textViewName.setOnClickListener {
                    selectNext(model.parentId ?: 0)
                    selectPlease.visibility = View.VISIBLE
                    val selectSize = selectTab.size -1
                    for (i in selectSize downTo 0){
                        if (i > (model.type ?: 0) -2){
                            selectTab.removeViewAt(i)
                            areaList.removeAt(i)
                        }
                    }
                }
                areaList.add(model.name ?: "")
                selectTab.addView(textTab)

            }
            1 -> {
                selectTab.removeViewAt((model.type ?: 0) -1)
                areaList.removeAt((model.type ?: 0) - 1)
                navigationTab(model,0)
            }
        }
    }

    inner class CityAdapter(private val array : ArrayList<PlaceModel>) : RecyclerView.Adapter<CityAdapter.MyViewHolder>(){

        inner class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
            val cityName: AppCompatTextView = itemView.findViewById(R.id.textViewName)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_single_textview, parent, false))
        }

        override fun getItemCount(): Int = array.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.cityName.text = array[position].name
            holder.itemView.setOnClickListener {
                if (array[position].hasChild == 1){
                    navigationTab(array[position], 0)
                    selectNext(array[position].id ?: 0)
                } else {
                    if (areaList.size < array[position].type ?: 0){
                        navigationTab(array[position],0)
                    }else{
                        navigationTab(array[position], 1)
                    }
                    selectPlease.visibility = View.GONE
                }
            }
        }
    }

    //暂时先用这些数据，数据应该可以从某个网上下载导入，然后根据数据源创建Model
    private fun getCity() : ArrayList<PlaceModel>{
        val cityList =  ArrayList<PlaceModel>()
        val model1 = PlaceModel().apply { id = 1;name = "江苏省";type = 1;parentId = 0;hasChild = 1 }
        val model2 = PlaceModel().apply { id = 2;name = "河南省";type = 1;parentId = 0;hasChild = 1 }
        val model3 = PlaceModel().apply { id = 3;name = "山东省";type = 1;parentId = 0;hasChild = 1 }
        val model4 = PlaceModel().apply { id = 4;name = "苏州";type = 2;parentId = 1;hasChild = 1 }
        val model5 = PlaceModel().apply { id = 5;name = "南京";type = 2;parentId = 1;hasChild = 1 }
        val model6 = PlaceModel().apply { id = 6;name = "盐城";type = 2;parentId = 1;hasChild = 1 }
        val model7 = PlaceModel().apply { id = 7;name = "开封";type = 2;parentId = 2;hasChild = 1 }
        val model8 = PlaceModel().apply { id = 8;name = "郑州";type = 2;parentId = 2;hasChild = 1 }
        val model9 = PlaceModel().apply { id = 9;name = "商丘";type = 2;parentId = 2;hasChild = 1 }
        val model10 = PlaceModel().apply { id = 10;name = "青岛";type = 2;parentId = 3;hasChild = 1 }
        val model11 = PlaceModel().apply { id = 11;name = "济南";type = 2;parentId = 3;hasChild = 1 }
        val model12 = PlaceModel().apply { id = 12;name = "烟台";type = 2;parentId = 3;hasChild = 1 }
        val model13 = PlaceModel().apply { id = 13;name = "虎丘区";type = 3;parentId = 4;hasChild = 0 }
        val model14 = PlaceModel().apply { id = 14;name = "吴中区";type = 3;parentId = 4;hasChild = 0 }
        val model15 = PlaceModel().apply { id = 15;name = "相城区";type = 3;parentId = 4;hasChild = 0 }
        val model16 = PlaceModel().apply { id = 16;name = "姑苏区";type = 3;parentId = 4;hasChild = 0 }
        val model17 = PlaceModel().apply { id = 17;name = "吴江区";type = 3;parentId = 4;hasChild = 0 }
        val model18 = PlaceModel().apply { id = 18;name = "常熟市";type = 3;parentId = 4;hasChild = 0 }
        val model19 = PlaceModel().apply { id = 19;name = "张家港市";type = 3;parentId = 4;hasChild = 0 }
        val model20 = PlaceModel().apply { id = 20;name = "昆山市";type = 3;parentId = 4;hasChild = 0 }
        val model21 = PlaceModel().apply { id = 21;name = "太仓市";type = 3;parentId = 4;hasChild = 0 }
        val model22 = PlaceModel().apply { id = 22;name = "玄武区";type = 3;parentId = 5;hasChild = 0 }
        val model23 = PlaceModel().apply { id = 23;name = "秦淮区";type = 3;parentId = 5;hasChild = 0 }
        val model24 = PlaceModel().apply { id = 24;name = "建邺区";type = 3;parentId = 5;hasChild = 0 }
        val model25 = PlaceModel().apply { id = 25;name = "鼓楼区";type = 3;parentId = 5;hasChild = 0 }
        val model26 = PlaceModel().apply { id = 26;name = "浦口区";type = 3;parentId = 5;hasChild = 0 }
        val model27 = PlaceModel().apply { id = 27;name = "栖霞区";type = 3;parentId = 5;hasChild = 0 }
        val model28 = PlaceModel().apply { id = 28;name = "雨花台区";type = 3;parentId = 5;hasChild = 0 }
        val model29 = PlaceModel().apply { id = 29;name = "江宁区";type = 3;parentId = 5;hasChild = 0 }
        val model30 = PlaceModel().apply { id = 30;name = "亭湖区";type = 3;parentId = 6;hasChild = 0 }
        val model31 = PlaceModel().apply { id = 31;name = "盐都区";type = 3;parentId = 6;hasChild = 0 }
        val model32 = PlaceModel().apply { id = 32;name = "大丰区";type = 3;parentId = 6;hasChild = 0 }
        val model33 = PlaceModel().apply { id = 33;name = "响水县";type = 3;parentId = 6;hasChild = 0 }
        val model34 = PlaceModel().apply { id = 34;name = "滨海区";type = 3;parentId = 6;hasChild = 0 }
        val model35 = PlaceModel().apply { id = 35;name = "阜宁区";type = 3;parentId = 6;hasChild = 0 }
        val model36 = PlaceModel().apply { id = 36;name = "射阳县";type = 3;parentId = 6;hasChild = 0 }
        val model37 = PlaceModel().apply { id = 37;name = "建湖县";type = 3;parentId = 6;hasChild = 0 }
        val model38 = PlaceModel().apply { id = 38;name = "东台市";type = 3;parentId = 6;hasChild = 0 }
        val model39 = PlaceModel().apply { id = 39;name = "龙亭区";type = 3;parentId = 7;hasChild = 0 }
        val model40 = PlaceModel().apply { id = 40;name = "通许县";type = 3;parentId = 7;hasChild = 0 }
        val model41 = PlaceModel().apply { id = 41;name = "金明区";type = 3;parentId = 7;hasChild = 0 }
        val model42 = PlaceModel().apply { id = 42;name = "尉氏县";type = 3;parentId = 7;hasChild = 0 }
        val model43 = PlaceModel().apply { id = 43;name = "兰考县";type = 3;parentId = 7;hasChild = 0 }
        val model44 = PlaceModel().apply { id = 44;name = "中原区";type = 3;parentId = 8;hasChild = 0 }
        val model45 = PlaceModel().apply { id = 45;name = "上街区";type = 3;parentId = 8;hasChild = 0 }
        val model46 = PlaceModel().apply { id = 46;name = "金水区";type = 3;parentId = 8;hasChild = 0 }
        val model47 = PlaceModel().apply { id = 47;name = "新郑区";type = 3;parentId = 8;hasChild = 0 }
        val model48 = PlaceModel().apply { id = 48;name = "惠济区";type = 3;parentId = 8;hasChild = 0 }
        val model49 = PlaceModel().apply { id = 49;name = "登封区";type = 3;parentId = 8;hasChild = 0 }
        val model50 = PlaceModel().apply { id = 50;name = "梁园区";type = 3;parentId = 9;hasChild = 0 }
        val model51 = PlaceModel().apply { id = 51;name = "虞城区";type = 3;parentId = 9;hasChild = 0 }
        val model52 = PlaceModel().apply { id = 52;name = "永城区";type = 3;parentId = 9;hasChild = 0 }
        val model53 = PlaceModel().apply { id = 53;name = "民权县";type = 3;parentId = 9;hasChild = 0 }
        val model54 = PlaceModel().apply { id = 54;name = "李沧区";type = 3;parentId = 10;hasChild = 0 }
        val model55 = PlaceModel().apply { id = 55;name = "历下区";type = 3;parentId = 11;hasChild = 0 }
        val model56 = PlaceModel().apply { id = 56;name = "蓬莱市";type = 3;parentId = 12;hasChild = 0 }


        cityList.add(model1)
        cityList.add(model2)
        cityList.add(model3)
        cityList.add(model4)
        cityList.add(model5)
        cityList.add(model6)
        cityList.add(model7)
        cityList.add(model8)
        cityList.add(model9)
        cityList.add(model10)
        cityList.add(model11)
        cityList.add(model12)
        cityList.add(model13)
        cityList.add(model14)
        cityList.add(model15)
        cityList.add(model16)
        cityList.add(model17)
        cityList.add(model18)
        cityList.add(model19)
        cityList.add(model20)
        cityList.add(model21)
        cityList.add(model22)
        cityList.add(model23)
        cityList.add(model24)
        cityList.add(model25)
        cityList.add(model26)
        cityList.add(model27)
        cityList.add(model28)
        cityList.add(model29)
        cityList.add(model30)
        cityList.add(model31)
        cityList.add(model32)
        cityList.add(model33)
        cityList.add(model34)
        cityList.add(model35)
        cityList.add(model36)
        cityList.add(model37)
        cityList.add(model38)
        cityList.add(model39)
        cityList.add(model40)
        cityList.add(model41)
        cityList.add(model42)
        cityList.add(model43)
        cityList.add(model44)
        cityList.add(model45)
        cityList.add(model46)
        cityList.add(model47)
        cityList.add(model48)
        cityList.add(model49)
        cityList.add(model50)
        cityList.add(model51)
        cityList.add(model52)
        cityList.add(model53)
        cityList.add(model54)
        cityList.add(model55)
        cityList.add(model56)

        return cityList
    }

}