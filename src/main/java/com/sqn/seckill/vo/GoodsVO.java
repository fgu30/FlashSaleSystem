package com.sqn.seckill.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.sqn.seckill.entity.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Title: GoodsVO
 * Description: 商品返回对象
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/15 0015 下午 7:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVO extends Goods {

    /**
     * 秒杀价
     */
    @TableField("seckill_price")
    private BigDecimal seckillPrice;

    /**
     * 库存数量
     */
    @TableField("stock_count")
    private Integer stockCount;

    /**
     * 秒杀开始时间
     */
    @TableField("start_date")
    private Date startDate;

    /**
     * 秒杀结束时间
     */
    @TableField("end_date")
    private Date endDate;


}
