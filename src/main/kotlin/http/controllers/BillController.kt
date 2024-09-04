package http.controllers

import com.toms223.winterboot.annotations.Controller
import com.toms223.winterboot.annotations.mappings.DeleteMapping
import com.toms223.winterboot.annotations.mappings.GetMapping
import com.toms223.winterboot.annotations.mappings.PostMapping
import com.toms223.winterboot.annotations.mappings.PutMapping
import com.toms223.winterboot.annotations.parameters.Body
import com.toms223.winterboot.annotations.parameters.Path
import http.entities.bill.NewBill
import http.entities.bill.ReturningBill
import http.entities.bill.ReturningBill.Companion.toReturningBill
import services.Services
import java.time.Period


@Controller
class BillController(private val services: Services) {
    @GetMapping("/bills/{id}")
    fun getBillById(@Path id: Int): ReturningBill {
        return services.billService.getBillById(id).toReturningBill()
    }

    @DeleteMapping("/bills/{id}")
    fun deleteBillById(@Path id: Int){
        services.billService.deleteBill(id)
    }

    @PutMapping("/bills/{id}/pay")
    fun payBillById(@Path id: Int): ReturningBill {
        return services.billService.payBill(id).toReturningBill()
    }

    @PutMapping("/bills/{id}/unpay")
    fun unpayBillById(@Path id: Int): ReturningBill {
        return services.billService.unpayBill(id).toReturningBill()
    }

    @PutMapping("/bills")
    fun updateBillById(@Body receivingBill: ReturningBill): ReturningBill {
        return services.billService.updateBill(receivingBill.id, receivingBill.name, receivingBill.date, receivingBill.continuous, receivingBill.period).toReturningBill()
    }

    @PostMapping("/bills")
    fun createBill(@Body newBill: NewBill): ReturningBill {
        val account = services.accountService.getAccountById(newBill.accountId)
        return services.billService.createBill(newBill.name, newBill.date, newBill.continuous, newBill.period ,account).toReturningBill()
    }
}