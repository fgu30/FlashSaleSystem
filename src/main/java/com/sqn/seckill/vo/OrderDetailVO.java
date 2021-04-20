package com.sqn.seckill.vo;

import com.sqn.seckill.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Title: OrderDetailVO
 * Description:
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/20 0020 下午 4:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {

    private Order order;

    private GoodsVO goodsVO;

}
