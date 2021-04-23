package com.fish.service;

import com.common.api.response.ApiError;
import com.common.domain.User;
import com.common.exceptions.RestApiException;
import com.common.security.Auth;
import com.fish.api.dto.OrderDTO;
import com.fish.api.dto.OrderDetailsVO;
import com.fish.api.dto.OrderVO;
import com.fish.api.qo.OrderQO;
import com.fish.domain.mysql.Order;
import com.fish.domain.mysql.OrderDetails;
import com.fish.domain.mysql.Product;
import com.fish.repository.OrderDetailsRepository;
import com.fish.repository.OrderRepository;
import com.fish.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private ProductRepository productRepository;

    public void createOrder(OrderVO orderVO){
        Order order = new Order();
        List<OrderDetailsVO> vos = orderVO.getVos();
        // 设置用户基本信息
        User user = Auth.getDomainUser();
        order.setAddress(user.getAddress());
        order.setPhone(user.getPhone());
        order.setUserName(user.getName());
        // 设置订单信息
        order.setDesc(orderVO.getDesc());
        order.setIsTurn(orderVO.getIsTurn());
        List<OrderDetails> lt = vos.stream().map(this::handleData).collect(Collectors.toList());
        // 算出总价
        Integer allPrice = lt.stream().map(OrderDetails::getPrice).reduce(0,Integer::sum);
        order.setPrice(allPrice);
        order.setRealPrice(allPrice);
        order.setStatus(1);
        order =  orderRepository.save(order);
        Long id = order.getId();
        lt.forEach(m -> { m.setOrderId(id);});
        orderDetailsRepository.saveAll(lt);
    }

    public void modifyOrder(OrderVO orderVO){
        if(null == orderVO.getId()){
            throw new RestApiException(ApiError.RESOURCE_NOT_FOUND,orderVO.getId());
        }
        orderDetailsRepository.deleteByOrderById(orderVO.getId());
        Order order = orderRepository.getOne(orderVO.getId());
        List<OrderDetailsVO> vos = orderVO.getVos();
        // 设置订单信息
        order.setDesc(orderVO.getDesc());
        order.setIsTurn(orderVO.getIsTurn());
        List<OrderDetails> lt = vos.stream().map(this::handleData).collect(Collectors.toList());
        // 算出总价
        Integer allPrice = lt.stream().map(OrderDetails::getPrice).reduce(0,Integer::sum);
        order.setPrice(allPrice);
        order.setRealPrice(allPrice);
        order.setStatus(1);
        order =  orderRepository.save(order);
        Long id = order.getId();
        lt.forEach(m -> { m.setOrderId(id);});
        orderDetailsRepository.saveAll(lt);
    }

    private OrderDetails handleData(OrderDetailsVO vo){
        OrderDetails details = new OrderDetails();
        Product p = productRepository.getOne(vo.getProductId());
        details.setIsTurn(vo.getIsTurn());
        details.setNum(vo.getNum());
        details.setPrice(p.getRealPrice());
        details.setProductId(vo.getProductId());
        return details;
    }

    public Page<OrderDTO> getOrder(OrderQO qo, Pageable pageable){
        return orderRepository.getOrder(qo,pageable);
    }





}
