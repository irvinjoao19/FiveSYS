package com.fivesys.alphamanufacturas.fivesys.helper

import com.fivesys.alphamanufacturas.fivesys.entities.DataList

interface ItemClickListener {
    fun onClick(data: DataList, position: Int)
}