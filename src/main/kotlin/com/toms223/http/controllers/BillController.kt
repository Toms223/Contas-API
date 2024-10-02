package com.toms223.http.controllers

import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.DeleteMapping
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.mappings.PutMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Path
import com.toms223.http.entities.bill.NewBill
import com.toms223.http.entities.bill.ReturningBill
import com.toms223.http.entities.bill.ReturningBill.Companion.toReturningBill
import com.toms223.services.Services
import com.toms223.winterboot.annotations.parameters.Cookie


@Controller
class BillController(private val services: Services) {
    @GetMapping("/bills/{billId}")
    fun getBillById(@Cookie id: Int, @Path billId: Int): ReturningBill {
        return services.billService.getBillById(id, billId).toReturningBill()
    }

    @DeleteMapping("/bills/{billId}")
    fun deleteBillById(@Cookie id: Int, @Path billId: Int){
        services.billService.deleteBill(id, billId)
    }

    @PutMapping("/bills/{billId}/pay")
    fun payBillById(@Cookie id: Int, @Path billId: Int): ReturningBill {
        return services.billService.payBill(id, billId).toReturningBill()
    }

    @PutMapping("/bills/{billId}/unpay")
    fun unpayBillById(@Cookie id: Int, @Path billId: Int): ReturningBill {
        return services.billService.unpayBill(id, billId).toReturningBill()
    }

    @PutMapping("/bills")
    fun updateBillById(@Cookie id: Int, @Body receivingBill: ReturningBill): ReturningBill {
        return services.billService.updateBill(id,
            receivingBill.id,
            receivingBill.name,
            receivingBill.date,
            receivingBill.continuous,
            receivingBill.period
        ).toReturningBill()
    }

    @PostMapping("/bills")
    fun createBill(@Cookie id: Int, @Body newBill: NewBill): ReturningBill {
        return services.billService.createBill(id, newBill.name, newBill.date, newBill.continuous, newBill.period).toReturningBill()
    }
}