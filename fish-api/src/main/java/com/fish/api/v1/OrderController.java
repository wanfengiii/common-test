package com.fish.api.v1;

import com.common.api.response.DataResponse;
import com.fish.api.dto.FullOrderDTO;
import com.fish.api.dto.OrderDTO;
import com.fish.api.dto.OrderVO;
import com.fish.api.dto.ProductDTO;
import com.fish.api.qo.OrderQO;
import com.fish.domain.mysql.Category;
import com.fish.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "订单Api", tags = "订单Api")
@RestController
@RequestMapping("/v1/order")
public class OrderController {
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

    @ApiOperation("新增订单")
    @PostMapping("/add")
    public DataResponse addOrder(@RequestBody OrderVO orderVO){
        orderService.createOrder(orderVO);
        return DataResponse.success() ;
    }

    @ApiOperation("编辑订单")
    @PostMapping("/edit")
    public DataResponse editOrder(@RequestBody @Valid OrderVO orderVO){
        orderService.modifyOrder(orderVO);
        return DataResponse.success() ;
    }

    @ApiOperation("删除订单")
    @DeleteMapping("/{id}")
    public DataResponse deleteOrder(@PathVariable Long id){
        orderService.removeOrder(id);
        return DataResponse.success() ;
    }
}
