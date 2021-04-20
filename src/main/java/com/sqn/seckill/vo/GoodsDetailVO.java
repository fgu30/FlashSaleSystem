package com.sqn.seckill.vo;

import com.sqn.seckill.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Title: GoodsDetailVO
 * Description:商品详情返回对象
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/20 0020 上午 10:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetailVO {

    private User user;

    private GoodsVO goodsVO;

    private int secKillStatus;

    private Long remainSeconds;

    private Long endSeconds;

}
