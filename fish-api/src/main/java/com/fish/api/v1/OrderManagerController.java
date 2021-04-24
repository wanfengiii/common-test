package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.api.dto.FullOrderDTO;
import com.fish.api.dto.OrderDTO;
import com.fish.api.qo.OrderQO;
import com.fish.domain.mysql.Category;
import com.fish.service.CategoryService;
import com.fish.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "订单Api", tags = "订单Api")
@RestController
@RequestMapping("/v1/order/manager")
public class OrderManagerController {
    @Autowired
    private OrderService orderService;

    @ApiOperation("查询订单")
    @GetMapping
    public DataResponse<Page<OrderDTO>> getOrderList(OrderQO qo, Pageable pageable) {
        return DataResponse.of(orderService.getOrder(qo,pageable));
    }

    @ApiOperation("查询单个订单详细信息")
    @GetMapping("/{id}")
    public DataResponse<FullOrderDTO> getOrderById(@PathVariable Long id) {
        return DataResponse.of(orderService.getOrderById(id));
    }


    @ApiOperation("确认订单")
    @GetMapping("/confirm/{id}")
    public DataResponse confirmOrder(@PathVariable Long id){
        orderService.confirmOrder(id);
        return DataResponse.success() ;
    }


    @ApiOperation("完成订单")
    @GetMapping("/complete/{id}")
    public DataResponse completeOrder(@PathVariable Long id){
        orderService.completeOrder(id);
        return DataResponse.success();
    }

    @ApiOperation("取消订单")
    @DeleteMapping("/cancel/{id}")
    public DataResponse deleteOrder(@PathVariable Long id){
        orderService.cancelOrder(id);
        return DataResponse.success() ;
    }
}
