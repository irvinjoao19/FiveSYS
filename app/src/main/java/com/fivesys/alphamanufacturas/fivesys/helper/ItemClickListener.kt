package com.fivesys.alphamanufacturas.fivesys.helper

import com.fivesys.alphamanufacturas.fivesys.entities.Auditoria

interface ItemClickListener {
    fun onItemClick(a: Auditoria,position: Int)
}